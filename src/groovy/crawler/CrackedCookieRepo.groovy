package crawler

import groovy.util.logging.Log4j

import org.jsoup.Jsoup
import org.jsoup.Connection.Method
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document

@Log4j
class CrackedCookieRepo {

	public static int nextNum;
	public static ArrayList<CrackedCookie> allCookies;
	public static init(){
		allCookies=new ArrayList<>();
		nextNum=0;
		//1st
		def data=[:];
		//		data["SSOLoginState"]="1453705731";
		//		data["SUB"]="_2A257o12xDeTxGeNI6lYU9y7IwjqIHXVZbGP5rDV6PUJbstANLUPtkW1LHeuRLReS2CMn3T8vndU32hGsgBnzuQ..";
		//		data["SUBP"]="0033WrSXqPxfM725Ws9jqgMF55529P9D9W52ck6hPOverwBC8pj1Tvjq5JpX5o2p";
		//		data["SUHB"]="08JrN0xnw_LcLe";
		//		data["_T_WM"]="be3b90897a3f231a69328f70b4a44ac4";
		//		data["gsid_CTandWM"]="4uS6CpOz58HrY6LJuqVx4nyBA80";
		data["SSOLoginState"]="1454156630";
		data["SUB"]="_2A257qNsGDeTxGeNI6lYU9y7IwjqIHXVZUuVOrDV6PUJbstBeLVHSkW1LHetGNW65weMQtkrLoqSsOxFAxKw8FQ..";
		data["SUBP"]="0033WrSXqPxfM725Ws9jqgMF55529P9D9W52ck6hPOverwBC8pj1Tvjq5JpX5o2p";
		data["SUHB"]="0FQ7X4KQ1qAK3";
		data["_T_WM"]="3ce3f486d6626a0af3b169817495bf7a";
		data["gsid_CTandWM"]="4u6MCpOz5vXl3pV7UsW4mnyBA80";
		data["mobile"]='theia_fq@163.com';
		if(useful(data)){
			allCookies.add(new CrackedCookie(data));
			log.info "1st cookie initialized.";
		}else{
			log.error "1st cookie is not valid any more!";
		}

		//2nd
		def data1=[:];
		//		data1["SUB"]="_2A257mV_HDeRxGeNJ6FQT9CvLyDWIHXVZYmGPrDV6PUJbrdANLVT7kW1LHeuRAFREkVBOlepzu9C9YD-7bWsfAw..";
		//		data1["_T_WM"]="7c4c1161f96a25858934b16bd7887a83";
		//		data1["gsid_CTandWM"]="4u42CpOz5I1CYn6hLFzmMo4g5bV";
		data1["SSOLoginState"]="1454061240";
		data1["SUB"]="_2A257r0boDeRxGeNJ6FQT9CvLyDWIHXVZUGqgrDV6PUJbstBeLWbfkW1LHetL8cTzv1VersJ177LBycBTYyIIFw..";
		data1["SUBP"]="0033WrSXqPxfM725Ws9jqgMF55529P9D9W5HEln_9O7yLQbRLjxh0O4v5JpX5o2p";
		data1["SUHB"]="0jBIvQRBCKztiy";
		data1["_T_WM"]="0d19fe95e27c47dccecbc0a8eddb5d56";
		data1["gsid_CTandWM"]="4uNNCpOz5hl3kAutfnsQDo4g5bV";
		data1["mobile"]="18717731224";
		if(useful(data1)){
			allCookies.add(new CrackedCookie(data1));
			log.info "2nd cookie initialized.";
		}else{
			log.error "2nd cookie is not valid any more!";
		}

		if(allCookies.size()==0){
			log.error "No cookie available anymore! \nI want new COOKIES AHHHHHH!\n";
		}


		//test if the cookies all work
		//haveATry();

	}
	public static boolean useful(def cookies){
		boolean isValid;
		for(int i=0;i<3;i++){
			String url="http://weibo.cn";
			try{
				Response res = Jsoup.connect(url)
						.userAgent("Mozilla")
						.method(Method.POST)
						.cookies(cookies)
						.timeout(2000)
						.execute();
				Document doc=res.parse();
				isValid=WeiboCrawlerMaster.checkPageContent(doc);
				break;
			}catch(SocketTimeoutException e){
				log.info "Test of cookies got time-out in ${i}th fetch.";
				continue;
			}

		}
		return isValid;
	}
	public static void haveATry(){
		String url="http://weibo.cn";
		//check each cookie hijacked
		for(int i=0;i<allCookies.size();i++){
			def cookies=allCookies[i].values;
			log.info "Checking ${i}th cookie.";
			Response res = Jsoup.connect(url)
					.userAgent("Mozilla")
					.method(Method.POST)
					.cookies(cookies)
					.timeout(2000)
					.execute();
			Document doc=res.parse();
			boolean isValid=WeiboCrawlerMaster.checkPageContent(doc);
			log.info "The status is ${isValid} for this cookie. The content of page is "+doc.text();
		}

	}
	public static Map getOneCookie(){

		if(allCookies==null||allCookies.size()==0){
			log.info "No cracked cookies available?";
			init();
		}
		println "${allCookies.size()} items available.";
		if(nextNum>=allCookies.size()-1){
			nextNum-=allCookies.size();
		}

		def cookie=allCookies[nextNum];
		nextNum++;
		return cookie.values;
	}
	public static int getBotCount(){
		return allCookies.size();
	}
	public static class CrackedCookie{
		Map values;
		public CrackedCookie(Map cookie){
			values=cookie;
			println "New cookie initialized with value "+values;
		}
	}

	public static void main(String[] args){
		init();
		def cookies=getOneCookie();
		def yacookies=getOneCookie();
		println "This is the 1st buddy.";
		String url="http://weibo.cn";
		Response res = Jsoup.connect(url)
				.userAgent("Mozilla")
				.method(Method.POST)
				.cookies(cookies)
				.timeout(2000)
				.execute();
		Document doc=res.parse();
		println doc.text();

		println "And this is the 2nd.";
		Response res2 = Jsoup.connect(url)
				.userAgent("Mozilla")
				.method(Method.POST)
				.cookies(yacookies)
				.timeout(2000)
				.execute();
		Document doc2=res2.parse();
		println doc2.text();
	}
}
