package job

import groovy.time.TimeCategory
import groovy.util.logging.Log4j
import codebigbrosub.Job
import codebigbrosub.Task
import codebigbrosub.Weibo

@Log4j
class TaskMaster {
	static Map<String,Double> timeEstimations=new HashMap<>();
	static Map<String,Double> percentageEstimations=new HashMap<>();
	static List<String> taskNames=['keyword','stat','interaction','cluster','timeline'];//compulsory fields that the time fields must have
	public static int timeDifference(Date start,Date end){
		int diff;
		use(TimeCategory) {
			def duration = end - start;
			//calculate the difference between the two dates
			diff=duration.seconds+duration.minutes*60+duration.hours*3600+duration.days*86400;

		}
		println "The difference between date ${start} and ${end} is ${diff} seconds.";
		return diff;
	}
	//record a new task based on the job passed to it
	public static boolean saveJobTime(Job job){
		if(job==null){
			log.debug "Empty job. No need to save.";
			return false;
		}
		if(job.taskTime==null||job.taskTime.size()==0){
			log.debug "The job has no time recorded. No need to save.";
			return false;
		}
		boolean saved=false;
		try{
			Task t=new Task();
			def timeMap=job.taskTime;
			t.preCrawlTime=timeMap["preCrawl"];
			t.keywordTime=timeMap["keyword"];
			t.statTime=timeMap["stat"];
			t.interactionTime=timeMap["interaction"];
			t.clusterTime=timeMap["cluster"];
			t.timelineTime=timeMap["timeline"];
			t.deepCrawlTime=timeMap["deepCrawl"];

			//get the weibo count of job
			String userName=job.userName;
			int weiboCount=job.weiboCount;
			if(weiboCount==null||weiboCount==0){
				log.info "No weibo count recored for job. I'll find out myself.";
				if(userName==null||userName==""){
					log.error "No user name for this job!";
				}else{
					Weibo.withTransaction{
						def knownWeibo=Weibo.findAll("from Weibo as w where w.ownerName=?",[userName]);
						weiboCount=knownWeibo?.size();
						log.debug "The job is based on ${weiboCount} items.";
						t.taskSize=weiboCount;
					}
				}
			}
			Task.withTransaction{
				t.save(flush:true);
				if(t.hasErrors()){
					log.error "Error saving task: "+t.errors;
					saved=false;
				}else{
					log.debug "Task saved.";
					saved=true;
				}
			}
		}catch(Exception e){
			log.error "An exception occurred saving the task.";
			e.printStackTrace();
			saved=false;
		}
		return saved;
	}
	//initialize with calculation average estimation for the jobs
	public static void calculateTime(){
		def tasks;
		Task.withTransaction{
			tasks=Task.list([max: 20,offset:0,sort: "id",order: "desc"]);
		}
		//calculate the average time for each job
		def putInMap={map,field,value->
			if(value==null||value==0){
				//nothing
			}else{
				if(map.containsKey(field)){
					map[field].add(value);
				}else{
					def list=[];
					list.add(value);
					map.put(field,list);
				}
			}
		}
		//the map stores the time per unit of weibo item
		def timeMap=[:];
		tasks.each{task->
			//get the weibo count of each task
			int siz=task.taskSize;
			if(siz==null||siz==0)
				siz=100;

			//putInMap(timeMap,"preCrawl",task.preCrawlTime/siz);
			putInMap(timeMap,"keyword",task.keywordTime/siz);
			putInMap(timeMap,"stat",task.statTime/siz);
			putInMap(timeMap,"interaction",task.interactionTime/siz);
			putInMap(timeMap,"cluster",task.clusterTime/siz);
			putInMap(timeMap,"timeline",task.timelineTime/siz);
			//putInMap(timeMap,"deepCrawl",task.deepCrawlTime);//deep crawling time is not included
		}
		log.debug "Aggregated all task times in map: "+timeMap;
		timeMap.each{taskName,timeList->
			double avg=(double)timeList.sum()/timeList.size();
			if(avg==0){
				log.error "No ${taskName} time recorded yet. Set to 1s.";
				avg=1;
			}
			timeEstimations.put(taskName,avg);
		}
		log.debug "Calculated average job time map: "+timeEstimations;
		//normalize the time to percentages of each job
		double sum=0;
		timeEstimations.each{ sum+=it.value; }
		if(sum==0){
			log.error "The sum of time expectations is 0!";
			timeEstimations.each{
				percentageEstimations.put(it.key,((double)it.value)/timeEstimations.size());
			}
		}else{
			timeEstimations.each{
				percentageEstimations.put(it.key,it.value/sum)
			}
		}
		log.debug "Calculated percentage of each job: "+percentageEstimations;
		//check all compulsory fields
		taskNames.each{taskName->
			if(!(timeEstimations.containsKey(taskName))){
				log.error "No field ${taskName} in time estimations. Put an arbitrary value.";
				timeEstimations.put(taskName,1);
			}
			if(!(percentageEstimations.containsKey(taskName))){
				log.error "No field ${taskName} in percentage estimations. What's wrong!";
				percentageEstimations.put(taskName,0.2);
				
			}
		}
		
		return;
	}
	public static Map getEstimation(Job job){
		log.debug "Estimating the time based on job scale.";
		if(timeEstimations==null||timeEstimations.size()==0){
			log.debug "Estimations are not initialized yet.";
			calculateTime();
		}
		//get the scale of this job
		int scale=job.weiboCount;
		if(scale==null||scale==0){
			scale=100;
		}
		
		def result=[:];
		timeEstimations.each{k,v->
			result.put(k,v*scale);	
		}
		
		
		log.debug "Estimated time on job scale: "+result;
		return result;
	}
	//calculate the expected finish time of each job
	public static Map estimate(Job job){
		if(job==null){
			return;
		}
		//initiate if not yet done
		if(timeEstimations.size()==0){
			log.debug "Estimations are not initialized yet.";
			calculateTime();
		}
		//get the scale of the job, weiboCount should be calculated after pre-crawl
		int scale=job.weiboCount;
		if(scale==null||scale==0){
			scale=100;
		}
		//calculate percentage for each job
		double allPerUnit=timeEstimations["timeline"]+timeEstimations["cluster"]+timeEstimations["interaction"]+timeEstimations["stat"]+timeEstimations["keyword"];
		double allTotal=allPerUnit*scale;
		//log.info "Estimated ${allPerUnit} seconds per unit. ${allTotal} seconds in total.";
		
		double interval=10.0;//refresh every 10s
		def result=[:];
		double current=job.currentPercent;
		double next;
		double noMoreThan;
		if(job.timelineComplete){
			next=1.0;
			noMoreThan=1.0;
		}else if(job.clusterComplete){//in process of timeline study
			noMoreThan=0.99;
			double willTake=timeEstimations["timeline"]*scale;
			next=current+interval/willTake;
			if(next>=noMoreThan)
				next=noMoreThan;
		}else if(job.interactionComplete){// in process of cluster study
			noMoreThan=0.99-percentageEstimations["timeline"];
			double willTake=timeEstimations["cluster"]*scale;
			next=current+interval/willTake;
			if(next>=noMoreThan)
				next=noMoreThan;
		}else if(job.statComplete){//in process of interaction study
			noMoreThan=0.99-percentageEstimations["cluster"]-percentageEstimations["timeline"];
			double willTake=timeEstimations["interaction"]*scale;
			next=current+interval/willTake;
			if(next>=noMoreThan)
				next=noMoreThan;
		}else if(job.keywordsComplete){// in process of stat study
			noMoreThan=0.99-percentageEstimations["cluster"]-percentageEstimations["timeline"]-percentageEstimations["interaction"];
			double willTake=timeEstimations["stat"]*scale;
			next=current+interval/willTake;
			if(next>=noMoreThan)
				next=noMoreThan;
		}else if(job.userCrawlComplete){//in process of keyword study
			noMoreThan=0.99-percentageEstimations["cluster"]-percentageEstimations["timeline"]-percentageEstimations["interaction"]-percentageEstimations["stat"];
			double willTake=timeEstimations["keyword"]*scale;
			next=current+interval/willTake;
			if(next>=noMoreThan)
				next=noMoreThan;
		}else{//in pre-crawling
//			noMoreThan=0.99999-percentageEstimations["cluster"]-percentageEstimations["timeline"]-percentageEstimations["interaction"]-percentageEstimations["stat"]-percentageEstimations["keyword"];
//			double willTake=timeEstimations["preCrawl"];
//			next=current+interval/willTake;

			double noLessThan=0.01;
//			if(next>=noMoreThan)
//				next=noMoreThan;
//			if(next<noLessThan)
//				next=noLessThan;
			next=noLessThan;
		}
		//put in return format
		result.put("current",current);
		result.put("next",next);
		job.currentPercent=next;
		log.debug "Job has moved one step forward to ${next}";

		return result;
	}
	public static void main(String[] args){
		//test convert time duration to second
		Date date1=new Date().parse('yyyy/MM/dd', '2016/03/09')
		//sleep(2000);
		Date date2=new Date();
		int diff=timeDifference(date1,date2);

	}
}
