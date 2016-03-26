package crawler

import groovy.time.TimeCategory
import groovy.util.logging.Log4j

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import toolkit.DocumentChecker
import crawler.CrackedCookieRepo.CrackedCookie




@Log4j
class CrawlerThread implements Runnable{
	int tid;//thread id
	String url;
	// the start page and end page index
	int start;
	int end;
	//cookies to use in crawling
	CrackedCookie cookies;
	String userId;
	String userName;

	int retry;
	//keep a local record of how many are collected from this thread
	int gotCount=0;

	WeiboCrawlerMaster boss;
	WeiboCrawler crawler;
	//mode settings, default to false
	boolean needSave;
	boolean forceSave;
	boolean fastMode;

	public void debrief(){
		//generate a random 4-digit number as name;
		Random rand=new Random();
		tid = rand.nextInt(8999) + 1000;
		log.info "Thread ${tid}: I cover the url ${url} and page from ${start} to ${end}. I belong to user ${userId}:${userName}";
	}
	@Override
	public void run(){
		debrief();
		//timing start
		def startTime=new Date();
		
		//initialize the crawler who is to do the real work
		crawler=new WeiboCrawler(userName:userName,boss:boss,needSave:needSave,forceSave:forceSave,fastMode:fastMode);

		for(int i=start;i<=end;i++){
			String nextUrl=url;
			boolean fetched;
			
			for(int j=0;j<retry&&fetched==false;j++){

				try{
					Document doc = Jsoup.connect(nextUrl)
							//.data("query", "Java")
							.data("page", i+"")
							.userAgent("Mozilla")
							.cookies(cookies.values)
							.timeout(1500)
							.post();
					log.info "Got page ${i} "+doc.text();
					//check if the page is valid
					boolean isUseful=DocumentChecker.checkPageContent(doc);
					if(!isUseful){
						log.error "Uh-oh it seems I'm caught on page ${i}. ";
						sleep(30000);
						continue;
					}

					//extract weibo
					Elements contents=doc.select("span[class=ctt]");
					log.info "Found ${contents.size()} items.";
					//when page=1 there are 3 spans containing only the user info that should be disposed
					int startIndex;
					if(i==1)
						startIndex=3;
					else
						startIndex=0;
					for(int k=startIndex;k<contents.size();k++){
						Element zone=contents.get(k);
						Element weibo=zone.parent().parent();
						boolean success=crawler.extractFromWeibo(weibo,zone);
						if(success){
							gotCount++;
						}else{
							log.error "Failed in information extraction from weibo!";
						}
					}
					fetched=true;

				}
				catch(SocketTimeoutException e){
					log.error "Time out for the ${j}th try of page ${i}.";
					sleep(1000);
					continue;
				}
				catch(Exception e){
					log.error "Thread ${tid}: Exception! Got caught in the ${i}th page"
					e.printStackTrace();
				}
				if(fetched==false){
					log.error "Failed to fetch the ${i}th page at last.";

				}

				//sleep
				sleep(2000);
			}


		}
		//timing end
		def endTime=new Date();
		def period=TimeCategory.minus(endTime, startTime);
		log.info "Crawler thread spent ${period}";

		//report the number collected from this thread
		log.info "Thread ${generateName()} collected ${gotCount} weibo items.";


	}
	public String generateName(){
		return "User ${userName} for pages ${start}-${end}";
	}

}
