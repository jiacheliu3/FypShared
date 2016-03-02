
package crawler
import groovy.util.logging.Log4j

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import org.jsoup.Jsoup
import org.jsoup.Connection.Method
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import codebigbrosub.User
import codebigbrosub.Weibo

@Log4j
class WeiboCrawlerMaster {
	int pageCount;
	int gotPageCount=0;
	int gotCount=0;
	int totalCount;
	
	UserPageCrawler userPageCrawler;
	Elements foundFriends;
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
	public void crawlWeibo(def data){
		boolean gotInitPage=false;
		int retry=5;
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
		if(needCheck){
			for(int p=0;p<retry&&gotInitPage==false;p++){
				//first get the user page to extract basic info
				Document doc;
				def cookies=CrackedCookieRepo.getOneCookie();
				//def cookies=login('hehehe@163.com','zsygpqdh');
				doc = Jsoup.connect(url)
						.data("query", "Java")
						.userAgent("Mozilla")
						.cookies(cookies)
						.timeout(3000)
						.post();
				log.info "Raw doc text "+doc.text();
				//check if the page is useful
				boolean isUseful=checkPageContent(doc);
				if(!isUseful){
					log.error "The page is useless. Have another try.";
					log.info "One cookie failed. ";
					if(p==retry-1){
						log.error "Let's try re-initiate the cookie repository before the last try.";
						CrackedCookieRepo.init();
					}
					log.info "Wait a moment and try again with another cookie.";
					cookies=CrackedCookieRepo.getOneCookie();
					continue;
				}
				
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
				gotInitPage=true;
			}
			//report final status
			if(gotInitPage==false){
				log.error "Page not got after 5 attempts. Crawler cannot proceed. ";
				return;

			}
		}

		//calculate thread count and allocate
		int threadCount=accountCount;
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

			threads.add(new CrawlerThread(url:baseUrl,start:startIndex,end:endIndex,userId:userId,userName:userName,cookies:newCookie,retry:2,boss:this));

		}
		//es.shutdown();
		
		//start the thread pool
		try {
			List<Future> futures = threads.collect{theThread->
			  es.submit({->
				  theThread.run();
			  });
			}
			// recommended to use following statement to ensure the execution of all tasks.
			futures.each{it.get()}
		  }finally {
			def notRun=es.shutdownNow();
			log.info "Now the thread pool is shut down. Still ${notRun} threads are not finished and stopped.";
		  }
		//gotInitPage=true;
//		boolean finished = es.awaitTermination(40, TimeUnit.MINUTES);
//		log.info "Status is ${finished}";

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
		for(int r=0;r<5&&found==false;r++){
			Document doc;
			try{
				doc = Jsoup.connect(url)
						.data("query", "Java")
						.userAgent("Mozilla")
						.cookies(cookies)
						.timeout(5000)
						.post();
				log.info "Raw doc "+doc.text();
				//check whether the document is valid
				if(!checkPageContent(doc)){
					log.info "One cookie failed. ";
					if(r>=2){
						log.error "Getting invalid document after 2 try. Let's try re-initiate the cookie repository.";
						CrackedCookieRepo.init();
					}
					log.info "Wait a moment and try again with another cookie.";
					cookies=CrackedCookieRepo.getOneCookie();
					sleep(10000);
					continue;
				}
				log.debug "Valid document. Go on.";
				
			}catch(SocketTimeoutException e){
				log.error "Time out in the ${r}th connection!";
				continue;
			}
			def holder=doc.select("div[class=tip2]");
			if(holder.size()==0){
				log.error "No user info found";
				log.info doc.text();
				sleep(1000);
				continue;
			}

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
				faceUrl=getImg(faceUrl,cookies);
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
				
		foundFriends.add(ats);
		log.debug "Stored ${ats.size()} mentionings for further study.";
	}
	public studyAts(){
		//initiate the user page crawler
		if(userPageCrawler==null){
			userPageCrawler=new UserPageCrawler();
		}
		log.info "Start to study the friends of user";
		def userStats=userPageCrawler.studyLinkToUsers(foundFriends);
		return userStats;
	}
	//extract the image from image page
	public static String getImg(String imageUrl,Map cookies){
		Response res = Jsoup.connect(imageUrl)
				.userAgent("Mozilla")
				.method(Method.POST)
				.cookies(cookies)
				.timeout(4000)
				.execute();
		Document doc=res.parse();
		println "The raw doc is "+doc.text();
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
		//crawl the user page
		Map data=new HashMap();
		data.userId=u.weiboId;
		data.userName=u.weiboName;
		//crawl user's homepage
		data=crawlUser(url,data);
		log.info "Initialized data holder: "+data;

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
	public static boolean checkPageContent(Document doc){
		String title=doc.title();
		if(title.contains('\u65b0\u6d6a\u901a\u884c\u8bc1')){
			log.error "This page is useless. Better check what's wrong.";
			log.error doc;
			return false;
		}
		String content=doc.text().trim();
		if(content.startsWith('\u65b0\u6d6a\u901a\u884c\u8bc1')){
			log.error "This page is useless. Better check what's wrong.";
			log.error doc;
			return false;
		}
		return true;
	}
	
	public static void main(String[] args){
		def c=new WeiboCrawlerMaster();

		//c.crawlWeibo("http://weibo.cn/sciencenet",2);
		//c.crawlUser("http://weibo.cn/sciencenet");
		c.crawlWeibo("http://weibo.cn/liuwenyong", 1);
		c.report();
	}
}
