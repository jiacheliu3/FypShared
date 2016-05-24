package prediction

import groovy.util.logging.Log4j
import codebigbrosub.Job
import crawler.UserPageCrawler
import static toolkit.JobLogger.jobLog

@Log4j
class UserPredictor {
	static double mostSure=0.99;
	static Random random=new Random();
	
	Job job;//store the reference for the sake of log
	public UserPredictor(Job job){
		this.job=job;
	}
	//from the user data, get as much solid info as you can
	public Map doWhatYouCan(Map userStat){
		log.debug "Trying to extract info from the raw user stat.";
		jobLog(job.id,"Extracting existing information from what is public of the user.");
		/*
		 * raw format from UserPageCrawler
		 data['basic']=studyBasicInfo(content);
		 data['work']=studyWorkExp(content);
		 data['education']=studyEducation(content);
		 data['other']=studyOtherInfo(content);
		 */
		def result=[:];
		UserPageCrawler userCrawler=new UserPageCrawler();

		def basic=userStat["basic"];
		//gender
		def gender=basic["gender"];
		if(gender==null){}
		else{
			def g=userCrawler.convertGender(gender);
			log.debug "Found user gender: ${g}";
			result.put("gender",g);
		}
		//age
		def age=basic["age"];
		if(age==null){}
		else{
			def thisBir=basic["birthday"];
			def thisAge=userCrawler.convertAge(thisBir);
			if(thisAge!=null){
				log.debug "Found user age: ${thisAge}";
				result.put("age",thisAge);
			}
		}
		//geo
		def geo=basic["location"];
		if(geo==null){}
		else{
			def thisProv=userCrawler.convertLocation(geo);
			if(thisProv!=null){
				result.put("geo",thisProv);
			}
		}

		//auth
		String auth=basic["auth"];
		if(auth==null){}
		else{
			//do sth later
			result.put("auth",auth);
		}


		//work and edu
		def work=userStat["work"];
		if(work==null||work.size()==0){
			log.debug "No work info.";

		}else{
			def converted=[:];
			work.each{corp,period->
				String p=userCrawler.convertWorkPeriod(period);
				converted.put(corp,p);
			}
			result.put("work",converted);
		}
		def edu=userStat["edu"];
		if(edu==null||edu.size()==0){
			log.debug "No edu info";
		}else{
			result.put("edu",edu);
		}
		jobLog(job.id,"Here's what the user has made public: "+result.toString());
		return result;
	}
	public Map predictUser(Map userStat,Map friendStat){
		log.debug "The user stats are: "+userStat;
		//check what are available in the user stat
		def raw=doWhatYouCan(userStat);
		/* Sample friend stat
		 * {
		 * 'gender':gender,'age':formatAge,'geo':formatGeo,'tag':formatTags,'edu':edu,'work':work,'workPeriod':workPeriod,"auth":auth,"intro":intro,"allCount":all.size()
		 * }
		 * */
		jobLog(job.id,"Making predictions on what the user chooses to keep as a secret.");
		log.info "Making predictions.";
		def probs=[:];
		//gender
		if(raw.containsKey("gender")){
			def gender=raw["gender"];
			def genderMap=[:];
			genderMap.put(gender,mostSure);
			probs.put("gender",genderMap);
		}else{
			def genderMap=predictGender(friendStat);
			probs.put("gender",genderMap);
		}
		//age
		if(raw.containsKey("age")){
			def age=raw["age"];
			def ageMap=[:];
			ageMap.put(age,mostSure);
			probs.put("age",ageMap);
		}else{
			def ageMap=predictAge(friendStat);
			probs.put("age",ageMap);
		}
		//geo
		if(raw.containsKey("geo")){
			def geo=raw["geo"];
			def geoMap=[:];
			geoMap.put(geo,mostSure);
			probs.put("geo",geoMap);

		}else{
			def geoMap=predictGeo(friendStat);
			probs.put("geo",geoMap);
		}
		//work
		if(raw.containsKey("work")){
			def work=raw["work"];
			def workMap=[:];
			work.each{place,period->
				workMap.put(place,mostSure);
			}
			probs.put("work",workMap);
		}else{
			def workMap=predictWork(friendStat);
			probs.put("work",workMap);
		}
		//edu
		if(raw.containsKey("edu")){
			def edu=raw["edu"];
			def eduMap=[:];
			edu.each{place,period->
				eduMap.put(place,mostSure);
			}
			probs.put("edu",eduMap);
		}else{
			def eduMap=predictSchool(friendStat);
			probs.put("edu",eduMap);
		}
		log.info "User prediction complete.";
		jobLog(job.id,"Predictions made: "+probs.toString());
		
		return probs;
	}
	/* Fast solutions for now */
	public Map predictGender(Map friendStat){
		def friendGender=friendStat["gender"];
		//return format
		def genderProb=[:];
		if(friendGender==null){
			log.error "Null gender map from friends!";
			return genderProb;
		}

		//based on the assumption that more users befriend more of the same gender
		int male=friendGender["male"];
		int female=friendGender["female"];
		if(male==0&&female==0){
			log.warning "The user has no friends that have exposed their gender. Guess randomly.";
			double r=random.nextDouble();
			if(r>0.5){
				genderProb.put("male",0.5);
			}
			else{
				genderProb.put("female",0.5);
			}
		}
		log.debug "From the user's friends, ${male} are male and ${female} are female.";
		if(male>female){
			double prob=male/(male+female+0.0);
			if(prob>mostSure)
				prob=mostSure;
			genderProb.put("male",prob);
		}else{
			double prob=female/(male+female+0.0);
			if(prob>mostSure)
				prob=mostSure;
			genderProb.put("female",prob);
		}

		return genderProb;
	}
	public Map predictAge(Map friendStat){
		def friendAge=friendStat["age"];
		def ageProb=[:];

		/*
		 * Sample format:
		 * {
		 * "known":[20, 31, 15, 14, 16, 16, 18, 20, 25, 60, 72, 50, 44, 42, 10, 5, 22, 35, 34, 32, 25, 40, 39, 20, 22],"unknown":40
		 * }
		 * */
		if(friendAge==null){
			log.error "No age from friends.";
			return ageProb;
		}
		def knownList=friendAge["known"];
		if(knownList==null||knownList.size()==0){
			log.info "No known friend ages. Guess randomly.";
			/*
			 * From population statistics, http://www.stats.gov.cn/ztjc/zdtjgz/zgrkpc/dlcrkpc/dcrkpcyw/201104/t20110428_69407.htm
			 * */
			double r=random.nextDouble();
			if(r<=0.166){
				ageProb.put("0~14",0.166);
			}else if(r<=(1.0-0.166-0.1326)){
				ageProb.put("15~59",1.0-0.166-0.1326);
			}else{
				ageProb.put(">60",0.1326);
			}
			return ageProb;
		}

		//put the ages in age ranges
		def counts=[:];
		int sum=knownList.size();
		knownList.each{thisAge->
			int ageNum;
			try{
				if(thisAge instanceof String){
					ageNum=Integer.parseInt(thisAge);
				}else{
					ageNum=thisAge;
				}
				Integer slot=ageNum/10;
				if(counts.containsKey(slot)){
					counts[slot]+=1;
				}else{
					counts.put(slot,1);
				}
			}catch(ClassCastException e){
				log.error "Error casting ${thisAge} to number.";
				e.printStackTrace();
			}catch(MissingMethodException e){
				log.error "Missing method exception";
				e.printStackTrace();
			}

		}
		log.debug "Counted the age group of user's friends.";

		//find the most possible one
		Integer max=counts.max {it.value}.key;
		Integer maxCount=counts[max];
		String maxSlot=(max*10)+"~"+(max*10+10);
		log.debug "Most users are from this range: ${maxSlot}";
		double prob=maxCount/sum;
		if(prob>mostSure)
			prob=mostSure;
		ageProb.put(maxSlot,prob);
		return ageProb;

	}
	public Map predictGeo(Map friendStat){
		def geo=friendStat["geo"];
		def geoProb=[:];
		if(geo==null||geo.size()==0){
			log.error "No geographical info from user's friends.";
			return geoProb;
		}

		def known=geo["known"];
		if(known==null||known.size()==0){
			log.debug "No known geo info from friends.";
			geoProb.put("Unknown",0.0);
			return geoProb;
		}
		/*
		 * Sample data of known list:
		 * [["Gansu", 48], ["Qinghai", 47], ["Guangxi", 45], ["Guizhou", 35], ["Chongqing", 34], ["Beijing", 12]]
		 * */
		def maxList=known.max {it[1]};
		String place=maxList[0];
		def count=maxList[1];
		if(count==0){
			log.error "Friend distribution in province sums at number 0!";
			geoProb.put("Unknown",0.0);
			return geoProb;
		}
		def sum=known.sum {it[1]};
		def ratio=(sum+0.0)/count;
		log.debug "Friends from ${place} count for most ratio: ${ratio}";
		geoProb.put(place,ratio);

		return geoProb;
	}
	public Map predictWork(Map friendStat){
		def work=friendStat["work"];
		def workProb=[:];
		if(work==null||work.size()==0){
			log.error "No geographical info from user's friends.";
			return workProb;
		}
//		def known=work["known"];
//		if(known==null||known.size()==0){
//			log.error "Nothing known about friends' work.";
//			workProb=["Unknown":(double)0.0];
//			return workProb;
//		}
		//def result=topInMap(known);
		def result=topInMap(work);
		if(result==null||result.size()==0){
			log.error "Failed to find the max in work distribution.";
			workProb=["Unknown":(double)0];
			return workProb;
		}
		workProb.put(result["max"],result["ratio"]);
		return workProb;

	}
	public Map predictSchool(Map friendStat){
		def edu=friendStat["edu"];
		def eduProb=[:];
		if(edu==null||edu.size()==0){
			log.error "No edu info in friendstat!";
			return eduProb;
		}
//		def known=edu["known"];
//		if(known==null||known.size()==0){
//			log.error "No edu info known.";
//			eduProb=["Unknown":(double)0.0];
//			return eduProb;
//		}
//		def result=topInMap(known);
		def result=topInMap(edu);
		if(result==null||result.size()==0){
			log.error "Failed to find the max in work distribution.";
			eduProb=["Unknown":(double)0.0];
			return eduProb;
		}
		eduProb.put(result["max"],result["ratio"]);
		return eduProb;
	}
	//find the item in map with the most weight and calculate its ratio
	public Map topInMap(Map input){
		//the map value must be in numbers
		double sum=0.0;
		input.each{ sum+=it.value; }
		if(sum==0.0){
			log.error "This map accumulates to 0!"+input;
			return [:];
		}
		String max=input.max {it.value}.key;
		def maxCount=input[max];
		if(max==null||maxCount==null){
			log.error "Failed to find the max item! key: ${max}, value: ${maxCount}";
			return [:];
		}
		double ratio=maxCount/sum;
		def result=["max":max,"ratio":ratio];
		return result;
	}
	public static void main(String[] args){

	}
}
