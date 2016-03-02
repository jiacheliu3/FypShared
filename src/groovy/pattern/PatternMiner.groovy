package pattern

import groovy.util.logging.Log4j
import keyword.KeywordsExtractor
import segmentation.SepManager
import codebigbrosub.Weibo


@Log4j
class PatternMiner {

	public static int counter=0;
	
	private Map<Weibo,List> items;
	public storeInteractions(Map<Weibo,List> interactions){
		items=interactions;
		log.info "Pattern miner got ${items.size()} items for studying. ";
	}
	public Collection<SubPattern> studyPatterns(){
		//filter those useless items
		Map<Weibo,List> newItems=new HashMap<>();
		items.each{k,v->
			if(v.size()>0)
				newItems.put(k,v);
		}
		items=newItems;
		//check mining material
		if(items.size()<=1){
			log.error "Size of mining material is ${items.size()}. No patterns to be found.";
			return new HashMap<Set,Long>();
		}else{
			log.info "${items.size()} items for pattern mining.";
		}
		//output to a file
		int i=100;
		String path="D:/pattern/${i}.txt";
		File input=new File(path);
		if(input.exists()){
			log.info "File ${path} already exists. Deleting it first.";
			input.delete();
		}
		items.each{key,value->
			//output the list to a file, separated by tab
			if(value.size()>0){
				String holder="";
				for(int j=0;j<value.size();j++){
					String name=value[j];
					holder+=name;
					if(j!=value.size()-1){
						holder+='\t';
					}
				}
				input.append(holder+"\n",'utf-8');
			}
		}
		log.info "Interaction info written to file ${path} for pattern mining.";

		//initialize fp-tree
		Fptree tree=new Fptree(counter);
		counter++;
		
		//decide threshold
		int threshold=0.1*items.size();
		tree.setThreshold(threshold);
		log.info "Setting minimal support to ${threshold}.";

		//call fp tree miner to get patterns
		Map result=tree.mine(path);
		log.info "The mining result is "+result;
		if(result==null){
			log.error "No patterns found. ";
			def l=[];
			return l;
		}
		
		//count support
		Map<String,Integer> allSupport=new HashMap<>();
		for(def e:items){
			def l=e.value;
			if(l.size()==0)
				continue;
			l.each{n->
				if(allSupport.containsKey(n)){
					allSupport[n]=allSupport[n]+1;
				}else{
					allSupport.put(n,1);
				}
			}
		}
		log.info "Counted all support of interactions: "+allSupport; 
		
		//map patterns back to weibo, generate udf objects
		def patterns=[];
		result.each{patNames,patSupport->
			SubPattern s=new SubPattern();
			s.support=(int)patSupport;
			s.names=patNames;
			//find all weibo that has this pattern
			List<Weibo> theList=new ArrayList<>();
			items.each{weibo,nameList->
				Set<String> set=new HashSet<>();
				set.addAll(nameList);
				if(set.contains(patNames)){
					theList.add(weibo);
				}
			}
			//s.weibos=theList;
			s.studyKeywords();
			patterns.add(s);
		}
		log.info "Generated all matchings for patterns.";
		
		return patterns;

	}
	public static class SubPattern{
		int support;
		Set<String> names;
		Set<String> keywords;
		//extract keywords from the weibo in the pattern
		public void studyKeywords(List<Weibo> weibos){
			//generate string collection
			def strings=[];
			weibos.each{
				String content="";
				if(it.isForwarded){
					content=it.content+"//"+it.orgContent;
				}else{
					content=it.content;
				}
				strings.add(content);
			}
			//get keywords
			List<List<String>> raw=SepManager.getSepManager().parallelSeg(strings);
			List<String> keywords=KeywordsExtractor.getTopKeywords(raw,10);
			this.keywords=keywords;
			log.info "Extracted top keywords for pattern: ${names}\n"+this.keywords;
		}
		
	}
	//for testing purpose
	public static void main(String[] args){

	}
}
