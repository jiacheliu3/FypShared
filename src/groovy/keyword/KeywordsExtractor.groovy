package keyword

import static grails.async.Promises.*
import grails.async.Promise
import grails.async.PromiseList
import groovy.util.logging.Log4j
import segmentation.*
import vectorization.*
import codebigbrosub.*

@Log4j
class KeywordsExtractor {
	public static Set<String> extractKeywords(String content){
		return SepManager.getSepManager().extractKeywords(content);
	}

	public static void keywordStudy(User u){
		//only for testing purpose
		if(u==null){
			def l=User.withTransaction{User.findAll("from User as u order by u.weiboId",[max: 1]);}
			u=l[0];
			println "Found the white rat ${u}";
		}
		def weibos;

		Weibo.withTransaction{weibos=Weibo.findAll("from Weibo as w where w.ownerName=?", [u.weiboName]);}
		//get keywords in parallel
		u.keywords=parallelKeywords(u,weibos);
		println u.keywords;
		User.withTransaction {
			u.merge(flush:true);
			if(u.hasErrors())
				println u.errors;
			else{
				println "User keyword study complete";
				println "Now the user has\n Keywords: ${u.keywords}\nTags: ${u.tags}";
			}
		};
	}
	public static Map<String,Double> parallelKeywords(User u=null,Collection<Weibo> weibos){
		LinkedHashMap<String,Double> keywords=new LinkedHashMap<>();

		//decide the scope of weibo and assign tasks
		println "${weibos.size()} items to extract keywords from.";
		int siz=weibos.size();
		int processNumber;

		if(siz<10){
			processNumber=1;
		}else if(siz<=100){
			processNumber=siz/10;
		}else{
			processNumber=10;
		}
		println "${processNumber} processes to activate.";
		int eachTask=weibos.size()/processNumber;
		println "Each process has ${eachTask} weibo items to handle.";
		def missions=[];
		for(int i=0;i<processNumber;i++){

			int startIndex=i*eachTask;
			int endIndex;
			if(i==processNumber-1){
				endIndex=siz;
			}else{
				endIndex=i*eachTask+eachTask;
			}
			println "Task generated for ${startIndex} to ${endIndex}.";
			missions.add(weibos.subList(startIndex,endIndex));
		}
		Set<String> gotTags=new HashSet<>();

		//define task
		def extractTask={myWeibos->
			int scope=myWeibos.size();
			boolean approx=false;
			if(scope>=30){
				println "Too many items. Activate approximation methods.";
				approx=true;
			}
			println "${scope} weibos for this thread to handle.";
			HashMap<String,Double> myKeywords=new HashMap<>();
			Weibo.withNewSession{
				println "New weibo session.";

				int counter;
				String content;
				for(Weibo weibo:myWeibos){
					weibo=weibo.merge();
					if(weibo==null){
						println "Weibo lost in merging the session";
						continue;
					}
					if(approx){
						//create a local register for content
						String reg;
						if(weibo.isForwarded){
							reg=weibo.content+"//"+weibo.orgContent;
						}else{
							reg=weibo.content;
						}
						//just append the contents to buffer
						content=content+"\n"+reg;
						if(counter%10==0||counter==myWeibos.size()-1){
							println "Counter is ${counter}. Keywords study starts.";
							//work
							SepManager sep=SepManager.getSepManager();
							Map<String,Double> parts=sep.extractKeywords(content);
							myKeywords.putAll(parts);
							//classify each weibo
							String tag=Classifier.classify(content);
							if(tag==null){
								println "No tag for weibo "+content;
							}else{
								Tag t=Tag.find("from Tag as tg where tg.name=?",[tag]);
								if(t==null){
									println "Tag ${tag} not found";
								}else{
									gotTags.add(tag);
									weibo.tag=t;
									weibo.merge();
									if(weibo.hasErrors())
										println weibo.errors;
								}
							}
							//reset buffer
							content="";
						}
						counter++;
					}else{
						//the repost content should be attached to the original content
						String reg;
						if(weibo.isForwarded){
							reg=weibo.content+"//"+weibo.orgContent;
						}else{
							reg=weibo.content;
						}
						//println weibo.content;
						SepManager sep=SepManager.getSepManager();
						Map<String,Double> parts=sep.extractKeywords(reg);
						myKeywords.putAll(parts);
						//classify each weibo
						String tag=Classifier.classify(reg);
						if(tag==null){
							println "No tag for weibo "+reg;
						}else{
							Tag t=Tag.find("from Tag as tg where tg.name=?",[tag]);
							if(t==null){
								println "Tag ${tag} not found";
							}else{
								gotTags.add(tag);
								weibo.tag=t;
								weibo.merge();
								if(weibo.hasErrors())
									println weibo.errors;
							}
						}
					}
				}//end of for loop
			}
			println "Final keywords from this thread are:"+myKeywords;
			return myKeywords;
		}

		//user promises
		Collection results;
		PromiseList promises=new PromiseList();
		println "${missions.size()} tasks to assign to threads.";
		missions.collect{myTask->
			Promise p=task{extractTask(myTask)}
			if(p==null)
				println "Promise is null!";
			promises.add(p);
		}
		promises.onError{Throwable t->
			println "Got error in parallel keyword extraction.";
			println t.message;

		}
		results=promises.get();


		//report the tags and keywords
		println "Tags found:"+gotTags;
		println "Keywords found:"+results;

		//add the tags to user
		gotTags.each{tagName->
			Tag t;
			Tag.withTransaction{t=Tag.find("from Tag as tg where tg.name=?",[tagName]);}
			if(u!=null){
				if(u.tags==null){
					u.tags=[t];
				}else{
					u.tags.add(t);
				}
			}
		}

		results.each{keywords.putAll(it)}

		keywords=keywords.sort{ a, b -> b.value <=> a.value }
		//just keep 100 keywords at most
		int maxKeywords=100;
		LinkedHashMap<String,Double> chosenKeywords=new LinkedHashMap<>();
		int s=0;
		for(def e:keywords){
			s++;
			if(s>maxKeywords){
				println "Got ${maxKeywords} keywords already.";
				break;
			}
			chosenKeywords.put(e.key,e.value);

		}
		return chosenKeywords;

	}
	public static Map<String,Double> getWorkResults(Collection<Weibo> weibos){
		HashMap<String,Double> keywords=new HashMap<>();
		weibos.each{
			println it.content;
			SepManager sep=SepManager.getSepManager();
			Map<String,Double> parts=sep.extractKeywords(it.content);
			keywords.putAll(parts);
			//classify each weibo
			String tag=Classifier.classify(it.content);
			if(tag==null){
				println "No tag for weibo "+it.content;
			}else{
				//println "tagged as ${tag}";
				Tag t=Tag.find("from Tag as tg where tg.name=?",[tag]);
				if(t==null){
					println "Tag ${tag} not found";
				}else{
					it.tag=t;
					it.merge();
					if(it.hasErrors())
						println it.errors;
					else{
						//println "Weibo tagged as ${tag}";
					}
					u.tags.add(t);

				}
			}
		}
		return keywords;
	}


	//obsolete
	public static Map<String,Double> scale(Map<String,Double> keywords){
		println "Scale start"
		double min=50d;
		double max=100d;
		//find the scope of original scope
		def entryList =	new ArrayList<Map.Entry<String, Integer>>(keywords.entrySet());
		def firstEntry=entryList.get(0);
		double b=firstEntry.value;
		def lastEntry =	entryList.get(entryList.size()-1);
		double a=lastEntry.value;


		keywords.each{key,value->
			value=(value-a)*(max-min)/(b-a)+min;
			println "scaled to ${value}"
		}
		println "Scale complete"
		//println keywords;
		return keywords;
	}

	public static List<String> getTopKeywords(def raw,int k){
		Map<String,Integer> weight=new LinkedHashMap<>();
		//raw can be a list or list of list
		if(raw.size()==0){
			log.error "You have passed sth that is not a list into KeywordExtractor!";
			return null;
		}
		def a=raw[0];
		// raw is a nested array
		if(isCollectionOrArray(a)){
			raw.each{arr->
				arr.each{word->
					if(weight.containsKey(word)){
						weight[word]=weight[word]+1;
					}else{
						weight.put(word,1);
					}
				}
			}
		}
		// raw is a list of strings
		else{
			raw.each{word->
				if(weight.containsKey(word)){
					weight[word]=weight[word]+1;
				}else{
					weight.put(word,1);
				}
			}
		}
		//get topk
		int counter=0;
		def result=[];
		for(def e:weight){
			counter++;
			if(counter>k)
				break;
			result.add(e.key);

		}
		return result;
	}
	public static boolean isCollectionOrArray(object) {
		[Collection, Object[]].any { it.isAssignableFrom(object.getClass()) }
	}
}
