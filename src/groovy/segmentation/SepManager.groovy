package segmentation

import static grails.async.Promises.*
import grails.async.Promise
import grails.async.PromiseList
import groovy.util.logging.Log4j
import input.FileVisitor
import input.Patterns

import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap

import keyword.KeywordFilter

import org.lionsoul.jcseg.core.*

import toolkit.PathManager
import codebigbrosub.Weibo

import com.google.gson.Gson
import groovy.io.FileType


//import org.lionsoul.jcseg.core.*;

@Log4j
class SepManager {
	String configPath;
	JcsegTaskConfig config;
	ADictionary dic;
	ISegment seg;

	String pyInput;
	String pyOutput;

	//Singleton
	private static SepManager manager;

	static String base=PathManager.sepManagerTempFolder;
	//String uselessWordPath=base+"StopWords.txt";
	

	HashSet<String> negation1=["��", "��", "��", "��"];
	HashSet<String> negation2=["����", "��Ҫ", "����", "û��", "����"];
	HashSet<String> negation3=["������"];

	//handle job id to track each job
	static long jobIndex=0;
	public static long nextJobIndex(){
		long result=jobIndex;
		jobIndex++;
		if(jobIndex>=10000){
			log.debug "Hit 10000 for separation manager. Clear the number and start again.";
			jobIndex=0;
		}
		return result;
	}

	private SepManager(){
		//use jcseg to segment sentences
		//prepareJcseg();

		pyInput=base+"/temp/tempInput.txt";
		pyOutput=base+"/temp/tempOutput.txt";

		LinkedHashMap<String,Double> keywordOdds=new LinkedHashMap<>();

		log.info "SepManager is initialized.";
	}
	public static SepManager getSepManager(){
		if(manager==null)
			manager=new SepManager();
		return manager;
	}
	public Set<String> TFIDF(String content){
		content=filter(content);
		//assign id
		long jobId=nextJobIndex();

		//pass to python script
		PythonCaller.tfidf(content,jobId);

		Set<String> results=new HashSet<>();
		String featureBase=PathManager.keywordFileTempBase;
		File jiebaTFIDF=new File(featureBase+"\\${jobId}jiebaTFIDF.txt");
		if(!jiebaTFIDF.exists()){
			log.error "The python output file is not there!";
			//other methods to be taken
			return [] as Set<String>;
		}
		jiebaTFIDF.withReader('UTF-8'){
			jiebaTFIDF.readLines().each {line->
				//each line is composed of a few keywords separated by comma
				def parts=line.split(',');
				results.addAll(parts);
			}
		}
		if(!jiebaTFIDF.delete()){
			log.error "Temp file ${jiebaTFIDF.getCanonicalPath()} cannot be deleted!";
		}
		return results;
	}
	public Set<String> TFIDF(Collection<String> c,int number){
		LinkedHashMap<String,Double> scores=new LinkedHashMap<>();
		//single-thread tfidf
		//		for(String s:c){
		//			if(s.length()<=5){
		//				continue;
		//			}
		//			//expected+=Math.ceil(Math.sqrt(s.length()));
		//			//get keywords from
		//			Set<String> keywords=TFIDF(s);
		//			println "Got keywords ${keywords}"
		//			keywords.each{w->
		//				if(scores.containsKey(w)){
		//					scores[w]+=1.0
		//				}else{
		//					scores.put(w,1.0);
		//				}
		//
		//			}
		//
		//		}
		//approximate tfidf in a batch
		String reg="";
		for(String s:c){
			reg=reg+s+"\n";
		}
		Set<String> keywords=TFIDF(reg);
		log.debug "Got keywords ${keywords}"
		keywords.each{w->
			if(scores.containsKey(w)){
				scores[w]+=1.0
			}else{
				scores.put(w,1.0);
			}

		}

		//sort by wordcount
		scores=scores.sort { a, b -> b.value <=> a.value };
		//return first K elements
		Set<String> results=new HashSet<>();
		int count=0;
		for(def e : scores){
			count++;
			if(e.key==null||e.key=="")
				continue;
			results.add(e.key);
			if(count>=number)
				break;
		}

		//filter the useless
		results=KeywordFilter.filterList(results);
		return results;
	}
	//merge I/O and get keywords in a batch
	public Map batchKeywords(List<Weibo> weibos){
		//initialize jobid
		long jobId=nextJobIndex();
		
		//initialize input file and flush inputs
		String inputPath=PathManager.keywordFileTempBase+"\\${jobId}input.txt";
		File inputFile=new File(inputPath);
		String reg='';
		for(int i=0;i<weibos.size();i++){
			Weibo w=weibos[i];
			String content;
			if(w.isForwarded){
				content=w.content+"//"+w.orgContent;
			}else{
				content=w.content;
			}
			if(i==0)
				reg=content;
			else{
				reg=reg+'\n'+content;
			}
			//flush to file when you have a hundred
			if(i>0&&i%100==0){
				log.debug "Reached i=${i}. Flush";
				inputFile.append(reg,'utf-8');
				reg='';
			}
		}
		//last flush to clear the register
		inputFile.append(reg,'utf-8');
		reg='';
		log.debug "Flushed the last batch to file ${inputPath}.";
		
		//initialize output file
		String outputTFIDF=PathManager.keywordFileTempBase+"\\${jobId}jiebaTFIDF.txt";
		File tfidf=new File(outputTFIDF);
		tfidf.write("",'utf-8');
		String outputTR=PathManager.keywordFileTempBase+"\\${jobId}jiebaTextRank.txt";
		File tr=new File(outputTR);
		tr.write("",'utf-8');
		//call python script
		PythonCaller.largeFileKeyword(inputFile,tr,tfidf);
		//get the output
		List trWords=FileVisitor.readCsvFile(tfidf);
		List tfidfWords=FileVisitor.readCsvFile(tr);
		
		//format the keywords
		LinkedHashMap<String,Double> keywordOdds=new LinkedHashMap<>();
		trWords.each{lineList->
			lineList.each{it->
				callIncrement(keywordOdds,it,0.506);
			}
		}
		tfidfWords.each{lineList->
			lineList.each{it->
				callIncrement(keywordOdds,it,0.483);
			}
		}
		
		//improve the results
		int maxWords=100;
		log.debug "Raw keywords without organizing: "+keywordOdds;
		keywordOdds=improveKeywords(keywordOdds);
		def results=getResults(keywordOdds,maxWords);
		//results=removeNegationWords(results);
		
		//cleanup output files
		if(tr.delete()==false){
			log.error "Task ${jobId} Failed to clean up jieba textrank output.";
		}
		if(tfidf.delete()==false){
			log.error "Task ${jobId} Failed to clean up jieba tfidf output.";
		}
		
		return results;
	}
	//filter out useless contents
	public static String filter(String s){
		if(s==null)
			return null;
		//filter out the usernames, numbers and urls
		return s.replaceAll("@[\\u4e00-\\u9fa5a-zA-Z\\-_0-9]+", "").replaceAll(Patterns.URL.reg(),"").replaceAll("\\d+"," ");
	}
	//filter keywords and improve results
	public Map improveKeywords(Map<String,Double> keywordOdds){
		log.debug "Improve ${keywordOdds.size()} raw keywords.";
		//filter out stop words
		HashSet<String> useless=new HashSet<>();
		keywordOdds.each{
			if(KeywordFilter.isUselessWord(it.key))
				useless.add(it.key);
		}
		useless.each{
			//log.debug "Filter out useless keyword "+it;
			keywordOdds.remove(it);
		}
		//return only the top results
		log.debug "Sorting...";
		keywordOdds=keywordOdds.sort { a, b -> b.value <=> a.value };
		
		return keywordOdds;
	}
	//remove negation words
	public Map removeNegationWords(Map keywords,String s){
		//check if negation words exist before return
		HashMap<String,Double> toRemove=new HashMap<>();
		HashMap<String,Double> toAdd=new HashMap<>();
		keywords.each{key,value->
			//find all occurrence of keyword in string
			for (int index = s.indexOf(key);
			index >= 0;
			index = s.indexOf(key, index + 1))
			{
				//check if 1-char negation exists
				if(index>=1){
					String n1=s.substring(index-1,index);
					//replace the keyword with its opposite
					if(negation1.contains(n1)){
						toRemove.put(key,value);
						toAdd.put(n1+key,value);
					}
				}
				//check if 2-char negation exists
				if(index>=2){
					String n2=s.substring(index-2,index);
					//replace the keyword with its opposite
					if(negation2.contains(n2)){
						toRemove.put(key,value);
						toAdd.put(n2+key,value);
					}
				}
				//check if 3-char negation exits
				if(index>=3){
					String n3=s.substring(index-3,index);
					//replace the keyword with its opposite
					if(negation3.contains(n3)){
						toRemove.put(key,value);
						toAdd.put(n3+key,value);
					}
				}
			}
		}
		toRemove.each{
			keywords.remove(it.key);
		}
		toAdd.each{
			keywords.put(it.key, it.value);
		}
		return keywords;
	}
	//entrance of separation
	public Map extractKeywords(String s){
		s=filter(s);
		//the expected number of keywords is around the sqrt of length
		int num=Math.ceil(Math.sqrt(s.length()));
		if(num<=2){
			log.debug "String is too short for any keywords!"
			return [:];
		}

		//renew the map
		LinkedHashMap<String,Double> keywordOdds=new LinkedHashMap<>();

		//assign job id to python jobs
		long jobId=nextJobIndex();

		//put files in a large scope
		File jiebaTFIDF;
		File jiebaTextrank
		try{
			//pass to python script
			PythonCaller.call(s,jobId);
			String tfidfPath=base+"/temp/${jobId}jiebaTFIDF.txt";
			jiebaTFIDF=new File(tfidfPath);
			if(!jiebaTFIDF.exists()){
				log.error "File not found: ${tfidfPath}";
			}else{
				jiebaTFIDF.withReader('UTF-8'){
					jiebaTFIDF.readLines().each {line->
						def parts=line.split(',');
						parts.each{word->
							callIncrement(keywordOdds,word,0.483);
						}
					}
				}
			}
			String trPath=base+"/temp/${jobId}jiebaTextRank.txt";
			jiebaTextrank=new File(trPath);
			if(!jiebaTextrank.exists()){
				log.error "File not found: ${trPath}";
			}else{
				jiebaTextrank.withReader('UTF-8'){
					jiebaTextrank.readLines().each{line->
						def parts=line.split(',');
						parts.each{word->
							callIncrement(keywordOdds,word,0.506);
						}						
					}
				}
			}
		}catch(Exception e){
			log.error "An exception in keyword extraction!";
			e.printStackTrace();
			
		}
		//store raw keywords
		log.debug "Raw keywords without organizing: "+keywordOdds;
		keywordOdds=improveKeywords(keywordOdds);

		def results=getResults(keywordOdds,num);
		results=removeNegationWords(results,s);

		//cleanup output files from jieba
		if(jiebaTextrank.delete()==false){
			log.error "Task ${jobId} Failed to clean up jieba textrank output.";
		}
		if(jiebaTFIDF.delete()==false){
			log.error "Task ${jobId} Failed to clean up jieba tfidf output.";
		}

		return results;
	}
	public LinkedHashMap<String,Double> getResults(Map<String,Double> keywordOdds,int number){

		//improve the final result
		keywordOdds=improveResult(keywordOdds);

		int i=1;
		LinkedHashMap<String,Double> result=new LinkedHashMap<>();
		for(def e:keywordOdds){
			result.put(e.key,e.value);
			if(i>=number){
				//log.debug "Task final keywords: "+result;
				return result;
			}
			i++;
		}
		return result;
	}
	public Map<String,Double> improveResult(Map<String,Double> keywords){
		def keywordOdds=keywords;

		HashSet<String> improvedHolder=new HashSet<>();
		HashSet<String> toDelete=new HashSet<>();
		//if A+B and A both exists, add weight of A to A+B
		keywordOdds.each{key,value->
			int l=key.length();

			ArrayList<String> comb=combination(key);
			for(String s in comb){

				String left=key.substring(0,key.indexOf(s));
				String right=key-left-s;
				Boolean deleteL;
				Boolean deleteM;
				Boolean deleteR;
				//log.debug "L:${left} R:${right}";
				if(left==""||keywordOdds[left]!=null){
					deleteL=true;
					if(left!=""&&!improvedHolder.contains(left)){
						keywordOdds[key]+=keywordOdds[left];
						improvedHolder.add(left);
					}
				}
				if(keywordOdds[s]!=null){
					deleteM=true;
					if(!improvedHolder.contains(s)){
						keywordOdds[key]+=keywordOdds[s];
						improvedHolder.add(s);
					}
				}
				if(right==""||keywordOdds[right]!=null){
					deleteR=true;
					if(right!=""&&!improvedHolder.contains(right)){
						keywordOdds[key]+=keywordOdds[right];
						improvedHolder.add(right);
					}
				}
				if(deleteL&&deleteM&&deleteR){
					toDelete.add(left);
					toDelete.add(s);
					toDelete.add(right);

				}

			}

		}

		toDelete.each{
			if(it!=null&&it!="")
				keywordOdds.remove(it);
		}
		return keywordOdds;
	}
	public ArrayList<String> combination(String s){
		ArrayList<String> results=new ArrayList<>();
		if(s.length()==1)
			results.add(s);
		else{
			for(int i in 2..s.length()){
				for(int j in 0..s.length()-i+1){
					results.add(s.substring(j, j+i-1));
				}
			}
		}
		//log.debug results;
		return results;
	}
	//
	public void callIncrement(Map<String,Double> keywordOdds,String str,double value){
		if(str==""||str=="\n"||str==" "||str=="\t"){
			//do nothing
		}else if(str.matches(Patterns.PUNC.value())){
			//skip punctuation
		}
		//not null
		else{

			increment(keywordOdds,str,value);
		}
	}
	public void increment(Map<String,Double> keywordOdds,String key,double value){
		//closure to modify value of key

		if(keywordOdds[key]!=null)
			keywordOdds[key]+=value;
		else
			keywordOdds.put(key, value);

	}
	public ArrayList<String> jcseg(String s){

		//first filter out the @usernames in content to avoid noise
		//String content=str.replaceAll("@[\\u4e00-\\u9fa5a-zA-Z\\-_0-9]+", '');

		//Pass string to string smasher
		seg?.reset(new StringReader(s));

		ArrayList<String> result=new ArrayList<>();
		IWord word=null;
		while ( (word = seg.next()) != null ) {
			//if(isNeeded(word))
			result.add(word.getValue());
		}
		log.debug "Segmentation complete with "+result.size+" segments.";
		return result;
	}
	//segment a string into words
	public ArrayList<String> segment(String s){
		log.debug "Use Fnlp to segement sentence"
		//first filter unnecessary elements
		s=filter(s);
		//use Fnlp to segment sentences
		ArrayList<String> r=FnlpManager.justSegment(s);

		return r;
	}
	//get raw segments
	public ArrayList<String> segment(Collection<String> c){
		log.debug "Use Fnlp to segement sentences"
		ArrayList<String> results=new ArrayList<>();
		c.each{
			results.addAll(segment(it));
		}

		return results;

	}
	//get keywords
	public ArrayList<String> mash(Collection c){
		return FnlpManager.mashCollection(c);
	}

	public ArrayList<String> extractKeywords(ArrayList<String> src){
		StringBuilder sb=new StringBuilder();
		src.each {
			sb.append(it+'\n');
		}
		return extractKeywords(sb.toString());
	}
	//obsolete with Jcseg

	//decide whether a word is needed
	//public boolean isNeeded(IWord word){
	/*
	 China,JPanese,Korean words 
	 T_CJK_WORD = 1;
	 chinese and english mix word.		 like B��,SIM��. 
	 T_MIXED_WORD = 2;
	 chinese last name. 
	 T_CN_NAME = 3;
	 chinese nickname.		 like: �ϳ� 
	 T_CN_NICKNAME = 4;
	 latain series.		 including the arabic numbers.
	 T_BASIC_LATIN = 5;
	 letter number like '���' 
	 T_LETTER_NUMBER = 6;
	 other number like '�٢�΢�' 
	 T_OTHER_NUMBER = 7;
	 pinyin 
	 T_CJK_PINYIN = 8;
	 Chinese numeric   
	 T_CN_NUMERIC = 9;
	 T_PUNCTUATION = 10;
	 useless chars like the CJK punctuation
	 T_UNRECOGNIZE_WORD = 11;
	 * */
	//		def result;
	//		switch(word.getType()){
	//			case 10:case 11:
	//				result=false;
	//				break;
	//			default:result=true;
	//		}
	//if word is needed, continue to check getPosition()
	/* NAME_POSPEECH = {"nr"};����
	 NUMERIC_POSPEECH = {"m"};300��
	 EN_POSPEECH = {"en"};
	 MIX_POSPEECH = {"mix"};
	 PPT_POSPEECH = {"nz"};����ٟ���S�M
	 PUNCTUATION = {"w"};
	 UNRECOGNIZE = {"urg"};�� ������-��
	 * 
	 * */
	//		if(result){
	//			switch(word.getPosition()){
	//				case '/w':case 'w':case '[w]':
	//				case 'urg':case '/urg':
	//					result=false;
	//					break;
	//			}
	//		}
	//of the result words, filter out the useless ones
	//		if(uselessWords.contains(word.getValue())){
	//			result=false;
	//			//log.debug "Found useless word "+word.getValue()+"!!"
	//		}
	//		return result;
	//	}

	/*obsoleted with jcseg*/
//	public void prepareJcseg(){
//		configPath=base+"conf/jcseg.properties";
//		config = new JcsegTaskConfig(configPath);
//		dic = DictionaryFactory.createDefaultDictionary(config);
//		seg = SegmentFactory.createJcseg(JcsegTaskConfig.COMPLEX_MODE, config, dic);
//
//		//load useless words
//		File stopWords=new File(uselessWordPath);
//		String[] useless=stopWords.readLines();
//		useless.each {
//			uselessWords.add(it);
//		}
//		log.debug "SeparatorManager initialized with "+uselessWords.size()+" words filtered."
//	}

	//obsoleted because of using jcseg
	//	public ArrayList<String> obsoleteSeparate(String str){
	//
	//
	//		//first filter out the @usernames in content to avoid noise
	//		String content=str.replaceAll("@[\\u4e00-\\u9fa5a-zA-Z\\-_0-9]+", '');
	//
	//		//Pass string to string smasher
	//		seg?.reset(new StringReader(content));
	//
	//		ArrayList<String> result=new ArrayList<>();
	//		IWord word=null;
	//		while ( (word = seg.next()) != null ) {
	//			if(isNeeded(word))
	//				result.add(word.getValue());
	//		}
	//		log.debug "Segmentation complete with "+result.size+" segments.";
	//		return result;
	//	}
	
	public ArrayList<ArrayList<String>> parallelSeg(ArrayList<String> contents){
		//decide process number
		int siz=contents.size();
		int processNum;
		if(siz<10)
			processNum=1;
		else if(siz<100)
			processNum=2;
		else
			processNum=5;
		log.info "${siz} items in contents for ${processNum} processes.";
		//initialize a map to store all results
		ConcurrentMap<Integer,ArrayList<String>> resultMap=new ConcurrentSkipListMap<Integer,ArrayList<String>>();
		PromiseList promises=new PromiseList();

		//define job
		def seg={missionId,mission,start,end->
			log.info "Mission ${missionId} has ${mission.size()} items to segment.";
			ArrayList<ArrayList<String>> bags=new ArrayList<>();
			for(int j=start;j<end;j++){
				log.debug "Process index ${j} in contents.";
				int index=j-start;
				Weibo w=mission[j-start];
				String taskContent;
				if(w.isForwarded){
					taskContent=w.content+"//"+w.orgContent;
				}else{
					taskContent=w.content;
				}
				long jobId=SepManager.getSepManager().nextJobIndex();
				log.debug "Segment ${index} in mission ${missionId}: "+taskContent;
				def wordbag=PythonCaller.pythonSeg(taskContent,jobId);
				log.debug "Got ${index} in mission ${missionId}: "+wordbag;
				bags.add(wordbag);
				//store the results in concurremt map
				resultMap.put(j,wordbag);

			}
			return bags;
		}
		
		def newSeg={missionId,mission,start,end->
			log.info "Mission ${missionId} has ${mission.size()} items to segment.";
			ArrayList<ArrayList<String>> bags=new ArrayList<>();
			long jobId=SepManager.getSepManager().nextJobIndex();
			log.debug "Segment job index:${jobId}";
			String inputPath=PathManager.segFileTempBase+"\\${jobId}segInput.txt";
			log.debug "Segment file at ${inputPath}";
			File inputFile=new File(inputPath);
			inputFile.write("","utf-8");
			//ensure output file exists
			String outputPath=PathManager.segFileTempBase+"\\${jobId}segOutput.txt";
			File outputFile=new File(outputPath);
			outputFile.write("",'utf-8');
			//output to file
			int taskCount=0;
			String reg="";
			for(int j=start;j<end;j++){
				//log.debug "Process index ${j} in contents.";
				int index=j-start;
				Weibo w=mission[j-start];
				String taskContent;
				if(w.isForwarded){
					taskContent=w.content+"//"+w.orgContent;
				}else{
					taskContent=w.content;
				}
				//filter
				taskContent=KeywordFilter.filterUrl(taskContent);
				if(j==start)
					reg=taskContent;
				else
					reg=reg+'\n'+taskContent;
				taskCount++;
				if(taskCount%100==0){
					log.debug "Piled 100 files. Flush to file.";
					inputFile.append(reg,'utf-8');
					reg='';
				}
			}
			log.debug "Last flush to file.";
			inputFile.append(reg,'utf-8');
			reg='';
			log.debug "Finished flushing ${taskCount} weibo items.";
			//python segment
			def wordbags=PythonCaller.batchSeg(inputFile,outputFile);
			//check size
			if(wordbags.size()!=taskCount){
				log.error "Task count not matching! ${taskCount} tasks written to file yet ${wordbags.size()} got.";
			}
			//put into map
			for(int j=start;j<end;j++){
				int index=j-start;
				def wordbag=wordbags[index];
				if(wordbag==null){
					continue;
				}
				resultMap.put(j,wordbag);
			}
			return bags;
		}

		//generate separated training sets and assign them to promises
		int taskNum=siz/processNum;
		def missions=[];
		for(int i=0;i<processNum;i++){
			int start=i*taskNum;
			int end;
			if(i==processNum-1)
				end=siz;
			else
				end=i*taskNum+taskNum;
			def mission=contents.subList(start,end);
			//create new promise
			Promise p=task{newSeg(i,mission,start,end)}
			if(p==null)
				log.info "Promise is null!";
			else
				log.info "Create promise on ${start} to ${end} of contents.";
			promises.add(p);
		}

		//error handling
		promises.onError{Throwable t->
			log.error "Got error in parallel keyword extraction in clustering.";
			t.printStackTrace();

		}

		Collection results;
		results=promises.get();
		//report
		log.debug "Finished parallel segmentation. ";
		//check the results from resultmap
		HashSet<Integer> missed=new HashSet<>();
		ArrayList<ArrayList<String>> finalWordbags=new ArrayList<>();
		for(int k=0;k<siz;k++){
			if(!resultMap.containsKey(k)){
				log.info "${k}th result not found from resultmap!";
				missed.add(k);
			}else{
				finalWordbags.add(resultMap[k]);
			}
		}
		if(missed.size()>0){
			log.error "All missed numbers are: "+missed;
		}

		return finalWordbags;

	}
	//To test and compare the performance of segmentation services
	public static void main(String[] args){
		//		File test=new File("F:/needless.lex");
		//		StringBuilder sb=new StringBuilder();
		//		test.readLines().each {
		//			sb.append(it);
		//		}
		//		String target=sb.toString();
		//
		//		def manager=new SepManager();
		//		manager.separate(target);
		//		return;
		//File ts=new File(base+"temp\\sampleSet.txt");
		SepManager sep=new SepManager();
		Gson gson=new Gson();
		//
		//		File output=new File(base+"temp\\sampleResult.json");
		//		if(output.exists())
		//			output.write("");
		//		def set=ts.readLines();

		//		JsonWrapper jw=new JsonWrapper();
		//		set.each {
		//			if(it=="\n"||it==""||it==" "||it=="\t"){}
		//			else{
		//				//filter out names
		//				String str=it.replaceAll("@[\\u4e00-\\u9fa5a-zA-Z\\-_0-9]+", "");
		//
		//				def r=sep.separate(str);
		//				println r;
		//
		//				JsonObject j=new JsonObject(it,r);
		//				jw.add(j);
		//			}
		//		}
		//		String temp=gson.toJson(jw);
		//		output.append(temp);

		String test="""用自己的努力付出创造出自己真正想要的未来 ~
北京原駐
了解百姓，关注百姓。
轻财足以聚人，律己足以服人，量宽足以得人，身先足以率人。
手自一体汽车技术已经为中国汽车界创造纯利潤2000多亿元。近期的简易房车。价格10万元左右――这是填补市场空白。欢迎各位给我打电话181 0432 8290
胸怀正义 敢于执言 维护公平 创建和谐 新浪微博社区委员会专家成员
社会心理学者、大学兼职教授、心理顾问与商务顾问。关注社会变迁，关注心态演化，关注成长适应，关注发展促进。网站：http://chinaxhpsy.sunbo6.net
法律专家 ,从业30多年。
其实我很帅只是帅的不明显。。。我等待自己的另一半
佛教禅宗曹洞宗第二十六代传人，菏泽宗第四十代传人，中国禅文化研修院院长，中国佛教书画研修院副院长，中国佛医研究会执行主席。""";
		def n=sep.extractKeywords(test);
		//def m=sep.segment(test);
		println n;
	}
}
