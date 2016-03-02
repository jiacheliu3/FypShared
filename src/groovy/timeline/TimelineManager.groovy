package timeline

import keyword.KeywordsExtractor
import Jama.Matrix
import codebigbrosub.User
import codebigbrosub.Weibo

class TimelineManager {
	//store the categrized weibo list
	TreeMap<Integer,List> keywordDates;

	public Map timelineTopics(List<Weibo> weibos){
		if(keywordDates==null||keywordDates.size==0){
			//categorize the weibo items into keywordDates
			debrief(weibos);
		}
		println "Start to extract keywords date by date.";
		def map=[:];
		keywordDates.each{date,things->
			println "The date is ${date}";
			def words=KeywordsExtractor.parallelKeywords(null,things);
			map.put(date,words);
		}
		println "Keywords are categorized by keywordDates:\n"+map;
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
				println "This weibo has no date.";
				continue;
			}
			println "This weibo has a valid date. Continue.";
			valid++;
			//convert the date into a int to make it faster
			int year=d.getYear()+1900;
			if(year<2000){
				println "Why the year is before 2000? The date is ${d} with year ${d.getYear()} and converted ${d.getYear()+1900}";
			}else{
				println "For comparison the date is ${d} with year ${d.getYear()} and converted ${d.getYear()+1900}";
			}
			int month=d.getMonth()+1;
			int date=d.getDate();
			int repre=year*100+month;
			if(!keywordDates.containsKey(repre)){
				println "Adding date ${repre}"
				keywordDates.put(repre,[w]);
			}else{
				keywordDates[repre].add(w);
			}
		}
		println "The weibos fall in keywordDates "+keywordDates;
		return keywordDates;
	}
	public static go(){
		User u;
		User.withTransaction{
			u=User.find("from User as u where u.weiboName=?",["宇宙最后"]);
		}
		if(u==null){
			println "User not found";
		}else{
			List<Weibo> weibos;
			Weibo.withTransaction{
				weibos=Weibo.findAll("from Weibo as w where w.ownerName=?",["宇宙最后"]);
			}
			if(weibos==null||weibos.size()==0){
				println "No weibo found";
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
		println "Finished converting timeline cells to ${newCells.size()} ones.";
		println newCells;
		return newCells;
	}
	public List timelineBlogs(List<Weibo> weibos){
		def spots=[];
		for(Weibo w:weibos){
			Date d=w.createdTime;
			if(d==null){
				println "This weibo has no date.";
				continue;
			}
			println "This weibo has a valid date. Continue.";
			//Convert the date to milliseconds
			int ms=d.getTime();
			spots.add(['value':ms]);
		}
		println "Mapped the dates to spots: ";
		println spots;
		return spots;
	}
	public static void main(String[] args){
		
	}
}
