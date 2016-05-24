package network

import exceptions.*
import groovy.util.logging.Log4j
import input.*
import network.Network.Link
import network.Network.Node

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import pattern.PatternMiner
import codebigbrosub.Job
import codebigbrosub.User
import codebigbrosub.Weibo
import crawler.*
import static toolkit.JobLogger.jobLog

@Log4j
class NetworkGenerator {

	/* These two paths are not really used. */
	static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
	static String userBase=base+"userFiles\\";

	public PatternMiner patternMiner;
	boolean studied;

	public Job job;//save a reference of job for the sake of logging
	// get one session to use
	public static  checkLoginStatus(){
		def cookies=CrackedCookieRepo.getOneCookie();
		if(cookies!=null){
			log.info "Got cookies for weibo.cn";
			log.info cookies;
		}else{
			log.error "Null cookies!";

		}
		return cookies;
	}
	public NetworkGenerator(Job job){
		this.job=job;
		log.debug "Set job in NetworkGenerator.";
	}
	public userWeiboCrawl(User u,List<Weibo> weibos,WeiboCrawlerMaster crawlerMaster){
		log.info "Start crawling user's weibo items";
		
		jobLog(job.id,"Start crawling all the microblogs of user.");
		int retryTimes=3;
		int timeOut=1000;
		String n=u.weiboName;
		log.info "${weibos.size()} weibo pieces to start with. ";

		def support=crawlerMaster.crawlRCL(u,weibos);
		//store the contents from support into user fields
		def interaction=support["interactions"];
		
		if(patternMiner==null){
			patternMiner=new PatternMiner();
		}
		patternMiner.storeInteractions(interaction);
		log.info "Interactions passed to the pattern miner.";
		
		User.withTransaction{
			u.merge(flush:true);
			if(u.hasErrors()){
				log.error "Error saving user interactions for user.";
				log.error u.errors;
			}else{
				log.info "User network study complete";
				log.info "Now user has forwarding:${u.forwarding}, commented: ${u.commented}, forwarded:${u.forwarded}, commented:${u.commenting}"
			}
		}
		//report support
		//log.info "Support from users is:"+support;
		return support;
	}
	public static userWeiboCrawlHK(User u,List<Weibo> weibos){
		int retryTimes=3;
		int timeOut=1000;
		log.info "${weibos.size()} weibo pieces to start with. ";

		weibos.each{
			String url=it.url;
			//check if the url is from chinese site
			boolean needTrans;
			if(url.contains('//weibo.cn')||url.contains('//weibo.com')||url.contains('www.weibo.com')){
				log.debug "${url} needs translation";
				needTrans=true;
			}
			if(needTrans){
				String weiboId=it.weiboId;
				if(weiboId.length()!=9){
					log.debug "Weibo has id in an unrecognized format:"+weiboId;
				}
				url=Base62Converter.convertToHKId(weiboId);

			}
			boolean got=false;
			//connect to url
			for(int count=0;count<retryTimes&&got==false;count++){

				try{
					Document doc;
					doc = Jsoup.connect(url)
							.data("query", "Java")
							.userAgent("Mozilla")
							.timeout(timeOut)
							.post();


				}catch(SocketTimeoutException e){
					log.error "Fetch likes time-out for the ${count}th try";
					continue;
				}catch(IndexOutOfBoundsException e){
					log.error "Login failed or field extraction failed";
					continue;
				}catch(Exception e){
					e.printStackTrace();
				}
				//sleep 1 second
				//waitASecond();
				//sleep(1000);
			}


			//use hk crawler to get the document

		}
	}
	public Map<String,Integer> filterSupport(Map<String,Integer> support,int total){
		int counter=0;
		//keep top 10 if more than 10
		LinkedHashMap<String,Integer> betterMap=new LinkedHashMap<>();
		for(def e:support){
			betterMap.put(e.key,e.value);
			counter++;
			if(counter>=10){break;}
		}
		return betterMap;
	}
	public formNetwork(User u,WeiboCrawlerMaster crawlerMaster){
		if(u==null){
			throw new UserNotFoundException();
		}
		def weibos;
		String n=u.weiboName;
		//for each weibo get info like comments, likes, forwards
		Weibo.withTransaction{weibos=Weibo.findAll("from Weibo as w where w.ownerName=:name",[name:n]);}
		if(weibos==null||weibos.size()==0){
			log.error "The user has no weibo. No network can be formed.";
			jobLog(job.id,"No weibo known for the network study. You should activate the crawler first.");
		}

		//pass to study
		def support=userWeiboCrawl(u,weibos,crawlerMaster);

		//use hk site to crawl
		//userWeiboCrawlHK(u,weibos);

		//return similar
		
		//sort before return
		jobLog(job.id,"Reordering the supports to make it more logical.");
		def reorder={map,total->
			def newMap=map.sort { a, b -> b.value <=> a.value };
			newMap=filterSupport(newMap,total);
			return newMap;
		}
		int total=weibos.size();
		def newSupport=[:];
		newSupport.put("forwarded",reorder(support["forwarded"],total));
		newSupport.put("forwarding",reorder(support["forwarding"],total));
		newSupport.put("commented",reorder(support["commented"],total));
		newSupport.put("commenting",reorder(support["commenting"],total));
		newSupport.put("liked",reorder(support["liked"],total));
		newSupport.put("liking",reorder(support["liking"],total));
		newSupport.put("mentioning",reorder(support["mentioning"],total));
		newSupport.put("mentioned",reorder(support["mentioned"],total));
		newSupport.put("total",weibos.size());
		log.debug "Final support after sorting is:"+newSupport;

		return newSupport;
	}
	public studyPatterns(String userName){
		if(patternMiner==null){
			log.error "Pattern miner is not initialized.";
			return null;
		}
		def result=patternMiner.studyPatterns(userName);
		jobLog(job.id,"Pattern mining is complete. The found patterns are: ");
		jobLog(job.id,result.toString());
		return result;
	}
	public static void waitASecond(){
		sleep(500);
	}

	public findSimilar(User u){
		HashMap<String,Network> similar=new HashMap<>();
		if(u==null){
			log.error "Passed an empty user to network generator!";
			//generate new empty network
			Network empty=new Network();
			similar.put("all",empty);
			return similar;
		}
		HashSet<String> related=getFriends(u);
		//check if the network is empty
		int siz;
		Weibo.withTransaction{
			def w=Weibo.findAll("from Weibo as w where w.ownerName=?",[u.weiboName]);
			siz=w.size();
			
		}
		if(siz==0){
			log.info "The user has no weibo at all. There should be no network formed.";
			//generate new empty network
			Network empty=new Network();
			similar.put("all",empty);
			return similar;
		}
		log.info("Generating full network");
		Network fullNetwork=generate(u,related);
		log.info("Network completed: "+fullNetwork);
		//reduce the scale of the network
		if(fullNetwork.isLarge()){
			log.info "network is large";
			//fullNetwork=reduceNetwork(fullNetwork);
		}
		//improve network by marking top pagerank ones
		fullNetwork=improveNetwork(fullNetwork);
		similar.put("all",fullNetwork);


		//for each tag find from related users who have this tag
		u.tags.each{t->
			//generate the network
			HashSet<String> smallNetwork=new HashSet<>();
			related.each{userName->
				User v=User.find("from User as v where v.weiboName=?",[userName]);
				if(v!=null){
					if(v.tags.contains(t)){
						smallNetwork.add(userName);

					}
				}
			}
			log.info "Clique on tag ${t} is "+smallNetwork;
			Network clique=generate(u,smallNetwork);
			if(clique.isLarge()){
				log.info "Clique is large";
				//clique=reduceNetwork(clique);
			}
			clique=improveNetwork(clique);
			log.info("Network completed on tag ${t}: "+clique);
			similar.put(t,clique);
		}

		log.info similar;

		return similar;
	}
	public Network generate(User u,HashSet<String> related){

		//first generate the full map
		TreeMap<String,Integer> indexMap=new TreeMap<>();
		indexMap.put(u.weiboName,0);
		int count=1;

		related.each{
			//log.debug "Can this be the root of corruption?"+it;
			indexMap.put(it,count);
			count++;
		}
		log.info "Constructed index map:"+indexMap;

		Network fullNetwork=new Network();
		fullNetwork.addNode(new Node(name:u.weiboName,group:1));
		related.each{it->
			//log.debug "This fucker is "+it;
			if(it!=u.weiboName){
				fullNetwork.addNode(new Node(name:it,group:1));
				User v=User.find("from User as v where v.weiboName=?",[it]);
				if(v!=null){
					log.debug v.weiboName+" is existing user.";
					//recursively crawl out for user v, disabled in v1.2
					//formNetwork(v);
					HashSet<String> friends=getFriends(v);
					friends.each{f->
						//if(related.contains(f)||f==u.weiboName){
						if(related.contains(f)){
							//should be added into edges
							fullNetwork.addLink(new Link(source:f,target:it,value:1.0));
						}
					}
				}
				else{
					log.debug v+" not found, cannot stretch further out.";
					fullNetwork.addLink(new Link(source:it,target:u.weiboName,value:1.0));
				}
			}
		}


		return fullNetwork;
	}
	public getFriends(User u){
		HashSet<String> friends=new HashSet<>();
		friends.addAll(u.forwarded?.keySet());
		friends.addAll(u.forwarding?.keySet());
		friends.addAll(u.commenting?.keySet());
		friends.addAll(u.commented?.keySet());
		friends.addAll(u.liked?.keySet());
		friends.addAll(u.liking?.keySet());
		friends.addAll(u.mentioned?.keySet());
		friends.addAll(u.mentioning?.keySet());
		log.info("friends of ${u.weiboName} are "+friends);
		return friends;
	}
	public getFriendUsers(User u){
		HashSet<String> names=getFriends(u);
		ArrayList<User> users=new ArrayList<>();
		//add the user himself first
		users.add(u);
		//add his friends
		names.each{
			User v=User.find("from User as v where v.weiboName=?",[it]);
			users.add(v);
		}
		return users;
	}
	//handles undirected pagerank
	public static Map networkPagerank(Network network){
		//convert data format
		Set<Node> nodes=new HashSet<>();
		Set<Map> links=new HashSet<>();
		network.nodes.each{
			nodes.add(new Node(name:it.name,group:it.group));
		}
		network.links.each{
			links.add(['source':it.source,'target':it.target,'weight':1]);
		}
		log.info "${nodes.size()} nodes and ${links.size()} links passed to pagerank study";
		//calculate pagerank
		def pagerank=PageRankCalc.calculatePageRank(nodes,links,false);
		log.info "Pagerank of network calculated:"+pagerank;
		return pagerank;
	}
	public static Network cleanUp(Network network){
		//record all nodes that have links
		HashSet<String> known=new HashSet<>();
		HashSet<Link> toDelete=new HashSet<>();
		network.links.each{link->
			//check if source is same with target, if so, remove the link
			if(link.source==link.target){
				log.debug "Source ${link.source} is equal to target for link.";
				toDelete.add(link);
			}else{
				known.add(link.source);
				known.add(link.target);
			}
		}
		HashSet<Node> newNodes=new HashSet<>();
		known.each{
			newNodes.add(new Node(name:it,group:1));
		}
		network.nodes=newNodes;
		toDelete.each{
			network.links.remove(it);
		}
		return network;
	}
	public static Network improveNetwork(Network network){
		//first clean up those nodes that have no links
		network=cleanUp(network);

		def pagerank=networkPagerank(network);
		pagerank=pagerank.sort { a, b -> b.value <=> a.value }
		log.debug "Sorted on pagerank: "+pagerank;
		//mark top 10% as important nodes
		int remarkable=network.nodes.size()/10;
		log.info "At most ${remarkable} nodes to mark as important.";
		//first traverse to set the threshold
		TreeMap<Double,Integer> valueMap=new TreeMap<>(Collections.reverseOrder());
		for(def e:pagerank){
			if(valueMap.containsKey(e.value)){
				valueMap[e.value]=valueMap[e.value]+1;
			}
			else{
				valueMap.put(e.value,1);
			}
		}
		log.debug "The distribution of pagerank is "+valueMap;
		//decide threshold
		double thres=100.0;
		int sizeNow=0;
		for(def e:valueMap){
			if(e.value+sizeNow>=remarkable){
				break;
			}

			sizeNow+=e.value;
			thres=e.key;
		}
		//construct the set of distincted names
		HashSet<String> chosen=new HashSet<>();
		for(def e:pagerank){
			if(e.value>=thres){
				log.debug "${e.key} chosen.";
				chosen.add(e.key);
			}
		}
		//mark top nodes
		int count;
		for(Node n:network.nodes){
			String name=n.name;
			//see if the name is chosen
			boolean wanted;
			for(String c:chosen){
				if(n.name==c)
					n.group=2;
			}


		}
		return network;
	}
	public static Network reduceNetwork(Network network){
		def pagerank=networkPagerank(network);
		//find cutoff 200 nodes
		pagerank=pagerank.sort { a, b -> b.value <=> a.value }
		int cutoff=200;
		int notable=cutoff/10;
		int count=0;
		Set<String> newNodes=new HashSet<>();
		//reform network
		Network newNetwork=new Network();
		for(def e: pagerank){
			count++;
			if(count>=201)
				break;
			newNodes.add(e.key);
			if(count<notable){
				newNetwork.addNode(new Node(name:e.key,group:2));
			}else{
				newNetwork.addNode(new Node(name:e.key,group:1));
			}
		}


		network.links.each{l->
			if(newNodes.contains(l.source)&&newNodes.contains(l.target))
				newNetwork.addLink(l);
		}

		log.info "Reduced network "+newNetwork;
		return newNetwork;
	}
}
