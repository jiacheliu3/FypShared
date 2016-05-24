package clustering

import groovy.util.logging.Log4j
import cc.mallet.types.FeatureVector
import cc.mallet.types.InstanceList
import cc.mallet.types.SparseVector
import codebigbrosub.Weibo

@Log4j
class ClusterWrapper {
	ArrayList<ArrayList<Double>> centroids;
	ArrayList<ArrayList<ArrayList<Double>>> clusters;
	ArrayList<String> tags;

	TreeMap<VectorAsKey,ArrayList<Weibo>> vectorMap;
	//LinkedHashMap<String,ArrayList<ArrayList<Double>>>
	//HashMap<Weibo,Integer> mapping;
	public ClusterWrapper(){
		centroids=new ArrayList<>();
		clusters=new ArrayList<>();
		tags=new ArrayList<>();
		vectorMap=new TreeMap<>();
	}
	public void addCentroids(ArrayList<SparseVector> c){
		if(centroids==null)
			centroids=new ArrayList<>();
		c.each{
			if(it instanceof FeatureVector||it instanceof SparseVector){
				double[] values=it.getValues();
				centroids.add((ArrayList<Double>)values.toList());
			}else if(it instanceof List){
				centroids.add((ArrayList<Double>)it);
			}
		}

	}
	public Map<String,List<Weibo>> matchTagToWeibo(){
		//find the closest weibo to each centroid as the representative
		Map<String,List<Weibo>> tagRep=new HashMap<>();
		for(int i;i<centroids.size();i++){
			def v=centroids[i];
			//find closest weibo
			def weiboList=findClosestWeibo(v);
			//find tag for this weibo
			String tag=tags[i];
			tagRep.put(tag,weiboList);
		}
		return tagRep;
	}
	//euclidean distance
	public double distanceBetween(double[] vec1,double[] vec2){
		if(vec1.length!=vec2.length){
			log.error "Two vectors have different length! Vector 1 has ${vec1.length} dimensions while vector 2 has ${vec2.length}.";
			return 1000;
		}
		double sum;
		for(int i=0;i<vec1.length;i++){
			double diff=vec1[i]-vec2[i];
			sum+=diff*diff;
		}
		return Math.sqrt(sum);
	}
	//find the vector that is closest to the centroid
	public ArrayList<Weibo> findClosestWeibo(ArrayList<Double> centroid){
		double[] cen=centroid as double[];
		//find closest vector to the centroid
		double distance=1000;
		def weiboReg;//store the weibo list to return
		vectorMap.each{vector,weiboList->
			double[] vec=vector.values;
			double dis=distanceBetween(vec,cen);
			if(dis<distance){
				distance=dis;
				weiboReg=weiboList
			}
		}

		//return the weibo for the centroid
		log.info "Found weibo list for this centroid: ${weiboReg.size()} weibo.";
		return weiboReg;
	}
	public void addTags(ArrayList<String> t){
		tags.addAll(t);
	}
	public void addClusters(InstanceList[] clustering){
		//log.info "Add clusters ${clustering.class}:${clustering}";
		clustering.each{instanceList->
			ArrayList<ArrayList<Double>> cluster=new ArrayList<>();

			instanceList.each{instance->
				def data=instance.getData();
				//log.debug "Data for instance: "+instance;
				if(!data instanceof SparseVector){
					log.error "Data is not an instance of SparseVector";
				}

				double[] values=((SparseVector)data).getValues();
				def vector=(ArrayList<Double>)values.toList();
				cluster.add(vector);
				//log.debug "Add ${vector} to cluster";

			}
			log.info "${cluster.size()} vectors in this cluster.";
			clusters.add(cluster);

		}
		log.info "${clusters.size()} clusters in this graph.";

	}
	public void addClusters(Map<List,List> clustering){
		log.info "Add clusters ${clustering.class}:${clustering}";
		clustering.each{centroid,cluster->
			log.info "This group has ${cluster} points.";
			clusters.add(cluster);
			
		}
		log.info "Finished recording ${clusters.size()} clusters.";
	}
	public void record(Weibo w, double[] vector){
		VectorAsKey v=new VectorAsKey(vector);
		if(vectorMap.containsKey(v)){
			vectorMap[v].add(w);
		}
		else{
			vectorMap.put(v,[w]);
		}
	}
	public void reportVectorMapping(){
		log.info "Reporing vector mapping";
		log.info vectorMap;
	}
	//questionably causing stackoverflow
	//	public String getReport(){
	//		log.info "Start generating reports";
	//		String report="";
	//		vectorMap.each{key,value->
	//			report+="Vector ${key} has \n";
	//			value.each{weibo->
	//				if(!weibo instanceof Weibo){
	//					log.info "Not instance of Weibo?!"
	//				}else{
	//					report+="User ${weibo.ownerName} has ${weibo.content}\n";
	//				}
	//
	//			}
	//		}
	//		return report;
	//	}
	public LinkedHashMap<Weibo,String> matchWeiboToTags(){
		LinkedHashMap<Weibo,String> mappings=new LinkedHashMap<>();
		if(tags.size()!=clusters.size()){
			log.error "Error! Tags doesn't have the same size as clusters. There are ${tags.size()} tags and ${clusters.size()} clusters.";

		}
		for(int i=0;i<tags.size();i++){
			String tag=tags[i];
			ArrayList<ArrayList<Double>> cluster=clusters[i];
			//find each vector in the cluster and match back to the weibo
			cluster.each{
				VectorAsKey v=new VectorAsKey((double[])it.toArray());
				def w=vectorMap[v];
				if(w==null){
					log.error "Weibo lost in transformation?! What happened?";
				}
				else{
					w.each{weibo->
						mappings.put(weibo,tag);
					}
				}
			}
		}
		log.info "Finished generating the tag mapping report";
		//check on the contents
//		mappings.each{weibo,tag->
//			log.debug "${weibo.content} has tag ${tag}";
//		}

		return mappings;

	}
	public void sanityCheck(){
		log.info clusters.class;
		//calculate centroids based on each cluster
		ArrayList<ArrayList<Double>> c=calculateCentroids(clusters);

		//if the order of centroids match that of clusters, nothing needs to be done
		if(orderMatch(c,centroids)){
			log.error "The order of clusters matches centroids. No need to go further.";
		}
		//else the list needs to be reordered
		else{
			log.error "Order mismatch found in centroids. The centroids order is to be reset.";
			centroids=c;
		}
	}
	public ArrayList<ArrayList<Double>> calculateCentroids(def clusters){
		log.debug "Calculate centroids of cluster ${clusters.class}: ${clusters}";
		ArrayList<ArrayList<Double>> centroids=new ArrayList<>();
		clusters.each{
			centroids.add(findCentroid(it));
		}
		return centroids;
	}

	public static class VectorAsKey implements Comparable<VectorAsKey>{
		double[] values;
		public VectorAsKey(double[] numbers){
			values=numbers;
		}
		public int compareTo(VectorAsKey v){
			double[] otherValues=v.getValues();
			if(values.length!=otherValues.length){
				log.info "Mismatch in size of VectorAsKey class. A has ${values.length} yet B has ${otherValues.length}";
			}
			int siz=Math.min(values.length,otherValues.length);
			for(int i=0;i<siz;i++){
				if(values[i]>otherValues[i])
					return 1
				else if(values[i]<otherValues[i])
					return -1
				else
					continue;
			}
			return 0;
		}
		@Override
		public String toString(){
			return "Vector: ${values}";
		}

	}

	//static toolbox functions
	private static boolean orderMatch(ArrayList<ArrayList<Double>> centroids, ArrayList<ArrayList<Double>> newCentroids){
		if(centroids.size()!=newCentroids.size()){
			log.error "Mismatch in size. A has ${centroids.size()} while B has ${newCentroids.size()}!";
			return false;
		}
		for(int i=1;i<centroids.size();i++){
			if(compareArrays(centroids[i] as double[], newCentroids[i] as double[])){}
			else{
				log.error "Centroids mismatch at ${i}th.";
				return false;
			}
		}
		return true;
	}

	private static double[] findCentroid(def list){
		int siz=list.size();
		if(siz==0){
			log.info "No vectors in this cluster at all";
			return ;
		}
		double[] sum=new double[list[0].size()];
		log.info "${sum} dimensions found.";
		for(int i=0;i<list.size();i++){
			ArrayList<Double> vector=list[i];
			for(int j=0;j<list[0].size;j++){
				sum[j]+=vector[j];
			}
		}
		for(int j=0;j<list[0].size;j++){
			sum[j]/=siz;
		}
		log.info "Centroid is ${sum}";
		return sum;
	}
	private static boolean compareArrays(double[] a, double[] b){
		if(a.length!=b.length){
			log.error "Two arrays have different length. A has ${a.length} while B has ${b.length}!"
			return false;
		}else{
			for(int i=0;i<a.length;i++){
				if(a[i]!=b[i]){
					log.error "Mismatch at ${i}th value.";
					return false
				}

			}
			return true;
		}
	}
	public static void main(String[] args){

		def a1=[1.0, 1.0, 2.0, 3.0, 5.0];
		def a2=[1.0, 1.0, 2.0, 3.0, 5.0];
		def b1=[1.0, 1.0, 2.0, 3.0, 5.0];
		def b2=[1.0, 1.0, 2.0, 3.0, 5.0];
		//log.info a.equals(b);
		def A=[a1, a2];
		def B=[b1, b2];
		println orderMatch(A,B);
		//		def c=[1.0,1.0,2.0,3.0,5.0] as double[];
		//		log.info a1.equals(c);
	}

}
