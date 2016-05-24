package classification

import groovy.util.logging.Log4j
import toolkit.PathManager
import codebigbrosub.Weibo

@Log4j
class EnsembleCaller {
	
	static String pythonHome=PathManager.ensemblePythonHome;
	static String tempFileBase=PathManager.ensembleTempFileBase;
	static String pythonExe=PathManager.ensemblePythonExe;
	
	static Map indexToLabel;
	//feature and label names
	public static void loadLabelNames(){
		indexToLabel=new HashMap<Integer,String>();
		String labelPath=PathManager.ensembleLabelFile;
		log.debug "Load the label names from file ${labelPath}";
		File labelFile=new File(labelPath);
		if(!labelFile.exists()){
			log.error "Label map file does not exist!";
			return;
		}
		def rows=labelFile.readLines('utf-8');
		for(int i=0;i<rows.size();i++){
			String row=rows[i];
			def parts=row.split(',');
			if(parts.size()==0)
				continue;
			int index=Integer.parseInt(parts[0]);
			indexToLabel.put(index,parts[1]);
		}
		log.info "Loaded ${indexToLabel.size()} labels.";
		
	}
	public static String getWeiboContent(Weibo w){
		if(w==null){
			log.error "Weibo is null.";
			return null;
		}
		String content;
		if(w.isForwarded){
			content=w.content+"//"+w.orgContent;
		}else{
			content=w.content;
		}
		return content;
	}
	public static void outputToPredict(List<List> vectors,File file){
		if(vectors==null||vectors.size()==0){
			log.debug "No vectors to output.";
			return;
		}
		log.debug "Flushing ${vectors.size()} vectors to file.";
		vectors.each{
			OutputManager.outputSparseRow(it,-1,file);
		}
		log.debug "${vectors.size()} vectors flushed to file.";
	}
	public static Map parallelClassify(List<Weibo> weibos,def jobId){
		log.info "Converting ${weibos.size()} to vectors";
		//prepare temp file for python to read
		String inputPath=tempFileBase+"${jobId}forPython.txt";
		File inputForPython=new File(inputPath);
		inputForPython.write("","ascii");
		
		//not all the weibo items shall be kept
		def keptWeibo=[];
		
		//convert the weibo items to vectors
		def vecList=[];
		for(int i=0;i<weibos.size();i++){
			Weibo weibo=weibos[i];
			String content=getWeiboContent(weibo);
			def vector=VecTransformer.convertVec(content);
			if(vector==null||vector.size()==0){
				log.debug "This vector does not have seen words.";
				continue;
			}
			//keep the weibo for matching the predictions
			keptWeibo.add(weibo);
			vecList.add(vector);
			if(i>0&&i%100==0){
				log.debug "Converted ${i} weibo items.";
				outputToPredict(vecList,inputForPython);
				vecList.clear();
			}
		}
		log.debug "Flush for the last time.";
		outputToPredict(vecList,inputForPython);
		//prepare output file and initialize it
		String outputPath=tempFileBase+"${jobId}forJava.txt";
		File outputForPython=new File(outputPath);
		outputForPython.write("",'ascii');
		
		//pass to python classifier model
		String scriptAddr=pythonHome+"sk_classify.py";
		//check the script first
		File scriptFile=new File(scriptAddr);
		if(!scriptFile.exists()){
			log.error "The script doesn't exist!";
		}
		log.info "Calling python script at ${scriptAddr}...";
		String pythonBase=PathManager.ensemblePythonExe;
		Process proc1=Runtime.getRuntime().exec("${pythonBase}python.exe ${scriptAddr} ${inputPath} ${outputPath}");
		//proc1.waitFor();
		proc1.waitForProcessOutput(System.out,System.err);
		log.info "Ensemble classification is complete.";
		
		//read the output from python process
		def results=outputForPython.readLines();
		if(results.size()!=keptWeibo.size()){
			log.error "The result and input don't match! There are ${keptWeibo.size()} inputs while only ${results.size()} results predicted.";
			return;
		}
		def probs=convertProb(results);
		log.info "Got the predicted probabilities: "+probs;
		
//		//return format
//		def toReturn=[:];
//		for(int i=0;i<weibos.size();i++){
//			Weibo w=weibos[i];
//			toReturn.put(w,probs[i]);
//		}
		
		//aggregate the score under each tag
		def total=aggregateProb(probs);
		log.info "Aggregated score for each weibo item.";
		
		//find the most representative item under each tag
		def typical=findDelegate(probs,weibos);
		
		//format for return
		def result=[:];
		result.put("total",total);
		result.put("delegate",typical);
		return result;
		
	}
	//sum the score under each label
	public static aggregateProb(List probs){
		def stack=[:];
		probs.each{item->//a map of prob under top tags
			item.each{k,v->
				if(stack.containsKey(k)){
					stack[k]+=v;
				}else{
					stack.put(k,v);
				}
			}
		}
		log.debug "Aggregated the scores: "+stack;
		return stack;
	}
	//for each tag, find the item with the top prob to have the tag
	public static findDelegate(List<Map> probs,List<Weibo> weibos){
		log.info "Looking for delegating weibo for each class.";
		def topMap=[:];
		def topWeibo=[:];
		//extract content from weibo item
		def getContent={weibo->
			String content;
			if(weibo.isForwarded){
				content=weibo.orgContent+"//"+weibo.content;
			}else{
				content=weibo.content;
			}
			return content;
		}
		for(int i=0;i<probs.size();i++){
			Map prob=probs[i];
			Weibo weibo=weibos[i];
			
			/* deprecated */
//			def top=prob.max {it.value};
//			String topLabel=top.key;
//			double topProb=top.value;
//			if(topMap.containsKey(topLabel)){
//				double topForThisLabel=topMap[topLabel];
//				if(topProb>topForThisLabel){
//					topWeibo[topLabel]=getContent(weibo);
//					topMap[topLabel]=topProb;
//				}else{
//					//no need to update
//				}
//			}else{
//				topMap[topLabel]=topProb;
//				topWeibo[topLabel]=getContent(weibo);
//			}
			
			//update the top prob map
			prob.each{k,v->
				if(topMap.containsKey(k)){
					def value=topMap[k];
					if(value<v){
						topMap[k]=v;
						topWeibo[k]=getContent(weibo);
					}
				}else{
					topMap.put(k,v);
					topWeibo.put(k,getContent(weibo));
				}
			}
		}
		log.debug "Found the most representative weibo under each tag: "+topWeibo;
		return topWeibo;
	}
	//return a list of maps containing the top probs of classes, match back to class names
	public static List<Map> convertProb(List<String> lines){
		def assignedProbs=[];
		int toKeep=3;
		for(int i=0;i<lines.size();i++){
			String line=lines[i];
			def parts=line.split(',');
			if(parts.size()==0){
				continue;
			}
			def probs=new TreeMap<Double,Integer>(Collections.reverseOrder());//sort the prob in descending order
			for(int j=0;j<parts.size();j++){
				String part=parts[j];
				double prob=Double.parseDouble(part);
				int index=j+1;//the class labels start from 1
				probs.put(prob,index);
			}
			//keep top
			def toReturn=[:];
			for(def e:probs){
				if(toReturn.size()>=toKeep){
					break;
				}
				//convert to label names
				int index=e.value;
				if(indexToLabel==null||indexToLabel.size()==0){
					log.debug "No label map loaded yet. Initialize it first.";
					loadLabelNames();
				}
				String labelName=indexToLabel[index];
				if(labelName==null||labelName==""){
					log.error "Label name for index ${index} is not known!";
					labelName="Unknown";
				}
				toReturn.put(labelName,e.key);
			}
			assignedProbs.add(toReturn);
		}
		log.info "Read ${assignedProbs.size()} results from python output.";
		return assignedProbs;
	}
	public static void testWithKnown(){
		def paths=['pet','digital','comic'];
		//for the files under path, predict their labels
		for(int i=0;i<paths.size();i++){
			String name=paths[i];
			log.info "Testing on file ${name}";
			
			//make the path for input
			String inputPath=tempFileBase+paths[i];
			File inputFile=new File(inputPath);
			if(!inputFile.exists()){
				log.error "No input file found!";
				continue;
			}
			
			//pass to python classifier model
			String scriptAddr=pythonHome+"sk_classify.py";
			//check the script first
			File scriptFile=new File(scriptAddr);
			if(!scriptFile.exists()){
				log.error "The script doesn't exist!";
			}
			//prepare output file and initialize it
			File outputForPython=new File(tempFileBase+"forJava.txt");
			outputForPython.write("",'ascii');
			
			log.info "Calling python script at ${scriptAddr}...";
			String pythonBase=PathManager.ensemblePythonExe;
			Process proc1=Runtime.getRuntime().exec("${pythonBase}python.exe ${scriptAddr} ${inputPath} ${tempFileBase+'forJava.txt'}");
			//proc1.waitFor();
			proc1.waitForProcessOutput(System.out,System.err);
			log.info "Ensemble classification is complete.";
			
			//read the output from python process
			def results=outputForPython.readLines();
			def probs=convertProb(results);
			log.info "Got the predicted probabilities: "+probs;
			
			log.info "Test complete.";
		}
	}
	public static void main(String[] args){
		testWithKnown();
	}
}
