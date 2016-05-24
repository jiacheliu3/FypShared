package prediction

import java.util.Map;

import groovy.util.logging.Log4j
import codebigbrosub.Job
import static toolkit.JobLogger.jobLog

@Log4j
class FinalReportGenerator {
	Job job;
	public FinalReportGenerator(Job job){
		this.job=job;//for logging
	}
	//use whatever information is stored in the session to generate a final report for user
	public overview(def session){
		//get data from session
		def keywords=session["keywords"];
		if(keywords==null){
			log.error "No keyword information stored.";
			return;
		}
		def stat=session["stats"];
		if(stat==null){
			log.error "No user stat stored.";
			return;
		}
		def interaction=session["interactions"];
		if(interaction==null){
			log.error "No interaction data stored.";
			return;
		}
		def timeline=session["timeline"];
		if(timeline==null){
			log.error "No timeline information stored.";
			return;
		}

		def timePred=timeline["prediction"];
		def timeCount=timeline["blocks"];
		def classPred=keywords["classification"]["total"];
		def userPred=stat["prediction"];
		def friendStat=stat["friends"];
		def support=interaction["interaction"]["support"];
		log.debug "Extracted all the necessary information from session. Start report generation.";
		jobLog(job.id,"Start the final evaluation based on the user's overall information.");

		//generate each field of radar graph within [0,1]
		double friendAllGender=evalFriendAndGender(userPred,friendStat);
		double earlyBird=evalBedTime(timePred);
		double active=evalActiveness(timeCount);
		double multitopic=evalTopics(classPred);
		double interactivity=evalInteraction(support);
		//the size of these 3 must match
		def dataList=[friendAllGender, earlyBird, active, multitopic, interactivity];
		def nameList=["Cross-gender interactivity", "Sleep healthily", "Posting a lot", "Variety in interests", "Active among friends"];
		def introList=["The more friends of the other gender has interacted with this user, the higher this score will be.", "This score indicates the healthiness of this user's daily schedule.", "The more active this user is in posting microblogs, the higher this score will be.", "This score shows if the user is interested in various topics in microblogs.", "This score indicates how much this user interacts with his or her friends."];
		//put in format for radar map
		def radar=formatRadarMap(nameList,dataList,introList);
		jobLog(job.id,"Several aspects of user are evaluated and rated to make a radar graph.");

		//predictions in list
		def habits=evalPostingHabits(timePred);
		def occupation=evalOccupation(userPred,timePred);
		def buddies=evalCloseFriends(support);
		def websites=evalInterestedWebsites(classPred);
		def words=evalInterestedKeywords(classPred);

		//put in format for text map
		def textReport=["occupation":occupation,"habits":habits,"words":words,"websites":websites];
		jobLog(job.id,"A text report is generated: "+textReport.toString());

		//final result
		def result=["radar":radar,"text":textReport];
		return result;
	}
	//find the most likely close friends
	public Collection<String> evalCloseFriends(Map support){
		//get weibo count
		int allCount=support["total"];
		//get the total number of replies the user has got
		def commented=support["commented"];
		def liked=support["liked"];
		def reposted=support["forwarded"];
		def mentioned=support["mentioned"];

		def commenting=support["commenting"];
		def liking=support["liking"];
		def reposting=support["forwarding"];
		def mentioning=support["mentioning"];

		//merge the supports
		def inMap=[:];
		mergeMap(inMap,commented);
		mergeMap(inMap,reposted);
		mergeMap(inMap,liked);
		mergeMap(inMap,mentioned);

		def outMap=[:];
		mergeMap(outMap,commenting);
		mergeMap(outMap,reposting);
		mergeMap(outMap,liking);
		mergeMap(outMap,mentioning);

		def totalMap=[:];
		mergeMap(totalMap,inMap);
		mergeMap(totalMap,outMap);
		totalMap=totalMap.sort { a, b -> b.value <=> a.value };

		//find the top
		Set<String> buddies=new HashSet<>();
		int toKeep=10;
		def inSet=inMap.keySet();
		def outSet=outMap.keySet();
		def inter=inSet.intersect(outSet);
		log.debug "Found ${inter.size()} friends that have a double sided relationship with target user: "+inter;
		
		def interMap=[:];
		for(String s:inter){
			//find the total support
			int total=totalMap[s];
			interMap.put(s,total);
		}
		interMap=interMap.sort { a, b -> b.value <=> a.value };
		for(def e:interMap){
			if(buddies.size()<toKeep){
				buddies.add(e.key);
			}
		}
		/* deprecated */
//		for(def e:totalMap){
//			String name=e.key;
//			if(buddies.size()>=toKeep){
//				log.debug "Found ${toKeep} close friends.";
//				break;
//			}
//			if(outMap.containsKey(name)&&inMap.containsKey(name))
//				buddies.add(name);
//		}
		log.info "Found the close friends that have the most interaction with the target user: "+buddies;
		return buddies;
	}

	//find the keywords that the user might be interested in by the predicted topics
	public Collection<String> evalInterestedKeywords(Map classPred){
		def total=classPred;
		Set<String> finalWords=new HashSet<>();
		//keep only first 3
		int tokeep=3;
		int cnt=0;
		for(def e:total){
			if(cnt>=tokeep)
				break;
			String type=e.key;
			def words=SavedModelMatcher.keywordsFromType(type);
			finalWords.addAll(words);
			cnt++;
		}
		log.info "Total words that the user might be interested in: "+finalWords;
		return finalWords;
	}
	//find the websites that the user might be interested in by the predicted topics
	public Collection<String> evalInterestedWebsites(Map classPred){
		def total=classPred;
		Set<String> finalSites=new HashSet<>();
		//the first 2 types have 2 sites each and the following 2 have 1 each
		int cnt=0;
		for(def e:total){
			String type=e.key;
			if(cnt<2){
				def sites=SavedModelMatcher.websitesFromType(type,1);
				finalSites.addAll(sites);
			}
			else if(cnt<4){
				def sites=SavedModelMatcher.websitesFromType(type,1);
				finalSites.addAll(sites);
			}
			else
				break;

		}
		log.info "Total websites that the user might be interested in: "+finalSites;
		return finalSites;
	}
	public Collection<String> evalPostingHabits(Map timePred){
		//calculate the user's sleep time
		def bedtime=timePred["bedtime"];
		def start=bedtime["start"];
		def end=bedtime["end"];
		int duration;
		if(start==null||end==null){
			log.error "Null bedtime: "+bedtime;
			duration=8;
		}
		else if(end<start)
			duration=end+24-start;
		else
			duration=end-start;
		log.debug "The bedtime is: ${start}~${end} ${duration} hours";
		//each of them is a map
		def active=timePred["active"];
		def inactive=timePred["inactive"];
		log.debug "The active period is: "+active;
		log.debug "The inactive period is: "+inactive;
		//make prediction
		Set<String> pred=new HashSet<>();
		//guess the reason why the user is active at each slot
		if(active==null){
			log.error "User's active time slot is empty!";

		}else{
			active.each{time,support->
				switch(time){
					case 11:case 12:case 13:
						pred.add("The user is active at time before or after lunch. Try eating more with friends might help.");
						break;
					case 14: case 15: case 16:
						pred.add("The user is active in the afternoon when the mood is low and head is drowsy.");
						break;
					case 17: case 18:case 19:
						pred.add("The user is active at time before or after dinner.");
						break;
					case 20: case 21: case 22:
						pred.add("The user is active during evening prime time. Does he/she watch TV play?");
						break;
					case 23: case 24: case 0:
						pred.add("The user is active around midnight.");
						break;
					case 1:case 2: case 3:
						pred.add("The user is active deep in the night, seizing the precious time foraging before dawn.");
						break;
					case 4:case 5:
						pred.add("The user is active before dawn. There are many possible reasons why he/she is still up, but neither of them can be a good life style.");
						break;
					case 6:case 7:case 8:
						pred.add("The user is an early bird. How's breakfast?");
						break;
					case 9:case 10:
						pred.add("The user is active in the middle of the morning. Is it because his/her work starts, or never starts?");
						break;
					default:
						log.error "This time is unknown: "+time;
						break;

				}
			}
		}
		//guess why the user is not active in a period
		if(inactive==null){
			log.error "The user's inactive time slot is empty!";

		}else{
			inactive.each{time,support->
				//if the user is predicted to be asleep at that time, ignore the reason
				if(time>=0&&time>=start&&time<=end){
					//0<start<end
					log.debug "The user is inactive at ${time} because it's between bedtime: ${start}~${end}";
				}else if((time<=24&&time>=start)||(time>=0&&time<=end)){
					//start<24, 0<end
					log.debug "The user is inactive at ${time} because it's between bedtime: ${start}~${end}";
				}else{
					//the user is sober
					switch(time){
						case 11:case 12:case 13:
							pred.add("The user is inactive around the lunch time. Is it because he/she has no lunch breaks?");
							break;
						case 14: case 15: case 16:
							pred.add("The user is inactive in the afternoon when everybody's drowsy. The user must drink coffee or take naps. ");
							break;
						case 17: case 18:case 19:
							pred.add("The user is inactive at time before or after dinner. There must be something more interesting than microblogs.");
							break;
						case 20: case 21: case 22:
							pred.add("The user is inactive during the evening prime time. He/she must be very focused on the TV or PC screen.");
							break;
						case 23: case 24: case 0:
							pred.add("The user is inactive around midnight. The user must be not on him/herself.");
							break;
						case 1:case 2: case 3:
							pred.add("The user is inactive deep in the night but not asleep.");
							break;
						case 4:case 5:
							pred.add("The user is inactive before dawn. He/she must be either too wide awake or worn out for microblogs.");
							break;
						case 6:case 7:case 8:
							pred.add("The user is up at an early hour but no action in microblogs. Does he/she drive?");
							break;
						case 9:case 10:
							pred.add("The user is inactive in the morning. It's good to concentrate when he/she is completely sober. Or is he/she not?");
							break;
						default:
							log.error "This time is unknown: "+time;
							break;
					}
				}
			}
		}
		//format the active/inactive time
		Set<String> activeSet=new HashSet<>();
		activeSet.addAll(active.keySet());
		active.each{k,v->
			activeSet.add(k+1);
		}
		Set<String> inactiveSet=new HashSet<>();
		inactiveSet.addAll(inactive.keySet());
		inactive.each{k,v->
			inactiveSet.add(k+1);
		}
		//guess the correlation between active/inactive period with bedtime
		if(activeSet.contains(start)){
			pred.add("The user likes to post microblogs right after he/she wakes up.");
		}
		else if(inactiveSet.contains(start)){
			pred.add("The user does not like to post microblogs right after he/she wakes up.");
		}
		if(activeSet.contains(end)){
			pred.add("The user likes to post microblogs just before the bed time.");
		}
		else if(inactiveSet.contains(end)){
			pred.add("The user does not like to post microblogs just before the bed time.");
		}
		return pred;
	}
	//evaluate the user's occupation
	public Collection<String> evalOccupation(Map userPred,Map timePred){
		//get the predicted user age slot
		Map ageThing=userPred["age"];
		log.debug "Got user age prediction: "+ageThing;
		String ageName;
		if(ageThing==null){
			log.error "User has no age prediction: "+userPred;
			ageName='unknown';
		}else{
			//try to get the age by the 1st char
			if(ageThing.size()==0){
				log.error "No age for user: "+userPred;
				ageName='unknown';
			}
			else if(ageThing.size()!=1){
				log.error "More than 1 age for user?!";
				ageName='unknown';
			}else{
				for(def e:ageThing){
					ageName=e.key;
				}
				if(ageName==null||ageName=="")
					ageName='unknown';
			}
		}
		//calculate the user's sleep time
		def bedtime=timePred["bedtime"];
		def start=bedtime["start"];//what if null
		def end=bedtime["end"];
		int duration;
		if(start==null||end==null){
			duration=8;//assume the user sleeps 8 hours if not known otherwise
		}
		else if(end<start)
			duration=end+24-start;
		else
			duration=end-start;
		//predict the occupation based on user age and sleep time
		Set<String> pred=new HashSet<>();
		switch(ageName){
			case '0~14':case '0~10':
				pred+="The user should be a student based on the age.";
				if(end>8){
					pred.add(" But why is his/her predicted getting up time later than 8 in the morning?");
				}
				break;
			case '>60':
				pred.add("The user is very likely retired already. Here are my due respects if it's not the case.");
				break;
			case '15~59':case '10~20':case '20~30':case '30~40':case '40~50':case '50~60':
				if(duration<6){
					pred.add("Either the user's occupation can afford his/her having a flexible schedule or he/she is simply too busy to sleep.");
				}
				if(end>=8&&end<=12){
					pred.add("It seems the user's occupation doesn't require him/her to get up early.");
				}else if(end>12){
					pred.add("Either the user never works or never looks at his/her weibo in the morning.");
				}else{
					pred.add("The user get up before 8 so it can be a routine and regular occupation.");
				}
				break;
			default:
				pred.add("The prediction cannot be made without the user's age.");
				break;

		}
		return pred;
	}
	//format radar related data
	public List formatRadarMap(List names,List data,List intro){
		//check if size matches between data and introduction
		if(names==null){
			log.error "No name for each radar angle!";
			return [];
		}
		if(data==null){
			log.error "Radar data is null!";
			return [];
		}
		if(intro==null){
			log.error "No introduction for each radar angle!";
			return [];
		}
		if(data.size()!=names.size()){
			log.error "The size of radar data does not match with that of names. Data:${data.size()}, Names:${names.size()}";
			return [];
		}
		//format the values
		def things=[];
		for(int i=0;i<data.size();i++){
			def name=names[i];
			def score=data[i];
			def word=intro[i];
			def map=["axis":name,"value":score,"intro":word];
			things.add(map);
		}
		return things;
	}
	public double evalFriendAndGender(Map userPred,Map friendStat){
		//get the user's gender
		def genderMap=userPred["gender"];
		String gender;
		if(genderMap.containsKey('male')){
			gender='male';
		}else if(genderMap.containsKey('female')){
			gender='female';
		}else{
			log.error "Cannot find gender from user stat: "+genderMap;
			gender='female';
		}
		//get the friends stat
		def all=friendStat["allCount"]?.toString();
		int allCount;
		if(all==null){
			log.error "The all count is wrong: "+all;
			allCount=100;
		}else if(all instanceof String){
			allCount=Integer.parseInt(all);
		}else{
			allCount=all;
		}
		def friendGender=friendStat["genderMap"];
		def diffGender;
		int diffCount=0;
		if(gender=='male'){
			diffGender=friendGender['female'];
		}else{
			diffGender=friendGender['male'];
		}
		diffCount=diffGender.size();
		//the number of diff gender friends contribute to the score
		double baseScore=1.0;
		if(diffCount<50)
			baseScore=1-0.02*(50-diffCount);
		//calculate the ratio
		log.debug "${diffCount} friends are of different gender from the user.";
		double ratio=baseScore*(diffCount+0.0)/allCount;
		if(ratio>1.0)
			ratio=1.0;
		log.debug "Gender score is: "+ratio;
		return ratio;

	}
	public double evalBedTime(Map timePred){
		def bedtime=timePred['bedtime'];
		int start=bedtime["start"];
		int end=bedtime['end'];

		//calculate the duration of sleep
		if(end<start)
			end=end+24;
		int length=end-start;
		double durationScore=Math.abs(length-8)*0.1*(-1);

		//calculate the score on bedtime
		int nightnight=start;
		if(nightnight<10)
			nightnight+=24;
		int diff=Math.abs(nightnight-22);
		double timeScore=-0.1*diff;

		double finalScore=1.0+durationScore+timeScore;
		log.debug "The final score based on the use's bedtime is: "+finalScore;
		return finalScore;
	}
	//give activeness based on average weibo count per month
	public double evalActiveness(Map timeCount){
		//sum up the weibo count under each time
		TreeMap<Integer,Integer> timeline=new TreeMap<>(Collections.reverseOrder());
		timeCount.each{k,map->
			int sum=0;
			map.each{key,value->
				sum+=value;
			}
			timeline.put(k,sum);
		}
		//keep the last 3 months and calculate activeness
		int allICare=3;
		int allSum;
		int index=0;
		for(def e:timeline){
			if(index>=allICare)
				break;
			int thisMonth=e.value;
			allSum+=thisMonth;
			index++;
		}
		log.debug "${allSum} weibos in the last ${allICare} months";

		double score=(double)allSum/60;
		log.debug "The activeness score is: "+score;
		return score;
	}
	public double evalTopics(Map classPred){
		//normalize the score for each topic
		int max=100;

		def normalized=[:];
		double maxScore=classPred.max {it.value}.value;
		log.debug "The largest score among the topics is ${maxScore}";

		classPred.each{topic,score->
			double inRange=score/maxScore*max;
			normalized.put(topic,inRange);
		}
		//count the number of topics with score>50
		int count=0;
		normalized.each{k,v->
			if(v>50)
				count++;
		}
		double score=(count+0.0)/6;
		log.debug "${count} topics earned interest of more than 50% and the final score is ${score}";
		return score;
	}
	public double evalInteraction(Map support){
		//get weibo count
		int allCount=support["total"];
		//get the total number of replies the user has got
		def commented=support["commented"];
		def liked=support["liked"];
		def reposted=support["forwarded"];
		def accumuMap={map->
			int sum=0;
			map.each{k,v->
				sum+=v;
			}
			return sum;
		}
		int total=accumuMap(commented)+accumuMap(liked)+accumuMap(reposted);
		log.debug "Accumulated total responses in user's weibo.";

		//if every weibo can get 2 replies the score is 1
		double score=(total+0.0)/(2*allCount);
		if(score>1.0)
			score=1.0;
		log.debug "The final interactivity score is ${score}";
		return score;
	}
	//helper function
	public void mergeMap(Map largeMap,Map smallMap){
		if(smallMap.size()==0){
			return;
		}else{
			smallMap.each{k,v->
				//ensure value is number
				def value=ensureNumber(v);
				if(largeMap.containsKey(k)){
					largeMap[k]+=value;
				}else{
					largeMap.put(k,value);
				}

			}
		}
	}
	public ensureNumber(def v){
		def value;
		if(v instanceof Integer){
			value=v;
		}else if(v instanceof Double){
			value=v;
		}else if(v instanceof String){
			if(v.contains('.')){
				try{
					value=Double.parseDouble(v);
				}catch(ClassCastException e){
					log.error "Cannot cast value ${v}(${v.class}) to Double!";
					value=null;
				}
			}else{
				try{
					value=Integer.parseInt(v);

				}catch(ClassCastException e){
					log.error "Cannot cast value ${v}(${v.class}) to Integer!";
					value=null;
				}
			}
		}
		return value;
	}
}
