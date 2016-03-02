package crawler

import gro.*
import input.Patterns

import java.math.RoundingMode
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.Connection.Method
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import cn.edu.hfut.dmic.webcollector.crawler.BreadthCrawler
import cn.edu.hfut.dmic.webcollector.model.Links
import cn.edu.hfut.dmic.webcollector.model.Page
import cn.edu.hfut.dmic.webcollector.net.HttpResponse
import codebigbrosub.Weibo

public class UserWeiboCrawler{

	static String base="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\";
	int counter=0;
	File inputFile;
	File outputFile;
	String name;


	ArrayList<String> seeds;
	
	static String sessionId;
	static def cookies;

	public UserWeiboCrawler(String crawlPath, boolean autoParse) {
		//super(crawlPath, autoParse);
		//		/*start page*/
		//		this.addSeed("http://ol.gamersky.com/");
		//		this.addSeed("http://www.gamersky.com/pcgame/");
		//		this.addSeed("http://shouyou.gamersky.com/");
		//		this.addSeed("http://tv.gamersky.com/");
		//
		//		/*fetch url like http://news.yahoo.com/xxxxx*/
		//		this.addRegex("-http://www.hao123.com/.*");
		//		/*do not fetch url like http://news.yahoo.com/xxxx/xxx)*/
		//		this.addRegex("-http://\\.*.hao123.com/.+/.*");
		/*do not fetch jpg|png|gif*/
		//this.addRegex("-.*\\.(jpg|png|gif).*");
		/*do not fetch url contains #*/
		//this.addRegex("-.*#.*");
		seeds=new ArrayList<>();
		System.out.println("Crawler initialized");
	}

//	public void prepareCrawl(File ent){
//		ArrayList<String> url=ent.readLines();
//		this.seeds=new ArrayList<>();
//		url.each{
//			seeds.add(it)
//		}
//	}

	public void addSeed(String url){
		seeds.add(url);
	}
	public void start(int recur){
		for(String url:seeds){
			visit(url);
		}
	}
	public void visit(String url) {
		Document doc;
		if(cookies!=null){
		//get document by jsoup
		
		doc = Jsoup.connect(url)
							.data("query", "Java")
							.userAgent("Mozilla")
							.cookies(cookies)
							.timeout(3000)
							.post();
		}
		else{
			doc = Jsoup.connect(url)
			.data("query", "Java")
			.userAgent("Mozilla")
			.timeout(3000)
			.post();
		}
		/*extract title and content of news by css selector*/
		String title=doc.title();
		String userName=title.substring(0,title.length()-3);
		String raw = doc.body().text();

		//if the text is decoded correctly, redo decoding
//		if(!isValid(raw)){
//			raw=redecode(response);
//		}
		/*If you want to add urls to crawl,add them to nextLinks*/
		/*WebCollector automatically filters links that have been fetched before*/
		/*If autoParse is true and the link you add to nextLinks does not match the regex rules,the link will also been filtered.*/
		// nextLinks.add("http://xxxxxx.com");

		//find all weibo in the page
		String subquery="M_";
		Elements allWeibo=doc.select("div[id^=${subquery}]");
		allWeibo.each{
			Weibo weibo=new Weibo();
			//user name
			weibo.ownerName=userName;
			println("Receptacle weibo created for user "+userName);
			//weibo id
			String weiboId=it.attr("id");
			weiboId=weiboId.replace("M_","");
			weibo.weiboId=weiboId;
			
			//weibo url
			String c="cc";
			Elements a=it.select("a[class=${c}]");
			println("Got ${a.size()} candidate comment links.");
			a.each{thisA->
				String spanText=thisA.text();
				println("this link has text "+spanText);
				if(spanText.contains("\u539f\u6587\u8bc4\u8bba")){}
				else{
					println("this is the comment i want");
					String weiboUrl=thisA.attr("href");
					weibo.url=weiboUrl;
				}
			}
			

			String m="cmt";
			Elements e=it.select("div>span[class=${m}]>a");
			if(e.size()==0){//no class "cmt", original weibo
				//content of weibo
				String s="ctt";
				Element org=it.select("span[class=${s}]").first();
				weibo.content=org.text();
				


			}else{//forward weibo from others
				//content of weibo
				String s="ctt";
				Element org=it.select("span[class=${s}]").first();
				weibo.orgContent=org.text();
				//org owner
				String cmt="cmt";
				Element owner=it.select("div>span[class=${cmt}]>a").first();
				weibo.orgOwnerName=owner.text();
				//user content
				Element forwardDiv=it.select("div").last();
				String forwardMessage=forwardDiv.text();
				println("In forward message: "+forwardMessage);
				forwardMessage=forwardMessage.replace("\u8f6c\u53d1\u7406\u7531:","").trim();
				weibo.content=forwardMessage;
			}
			
			weibo.save();
		}
		counter++;
	}
	public boolean isValid(String s){
		Pattern normal=Patterns.TEXT.value();
		if(s.length()==0){
			println "Empty string."
			return true;//since empty string has no use, each code is valid
		}
		try{
			String part=s.substring(0, Math.min(20, s.length()));
			Matcher match=normal.matcher(part);
			BigDecimal count=new BigDecimal("0");//number of meaningful char in the string
			while(match.find()){
				count=count.add(1);
			}
			//if no meaningful char found in the first part of the string, deem the string as incorrectly decoded
			BigDecimal result=count.divide(part.length(), 4,RoundingMode.HALF_UP);

			if(result<0.6){
				println "Only ${count} meaningful char in the string, failed to decode."
				return false;
			}
			return true;
		}catch(Exception e){
			e.printStackTrace()
		}
	}
//	public String redecode(HttpResponse p){
//		//code to choose from
//		String[] codes=["utf-8","gbk", "ISO8859-1", "utf-16", "gb2312"];
//		boolean solved=false;
//		int i=0;
//		String raw="";
//		byte[] content=p.getContent();
//		while(!solved&&i<codes.size()){
//			println "Trying to decode with ${codes[i]}!"
//
//			String html=new String(content,codes[i]);
//			Document doc=Jsoup.parse(html,p.getUrl());
//			raw=doc.text();
//			solved=isValid(raw);
//			if(solved)
//			println "Successfully decoded with ${codes[i]}";
//			i++;
//		}
//		if(!solved)
//		println "Failed eventually in decoding.";
//		return raw;
//	}
	//return the logged in session
	public static login(String userName,String pwd){
		//��һ������
		Connection con=Jsoup.connect("https://login.weibo.cn/login/");//��ȡ����
		con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");//����ģ�������
		Response rs= con.execute();//��ȡ��Ӧ
		Document d1=Jsoup.parse(rs.body());//ת��ΪDom��
		List<Element> et= d1.select("form");//��ȡform�?������ͨ��鿴ҳ��Դ������֪
		//��ȡ��cooking�ͱ?���ԣ�����map���postʱ�����
		Map<String, String> datas=new HashMap<>();
		for(Element e:et.get(0).getAllElements()){
			if(e.attr("name").equals("mobile")){
				e.attr("value", userName);//�����û���
			}
			  
			if(e.attr("name").contains("password")){
				e.attr("value",pwd); //�����û�����
			}
			  
			if(e.attr("name").length()>0){//�ų��ֵ�?����
				  datas.put(e.attr("name"), e.attr("value"));
			}
		}
		   

		Connection con2=Jsoup.connect("https://login.weibo.cn/login/");
		con2.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
		//����cookie��post�����map���
		Response login=con2.ignoreContentType(true).method(Method.POST).data(datas).cookies(rs.cookies()).execute();
		//��ӡ����½�ɹ������Ϣ
		System.out.println(login.body());
		  
		//��½�ɹ����cookie��Ϣ�����Ա��浽���أ��Ժ��½ʱ��ֻ��һ�ε�½����
		Map<String, String> map=login.cookies();
		println(map);
		def sessionId = login.cookie("SESSIONID");
		cookies=login.cookies();
		
		//println("Got session "+sessionId);
		return cookies;
	}
	public static def getCookies(){
		if(cookies!=null){
			println "Logged in cookies available.";
		}
		else{
			login("18717731224","KOFkof94")
		}	
	}
	public static void main(String[] args) throws Exception{

		
		UserWeiboCrawler g= new UserWeiboCrawler("ccccc",true);
		
		
		g.addSeed("http://weibo.cn/1030503292");
		login("18717731224","KOFkof94");
		g.start(1);

	}
	
}


