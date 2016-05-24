package keyword

import static grails.async.Promises.*
import static toolkit.JobLogger.jobLog
import grails.async.Promise
import grails.async.PromiseList
import groovy.util.logging.Log4j
import segmentation.*
import toolkit.PathManager
import vectorization.*
import codebigbrosub.*

@Log4j
class KeywordsExtractor {
	public static Set<String> extractKeywords(String content){
		return SepManager.getSepManager().extractKeywords(content);
	}

	public static void keywordStudy(User u,Job job){
		//only for testing purpose
		if(u==null){
			log.error "A NULL user is passed to keyword study!";
			jobLog(job.id,"The user is null!");
			return;
		}
		def weibos;
		Weibo.withTransaction{weibos=Weibo.findAll("from Weibo as w where w.ownerName=?", [u.weiboName]);}
		if(weibos==null||weibos.size()==0){
			log.info "No weibo found for user. Keyword study finished.";
			jobLog("The user has no microblog known in the database so no result from keyword study. You should activate the crawler to collect them first.");
			return;
		}
		log.info "Studying user keywords based on ${weibos.size()} weibo items.";
		jobLog(job.id,"Extracting keywords from ${weibos.size()} microblogs of the user.");
		//check if there are existing results to use
		def leftover=u.keywords;
		boolean needed=true;
		//there are some
		if(leftover!=null&&leftover.size()>0){
			int basedOn=u.keywordsBasedOn;
			if(basedOn!=null){
				//compare the study last time and this time
				if(basedOn==weibos.size()){
					log.info "The keyword study last time has already stored the keyword on exactly the same amount of microblogs. No need to go for this one.";
					needed=false;
				}else{
					log.info "The stored keywords were based on a different size of items. Last time: ${basedOn}, this time: ${weibos.size()}";
					needed=true;
				}
			}
		}

		//get keywords in parallel, if only necessary
		if(needed){
			def keywords=parallelKeywords(u,weibos);
			User.withTransaction {
				log.debug "Saving keywords to user.";
				u.keywords=keywords;
				u.keywordsBasedOn=weibos.size();
				u.merge(flush:true);
				if(u.hasErrors()){
					log.error "An error occurred saving keywords for user.";
					log.error u.errors;
				}else{
					log.info "User keyword study complete";
					log.info "Now the user has\n Keywords: ${u.keywords}\n";
				}
			};
		}
		jobLog(job.id,"Keyword extraction is finished.");
	}
	public static Map<String,Double> parallelKeywords(User u=null,Collection<Weibo> weibos){
		LinkedHashMap<String,Double> keywords=new LinkedHashMap<>();

		//decide the scope of weibo and assign tasks
		log.info "${weibos.size()} items to extract keywords in parallel.";
		int siz=weibos.size();
		int processNumber;

		if(siz<10){
			processNumber=1;
		}else if(siz<=100){
			processNumber=2;
		}else{
			processNumber=siz/100;
		}
		log.info "${processNumber} processes to activate.";
		int eachTask=weibos.size()/processNumber;
		log.info "Each process has ${eachTask} weibo items to handle.";
		def missions=[];
		for(int i=0;i<processNumber;i++){

			int startIndex=i*eachTask;
			int endIndex;
			if(i==processNumber-1){
				endIndex=siz;
			}else{
				endIndex=i*eachTask+eachTask;
			}
			log.info "Task generated for ${startIndex} to ${endIndex}.";
			missions.add(weibos.subList(startIndex,endIndex));
		}
		Set<String> gotTags=new HashSet<>();
		/* keyword extraction in batch, added in v1.2.3 */
		def newExtraction={myWeibos->
			def theKeywords=SepManager.getSepManager().batchKeywords(myWeibos);
			log.debug "The SepManager return ${theKeywords.size()} keywords.";
			return theKeywords;
		}

		/* deprecated */
		//define task
		def extractTask={myWeibos->
			int scope=myWeibos.size();
			boolean approx=false;
			if(scope>=30){
				log.info "Too many items. Activate approximation methods.";
				approx=true;
			}
			log.info "${scope} weibos for this thread to handle.";
			HashMap<String,Double> myKeywords=new HashMap<>();
			Weibo.withNewSession{
				log.info "New weibo session.";

				int counter;
				String content;
				for(Weibo weibo:myWeibos){
					weibo=weibo.merge();
					if(weibo==null){
						log.error "Weibo lost in merging the session";
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
							log.info "Counter is ${counter}. Keywords study starts.";
							//work
							SepManager sep=SepManager.getSepManager();
							Map<String,Double> parts=sep.extractKeywords(content);
							myKeywords.putAll(parts);
							//							//classify each weibo
							//							String tag=Classifier.classify(content);
							//							if(tag==null){
							//								log.info "No tag for weibo "+content;
							//							}else{
							//								Tag t=Tag.find("from Tag as tg where tg.name=?",[tag]);
							//								if(t==null){
							//									log.error "Tag ${tag} not found";
							//								}else{
							//									gotTags.add(tag);
							//									weibo.tag=t;
							//									weibo.merge();
							//									if(weibo.hasErrors()){
							//										log.error "An error occurred saving tags to weibo ${weibo}";
							//										log.error weibo.errors;
							//									}
							//								}
							//							}
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
						SepManager sep=SepManager.getSepManager();
						Map<String,Double> parts=sep.extractKeywords(reg);
						myKeywords.putAll(parts);
						//						//classify each weibo
						//						String tag=Classifier.classify(reg);
						//						if(tag==null){
						//							log.info "No tag for weibo "+reg;
						//						}else{
						//							Tag t=Tag.find("from Tag as tg where tg.name=?",[tag]);
						//							if(t==null){
						//								log.error "Tag ${tag} not found";
						//							}else{
						//								gotTags.add(tag);
						//								weibo.tag=t;
						//								weibo.merge();
						//								if(weibo.hasErrors()){
						//									log.error "An error occurred merging tag to reposted weibo ${weibo}";
						//									log.error weibo.errors;
						//								}
						//							}
						//						}
					}
				}//end of for loop
			}
			log.info "Final keywords from this thread are:"+myKeywords;
			return myKeywords;
		}

		//user promises
		Collection results;
		PromiseList promises=new PromiseList();
		log.info "${missions.size()} tasks to assign to threads.";
		missions.collect{myTask->
			Promise p=task{newExtraction(myTask)}
			if(p==null)
				log.error "Promise is null!";
			promises.add(p);
		}
		promises.onError{Throwable t->
			log.error "Got error in parallel keyword extraction.";
			t.printStackTrace();

		}
		results=promises.get();


		/* Deprecated: no more tags needed */
		//		//report the tags and keywords
		//		log.info "Tags found:"+gotTags;
		//		log.info "Keywords found:"+results;
		//
		//		//add the tags to user
		//		gotTags.each{tagName->
		//			Tag t;
		//			Tag.withTransaction{t=Tag.find("from Tag as tg where tg.name=?",[tagName]);}
		//			if(u!=null){
		//				if(u.tags==null){
		//					u.tags=[t];
		//				}else{
		//					u.tags.add(t);
		//				}
		//			}
		//		}

		results.each{keywords.putAll(it)}

		keywords=keywords.sort{ a, b -> b.value <=> a.value }
		//just keep 100 keywords at most
		int maxKeywords=100;
		LinkedHashMap<String,Double> chosenKeywords=new LinkedHashMap<>();
		int s=0;
		for(def e:keywords){
			s++;
			if(s>maxKeywords){
				log.info "Got ${maxKeywords} keywords already.";
				break;
			}
			chosenKeywords.put(e.key,e.value);

		}
		return chosenKeywords;

	}
	public static Map<String,Double> getWorkResults(Collection<Weibo> weibos){
		HashMap<String,Double> keywords=new HashMap<>();
		weibos.each{
			log.debug it.content;
			SepManager sep=SepManager.getSepManager();
			Map<String,Double> parts=sep.extractKeywords(it.content);
			keywords.putAll(parts);
			//classify each weibo
			String tag=Classifier.classify(it.content);
			if(tag==null){
				log.debug "No tag for weibo "+it.content;
			}else{
				//println "tagged as ${tag}";
				Tag t=Tag.find("from Tag as tg where tg.name=?",[tag]);
				if(t==null){
					log.error "Tag ${tag} not found";
				}else{
					it.tag=t;
					it.merge();
					if(it.hasErrors())
						log.error it.errors;
					else{
						//println "Weibo tagged as ${tag}";
					}
					//u.tags.add(t);

				}
			}
		}
		return keywords;
	}


	//obsolete
	public static Map<String,Double> scale(Map<String,Double> keywords){
		log.debug "Scale start"
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
			log.info "scaled to ${value}"
		}
		log.debug "Scale complete"

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
