
package crawler
import groovy.util.logging.Log4j
import input.Patterns

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.jsoup.Jsoup
import org.jsoup.Connection.Method
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import toolkit.ConverterManager
import toolkit.DocumentChecker
import codebigbrosub.Job
import codebigbrosub.User
import codebigbrosub.Weibo
import crawler.CrackedCookieRepo.CrackedCookie
import crawler.UserPageCrawler.UserLink
import static toolkit.JobLogger.jobLog

@Log4j
class WeiboCrawlerMaster {
	int pageCount;
	int gotPageCount=0;
	int gotCount=0;
	int totalCount;


	UserPageCrawler userPageCrawler;
	Elements foundFriends;
	
	Job job;//save a reference of job for the sake of logging
	//
	//	public void userWeiboCrawl(User u){
	//		//get target url
	//		String url=u.url;
	//		println "User's url is ${url}";
	//		//decide how many accounts are available
	//		int accounts=CrackedCookieRepo.getBotCount();
	//
	//		println "${accounts} bots available for crawling";
	//		if(accounts==0){
	//			println "No bots available. Abort.";
	//			return;
	//		}
	//		//start crawling
	//		crawlWeibo(url,accounts);
	//
	//
	//	}
	public void setJob(Job job){
		this.job=job;
		log.debug "Set job reference in WeiboCrawlerMaster";
	}
	public void crawlWeibo(def data){
		boolean gotInitPage=false;
		//get known info
		String userId=data["userId"];
		String userName=data["userName"];
		String url=data["userUrl"];
		String pageCountInfo=data["pageCount"];
		String accountCountInfo=data["botCount"];
		int pageCount;
		int accountCount;
		//first level check on user id and username
		boolean needUrl=true;
		if(userName==null||userName==""){
			log.error "User name is not provided! The crawling request cannot proceed.";
			return;
		}
		if(url==null||url==""){
			log.debug "Url is not found. That's ok I'll try to generate one.";
		}else if(url.indexOf('/')==-1){
			log.debug "Url does not really contain a / character. Try to fix it.";
		}
		if(needUrl){
			if(userId==null||userId==""){
				log.debug "Even user id is not valid. This cannot be finished.";
				return;
			}
			if(!userId.matches("\\d{5,15}")){
				log.info  "User id ${userId} is not numerical.";
				return;
			}
			url="http://weibo.cn/u/${userId}";
			log.info "User url is generated: "+url;
		}
		boolean needCheck=true;
		if(pageCount==""||pageCount==null){
			log.error "Page count is not available.";
		}else if(accountCountInfo==null||accountCountInfo==""){
			log.error "Account count is not valid";
		}else{
			pageCount=Integer.parseInt(pageCountInfo);
			accountCount=Integer.parseInt(accountCountInfo);
			needCheck=false;
		}
		//if pagecount is not available, get the first page and extract
		int retry=5;
		int timeout=3000;
		if(needCheck){
			boolean gotPage=false;
			Document doc=PageRetriever.fetchPage(url);
			if(doc==null){
				log.error "No weibo got from ${url}";
			}else{
				gotPage=true;
			}
			
			if(gotPage){
				//get basic info from the first page
				def weiboEndHolder=doc.select("input[name=mp]");
				if(weiboEndHolder.size()==0){
					//if there is no page count span in the page, there should be only one page
					pageCount=1;
					accountCount=1;
					log.info "Page count span not found";
				}else{
					//extract page count
					def weiboEnd=weiboEndHolder.get(0);
					String page=weiboEnd.attr("value");
					log.info "Total page count is ${page}";
					pageCount=Integer.parseInt(page);
					//get account count
					accountCount=CrackedCookieRepo.getBotCount();
					//each thread should have one account for crawling
					log.info "${accountCount} accounts available for crawling.";
				}
			}else{
				log.error "Crawler cannot proceed. ";
				return;
			}
		}
		//crawl in a wrapped function
		def args=["userId":userId,"userName":userName,"userUrl":url,"pageCount":pageCount,"accountCount":accountCount,"needSave":true,"fastMode":false];
		parallelCrawlWeibo(args);
		
		//user hk entrance instead
		//HKWeiboCrawlerMaster hkMaster=new HKWeiboCrawlerMaster(weiboName:userName,weiboId:userId);

		//single thread
		//		String baseUrl=url;
		//		int startIndex=0;
		//		int endIndex=pageCount;
		//		def newCookie=CrackedCookieRepo.getOneCookie();
		//		CrawlerThread crawler=new CrawlerThread(url:baseUrl,start:startIndex,end:endIndex,userId:userId,userName:userName,cookies:newCookie,retry:2,boss:this);
		//		log.info "Crawler is running.";
		//		crawler.run();

		//final sanity check on how many microblogs are collected for user
		log.info "Now that all threads are done. Sanity check from crawler master.";
		User.withTransaction{
			def weibos=Weibo.findAll("from Weibo as w where w.ownerName=?",[userName]);
			log.info "Found ${weibos?.size()} weibo that belong to user ${userName}";
			User u=User.find("from User as u where u.weiboName=?",[userName]);
			u.weibos.addAll(weibos);
			u.save(flush:true);
			if(u.hasErrors()){
				log.error "Error when add weibo to the user.";
				log.error u.errors;

			}else{
				log.info "Owner updated successfully.";
			}
		}

	}

	//study the user's friends based on their info pages
	public Map studyStats(User u,List<Weibo> weibos,Job theJob){
		boolean needCrawl=false;
		if(foundFriends==null){
			log.info "No known friends info, need to crawl the weibo elements first";
			needCrawl=true;
		}
		if(needCrawl){
			jobLog(job.id,"There are no friends known by the application. A full scan will be performed on the user's all microblog items to see who have interacted with the user.");
			log.info "Need to get the full elements from weibo.";
			getFullElement(theJob);
		}

		//pass the found friends to user page crawler
		log.info "${foundFriends?.size()} friends passed to user stats study.";
		jobLog(job.id,"Studying the information of user's interacted friends...");
		UserPageCrawler userCrawler=new UserPageCrawler();
		def stats=userCrawler.studyLinkToUsers(foundFriends);
		jobLog(job.id,"Aggregated statistics from the friends.");
		return stats;

	}
	//crawl the user weibo again
	public void getFullElement(Job theJob){
		def crawlReg=theJob.crawlReg;
		def data=this.crawlWeibo(crawlReg);
		jobLog(theJob.id,"The full scan is complete. Collected data:");
		jobLog(theJob.id,data.toString());
	}
	//check if the weibo item has passed the study on mention, comment, repost and like info. True means needing crawling.
	public Map needFurtherCrawl(Weibo w){
		String repost=w.repostElement;
		String comment=w.commentElement;
		String like=w.likeElement;
		String full=w.fullElement;
		boolean r=!(DocumentChecker.validateHtmlElement(repost));
		boolean c=!(DocumentChecker.validateHtmlElement(comment));
		boolean l=!(DocumentChecker.validateHtmlElement(like));
		boolean f=!(DocumentChecker.validateHtmlElement(full));

		def flags=["repost":r,"comment":c,"like":l,"full":f];
		return flags;
	}
	public Map crawlRCL(User u,List<Weibo> weibos){
		//store user interaction of each weibo
		Map<Weibo,ArrayList> interaction=new HashMap<>();

		//get cookies

		def cookies=CrackedCookieRepo.getOneCookie();
		int retry=5;
		int timeout=3000;
		boolean cookieIsBad=false;//whether the cookies still work

		//initiate crawler
		WeiboCrawler crawler=new WeiboCrawler(userName:u.weiboName,boss:this,needSave:false,forceSave:false,fastMode:false);
		crawler.setCookies(cookies);

		Map<String,Integer> allCommented=new HashMap<>();
		Map<String,Integer> allCommenting=new HashMap<>();
		Map<String,Integer> allForwarded=new HashMap<>();
		Map<String,Integer> allForwarding=new HashMap<>();
		Map<String,Integer> allLiked=new HashMap<>();
		Map<String,Integer> allLiking=new HashMap<>();
		Map<String,Integer> allMentioned=new HashMap<>();
		Map<String,Integer> allMentioning=new HashMap<>();
		for(int i=0;i<weibos?.size();i++){
			Weibo w=weibos[i];
			//check existing stored info first
			//if any of mention, comment, repost or like info is available from the db, it must have passed the related study
			Map needCrawl=needFurtherCrawl(w);

			//store the names mentioned in the weibo content
			HashSet<String> nameList=new HashSet<>();

			//store mention info
			def mentioning=[:];
			def mentioned=[:];
			def mentionMap=[:];

			//find forward information
			String content=w.content;
			Pattern mention=Patterns.AT.value();
			Matcher mm=mention.matcher(content);
			while(mm.find()){
				String name=mm.group();
				//remove the @ sign in forward tag
				name=name.replace("@","");

				//add to mention list
				if(!mentioning.containsKey(name)){
					mentioning.put(name,1);
				}else{
					mentioning[name]=mentioning[name]+1;
				}
			}
			mentionMap.put("mentioning",mentioning);
			mentionMap.put("mentioned",mentioned);

			//get comments
			String url=w.url;
			url=url.replace("weibo.com","weibo.cn");
			println "Going to ${url}";
			//fetch the comments for 5 times before abort
			boolean gotComment=false;
			boolean hasRepost=false;
			boolean hasLike=false;
			boolean hasComment=false;

			def commentMap=[:];
			def commenting=[:];
			def commented=[:];
			Element commentBody;
			//if no need to crawl the comments, parse from string
			if(needCrawl["comment"]==false){
				String commentElement=w.commentElement;
				commentBody=Jsoup.parse(commentElement);
			}
			else{
				Document doc=PageRetriever.fetchPage(url);
				if(doc==null){
					log.error "No comment page fetched at last!";
				}else{
					commentBody=doc.body();
					String commentString=commentBody.toString();
					
					log.debug "Got the body of comments: "+commentBody.text();
					//store the element with the weibo
					w.commentElement=commentString;
					gotComment=true;
				}
			}
			if(commentBody==null){
				log.error "The comment content of weibo is not found!";
				hasComment=false;
			}else{
				hasComment=true;
			}
			if(hasComment){
				//extract info from the comment body
				//check if the text contains number
				def hasNumber={text->
					def numbers=text.findAll( /\d+/ );
					//println "Got number ${numbers}";
					if(numbers.size()==0)
						return false;
					else if(numbers.get(0)==0)
						return false;
					else
						return true;
				}
				//check the repost and like count
				String forwardSelector="/repost";
				def forwardHolder=commentBody.select("a[href~=/repost]");
				Element forwardElement=forwardHolder.get(0);
				String forwardText=forwardElement.text();
				hasRepost=hasNumber(forwardText);

				String likeSelector="/attitude";
				def likeHolder=commentBody.select("a[href~=/attitude]");
				Element likeElement=likeHolder.get(0);
				String likeText=likeElement.text();
				hasLike=hasNumber(likeText);

				//extract comment elements
				String subquery="C_";
				Elements comments=commentBody.select("div[id^=${subquery}]");
				if(comments.size()==0){println "No comments from this one";}
				comments.each{
					String raw=it.text();
					int colon=raw.indexOf(":");
					String name=raw.substring(0,colon).trim();
					String body=raw.substring(colon+1).trim();
					println "Found comment from user ${name}";
					//add to comment list
					if(!commented.containsKey(name)){
						commented.put(name,1);
					}else{
						commented[name]=commented[name]+1;
					}
				}
				//put into map
				commentMap.put("commented",commented);
				commentMap.put("commenting",commenting);
			}

			String userId=u.weiboId;

			def forwardMap=[:];
			if(!hasRepost){
				log.info "No repost number for this weibo item.";
			}else{
				//construct the url for repost page and extract info
				String forwardUrl=url.replace("comment","repost");
				println("Made repost url "+forwardUrl);
				forwardMap=crawler.crawlRepostPage(forwardUrl,w,needCrawl["repost"]);
				//save the forward element
				if(needCrawl["repost"]==true){
					log.debug "Store the repost element in weibo.";
					w.repostElement=forwardMap["element"];//in string
				}
			}

			def likeMap=[:];
			if(!hasLike){
				log.info "No like number for this weibo item.";
			}else{
				//find likes on this weibo, count as comments from others
				String likeUrl=url.replace("comment","attitude");
				println("Made attitude url "+likeUrl);
				likeMap=crawler.crawlLikePage(likeUrl,w,needCrawl["like"]);
				//save the like element
				if(needCrawl["like"]==true){
					log.debug "Store the like element in weibo.";
					w.likeElement=likeMap["element"];
				}
			}

			//merge the maps into the name list
			if(hasComment){
				nameList.addAll(commentMap["commented"]?.keySet());
				nameList.addAll(commentMap["commenting"]?.keySet());
			}
			if(hasRepost){
				nameList.addAll(forwardMap["forwarded"]?.keySet());
				nameList.addAll(forwardMap["forwarding"]?.keySet());
			}
			if(hasLike){
				nameList.addAll(likeMap["liked"]?.keySet());
				nameList.addAll(likeMap["liking"]?.keySet());
			}
			nameList.addAll(mentionMap["mentioned"]?.keySet());
			nameList.addAll(mentionMap["mentioning"]?.keySet());

			//merge the maps into the main map
			if(hasComment){
				mergeMaps(allCommented,commentMap["commented"]);
				mergeMaps(allCommenting,commentMap["commenting"]);
			}
			if(hasRepost){
				mergeMaps(allForwarded,forwardMap["forwarded"]);
				mergeMaps(allForwarding,forwardMap["forwarding"]);
			}
			if(hasLike){
				mergeMaps(allLiked,likeMap["liked"]);
				mergeMaps(allLiking,likeMap["liking"]);
			}
			mergeMaps(allMentioned,mentionMap["mentioned"]);
			mergeMaps(allMentioning,mentionMap["mentioning"]);

			//store the namelist of interaction
			log.info "Names extracted from this weibo: "+nameList;
			interaction.put(w,nameList);

			//save the changes
			Weibo.withTransaction{
				boolean goodToSave=w.validate();
				if(!goodToSave){
					log.error "Weibo doesn't pass the validation!";
					log.error w.errors;
				}else{
					try{
						w.merge(flush:true);
						if(w.hasErrors()){
							log.error "Failed to save the html elements of weibo!";
							log.error w.errors;
						}else{
							log.debug "Weibo is merged with html elements.";
						}
					}
					catch(Exception e){
						log.error "An exception is reported saving the weibo item!";
						e.printStackTrace();
					}
				}
			}
		}
		//prepare to return
		def support=[:];
		support.put("commenting",allCommenting);
		support.put("commented",allCommented);
		support.put("forwarding",allForwarding);
		support.put("forwarded",allForwarded);
		support.put("liking",allLiking);
		support.put("liked",allLiked);
		support.put("mentioned",allMentioned);
		support.put("mentioning",allMentioning);
		support.put("interactions",interaction);
		log.info "The final support is "+support;

		//save the fields to user
		User.withTransaction{
			boolean goodToSave=u.validate();
			if(!goodToSave){
				log.error "The user object doesn't pass the validation.";
				log.error u.errors;
			}else{
				try{
					u.forwarding.putAll(ConverterManager.integerMapToStringMap(allForwarding));
					u.forwarded.putAll(ConverterManager.integerMapToStringMap(allForwarded));
					u.commenting.putAll(ConverterManager.integerMapToStringMap(allCommenting));
					u.commented.putAll(ConverterManager.integerMapToStringMap(allCommented));
					u.liking.putAll(ConverterManager.integerMapToStringMap(allLiking));
					u.liked.putAll(ConverterManager.integerMapToStringMap(allLiked));
					u.mentioning.putAll(ConverterManager.integerMapToStringMap(allMentioning));
					u.mentioned.putAll(ConverterManager.integerMapToStringMap(allMentioned));

					u.merge(flush:true);
					if(u.hasErrors()){
						log.error "Failed saving user's interactions!";
						log.error u.errors;
					}else{
						log.info "Saved user's interactions.";
					}
				}catch(Exception e){
					log.error("An exception occurred saving the user!");
					e.printStackTrace();
				}
			}
		}
		
		//check if saved
		String userName=u.weiboName;
		checkUser(userName);

		return support;
	}
	public void checkUser(String name){
		User u;
		User.withTransaction{
			def users=User.findAll("from User as u where u.weiboName=?",[name]);
			if(users.size()>1){
				log.error "Why there are more than 1 user with name ${name}!";
				log.info "The users are ${users}";
			}else if(users.size()>0){
				u=users[0];
			}else{
				log.error "The user is not found!!";
			}
			if(u==null){
				log.error "No user to study.";
				return;
			}else{
				log.info "User has:";
				log.info "Forwarding: ${u.forwarding}";
				log.info "Forwarded: ${u.forwarded}";
				log.info "Commenting: ${u.commenting}";
				log.info "Commented: ${u.commented}";
				log.info "Liking: ${u.liking}";
				log.info "Liked: ${u.liked}";
				log.info "Mentioning: ${u.mentioning}";
				log.info "Mentioned: ${u.mentioned}";
			}
		}
	}
	//merge the smaller map into a larger map as repo
	public void mergeMaps(Map bigMap,Map smallMap){
		if(smallMap==null||smallMap.size()==0){
			return;
		}
		smallMap.each{key,value->
			if(bigMap.containsKey(key)){
				bigMap[key]+=value;
			}else{
				bigMap.put(key,value);
			}
		}
	}
	public void parallelCrawlWeibo(def args){
		log.info "Got request for parallet crawling. The params are ${args}";
		//get the arguments first
		//get known info
		String userId=args["userId"];
		String userName=args["userName"];
		String url=args["userUrl"];
		String pageCountInfo=args["pageCount"];
		String accountCountInfo=args["botCount"];
		int pageCount=args["pageCount"];
		int accountCount=args["accountCount"];
		boolean needSave=args["needSave"];//whether the weibo should be saved
		boolean fastMode=args["fastMode"];

		//calculate thread count and allocate
		int threadCount=accountCount;
		//int threadCount=1;
		int threadTask=pageCount/threadCount;
		log.info "Need ${threadCount} threads. Each thread responsible for ${threadTask} pages.";

		String baseUrl=url;
		ExecutorService es = Executors.newFixedThreadPool(threadCount);
		//ExecutorService es = Executors.newCachedThreadPool();
		def threads=[];
		for(int i=0;i<threadCount;i++){
			int endIndex=i*threadTask+threadTask-1;
			if(i==threadCount-1)
				endIndex=pageCount;
			int startIndex;
			if(i==0)
				startIndex=1;
			else
				startIndex=i*threadTask;
			//assign cookies for the thread
			def newCookie=CrackedCookieRepo.getOneCookie();
			threads.add(new CrawlerThread(
					url:baseUrl,start:startIndex,end:endIndex,userId:userId,userName:userName,cookies:newCookie,retry:2,boss:this,needSave:needSave,fastMode:fastMode
					));

		}
		//es.shutdown();

		//start the thread pool
		try {
			List<Future> futures = threads.collect{theThread->
				es.submit({
					->
					theThread.run();
				});
			}
			// recommended to use following statement to ensure the execution of all tasks.
			futures.each{it.get()}
		}catch(Exception e){
			log.error "An error occurred in the thread pool!";
			e.printStackTrace();
		}
		finally {
			def notRun=es.shutdownNow();
			log.info "Now the thread pool is shut down. Still ${notRun} threads are not finished and stopped.";
		}
		return;
	}
	public void gotOneMore(){
		if(gotCount<totalCount)
			gotCount++;
		else
			log.debug "Already got ${gotCount} out of ${totalCount} weibo items. No need to increment the known count.";
	}
	public crawlUser(String url,def data){
		//def cookies=login('theia_fq@163.com','zsygpqdh');
		//def cookies=login('theia_fq@163.com','lallala');
		log.info "Crawling user with info provided: ";
		log.info "Url: ${url}";
		log.info "Data: ${data}"
		def cookies=CrackedCookieRepo.getOneCookie();
		boolean found;
		Document doc=PageRetriever.fetchPage(url);
		boolean gotUserPage=false;
		def holder;
		//get the user page for next step study
		for(int i=0;i<3&&gotUserPage==false;i++){
			if(doc==null){
				log.error "No user page is fetched.";
			}
			else{
				holder=doc.select("div[class=tip2]");
				if(holder.size()==0){
					log.error "No user info found";
					log.info doc.text();
					sleep(1000);
				}else{
					log.info "Got user page";
					gotUserPage=true;
				}
			}
		}
		if(!gotUserPage){
			log.error "No user page to extract data from, returning the old map.";
			return data;
		}
		if(gotUserPage){
			log.info "Start extracting data from user page";
			Element infoZone=holder.get(0);
			Element weiboCount=infoZone.select("span[class=tc]")?.get(0);//count of weibo
			Element follow=infoZone.select("a[href~=follow]")?.get(0);//following
			Element fans=infoZone.select("a[href~=fans]")?.get(0);//fans count
			Element group=infoZone.select("a[href~=attgroup]")?.get(0);//user grouping
			Element at=infoZone.select("a[href~=at/weibo]")?.get(0);//get @ed
			//println weiboCount,follow,fans,group,at;
			found=true;

			//massage the data found
			def completeUrl={shortElement->
				String shortUrl=shortElement.attr("href");
				if(!shortUrl.contains('http://weibo.cn'))
					shortElement.attr('href','http://weibo.cn'+shortUrl);
				shortElement;
			}

			//extract user id, name and url
			String theId;
			String theName;
			String theUrl;
			String faceUrl;
			def avatar=doc.select("a[href~=avatar]");
			if(avatar.size()==0){
				log.info "Failed to locate user avatar and id."
			}
			//otherwise extract user info from the image
			else{
				Element face=avatar.get(0);
				faceUrl=face.attr("href");
				if(!faceUrl.contains("http://weibo.cn")){
					faceUrl="http://weibo.cn"+faceUrl;
				}
				def faceUrlReg=faceUrl;//store the avatar href for id extraction
				//extract image from user's image page
				faceUrl=getImg(faceUrl);
				log.info 'face at: '+faceUrl;
				String wid=faceUrlReg.find("/\\d{5,15}/");
				if(wid!=null&&wid!=""){
					theId=wid.substring(1,wid.size()-1);
					log.info "Found user id: "+theId;
				}
				String title=doc.title();
				if(title.contains("\u7684\u5fae\u535a")){
					log.info "Got key words in title.";
					int idx=title.indexOf("\u7684\u5fae\u535a");
					String myName=title.substring(0,idx);
					theName=myName;
				}else{
					def nameHolder=doc.select("span[class=ctt]").get(0);
					log.info nameHolder;
					String nameRaw=nameHolder.text();
					String myName=nameRaw.split().get(0);
					theName=myName;
				}
				log.info "Got user name "+theName;
				//if the user id is still not initialized, try extracting it from the url
				if(theId==null||theId==""){
					theId=url.find("/\\d{5,15}/");
					log.info "No user id extracted from avatar url. Trying to extract it from the url.";
					log.info "Got user id ${theId}";
				}
				//build the url from user id
				if(theId!=null&&theId!=""){
					theUrl="http://weibo.cn/u/${theId}";
					log.info "Build the url based on user id "+theUrl;
				}
			}
			//fill the data
			if(data["userUrl"]==null)
				data.userUrl=theUrl;
			if(data["userName"]==null)
				data.userName=theName;
			if(data["userId"]==null)
				data.userId=theId;
			if(data["avatar"]==null)
				data.faceUrl=faceUrl;
			log.info "Finished user basic info extraction. Now the data is "+data;


			//weibo count needs to be extracted
			def numbers=weiboCount.text().findAll( /\d+/ )*.toInteger();
			int weiboCountNumber;
			if(numbers.size()==0){
				log.error "No numbers extracted for weibo count!";
				weiboCountNumber=-1;
			}else{
				weiboCountNumber=numbers.get(0);
			}
			data.weiboCount=weiboCountNumber;

			data.weiboCountSpan=weiboCount.toString();

			data.following=completeUrl(follow).toString();
			data.followed=completeUrl(fans).toString();
			data.grouping=completeUrl(group).toString();
			data.mentioned=completeUrl(at).toString();
			//Store the weibo count for further check
			this.totalCount=weiboCountNumber;


			//optional: get page count and bot count
			def weiboEndHolder=doc.select("input[name=mp]");
			if(weiboEndHolder.size()==0){
				log.error "Page count span not found. Set page count to 1.";
				pageCount=1;
			}else{
				//extract page count
				def weiboEnd=weiboEndHolder.get(0);
				String page=weiboEnd.attr("value");
				log.info "Total page count is ${page}";
				pageCount=Integer.parseInt(page);
				//get account count
				//				int accountCount=.getBotCount();
				int accountCount=CrackedCookieRepo.getBotCount();
				//each thread should have one account for crawling
				log.info "${accountCount} accounts available for crawling.";
				data["pageCount"]=pageCount;
				data["botCount"]=accountCount;
			}
			log.info "Finished 2nd level info extraction. Now the data is "+data;

		}

		return data;

	}
	public void storeAts(Elements ats){

		if(foundFriends==null)
			foundFriends=[];
		foundFriends.addAll(ats);
		log.debug "Stored ${ats.size()} @ links for further study.";
	}
	public studyAts(){
		//initiate the user page crawler
		if(userPageCrawler==null){
			userPageCrawler=new UserPageCrawler();
		}
		log.info "Start to study the friends of user";
		//convert elements to wrapper class objects
		def links=[];
		for(Element e:foundFriends){
			//get the name outta href link element
			String t=e.text();
			t=t.replace("@","");
			UserLink u=new UserLink(t,e.attr('href'));
			links.add(u);
		}
		def userStats=userPageCrawler.studyLinkToUsers(links);
		return userStats;
	}
	//extract the image from image page
	public static String getImg(String imageUrl){
		Document doc=PageRetriever.fetchPage(imageUrl);
		if(doc==null){
			log.error "No user avatar image is got from ${imageUrl}";
			return imageUrl;
		}

		log.debug "The raw doc is "+doc.text();
		//get image
		def images=doc.select("img");
		//if condition not satisfied, do nothing to the url
		if(images.size()==0){
			log.error "No image found.";
			return imageUrl;
		}else if(images.size()>1){
			log.error "Too many images.";
			log.error images;
			return imageUrl;

		}else{
			def image=images[0];
			log.info "Located avatar url in user image page.";
			String src=image.attr("src");
			return src;
		}
	}
	public void gotOnePage(){
		gotPageCount++;
	}
	public void report(){
		log.info "Got ${gotPageCount} of ${pageCount}";
	}
	public scopeStudy(User u){
		String url=u.url;
		if(url==null){
			log.error "No url for the user";
			return null;
		}
		//convert the url to weibo.cn
		url=url.replace('com','cn');
		jobLog(job.id,"Going to url ${url} for user information.");
		//crawl the user page
		Map data=new HashMap();
		data.userId=u.weiboId;
		data.userName=u.weiboName;
		//crawl user's homepage
		data=crawlUser(url,data);
		log.info "Initialized data holder: "+data;
		jobLog(job.id,"Data from user's homepage: "+data.toString());
		
		//find how many weibo the system has already got
		def knownWeibo;
		Weibo.withTransaction{
			knownWeibo=Weibo.findAll("from Weibo as w where w.ownerName=?",[u.weiboName]);
		}
		int known=knownWeibo.size();
		log.info "${known} weibo known by the system";
		data["knownCount"]=known;


		return data;
	}
	public static void main(String[] args){
		def c=new WeiboCrawlerMaster();

		//c.crawlWeibo("http://weibo.cn/sciencenet",2);
		//c.crawlUser("http://weibo.cn/sciencenet");
		c.crawlWeibo("http://weibo.cn/liuwenyong", 1);
		c.report();
	}
}
