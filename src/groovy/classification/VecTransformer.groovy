package classification

import groovy.util.logging.Log4j
import input.FileVisitor
import toolkit.PathManager

@Log4j
class VecTransformer {
	static Map<String,Integer> featureToIndex;
	static Map<Integer,String> indexToLabel;

	public static void loadFeatures(){
		indexToLabel=new TreeMap<>();
		//load the feature file
		featureToIndex=FeatureSelector.loadFeatures();
	}
	public static List<Double> convertVec(String content){
		if(featureToIndex==null){
			log.debug "Need to initialize the features.";
			loadFeatures();
		}
		//map to features
		int siz=featureToIndex.size();
		//println "Initialize a vector of ${siz} dimensions.";
		double[] vec=new double[siz+1];
		boolean seen=false;
		featureToIndex.each{k,v->
			if(content.contains(k)){
				int index=v-1;
				vec[index]=1.0;
				seen=true;
			}
		}

		//set the label
		//vec[siz]=label+0.0;
		vec[siz]=1.0;

		if(seen==false){
			return null
		}
		return vec as List<Double>;
	}
	public static List<Double> convertOneFile(File input,int label){
		String content=input.getText('UTF-8');
		//initialize if not done
		if(featureToIndex==null){
			println "Need to initialize the features.";
			loadFeatures();
		}
		def vector=convertVec(content);
		return vector;
	}
	//for a directory, assign a class label
	public static convertDir(File folder,int label){
		if(!folder.isDirectory()){
			println "The file assigned to ${label} is not a folder!";
			return;
		}
		def files=folder.listFiles();
		println "Assigning ${files.size()} files to label ${label}";

		def matrix=[];
		for(int i=0;i<files.size();i++){
			File file=files[i];
			def vec=convertOneFile(file,label);
			if(vec==null){
				log.debug "This file does not contain any known words.";
			}else{
				//println "Adding vector of length ${vec.size()}";
				matrix.add(vec);
			}
			if(i>0&&i%100==0){
				println "${i}th file converted. Flush.";
				outputToCsv(matrix,label);
				matrix.clear();
			}
		}
		log.debug "Last flush with ${matrix.size()} rows.";
		outputToCsv(matrix,label);
		//		//check the conformity of all vectors in the matrix
		//		int siz=matrix[0].size();
		//		println "The matrix is supposed to have a dimension of ${siz}";
		//		for(int i=0;i<matrix.size();i++){
		//			def row=matrix[i];
		//			if(row.size()!=siz){
		//				println "Row ${i} has size ${row.size()}!";
		//			}
		//		}
		//

	}
	public static outputToCsv(List matrix,int label){//a list of list<double>
		String outputBase=PathManager.csvOutputBase;
		File outputFolder=new File(outputBase);
		if(!outputFolder.isDirectory()){
			log.error "Output is not a folder!";
			return;
		}
		//decide the file to flush to
		String filePath=outputBase+indexToLabel[label];
		log.debug "Will output to file ${filePath}";
		File output=new File(filePath);
		if(!output.exists()){
			output.createNewFile();
		}

		matrix.each{row->
			OutputManager.outputSparseRow(row,label,output);
		}


	}
	public static assignLabels(String allClasses){
		def files=FileVisitor.readDir(allClasses);
		int label=1;
		println "${files.size()} labels in total.";
		for(int i=0;i<files.size();i++){
			println "Assigning label ${label}";
			File folder=files[i];
			//get the class name and output
			String fullName=folder.getCanonicalPath();
			//replace things before the last part of path
			int nameStart=fullName.lastIndexOf('\\');
			if(nameStart==-1)
				nameStart=fullName.lastIndexOf('/');
			String lastName=fullName.substring(nameStart+1);
			println "Full name is ${lastName}";
			indexToLabel.put(label,lastName);
			//start converting
			convertDir(folder,label);


			label++;

		}
		//output the labels
		println "Assigned ${indexToLabel.size()} labels.";
		File labels=new File(PathManager.csvOutputBase+"labels.txt");
		indexToLabel.each{k,v->
			labels.append(k+","+v+"\n");

		}
	}
	public static void main(String[] args){
		//		File f=new File("C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\resources\\动漫\\www58dmcom#week3.txt");
		//		convertOneFile(f,0);
		//FeatureSelector.readFeatures();
		loadFeatures();
		assignLabels("C:/Users/jiacheliu3/git/projects/CodeBigBro/data/resources");
	}
}
