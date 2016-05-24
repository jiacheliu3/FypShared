package toolkit

import groovy.util.logging.Log4j

@Log4j
class JobLogger {

	//save the log
	static String logBase=PathManager.jobLogTempFolder;//but nothing is actually saved here
	static Map<Long,List> logRepo=new HashMap<>();
	
	//instead of writing log to files, store in memory, need to swap to I/O type when the user size grows
	public static void jobLog(def jobId,String content){
		//initialize if not done
		if(logRepo==null)
			logRepo=new HashMap<>();
		//convert the id if needed
		if(jobId instanceof Long){}
		else{
			jobId=new Long(jobId);
		}
		//format the log
		Date now=new Date();
		String theLog="["+now.format("HH:mm:ss")+"] "+" Job ${jobId}: "+content;
		if(logRepo.containsKey(jobId)){
			List logList=logRepo[jobId];
			if(logList==null){
				logList=new ArrayList<String>();
			}
			logList.add(theLog);
		}else{
			List logList=new ArrayList<String>();
			logList.add(theLog);
			logRepo.put(jobId,logList);
		}
		log.info "Stored log for job ${jobId}";
	}
	//fetch the log for job id given
	public static List getLog(def jobId){
		//convert to long if needed
		if(jobId instanceof Long){}
		else{
			jobId=new Long(jobId);
		}
		def logList=logRepo[jobId];
		if(logList==null){
			log.info "No log found for job ${jobId}";
			return [];
		}else if(logList.size()==0){
			String notice="Process is running, please wait...";
			Date now=new Date();
			String theLog="["+now.format("HH:mm:ss")+"] "+notice;
			return [theLog];
		}
		else{
			log.debug "Log located for job ${jobId}";
			def toReturn=logList.collect();
			logList.clear();
			return toReturn;
		}
	}
}
