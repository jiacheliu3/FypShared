package timeline

import groovy.util.logging.Log4j
import keyword.KeywordsExtractor
import Jama.Matrix
import codebigbrosub.User
import codebigbrosub.Weibo

@Log4j
class TimelineManager {
	static timeSlots=[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11];
	//store the categrized weibo list
	TreeMap<Integer,List> keywordDates;
	//find keywords in a date range
	public Map timelineTopics(List<Weibo> weibos){
		if(keywordDates==null||keywordDates.size==0){
			//categorize the weibo items into keywordDates
			debrief(weibos);
		}
		log.info "Start to extract keywords date by date.";
		def map=[:];
		keywordDates.each{date,things->
			log.debug "The date is ${date}";
			def words=KeywordsExtractor.parallelKeywords(null,things);
			map.put(date,words);
		}
		log.info "Keywords are categorized by keywordDates:\n"+map;
		return map;

	}
	//check on the weibo list
	public debrief(List<Weibo> weibos){
		int valid=0;
		keywordDates=new TreeMap<>();
		//check how many keywordDates the microblogs fall in
		for(Weibo w:weibos){
			Date d=w.createdTime;
			if(d==null){
				log.error "Weibo ${w.toString()} has no date.";
				continue;
			}
			//log.debug "This weibo has a valid date. Continue.";
			valid++;
			//convert the date into a int to make it faster
			int year=d.getYear()+1900;
			if(year<2000){
				log.error "Why the year is before 2000? The date is ${d} with year ${d.getYear()} and converted ${d.getYear()+1900}";
			}else{
				log.debug "For comparison the date is ${d} with year ${d.getYear()} and converted ${d.getYear()+1900}";
			}
			int month=d.getMonth()+1;
			int date=d.getDate();
			int repre=year*100+month;
			if(!keywordDates.containsKey(repre)){
				log.debug "Adding date ${repre}"
				keywordDates.put(repre,[w]);
			}else{
				keywordDates[repre].add(w);
			}
		}
		log.info "The weibos fall in keywordDates "+keywordDates;
		return keywordDates;
	}
	public static go(){
		User u;
		User.withTransaction{
			u=User.find("from User as u where u.weiboName=?",["宇宙最后"]);
		}
		if(u==null){
			log.error "User not found";
		}else{
			List<Weibo> weibos;
			Weibo.withTransaction{
				weibos=Weibo.findAll("from Weibo as w where w.ownerName=?",["宇宙最后"]);
			}
			if(weibos==null||weibos.size()==0){
				log.error "No weibo found";
			}else{
				TimelineManager tm=new TimelineManager();
				def result=tm.timelineTopics(weibos);
				return result;
			}
		}
	}
	public static List convertCells(List<TimelineCell> cells,List rowList, List columnList){
		//convert the list to map
		def rowMap=[:];
		for(int i=0;i<rowList.size();i++){
			rowMap.put(rowList[i],i);
		}
		def colMap=[:];
		for(int i=0;i<columnList.size();i++){
			colMap.put(columnList[i],i);
		}
		//use matrix to ensure every cell has a value
		Matrix zeros=new Matrix(rowList.size(),columnList.size());
		//match the cell values to indices, set the matrix with values
		for(int i=0;i<cells.size();i++){
			def c=cells[i];
			int rowIdx=rowMap[c.row];
			int colIdx=colMap[c.column];
			zeros.set(rowIdx,colIdx,c.value);
		}
		def newCells=[];
		for(int i=0;i<rowList.size();i++){
			for(int j=0;j<columnList.size();j++){
				double v=zeros.get(i,j);
				newCells.add(new TimelineCell(row:i+1,column:j+1,value:v));
			}
		}
		log.info "Finished converting timeline cells to ${newCells.size()} ones.";
		log.debug newCells;
		return newCells;
	}
	public List timelineBlogs(List<Weibo> weibos){
		def spots=[];
		for(Weibo w:weibos){
			Date d=w.createdTime;
			if(d==null){
				log.error "Weibo ${w} has no createdTime.";
				continue;
			}
			int year=d.getYear();
			if(year<=100){
				log.error "Wrong date before year 2000: ${d}";
				continue;
			}
			if(d==null){
				log.error "This weibo has no date.";
				continue;
			}
			log.debug "This weibo has a valid date. Continue.";
			//Convert the date to milliseconds
			int ms=d.getTime();
			spots.add(['value':ms]);
		}
		log.debug "Mapped the dates to spots: ";
		log.debug spots;
		return spots;
	}
	public Map weekTimelineBlogs(List<Weibo> weibos){
		TreeMap<Integer,Map> timeline=new TreeMap<>();
		Calendar c=Calendar.getInstance();
		for(Weibo w:weibos){
			Date d=w.createdTime;
			if(d==null){
				log.error "This weibo has no date.";
				continue;
			}
			//segment timeline by 2 months
			int year=d.getYear()+1900;
			int month=d.getMonth()+1;
			
			//find the day of week
			c.setTime(d);
			int dayOfWeek=c.get(Calendar.DAY_OF_WEEK)-1;//day starts with 1 indicating Sunday, match to 0~6
			
			//7 columns in total
			int yearNmonth=year*100+month;
			if(timeline.containsKey(yearNmonth)){
				def theDay=timeline[yearNmonth];
				if(theDay.containsKey(dayOfWeek)){
					theDay[dayOfWeek]+=1;
				}else{
					theDay.put(dayOfWeek,1);
				}
			}else{
				def theDay=[:];
				theDay.put(dayOfWeek,1);
				timeline.put(yearNmonth,theDay);
			}
		}
		log.info "Put all weibo into time blocks: "+timeline;

		return timeline;
	}
	public Map blockedTimelineBlogs(List<Weibo> weibos){
		TreeMap<Integer,Map> timeline=new TreeMap<>();
		for(Weibo w:weibos){
			Date d=w.createdTime;
			if(d==null){
				log.error "This weibo has no date.";
				continue;
			}
			int year=d.getYear()+1900;
			int month=d.getMonth()+1;
			int hour=d.getHours();//0-23
			//12 columns in total, put 2 adjacent hours in one
			int hourCol=hour/2;
			int yearNmonth=year*100+month;
			if(timeline.containsKey(yearNmonth)){
				def theDay=timeline[yearNmonth];
				if(theDay.containsKey(hourCol)){
					theDay[hourCol]+=1;
				}else{
					theDay.put(hourCol,1);
				}
			}else{
				def theDay=[:];
				theDay.put(hourCol,1);
				timeline.put(yearNmonth,theDay);
			}
		}
		log.info "Put all weibo into time blocks: "+timeline;

		return timeline;

	}
	public Map makePredictions(Map timeblocks){
		//the input format is {201401={10=1, 4=2, 8=1, 5=1, 6=1, 0=1, 9=2},..}
		def mergeMap={large,small->
			small.each{k,v->
				if(large.containsKey(k)){
					large[k]+=v;
				}else{
					large.put(k,v);
				}
			}
		}

		def countMap=[:];
		for(def e:timeblocks){
			def timeMap=e.value;
			log.debug "Time count for ${e.key}: "+timeMap;
			mergeMap(countMap,timeMap);
		}
		log.debug "Aggregated weibo time count in each time period: "+countMap;
		//find the active, inactive and bed time for user
		def minMax=findMinMax(countMap);
		def active=minMax['max'];
		def inactive=minMax['min'];
		def bedTime=findBedTime(countMap);

		//format the result
		def result=["active":active,"inactive":inactive,"bedtime":bedTime];
		return result;
	}
	//find 3 top active time zones
	public Map findMinMax(Map timeCount){
		//sample data format: {11=27, 10=20, 4=5, 8=6, 5=1, 6=3, 0=10, 9=22, 7=4, 2=1} with keys in [0,11]
		if(timeCount==null||timeCount.size()<=1){
			log.error "The timeline slot is empty or is too small: "+timeCount;
			return [:];
		}
		int keepMin;
		int keepMax;

		//decide the size to keep
		if(timeCount.size()>=9){
			keepMin=3;
			keepMax=3;
		}else if(timeCount.size()>=6){
			keepMin=2;
			keepMax=2;
		}else if(timeCount.size()>=2){
			keepMin=1;
			keepMax=1;
		}else{
			log.error "The size is not taken into consideration: "+timeCount;
			return [:];
		}
		def sorted=timeCount.sort { a, b -> b.value <=> a.value };
		def maxi=[:];
		def mini=[:];
		//start to throw things into the maps
		int count=0;
		for(def e:sorted){
			//the keys shall be doubled in order to match 24 hours
			if(count<keepMax){
				maxi.put(e.key*2,e.value);
			}else if(count>=(sorted.size()-keepMin)){
				mini.put(e.key*2,e.value);
			}else{
				//nothing here
			}
			count++;
		}

		def result=["max":maxi,"min":mini];
		log.debug "Found max and min active slots: "+result;
		return result;
	}
	public findBedTime(Map timeCount){
		log.debug "Finding bedtime from count of each time slot:"+timeCount;
		//the method to calculate the support in the next 6 hours
		def next6={supportMap,index->
			int sum=0;
			def toGo=[];
			toGo.add(index);
			//find the following 2 indices of this one
			int next1=index+1;
			if(next1>11)
				next1-=11;
			toGo.add(next1);
			int next2=index+2;
			if(next2>11)
				next2-=11;
			toGo.add(next2);
			//find the support of each
			toGo.each{it->
				int thisHour=timeSlots[it];
				def sup=supportMap[thisHour];
				if(sup==null){}
				else if(!(sup instanceof Integer)){
					log.error "The value from time count is not integer: ${sup}. What's wrong?";
				}
				else{
					sum+=sup;
				}
			}
			return sum;
		}
		//with the sliding window of 6 hours
		def window6=[:];
		for(int i=0;i<timeSlots.size();i++){
			//int time=timeSlots[i];
			int sum6=next6(timeCount,i);
			window6.put(i,sum6);
		}
		//method to find the next slot of time
		def nextSlot={thisHour->
			int nextHour=thisHour+1;
			if(nextHour>11)
				nextHour-=11;
			return nextHour;
		}
		//keep the top 3 inactive periods
		window6=window6.sort {it.value};
		int count=0;
		TreeMap<Integer,Integer> sleeping=new TreeMap<>();//key is bed time beginning and value is end
		for(def e:window6){
			if(count>=3)
				break;
			sleeping.put(e.key,nextSlot(nextSlot(nextSlot(e.key))));
			count++;
		}

		//merge if two slots can be
		int start=sleeping.firstEntry().key;
		if(sleeping.containsKey(nextSlot(start))&&sleeping.containsKey(nextSlot(nextSlot(start)))){
			def endValue=sleeping[nextSlot(nextSlot(start))];
			sleeping.clear();
			sleeping.put(start,endValue);
		}else if(sleeping.containsKey(nextSlot(start))){
			def endValue=sleeping[nextSlot(start)];
			sleeping.clear();
			sleeping.put(start,endValue);
		}else{
			def endValue=sleeping[start];
			sleeping.clear();
			sleeping.put(start,endValue);
		}
		log.debug "Found the possible sleeping time: "+sleeping;

		//format for return
		start=sleeping.firstEntry().key;
		int end=sleeping.firstEntry().value;
		def result=["start":start*2,"end":end*2];
		return result;
	}

	public static void main(String[] args){


	}
}
