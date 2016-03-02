package job

import codebigbrosub.Job

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
			println "Job not found.";
		}
		Job j=jobMap[toFind]
		return j;
	}
}
