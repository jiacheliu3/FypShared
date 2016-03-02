package crawler

import groovy.util.logging.Log4j

@Log4j
class HKWeiboCrawlerThread implements Runnable{
	String url;
	// the start page and end page index
	int start;
	int end;
	//cookies to use in crawling
	def cookies;
	String userId;
	String userName;

	int retry;
	
	WeiboCrawlerMaster boss;

	public void debrief(){
		println "I cover the url ${url} and page from ${start} to ${end}. I belong to user ${userId}:${userName}";
	}
	
	public String buildUserUrl(String userId){
		//http://hk.weibo.com/p/weibostatus/user/index/1758509357/
		return "http://hk.weibo.com/p/weibostatus/user/index/"+userId+"/";
	}
	public String buildWeiboUrl(String userId,String mid){
		String convertedId=Base62Converter.convertToId(mid);
		log.debug "Converted to id"+convertedId;
		return buildUserUrl(userId)+convertedId;
	}
	@Override
	public void run(){
		String userUrl=buildUserUrl(userId);
		log.info "Target at user url "+userUrl;
		debrief();
	}
}
