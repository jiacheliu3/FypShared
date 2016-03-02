package vectorization

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import groovy.time.*;
import gro.crawler.*;
import Jama.Matrix;
import search.*;
import exceptions.*;
import svm.*;


class Trainer {

	public static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\resources\\";
	public static String train="D:\\weibo\\train\\";
	public static String test="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\test\\";
	public static String cv="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\cv\\";
	public static String feature="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\feature\\";

	public static String libLinear="D:\\weibo\\liblinear-2.1\\windows\\";


	public static Matrix F;

	public static HashMap<File,Integer> trainingSet=new HashMap<>();
	public static TreeMap<Integer,String> features=new TreeMap<>(); 
	static {
		readFeatures();
		getMatrix();
	}

	public static Matrix getMatrix(){
		if(F==null){
			println "Matrix is not initialized!";
			String modelPath=libLinear+"train.s.model";
			File model=new File(modelPath);
			F=readWeight(model);
		}
		return F
	};
	public static void addFolder(File folder,Integer label){
		if(!folder.isDirectory()){
			println "Not a folder!";
			throw new FileNotFoundException("Folder expected!");
		}
		folder.eachFile{
			addMaterial(it,label);
		}
	}
	public static void addMaterial(File file,Integer label){
		trainingSet.put(file,label);
	}
	public static void readFeatures(){
		File input=new File(feature+"features.txt");
		ArrayList<String> content=input.readLines();
		content.each{
			String[] s=it.split(" ");
			features.put(Integer.parseInt(s[0]),s[1]);
		}
		println "${features.size()} features read.";
	}
	public static void train(){
		File output=new File(train+"train.s");
		//extract features from training set
		trainingSet.each{
			output.append("${it.value}");
			TreeMap<Integer,Double> X=transform(it.key);


			for(def e in X){
				output.append(" ${e.key}:${e.value}")
			}
			output.append("\n");
		}

		//svm reads vectors
		//callSVM();
		//optimize();
		callLinear();
	}
	public static callLinear(){
		def timeStart = new Date();
		println "Calling liblinear.";

		String line = libLinear+"train -s 2 -v 5 -C "+train+"train.s";
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(line);
		p.waitFor();
		BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String holder;
		String content;
		while ((holder = b.readLine()) != null) {
			content+=holder;
		}
		b.close();

		//extract C from model
		int cPos=content.indexOf("Best C = ");
		int accPos=content.indexOf("CV accuracy");
		String cValue=content.substring(cPos+8,accPos).trim();
		String accValue=content.substring(accPos+14).trim();

		println "Best C is ${cValue}, accuracy is ${accValue}";

		//get the model by c value
		double c=Double.parseDouble(cValue);
		String line2 = libLinear+"train -s 2 -c ${c} "+train+"train.s";
		Runtime r2 = Runtime.getRuntime();
		Process p2 = r.exec(line2);
		p2.waitFor();
		BufferedReader b2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));

		String holder2;
		String result;
		while ((holder2 = b2.readLine()) != null) {
			result+=holder2;
		}
		b2.close();

		String modelPath=libLinear+"train.s.model";
		File model=new File(modelPath);
		F=readWeight(model);

		println "Model trained."





		def timeStop = new Date();
		TimeDuration duration = TimeCategory.minus(timeStop, timeStart);
		println "Total time: ${duration}";
	}
	public static Matrix readWeight(File model){
		ArrayList<String> param=model.readLines();

		//int featureNum=TypeRepo.getFeatureNum();
		int featureNum=1000;
		int typeNum=31;
		int startOfMatrix;
		for(int c in 0..20){
			if(param.get(c)!="w"){
				println param.get(c);
			}
			else{
				//check if next line is indeed content
				String nxt=param.get(c+1);
				String f=nxt.substring(0,nxt.indexOf(" "));
				if(f.matches("-?[0-9.]*")){
					println "Start of Matrix at Line ${c+1}";
					startOfMatrix=c+1;
				}

			}
		}
		ArrayList<String> raw=param.subList(startOfMatrix,param.size());

		Matrix weights=constructMatrix(raw,featureNum,typeNum);


		return weights;
	}
	public static Matrix constructMatrix(ArrayList<String> source,int m,int n){
		if(source.size()!=m){
			println "Warning! Possible mismatch in matrix row size. ${m}-row matrix is expected while source has ${source.size()}"
		}
		//need exception handling
		Matrix matrix=new Matrix(m,n,0d);
		for(int i=0;i<m;i++){
			String[] line=source.get(i).split(" ");
			if(line.length!=n){
				println "Warning! Possible mismatch in matrix column size. ${n}-column matrix is expected while source has ${line.length}"
			}
			for(int j=0;j<n;j++){
				matrix.set(i,j,Double.parseDouble(line[j]));
			}
		}
		println "${m}*${n} matrix constructed."
		return matrix;
	}
	public static callSVM(){
		// TODO Auto-generated method stub
		//Test for svm_train and svm_predict
		//svm_train:
		//    param: String[], parse result of command line parameter of svm-train
		//    return: String, the directory of modelFile
		//svm_predect:
		//    param: String[], parse result of command line parameter of svm-predict, including the modelfile
		//    return: Double, the accuracy of SVM classification
		//		String[] trainArgs = ["-s", "0", "-t", "2", train+"train.s"];//directory of training file
		//		String modelFile = SvmTrain.main(trainArgs);
		//		String[] testArgs = ["UCI-breast-cancer-test", modelFile, "UCI-breast-cancer-result"];//directory of test file, model file, result file
		//		Double accuracy = SvmPredict.main(testArgs);
		//		println("SVM Classification is done! The accuracy is " + accuracy);

		def timeStart = new Date()

		//Test for cross validation
		String[] crossValidationTrainArgs = ["-v", "5", train+"train.s"];// 10 fold cross validation
		String modelFile = SvmTrain.main(crossValidationTrainArgs);
		print("Cross validation is done! The modelFile is " + modelFile);

		def timeStop = new Date()
		TimeDuration duration = TimeCategory.minus(timeStop, timeStart)
		println "Total time: ${duration}";
	}
	public static TreeMap<Integer,Double> transform(File f){
		String content=f.text;
		TreeMap<Integer,Double> X=new TreeMap<>();

		for(def e in features){
			if(content.contains(e.value)){
				X.put(e.key,1d);
			}
		}
		println "File ${f.name} activates ${X.size()} features."

		return X;
	}
	public static optimize(){
		//initialize the var gamma and C
		ArrayList<Double> c = new ArrayList<>();
		ArrayList<Double> gamma = new ArrayList<>();
		TreeMap<Double,Param> result=new TreeMap<>(Collections.reverseOrder());

		for(int i in -3..3){
			c.add(Math.pow(2,i));
		}
		for(int j in -3..3){
			gamma.add(Math.pow(2,j));
		}

		//run each and get the accuracy
		for(Double d in c){
			for(Double g in gamma){
				String[] trainArgs = ["-v", "5", "-s", "0", "-g", g, "-c", d, train+"train.s"];
				SvmTrain.main(trainArgs);
				double rate=SvmTrain.getCVCorr();
				result.put(rate,new Param(d,g));
				println "Params C:${d} Gamma:${g} has correction rate ${rate}";
			}
		}


		//choose the best one
		Param selected = result.firstEntry().value;
		println "Best params C:${selected.c} gamma:${selected.gamma}";

	}
	public static class Param{
		double c;
		double gamma;
		Param(double c,double gamma){
			this.c=c;
			this.gamma=gamma;

		}
		@Override
		public boolean equals(Object obj){
			if(obj!=null&&obj instanceof Param){
				Param p=(Param) obj;
				return (c==p.c)&&(gamma==p/gamma);
			}
			return false;
		}
		@Override
		public int hashCode(){
			return c.hashCode()+gamma.hashCode();
		}
	}
	public static main(String[] args){
		//		File match=new File("C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\type\\types.txt");
		//		ArrayList<String> contents=match.readLines();
		//		contents.each{
		//			String[] s=it.split(" ");
		//			addFolder(new File(base+s[1]),Integer.parseInt(s[0]));
		//		}
		//		//		addFolder(new File(base+"软件"),1);
		//		//		addFolder(new File(base+"财经"),2);
		//		//		addFolder(new File(base+"健康"),3);
		//		//		addFolder(new File(base+"教育"),4);
		//		//		addFolder(new File(base+"军事"),5);
		//		//		addFolder(new File(base+"旅游"),6);
		//		//		addFolder(new File(base+"体育"),7);
		//		//addFolder(new File(base+"文化"),8);
		//
		//		readFeatures();
		//		train();
		File match=new File("C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\type\\types.txt");
		ArrayList<String> contents=match.readLines();
		contents.each{
			String[] s=it.split(" ");
			Type t=new Type();
			t.load(s[1]);
			//			addFolder(new File(base+s[1]),Integer.parseInt(s[0]));
		}

		println "${TypeRepo.types.size()} types ready";
		TypeRepo.startCompute();
		TypeRepo.finalizeFeatures();
	}
}
