package codebigbrosub

import static grails.async.Promises.*

import java.util.LinkedHashMap;


import exceptions.UserNotFoundException
import grails.converters.JSON
import groovy.json.*
import input.*
import keyword.*
import network.NetworkGenerator
import search.*
import clustering.ClusterManager
import crawler.UserWeiboCrawler
import groovy.util.logging.Log4j

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
		log.info "Clean up the session.";
		//clean up previous search information
		session["user"]=null;
		session["job"]=null;
		session["wName"]=null;
		session["wId"]=null;
		session["wUrl"]=null;
		session["keywords"]=null;
		session["relations"]=null;
		session["network"]=null;
		session["clusters"]=null;
		session["crawler"]=null;
		session["running"]=null;
		session["jobId"]=null;

	}
	public static prepareRestudy(def session){

		session["keywords"]=null;
		session["relations"]=null;
		session["network"]=null;
		session["clusters"]=null;
		session["crawler"]=null;
		//deep crawl data is not cleared

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
				render data as JSON;
				return;
			}
			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.debug "Retrieved job "+job;
			//if crawling has already finished, no need for another round
			if(job.crawlerComplete==true){
				log.info("Crawler already finished. No need for another crawl.");
				render(status:200,text:'{}');
				return;
			}else if(job.crawlerStarted==true){
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
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}
	}
	def asynchroNetwork={
		try{
			//check if the info already exists in session
			def data=session["network"];
			if(!(data==null||data=="")){
				render data as JSON;
				return;
			}
			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.debug "Retrieved job "+job+" in network";
			//if the service already started
			if(job.networkStarted){
				log.info "Another network study request comes in while one is already in progress. Nothing to return in this request.";
				render(status:200,text:'{}');
				return;
			}
			//		if(!(job.keywordsComplete&&job.relationComplete)){
			//			log.info "Keyword and relation not yet finished. Network study abort.";
			//			return;
			//		}
			JobController.jobMaster(session,job,'network');
			chain(action:asynchroClusters,params:params);
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}

	}
	public static void blockTillReady(def session){
		boolean keywordsDone=session["keywordsComplete"];
		boolean relationDone=session['relationComplete'];
		while(!(keywordsDone&&relationDone)){
			sleep(300);
			keywordsDone=session["keywordsComplete"];
			relationDone=session['relationComplete'];
		}
	}
	def asynchroRelation={
		try{
			//check if the info already exists in session
			def data=session["relations"];
			if(!(data==null||data=="")){
				log.info "Found existing data, proceed and return.";
				render data as JSON;
				return;
			}
			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.info "Retrieved job "+job+" in relation";
			//if there's already another relation study, nothing to return for this
			if(job.relationStarted){
				render(status:200,text:'{}');
				return;
			}
			if(!job.crawlerComplete){
				println "Crawling not yet finished. Relation study abort.";
				render(status:200,text:'{}');
				return;
			}

			JobController.jobMaster(session,job,'relation');
			chain(action:asynchroNetwork,params:params);
			//chain(action:asynchroClusters,params:params);
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}

	}
	def asynchroKeywords={
		try{
			//check if the info already exists in session
			def data=session["keywords"];
			if(!(data==null||data=="")){
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
			if(!job.crawlerComplete){
				log.info "Crawling not yet finished. Keyword study abort.";
				render(status:200,text:'{}');
				return;
			}
			JobController.jobMaster(session,job,"keyword");
			//save status update
			//		job.merge(flush:true);


			chain(action:asynchroRelation,params:params);
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}


	}
	def asynchroClusters={
		try{
			//check if the info already exists in session
			def data=session["clusters"];
			if(!(data==null||data=="")){
				render data as JSON;
				return;
			}

			//start of job, retrieve job from session
			String jobId=session["jobId"];
			Job job=JobController.trackJob(jobId);
			log.info "Retrieved job "+job+" in cluster";

			if(job.clusterStarted){
				log.info "Cluster study has already started. Abort this one.";
				render(status:200,text:'{}');
				return;
			}
			if(!job.networkComplete){
				log.info "Network not yet finished. Cluster study abort.";
				render(status:200,text:'{}');
				return;
			}

			JobController.jobMaster(session,job,'cluster');

			redirect(controller:'framework',action:'account');
			//chain(action:asynchroDeepCrawl,params:params);
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}
	}

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
//
//			if(job.clusterStarted){
//				log.info "Cluster study has already started. Abort this one.";
//				render(status:200,text:'{}');
//				return;
//			}
//			if(!job.networkComplete){
//				log.info "Network not yet finished. Cluster study abort.";
//				render(status:200,text:'{}');
//				return;
//			}

			JobController.jobMaster(session,job,'timeline');
			log.info "Timeline triggered.";
			
		}catch(Exception e){
			e.printStackTrace();
			render(status:500);

		}
		
		
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
}
