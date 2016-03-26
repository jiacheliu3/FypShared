package codebigbrosub

import static grails.async.Promises.*
import exceptions.UserNotFoundException
import grails.converters.JSON
import groovy.json.*
import groovy.util.logging.Log4j
import input.*
import keyword.*
import network.Network
import network.Network.Link
import search.*
import toolkit.SessionChecker
import crawler.UserWeiboCrawler

@Log4j
class EnvironmentController {

	static int errorCode;
	def index= {
		redirect(action:readInput);
	}

	def keywords={
		User u=User.find("from User order by weiboName");
		KeywordsExtractor.keywordStudy(u);
	}
	def readInput={
	}
	public static cleanUpSession(def session){
		log.info "Going to clean up the session.";
		SessionChecker.printSession(session);
		//clean up previous search information
		session["user"]=null;
		session["job"]=null;
		session["wName"]=null;
		session["wId"]=null;
		session["wUrl"]=null;
		session["keywords"]=null;
		session["stats"]=null;
		//session["relations"]=null;
		//session["network"]=null;
		session["interactions"]=null;
		session["clusters"]=null;
		session["timeline"]=null;
		session["crawler"]=null;
		session["running"]=null;
		session["jobId"]=null;
		SessionChecker.printSession(session);

	}
	public static prepareRestudy(def session){
		log.info "Going to clean up all existing study results for another round.";
		SessionChecker.printSession(session);
		session["keywords"]=null;
		session["stats"]=null;
		//session["relations"]=null;
		//session["network"]=null;
		session["interactions"]=null;
		session["clusters"]=null;
		session["timeline"]=null;
		session["crawler"]=null;
		SessionChecker.printSession(session);

	}
	//fork to two processes, one is just show start page, one is background process on user
	def getReady={
		//		session["wName"]=wName;
		//		session["wId"]=wId;
		//		session["wUrl"]=wUrl;

		//		WeiboManhuntThread hunt=new WeiboManhuntThread();
		//		hunt.run();
		//		def p=task{ Reader.main(); }
		//
		//		p.onError { Throwable err -> println "An error occured ${err.message}" }
		//		p.onComplete { result -> println "Promise returned $result" }
		//		p.get();

	}
	def beforeStart={
		//clean up session information
		cleanUpSession(session);


		String wName=request.getParameter("wName");
		String wId=request.getParameter("wId");
		String wUrl=request.getParameter("wUrl");
		session["wName"]=wName;
		session["wId"]=wId;
		session["wUrl"]=wUrl;
		//ensure the user can be found



		//redirect(action:start);
		redirect(controller:'framework',action:'account');
	}
	def start={
		//render request.getParameter("weiboUsername");
		//BackgroundController.loadTypes();

		String wName=session["wName"];
		//String wName=request.getParameter("wName");
		wName=wName?.trim();
		String wId=session["wId"];
		//String wId=request.getParameter("wId");
		wId=wId?.trim();
		String wUrl=session["wUrl"];
		//		String wUrl=request.getParameter("wUrl");
		wUrl=wUrl?.trim();
		User u;
		try{
			u=findWithInfo(session);
			session.setAttribute("user",u);
			log.info "User found";
		}
		catch(UserNotFoundException e){
			errorCode=100;
			redirect(action:"errorPage",model:"[errorCode:0]");
			return;
		}
		//if user already exists no need to create
		if(u==null){
			log.info "Not existing user";

		}
		else{
			log.info "Found existing user.";
			request.setAttribute("user",u);
		}



		//		KeywordWrapper[] arr=new KeywordWrapper[map.size()];
		//		int count=0;
		//		for(def e in map){
		//			def k=new KeywordWrapper(e);
		//			arr[count]=k;
		//			count++;
		//		}
		//		print arr;
	}
	public static User findWithInfo(def src){
		String wName=src["wName"];
		String wId=src["wId"];
		String wUrl=src["wUrl"];
		log.debug "name:${wName},id:${wId},url:${wUrl}";
		User u;
		boolean found;
		if (wId!=null){
			u=User.find("from User as u where u.weiboId=?", [wId]);
			if(u!=null){
				log.info "User found by id"
				found=true;
			}

		}
		if(wName!=null&&found==false){
			//check if user existing
			u=User.find("from User as u where u.weiboName=?", [wName]);
			if(u!=null){
				log.info "User found by name";
				found=true;
			}

		}
		if(wUrl!=null&&found==false){
			//redirect to detailed page



		}
		if(found==false){
			log.info "User not found at last";
			throw new UserNotFoundException();

		}
		return u;
	}
	def errorPage={
		switch(errorCode){
			case 100:case '100':
				render "User not found!";
				break;
		}
	}
	def userNotFound(String wName,String wId,String wUrl){
		[wName:wName,wId:wId,wUrl:wUrl]
	}
	def scanUsers(String wName,String wId,String wUrl){
		[wName:wName,wId:wId,wUrl:wUrl]

	}
	def wordcloud={
		//render request.getParameter("weiboUsername");
		//BackgroundController.loadTypes();


		String name=request.getParameter("weiboUsername");
		String path="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\userFiles\\"
		User u=BackgroundController.read(new File(path+name+".json"));
		request.setAttribute("user",u);

		//generate map for word cloud
		Map<String,Integer> map=new HashMap<>();
		u.keywords.each{
			map.put(it.key,(int)it.value);
		}


		//		KeywordWrapper[] arr=new KeywordWrapper[map.size()];
		//		int count=0;
		//		for(def e in map){
		//			def k=new KeywordWrapper(e);
		//			arr[count]=k;
		//			count++;
		//		}
		//		print arr;
		def json=JsonOutput.toJson(map);
		log.debug "User keyword map converted to json";
		log.debug json;

		request.setAttribute("cloudJson",json);
	}
	public static class KeywordWrapper{
		def content;
		def weight;
		KeywordWrapper(def e){
			content=e.key;
			weight=e.value;
		}
		@Override
		String toString(){
			return "${content}:${weight}";
		}
		@Override
		int hashCode(){
			return content.hashCode()+weight.hashCode();
		}
	}
	def network={
		User u;
		try{
			u=findWithInfo(session);
		}
		catch(UserNotFoundException e){
			errorCode=100;
			redirect(action:"errorPage",model:"[errorCode:0]");
			return;
		}
		session.setAttribute("user",u);
	}

	def asynchroUser={
		try{
			User u=session["user"];
			def data=[:];
			if(u==null){
				log.info "User not found on user info request!";
				data["userName"]="Not found";
				data["userId"]="Not found";
				data["userUrl"]="Not found";
				data["userFace"]="#";

			}else{
				data["userName"]=u.weiboName;
				data["userId"]=u.weiboId;
				data["userUrl"]=u.url;
				data["userFace"]=u.faceUrl;

			}
			render data as JSON;
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}

	}
	def asynchroCrawlerStatus={
		try{
			//check if the info already exists in session
			//Map<String,String> data=session["crawler"];
			Map<String,String> data=session["crawler"];
			//get job to keep stack of status
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);

			//		var got=jsonData["gotCount"];
			//		var all=jsonData["weiboCount"];
			if(!(data==null||data=="")){
				def result=[:];
				int gotNow=job.updateCrawlStatus(data);
				data["gotCount"]=gotNow;

				render (data as JSON);


			}else{
				render(status:200,text:'{}');
			}


			return;
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}
	}
	def asynchroDeepCrawl={
		try{
			//check if the info already exists in session
			def data=session["deepCrawl"];
			if(!(data==null||data=="")){
				render data as JSON;
				return;
			}
			//start of job, retrieve job from session
			String jobId=session["jobId"];

			Job job=JobController.trackJob(jobId);
			log.info "Retrieved job "+jobId+" in deep crawl.";
			if(job==null){
				render(status:200,text:'{}');
			}
			//check if started already
			if(job.deepCrawlComplete==true){
				log.info("Crawler already finished. No need for another crawl.");
				render(status:200,text:'{}');
				return;
			}else if(job.deepCrawlStarted==true){
				log.info "Job already started. No need for another deep crawl task.";
				render(status:200,text:'{}');
				return;
			}

			//if not, start studying again
			User u=session.getAttribute("user");
			//session["user"]=null;
			log.info "User passed successfully";

			job.deepCrawlStarted=true;

			//if keyword and relation studies are not yet finished, fire them first
			def result=JobController.jobMaster(session,job,'deepCrawl');

			job.deepCrawlComplete=true;

			chain(action:studyRestart,params:[jobId:jobId]);
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}
	}
	def studyRestart={
		prepareRestudy(session);


		chain(controller:'framework',action:'account',params:params)
	}
	def restartEntrance={
		prepareRestudy(session);
		//reset the job
		String jobId=session["jobId"];
		Job job=JobController.trackJob(jobId);
		job.cleanBeforeRestart();
		log.info "Prepared job for restart.";
		chain(action:asynchroKeywords,params:params);
	}
	def studyRestartTest={
		log.info "Test succeed.";
		render "Ok to restart";
	}
	def asynchroCrawl={
		try{
			//check if the info already exists in session
			def data=session["crawler"];
			if(!(data==null||data=="")){
				log.debug "The data is ";
				log.debug data;
				render data as JSON;
				return;
			}
			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.debug "Retrieved job "+job;
			//if crawling has already finished, no need for another round
			if(job.userCrawlComplete==true){
				log.info("Crawler already finished. No need for another crawl.");
				render(status:200,text:'{}');
				return;
			}else if(job.userCrawlStarted==true){
				log.info("Crawler has already started. Abort this one.");
				render(status:200,text:'{}');
				return;
			}

			//if not, start studying again
			User u=session.getAttribute("user");
			//session["user"]=null;
			log.info "User passed successfully";

			//if keyword and relation studies are not yet finished, fire them first
			def result=JobController.jobMaster(session,job,'crawl');
			//
			//		job.crawlerStarted=true;
			//		println "Crawling task started.";
			//		NetworkGenerator.formNetwork(u);
			//		println "User crawl complete.";
			//		job.crawlerComplete=true;
			//		//save status update
			//		job.merge(flush:true);
			//		def dummy=[];
			//		render dummy as JSON;
			//render data as JSON;

			chain(action:asynchroKeywords,params:[jobId:jobId]);

			//chain(action:asynchroClusters,params:[jobId:jobId])
			//chain(action:asynchroRelation,params:[jobId:jobId])
			//chain(action:asynchroNetwork,params:[jobId:jobId])
			//chain(action:asynchroDeepCrawl,params:[jobId:jobId])

			//chain(action:asynchroInteraction,params:[jobId:jobId])
			//chain(action:asynchroTestStats,params:[jobId:jobId])
		}catch(Exception e){
			e.printStackTrace();
			render(status:500,text:e.message);
		}
	}
	def asynchroKeywords={
		try{
			//check if the info already exists in session
			def data=session["keywords"];
			if(!(data==null||data=="")){
				log.debug "The data is ";
				log.debug data;
				render data as JSON;
				return;
			}
			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);

			if(job.keywordsStarted){
				log.info "Keyword study already started. Abort new request.";
				render(status:200,text:'{}');
				return;
			}
			//println "Cluster job:"+job.crawlerComplete;
			if(!job.userCrawlComplete){
				log.info "Crawling not yet finished. Keyword study abort.";
				render(status:200,text:'{}');
				return;
			}
			JobController.jobMaster(session,job,"keyword");
			chain(action:asynchroStats,params:params);
		}catch(Exception e){
			e.printStackTrace();
			render(status:500,text:e.message);

		}


	}
	def asynchroStats={
		try{
			//check if the info already exists in session
			def data=session["stats"];
			if(!(data==null||data=="")){
				render data as JSON;
				return;
			}

			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.info "Retrieved job "+job+" in cluster";

			if(job?.statStarted){
				log.info "Statistic study has already started. Abort this one.";
				render(status:200,text:'{}');
				return;
			}
			log.info "Stats study triggered.";
			JobController.jobMaster(session,job,'stats');
			

			//redirect(controller:'framework',action:'account');
			chain(action:asynchroInteraction,params:[jobId:jobId])
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}

	}
	//	def asynchroRelation={
	//		try{
	//			//check if the info already exists in session
	//			def data=session["relations"];
	//			if(!(data==null||data=="")){
	//				log.debug "The data is ";
	//				log.debug data;
	//				render data as JSON;
	//				return;
	//			}
	//			//start of job, retrieve job from session
	//			String jobId=session["jobId"];
	//			Job job=JobController.trackJob(jobId);
	//			log.info "Retrieved job "+job+" in relation";
	//			//if there's already another relation study, nothing to return for this
	//			if(job.relationStarted){
	//				render(status:200,text:'{}');
	//				return;
	//			}
	//			if(!job.userCrawlComplete){
	//				println "Crawling not yet finished. Relation study abort.";
	//				render(status:200,text:'{}');
	//				return;
	//			}
	//
	//			JobController.jobMaster(session,job,'relation');
	//			chain(action:asynchroStats,params:params);
	//			//chain(action:asynchroNetwork,params:params);
	//			//chain(action:asynchroClusters,params:params);
	//		}catch(Exception e){
	//			e.printStackTrace();
	//			render(status:500);
	//
	//		}
	//
	//	}
	//	def asynchroNetwork={
	//		try{
	//			//check if the info already exists in session
	//			def data=session["network"];
	//			if(!(data==null||data=="")){
	//				log.debug "The data is ";
	//				log.debug data;
	//				render data as JSON;
	//				return;
	//			}
	//			//start of job, retrieve job from session
	//			String jobId=session["jobId"];
	//			Job job=JobController.trackJob(jobId);
	//			log.debug "Retrieved job "+job+" in network";
	//			//if the service already started
	//			if(job.networkStarted){
	//				log.info "Another network study request comes in while one is already in progress. Nothing to return in this request.";
	//				render(status:200,text:'{}');
	//				return;
	//			}
	//			//		if(!(job.keywordsComplete&&job.relationComplete)){
	//			//			log.info "Keyword and relation not yet finished. Network study abort.";
	//			//			return;
	//			//		}
	//			JobController.jobMaster(session,job,'network');
	//			//chain(action:asynchroClusters,params:params);
	//			chain(action:asynchroTimeline,params:params);
	//		}catch(Exception e){
	//			e.printStackTrace();
	//			render(status:500);
	//
	//		}
	//
	//	}
	//merge relation and network to interaction
	def asynchroInteraction={
		try{
			//check if the info already exists in session
			def data=session["interactions"];
			if(!(data==null||data=="")){
				log.debug "The data is ";
				log.debug data;
				render data as JSON;
				return;
			}
			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.info "Retrieved job "+job+" in interaction";
			//if there's already another relation study, nothing to return for this
			if(job.interactionStarted){
				log.info "Interaction study already started. No need to start another one.";
				render(status:200,text:'{}');
				return;
			}
			if(!job.statComplete){
				log.info "Stat study not yet finished. Interaction study abort.";
				render(status:200,text:'{}');
				return;
			}

			JobController.jobMaster(session,job,'interaction');
			log.info "Interaction study is complete.";


			chain(action:asynchroClusters,params:[jobId:jobId])
		}catch(Exception e){
			log.error "An error occurred in interaction study.";
			e.printStackTrace();
			render(status:500,text:e.message);
		}
	}
	def asynchroClusters={
		try{
			//check if the info already exists in session
			def data=session["clusters"];
			if(!(data==null||data=="")){
				log.debug "The data is ";
				log.debug data;
				render data as JSON;
				return;
			}

			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.info "Retrieved job "+job+" in cluster";
			if(job==null){
				log.info "No job fetched.";
				render(status:200,text:'{}');
				return;
			}

			if(job.clusterStarted){
				log.info "Cluster study has already started. Abort this one.";
				render(status:200,text:'{}');
				return;
			}
//			if(!job.interactionComplete){
//				log.info "Network not yet finished. Cluster study abort.";
//				render(status:200,text:'{}');
//				return;
//			}

			JobController.jobMaster(session,job,'cluster');
			chain(action:asynchroTimeline,params:params);
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}
	}
	def asynchroTimeline={
		try{
			//check if the info already exists in session
			def data=session["timeline"];
			if(!(data==null||data=="")){
				render data as JSON;
				return;
			}
			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.info "Retrieved job "+job+" in cluster";
			if(job.timelineStarted){
				log.info "Timeline study has already started. Abort this one.";
				render(status:200,text:'{}');
				return;
			}
			if(!job.clusterComplete){
				log.info "Cluster study not yet finished. Timeline study abort.";
				render(status:200,text:'{}');
				return;
			}
			log.info "Timeline triggered.";
			JobController.jobMaster(session,job,'timeline');

			redirect(controller:'framework',action:'account');

		}catch(Exception e){
			e.printStackTrace();
			render(status:500);
		}
	}
	/* Dedicated for geographical info */
	def asynchroMap={
		def l=[];
		log.info "Requesting map info";
		String jsonRaw='[["Gansu", 48], ["Qinghai", 47], ["Guangxi", 45], ["Guizhou", 35], ["Chongqing", 34], ["Beijing", 12], ["Fujian", 35], ["Anhui", 6], ["Guangdong", 40], ["Xizang", 3], ["Xinjiang", 12], ["Hainan", 21], ["Ningxia", 8], ["Shaanxi", 40], ["Shanxi", 11], ["Hubei", 1], ["Hunan", 23], ["Sichuan", 19], ["Yunnan", 19], ["Hebei", 34], ["Henan", 20], ["Liaoning", 14], ["Shandong", 0], ["Tianjin", 12], ["Jiangxi", 20], ["Jiangsu", 37], ["Shanghai", 34], ["Zhejiang", 46], ["Jilin", 38], ["Inner Mongol", 10], ["Heilongjiang", 20], ["Taiwan", 45], ["Xianggang", 35], ["Macau", 10]]';
		render(status:200,text:jsonRaw);
	}
	/* Added in v1.2 sending local json data to external */
	def asynchroMainlandData={
		String content=FileVisitor.readFileContent('maps/zh-mainland-provinces.topo.json');
		if(content==""){
			log.error "Unable to get map data!";
		}else{
			log.info "Read data from map in json";
			render(status:200,text:content);

		}
	}
	def asynchroTaiwanData={
		String content=FileVisitor.readFileContent('maps/zh-chn-twn.topo.json');
		if(content==""){
			log.error "Unable to get map data!";
		}else{
			log.info "Read data from map in json";
			render(status:200,text:content);

		}
	}
	def asynchroMacauData={
		String content=FileVisitor.readFileContent('maps/zh-hkg-mac.topo.json');
		if(content==""){
			log.error "Unable to get map data!";
		}else{
			log.info "Read data from map in json";
			render(status:200,text:content);

		}
	}
	/* wrapper classes */
	public static class NodeWrapper{
		def match;
		def name;
		def artist;
		def id;
		def playcount;
		NodeWrapper(String s){
			match=1;
			name=s;
			artist=s;
			id=s;
			playcount=1000;
		}
	}
	public static class EdgeWrapper{
		def source;
		def target;
		EdgeWrapper(String s,String t){
			source=s;
			target=t;
		}
	}
	/* test zone */
	def coffee={}
	def graph={
		def inputFile = new File("D:\\graph.json");
		//def s='{"nodes":[{"group":1,"name":"@\u6b63\u4e49\u5929\u5802\u7684\u6d77\u89d2"}],"links":[]}';
		//def j = new JsonSlurper().parseText(inputFile.text);

		//println(j);
		request.setAttribute("data",inputFile.text);

		def network='{"nodes":[{"group":1,"name":"@qwe25322570"},{"group":1,"name":"@\u6c88\u9633\u8f9b\u98962\u4e16"},{"group":1,"name":"@\u660e\u5fb7\u7b03\u884c"},{"group":1,"name":"@\u6c88\u9633\u53ef\u4e50"},{"group":1,"name":"\u6c88\u9633\u8f9b\u98962\u4e16"},{"group":1,"name":"@\u6c88\u9633\u5f6d\u79c0\u8363"},{"group":1,"name":"@\u6c88\u9633\u738b\u632f\u534e"}],"links":[{"value":1.0,"target":0,"source":1},{"value":1.0,"target":0,"source":2},{"value":1.0,"target":0,"source":3},{"value":1.0,"target":0,"source":4},{"value":1.0,"target":0,"source":5},{"value":1.0,"target":0,"source":6}]}';

		request.setAttribute("network",network);


	}
	def progress={}
	def crawlUser={ UserWeiboCrawler.main(); }
	def bootstrap={

	}
	//notice trial page
	def testNotice={
		try{
			log.info "Outer try";
			try{
				log.info "Inner try";
			}catch(Exception e){
				log.error "Got exception in inner try";
				e.printStackTrace();
			}finally{
				log.info "Inner finally";
			}
		}finally{
			log.info "Outer finally";
		}
	}
	def createError={
		def x=5.0/0.0;
		render x as JSON;
	}
	def asynchroTopics={
		try{
			//check if the info already exists in session
			def raw=session["clusters"];
			def data=raw["topicTimeline"];
			if(!(data==null||data=="")){
				render data as JSON;
				return;
			}
			else{
				render(status:500);
			}
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}

	}
	def asynchroTestStats={
		log.info "Got request for stats. Use test info";
		def gender;
		def age;
		def geo;
		def tags;
		def edu;
		def work;
		def authCount;
		def authList;
		def introKeywords;
		def longestIntro;
		def introUser;
		def allCount;

		gender=["male":50,"female":20,"unknown":5];
		age=["known":[20, 31, 15, 14, 16, 16, 18, 20, 25, 60, 72, 50, 44, 42, 10, 5, 22, 35, 34, 32, 25, 40, 39, 20, 22],"unknown":40];
		geo=["known":[["Gansu", 48], ["Qinghai", 47], ["Guangxi", 45], ["Guizhou", 35], ["Chongqing", 34], ["Beijing", 12], ["Fujian", 35], ["Anhui", 6], ["Guangdong", 40], ["Xizang", 3], ["Xinjiang", 12], ["Hainan", 21], ["Ningxia", 8], ["Shaanxi", 40], ["Shanxi", 11], ["Hubei", 1], ["Hunan", 23], ["Sichuan", 19], ["Yunnan", 19], ["Hebei", 34], ["Henan", 20], ["Liaoning", 14], ["Shandong", 0], ["Tianjin", 12], ["Jiangxi", 20], ["Jiangsu", 37], ["Shanghai", 34], ["Zhejiang", 46], ["Jilin", 38], ["Inner Mongol", 10], ["Heilongjiang", 20], ["Taiwan", 45], ["Xianggang", 35], ["Macau", 10]],"unknown":[:]];
		tags=["青年律师 ":2,"律师":3,"心理学":1, "社会 ":1,"创业":5,"二次元":1,"美国留学":6,"留学":8,"学习":3,"NBA":15,"英超":2,"球迷":10,"读书":6];
		edu=["unknown":30,"北京大学":12,"重庆文理学院":1,"南京大学":2,"北京师范大学":5,"平顶山教育学院":1,"叶县一高":2,"郑州科技学院":1,"西藏藏医学院":1,"兰州医学院":1,"兰州六中":5,"杂多民族中学":1,"法一小学":3];
		work=["重庆市协和心理顾问事务所":5,"北京东城访爷无限责任公司":1,"unknown":120,"南昌理工学院":1];
		longestIntro=["content":"胸怀正义 敢于执言 维护公平 创建和谐 新浪微博社区委员会专家成员","user":"Alala"];
		introKeywords=["足以":0.989, "关注":0.989, "研修":0.989, "传人":0.989, "院长":0.989, "百姓":0.989, "简易房":0.989, "中国":0.989, "执言":0.989, "专家":0.506, "汽车":0.506, "敢于":0.506, "创造":0.506, "心理":0.506, "维护":0.506, "公平":0.506, "网站":0.506, "空白":0.506];
		introUser="Atwatere";
		authCount=15;
		allCount=100;

		def intro=["introKeyword":introKeywords,"introLong":longestIntro,"introUser":introUser];
		def auth=["authKeyword":introKeywords,"authLong":longestIntro,"authUser":introUser,"authCount":authCount];


		//forge data
		def result=['all':allCount,'gender':gender,'age':age,'geo':geo,'tag':tags,'edu':edu,'work':work,'auth':auth,'intro':intro];


		render result as JSON;
	}
	def asynchroTestNetwork={
		//make networks and find cliques
		def rawLinks1="""[
  {
    "source": "P1",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P1",
    "target": "P3",
    "value": 1
  },
  {
    "source": "P1",
    "target": "P4",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P1",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P3",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P4",
    "value": 1
  },
  {
    "source": "P2",
    "target": "P7",
    "value": 1
  },
  {
    "source": "P3",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P3",
    "target": "P4",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P1",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P3",
    "value": 1
  },
  {
    "source": "P4",
    "target": "P6",
    "value": 1
  },
  {
    "source": "P5",
    "target": "P6",
    "value": 1
  },
  {
    "source": "P5",
    "target": "P7",
    "value": 1
  },
  {
    "source": "P5",
    "target": "P8",
    "value": 1
  },
  {
    "source": "P6",
    "target": "P2",
    "value": 1
  },
  {
    "source": "P6",
    "target": "P5",
    "value": 1
  },
  {
    "source": "P6",
    "target": "P7",
    "value": 1
  },
  {
    "source": "P7",
    "target": "P5",
    "value": 1
  },
  {
    "source": "P7",
    "target": "P6",
    "value": 1
  },
  {
    "source": "P8",
    "target": "P5",
    "value": 1
  },
  {
    "source": "P8",
    "target": "P9",
    "value": 1
  },
  {
    "source": "P9",
    "target": "P8",
    "value": 1
  }
]""";
		def links1=JSON.parse(rawLinks1);
		def nodes1=new ArrayList<String>();
		nodes1.addAll(["P1", "P2", "P3", "P5", "P4", "P6", "P7", "P8", "P9"]);
		Network n1=new Network();
		nodes1.each{name->
			Node n=new Node(name:name,group:1);
			n1.addNode(n);
		}
		links1.each{
			Link l=new Link(source:it.source,target:it.target,value:it.value);
			n1.addLink(l);
		}
		log.info "The sample network is ${n1} and IMA find cliques outta it.";
		def map=[:];
		map.put("full",n1);
		map.put("simple",n1);
		render map as JSON;
	}
	def asynchroTestRelation={
	}
	def asynchroLog={
		try{
			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.info "Retrieved job "+job+" in cluster";
			if(job==null){
				log.error "No job found in log retrieval.";
				render(status:500,text:"");
				return;
			}
			//get the log file based on job id
			List theLogs=job.getLog();
			
			render theLogs as JSON;
		}catch(Exception e){
			e.printStackTrace();
			render(status:500,text:e.message);
		}
	}
}
