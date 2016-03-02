package clustering

import groovy.json.JsonSlurper
import segmentation.PythonCaller

class GroupFinder {
	static Map<String,Integer> featureMap=new HashMap<>();
	static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
	static Map<String, ArrayList<Double>> clusterMeans=new HashMap<>();
	public static void loadFeatures(){
		if(ClusterManager.featureMap?.size()>0){
			featureMap=ClusterManager.featureMap;
			return;
		}
		//read features
		File features=new File(base+"weiboFeatures.txt");
		int lineNum=0;
		if(!features.exists()){
			println "Sth wrong with features file!"
		}
		features.withReader('utf-8'){
			def contents=it.readLines();
			contents.each{line->
				String[] parts=line.split('\t',2);
				if(parts.length>=2){
					featureMap.put(parts[1],Integer.parseInt(parts[0]));
					lineNum++;
				}
			}

		}
		println "${featureMap.size()} features found"
	}
	public static void loadClusters(){
		File clusterFile=new File(base+'clusterModel.txt');
		def contents=clusterFile.readLines();
		contents.each{
			String[] parts=it.split('\t');
			if(parts.length>=2){
				String tag= parts[0];
				def jsonSlurper = new JsonSlurper();
				def vector = jsonSlurper.parseText(parts[1]);
				//println vector.class;

				def array=vector.toArray();
				//println array;

				clusterMeans.put(tag,array.toList());

			}else{
				println "Empty line, no tags and point found";
			}
		}
		//println clusterMeans;
	}
	public static String classify(String content){

		int dimension=featureMap.size();
		//segment the content and map to vector
		double[] values=new double[dimension];
		def wordbag=PythonCaller.pythonSeg(content);
		println "Got keywords ${wordbag}";
		//def wordbag=SepManager.getSepManager().segment(content);
		wordbag.each{
			if(featureMap.containsKey(it))
				values[featureMap[it]-1]=1.0;
		}
		println "Generated vector ${values}";
		//find the cluster
		String tag=findClosestTag(values);

		return tag;
		
	}
	public static String findClosestTag(double[] vector){
		println "Input vector has dimension ${vector.length}";
		double distance=100000;
		String tag;
		clusterMeans.each{key,value->
			double d=calculateDistance(vector,(Double[])value.toArray());
			if(d<distance){
				tag=key;
				distance=d;
			}
		}
		println "Final closest cluster is ${tag} with distance ${distance}";
		return tag;
	}
	public static double calculateDistance(double[] A,double[] B){
		//check dimension
		if(A.length!=B.length){
			println "Two vectors don't match in dimension. A: ${A.length} and B:${B.length}";
			return 10000.0;
		}
		else{
			int l=A.length;
			double sum=0;
			for(int i=0;i<l;i++){
				sum+=Math.pow(A[i]-B[i],2);
			}
			return Math.sqrt(sum);
		}
		
	}
	public static void main(String[] args){
		loadFeatures();
		loadClusters();
		classify("由萤石独家冠名的《我是传奇》活动将于7月18日正式启动，这是一项追梦项目，助力主人公“大河”重返蓝天。大河是伞圈里的传奇，他在2013年一次运动意外事故严重致伤，造成小腹以下失去知觉。本活动正在紧锣密鼓的筹备中。快看我们的帅大河，正在接受CCTV的采访呢");

	}
}
