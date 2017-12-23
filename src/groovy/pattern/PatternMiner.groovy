package pattern

import groovy.util.logging.Log4j
import keyword.KeywordsExtractor
import segmentation.SepManager
import codebigbrosub.Weibo
import toolkit.PathManager
import java.util.Random;

@Log4j
class PatternMiner {

	public static int counter=0;

	private Map<Weibo,List> items;
	public storeInteractions(Map<Weibo,List> interactions){
		items=interactions;
		log.info "Pattern miner got ${items.size()} items for studying. ";
	}
	public Collection<SubPattern> studyPatterns(String userName){
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
        Random rand=new Random();
		int i=rand.nextInt();
//		String path="D:/pattern/${i}.txt";
		String path=PathManager.patternBasePath+"/pattern/${i}.txt";
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
		int threshold=0.05*items.size();
		tree.setThreshold(threshold);
		log.info "Setting minimal support to ${threshold}.";

		//call fp tree miner to get patterns
		Map<Set<String>,Long> result=tree.mine(path);//Map<Set<String>,Long>
		log.info "The mining result is "+result;
		if(result==null){
			log.error "No patterns found. ";
			def l=[];
			return l;
		}
		//filter the found patterns
		/* Filter rules:
		 * filter those with can be merged with larger patterns
		 * 
		 * */
		//find the largest
		log.info "Cleaning up the overlapped patterns in the results, only keeping the largest one.";
		Map kept=new HashMap<Set,Long>();
		while(result.size()>0){
			//find the largest
			Set largest=new HashSet<String>();
			result.each{set,count->
				if(set.size()>largest.size())
					largest=set;
			}
			kept.put(largest,result[largest]);
			result.remove(largest);
			//move it to the keep list and make sure no other one is overlapped in the set
			def toRemove=[];
			result.each{set,count->
				if(largest.containsAll(set))
					toRemove.add(set);
			}
			toRemove.each{
				result.remove(it);
			}
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
		
		//filter out patterns with only 1 user
		def uShouldGo=[];
		kept.each{names,support->
			if(names==null){
				uShouldGo.add(names);
			}
			else if(names.size()==1){
				uShouldGo.add(names);
			}
			else if(names.size()==2&&names.contains(userName)){
				log.debug "The pattern ${names} contains the target user himself. Remove from results.";
				uShouldGo.add(names);
			}
		}
		uShouldGo.each{
			kept.remove(it);
		}
		log.debug "All patterns that are too small are filtered.";
		
		//map patterns back to weibo, generate udf objects
		def patterns=[];
		kept.each{patNames,patSupport->
			SubPattern s=new SubPattern();
			s.support=(int)patSupport;
			s.names=patNames;
			//find all weibo that has this pattern
			List<Weibo> theList=new ArrayList<>();
			items.each{weibo,nameList->
				Set<String> set=new HashSet<>();
				set.addAll(nameList);
				if(set.containsAll(patNames)){
					theList.add(weibo);
				}
			}
			//s.weibos=theList;
			s.studyKeywords(theList);
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
			/* Deprecated */
			//			weibos.each{
			//				String content="";
			//				if(it.isForwarded){
			//					content=it.content+"//"+it.orgContent;
			//				}else{
			//					content=it.content;
			//				}
			//				strings.add(content);
			//			}
			//			//get keywords
			//			List<List<String>> raw=SepManager.getSepManager().parallelSeg(strings);
			//			List<String> keywords=KeywordsExtractor.getTopKeywords(raw,10);
			//			this.keywords=keywords;
			//			log.info "Extracted top keywords for pattern: ${names}\n"+this.keywords;

			def keywordRaw=KeywordsExtractor.parallelKeywords(weibos);
			def sorted=keywordRaw.sort { a, b -> b.value <=> a.value };
			int count=0;
			def top=[];
			for(def e:sorted){
				if(count>=20)
					break;
				top.add(e.key);
				count++;
			}
			if(keywords==null)
				keywords=new HashSet<>();
			keywords.addAll(top);
			log.debug "Digged keywords from the subpattern: "+keywords;
		}

	}
	//for testing purpose
	public static void main(String[] args){
		def a=[1, 2, 3];
		Set set1=new HashSet();
		set1.addAll(a);

		def b=[1, 2, 3, 4];
		Set set2=new HashSet();
		set2.addAll(b);

		println set2.containsAll(set1);
	}
}
