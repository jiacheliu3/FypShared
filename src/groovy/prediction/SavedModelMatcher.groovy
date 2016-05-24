package prediction

import groovy.util.logging.Log4j
import keyword.KeywordFilter
import segmentation.SepManager
import toolkit.PathManager

@Log4j
class SavedModelMatcher {
	//get the extracted keywords for the specific type
	public static Collection<String> keywordsFromType(String type){
		String filePath=PathManager.keywordBasePath+"\\${type}_keywords.txt";
		File keywordFile=new File(filePath);
		if(!keywordFile.exists()){
			log.error "Pre-defined keyword for class ${type} is not found!";
			return [];
		}
		//read the file and get keywords from it
		Set<String> words=new HashSet<>();
		keywordFile.withReader("utf-8"){
			def content=keywordFile.text;
			if(content.length()<30){
				def parts=content.split("\\s+");
				words.addAll(KeywordFilter.filterList(parts));
			}else{
				//if the content is long, extract keywords from it
				Map keywordOdds=SepManager.getSepManager().extractKeywords(content);
				//keep top 10 of keywords
				int cnt=0;
				int toKeep=10;
				for(def e:keywordOdds){
					if(words.size()>=toKeep)
						break;
					words.add(e.key);
				}
			}
		}
		log.debug "Fetched keywords for class ${type}: "+words;
		return words;
	}
	//get the pre-defined for the specific type
	public static Collection<String> websitesFromType(String type){
		log.info "Finding pre-defined interesting websites for predicted type: ${type}";
		String filePath=PathManager.websiteBasePath+"\\${type}_websites.txt";
		File websiteFile=new File(filePath);
		if(!websiteFile.exists()){
			log.error "Pre-defined website file for type ${type} does not exist!";
			return [];
		}
		def websites=[];
		def lines=websiteFile.readLines('utf-8');
		for(int i=0;i<lines.size();i++){
			String line=lines[i];
			if(!line.contains('.'))
				continue;
			websites.add(line);
		}
		log.info "Fetched interesting websites for class ${type}: "+websites;
		return websites;
	}
	//get the pre-defined for the specific type, only keeping the top
	public static Collection<String> websitesFromType(String type, int tokeep){
		log.info "Finding pre-defined interesting websites for predicted type: ${type}";
		String filePath=PathManager.websiteBasePath+"\\${type}_websites.txt";
		File websiteFile=new File(filePath);
		if(!websiteFile.exists()){
			log.error "Pre-defined website file for type ${type} does not exist!";
			return [];
		}
		def websites=[];
		def lines=websiteFile.readLines('utf-8');
		for(int i=0;i<lines.size();i++){
			String line=lines[i];
			if(websites.size()>=tokeep){
				log.debug "Found the top ${tokeep} websites already.";
				break;
			}				
			if(!line.contains('.'))
				continue;
			websites.add(line);
		}
		log.info "Fetched interesting websites for class ${type}: "+websites;
		return websites;
	}
}
