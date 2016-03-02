package crawler
import groovy.util.logging.Log4j

import java.awt.BorderLayout
import java.awt.Container
import java.awt.Image

import javax.imageio.ImageIO
import javax.servlet.http.Cookie
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JTextField

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.Connection.Method
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

@Log4j
class CookieManager {

	static def weiboCookies;
	static LinkedHashMap<String,String> bots=new LinkedHashMap<>();
	static ArrayList<Cookie> weiboBots=new ArrayList<>();
	static int weiboIndex;
	
	//store images as test data
	static int counter=0;

	public static init(){
		addBot('18717731224','KOFkof94');
		//addBot('theia_fq@163.com','zsygpqdh');
		weiboAllLogin();
	}
	public static addBot(String name,String passwd){
		bots.put(name, passwd);
	}
	public static weiboAllLogin(){
		log.info "Initialize bots.";
		bots.each{name,passwd->
			log.info "This user is ${name}:${passwd}";
			boolean complete=false;
			while(!complete){
				try{

					weiboBots.add(login(name,passwd));
					complete=true;
				}catch(SocketTimeoutException e){
					log.info "Time out when logging in";
					continue;
				}
			}
		}
		weiboIndex=0;
		log.info "All sessions generated.";
	}
	public static login(String userName,String pwd){
		Connection con=Jsoup.connect("https://login.weibo.cn/login/");
		con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
		Response rs= con.execute();
		Document d1=Jsoup.parse(rs.body());
		List<Element> et= d1.select("form");

		Map<String, String> datas=new HashMap<>();
		//get the valdation code if any
		Elements val=d1.select("input[name=code]");
		if(val.size()==0){
			log.info "No need to fill in validation code in first request.";
			def form=d1.select("form")?.get(0);
			datas=fillForm(form,"");
		}else{
			String imageUrl=d1.select("img").get(0).attr("src");
			log.info "Got image url "+imageUrl;
			def form=et.get(0);
			datas=fillForm(form,imageUrl);
		}

		def cookies;//cookies to store the logged in info
		boolean valPassed=false;
		def cookieReg=rs.cookies();
		while(!valPassed){
			Connection con2=Jsoup.connect("https://login.weibo.cn/login/");
			con2.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			Response login=con2.ignoreContentType(true).method(Method.POST).data(datas).cookies(cookieReg).execute();
			Document d2=Jsoup.parse(login.body());
			println(d2.text());
			//check if this requires validation code
			boolean needVal=false;
			Elements val1=d2.select("input[name=code]");
			String tex=d2.text();
			if(val1.size()>0){
				log.info "Found validation code zone.";
				needVal=true;
			}
			else if(tex.contains(/\xE8\xBE\x93\xE5\x85\xA5\xE9\xAA\x8C\xE8\xAF\x81\xE7\xA0\x81/)){
				log.info "Found notification for validation code.";
				needVal=true;
			}

			if(!needVal){
				log.info "No validation code required in the real login step or the login stage has passed. Safe.";
//				Map<String, String> map=login.cookies();
//				log.info("No validation code needed. The cookies are "+map);
				cookies=login.cookies();
				log.info "Got cookies "+cookies;
				valPassed=true;
			}else{
				log.info "After attempt to login got the request for validation code.";
				//backup the cookies
				cookieReg=login.cookies();
				String imageUrl=d2.select("img").get(0).attr("src");
				log.info "Got image url "+imageUrl;
				def form=d2.select("form").get(0);
				datas=fillForm(form,imageUrl);
				//log.info "Now http header is "+datas;
				//connect using the cookies
				//				Response login3=con2.ignoreContentType(true).method(Method.POST).data(datas).cookies(cookieReg).execute();
				//				Document d3=Jsoup.parse(login.body());
				//				log.info(d3.text());
				//				//decide whether it's successful
				//				boolean needVal3=false;
				//				Elements val3=d2.select("input[name=code]");
				//				String tex3=d3.text();
				//				if(val3.size()>0){
				//					log.info "Found validation code zone.";
				//					needVal=true;
				//				}
				//				else if(tex3.contains(/\xE8\xBE\x93\xE5\x85\xA5\xE9\xAA\x8C\xE8\xAF\x81\xE7\xA0\x81/)){
				//					log.info "Found notification for validation code.";
				//					needVal=true;
				//				}else{
				//					log.info "Login complete";
				//				}

			}
		}

		//log.info("Got session "+sessionId);
		return cookies;
	}
	public static Map fillForm(Element form,String imageUrl){
		if(imageUrl==""){
			println "No image url got. Assume there is none needed.";
		}
		def all=form.select("input");
		String userName="18717731224";
		String pwd="KOFkof94";
		def data=[:];
		all.each{e->
			println "This element is "+e;
			if(e.attr("name")=="mobile"){
				data["mobile"]=userName;
			}
			else if(e.attr("name")=="code"){
				String code=readValidationCode(imageUrl);
				data["code"]=code;
			}
			else if(e.attr("name").startsWith("password")){
				data[e.attr("name")]=pwd;
			}
			else if(e.attr("name").length()>0){
				data[e.attr("name")]=e.attr("value");
			}
		}

		return data;
	}
	public static String readValidationCode(String imageUrl){
		Image image = null;
		String targetCode;
		try {
			URL url = new URL(imageUrl);
			image = ImageIO.read(url);
			//save the image
			File f=new File("D:/tempCaptchas/${counter}.gif");
			if(f.exists())
				f.delete();
			ImageIO.write(image, "gif",f);
			println "The image is store at ${counter}.gif";
			counter++;
			
		} catch (IOException e) {
			log.info "Got exception when reading image.";
			e.printStackTrace();
		}
//		JLabel picLabel = new JLabel(new ImageIcon(image));
//
//		// Use a label to display the image
//		JFrame frame = new JFrame();
//		frame.setLayout(new BorderLayout());
//		Container c = frame.getContentPane();
//
//		//JPanel mainPanel = new JPanel(new BorderLayout());
//
//		JTextArea text=new JTextArea();
//		JButton button=new JButton("Confirm");
//		button.addActionListener(new ActionListener(){
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						targetCode = text.getText();
//						log.info "Read code ${targetCode}";
//					}
//				});
//		// add more components here
//		c.add(picLabel, BorderLayout.CENTER);
//		c.add(text, BorderLayout.SOUTH);
//		c.add(button, BorderLayout.WEST);
//		frame.setVisible(true);
		
		//fake the code
		//targetCode="1111";
		
		//ReaderFrame d=new ReaderFrame(counter);
		//sleep(5000);
//		String code=d.readValue();
//		println "Got code ${code}";
		sleep(10000);
		String code=readCodeFromFile("D:/tempCaptchas/theCode.txt");

		return code;
	}
	public static String readCodeFromFile(String path){
		File f=new File(path);
		
		def d=f.readLines();
		String s=d.get(0).trim();
		println "Got code ${s}";
		return s;
	}
	/* Simple dialog asking for input */
	public static class ReaderFrame extends JDialog{
		private JTextField input;
		public ReaderFrame(int i){
			input=new JTextField();
			JLabel message=new JLabel("Picture is ${i}.gif");
			Container c=getContentPane();
			c.add(message,BorderLayout.NORTH);
			c.add(input,,BorderLayout.CENTER);
//			setHorizontalAlignment(SwingConstants.CENTER);
			setModal(true);
			setSize(300,200);
			setVisible(true);
		}
		public String readValue(){
			String s=input.getText();
			return s;
		}
	}
	public static getWeiboSession(){
		//check if initialization is needed
		int siz=weiboBots.size();
		if(siz==0){
			log.info "No available bots found. Check if initialization is needed.";
			init();

		}
		siz=weiboBots.size();
		if(weiboIndex>=siz-1){
			weiboIndex=0;
		}
		def reg=weiboBots.get(weiboIndex);
		weiboIndex++;
		return reg;
	}
	public static int getBotCount(){
		int siz=weiboBots.size();
		if(siz==0){
			log.info "No bots found. Initialization may be needed.";
			init();
		}
		siz=weiboBots.size();
		return siz;
	}
	public static Document spiderCrawl(String url){
		Document doc = Jsoup.connect(url)
				.data("query", "Java")
				.userAgent("spider")
				.timeout(3000)
				.post();
		String tex=doc.text();
		log.info "Got text from url\n"+tex;
	}
	public static String id2mid(String did){
		String alphabet="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		long d=Long.parseLong(did);
		String base62=Long.toString(d,62);
		return base62;
		
	}
	public static void main(String[] args){
		String url="http://hk.weibo.com/p/weibostatus/user/index/2721890875/1/";
		def d=spiderCrawl(url);
		
		String toGet="CeaOU15IT";
		println "Target string is "+toGet;
		String got=id2mid("3833781880260331");
		println "Got "+got;
	}

}
