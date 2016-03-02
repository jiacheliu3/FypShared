package vectorization

class TypeRepo {
	public static HashSet<Type> types=new HashSet<>();
	static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\feature\\";

	static int featureNum=1000;
	public static int getFeatureNum(){return featureNum;}
	public static startCompute(){
		types.each{
			//compute ABCD value of all words in the type
			it.computeAll();
		}
	}

	public static finalizeFeatures(){
		LinkedHashMap<String,Double> features=new LinkedHashMap<>();

		types.each{
			features<<it.getFeatures();
		}
		println "Totally ${features.size()} candidate features.";

		features=features.sort{ a, b -> b.value <=> a.value }

		int count= 0;
		LinkedHashMap<String,Double> t=new LinkedHashMap<>();

		//output features to file
		File outputFolder=new File(base);
		if(!outputFolder.exists())
			outputFolder.mkdirs();
		File featureOutput=new File(base+"features.txt");

		for(def e in features){
			count++;
			featureOutput.append(count+" "+e.key+"\n");
			if(count>=featureNum){
				println "${featureNum} features extracted."
				break;
			}
		}
		println "Output complete.";

	}
	
	public static main(String[] args){
		def s= [1,2,3,4,5,6,7,8].subList(0,1);
		println s;
	}
}
