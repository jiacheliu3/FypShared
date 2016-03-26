package input

import groovy.util.logging.Log4j

@Log4j
class FileVisitor {

	static String basePath="D:/FypGitRepo/web-app/resources/";
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
}
