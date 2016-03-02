package crawler

import grails.async.PromiseList
import groovy.util.logging.Log4j

@Log4j
class HKWeiboCrawlerMaster {
	String weiboName;
	String weiboId;
	public static String buildUserUrl(String userId){
		//http://hk.weibo.com/p/weibostatus/user/index/1758509357/
		return "http://hk.weibo.com/p/weibostatus/user/index/"+userId+"/";
	}
	public static String buildWeiboUrl(String userId,String mid){
		String convertedId=Base62Converter.convertToId(mid);
		log.debug "Converted to id"+convertedId;
		return buildUserUrl(userId)+convertedId;
	}
	public void crawlEntrance(){
		String userUrl=buildUserUrl(weiboId);
		log.info "Target at user url "+userUrl;
		//decide scope 
		
		
		PromiseList promises=new PromiseList();
		
	}
}
