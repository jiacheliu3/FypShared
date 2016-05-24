package classification

import groovy.util.logging.Log4j

@Log4j
class OutputManager {
	//the vector is a list of double itself
	public static outputSparseRow(List vector,int label,File target){
		if(label==null){
			label=-1;
		}
		if(label==-1){
			//log.debug "This group of vectors have no label.";	
		}
		String content=label+" ";
		for(int i=0;i<vector.size();i++){
			double number=vector[i];
			//index starts from 1
			if(number!=0.0){
				int index=i+1;
				content=content+" "+index+":"+number;
			}
		}
		//target.append(content+"\n",'utf-8');
		target.append(content+"\n",'ascii');
	}
}
