package clustering

import groovy.util.logging.Log4j

@Log4j
class KMeansManager {
	//initialize a random generator
	Random random = new Random();
	
	private boolean compareArrays(double[] a, double[] b){
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
	public double distanceBetween(List<Double> vector1,List<Double> vector2){
		if(vector1==null){
			log.error "How come vector is null:${vector1},${vector2}";
		}
		double[] vec1=vector1 as double[];
		double[] vec2=vector2 as double[];
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
	public ArrayList<ArrayList<Double>> calculateCentroids(def clusters){
		log.debug "Calculate centroids of cluster ${clusters.class}: ${clusters}";
		ArrayList<ArrayList<Double>> centroids=new ArrayList<>();
		clusters.each{
			centroids.add(findCentroid(it));
		}
		return centroids;
	}
	private List<Double> findCentroid(def list){
		int siz=list.size();
		if(siz==0){
			log.error "No vectors in this cluster at all";
			return ;
		}
		double[] sum=new double[list[0].size()];
		//println "${sum.length} dimensions found.";
		for(int i=0;i<list.size();i++){
			ArrayList<Double> vector=list[i];
			for(int j=0;j<list[0].size;j++){
				sum[j]+=vector[j];
			}
		}
		for(int j=0;j<list[0].size;j++){
			sum[j]/=siz;
		}
		log.debug "New centroid is ${sum}";
		return sum as ArrayList<Double>;
	}
	public List<Double> findClosestCentroid(List<Double> vector,List<List<Double>> centroids){
		if(centroids==null||centroids.size()==0){
			log.error "No centroids passed to findClosestCentroid()!";
			log.debug "The centroids are ${centroids} and vector is ${vector}."
		}
		if(vector==null){
			log.error "No vector passed to findClosestCentroid()!";
			log.debug "The centroids are ${centroids} and vector is ${vector}."
		}
		double dist=Double.MAX_VALUE;
		def closest;
		centroids.each{cen->
			double d=distanceBetween(vector,cen);
			if(d<dist){
				dist=d;
				closest=cen;
			}
		}
		return closest;
	}
	public Map<Double,List<Double>> calculateProb(def vectors,def centroids){
		def closestDist=[:];
		//find the closest centroids of each vector and record the distance
		double sum=0;
		vectors.each{vector->
			def closestCentroid=findClosestCentroid(vector,centroids);
			double distance=distanceBetween(vector,closestCentroid);
			closestDist.put(vector, distance);
			sum+=distance;
		}
		//assign prob
		for(def e:closestDist){
			def v=e.key;
			closestDist[v]=e.value/sum;
		}
		log.debug "Assigned probability for each vector.";
		return closestDist;

	}
	public List<Double> findNextCentroid(def prob,double num){
		log.debug "Finding next centroid for random number ${num}";
		if(num>1){
			log.debug "Impossible to find a vector with random number ${num}";
			return null;
		}
		def centroid;
		double sum=0;
		for(def e:prob){
			def vector=e.key;
			def chance=e.value;
			sum+=chance;
			if(sum>=num){
				centroid=vector;
				break;
			}
		}
		if(centroid==null){
			log.debug "Why the centroid is not initialized? Check the probability.";
			log.debug prob;
			Random r=new Random();
			double n=r.nextDouble();
			return findNextCentroid(prob,n);
		}
		return centroid;
	}
	public List<List<Double>> putCentroidsApartByDistance(List<List<Double>> vectors,int k){
		log.debug "Generating ${k} centroids for ${vectors.size()} vectors.";
		def centroids=[];
		int max=vectors.size()-1;

		int randomNumber = random.nextInt(max);
		List<Double> firstCentroid=vectors[randomNumber];
		log.debug "Got the 1st centroid:"+firstCentroid;
		centroids.add(firstCentroid);

		while(centroids.size()<k){
			//calculate the probability of each point to be chosen as the next centroid
			//based on the distance to the closest centroid
			def prob=calculateProb(vectors,centroids);
			//pick the centroid based on prob.
			double nextNum=random.nextDouble();
			def nextCentroid=findNextCentroid(prob,nextNum);
			centroids.add(nextCentroid);

		}

		return centroids;
	}
	public List<List<Double>> putCentroidsApartRandomly(List<List<Double>> vectors,int k){
		log.debug "Generating ${k} centroids for ${vectors.size()} vectors.";
		def centroids=[];
		while(centroids.size()<k){
			int nextIndex=random.nextInt(vectors.size());//random is exclusive of the top value given
			def nextOne=vectors[nextIndex];
			centroids.add(nextOne);
			vectors.remove(nextOne);
		}
		log.debug "${k} vectors randomized.";
		return centroids;
	}
	//assign each cluster to centroid and calculate new centroids
	public Map<List,List> calculateMapping(List<List<Double>> centroids,List<List<Double>> vectors){
		log.debug "Mapping ${vectors.size()} to ${centroids.size()} centroids.";
		log.debug "Mapping ${vectors} to ${centroids}";
		
		def mapping=[:];
		//initialize the mapping
		centroids.each{c->
			def points=[];
			mapping.put(c,points);
		}
		//for each point assign them to the closest centroid
		vectors.each{vector->
			def centroid=findClosestCentroid(vector,centroids);
			mapping[centroid].add(vector);
		}
		log.debug "Finished point assignment, the mapping is: ";
		log.debug mapping;
		//calculate new centroids
		def newMapping=[:];
		mapping.each{centroid,group->
			//calculate new centroid based on the group of points
			def newCentroid=findCentroid(group);
			newMapping.put(newCentroid, group);
		}
		return newMapping;
	}
	//compare two groups of centroids checking whether they are the same
	public boolean compareCentroids(def centroids,def newCentroids){
		//use the comparator of Centroid class to compare two clusters
		Set<Centroid> group1=new TreeSet<>();
		Set<Centroid> group2=new TreeSet<>();
		centroids.each{
			group1.add(new Centroid(it));
		}
		newCentroids.each{
			group2.add(new Centroid(it));
		}
		//compare two groups
		if(group1.size()!=group2.size())
			return false;
		return group1.equals(group2);
	}
	public Map calculateKmeans(List<List<Double>> vectors){
		int maxK=10;
		//decide k value
		int siz=vectors.size();
		int k=Math.ceil(siz/40);
		if(k>maxK){
			log.debug "K value reaches upper limit. Set to ${maxK} clusters manually.";
			k=maxK;
		}
			
		log.debug "Arbitrarily choose k value as ${k}.";
		
		//approximate by using plain kmeans if there are too many vectors
		int threshold=500;
		def clustering;
		if(siz>threshold){
			log.info "${siz} vectors is beyond the calculation power. Use plain kmeans to achieve quicker response.";
			clustering=kmeans(vectors,k);
		}else{
			log.info "${siz} vectors is within calculation power. Use kmeans++ to achieve better results.";
			clustering=kmeansPlus(vectors,k);
		}
		return clustering;
	}
	//calculate kmeans
	public Map kmeans(List<List<Double>> vectors,int k){
		//initialize k centroids that are far enough apart
		List<List<Double>> centroids=putCentroidsApartRandomly(vectors,k);
		//continue to calculate kmeans until centroids stablize
		Map<List<Double>,List<List<Double>>> mapping;
		boolean converged=false;
		int counter=1;
		while(!converged&&counter<50){
			mapping=calculateMapping(centroids,vectors);
			def newCentroids=mapping.keySet();
			//check if converged
			converged=compareCentroids(centroids,newCentroids);
			log.debug "${counter}th iteration. Converged: ${converged}";
			//store the new centroids
			centroids.size=0
			centroids.addAll(newCentroids);
			counter++;
		}
		return mapping;
	}
	//calculate k means clustering of a list of vectors
	public Map kmeansPlus(List<List<Double>> vectors,int k){
		Map<List<Double>,List<List<Double>>> mapping;

		if(vectors.size()==0){
			log.info "No vectors to cluster"
			mapping=new HashMap<>();
			return mapping;
		}

		//initialize k centroids that are far enough apart
		List<List<Double>> centroids=putCentroidsApartByDistance(vectors,k);
		//continue to calculate kmeans until centroids stablize
		boolean converged=false;
		int counter=1;
		while(!converged&&counter<50){
			mapping=calculateMapping(centroids,vectors);
			def newCentroids=mapping.keySet();
			//check if converged
			converged=compareCentroids(centroids,newCentroids);
			log.debug "${counter}th iteration. Converged: ${converged}";
			//store the new centroids
			centroids.size=0
			centroids.addAll(newCentroids);
			counter++;
		}
		return mapping;
	}
	public static class Centroid implements Comparable<Centroid>{
		List<Double> vector;
		public Centroid(List<Double> vector){
			this.vector=vector;
		}
		public Centroid(double[] vector){
			this.vector=vector as ArrayList<Double>;
		}
		@Override
		public int compareTo(Centroid c) {
			List<Double> vector2=c.vector;
			if(vector==null||vector2==null){
				log.error "One of the vectors are null! ${vector},${vector2}";
				return -1;
			}
			if(vector.size()!=vector2.size())
				return vector.size().compareTo(vector2.size());
			for(int i=0;i<vector.size();i++){
				if(vector[i]==vector2[i])
					continue;
				else
					return vector[i].compareTo(vector2[i]);
			}
			return 0;
		}
		@Override
		public String toString(){
			return vector?.toString();
		}
	}
	public static List<Double> generateRandomVector(int siz){
		Random random=new Random();
		def vec=[];
		while(vec.size()<siz){
			int nextInt=random.nextInt(100);
			vec.add(nextInt);
		}
		return vec;
	}
	public static void main(String[] args){
		def vectors=[];
		for(int i=0;i<100;i++){
			def vec=generateRandomVector(3);
			vectors.add(vec);
		}
		KMeansManager kp=new KMeansManager();
		def result=kp.kmeansPlus(vectors,3);
		println "How did I make this through here!";
		println result;
	}
}
