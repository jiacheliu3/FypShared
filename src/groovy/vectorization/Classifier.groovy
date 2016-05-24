package vectorization

import toolkit.PathManager
import Jama.Matrix

/* The whole package is deprecated! */
class Classifier {

	static Matrix F;

	public static TreeMap<Integer,String> features;
	public static int typeNum;
	public static int featureNum;

	public static String typeHome=PathManager.classifierTypeFile;
	public static Map<Integer, String> typeMap=new HashMap<>();


	//load when read the class
	static {load();}
	public static load(){

		//load features
		if(Trainer.features==null||Trainer.features.size()==0)
			Trainer.readFeatures();
		features=Trainer.features;

		//load types
		if(typeMap.size()==0)
			readTypeMatch();

		//load parameters
		F=Trainer.getMatrix();

		//load type count and feature count
		typeNum=F.getColumnDimension();
		featureNum=F.getRowDimension();
		println "${featureNum}*${typeNum} matrix loaded in Classifier";
	}
	public static boolean isType(String s){
		for(def e in typeMap){
			if(e.value==s)
				return true;
		}
		return false;
	}
	public static String typeMatch(int i){

		String type;
		if(typeMap[i]!=null)
			type=typeMap[i];
		else
			println "Type ${i} not found"
		return type;
	}
	public static void readTypeMatch(){
		File typeMatching=new File(typeHome);
		ArrayList<String> contents=typeMatching.readLines();
		for(String s in contents){
			if(s==""||s==" ")
				continue;
			String[] x=s.split(" ");
			typeMap.put(Integer.parseInt(x[0]),x[1]);
		}
	}
	public static String classify(String weibo){
		if(weibo.length()<=10){
			println "Weibo is too short for a tag.";
			return null;
		}
		//transform string into vector
		Matrix X=new Matrix(1,featureNum,0);

		for(def e in features){
			if(weibo.contains(e.value)){
				println "Feature ${e.value} activated"
				X.set(0,e.key-1,1d);//features start with 1 while matrix starts with index 0
			}
		}
		//		//print 10 columns with precision of 2
		//		X.print(10,0);

		Matrix result=X.times(F);
		Double[][] Y = result.getArray();

		double d=-100d;
		int type=0;
		for(int i=0;i<typeNum;i++){
			if(Y[0][i]>d){
				d=Y[0][i];
				type=i+1;
			}
		}

		String r=typeMatch(type);
		println "${weibo} has been classified as type ${r}";
		return r;
	}

	public static void main(String[] args){
		Trainer.readFeatures();
		Classifier.load();
		String weibo="�����ȳ�����һ���5�� ��������������� һ���5��������Ҷ�֪�������ǳ��˵ĵ�ζƷ�����ں�����֣�������ɳ�����ȴ������һλ���ˣ������������ԣ�һ��Ҫ�Ե�3��5������������������";
		String type=Classifier.classify(weibo);
		String w1="���˹ʾ���״���飺�����ҹʾӳ����˹�֮�� Ԭ��֮�ʾ����ڳ��˰칫�������ׯ�ſڿ�Ҷ��أ�һЩʯ��������ѷ���ʱ���ʾ����滹����ɹ��ϯ�ӡ����������ϻ�ŵ�������ʱ��ס���ҦլҲ��֮";
		Classifier.classify(w1);
		String w2="����ӵ�С����칦�ܡ� ��������10��ܵ� ������ ��С�� ͨѶԱ ���� һ�����������Լ�ӵ������ĸ�Ӧ���������ڸ�Ӧ���ܻܵ�Σ�����?ȫ��Ϊ�˱���Ԥ��Ӧ�飬���Ӿ����Լ����׵ĵܵ�";
		Classifier.classify(w2);

	}
}
