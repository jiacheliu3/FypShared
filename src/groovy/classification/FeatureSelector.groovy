package classification

import input.FileVisitor
import input.Patterns

import java.util.regex.Pattern

import toolkit.PathManager

class FeatureSelector {
	static Set<String> stopwords=new HashSet<>();
	static Map<Integer,String> featureMap=new HashMap<>();

	public static Map reverseFeatureMap(Map features){
		def reverse=[:];
		features.each{k,v->
			reverse.put(v,k);
		}
		println "The reversed map has ${features.size()} rows.";
		return reverse;
	}
	public static Map loadFeatures(){
		if(featureMap!=null&&featureMap.size()>0){
			println "The features are already loaded.";
			return reverseFeatureMap(featureMap);
		}

		String featurePath=PathManager.ensembleFeaturesFile;
		File featureFile=new File(featurePath);
		if(!featureFile.exists()){
			println "Feature file not found!";
			readFeatures();
			return reverseFeatureMap(featureMap);
		}

		//load the features
		def rows=featureFile.readLines();
		rows.each{row->
			if(row==null){}
			else{
				def parts=row.split(',');
				if(parts.size()==0){}
				else{
					featureMap.put(Integer.parseInt(parts[0]),parts[1]);
				}
			}
		}
		println "Loaded ${featureMap.size()} features.";
		return reverseFeatureMap(featureMap);
	}
	public static void debrief(){

	}
	public static List<String> readFileLines(File input){
		def lines=input.readLines('utf-8');;
		println "File ${input.absolutePath} has ${lines.size()} lines";
		return lines;
	}
	public static Set<String> readSingleFile(File input){
		def lines=input.readLines('utf-8');
		println "File ${input.absolutePath} has ${lines.size()} lines.";
		//separate each line by comma
		Set<String> words=new HashSet<>();
		for(int i=0;i<lines.size();i++){
			String line=lines[i].trim();
			def parts=line.split(',');
			words.addAll(parts);
		}

		println "Found ${words.size()} items from the file.";
		return words;
	}
	public static void storeStopWords(String base){
		def files=FileVisitor.readDir(base);
		if(files==null){
			println "The stop word base is null!";
			return;
		}
		files.each{file->
			def words=readFileLines(file);
			stopwords.addAll(words);
		}
		println "Initiated with ${stopwords.size()} stop words.";
	}
	public static void readFeatures(){
		//initiate stop words
		String stopWordBase=PathManager.stopWordsBase;
		storeStopWords(stopWordBase);
		//read feature keyword files
		String featureBase=PathManager.rawFeaturesBase;
		def files=FileVisitor.readDir(featureBase);
		Set<String> features=new HashSet<>();
		if(files==null){
			println "No files in feature base.";
		}else{
			files.each{file->
				def keywords=readSingleFile(file);
				keywords=filterFeatures(keywords as List<String>);
				features.addAll(keywords);
			}
		}
		println "Read ${features.size()} features.";

		//assign index to the features
		outputFeatures(features);

	}
	public static void outputFeatures(Set<String> features){
		int count=1;
		String finalOutput=PathManager.ensembleFeaturesFile;
		File outputFile=new File(finalOutput);
		//outputFile.append("Index,Feature\n");
		for(String s:features){
			outputFile.append(count+","+s+"\n");
			featureMap.put(count,s);
			count++;
		}
		println "Output ${count} features to the file.";
	}
	//filter words
	public static List filterFeatures(List words){
		String repo="";

		println "${words.size()} words to filter";
		def filtered=[];
		Pattern puncPattern=Patterns.PUNC.value();
		Pattern urlPattern=Patterns.URL.value();
		Pattern datePattern=Patterns.DATE.value();
		Pattern timePattern=Patterns.TIME.value();
		Pattern numberPattern=Pattern.compile("\\d+[.]?\\d*");
		Pattern otherDate=Pattern.compile("\\d+[:\\-/]\\d+[:\\-/]\\d+");
		Pattern pureNumbers=Pattern.compile("[0-9]+");

		Pattern pureEnglish=Pattern.compile("[a-zA-Z]+");
		Pattern numberWithUnit=Pattern.compile("\\d+.?\\d*[\\u4E07\\u4E07\\u5343\\u767E\\u5143\\u4E2A\\u53EA\\u4EBA]");//1.2万千百元个只人
		for(int i=0;i<words.size();i++){
			String word=words[i].trim();

			//filter special punctuations
			word=word.replaceAll(Patterns.PUNC.reg(),"");

			//skip pure english words: one handicap for now
			if(word.matches(pureEnglish)){
				//word=word.toLowerCase();
				continue;
			}
			//single char: dispose
			if(word.length()<=1){
				continue;
			}
			//special char
			if(word.matches(puncPattern)){
				continue;
			}
			//urls
			if(word.matches(urlPattern)){
				continue;
			}
			//numbers with or without dots
			if(word.matches(numberPattern)){
				continue;
			}
			//date
			if(word.matches(datePattern)){
				println "${word} matches date pattern";
				continue;
			}
			//time
			if(word.matches(timePattern)){
				println "${word} matches time pattern";
				continue;
			}
			//some other kinds of time
			if(word.matches(otherDate)){
				continue;
			}
			//pure numbers
			if(word.matches(pureNumbers)){
				println "${word} matches pure numbers";
				continue;
			}
			//stop words and common ones
			if(stopwords.contains(word)){
				println "${word} is found as a stop word.";
				continue;
			}
			//simple numbers in chinese with units
			if(word.matches(numberWithUnit)){
				continue;
			}
			//check duplicate
			if(repo.contains(word)){
				println "${word} is overlapped.";
				continue;
			}

			//keep the word if it survives all this
			filtered.add(word);


		}
		println "${filtered.size()} kept";

		return filtered;
	}
	//output feature file

	public static void main(String[] args){
		//		Pattern numberWithUnit=Pattern.compile("\\d+.?\\d*[\\u4E07\\u4E07\\u5343\\u767E\\u5143\\u4E2A\\u53EA\\u4EBA]");//1000万
		//
		//		println "100万".matches(numberWithUnit)
		//		println "3.15万".matches(numberWithUnit);

		//		Pattern otherDate=Pattern.compile("\\d+[:\\-/]\\d+[:\\-/]\\d+");
		//		Pattern pureNumbers=Pattern.compile("[0-9]+");
		//		println "12335".matches(otherDate);
		//		println "12335".matches(pureNumbers);
		//		println "2011-1-25".matches(otherDate);
		//		println "2011-1-25".matches(pureNumbers);

		//		Pattern puncPattern=Patterns.PUNC.value();
		//		println "1.2万千百元".matches(puncPattern);
		//		println "万千百元".matches(puncPattern);

		readFeatures();


	}

}
