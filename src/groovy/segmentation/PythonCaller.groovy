package segmentation

import groovy.util.logging.Log4j
import input.FileVisitor
import keyword.KeywordFilter
import toolkit.PathManager

@Log4j
class PythonCaller {
	static String base=PathManager.sepManagerTempFolder;
	static String pythonHome=PathManager.keywordScriptPath;
	static String pythonBase=PathManager.pythonBasePath;

	public static void tfidf(String content,long jobId){
		String input=base+"/temp/${jobId}input.txt";
		//String inJiebaTFIDF=base+"temp\\jiebaTFIDFInput.txt";
		String outJiebaTFIDF=base+"/temp/${jobId}jiebaTFIDF.txt";

		//write the content to input file
		File inFile=new File(input);
		inFile.withWriter('UTF-8'){
			inFile.write(content);
		}
		
		//initialize the output file to ensure its existence
		File outputFile=new File(outJiebaTFIDF);
		if(!outputFile.exists()){
			outputFile.createNewFile();
			outputFile.write("","utf-8");
		}else{
			log.error "Why the output file is there without initializing?";
			outputFile.delete();
			outputFile.createNewFile();
			outputFile.write("","utf-8");
		}
		//jieba tfidf
//		Process proc1=Runtime.getRuntime().exec("${pythonBase}\\python ${pythonHome}\\jiebaTFIDF.py ${input} ${outJiebaTFIDF}")
		Process proc1=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaTFIDF.py ${input} ${outJiebaTFIDF}")
		//proc1.waitFor();
		proc1.waitForProcessOutput(System.out,System.err);
		log.debug "Finished jieba TFIDF";
		//clean up input temp file
		if(!inFile.delete()){
			log.error "Failed to clean up the input file ${input}!";
		}
	}
	//use jieba to segment sentences
	public static ArrayList<String> pythonSeg(String content,long jobId){
		log.debug "Prepare to segment ${content}";

		//generate file
		String input=base+"/temp/${jobId}toSeg.txt";
		//write the content to input file
		File inFile=new File(input);
		if(!inFile.exists()){
			inFile.createNewFile();
		}
		//pre-process the string
		content=KeywordFilter.filterUrl(content);

		inFile.write(content,'utf-8');

		String output=base+"/temp/${jobId}segResult.txt";
		File outputFile=new File(output);
		//initialize the file
		outputFile.withWriter('utf-8'){ it.write("") }
		//jieba
//		Process proc1=Runtime.getRuntime().exec("${pythonBase}\\python ${pythonHome}\\jiebaSeg.py ${input} ${output}")
		Process proc1=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaSeg.py ${input} ${output}")
		//proc1.waitFor();
		proc1.waitForProcessOutput(System.out,System.err);
		log.debug "Finished jieba TFIDF"
		//read the output
		def results=[];
		if(!outputFile.exists()){
			log.error "File not found: ${output}";
		}else{
			outputFile.withReader("utf-8"){
				String text=it.readLine();
				String[] words=text?.split(" ");
				if(words?.length>=1){
					words.each{w->
						results.add(w)
					}
				}
			}
		}
		//filter useless characters
		results=KeywordFilter.filterList(results);

		//clean up the file
		if(inFile.delete()==false)
			log.error "${input} file failed to delete";
		if(outputFile.delete()==false)
			log.error "${output} file failed to delete";
		return results;

	}
	//keyword extraction of a lot of weibo items at the same time
	public static largeFileKeyword(File inputFile,File trOutput,File tfOutput){
		//paths
		String input=inputFile.getCanonicalPath();
		String jiebaTR=trOutput.getCanonicalPath();
		String jiebaTFIDF=tfOutput.getCanonicalPath();
		
		//jieba
//		Process proc1=Runtime.getRuntime().exec("${pythonBase}\\python ${pythonHome}\\jiebaTFIDF.py ${input} ${jiebaTFIDF}")
		Process proc1=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaTFIDF.py ${input} ${jiebaTFIDF}")
		//proc1.waitFor();
		proc1.waitForProcessOutput(System.out,System.err);


		//jieba
//		Process proc2=Runtime.getRuntime().exec("${pythonBase}\\python ${pythonHome}\\jiebaTextRank.py ${input} ${jiebaTR}")
		Process proc2=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaTextRank.py ${input} ${jiebaTR}")
		//proc2.waitFor();

		proc2.waitForProcessOutput(System.out,System.err);

		//cleanup input file
		if(inputFile.delete()==false){
			log.error "Failed to clean up input file.";
		}
		log.debug "Keyword extraction task ${input} done with python script";
	}
	//segment a batch of files
	public static batchSeg(File inputFile,File outputFile){
		String inputPath=inputFile.getCanonicalPath();
		String outputPath=outputFile.getCanonicalPath();
		log.info "Prepare to segment contents in file ${inputPath}";
		//jieba
//		Process proc1=Runtime.getRuntime().exec("${pythonBase}\\python ${pythonHome}\\jiebaSeg.py ${inputPath} ${outputPath}");
		Process proc1=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaSeg.py ${inputPath} ${outputPath}");
		//proc1.waitFor();
		proc1.waitForProcessOutput(System.out,System.err);
		log.debug "Finished jieba segmentation";
		//read the output
		def results;
		if(!outputFile.exists()){
			log.error "File not found: ${outputPath}";
			return [];
		}else{
			results=FileVisitor.readCsvFile(outputFile);
		}
		//filter useless characters
		//results=KeywordFilter.filterList(results);
		results=KeywordFilter.filterNestedList(results);

		//clean up the file
		if(inputFile.delete()==false)
			log.error "${inputPath} file failed to delete";
		if(outputFile.delete()==false)
			log.error "${outputPath} file failed to delete";
		return results;
	}
	public static call(String content,long jobId){
		//generate the files
		String input=base+"/temp/${jobId}input.txt";
		//String inSnowNLP=base+"temp\\snowInput.txt";
		String outJiebaTFIDF=base+"/temp/${jobId}jiebaTFIDF.txt";
		String outJiebaTR=base+"/temp/${jobId}jiebaTextRank.txt";

		//write the content to input file
		log.info "Creating new file ${input}"
		File inFile=new File(input);
		if(!inFile.exists()){
			inFile.createNewFile();
		}
		inFile.withWriter('UTF-8'){
			inFile.write(content);
		}

		//initialize the file
		File outTFIDF=new File(outJiebaTFIDF);
		if(!outTFIDF.exists()){outTFIDF.createNewFile()}
		File outTR=new File(outJiebaTR);
		if(!outTR.exists()){outTR.createNewFile()}
		outTFIDF.withWriter('utf-8'){ it.write("") }
		outTR.withWriter('utf-8'){ it.write("") }

		//jieba
//		Process proc1=Runtime.getRuntime().exec("${pythonBase}\\python ${pythonHome}\\jiebaTFIDF.py ${input} ${outJiebaTFIDF}")
		Process proc1=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaTFIDF.py ${input} ${outJiebaTFIDF}")
		//proc1.waitFor();
		proc1.waitForProcessOutput(System.out,System.err);


		//jieba
//		Process proc2=Runtime.getRuntime().exec("${pythonBase}\\python ${pythonHome}\\jiebaTextRank.py ${input} ${outJiebaTR}")
		Process proc2=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaTextRank.py ${input} ${outJiebaTR}")
		//proc2.waitFor();
		proc2.waitForProcessOutput(System.out,System.err);


		//cleanup input file
		if(inFile.delete()==false){
			log.error "Failed to clean up input file.";
		}

		log.debug "Keyword extraction task ${jobId} done with python script";
		//return snowResult.readLines();

	}

	public static main(String[] args){
		String i="C:\\Users\\Tassadar\\Desktop\\Course\\weibo\\temp\\tempInput.txt";
		String o="C:\\Users\\Tassadar\\Desktop\\Course\\weibo\\temp\\output.txt";
		def result=call(i,o);
		println result;
	}

}
