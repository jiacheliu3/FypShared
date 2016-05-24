package clustering
import groovy.util.logging.Log4j
import keyword.*
import segmentation.SepManager
import toolkit.PathManager

@Log4j
class TFIDFManager {
	static String base=PathManager.featureTempBase;
	//static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
	ArrayList<String> trainingSet=new ArrayList<>();
	HashMap<String,Double> features=new HashMap<>();

	public void addToTrainingSet(String content){
		log.debug "Add to training set of tfidf manager:\n"+content;
		trainingSet.add(content);
	}
	public void addKeywords(Collection<String> c){
		log.debug "Add to tfidf keywords:\n"+c;
		c.each{
			if(!features.containsKey(it)){
				features.put(it,0.0);
			}
		}
		log.debug "After adding to tfidf keywords: "+c;
	}
	public void featureExtraction(){
		//report current training set
		log.info "Existing training set for feature extraction:";
		log.info trainingSet;
		
		//use TFIDF provided by jieba
		Set<String> keywords=SepManager.getSepManager().TFIDF(trainingSet,500);
		log.debug "Keywords got from features.";
		
		//filter out useless stop words
		keywords=KeywordFilter.filterList(keywords);
		
		//write to file
		File features=new File(base+"\\weiboFeatures.txt");
		if(features.exists()){
			features.withWriter('utf-8'){it.write("")}
		}
		int count=1;
		keywords.each{
			features.append(count+"\t"+it+"\n",'utf-8');
			count++;
		}
		//clean up training set
		trainingSet.size=0;
	}
	public void tagExtraction(){
		//convert each microblog in the trainingset into vector

		//kmeans to get clusters

		//extract tags


	}

}
