package timeline
import groovy.util.logging.Log4j

import java.util.regex.Matcher
import java.util.regex.Pattern

@Log4j
class DateExtractor {
	static Pattern beforeMonth= Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");//2015-01-01 10:05
	static Pattern withinHour=Pattern.compile("^\\d{1,2}\u5206\u949F\u524D");//27分钟前
	static Pattern withinToday=Pattern.compile("^\u4ECA\u5929 \\d{1,2}:\\d{2}");//今天 16:06
	static Pattern withinYesterday=Pattern.compile("^\u6628\u5929 \\d{1,2}:\\d{2}");//昨天 16:06
	static Pattern withinMonth=Pattern.compile("^\\d{1,2}\u6708\\d{1,2}\u65E5 \\d{1,2}:\\d{2}");//02月19日 22:51
	static Pattern hourAndMinute=Pattern.compile("\\d{1,2}:\\d{2}");
	static Pattern digits=Pattern.compile("\\d+");

	public static Date beforeMonthMatch(String raw){
		Matcher m= beforeMonth.matcher(raw);
		Date d;
		if(m.find()){
			String time=m.group();
			d=Date.parse("yyyy-MM-dd HH:mm", time);
			return d;
		}else{
			return null;
		}
	}
	public static Date withinMonthMatch(String raw){
		Matcher m= withinMonth.matcher(raw);
		Date d;
		if(m.find()){
			String time=m.group();
			Matcher timeMatcher=hourAndMinute.matcher(time);
			if(timeMatcher.find()){
				//extract the hour and minute
				String hm=timeMatcher.group();
				String monthAndDate=time.substring(0,timeMatcher.start());
				//make the date
				Date temp=new Date();
				int year=temp.getYear();
				if(year<2000)
					year+=1900;//if <2000 the year must be the years from 1900
				//extract month and date
				Matcher digitMatcher=digits.matcher(monthAndDate);
				def list=[];
				while(digitMatcher.find()){
					list.add(digitMatcher.group());
				}
				//println "Got month and date "+list;
				int month=Integer.parseInt(list[0]);
				int date=Integer.parseInt(list[1]);
				//generate date
				d=Date.parse("yyyy-MM-dd HH:mm", year+"-"+month+"-"+date+" "+hm);
				return d;

			}else{
				log.debug "No hour and minute found from ${time}";
				return null;
			}
		}else{
			log.debug "${raw} doesn't match the date within month";
			return null;
		}
	}
	public static Date withinTodayMatch(String raw){
		Matcher m=withinToday.matcher(raw);
		Date d;
		if(m.find()){
			String time=m.group();
			Matcher timeMatcher=hourAndMinute.matcher(time);
			if(timeMatcher.find()){
				//extract the hour and minute
				String hm=timeMatcher.group();
				String monthAndDate=time.substring(0,timeMatcher.start());
				//make the date
				Date temp=new Date();
				int year=temp.getYear();
				if(year<2000)
					year+=1900;//if <2000 the year must be the years from 1900
				//extract month and date
				int month=temp.getMonth()+1;//Month falls in 0-11 range, different from that of dates.
				int date=temp.getDate();
				//generate date
				d=Date.parse("yyyy-MM-dd HH:mm", year+"-"+month+"-"+date+" "+hm);
				return d;

			}else{
				log.debug "No hour and minute found from ${time}";
				return null;
			}
		}else{
			log.debug "${raw} doesn't match the date within today";
			return null;
		}
	}
	public static Date withinYesterdayMatch(String raw){
		Matcher m=withinYesterday.matcher(raw);
		Date d;
		if(m.find()){
			String time=m.group();
			Matcher timeMatcher=hourAndMinute.matcher(time);
			if(timeMatcher.find()){
				//extract the hour and minute
				String hm=timeMatcher.group();
				String monthAndDate=time.substring(0,timeMatcher.start());
				//make the date
				Date temp=getYesterday();
				int year=temp.getYear();
				if(year<2000)
					year+=1900;//if <2000 the year must be the years from 1900
				//extract month and date
				int month=temp.getMonth()+1;
				int date=temp.getDate();
				//generate date
				d=Date.parse("yyyy-MM-dd HH:mm", year+"-"+month+"-"+date+" "+hm);
				return d;

			}else{
				log.debug "No hour and minute found from ${time}";
				return null;
			}
		}else{
			log.debug "${raw} doesn't match the date within yesterday";
			return null;
		}
	}
	public static Date withinHourMatch(String raw){
		Matcher m=withinHour.matcher(raw);
		Date d;
		if(m.find()){
			String time=m.group();
			//extract minutes back
			Matcher digitMatcher=digits.matcher(raw);
			if(digitMatcher.find()){
				String minString=digitMatcher.group();
				int minutes=Integer.parseInt(minString);
				//make the date
				Date temp=turnbackTime(minutes)
				int year=temp.getYear();
				if(year<2000)
					year+=1900;//if <2000 the year must be the years from 1900
				int month=temp.getMonth()+1;
				int date=temp.getDate();
				int hour=temp.getHours();
				int minute=temp.getMinutes();
				//generate date
				d=Date.parse("yyyy-MM-dd HH:mm", year+"-"+month+"-"+date+" "+hour+":"+minute);
				return d;
			}
			else{
				log.debug "No minute found from ${raw}";
				return null;
			}
		}else{
			log.debug "${raw} doesn't match the date within hour";
			return null;
		}
	}
	public static void test(){
		println "Today is "+(new Date());
		println "Testing date format 2015-01-01 10:05";
		println beforeMonthMatch("2015-01-01 10:05");
		println "Testing date format 02月19日 22:51";
		println withinMonthMatch("02月19日 22:51");
		println "Testing date format 今天 20:44";
		println withinTodayMatch("今天 20:44");
		println "Testing date format 昨天 16:06";
		println withinYesterdayMatch("昨天 16:06");
		println "Testing date format 27分钟前";
		println withinHourMatch("27分钟前");

		//customized date
		println "\nNow testing with real dates";
		println "\nDate: 今天 14:49 来自抢考位";
		println extractDate("今天 14:49 来自抢考位");
		println "\nDate: 今天 10:04 来自皮皮时光机";
		println extractDate("今天 10:04 来自皮皮时光机");
		println "\nDate: 02月21日 18:59 来自iPhone 6s";
		println extractDate("02月21日 18:59 来自iPhone 6s");
	}
	public static Date extractDate(String raw){
		Date d;
		d=beforeMonthMatch(raw);
		if(d!=null){
			log.debug "The date is more than a month from now.";
			return d;
		}
		d=withinMonthMatch(raw);
		if(d!=null){
			log.debug "The date is within a month from now.";
			return d;
		}
		d=withinYesterdayMatch(raw);
		if(d!=null){
			log.debug "The date is within yesterday from now.";
			return d;
		}
		d=withinTodayMatch(raw);
		if(d!=null){
			log.debug "The date is within today from now.";
			return d;
		}
		d=withinHourMatch(raw);
		if(d!=null){
			log.debug "The date is within an hour from now.";
			return d;
		}
		log.debug "${raw} doesn't match any of the date format! Need to check!";
		return null;
	}
	public static Date getYesterday(){
		return new Date(System.currentTimeMillis()-24*60*60*1000);
	}
	//get the date several minutes back
	public static Date turnbackTime(int minutes){
		return new Date(System.currentTimeMillis()-1000*60*minutes);
	}
	public static void main(String[] args){
		test();

	}

}