package clustering


class KMeansManager {
	private boolean compareArrays(double[] a, double[] b){
		if(a.length!=b.length){
			println "Two arrays have different length. A has ${a.length} while B has ${b.length}!"
			return false;
		}else{
			for(int i=0;i<a.length;i++){
				if(a[i]!=b[i]){
					println "Mismatch at ${i}th value.";
					return false
				}
			}
			return true;
		}
	}
	public double distanceBetween(List<Double> vector1,List<Double> vector2){
		if(vector1==null){
			println "How come vector is null?!";
			println "${vector1},${vector2}";
		}
		double[] vec1=vector1 as double[];
		double[] vec2=vector2 as double[];
		if(vec1.length!=vec2.length){
			println "Two vectors have different length! Vector 1 has ${vec1.length} dimensions while vector 2 has ${vec2.length}.";
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
		println "Calculate centroids of cluster ${clusters.class}: ${clusters}";
		ArrayList<ArrayList<Double>> centroids=new ArrayList<>();
		clusters.each{
			centroids.add(findCentroid(it));
		}
		return centroids;
	}
	private List<Double> findCentroid(def list){
		int siz=list.size();
		if(siz==0){
			println "No vectors in this cluster at all";
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
		println "New centroid is ${sum}";
		return sum as ArrayList<Double>;
	}
	public List<Double> findClosestCentroid(List<Double> vector,List<List<Double>> centroids){
		if(centroids==null||centroids.size()==0){
			println "No centroids passed to findClosestCentroid()!";
			println "The centroids are ${centroids} and vector is ${vector}."
		}
		if(vector==null){
			println "No vector passed to findClosestCentroid()!";
			println "The centroids are ${centroids} and vector is ${vector}."
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
		println "Assigned probability for each vector: "+closestDist;
		return closestDist;

	}
	public List<Double> findNextCentroid(def prob,double num){
		println "Finding next centroid for random number ${num}";
		if(num>1){
			println "Impossible to find a vector with random number ${num}";
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
			println "Why the centroid is not initialized? Check the probability.";
			println prob;
			Random r=new Random();
			double n=r.nextDouble();
			return findNextCentroid(prob,n);
		}
		return centroid;
	}
	public List<List<Double>> putCentroidsApart(List<List<Double>> vectors,int k){
		println "Generating ${k} centroids for ${vectors.size()} vectors.";
		def centroids=[];
		//randomly pick the first centroid
		Random random = new Random();
		int max=vectors.size()-1;
		int randomNumber = random.nextInt(max);
		List<Double> firstCentroid=vectors[randomNumber];
		println "Got the 1st centroid:"+firstCentroid;
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
	//assign each cluster to centroid and calculate new centroids
	public Map<List,List> calculateMapping(List<List<Double>> centroids,List<List<Double>> vectors){
		println "Mapping ${vectors.size()} to ${centroids.size()} centroids.";
		println "Mapping ${vectors} to ${centroids}";
		
		
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
		println "Finished point assignment, the mapping is: ";
		println mapping;
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
	public Map kmeansPlus(List<List<Double>> vectors){
		//decide k value
		int siz=vectors.size();
		int k=Math.ceil(siz/40);
		println "Arbitrarily choose k value as ${k}.";
		return kmeansPlus(vectors,k);
	}
	//calculate k means clustering of a list of vectors
	public Map kmeansPlus(List<List<Double>> vectors,int k){
		//initialize k centroids that are far enough apart
		List<List<Double>> centroids=putCentroidsApart(vectors,k);
		//continue to calculate kmeans until centroids stablize
		Map<List<Double>,List<List<Double>>> mapping;
		boolean converged=false;
		int counter=1;
		while(!converged&&counter<50){
			mapping=calculateMapping(centroids,vectors);
			def newCentroids=mapping.keySet();
			//check if converged
			converged=compareCentroids(centroids,newCentroids);
			println "${counter}th iteration. Converged: ${converged}";
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
