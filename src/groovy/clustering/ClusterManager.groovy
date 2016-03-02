package clustering

import static grails.async.Promises.*
import grails.async.Promise
import grails.async.PromiseList
import groovy.util.logging.Log4j

import java.util.concurrent.*

import segmentation.PythonCaller
import segmentation.SepManager
import cc.mallet.cluster.Clustering
import cc.mallet.types.SparseVector
import codebigbrosub.User
import codebigbrosub.Weibo
@Log4j
class ClusterManager {

	static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
	ConcurrentMap<String,Integer> featureMap=new ConcurrentHashMap<>();
	//Map<String,Integer> featureMap=new HashMap<>();
	ArrayList<Weibo> trainingset=new ArrayList<>();
	ConcurrentMap<Integer,ArrayList<Double>> vectorMap=new ConcurrentHashMap<>();
	//	Map<Integer,ArrayList<Double>> vectorMap=new HashMap<>();
	MalletManager mm;
	TFIDFManager tfidfManager=new TFIDFManager();
	//wrapper class for a clustering
	ClusterWrapper wrapper;
	//store the wordbags generated
	def wordbags;

	//if not specified, use the top 200 microblogs are training data
	public  generateTrainingSet(){
		//load data
		int count=200;
		trainingset=Weibo.findAll("from Weibo as w order by w.createdTime",[max: count]);
		log.info "Training set has ${trainingset.size()}";
	}
	//use microblogs from specified users
	public  generateTrainingSet(Collection<User> users){
		users.each{
			if(it!=null){
				log.info "User is ${it.weiboName}";
				ArrayList<Weibo> weiboList=Weibo.findAll("from Weibo as w where w.ownerName=?", [it.weiboName]);
				log.info "Found ${weiboList.size()} microblogs for user ${it.weiboName}";
				trainingset.addAll(weiboList);

			}
			else{
				log.info "Null user passed to training set generation in cluster manager";
			}
		}
		log.info "${trainingset.size()} weibo added to trainingset from ${users.size()}.";
	}
	public  cleanUp(){
		trainingset?.size=0;
		featureMap?.size=0;
		vectorMap?.size=0;
	}
	//get the most delegating weibo for each tag
	public Map<String,List<Weibo>> getDelegates(){
		log.debug "Find the closest weibo for each tag.";
		def repre=wrapper.matchTagToWeibo();
		return repre;
	}
	public  LinkedHashMap<Weibo,String> formClusters(def wordbags){
		//integrity check on trainingset
		if(trainingset.size()==0){
			log.error "Nothing in the training set!!";
			return;
		}

		//clean up the last clustering process for a new one, and prepare a new one
		mm=new MalletManager();

		//object to wrap the cluster object
		wrapper=new ClusterWrapper();

		//train the features
		trainingset.each{
			tfidfManager.addToTrainingSet(it.content+'///'+it.orgContent);
		}
		tfidfManager.featureExtraction();

		//read features. Features are stored in variable and dimension is passed back
		int dimension = loadFeatures();

		//load the word bags for trainingset
		this.wordbags=wordbags;

		//transfer into formatted data
		generateVectors(mm,wrapper,dimension);
		//get the vectors from wrapper and use Kmeans++ to cluster
		def vectors=[];
		log.info "${vectorMap.size()} stored vectors.";
		for(def e:vectorMap){
			def vec=e.value;
			vectors.add(vec);
		}
		KMeansManager km=new KMeansManager();
		def clustering=km.kmeansPlus(vectors);
		if(clustering==null){
			log.info "No clustering formed.";
			return;
		}
		log.info "Kmeans++ procedure finished. The final clustering is "+clustering;
		def centroids=[];
		centroids.addAll(clustering.keySet());
		
		ArrayList<String> tags=new ArrayList<>();
		//ArrayList<>
		centroids.each{
			double[] values=it as double[];
			//find the tag of the cluster
			String tag=findTag(values);

			log.info "Found tag ${tag}";
			//tagMap.put(tag,values.toList());
			tags.add(tag);

		}
		
		/* Use mallet kmeans */
		//calculate k means, return value is the means of each cluster
		//		mm.kmeans();
		//		ArrayList<SparseVector> centroids=mm.getCentroids();
		//		Clustering clustering = mm.getClustering();
		//		if(clustering==null){
		//			log.info "No clustering formed.";
		//			return;
		//		}
		//
		//		//store the cluster model
		//		storeClusters(centroids);
		//
		//		//find the corresponding tags from the clusters
		//		//LinkedHashMap<String,ArrayList<Double>> tagMap=new LinkedHashMap<>();
		//		ArrayList<String> tags=new ArrayList<>();
		//		//ArrayList<>
		//		centroids.each{
		//			double[] values=it.getValues();
		//			//find the tag of the cluster
		//			String tag=findTag(values);
		//
		//			log.info "Found tag ${tag}";
		//			//tagMap.put(tag,values.toList());
		//			tags.add(tag);
		//
		//		}
		/* End of mallet zone */

		log.info "Final tags are ${tags}";


		wrapper.addCentroids(centroids);
		wrapper.addTags(tags);
		//wrapper.addClusters(clustering.getClusters());
		wrapper.addClusters(clustering);
		
		//def clusterJson=JsonOutput.toJson(wrapper);
		//log.info clusterJson;

		//questionably causing stackoverflow
		//		String report=wrapper.getReport();
		//		log.info report;

		//Already checked the orders of clustering and centroids match each other
		//wrapper.sanityCheck();

		//match the microblogs back to tags
		LinkedHashMap<Weibo,String> mappings=wrapper.matchWeiboToTags();


		return mappings;
	}
	private  String findTagMatch(HashMap<String,ArrayList<Double>> tagMap,double[] vector){
		for(def e:tagMap){
			double[] centroid=(double[])e.value.toArray();
			if(compareArrays(centroid,vector))
				return e.key;
		}
		return "Nothing found";
	}
	private  boolean compareArrays(double[] a, double[] b){
		if(a.length!=b.length){
			log.info "Two arrays have different length. A has ${a.length} while B has ${b.length}!"
			return false;
		}else{
			for(int i=0;i<a.length;i++){
				if(a[i]!=b[i]){
					log.info "Mismatch at ${i}th value.";
					return false
				}
			}
			return true;
		}
	}

	private  void storeClusters(ArrayList<SparseVector> clusters) {
		File clusterOutput=new File("D:\\clusters.txt");
		clusterOutput.write("",'utf-8');
		clusters.each{
			clusterOutput.append(it,'utf-8');
		}

		File clusterModel=new File(base+"clusterModel.txt");
		if(!clusterModel.exists()){
			clusterModel.createNewFile();
		}else{
			clusterModel.write("",'utf-8');

		}
		clusters.each{
			double[] values=it.getValues();
			String tag=findTag(values);
			//output as learned model
			clusterModel.append(tag+"\t"+values+'\n');
		}
		return;
	}
	public String findTag(double[] values){
		double bound=100.0;
		def l=findMax(values,bound);
		String tag="";
		if(l.size()==1){
			int index=l[0];
			tag=featureMap.find{it.value==index+1}?.key;

		}
		else{
			//at most 5 keywords
			for(int i;i<l.size();i++){
				Integer theIndex=l[i];
				String yatag=featureMap.find{it.value==theIndex+1}?.key;
				if(yatag==null||yatag=="")
					continue;
				//				if(i==l.size()-1)
				//					tag+=yatag;
				//				else
				//					tag+=yatag+"+";
				if(i==4){
					tag+=yatag;
				}else if(i<4){
					tag+=yatag+"+";
				}else{
					break;
				}
			}
		}
		if(tag==""){
			log.error "Tag not found by indices ${l}";
		}

		return tag;
	}
	public feedWeiboSegments(def weiboSegments){
		wordbags=weiboSegments;
		log.info "Weibo segments are fed into cluster manager. The segments ${weiboSegments.class} has ${weiboSegments.size()} items.";
	}
	private  generateVectors(MalletManager mm,ClusterWrapper wrapper,int dimension) {
		//integrity check on trainingset
		if(trainingset.size()==0){
			log.error "Nothing in the training set!!";
			return;
		}
		log.info "${trainingset.size()} samples as data";
		//if no weibo segments are fed, generate them based on trainingset
		if(wordbags==null||wordbags.size()==0){
			log.error "No word segments are stored in the cluster manager!";
			//in parallel segment training set text into wordbags
			wordbags=SepManager.getSepManager().parallelSeg(trainingset);//in the same order as traningset
		}
		else{
			log.info "Weibo items have been pre-processed before feeding to cluster study.";
		}

		//generate vector map
		for(int i=0;i<wordbags.size();i++){
			def wordbag=wordbags[i];
			double[] values=new double[dimension];
			wordbag.each{theWord->
				if(theWord==null||theWord==""){}
				else{
					for(def entry:featureMap){
						String feature=entry.key;
						if(feature.contains(theWord)||theWord.contains(feature)){
							//log.debug "${theWord} activated feature ${feature}";
							int index=entry.value;
							//values[index-1]=1.0;
							values[index-1]=100.0;
						}

					}
				}
			}
			log.info "Subtask ${i} Finished generating data instance ${values}";
			//add into vectors
			vectorMap.put(i,values.toList());
			mm.addData(values);
			//store the vectors in ClusterWrapper
			Weibo w=trainingset[i];
			wrapper.record(w,values);

		}

		log.debug "Vector generation finished.";
		log.debug vectorMap;
		//log.debug "Cluster wrapper now: "+wrapper.reportVectorMapping();
		//log.debug "Mallet now has data: "+mm;
		//			for(int i=0;i<trainingset.size();i++){
		//				Weibo w=trainingset[i];
		//				String content=w.content+'---'+w.orgContent;
		//				double[] values=new double[dimension]
		//
		//				//segment the content and map to vector
		//				def wordbag=PythonCaller.pythonSeg(content);
		//
		//
		//				//store the mapping of content and vector
		//				vectorMap.put(i,values.toList());
		//
		//				//def wordbag=SepManager.getSepManager().segment(content);
		//				wordbag.each{
		//					if(featureMap.containsKey(it))
		//						values[featureMap[it]-1]=1.0;
		//				}
		//				log.info "Finished generating data instance ${values}";
		//
		//				mm.addData(values);
		//				//store the vectors in ClusterWrapper
		//				wrapper.record(w,values);
		//			}

	}

	private  int loadFeatures() {
		File features=new File(base+"weiboFeatures.txt");
		int dimension=0;
		if(!features.exists()){
			log.error "Features file does not exist!"
			return 0;
		}
		features.withReader('utf-8'){
			def contents=it.readLines();
			contents.each{line->
				String[] parts=line.split('\t',2);
				if(parts.length>=2){
					featureMap.put(parts[1],Integer.parseInt(parts[0]));
					dimension++;
				}
			}

		}
		log.info "${featureMap.size()} features found"
		return dimension
	}
	public  findMax(double[] values,double bound){
		double max=-100;
		int index=-1;
		def l=[];
		for(int i=0;i<values.length;i++){
			if(values[i]>max){
				index=i;
				max=values[i];
				l.size=0;
			}
			else if(values[i]==max){
				l.add(i);
			}
		}
		if(l.size()==0){
			l.add(index);
		}
		else{
			log.debug "${l.size()} dimensions activated.";
		}
		log.info "Max is index ${l} with value ${max}";
		return l.sort();
	}
	//	public  Instance findMean(Dataset d,int columnNum){
	//		int size=d.size();
	//
	//		double[] values=new double[columnNum];
	//		for(int i=0;i<size;i++){
	//			for(int j=0;j<columnNum;j++){
	//				values[j]+=d.instance(i).value(j);
	//			}
	//		}
	//		log.info "Sum value ${values}"
	//		values.each{
	//			it=it/size
	//		}
	//		log.info "Mean value ${values}"
	//		Instance result=new DenseInstance(values);
	//		return result;
	//	}
	//	public  int findMaxFeature(Instance i){
	//		int size=i.size();
	//		double max=0.0;
	//		int index=0;
	//		for(int j=0;j<size;j++){
	//			if(i.value(j)>max){
	//				max=i.value(j)
	//				index=j;
	//			}
	//		}
	//		return index;
	//	}
	public static void main(String[] args){
		//		/* We load some data */
		//		Dataset data = FileHandler.loadDataset(new File("D:\\weibo\\javaml-0.1.7\\UCI-small\\UCI-small\\iris\\iris.data"), 4, ",");
		//		log.info data;
		//		/* We create a clustering algorithm, in this case the k-means
		//		 * algorithm with 4 clusters. */
		//		Clusterer km=new KMeans(4);
		//		/* We cluster the data */
		//		Dataset[] clusters = km.cluster(data);
		//		log.info clusters;
		//		/* Create a measure for the cluster quality */
		//		ClusterEvaluation sse= new SumOfSquaredErrors();
		//		/* Measure the quality of the clustering */
		//		double score=sse.score(clusters);
		//		log.info score;
		//		ClusterManager cm=new ClusterManager();
		//		cm.cleanUp();
		//		ArrayList<User> users=User.findAll("from User as u order by u.weiboId",[max: 2]);
		//		cm.generateTrainingSet(users);
		//		cm.formClusters();
	}
}
