package clustering

import groovy.util.logging.Log4j
import cc.mallet.cluster.Clustering
import cc.mallet.cluster.KMeans
import cc.mallet.pipe.Array2FeatureVector
import cc.mallet.pipe.Csv2Array
import cc.mallet.pipe.Noop
import cc.mallet.pipe.Pipe
import cc.mallet.pipe.SerialPipes
import cc.mallet.pipe.Target2Label
import cc.mallet.pipe.iterator.ArrayIterator
import cc.mallet.types.Instance
import cc.mallet.types.InstanceList
import cc.mallet.types.NormalizedDotProductMetric
import cc.mallet.types.SparseVector
import codebigbrosub.Job

@Log4j
class MalletManager {
	Pipe pipe = null;
	InstanceList instances = null;

	//static{init();}

	ArrayList<SparseVector> centroids;
	Clustering clustering;

	Job job;//store a reference for the sake of logging
	public MalletManager(Job job){
		this.job=job;
		init();
	}

	public static Pipe buildPipe() {
		ArrayList<Pipe> pipeList = new ArrayList<>();
		pipeList.add(new Csv2Array());
		pipeList.add(new Target2Label());
		pipeList.add(new Array2FeatureVector());
		//pipeList.add(new PrintInputAndTarget());
		return (new SerialPipes(pipeList));
	}

	public void cleanUp(){
		log.debug "Clean up MalletManager";
		if(pipe==null)
			pipe=buildPipe();
		instances = new InstanceList(pipe);
		clustering=null;
		centroids?.size=0;
	}
	public void init(){
		log.debug "Initiate MalletManager";
		pipe = buildPipe();
		instances = new InstanceList(pipe);
		centroids=new ArrayList<>();
	}
	public void addData(double[] row){

		String[][] trainingdata = DoubleArray2String.convert(row);
		try {
			//instances.addThruPipe(new ArrayIterator(trainingdata[i][0],trainingdata[i][1][0]));
			instances.addThruPipe(new ArrayIterator(trainingdata[0],trainingdata[1][0]));
			//instances.addThruPipe(new ArrayIterator(processed[0],processed[1][0]));

		} catch (Exception e) {
			log.error(e);
		}

	}

	public  kmeans(){
		//report existing data set
		log.debug instances;

		//integrity check on dataset
		if(instances.size()==0){
			log.info "No data to cluster";
			return;
		}
		else if(instances.size()==1){
			log.info "Only 1 item in the cluster.";
			return;
		}
		//decide the number of k
		int siz=instances.size();
		//		double l=Math.log10(siz);
		//		int k;
		//		if(l<2)
		//			k=2;
		//		else
		//			k=Math.ceil(l);
		int k=Math.ceil(siz/40);
		int minK=Math.ceil(siz/60);
		log.info "Target cluster size: "+k+",smallest expected size: "+minK;
		log.info "${instances.size()} items for clustering";
		//report instances before clustering
		//debriefInstances();

		KMeans kkk=new KMeans(new Noop(), k, new NormalizedDotProductMetric());
		def clusterReg=kkk.cluster(instances);


		//have a try auto decrease number of clusters
		while(clusterReg==null&&k>minK){
			log.info "Decrease number of clusters.";
			k--;
			log.info "Now k is ${k}";
			kkk=new KMeans(new Noop(), k, new NormalizedDotProductMetric());
			clusterReg=kkk.cluster(instances);
		}

		clustering=clusterReg;

		def mmm=kkk.getClusterMeans();
		boolean needManualAssignment;
		if(mmm==null){
			log.info "No centroids retrieved.";
			return;
		}else{
			log.info "Centroids generated are "+mmm;
			//get rid of duplicate centroids
			mmm=removeDupCentroids(mmm);
			if(centroids==null)
				centroids=new ArrayList<>();
			centroids.addAll(mmm);
		}
		//if the clustering is null, enter manual assignment
		if(clustering==null||needManualAssignment==true){
			log.info "No clustering got from Kmeans. Need to enter manual assignment.";
			int[] clusterLabels=assignLabels(instances,mmm);
			log.debug "Cluster assignment is "+clusterLabels;
			clustering=new Clustering(instances, mmm.size(), clusterLabels )
		}
		else{
			log.debug "Clustering got: "+clustering;
		}


		return centroids;
	}
	public ArrayList removeDupCentroids(ArrayList centroids){
		if(centroids==null){
			log.error "Centroids got from kmeans is null!";
			return;
		}
		//put all double[] into a set, if size decreases then there must be duplicates
		HashSet<ArrayList> newCents=new HashSet<>();
		def toRemove=[];
		centroids.each{
			def data=it.getValues();
			boolean isUnique=newCents.add(data.toList());
			log.debug "Vector is dup?"+isUnique;
			if(!isUnique)
				toRemove.add(it)
		}
		if(newCents.size()!=centroids.size()){
			log.info "Found duplicate centroids! There should be only ${newCents.size()} centroids.";
		}
		toRemove.each{
			centroids.remove(it);
		}
		return centroids;
	}
	public int[] assignLabels(InstanceList myInstances,List centroids){
		int siz=centroids.size();
		log.info "Manually assign vectors to ${siz} centroids";
		int[] assignments=new int[myInstances.size()];
		for(int i=0;i<myInstances.size();i++){
			double minDist=100000.0;
			for(int j=0;j<siz;j++){
				Instance vect=myInstances.get(i);
				SparseVector cent=centroids.get(j);
				//get data from both
				def vectDataReg=vect.getData();
				double[] centData=cent.getValues();
				double[] vectData=vectDataReg.getValues();
				double dist=vectorDistance(vectData,centData);
				if(dist<minDist){
					assignments[i]=j;
					minDist=dist;
				}
			}
		}
		log.debug "Final assignments are "+assignments;
		return assignments;

	}
	public void debriefInstances(){
		instances.each{inst->
			def data=inst.getData();
			double[] d=data.getValues();
			log.debug "Instance has "+d;

		}
	}
	public double vectorDistance(double[] vect1,double[] vect2){
		if(vect1.length!=vect2.length){
			log.error "Two vectors don't match in length! Vector 1 has ${vect1.length} while 2 has ${vect2.length}";
			return 100000.0;
		}
		double distance;
		for(int i=0;i<vect1.length;i++){
			distance += (vect1[i]-vect2[i])*(vect1[i]-vect2[i]);
		}
		return Math.sqrt(distance);
	}
	public Clustering getClustering(){
		if(clustering==null){
			log.info "There is no clustering stored!";
		}
		return clustering;
	}
	public ArrayList<SparseVector> getCentroids(){
		if(centroids?.size()==0){
			log.info "There are no centroids stored!";
		}
		return centroids;
	}
	public double[] anotherDoubleList(int dimension){
		Random r=new Random();
		double[] d=new double[dimension];
		for(int i=0;i<dimension;i++){
			d[i]=r.nextDouble();
		}
		return d;
	}
	public static void main(String[] args){
		//generate instances
		MalletManager mm=new MalletManager();
		for(int i=0;i<1000;i++){
			mm.addData(mm.anotherDoubleList(10));
		}


		int k=10;
		KMeans kkk=new KMeans(new Noop(), k, new NormalizedDotProductMetric());
		def clusterReg=kkk.cluster(mm.instances);
		Clustering clustering=clusterReg;
		println "Got clustering "+clustering==null;
		def mmm=kkk.getClusterMeans();
		println "Got means"+mmm;
	}
}
