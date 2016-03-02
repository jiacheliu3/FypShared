package codebigbrosub



import static org.springframework.http.HttpStatus.*

import java.util.regex.Matcher
import java.util.regex.Pattern

import job.JobRepository
import timeline.TimelineManager
import clustering.ClusterManager
import exceptions.UserNotFoundException
import grails.converters.JSON
import groovy.util.logging.Log4j


@Log4j
class FrameworkController {

	static scope = "session"

	int errorCode;

	Job job;//deprecated



	def start={
	}
	/*Return the current job in process*/


	def statusMonitor={
		//		if(job==null){
		//			log.info "Lost track of job...";
		//			//render [];
		//		}
		String jobId=session.jobId;
		//log.info "Tracking job with id "+jobId;
		//Job job=Job.findById(jobId);
		Job job=JobRepository.findJob(jobId);
		def statusNow=job?.report();
		//log.info statusNow;
		render statusNow as JSON;
	}
	def account={
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
			u=EnvironmentController.findWithInfo(session);
			session.setAttribute("user",u);
			log.info "User found";
		}
		catch(UserNotFoundException e){
			//maybe the user is not known
			log.info "User is not found.";
			//lead to detailed form
			redirect(controller:"framework",action:"detailedIndex");

			return;
		}
		//if user already exists no need to create
		if(u==null){
			log.info "Not existing user";

		}
		else{
			log.info "Found existing user. Job start.";
			//check if a new job shall be created
			def running=session["running"];
			log.info "Running status is ${running}"
			if(running==null||running==""){
				log.info "Need to init new job.";

				//create new job
				Job job=JobController.initiateJob(u.weiboName);
				session["jobId"]=job.id;
				log.info "Stored job in session "+job;
				session["running"]=job.id;
				request.setAttribute("user",u);



			}else{
				log.info "Job existing.";
			}
		}
	}
	def keywords={
		User u=session["user"];
		if(u!=null)
			log.info "Successfully get user ${u}"
		else{
			log.info "User lost in the way..."
			u=request["user"];
			if(u==null)
				log.info "Still get no user"
		}

	}
	def relation={
		User u=session["user"];
		if(u!=null)
			log.info "Successfully get user ${u}"
		else{
			log.info "User lost in the way..."
			u=request["user"];
			if(u==null)
				log.info "Still get no user"
		}
	}
	def network={
		User u=session["user"];
		if(u!=null)
			log.info "Successfully get user ${u}"
		else{
			log.info "User lost in the way..."
			u=request["user"];
			if(u==null)
				log.info "Still get no user"
		}

		//		if(job.relationComplete&&job.keywordsComplete){
		//			log.info "Ok to start network study";
		//			//request.setAttribute("ready","true");
		//		}
	}
	def blank={

	}
	def fonts(){

	}
	def clusterTest={ ClusterManager.main(); }
	def cluster={

		User u=session["user"];
		if(u!=null)
			log.info "Successfully get user ${u}"
		else{
			log.info "User lost in the way..."
			u=request["user"];
			if(u==null)
				log.info "Still get no user"
			else
				log.info "Retrieved user from request...";
		}

		//		if(job.networkComplete){
		//			log.info "Ok to start cluster study";
		//			//request.setAttribute("ready","true");
		//		}
	}
	def graph={

	}
	def newIndex={}
	def detailedIndex={

	}
	def crawlBeforeStart={
		log.info "Crawler is called for before anything could start.";
		//in this case only the id and url are useful
		//render "Go go the crawler is ready";

		String wId=request.getParameter("wId");
		String wUrl=request.getParameter("wUrl");
		session["wId"]=wId;
		session["wUrl"]=wUrl;

		//make sure no such user exists
		boolean gotId=(wId!=null&&wId!="");
		boolean gotUrl=(wUrl!=null&wUrl!="");
		boolean dup;
		String targetUrl;
		log.info "Got id: ${gotId}, Got url: ${wUrl}";
		User u;
		if(gotId){
			u=User.find("from User as u where u.weiboId=:wId",[wId:wId]);
			if(u==null){
				log.info "No existing user in the system.";
			}
			else{
				log.info "Found existing user in the system with weiboId=${wId}";
				dup=true;
			}
		}else if(gotUrl){
			u=User.find("from User as u where u.url=:url",[url:wUrl]);
			if(u==null){
				log.info "No existing user in the system.";
			}
			else{
				log.info "Found existing user in the system with url=${wUrl}";
				dup=true;
			}
		}
		if(dup){
			log.info "Lucky we know this user ${u}. \nResume to normal track.";
			//redirect to normal flow
			redirect(action:'account');
			return;
		}
		//prepare the target url
		boolean validUrl;
		if(gotUrl){
			targetUrl=fillUrl(wUrl);
		}
		else if(gotId){
			targetUrl=fillUrlById(wId);
		}

		//preparations: new job, new user, store in session
		def data=[:];
		data=JobController.prepareUserAndJob(targetUrl,session);
		



		redirect(action:"account")

	}
	public static String fillUrl(String wUrl){
		String url;
		if(!wUrl.startsWith('http://')){
			wUrl='http://'+wUrl;
		}
		if(wUrl.contains('weibo.cn')){
			log.debug "Under zone mobile.";
			if(wUrl.contains('www.weibo.cn')){
				log.info "www.weibo.cn is not a valid url.";
				url=wUrl.replace('www.',"");
			}
			else if (!wUrl.contains('/u/')){
				log.info "A valid user page should be in form weibo.cn/u/123456";
				url=wUrl;
			}else{
				url=wUrl;
			}
		}
		else if(wUrl.contains('www.weibo.com')){
			log.debug "Under zone of china weibo.";
			url=wUrl.replace('www.weibo.com','weibo.cn');
			
		}
		else if(wUrl.contains('weibo.com')){
			log.debug "Under zone of china weibo.";
			url=wUrl.replace('weibo.com','weibo.cn');
			
		}
		else if(wUrl.contains('hk.weibo.com')){
			log.debug "Under zone of hk.";
			//generate url based on this url from hk zone
			String reg;
			int idStart=wUrl.indexOf('index/');
			if(idStart==-1){
				log.info "Failed to extract user id from url ${wUrl}";
				reg=wUrl;
			}else{
				reg=wUrl.substring(idStart+5);
			}
			Pattern p=Pattern.compile("\\d{5,15}");
			Matcher m=p.matcher(reg);
			if(m.find()){
				String wId=m.group();
				url="http://weibo.cn/u/${wId}";
			}else{
				log.error "User id not located from hk user homepage!";
				url=wUrl;
			}
		}
		log.info "The filled url is "+url;
		return url;

	}
	public static String fillUrlById(String wId){
		wId=wId.trim();
		String url;
		if(wId.matches("\\d{5,15}")){
			url="http://weibo.cn/u/${wId}";
		}else{
			log.info "User id is not formed of numbers. Guess this is a name.";
			url="http://weibo.cn/${wId}";
		}
		return url;
	}
	//for admin LTE template
	def _lte_template={}
	def lte={}
	def lte_blank={}
	def timeline={
		User u=session["user"];
		if(u!=null)
			log.info "Successfully get user ${u}"
		else{
			log.info "User lost in the way..."
			u=request["user"];
			if(u==null)
				log.info "Still get no user";
			else
				log.info "Retrieved user from request...";
		}
		
	}
	def newGraphs={}
	
}
