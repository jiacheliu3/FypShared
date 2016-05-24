package crawler

import groovy.util.logging.Log4j

import org.jsoup.nodes.Document

import toolkit.ConverterManager
import toolkit.DocumentChecker
import toolkit.UrlConverter
import codebigbrosub.User

@Log4j
class UserManager {

	static Map<String,Map> buffer=new TreeMap<>();
	public static User generateUser(def data){
		User u;

		//check the user id and url
		String uname=data["userName"];
		String url=data["userUrl"];
		String uid=data["userId"];
		log.info "Creating user with fields: Name ${uname}, ID ${uid}, Url ${url}";
		
		//create new user
		u=new User();
		u.weiboName=uname;
		u.weiboId=uid;
		u.url=url;
		u.faceUrl=data["faceUrl"];
		boolean hasDup=checkDup(u);
		if(hasDup){
			u=retrieveDup(u);
		}else{
			boolean saved=saveUser(u);
			log.info "The user's save status: ${saved}";
		}
		return u;
	}
	public static String generateUrlFromId(String uid){
		def candidates=["http://weibo.cn/"+uid,"http://weibo.cn/u/"+uid];
		String url;
		Document doc;
		boolean gotIt=false;
		for(int i=0;i<candidates.size()&&gotIt==false;i++){
			doc=PageRetriever.fetchPage(candidates[i]);
			String foundId=DocumentChecker.getUserIdFromPage(doc);
			if(foundId==uid){
				log.info "Validated this page is just for the user ${uid}.";
				gotIt=true;
				url=candidates[i];	
			}
		}
		if(url==null){
			log.error "No user url generated from id ${uid}!";
		}
		
		return url;
	}
	public static String generateIdFromUrl(String url){
		log.debug "Retrieving id from url ${url}";
		//verify url
		url=UrlConverter.fillUrl(url);
		String uid;
		Document doc;
		boolean gotIt=false;
		doc=PageRetriever.fetchPage(url);
		
		//extract id from the page
		uid=DocumentChecker.getUserIdFromPage(doc);
		if(uid==null){
			log.error "No user id is retrieved from url ${url}";
			return null;
		}
		log.info "Generated user id ${uid} from url ${url}";
		return uid;
		
	}
	public static User retrieveDup(User u){
		User v;
		boolean hasFound=false;
		User.withTransaction{
			//if an existing user is there, return true
			def uid=u.weiboId;
			def url=u.url;
			def uname=u.weiboName;
			if(uname!=null){
				v=User.find("from User as u where u.weiboName=:wName",[wName:uname]);
				if(v!=null)
					hasFound=true;
			}
			else if(uid!=null&&hasFound==false){
				v=User.find("from User as u where u.weiboId=:wId",[wId:uid]);
				if(v!=null)
					hasFound=true;
			}
			else if(url!=null&&hasFound==false){
				v=User.find("from User as u where u.url=:wUrl",[wUrl:url]);
				if(v!=null)
					hasFound=true;
			}
		}
		if(hasFound){
			log.info "Found existing user.";
			return v;
		}else{
			log.error "The user is not even there!";
			return null;
		}
	}
	public static boolean checkDup(User u){
		boolean hasDup=false;
		User.withTransaction{
			//if an existing user is there, return true
			def uid=u.weiboId;
			def url=u.url;
			def uname=u.weiboName;
			User v;
			if(uname!=null){
				v=User.find("from User as u where u.weiboName=:wName",[wName:uname]);
				if(v!=null)
					hasDup=true;
			}
			if(uid!=null){
				v=User.find("from User as u where u.weiboId=:wId",[wId:uid]);
				if(v!=null)
					hasDup=true;
			}
			if(url!=null){
				v=User.find("from User as u where u.url=:wUrl",[wUrl:url]);
				if(v!=null)
					hasDup=true;
			}
			if(hasDup){
				log.info "Found existing user.";
				return true;
			}
		}
		log.info "Are there a duplicate existing user? ${hasDup}";
		return hasDup;
	}
	public static User papersPlease(User u){
		def url=u.url;
		def uid=u.weiboId;
		def uname=u.weiboName;
		if(url==null){
			url=UrlConverter.fillUrlById(uid);
			log.info "Generated url: ${url}";
			u.url=url;
		}
		return u;
	}
	public static boolean saveUser(User u){
		boolean saved;
		try{
			User.withTransaction{
				u.merge(flush:true);
				if(u.hasErrors()){
					log.error "Error saving user: "+u.errors;
					saved=false;
				}else{
					saved=true;
					log.debug "User is saved.";
				}
			}
		}catch(Exception e){
			log.error "An exception occurred saving user ${u}!";
			e.printStackTrace();
			saved=false;
		}
		return saved;
	}
	//when retrieving the user, saving what's related to the user in the buffer by the way
	public static User retrieveUser(String name){
		//check if anything in the buffer related to the name
		def stuff;
		boolean needSave=false;
		if(buffer.containsKey(name)){
			stuff=buffer[name];
			needSave=true;
			log.debug "User ${name} is called for. Save the buffer with him by the way.";
		}

		User u;
		User.withTransaction{
			u=User.find("from User as u where u.weiboName=?",[name]);
			log.debug "The retrieved user is ${u}";
			if(u==null){
				log.error "User ${name} is not found!";
			}else{
				if(needSave){
					//save the stuff to this user
					/* stuff is in the shape of 
					 * {
					 * 	forwarding:{tom:3}
					 * }
					 * */
					stuff.each{relation,countMap->
						Map<String,String> converted=ConverterManager.integerMapToStringMap(countMap);//convert to string map for the sake of persistence
						log.info "Saving to user ${name}'s ${relation}: "+converted;
						u[relation]=converted;
					}

					//save the user object
					boolean saved=saveUser(u);
					if(saved==false){
						log.error "The other side ${name} is not updated!";
					}
				}
			}
		}
		return u;
	}
	/* 
	 * Update the user's relations like commenting etc to the other side.
	 * Strategy: store the change in buffer, update when the other party is needed.
	 * Target: Reduce the number of transactions.
	 * 
	 * */
	public static void storeChangeInBuffer(User u){
		if(u==null){
			log.error "Cannot store empty user in buffer.";
		}
		if(buffer==null){
			buffer=new TreeMap<>();
		}
		//store the users interactions in buffer accordingly
		String userName=u.weiboName;
		log.info "Storing user ${userName}'s interactions into buffer.";
		//convert the interaction to the other side
		theOtherSide("forwarded",userName,u.forwarding);
		theOtherSide("forwarding",userName,u.forwarded);
		theOtherSide("commented",userName,u.commenting);
		theOtherSide("commenting",userName,u.commented);
		theOtherSide("liked",userName,u.liking);
		theOtherSide("liking",userName,u.liked);
		theOtherSide("mentioned",userName,u.mentioning);
		theOtherSide("mentioning",userName,u.mentioned);

		log.info "User ${userName}'s interactions in buffer.";
	}
	//for all interactions from one kind of interaction, eg. reposting, save to the reposted interaction of the other party
	public static void theOtherSide(String relation,String targetUser,Map<String,String> interactions){//targetUser is the user the whole study is about
		log.info "Convert interaction to the other party's ${relation}.";
		//convert string-string count map to string-integer map
		def converted=ConverterManager.stringMapToIntegerMap(interactions);
		converted.each{k,v->
			storeToBuffer(k,targetUser,relation,v);
		}
		log.info "Saved user's interactions to buffer.";

	}
	public static void storeToBuffer(String name,String targetUser,String relation,int value){//targetUser should be the user that the whole study is about
		/* Sample buffer structure:
		 * {
		 *   tom:{
		 *   	forwarding:{
		 *   	  jerry:3
		 *   	}
		 *   }
		 * }*/
		//merge an entry to a map
		def mergeMap={map,k,v->
			if(map.containsKey(k)){
				map[k]+=v;
			}else{
				map.put(k,v);
			}
		}
		if(buffer.containsKey(name)){
			def map=buffer[name];//map is a nested map like {forwarding:{k:v},...}
			if(map.containsKey(relation)){
				mergeMap(map[relation],targetUser,value);
			}else{
				//create a new map and merge it
				def relationMap=[:];
				mergeMap(relationMap,targetUser,value);
				map.put(relation,relationMap);
			}
		}else{
			def map=[:];
			def relationMap=[:];
			mergeMap(relationMap,targetUser,value);
			map.put(relation,relationMap);
			buffer.put(name,map);
		}
	}
	public static boolean validateUser(User u,Map data){
		log.info "Validating user fields based on crawler results.";
		def url=data["url"];

	}
}
