package network

import input.*

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import pattern.PatternMiner;
import codebigbrosub.User
import codebigbrosub.Weibo
import crawler.*
import exceptions.*
import groovy.json.JsonOutput
import groovy.util.logging.Log4j

@Log4j
class NetworkGenerator {

	static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
	static String userBase=base+"userFiles\\";

	public PatternMiner patternMiner;
	boolean studied;

	// get one session to use
	public static  checkLoginStatus(){
		def cookies=CrackedCookieRepo.getOneCookie();
		if(cookies!=null){
			println "Got cookies for weibo.cn";
			println cookies;
		}else{
			println "Null cookies!";

		}
		return cookies;
	}
	public userWeiboCrawl(User u,List<Weibo> weibos){
		int retryTimes=3;
		int timeOut=1000;
		String n=u.weiboName;
		//check login before crawling
		def cookies=checkLoginStatus();

		println "${weibos.size()} weibo pieces to start with. ";

		//store support info
		def support=[:];
		support.put("comment",new HashMap<String,Integer>());
		support.put("forward",new HashMap<String,Integer>());
		support.put("like",new HashMap<String,Integer>());

		//store user interaction of each weibo
		Map<Weibo,ArrayList> interaction=new HashMap<>();
		
		weibos.each{it->
			//put the weibo into interaction map
			ArrayList<String> nameList=new ArrayList<>();
			
			String content=it.content;
			//find forward information
			Pattern mention=Patterns.AT.value();
			Matcher mm=mention.matcher(content);
			while(mm.find()){
				String name=mm.group();
				//remove the @ sign in forward tag
				name=name.replace("@","");

				//println "Remove @ in ${name}";
				u.forwarding.add(name);
				nameList.add(name);
			}
			//get comments
			String url=it.url;
			url=url.replace("weibo.com","weibo.cn");
			println "Going to ${url}";
			//fetch the comments for 5 times before abort
			boolean gotComment=false;
			boolean gotForward=false;
			boolean gotLike=false;
			for(int count=0;count<retryTimes&&gotComment==false;count++){
				try{
					Document doc;
					doc = Jsoup.connect(url)
							.data("query", "Java")
							.userAgent("Mozilla")
							.cookies(cookies)
							.timeout(timeOut)
							.post();
					//check if the text contains number
					def hasNumber={text->
						def numbers=text.findAll( /\d+/ );
						println "Got number ${numbers}";
						if(numbers.size()==0)
							return false;
						else if(numbers.get(0)==0)
							return false;
						else
							return true;

					}
					//check the repost and like count
					String forwardSelector="/repost";
					def forwardHolder=doc.select("a[href~=/repost]");
					Element forwardElement=forwardHolder.get(0);
					String forwardText=forwardElement.text();
					println "Got forward span ${forwardText}";
					gotForward=hasNumber(forwardText);

					String likeSelector="/attitude";
					def likeHolder=doc.select("a[href~=/attitude]");
					Element likeElement=likeHolder.get(0);
					String likeText=likeElement.text();
					gotLike=hasNumber(likeText);

					println "Got "+doc.text();
					if(loggedIn(doc)){
						String subquery="C_";
						//String query="div[id^=\"C_\"]";
						Elements comments=doc.select("div[id^=${subquery}]");
						if(comments.size()==0){println "No comments from this one";}
						comments.each{
							String raw=it.text();
							int colon=raw.indexOf(":");
							String name=raw.substring(0,colon).trim();
							String body=raw.substring(colon+1).trim();
							println "Found comment from user ${name}";
							//add to comment list
							u.commented.add(name);
							nameList.add(name);
							if(!support["comment"].containsKey(name)){
								support["comment"].put(name,1);
							}else{
								support["comment"][name]=support["comment"][name]+1;
							}
						}
						gotComment=true;
					}
					else{
						println "Not in logged in status";
						//doc=logIn(url);
					}


				}catch(SocketTimeoutException e){
					println "Fetch comment time-out for the ${count}th try";
					continue;
				}
				catch(IndexOutOfBoundsException e){
					println "Login failed or field extraction failed";
					continue;
				}catch(Exception e){
					e.printStackTrace();
				}
				//sleep 1 second
				//waitASecond();
				sleep(1000);
			}
			if(gotComment==false){
				println "Failed to get comments at last!";
			}


			String userId=u.weiboId;
			String forwardUrl=url.replace("comment","repost");
			println("Made repost url "+forwardUrl);
			for(int count=0;count<retryTimes&&gotForward==false;count++){
				try{
					Document doc;
					doc = Jsoup.connect(forwardUrl)
							.data("query", "Java")
							.userAgent("Mozilla")
							.cookies(cookies)
							.timeout(timeOut)
							.post();

					println "Got "+doc.text();
					if(loggedIn(doc)){
						String subquery1="c";
						//String subquery2="C_";
						//String query="div[id^=\"C_\"]";
						Elements forwards=doc.select("div[class=${subquery1}]");
						//iterate in reverse order
						int siz=forwards.size();
						if(siz==0){
							println "No forward found";
						}
						else{
							boolean forwardStart;
							boolean forwardEnd;
							for(int i=siz-1;i>0&&forwardEnd==false;i--){
								Element f=forwards.get(i);
								//log.debug "Content of this forward element is "+f;
								String t=f.text();
								println "Text of this element is"+ t;
								int index=t.indexOf(":");
								int neg=t.indexOf("@");
								if(index==-1||neg<index){
									if(forwardStart){
										println "Now reached the end of forwardings.";
										forwardEnd=true;
									}else{

										println "No forward in this one";
									}

								}
								else{

									if(!forwardStart){
										println "Now start the forward messages.";
										forwardStart=true;
									}

									log.debug "Located : in text "+t;
									String fName=t.substring(0,index).trim().replaceAll("\\?","");
									if(fName.contains("\\u8f6c\\u53d1\\u4e86")||fName.contains(" ")){
										
										println "Text ${fName} is not a user name. Dispose.";
									}
									else{
										println "User ${fName} forwarded this user's weibo.";
										if(fName==n){
											println "This user forwarded his own weibo.";
										}else{
											//record
											u.forwarded.add(fName);
											nameList.add(fName);
											if(!support["forward"].containsKey(fName)){
												support["forward"].put(fName,1);
											}else{
												int fNow=support["forward"][fName];
												support["forward"][fName]=fNow+1;
											}
										}
									}
									//find whose weibo this user has forwarded
									Pattern fwp=Pattern.compile("//@\\S+?:");
									Matcher fwm=fwp.matcher(t);
									while(fwm.find()){
										String fwt=fwm.group();
										if(fwt.length()<=3){}
										else{
											String fwn=fwt.substring(3,fwt.length()-1);
											if(fwn!=n){
												log.info "User has forwarded ${fwn}'s weibo.";
												u.forwarding.add(fwn);
												nameList.add(fwn);
											}
										}
									}
								}

							}

						}
						gotForward=true;
					}
					else{
						println("Need to log in");
						doc=logIn(forwardUrl);
					}



				}catch(SocketTimeoutException e){
					println "Fetch reposts time-out for the ${count}th try";
					continue;
				}catch(IndexOutOfBoundsException e){
					println "Login failed or field extraction failed";
					continue;
				}catch(Exception e){
					e.printStackTrace();
				}
				//sleep 1 second
				//waitASecond();
			}
			if(gotForward==false){
				println "Failed to get forwards at last!";
			}

			//find likes on this weibo, count as comments from others
			String likeUrl=url.replace("comment","attitude");
			println("Made attitude url "+likeUrl);
			for(int count=0;count<retryTimes&&gotLike==false;count++){

				try{
					Document doc;
					doc = Jsoup.connect(likeUrl)
							.data("query", "Java")
							.userAgent("Mozilla")
							.cookies(cookies)
							.timeout(timeOut)
							.post();

					if(loggedIn(doc)){
						String c="c";
						String M="M";
						//String query="div[id^=\"C_\"]";
						Elements likes=doc.select("div[class=${c}]:not([id^=${M}]");
						likes.each{attitude->
							String t=attitude.text();
							if(t.contains("\u524d")){
								int space=t.indexOf(" ");
								if(space==-1){
									println("Error in processing likes in "+t);
								}else{
									String name=t.substring(0,space);
									println("Got liker "+name);
									//record
									u.commented.add(name);
									if(!support["like"].containsKey(name)){
										support["like"].put(name,1);
									}else{
										support["like"][name]=support["like"][name]+1;
									}
									nameList.add(name);
								}
							}

						}

						gotLike=true;
					}
					else{
						println "Not logged in. Cannot find likes.";
					}
				}catch(SocketTimeoutException e){
					println "Fetch likes time-out for the ${count}th try";
					continue;
				}catch(IndexOutOfBoundsException e){
					println "Login failed or field extraction failed";
					continue;
				}catch(Exception e){
					e.printStackTrace();
				}
				//sleep 1 second
				//waitASecond();
				sleep(1000);
			}
			if(gotLike==false){
				println "Failed to get likes at last!";
			}
			
			//store the namelist of interaction
			interaction.put(it,nameList);
			if(patternMiner==null){
				patternMiner=new PatternMiner();
			}
			patternMiner.storeInteractions(interaction);
			log.info "Interactions passed to the pattern miner.";
		}
		User.withTransaction{
			u.merge(flush:true);
			if(u.hasErrors())
				println u.errors;
			else{
				println "User network study complete";
				println "Now user has forwarding:${u.forwarding}, commented: ${u.commented}, forwarded:${u.forwarded}, commented:${u.commenting}"
			}
		}
		//report support
		log.info "Support from users is:"+support;
		return support;
	}
	public static userWeiboCrawlHK(User u,List<Weibo> weibos){
		int retryTimes=3;
		int timeOut=1000;
		println "${weibos.size()} weibo pieces to start with. ";

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
					println "Fetch likes time-out for the ${count}th try";
					continue;
				}catch(IndexOutOfBoundsException e){
					println "Login failed or field extraction failed";
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
	public formNetwork(User u){
		if(u==null){
			throw new UserNotFoundException();
		}
		def weibos;
		String n=u.weiboName;
		//for each weibo get info like comments, likes, forwards
		Weibo.withTransaction{weibos=Weibo.findAll("from Weibo as w where w.ownerName=:name",[name:n]);}

		//pass to study
		def support=userWeiboCrawl(u,weibos);

		//use hk site to crawl
		//userWeiboCrawlHK(u,weibos);

		//return similar
		//sort before return
		int total=weibos.size();
		def comment=support["comment"];
		comment=comment.sort { a, b -> b.value <=> a.value };
		comment=filterSupport(comment,total);
		def forward=support["forward"];
		forward=forward.sort { a, b -> b.value <=> a.value };
		forward=filterSupport(forward,total);
		def like=support["like"];
		like=like.sort { a, b -> b.value <=> a.value };
		like=filterSupport(like,total);

		support["forward"]=forward;
		support["comment"]=comment;
		support["like"]=like;
		support["total"]=weibos.size();

		log.debug "Final support after sorting is:"+support;

		return support;

	}
	public studyPatterns(){
		if(patternMiner==null){
			log.error "Pattern miner is not initialized.";
			return null;
		}
		def result=patternMiner.studyPatterns();
		return result;
	}
	public static void waitASecond(){
		sleep(500);
	}
	public static boolean loggedIn(Document doc){
		String title=doc.title();
		println "Title is ${title}";
		if(title=="\u5fae\u535a"||title=='\u65b0\u6d6a\u901a\u884c\u8bc1'){
			println "Document deemed not logged in";
			println doc.text();
			return false;
		}else{

			return true;
		}
	}
	public static Document logIn(String url){
		def c=UserWeiboCrawler.login("18717731224","KOFkof94");
		def cookies=c;
		Document doc=Jsoup.connect(url)
				.data("query", "Java")
				.userAgent("Mozilla")
				.cookies(c)
				.timeout(1500)
				.post();
		return doc;
	}
	public findSimilar(User u){
		HashSet<String> related=getFriends(u);
		HashMap<String,Network> similar=new HashMap<>();
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
		println("Generating full network");
		Network fullNetwork=generate(u,related);
		println("Network completed: "+fullNetwork);
		//reduce the scale of the network
		if(fullNetwork.isLarge()){
			println "network is large";
			//fullNetwork=reduceNetwork(fullNetwork);
		}
		//improve network by marking top pagerank ones
		fullNetwork=improveNetwork(fullNetwork);
		String json=JsonOutput.toJson(fullNetwork);
		println(json);

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
			println "Clique on tag ${t} is "+smallNetwork;
			Network clique=generate(u,smallNetwork);
			if(clique.isLarge()){
				println "Clique is large";
				//clique=reduceNetwork(clique);
			}
			clique=improveNetwork(clique);
			println("Network completed on tag ${t}: "+clique);
			String cliqueJson=JsonOutput.toJson(clique);
			println(cliqueJson);

			similar.put(t,clique);

		}

		println similar;

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
		println "Constructed index map:"+indexMap;

		Network fullNetwork=new Network();
		fullNetwork.addNode(new Node(name:u.weiboName,group:1));
		related.each{it->
			//log.debug "This fucker is "+it;
			if(it!=u.weiboName){
				fullNetwork.addNode(new Node(name:it,group:1));
				User v=User.find("from User as v where v.weiboName=?",[it]);
				if(v!=null){
					println v.weiboName+" is existing user.";
					//recursively crawl out for user v
					formNetwork(v);
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
					println v+" not found, cannot stretch further out.";
					fullNetwork.addLink(new Link(source:it,target:u.weiboName,value:1.0));
				}
			}
		}


		return fullNetwork;
	}
	public getFriends(User u){
		HashSet<String> friends=new HashSet<>();
		friends.addAll(u.forwarded);
		friends.addAll(u.forwarding);
		friends.addAll(u.commenting);
		friends.addAll(u.commented);

		println("friends of ${u.weiboName} are "+friends);
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
	public static class Node implements Comparable<Node>{
		String name;
		int group;

		@Override
		public String toString(){
			return name;
		}
		@Override
		public int hashCode(){
			return name.hashCode();
		}

		@Override
		public int compareTo(Node other){

			return name.compareTo(other.name);
		}
	}
	public static class Link{
		String source;
		String target;
		float value;

		@Override
		public String toString(){
			return "From ${source} to ${target}";
		}
		@Override
		public int hashCode(){
			return source.hashCode()+target.hashCode();
		}
	}
	public static class Network{
		TreeSet<Node> nodes;
		HashSet<Link> links;
		public Network(){
			nodes=new HashSet<>();
			links=new HashSet<>();
		}
		public void addNode(Node n){
			nodes.add(n);
		}

		public void addLink(Link l){
			links.add(l);

		}
		public boolean isLarge(){
			return nodes.size()>100;
		}
	}
	public static networkPagerank(Network network){
		//convert data format
		Set<Node> nodes=new HashSet<>();
		Set<Map> links=new HashSet<>();
		network.nodes.each{
			nodes.add(new Node(name:it.name,group:it.group));
		}
		network.links.each{
			links.add(['source':it.source,'target':it.target,'weight':1]);
		}
		println "${nodes.size()} nodes and ${links.size()} links passed to pagerank study";
		//calculate pagerank
		def pagerank=PageRankCalc.calculatePageRank(nodes,links,false);
		println "Pagerank of network calculated:"+pagerank;
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
		println "At most ${remarkable} nodes to mark as important.";
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

		println "Reduced network "+newNetwork;
		return newNetwork;
	}
}
