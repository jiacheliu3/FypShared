package vectorization
import gro.*;

class Type {


	String name;
	LinkedHashMap<String, Double> wordsChi=new LinkedHashMap<>();

	HashSet<String> allWords=new HashSet<>();
	HashMap<String,HashSet<String>> fileContent=new HashMap<>();
	HashMap<String,Map<String,Integer>> wordCount=new HashMap<>();

	String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\feature\\";
	String source="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\resources\\";


	public void load(String n){
		this.name=n;
		File folder=new File(source+this.name+"\\");
		if(!folder.isDirectory()){
			println "Not a folder passed to type ${name}!"
			throw new RuntimeException();
			
		}
		TypeRepo.types.add(this);
		folder.eachFile {
			println "Processing file ${it.name}";
			ArrayList<String> content=it.readLines();
			ArrayList<String> segments=SepManager.getSepManager().segment(content);
			HashSet<String> words=new HashSet<>();
			words.addAll(segments);
			record(it.name,words);
		}
		
	}
	public void record(String fileName,Set<String> words){
		fileContent.put(fileName,words);
		allWords.addAll(words);
	}

	public void computeAll(){
		allWords.each{
			computeAB(it);
			computeCD(it);
			computeChi(it);
			report(it);
		}
		//finalizeFeatures();
	}
	public int fileCount(){
		return fileContent.keySet().size();
	}

	public int outContain(String word){
		int count;
		if(wordCount.containsKey(word)){
			count=wordCount[word]["a"];
		}else{
			count=inContain(word);
		}
		return count;
	}
	public int inContain(String word){
		int count=0;
		int n=fileContent.keySet().size();
		for (def e in fileContent){
			if(e.value.contains(word)){
				count++;
			}
		}
		return count;
	}
	public void computeAB(String word){
		int count=0;
		int n=fileContent.keySet().size();
		for (def e in fileContent){
			if(e.value.contains(word)){
				count++;
			}
		}
		if(wordCount.containsKey(word)){
			wordCount[word].putAll(["a":count,"b":n-count]);
		}
		wordCount.put(word,["a":count,"b":n-count]);
		//println "Type ${name} has ${count} files containing ${word}";
	}
	public void computeCD(String word){
		int count=0;
		int size=0;
		for(Type t in TypeRepo.types){
			count+=t.outContain(word);
			size+=t.fileCount();
		}
		//double c=count/size;
		int c=count;
		//double d=(size-count)/size;
		int d=size-count;
		if(wordCount.containsKey(word)){
			wordCount[word].putAll(["c":c,"d":d]);
		}else{
			wordCount.put(word,["c":c,"d":d]);
		}
	}

	public computeChi(String word){
		Map<String,Integer> m=wordCount[word];
		//(AD-BC)^2/((A+B)*(C+D))
		int a=m["a"];
		int b=m["b"];
		int c=m["c"];
		int d=m["d"];
		def chi=((a*d-b*c)*(a*d-b*c))/((a+b)*(c+d));
		if(wordsChi.containsKey(word)){
			println "Chi value for ${word} already exists, replace ${wordsChi[word]} with ${chi}";
			wordsChi[word]=chi;
		}else{

			wordsChi.put(word,chi);
		}

	}
	public String report(String word){
		Map values=wordCount[word];
		if(values==null||values.size()==0){
			println "Error!${word} not found."
			return "";
		}

		//println "${word} has ${wordCount[word]}";

		println "${word} has chi value ${wordsChi[word]}"
		return wordCount[word].toString();
	}
	public LinkedHashMap<String,Double> getFeatures(){
		return wordsChi;
	}
	//obsolete
	public void finalizeFeatures(){
		if(wordsChi.size()==0){
			println "Error! Chi value of words is empty."
		}
		//sort chi value descendently
		Map<String,Double> top=wordsChi.sort{ a, b -> b.value <=> a.value }
		wordsChi=new LinkedHashMap<>();
		int count=0;
		for(def e in top){
			if(count>=1000)
				break;
			wordsChi.put(e.key,e.value);
			count++;

		}

		println "Extracted ${wordsChi.size()} features.";
		//output features to file
		File outputFolder=new File(base);
		if(!outputFolder.exists())
			outputFolder.mkdirs();
		File featureOutput=new File(base+name+"features.txt");
		int line=1;
		
		for(def e in wordsChi){
			featureOutput.append(line+" "+e.key+"\n");
			line++;
		}
		println "Output complete.";
	}

	@Override
	public String toString(){
		return "Type: ${name}"
	}
	@Override
	public int hashCode(){
		return name.hashCode();
	}
}
