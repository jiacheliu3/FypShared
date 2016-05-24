package toolkit

import groovy.util.logging.Log4j
import input.Patterns

import java.util.regex.Pattern

//Convert between all url formats
@Log4j
class UrlConverter {
	public static userIdToUrl(String wid){
		String url="weibo.cn/u/"+wid;
		return url;
	}
	public static userPageToInfoPage(String userUrl){
		//sample user page url: http://weibo.cn/u/3962479408
		if(userUrl.contains("weibo.cn/u/")){
			String pureUrl=userUrl.replace("weibo.cn/u/","weibo.cn/");
			String infoUrl;
			if(pureUrl.endsWith("/"))
				infoUrl=pureUrl+"/info";
			else
				infoUrl=pureUrl+"info";

			return infoUrl;
		}else{
			log.info "The user url ${userUrl} doesn't match the format!";
		}
		return null;
	}
	public static infoPageToUserPage(String infoUrl){
		//sample user page url: http://weibo.cn/3962479408/info
		if(infoUrl.contains("/u/")){
			log.info "The info url ${infoUrl} has user field in it!";
			return infoUrl;
		}else if(infoUrl.contains("/info")){
			String userUrl=infoUrl.replace("weibo.cn/","weibo.cn/u/");
			userUrl.replace("/info","");
			log.info "Converted info url to user url ${userUrl}";
			return userUrl;
		}else{
			log.info "The info url ${infoUrl} doesn't match the format!";
		}
		return null;
	}
	public static boolean isUseful(String url){
		//this url has nothing in the page
		//http://weibo.cn/sinaurl?f=w&amp;u=http%3A%2F%2Ft.cn%2FRbmkRlh&amp;ep=DfUZOiGKn%2C3962479408%2CDfUZOiGKn%2C3962479408
		if(url.contains("/sinaurl?")){
			return false;
		}
		//this url is for hashtag topics
		//http://weibo.cn/pages/100808topic?extparam=%E5%81%B6%E5%83%8F%E6%9D%A5%E4%BA%86&amp;from=feed
		else if(url.contains("pages")&&url.contains("topic")){
			return false;
		}
		//this is sample user page url which will be converted to /u/...
		///n/%E6%96%B0%E6%B5%AA%E7%BB%BC%E8%89%BA
		else if(url.contains("n/")){
			return true;
		}else{
			log.error "This url is unrecognized: ${url}";
			return true;
		}
	}
	public static String fillUrl(String link){
		String url=link;
		//check if the url is from weibo.com
		if(url.contains("www.weibo.com")){
			url=url.replace("www.weibo.com","weibo.cn");
		}
		else if(url.contains("weibo.com")){
			url=url.replace("weibo.com","weibo.cn");
		}
		//check if from weibo.cn
		if(url.matches("(http|https)://weibo\\.cn.*")){
			
		}else if(url.contains("weibo.cn")){//short of http
			if(url.startsWith('/'))
				url="http:/"+url;
			else
				url="http://"+url;
		}else{//no weibo no protocol
			if(url.startsWith("/"))
				url="http://weibo.cn"+url;
			else
				url="http://weibo.cn/"+url;			
		}
		
		return url;
	}
	public static boolean validate(String url){
		Pattern urlPattern=Patterns.URL.value();
		if(url.matches(urlPattern)){
			return true;
		}else{
			log.error "Url is not a valid one: ${url}";
			return false;
		}
	}
	public static String fillUrlById(String wId){
		wId=wId.trim();
		String url;
		if(wId.matches("\\d{5,15}")){
			url="http://weibo.cn/u/${wId}";
		}else{
			log.info "User id is not formed of numbers. Guess this is a name.";
			url="http://weibo.cn/${wId}";
		}
		return url;
	}
	public static void testUrl(String url){
		if(isUseful(url)){
			println fillUrl(url);
		}else{
			println "url is abandoned.";
		}
	}
	public static void main(String[] args){
		String url1="/n/%E6%96%B0%E6%B5%AA%E7%BB%BC%E8%89%BA";
		testUrl(url1);
		String url2="http://weibo.cn/pages/100808topic?extparam=%E5%81%B6%E5%83%8F%E6%9D%A5%E4%BA%86&amp;from=feed";
		testUrl(url2);
		String url3="http://weibo.cn/sinaurl?f=w&amp;u=http%3A%2F%2Ft.cn%2FRbmkRlh&amp;ep=DfUZOiGKn%2C3962479408%2CDfUZOiGKn%2C3962479408";
		testUrl(url3);

	}

}
