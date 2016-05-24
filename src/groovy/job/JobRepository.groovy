package job

import groovy.util.logging.Log4j
import codebigbrosub.Job

@Log4j
class JobRepository {

	static Map<Integer,Job> jobMap;
	static int nextId;
	static{
		init();
	}
	public static init(){
		nextId=1;
		jobMap=new HashMap<>();
	}
	public static void saveJob(Job job){
		job.id=nextId;
		jobMap.put(nextId,job);
		nextId++;
	}
	public static Job findJob(def id){
		Integer toFind;
		try{
			if (id instanceof Integer){
				toFind=id;
			}else if(id instanceof String){
				toFind=Integer.parseInt(id);
			}else{
				toFind=(Integer)id;
			}
		}catch(ClassCastException e){
			println "Not able to recognize id "+id.class+" "+id;
		}
		//println "Job to track with id "+toFind;
		if(!jobMap.containsKey(toFind)){
			println "Job ${toFind} not found.";
		}
		Job j=jobMap[toFind]
		return j;
	}
	public static void recordTime(Job job,String taskName,int time){
		if(job==null||job.id==null){
			log.error "Job is empty: ${job}. Cannot record time.";
		}
		log.info "Recording time: Job ${job.id}, task ${taskName}, time ${time}s";
		def timeMap=job.taskTime;
		if(timeMap.containsKey(taskName)){
			//log.error "Why job ${job.id} has already recorded task ${taskName}:${timeMap[taskName]}!";
			timeMap[taskName]=time;//update the time
		}else{
			timeMap.put(taskName,time);
			log.debug "Recorded time for task ${taskName}";
		}
	}
}
