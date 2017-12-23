package codebigbrosub

import groovy.util.logging.Log4j
import job.JobRepository
import crawler.UserGenerator
import crawler.WeiboCrawlerMaster

@Log4j
class JobController {

	static scaffolding=true;
	public static Job trackJob(String jobId){
		//Long l=Long.parseLong(jobId);
		Job job;

		//job=Job.find("from Job as j where j.id=:id",[id:l]);
		job=JobRepository.findJob(jobId);

		//		Job job=Job.findById(l);
		if(job==null){
			log.info "Job is not found. Need to check the error.";
			return;
		}
		if(job.userName==null){
			log.info "User name is null";
		}
		if(job.userName=="empty"){
			log.info "User name is not initialized properly!";
		}
		return job;
	}
	public static Job initiateJob(String name){
		Job job=new Job(name);

		//job.save(flush:true);
		JobRepository.saveJob(job);
		//		if(job.hasErrors())
		//			log.info job.errors;
		log.info "New job initiated "+job.id+": "+job.userName;
		return job;
	}

	public static jobMaster(def session,Job job,String option){
		def data;
		if(isValid(job)){
			switch(option){
				case "crawl":
					data=job.crawlSlave();
					log.debug "Data got from crawlSlave: "+data;
					session.setAttribute("crawler",data);
					break;
				case "keyword":
					data=job.keywordSlave();
					log.debug "Data got from keywordSlave: "+data;
					session.setAttribute("keywords",data);
					break;
				case "stats":
					data=job.statSlave();
					log.debug "Data got from timelineSlave: ":data;
					session.setAttribute("stats",data);
					break;
				//				case "relation":
				//					data = job.relationSlave();
				//					log.debug "Data got from relationSlave: "+data;
				//					session.setAttribute("relations",data);
				//					break;
				//				case "network":
				//					data=job.networkSlave();
				//					log.debug "Data got from networkSlave: "+data;
				//					session.setAttribute('network',data);
				//					break;
				case "interaction":
					data=job.interactionSlave();
					log.debug "Data got from interactionSlave: ":data;
					session.setAttribute("interactions",data);
					break;
				case "cluster":
					data=job.clusterSlave();
					log.debug "Data got from clusterSlave: "+data;
					session.setAttribute("clusters",data);
					break;
				case "timeline":
					data=job.timelineSlave();
					log.debug "Data got from timelineSlave: ":data;
					session.setAttribute("timeline",data);
					break;
				case "deepCrawl":
					data=job.deepCrawlSlave();
					log.debug "Data got from deepCrawlSlave: "+data;
					session.setAttribute("deepCrawl",data);
					break;
				default:
					log.error "No job matching. Job trigger failed.";
					break;
			}
			log.info "Finished ${option} task of "+job;
			return data;
		}
		else{
			log.error option+" job failed. This job is not valid.";
			return;
		}
	}
	public static prepareUserAndJob(String url,def session){
		//validate session
		String wId=session["wId"];
		String wUrl=session["wUrl"];
		log.debug "Prepare user with id:${wId},url:${wUrl}";
		log.debug "See if session overlaps or corrupts.";
		log.debug session;

		//get the url and crawl it
		def data=[:];
		WeiboCrawlerMaster crawlerMaster=new WeiboCrawlerMaster();
		data=crawlerMaster.crawlUser(url,data);
		log.info("Crawler master returned data "+data);

		//generate the user
		User u=UserGenerator.generateUser(data);
		session["user"]=u;

		//create new job
		Job job=initiateJob(u.weiboName);
		session["jobId"]=job.id;
		log.info "Stored job in session "+job;
		session["running"]=job.id;

		//perform preparation step crawling
		def d=job.newUserCrawlSlave();

		data.putAll(d);

		data["knownCount"]=0;
		session.setAttribute("crawler",data);



		return data;
	}
	public static boolean isValid(Job job){
		//get the user
		String name=job?.getUserName();
		if(name==null||name==""){
			log.info "Cannot find user name from job.";
			return false;
		}else{
			log.info "Got username "+name;
		}
		User u=User.find("from User as u where u.weiboName=?", [name]);
		if(u==null){
			log.error "Cannot find user from job.";
			return false;
		}
		return true;
	}
}
