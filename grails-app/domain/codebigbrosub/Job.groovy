package codebigbrosub

import static toolkit.JobLogger.jobLog
import bsh.This;
import grails.converters.JSON
import groovy.util.logging.Log4j
import keyword.KeywordsExtractor
import network.ComplexNetworkGenerator
import network.Network
import network.NetworkGenerator
import segmentation.SepManager
import timeline.TimelineCell
import timeline.TimelineManager
import toolkit.JobLogger
import topic.LDAManager
import clustering.ClusterManager
import clustering.MalletManager
import crawler.WeiboCrawlerMaster

@Log4j
class Job {
	String userName;


	boolean userCrawlComplete;
	boolean keywordsComplete;
	boolean statComplete;
	//boolean relationComplete;
	//boolean networkComplete;
	boolean interactionComplete;
	boolean clusterComplete;
	boolean timelineComplete;
	boolean deepCrawlComplete;

	boolean userCrawlStarted;
	boolean keywordsStarted;
	boolean statStarted;
	//boolean relationStarted;
	//boolean networkStarted;
	boolean interactionStarted;
	boolean clusterStarted;
	boolean timelineStarted;
	boolean deepCrawlStarted;

	boolean nothingToShow;


	MalletManager malletManager;
	ClusterManager clusterManager;
	NetworkGenerator networkGenerator;
	ComplexNetworkGenerator complexNetwork;
	WeiboCrawlerMaster crawlerMaster;
	//TFIDFManager tfidfManager;

	def crawlReg;//temp storage for 1st level crawling, fundamental knowledge

	//the fields are not to be stored
	static transients = ["malletManager", "clusterManager", "complexNetwork", "crawlerMaster", "crawlReg","networkGenerator"]
	static mapping={ 
		id generator:"identity" 
	}
	static constraints = {
		//userName(blank:true,nullable:true);
	}

	public Job(String name){
		userName=name;
		init();
		//println "Job is initiated "+this.id;
	}
	public init(){
		malletManager=new MalletManager(this);
		clusterManager=new ClusterManager(this);
		networkGenerator=new NetworkGenerator(this);
		crawlerMaster=new WeiboCrawlerMaster();
		crawlerMaster.setJob(this);
		complexNetwork=new ComplexNetworkGenerator(this);
	}
	public void setUserName(String name){
		userName=name;
		log.info "Set the user name in job as ${userName}";
		
		jobLog(this.id,"Set the user name for job as ${userName}");
	}
	public report(){
		def status=["crawlStarted":userCrawlStarted,"crawlComplete":userCrawlComplete,
			"keywordsStarted":keywordsStarted, "keywordsComplete":keywordsComplete,
			"statStarted":statStarted,"statComplete":statComplete,
			//"relationStarted":relationStarted,"relationComplete":relationComplete,
			//"networkStarted":networkStarted,"networkComplete":networkComplete,
			"interactionStarted":interactionStarted,"interactionComplete":interactionComplete,
			"clusterStarted":clusterStarted,"clusterComplete":clusterComplete,
			"timelineStarted":timelineStarted,"timelineComplete":timelineComplete,
			"deepCrawlStarted":deepCrawlStarted,"deepCrawlComplete":deepCrawlComplete,
			"nothingToShow":nothingToShow
		];
		log.info status;
		return status;
	}
	public newUserCrawlSlave(){
		jobLog(this.id,"Before creating a new user named ${userName}, check the database for an existing one.");
		//crawlerStarted=true;
		//retrieve user
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.error "Cannot find user from job.";
			jobLog(this.id,"Cannot find user from job.");
			return;
		}
		//crawl the user
		def data=crawlSlave();
		jobLog(this.id,"Finished basic data collection on the user: "+data.toString());
		//set all jobs to complete
		preventAllJob();

		//nothing to show since there is nothing available for studying
		nothingToShow=true;
		jobLog(this.id,"There is no data known about this user. Please click the deep crawl button to activate data collection on the user.");
		jobLog(this.id,"The crawler will go to the user's weibo page in real-time and save all the microblogs of user in database. So that the next time you study the same user the data will be available there.");
		
		//some further check on user id overlap etc

		log.info "User preparation complete in job.";
		jobLog(this.id,"User preparation finished. Waiting for further instructions.");
		//crawlerComplete=true;
		
		return data;
	}
	public preventAllJob(){
		jobLog(this.id,"Since the user is new, there is no information for any kinds of specific study.");
		log.info "Mark all jobs as completed, preventing them from getting triggered.";
		keywordsStarted=true;
		keywordsComplete=true;
		statStarted=true;
		statComplete=true;
//		relationStarted=true;
//		relationComplete=true;
//		networkStarted=true;
//		networkComplete=true;
		interactionStarted=true;
		interactionComplete=true;
		clusterStarted=true;
		clusterComplete=true;
		timelineStarted=true;
		timelineComplete=true;
	}
	public cleanBeforeRestart(){
		jobLog(this.id,"The current result of all studies will be cleared. A new round of studies will be performed based on collected data.");
		log.info "Reset the status of all jobs preparing for starting again.";
		keywordsStarted=false;
		keywordsComplete=false;
		statStarted=false;
		statComplete=false;
//		relationStarted=false;
//		relationComplete=false;
//		networkStarted=false;
//		networkComplete=false;
		interactionStarted=false;
		interactionComplete=false;
		clusterStarted=false;
		clusterComplete=false;
		timelineStarted=false;
		timelineComplete=false;
		nothingToShow=false;
		jobLog(this.id,"Ready for a new round of user studies.");
	}
	public crawlSlave(){
		jobLog(this.id,"The first round of crawling will be performed based on the target's user page.");
		userCrawlStarted=true;
		def result;
		User.withSession{
			User u=User.find("from User as u where u.weiboName=?", [userName]);
			if(u==null){
				log.error "Cannot find user from job.";
				jobLog(this.id,"Cannot find the user from job.");
				return;
			}

			//first level crawl
			result=crawlerMaster.scopeStudy(u);

			log.info "User first level crawling complete.";
			userCrawlComplete=true;
			crawlReg=result;//store the result of first level crawling in a register
			log.info "Store the data at hand for next level crawler:"+crawlReg;
			
			//decide whether it's necessary to go on
			int known=result["knownCount"];
			if(known==null||known==0){
				log.info "No wweibo known by the system for further study.";
				nothingToShow=true;
			}
		}
		jobLog(this.id,"The first round crawling collected based user information: "+result.toString());
		
		return result;
	}
	public keywordSlave(def session){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			jobLog(this.id,"Cannot find the user from job.");
			return;
		}
		log.info "User passed to keyword study";
		if(u==null||u.weiboId==""){
			log.info "User is null. Keyword study cannot proceed.";
		}
		//set the status of job as started
		keywordsStarted=true;
		jobLog(this.id,"User keyword study has started.");
		KeywordsExtractor.keywordStudy(u,this);

		Map<String,Double> map=new HashMap<>();
		def keywords=u?.keywords;
		log.info keywords;
		jobLog(this.id,"The keywords are: "+keywords.toString());
		
		u.keywords?.each{
			map.put(it.key,it.value);
		}
		//sort by value desc
		map=map.sort{ a, b -> b.value <=> a.value }
		log.info "Keyword map:"+map;

		keywordsComplete=true;
		jobLog(this.id,"User's keyword study is complete.");
		return map;
	}
	public statSlave(){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			jobLog(this.id,"Cannot find the user from job.");
			return;
		}
		log.info "User passed to stats study";
		statStarted=true;
		jobLog(this.id,"Start statistics study on user's friends.");
		
		//first gather the stats of user
		//not now
		
		//gather the stats from friends of user
		//where to gather the friend list
		def weibos;
		weibos=Weibo.findAll("from Weibo as w where w.ownerName=?",[userName]);
		def stats=crawlerMaster.studyStats(u,weibos,this);
		
		log.info "Stats got already: "+stats;
		jobLog(this.id,"Here are the friends' statistic: "+stats.toString());
		statComplete=true;
		jobLog(this.id,"Friend statistics study is complete.");
		return stats;
	}

//	public relationSlave(){
//		User u=User.find("from User as u where u.weiboName=?", [userName]);
//		if(u==null){
//			log.info "Cannot find user from job.";
//			return;
//		}
//		log.info "User passed to relation study";
//		//set the status of job as started
//		relationStarted=true;
//		//study all weibo, crawl them each to form relationship
//		def support=networkGenerator.formNetwork(u,crawlerMaster);
//
//		//study patterns
//		def patterns=networkGenerator.studyPatterns();
//
//		//generate result for display
//		def relation=["forwarding":u.forwarding,"forwarded":u.forwarded,"commenting":u.commenting,"commented":u.commented,"support":support,"patterns":patterns];
//		log.info "User's interaction relations are: "+relation;
//		relationComplete=true;
//		return relation;
//	}
//	public networkSlave(){
//		User u=User.find("from User as u where u.weiboName=?", [userName]);
//		if(u==null){
//			log.info "Cannot find user from job.";
//			return;
//		}
//		log.info "User passed to network study";
//		networkStarted=true;
//
//		//form network around user
//		//def similar=networkGenerator.findSimilar(u);
//
//		//construct simple network
//		def simple=complexNetwork.generateDeepSimpleNetwork(u);
//		//construct full network
//		def complex=complexNetwork.generateDeepFullNetwork(u);
//
//		log.info "User network study complete.";
//		def networks=[:];
//		networks.put("simple",simple);
//		networks.put("complex",complex);
//		networkComplete=true;
//		log.info "Network study result is "+networks;
//
//		return networks;
//	}
	public interactionSlave(){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			jobLog(this.id,"Cannot find the user from job.");
			return;
		}
		log.info "User passed to interaction study";
		interactionStarted=true;
		jobLog(this.id,"User's interaction study has started.");
		//study all weibo, crawl them each to form relationship
		def support=networkGenerator.formNetwork(u,crawlerMaster);
		jobLog(this.id,"Collected user's interaction counts: "+support.toString());
		//study patterns
		def patterns=networkGenerator.studyPatterns();
		jobLog(this.id,"Finished pattern searching.");
		
		//generate result for display
		def interaction=[:];
		interaction.put("support",support);
		interaction.put("pattern",patterns);
		log.info "User's interaction relations are: "+interaction;

		def networks=[:];
		//construct simple network
		def simpleMap=complexNetwork.generateDeepSimpleNetwork(u);
		Network simple=simpleMap["all"];
		jobLog(this.id,"Constructed the simple network around the target user.");
		jobLog(this.id,"The weight of all edges are equal in this network, with no regard of how often the two users interact.");
		if(simple!=null){
			String simpleJson=simple.toJson();
			networks.put("simple",simpleJson);
			jobLog(this.id,simpleJson);
		}
		
		//construct full network
		def complexMap=complexNetwork.generateDeepFullNetwork(u);
		Network complex=complexMap["all"];
		jobLog(this.id,"Constructed full network around the target user.");
		jobLog(this.id,"The weight of edge is the times of interaction between the two parties.");
		if(complex!=null){
			String complexJson=complex.toJson();
			networks.put("complex",complexJson);
			jobLog(this.id,complexJson);
		}
		log.info "User network study complete.";
		
		
		log.info "Network study result is "+networks;
		//generate overall wrapper for front end reception
		def result=[:];
		result.put("interaction",interaction);
		result.put("network",networks);

		log.info "Interaction study result is: "+result;
		interactionComplete=true;
		jobLog(this.id,"User interaction study is complete.");
		return result;
	}
	public clusterSlave(){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			return;
		}
		log.info "User passed to cluster study";
		jobLog(this.id,"The unsupervised clustering study is started on user's microblogs. The study will try to find natural clusters out of the user's microblogs, giving them tags. Also the study will apply topic modeling to extract topics from the contents.");
		//set the status of job as started
		clusterStarted=true;

		//if the user has no weibo at all, there should be no result
		boolean shouldStop;
		Weibo.withTransaction{
			def w=Weibo.findAll("from Weibo as w where w.ownerName=?",[u.weiboName]);
			if(w==null||w.size()==0){
				log.info "This user has no weibo at all. There should be no clusters formed.";
				jobLog(this.id,"No microblogs known for the clustering study.");
				shouldStop=true;
			}
		}
		if(shouldStop){
			HashMap<String,Integer> userWeiboCount=new HashMap<>();
			HashMap<String,Map> tagWeiboCount=new HashMap<>();
			def report=["users":userWeiboCount,"tags":tagWeiboCount];
			log.info "The empty cluster to return:"+report;
			clusterComplete=true;
			return report;
		}
		//get the network of user
		//Collection<User> friends=networkGenerator.getFriendUsers(u);
		//now only study the user
		Collection<User> friends=new HashSet<>();
		friends.add(u);

		//cluster all weibo of the friends
		jobLog(this.id,"Converting the microblog items to vectors to perform clustering calculation.");
		clusterManager.generateTrainingSet(friends);

		//segment the weibo as pre-processing
		def weibos=clusterManager.getTrainingset();
		def wordbags=SepManager.getSepManager().parallelSeg(weibos);//in the same order as trainingset
		jobLog(this.id,"The microblogs are segmented and passed to the next step.");

		//clusterManager.generateTrainingSet([u]);
		LinkedHashMap<Weibo,String> mappings=clusterManager.formClusters(wordbags);
		LinkedHashMap<String,List<Weibo>> closestWeibo=clusterManager.getDelegates();
		//format the data for display purpose
		Map<String,String> closestMap=new HashMap<>();
		closestWeibo.each{tag,weiboList->
			//content
			String content="";
			weiboList.each{weibo->
				if(weibo.isForwarded){
					content+='\n'+weibo.content+"//"+weibo.orgContent;
					//org owner
					//content+="\nOriginal Owner: "+weibo.orgOwnerName;
				}else{
					content+='\n'+weibo.content;
				}
				//url
				if(weibo.imageUrl!=null&&weibo.imageUrl!=""){
					//content+="\nMicroblog has image: "+weibo.imageUrl;
				}
			}
			closestMap.put(tag,content);
		}
		jobLog(this.id,"These are the most representative microblog items:");
		jobLog(this.id,closestMap.toString());
		//extract keywords from the weibo items
		def closestWords=[:];
		closestMap.each{tag,content->
			def words=SepManager.getSepManager().extractKeywords(content);
			log.info "Found ${words.size()} keywords for this tag ${tag}";
			closestWords.put(tag,words.keySet());
		}
		log.info "Extracted keywords from delegating weibo for each tag.";
		jobLog(this.id,"Extracted keywords from the delegating microblogs:");
		jobLog(this.id,closestWords.toString());
		
		/* Generate data for front end display */
		//count weibo of each user
		jobLog(this.id,"Counting microblog under each tag extracted.");
		HashMap<String,Integer> userWeiboCount=new HashMap<>();
		HashSet<String> tags=new HashSet<>();
		mappings.each{weibo,tag->
			String name=weibo.ownerName;
			if(userWeiboCount.containsKey(name)){
				userWeiboCount[name]+=1;
			}else{
				userWeiboCount.put(name,1);
			}

			tags.add(tag);
		}
		//generate count of weibo under each tag
		HashMap<String,Map> tagWeiboCount=new HashMap<>();
		tags.each{
			tagWeiboCount.put(it,new HashMap<String,Integer>());
		}
		mappings.each{weibo,tag->
			String name=weibo.ownerName;
			def map=tagWeiboCount[tag];
			if(map.containsKey(name)){
				tagWeiboCount[tag][name]+=1;
			}else{
				tagWeiboCount[tag].put(name,1);
			}
		}
		//sort before return
		for(def e:tagWeiboCount){
			def map=tagWeiboCount[e.key];
			tagWeiboCount[e.key]=map.sort{ a, b -> b.value <=> a.value }
		}
		jobLog(this.id,"Data formatting is complete. The tag counts are: ");
		jobLog(this.id,tagWeiboCount.toString());
		
		/* Topic modeling */
		//get topic from topic model
		jobLog(this.id,"Start to extract topics from the microblogs.");
		LDAManager lda=new LDAManager();
		def topics=lda.topicFlow(wordbags);
		jobLog(this.id,"Generated 20 latent topic groups from the contents:");
		jobLog(this.id,topics.toString());
		
		//generate date map for each
		jobLog(this.id,"Count microblogs under each tag on the timeline.");
		def rawCells=[];
		Set<Integer> allDates=new HashSet<>();
		closestWeibo.each{tag,weiboList->
			def dateMap=[:];
			weiboList.each{w->
				//put all dates in the set
				Date d=w.createdTime;
				if(d==null){}
				else{
					int y=d.getYear()+1900;
					int m=d.getMonth();
					int dateInt=y*100+m;
					allDates.add(dateInt);
					if(!dateMap.containsKey(dateInt)){
						dateMap.put(dateInt,1);
					}else{
						dateMap[dateInt]+=1;
					}
				}
			}
			dateMap.each{date,count->
				TimelineCell c=new TimelineCell(row:tag,column:date,value:count);
				rawCells.add(c);
			}
		}
		//re-order the date for return
		log.info "All the dates for topics are "+allDates;
		jobLog(this.id,"These are the dates that the tags will be counted under:");
		jobLog(this.id,allDates.toString());
		
		def allTags=closestWeibo.keySet();
		def allTagList=allTags as List;
		def allDateList=allDates as List;
		//convert the tags and dates to indices
		def cells=TimelineManager.convertCells(rawCells,allTagList, allDateList);
		log.info "The timeline cells are converted to ";
		log.info cells;
		jobLog(this.id,"The cells for timeline table are: ");
		jobLog(this.id,cells.toString());
		//generate a map holding useful data
		def timelineMap=[:];
		timelineMap["cells"]=cells;
		timelineMap["rows"]=allTagList;
		timelineMap["columns"]=allDateList;


		//generate the report
		//recordMappings(mappings);		
		
		//generate report
		def report=["users":userWeiboCount,"tags":tagWeiboCount,"delegates":closestWords,"topics":topics,"topicTimeline":timelineMap];
		log.info "The clustering result:"+report;
		clusterComplete=true;

		return report;
	}
	public timelineSlave(){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			jobLog(this.id,"Error! User is not found in timeline study!");
			return;
		}
		log.info "User passed to timeline study";
		jobLog(this.id,"Continue the timeline study.");
		
		timelineStarted=true;
		//if the user has no weibo at all, there should be no result
		boolean shouldStop;
		def weibos;
		Weibo.withTransaction{
			weibos=Weibo.findAll("from Weibo as w where w.ownerName=?",[u.weiboName]);
			if(weibos==null||weibos.size()==0){
				log.info "This user has no weibo at all. There should be no clusters formed.";
				jobLog(this.id,"No microblog items known for the timeline study.");
				shouldStop=true;
			}
			else{
				log.info "Found ${weibos.size()} weibo for this user";
			}
		}
		if(shouldStop){
			log.error "Stop since there are no weibo for this user found by system.";
			timelineComplete=true;
			return [:];
		}else{
			log.info "Continue the study flow";
			TimelineManager tm=new TimelineManager();
			def result=[:];
			//study the time line of weibo
			def spots=tm.timelineBlogs(weibos);
			result["spots"]=spots;
			def topics=tm.timelineTopics(weibos);
			result["topics"]=topics;

			def blocks=tm.blockedTimelineBlogs(weibos);
			result['blocks']=blocks;
			
			log.info "Results of timeline study: "+result;
			jobLog(this.id,"Timeline study is complete. The results are:");
			jobLog(this.id,result.toString());
			
			timelineComplete=true;
			return result;
		}


	}
	public recordMappings(def mappings){
		//output the mapping for next step LDA study
		String clusterBase='D:/pattern/tags';
		File folder=new File(clusterBase);
		if(!folder.exists()){
			log.info "Base folder doesn't exist now. Create it first.";
			folder.mkdirs();
		}
		Set<String> allTags=new HashSet<>();
		mappings.each{weibo,tag->
			allTags.add(tag);

		}
		log.info "Generating all files to store tag contents.";
		allTags.each{tag->
			File tagRepo=new File(clusterBase+'/${tag}.txt');
			if(tagRepo.exists()){
				tagRepo.delete();
				tagRepo.createNewFile();
			}
			else{
				tagRepo.createNewFile();
			}
			tagRepo.write("",'utf-8');
		}
		log.info "Now writing tag content to files.";
		mappings.each{weibo,tag->
			File tagRepo=new File(clusterBase+"/${tag}.txt");

			String text="";
			if(weibo.isForwarded){
				text=weibo.content+"//"+weibo.orgContent;
			}else{
				text=weibo.content;
			}
			tagRepo.append(text+'\n','utf-8');
		}
		log.info "Finished recording tag contents.";
	}
	public deepCrawlSlave(){
		log.info "Deep crawling on all user's weibo";
		//report
		log.info "Crawl with the data at hand: "+crawlReg;
		def data=crawlerMaster.crawlWeibo(crawlReg);
		log.info "Result of deep crawler: "+data;
		//clean the status preparing for restart
		cleanBeforeRestart();

		return data;
	}
	public updateCrawlStatus(def data){
		//get current count that is got
		int got=crawlerMaster.gotCount;
		def known=data?.knownCount?.toString();
		int knownCount;
		if(known==null||known==""){
			knownCount=0;
		}else{
			knownCount=Integer.parseInt(known);
		}
		int knownNow=got+knownCount;
		return knownNow;
	}
	public List getLog(){
		//get the log file
		List logs=JobLogger.getLog(this.id);
		log.info "Retrieved job log to be returned: "+logs;
		return logs;
		
	}
	@Override
	public String toString(){
		"""Job: 
id:${id}
user name:${userName}
"""
	}
	@Override
	public int hashCode(){
		id.hashCode()+userName.hashCode();

	}
}
