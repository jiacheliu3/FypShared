package segmentation

import groovy.util.logging.Log4j
import keyword.KeywordFilter

@Log4j
class PythonCaller {
	static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
	static String pythonHome="C:\\Users\\jiacheliu3\\workspace\\CodeBigBroRelated"

	public static void tfidf(String content,long jobId){
		String input=base+"temp\\${jobId}input.txt";
		//String inJiebaTFIDF=base+"temp\\jiebaTFIDFInput.txt";
		String outJiebaTFIDF=base+"temp\\${jobId}jiebaTFIDF.txt";

		//write the content to input file
		File inFile=new File(input);
		inFile.withWriter('UTF-8'){
			inFile.write(content);
		}
		//jieba tfidf
		Process proc1=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaTFIDF.py ${input} ${outJiebaTFIDF}")
		proc1.waitFor();
		log.debug "Finished jieba TFIDF"
	}
	//use jieba to segment sentences
	public static ArrayList<String> pythonSeg(String content,long jobId){
		log.debug "Prepare to segment ${content}";

		//generate file
		String input=base+"temp\\${jobId}toSeg.txt";
		//write the content to input file
		File inFile=new File(input);
		if(!inFile.exists()){
			inFile.createNewFile();
		}
		//pre-process the string
		content=KeywordFilter.filterUrl(content);

		inFile.write(content,'utf-8');

		String output=base+"temp\\${jobId}segResult.txt";
		File outputFile=new File(output);
		//initialize the file
		outputFile.withWriter('utf-8'){ it.write("") }
		//jieba
		Process proc1=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaSeg.py ${input} ${output}")
		proc1.waitFor();
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
	public static call(String content,long jobId){
		//generate the files
		String input=base+"temp\\${jobId}input.txt";
		//String inSnowNLP=base+"temp\\snowInput.txt";
		String outJiebaTFIDF=base+"temp\\${jobId}jiebaTFIDF.txt";
		String outJiebaTR=base+"temp\\${jobId}jiebaTextRank.txt";

		//write the content to input file
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
		Process proc1=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaTFIDF.py ${input} ${outJiebaTFIDF}")
		proc1.waitFor();


		//jieba
		Process proc2=Runtime.getRuntime().exec("python ${pythonHome}\\jiebaTextRank.py ${input} ${outJiebaTR}")
		proc2.waitFor();


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
