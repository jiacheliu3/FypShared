package crawler

import groovy.util.logging.Log4j

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import toolkit.DocumentChecker
import crawler.CrackedCookieRepo.CrackedCookie

@Log4j
class PageRetriever {
	static int retry=3;
	static timeout=3000;

	public static Document fetchPage(String url){
		Document doc;
		boolean gotPage=false;
		CrackedCookie cookies=CrackedCookieRepo.getOneCookie();
		for(int count=0;count<retry&&gotPage==false;count++){
			try{
				doc = Jsoup.connect(url)
						.data("query", "Java")
						.userAgent("Mozilla")
						.cookies(cookies.values)
						.timeout(timeout)
						.post();
				boolean docIsGood=DocumentChecker.checkPageContent(doc);
				if(docIsGood){
					log.debug "Got the page";
					gotPage=true;
				}
				else{
					println("The cookies are not useful anymore!");
					//report the cookies are done and get new ones
					CrackedCookieRepo.offYouGo(cookies);
					boolean hasMoreCookies=CrackedCookieRepo.hasValidCookies();
					if(hasMoreCookies){
						cookies=CrackedCookieRepo.getOneCookie();
					}else{
						log.error "No more cookies available. The crawler must be stopped.";
						break;
					}
				}
			}catch(SocketTimeoutException e){
				log.error "Fetch reposts time-out for the ${count}th try";
				continue;
			}catch(IndexOutOfBoundsException e){
				log.error "Login failed or field extraction failed";
				continue;
			}catch(Exception e){
				e.printStackTrace();
			}
			//sleep 1 second
			//waitASecond();
			sleep(1000);
		}

		return doc;
	}
}
