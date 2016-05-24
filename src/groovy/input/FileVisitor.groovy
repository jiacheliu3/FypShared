package input

import java.io.File;
import java.util.List;

import groovy.util.logging.Log4j

@Log4j
class FileVisitor {

	static String basePath="C:/Users/jiacheliu3/workspace/CodeBigBroSub/web-app/resources/";
	public static String readFileContent(String path){
		String filePath=basePath+path;
		File input=new File(filePath);
		if(!input.exists()){
			log.error "File not found under path ${filePath}";
			return "";
		}else{
			String contents=input.getText('UTF-8');
			return contents;
		}
	}
	public static List<File> readDir(String path){
		File baseDir=new File(path);
		def files;
		if(!baseDir.exists()){
			println "No base directory for stop words!";
			return;
		}else if(!baseDir.isDirectory()){
			println "Base directory is not a directory!";
		}else{
			files=baseDir.listFiles();
			println "Found ${files.size()} files in the directory.";
		}
		return files;
	}
	//read a file where each line is comma separated
	public static List readCsvFile(File target){
		if(!target.exists()){
			log.error "File ${target.getCanonicalPath()} does not exist!";
			return [];
		}
		def result=[];
		target.withReader('utf-8'){
			def rows=target.readLines();
			for(int i=0;i<rows.size();i++){
				String row=rows[i];
				def parts=row.split(',');
				result.add(parts);
			}
		}
		log.debug "Read file with ${result.size()} lines";
		return result;
	}
}