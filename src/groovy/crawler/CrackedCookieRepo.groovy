package crawler

import org.jsoup.Jsoup
import org.jsoup.Connection.Method
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document

import toolkit.DocumentChecker
import exceptions.NoCookiesAvailableException
import groovy.util.logging.Log4j

@Log4j
class CrackedCookieRepo {

	public static Iterator nextOne;
	//public static ArrayList<CrackedCookie> allCookies;
	public static Map<CrackedCookie,Boolean> allCookies;

	static int retry=3;
	public static init(){
		//allCookies=new ArrayList<>();
		allCookies=new HashMap<>();
		//1st
		def data=[:];
		//		data["SSOLoginState"]="1453705731";
		//		data["SUB"]="_2A257o12xDeTxGeNI6lYU9y7IwjqIHXVZbGP5rDV6PUJbstANLUPtkW1LHeuRLReS2CMn3T8vndU32hGsgBnzuQ..";
		//		data["SUBP"]="0033WrSXqPxfM725Ws9jqgMF55529P9D9W52ck6hPOverwBC8pj1Tvjq5JpX5o2p";
		//		data["SUHB"]="08JrN0xnw_LcLe";
		//		data["_T_WM"]="be3b90897a3f231a69328f70b4a44ac4";
		//		data["gsid_CTandWM"]="4uS6CpOz58HrY6LJuqVx4nyBA80";
		data["SSOLoginState"]="1460784675";
		data["SUB"]="_2A256Fb5zDeTxGeNI6lYU9y7IwjqIHXVZ-cI7rDV6PUJbstBeLUOlkW1LHeteRnhZoZdXHEDelQnSVqul9R5OxA..";
		data["SUBP"]="0033WrSXqPxfM725Ws9jqgMF55529P9D9W52ck6hPOverwBC8pj1Tvjq5JpX5o2p";
		data["SUHB"]="0U1ZjnLIMOK5qq";
		data["_T_WM"]="8683f45e19ed8da1dcd295e7f9d950d0";
		data["gsid_CTandWM"]="4ugMCpOz5uweIehx8xAAZnyBA80";
		data["mobile"]='theia_fq@163.com';
		if(useful(data)){
			allCookies.put(new CrackedCookie(data),new Boolean(true));
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
			allCookies.put(new CrackedCookie(data1),new Boolean(true));
			log.info "2nd cookie initialized.";
		}else{
			log.error "2nd cookie is not valid any more!";
		}

		if(allCookies.size()==0){
			log.error "No cookie available anymore! \nI want new COOKIES AHHHHHH!\n";
		}
		nextOne=allCookies.iterator();

		//test if the cookies all work
		haveATry();

	}
	public static boolean useful(def cookies){
		String url="http://weibo.cn";
		boolean isValid;
		for(int i=0;i<3&&isValid==false;i++){
			try{
				Response res = Jsoup.connect(url)
						.userAgent("Mozilla")
						.method(Method.POST)
						.cookies(cookies)
						.timeout(4000)
						.execute();
				Document doc=res.parse();
				isValid=DocumentChecker.checkPageContent(doc);
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
		int i=0;
		def notUseful=[];
		allCookies.each{cc,flag->
			def cookies=cc.values;
			Document doc;
			boolean isValid=false;
			for(int j=0;j<retry&&isValid==false;j++){
				try{
					log.info "Checking ${i}th cookie.";
					Response res = Jsoup.connect(url)
							.userAgent("Mozilla")
							.method(Method.POST)
							.cookies(cookies)
							.timeout(2000)
							.execute();
					doc=res.parse();
					isValid=DocumentChecker.checkPageContent(doc);
				}catch(SocketTimeoutException e){
					log.error "Timeout on the ${j}th try.";
					continue;
				}catch(Exception e){
					log.error "An exception took place initializing the CrackedCookieRepo";
					e.printStackTrace();
				}

			}
			//log.info "The status is ${isValid} for this cookie. The content of page is "+doc.text();
			log.info "The status is ${isValid} for this cookie. ";
			if(!isValid){
				notUseful.add(cc);
			}
		}
		log.info "${notUseful.size()} cookies out of ${allCookies.size()} are NOT useful";
		for(int j=0;j<notUseful.size();j++){
			offYouGo(notUseful[j]);
		}
	}
	public static void offYouGo(CrackedCookie c){
		println "Disable one cookie ${c.toString()}";
		allCookies[c]=new Boolean(false);
	}
	public static boolean hasValidCookies(){
		boolean moreAvail;
		allCookies.each{cc,flag->
			if(flag==true)
				moreAvail=true;
		}
		return moreAvail;
	}
	public static CrackedCookie getOneCookie(){
		if(allCookies==null||allCookies.size()==0){
			log.info "No cracked cookies available?";
			init();
		}
		println "${allCookies.size()} items available.";
		//return cookie.values;
		CrackedCookie c;
		boolean hasCookies=hasValidCookies();
		if(hasCookies==false){
			log.error "No more valid cookies to be used! Initiate again!";
			//one more chance before throwing exception
			init();
			hasCookies=hasValidCookies();
			if(hasCookies==false){
				throw new NoCookiesAvailableException();
			}else{
				log.info "Re-initialized the repository. Now ${allCookies.size()} cookies for use.";
			}
		}
		boolean notFound=true;
		def e;
		while(notFound){
			if(nextOne.hasNext()){
				e=nextOne.next();
			}else{
				log.info "The iterator of allCookies has reached the end.";
				nextOne=allCookies.iterator();
				e=nextOne.next();
			}
			if(e.value==true){
				c=e.key;
				notFound=false;
			}else{
				log.info "This cookie is not working.";
			}
		}
		return c;//return CrackedCookie object
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
		@Override
		public String toString(){
			if(values.containsKey("mobile")){
				return values["mobile"];
			}else{
				return "No mobile found";
			}

		}
		@Override
		public int hashCode(){
			if(values.containsKey("mobile")){
				return values["mobile"].hashCode();
			}else{
				return "No mobile found".hashCode();
			}
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
				.cookies(cookies.values)
				.timeout(2000)
				.execute();
		Document doc=res.parse();
		println doc.text();

		println "And this is the 2nd.";
		Response res2 = Jsoup.connect(url)
				.userAgent("Mozilla")
				.method(Method.POST)
				.cookies(yacookies.values)
				.timeout(2000)
				.execute();
		Document doc2=res2.parse();
		println doc2.text();
	}
}
