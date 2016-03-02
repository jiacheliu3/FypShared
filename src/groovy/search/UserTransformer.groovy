//package search
//
//
//import java.util.Map;
//
//import java.util.TreeMap;
//
//import Jama.Matrix
//import exceptions.TypeNotFoundException
//import exceptions.*;
//import svm.*;
//import vectorization.*;
//
////calculate the coverage of a user's weibo and output the user's type vector
//class UserTransformer {
//	static {load();}
//
//	public static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
//	public static TreeMap<Integer,String> features=new TreeMap<>();
//	public static Map<Integer, String> typeMap=new HashMap<>();
//	public static load(){
//		readFeatures();
//		readTypes();
//	}
//	public static void readFeatures(){
//		if(Trainer.features==null)
//			Trainer.readFeatures();
//
//
//		features=Trainer.features;
//
//	}
//	public static void readTypes(){
//		if(Classifier.typeMap==null||Classifier.typeMap.size()==0)
//			Classifier.readTypeMatch();
//		typeMap=Classifier.typeMap;
//	}
//	public static Matrix scanUser(User u){
//		//read user's weibo
//		HashSet<Weibo> weibos=u.weiboList;
//		//classify each weibo
//		int typeNum=typeMap.size();
//		TreeMap<Integer,Integer> typeCount=new TreeMap<>();
//		typeMap.each{
//			typeCount.put(it.key,0);
//		}
//		//load model first
//		Trainer.getMatrix();
//		for(Weibo w in weibos){
//			String type=Classifier.classify(w.content);
//			//tag the weibo and user
//			w.addTag(type);
//			u.addTag(type);
//
//			//map the type into integer
//			if(!typeMap.containsValue(type)){
//
//				println "Type is not found in TypeMap!";
//				throw new TypeNotFoundException();
//			}
//			//linear search for index
//			int index
//			for(def e in typeMap){
//				if(e.value==type)
//					index=e.key;
//			}
//
//			typeCount[index]+=1;
//		}
//		//generate type coverage
//		int all=weibos.size();
//		double[][] coverage=new double[1][typeNum];
//		for(int i =0;i<typeNum;i++){
//			coverage[0][i]=typeCount[i+1]/(double)all;
//		}
//		//output vector
//		Matrix X=new Matrix(coverage,1,typeNum);
//		return X;
//
//	}
//	public static void userToJson(String name){
//		//initialize the user
//		PinPoint p=new PinPoint();
//		User u=p.init(name);
//
//		//compute user vector
//		Matrix result=UserTransformer.scanUser(u);
//
//		//output coverage
//		//result.print(32,2);
//
//		//find users close to this user
//		HashSet<String> c=new HashSet<>();
//		u.tags.each{
//			c.add(it);
//		}
//		
//		Map<String,Map> relationMap=u.formNetwork(c);
//		
//
//		//Output to json
//		File output=new File(base+"userFiles\\${u.name}.json");
//		u.writeJson(output);
//	}
//
//	public static void main(String[] args){
//		
//		//start the whole program and load all users
//		//Bootstrap.start("1");
//		Bootstrap.start("2");
//
//		File log=new File("D:/log");
//		//		def s=System.out;
//		//		System.setOut(new PrintStream(new FileOutputStream(log)));
//		//make new user
//		println "${UserRepo.users.size()} users available";
//		println "${UserRepo.userNames.size()} as backup";
//
//		//		ArrayList<User> U=new ArrayList<>();
//		//		U.addAll(UserRepo.users);
//		//
//		//		String name=U.get(1).name;
//		//		println "Target ${name}";
//		//		User u=U.get(7);
//		//
//		//
//		//		//compute
//		//		Trainer.readFeatures();
//		//		Classifier.load();
//		//		UserTransformer.load();
//		//		Matrix result=UserTransformer.scanUser(u);
//		//		//output coverage
//		//		result.print(32,2);
//		//
//		//		def r=UserRepo.findSimilar(u);
//		//		println r;
//		//UserRepo.writeJson();
//
//		//		System.setOut(s);
//	}
//}
