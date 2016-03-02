package codebigbrosub

import groovy.util.logging.Log4j
import keyword.KeywordsExtractor
import network.NetworkGenerator
import segmentation.SepManager
import timeline.TimelineCell
import timeline.TimelineManager
import topic.LDAManager
import clustering.ClusterManager
import clustering.MalletManager
import crawler.WeiboCrawlerMaster

@Log4j
class Job {
	String userName;


	boolean crawlerComplete;
	boolean keywordsComplete;
	boolean relationComplete;
	boolean networkComplete;
	boolean clusterComplete;
	boolean deepCrawlComplete;

	boolean crawlerStarted;
	boolean keywordsStarted;
	boolean relationStarted;
	boolean networkStarted;
	boolean clusterStarted;
	boolean deepCrawlStarted;

	boolean nothingToShow;


	MalletManager malletManager;
	ClusterManager clusterManager;
	NetworkGenerator networkGenerator;
	WeiboCrawlerMaster weiboCrawler;
	//TFIDFManager tfidfManager;

	def crawlReg;//temp storage for 1st level crawling, fundamental knowledge

	//the fields are not to be stored
	static transients = ["malletManager", "clusterManager", "networkGenerator", "weiboCrawler", "crawlReg"]
	static mapping={ id generator:"identity" }
	static constraints = {
		//userName(blank:true,nullable:true);
	}
	//	public Job()
	//		userName="empty";
	//
	//		malletManager=new MalletManager();
	//		clusterManager=new ClusterManager();
	//		networkGenerator=new NetworkGenerator();
	//		//println "Job is initiated "+this.id;
	//	}
	public Job(String name){
		userName=name;
		init();
		//println "Job is initiated "+this.id;
	}
	public init(){
		malletManager=new MalletManager();
		clusterManager=new ClusterManager();
		networkGenerator=new NetworkGenerator();
		weiboCrawler=new WeiboCrawlerMaster();

	}
	public void setUserName(String name){
		userName=name;
		log.info "Set the user name in job as ${userName}";
	}
	public report(){
		def status=["crawlStarted":crawlerStarted,"crawlComplete":crawlerComplete,
			"keywordsStarted":keywordsStarted, "keywordsComplete":keywordsComplete,
			"relationStarted":relationStarted,"relationComplete":relationComplete,
			"networkStarted":networkStarted,"networkComplete":networkComplete,
			"clusterStarted":clusterStarted,"clusterComplete":clusterComplete,
			"deepCrawlStarted":deepCrawlStarted,"deepCrawlComplete":deepCrawlComplete,
			"nothingToShow":nothingToShow
		];
		log.info status;
		return status;
	}
	public newUserCrawlSlave(){
		//crawlerStarted=true;
		//retrieve user
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			println "Cannot find user from job.";
			return;
		}
		//crawl the user
		def data=crawlSlave();

		//set all jobs to complete
		preventAllJob();

		//nothing to show since there is nothing available for studying
		nothingToShow=true;
		//some further check on user id overlap etc

		log.info "User preparation complete in job.";
		//crawlerComplete=true;

		return data;
	}
	public preventAllJob(){
		log.info "Mark all jobs as completed, preventing them from getting triggered.";
		keywordsStarted=true;
		keywordsComplete=true;
		relationStarted=true;
		relationComplete=true;
		networkStarted=true;
		networkComplete=true;
		clusterStarted=true;
		clusterComplete=true;
	}
	public cleanBeforeRestart(){
		log.info "Reset the status of all jobs preparing for starting again.";
		keywordsStarted=false;
		keywordsComplete=false;
		relationStarted=false;
		relationComplete=false;
		networkStarted=false;
		networkComplete=false;
		clusterStarted=false;
		clusterComplete=false;
		nothingToShow=false;
	}
	public crawlSlave(){
		crawlerStarted=true;
		def result;
		User.withSession{
			User u=User.find("from User as u where u.weiboName=?", [userName]);
			if(u==null){
				println "Cannot find user from job.";
				return;
			}

			//first level crawl
			result=weiboCrawler.scopeStudy(u);

			log.info "User first level crawling complete.";
			crawlerComplete=true;
			crawlReg=result;//store the result of first level crawling in a register
			log.info "Store the data at hand for next level crawler:"+crawlReg;

			//decide whether it's necessary to go on
			int known=result["knownCount"];
			if(known==null||known==0){
				log.info "No wweibo known by the system for further study.";
				nothingToShow=true;
			}
		}

		return result;
	}
	public keywordSlave(def session){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			return;
		}
		log.info "User passed to keyword study";
		if(u==null||u.weiboId==""){
			log.info "User is null. Keyword study cannot proceed.";
		}
		//set the status of job as started
		keywordsStarted=true;

		KeywordsExtractor.keywordStudy(u);

		Map<String,Double> map=new HashMap<>();
		log.info u?.keywords;
		u.keywords?.each{
			map.put(it.key,it.value);
		}
		//sort by value desc
		map=map.sort{ a, b -> b.value <=> a.value }
		log.info "Keyword map:"+map;
		keywordsComplete=true;
		return map;
	}
	public relationSlave(){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			return;
		}
		log.debug "User passed to relation study";
		//set the status of job as started
		relationStarted=true;
		//study all weibo, crawl them each to form relationship
		def support=networkGenerator.formNetwork(u);

		//study patterns
		def patterns=networkGenerator.studyPatterns();

		//generate result for display
		def relation=["forwarding":u.forwarding,"forwarded":u.forwarded,"commenting":u.commenting,"commented":u.commented,"support":support,"patterns":patterns];
		log.debug "User's interaction relations are: "+relation;
		relationComplete=true;
		return relation;
	}
	public networkSlave(){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			return;
		}
		log.debug "User passed to network study";
		networkStarted=true;

		//form network around user
		def similar=networkGenerator.findSimilar(u);

		log.info "User network study complete.";

		networkComplete=true;
		log.info "Network study result is "+similar;

		return similar
	}
	public clusterSlave(){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			return;
		}
		log.info "User passed to cluster study";
		//set the status of job as started
		clusterStarted=true;

		//if the user has no weibo at all, there should be no result
		boolean shouldStop;
		Weibo.withTransaction{
			def w=Weibo.findAll("from Weibo as w where w.ownerName=?",[u.weiboName]);
			if(w==null||w.size()==0){
				log.info "This user has no weibo at all. There should be no clusters formed.";
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
		clusterManager.generateTrainingSet(friends);

		//segment the weibo as pre-processing
		def weibos=clusterManager.getTrainingset();
		def wordbags=SepManager.getSepManager().parallelSeg(weibos);//in the same order as trainingset

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
		//extract keywords from the weibo items
		def closestWords=[:];
		closestMap.each{tag,content->
			def words=SepManager.getSepManager().extractKeywords(content);
			log.info "Found ${words.size()} keywords for this tag ${tag}";
			closestWords.put(tag,words.keySet());
		}
		log.info "Extracted keywords from delegating weibo for each tag.";

		//generate date map for each
		def rawCells=[];
		Set<Integer> allDates=new HashSet<>();
		closestWeibo.each{tag,weiboList->
			def dateMap=[:];
			weiboList.each{w->
				//put all dates in the set
				Date d=w.createdTime;
				if(d==null){}
				else{
					int y=d.getYear();
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
		def allTags=closestWeibo.keySet();
		def allTagList=allTags as List;
		def allDateList=allDates as List;
		//convert the tags and dates to indices
		def cells=TimelineManager.convertCells(rawCells,allTagList, allDateList);
		log.info "The timeline cells are converted to ";
		log.info cells;
		//generate a map holding useful data
		def timelineMap=[:];
		timelineMap["cells"]=cells;
		timelineMap["rows"]=allTagList;
		timelineMap["columns"]=allDateList;


		//generate the report
		//recordMappings(mappings);

		//get topic from topic model
		LDAManager lda=new LDAManager();
		def topics=lda.topicFlow(wordbags);

		/* Generate data for front end display */
		//count weibo of each user
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
		//generate report
		def report=["users":userWeiboCount,"tags":tagWeiboCount,"delegates":closestWords,"topics":topics,"topicTimeline":timelineMap];
		println report;
		clusterComplete=true;
		//save status update
		//job.merge(flush:true);

		//save status update
		//job.merge(flush:true);
		return report;
	}
	public timelineSlave(){
		User u=User.find("from User as u where u.weiboName=?", [userName]);
		if(u==null){
			log.info "Cannot find user from job.";
			return;
		}
		log.info "User passed to timleine study";
		//if the user has no weibo at all, there should be no result
		boolean shouldStop;
		def weibos;
		Weibo.withTransaction{
			weibos=Weibo.findAll("from Weibo as w where w.ownerName=?",[u.weiboName]);
			if(weibos==null||weibos.size()==0){
				log.info "This user has no weibo at all. There should be no clusters formed.";
				shouldStop=true;
			}
			else{
				log.info "Found ${weibos.size()} weibo for this user";
			}
		}
		if(shouldStop){
			log.error "Stop since there are no weibo for this user found by system."
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
		def data=weiboCrawler.crawlWeibo(crawlReg);
		//clean the status preparing for restart
		cleanBeforeRestart();

		return data;
	}
	public updateCrawlStatus(def data){
		//get current count that is got
		int got=weiboCrawler.gotCount;
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
