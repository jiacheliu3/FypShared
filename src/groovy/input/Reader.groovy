package input;

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

import codebigbrosub.User
import codebigbrosub.Weibo
import exceptions.*
import groovy.time.TimeCategory


public class Reader {
	static {
		init();
	}
	static Pattern id;
	static Pattern uip;
	static Pattern ws;// whitespace
	static Pattern block;// big block containing data time etc. This is to
	// recognize where content locates.
	static Pattern fp;// pattern for forward message
	static Pattern image;// user's profile photo
	static Pattern uaf;

	public static void init() {
		id = Pattern.compile("\\d+\\s*\\|\\|\\s*\\|\\|\\s*\\-?\\d*");
		uip = Pattern.compile("\\d+");
		ws = Pattern.compile("\\s+");
		String url = Patterns.URL.reg();
		String date = Patterns.DATE.reg();
		String time = Patterns.TIME.reg();
		String num = Patterns.NUM.reg();
		String combine = url + "\\s+" + date + "\\s+" + time + "\\s+" + num + "\\s+" + num + "\\s+" + url;
		block = Pattern.compile(combine);

		String forwardBlock = num + "\\s+" + num + "\\s+" + url + "\\s+" + url;
		fp = Pattern.compile(forwardBlock);

		image = Pattern.compile("http[s]?://.*\\.jpg");
		String urlAndFace = url + "\\s+" + url;
		uaf = Pattern.compile(urlAndFace);

	}

	private static Weibo process(String line) {
		// fields of user
		String userName="";
		String userId="";
		String url="";
		String photoUrl="";

		Weibo w = new Weibo();
		Matcher m = id.matcher(line);
		String backupId;
		if (m.find()) {
			String i = m.group();
			line = line.substring(m.end()).trim();
			// this id is obsoleted and should not be used
			backupId = i.substring(0, i.indexOf("|") - 1).trim();
			//			w.setWeiboId(id);
			//			System.out.println(id);
		}

		// System.out.println(line);
		Matcher ms = ws.matcher(line);
		if (!ms.find())
			System.out.println("Not found");
		int contentStart = ms.end();
		String name = line.substring(0, ms.start()).trim();
		w.setOwnerName(name);
		userName=name;
		System.out.println(name);

		Matcher bm = block.matcher(line);
		if (!bm.find()) {
			System.out.println("Weibo is malformatted!");
			throw new BrokenLineException();
		}
		// process goes on only when the block is correctly found
		String sub = bm.group();
		//find weibo url and user url
		Pattern urlp=Patterns.URL.value();
		Matcher yaum=urlp.matcher(sub);
		if(yaum.find()){
			String wUrl=yaum.group();
			w.url=wUrl;
			//extract id from url
			int widEnd=wUrl.lastIndexOf('/');
			if(widEnd==-1){
				println "Id not found from url "+wUrl;
				w.weiboId=backupId;
			}else{
				String wid=wUrl.substring(widEnd+1);
				w.weiboId=wid;

			}
		}
		else{
			println "Failed to find weibo url and user profile photo. This weibo item will lose track due to incorrect id.";
			//use backup id
			w.setWeiboId(id);

		}
		if(yaum.find()){
			photoUrl=yaum.group();
		}
		else{
			println "Failed to find user profile photo"
		}
		//find date, time, comment and forward count
		Pattern dp = Patterns.DATE.value();
		Pattern tp = Patterns.TIME.value();
		Matcher dpm = dp.matcher(sub);
		Matcher tpm = tp.matcher(sub);
		if (!dpm.find() || !tpm.find()) {
			System.out.println("Date Time not found!");
		} else {
			try {
				Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dpm
						.group() + " " + tpm.group());
				w.setCreatedTime(d);
			} catch (ParseException e) {
				System.out.println("Error in time parsing");
			}

			System.out.println("Date:" + dpm.group() + " Time:" + tpm.group());

			// read forwarded and commented count
			String count = sub.substring(tpm.end());
			Pattern num = Patterns.NUM.value();
			Matcher nm = num.matcher(count);
			try {
				if (nm.find()) {
					w.setForwardCount(Integer.parseInt(nm.group()));
					if (nm.find())
						w.setCommentCount(Integer.parseInt(nm.group()));
				}
			} catch (NumberFormatException e) {
				System.out
						.println("Wrong format in forward count and comment count!");
			}

		}
		int contentEnd = bm.start();
		int blockEnd = bm.end();
		String content = line.substring(contentStart, contentEnd).trim();
		w.setContent(content);
		System.out.println(content);

		// truncate line, removing studied part
		line = line.substring(blockEnd);
		// user id is right after the block
		Matcher usrm = uip.matcher(line);
		if (!usrm.find()) {
			System.out.println("User id not found!");
		} else {
			userId = usrm.group();

		}

		// find user's url and photo
		Matcher uafm = uaf.matcher(line);
		if (uafm.find()) {
			println "Weibo contains an image"
			String r = uafm.group();
			Matcher um = Patterns.URL.value().matcher(r);
			if (um.find()) {
				url = um.group();
				if (um.find())
					w.imageUrl = um.group();
			} else {
				println "Error finding image in weibo";

			}

		} else {
			println "Weibo doesn't contain an image"
			//the first url in the line must be the user url
			Matcher urlm=urlp.matcher(line);
			if(urlm.find()){
				url=urlm.group();
			}else{
				println "Error finding user url";
			}
		}

		Matcher fm = fp.matcher(line);
		if (!fm.find()) {
			System.out.println("Not forwarded message!");

		} else {
			System.out.println("Forwarded message");
			w.setIsForwarded(true);

			int forwardStart = fm.end();
			line = line.substring(forwardStart).trim();
			Matcher fwm = ws.matcher(line);
			fwm.find();
			String ownerName = line.substring(0, fwm.start()).trim();

			System.out.println(ownerName);
			w.setOrgOwnerName(ownerName);
			line = line.substring(fwm.end()).trim();

			int fcEnd;
			Matcher im = image.matcher(line);
			if (im.find()) {
				fcEnd = im.start();
			} else {
				Matcher whitespace = ws.matcher(line);
				whitespace.find();
				fcEnd = whitespace.start();

			}
			String forwardContent = line.substring(0, fcEnd);
			w.setOrgContent(forwardContent);
			System.out.println(forwardContent);

			line = line.substring(fcEnd).trim();

		}
		//pass to user checking
		Map<String,String> userParams=new HashMap<>();
		userParams.put("userName", userName);
		userParams.put("userId", userId);
		userParams.put("url",url);
		userParams.put("faceUrl",photoUrl);
		checkUser(userParams);

		// check if another weibo is on the same line
		Matcher yabm = block.matcher(line);
		if (yabm.find()) {
			System.out.println("Another weibo item on the same line!");
			process(line);
		}
		Weibo.withTransaction{ w.save(); }
		if(w.hasErrors()){
			println "Got error saving weibo:";
			println w.errors;
			println "Weibo content: "+w.content;
		}
		else
			println "Weibo save complete"
		return w;
	}
	public static void checkUser(Map<String,String> params){
		String name=params.get("userName");
		String id=params.get("userId");
		String url=params.get("url");
		String face=params.get("faceUrl");
		User u;
		boolean changed;

		u=new User(weiboName:name,weiboId:id,url:url,faceUrl:face);
		//check if unique
		User.withTransaction {
			User dup=User.find("from User as d where d.weiboId=?",[id]);
			if(dup!=null){
				println "User with same id ${id} already exists.";
			}
			else{
				println "New user. Need to save into db.";
				u.save();
				if(u.hasErrors())
					println u.errors;
				else
					println "User saved"
			}
		}
	}
	public static boolean verifyId(User u,String id){
		boolean changed;
		if(u.weiboId==null||u.weiboId==""){
			if(id!=""&&id!=null){
				u.setWeiboId(id);
				changed=true;
				println "Updated weiboId for user ${u.name}";
			}

		}
		else if(u.weiboId!=id){
			println "User's weibo id doesn't match the one just extracted from file. Please check user ${u.name}";
		}

		return changed;
	}
	public static void verifyUrl(User u,String url){

	}

	public static void readFile(String path) {
		File mb = new File(path);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(mb), "UTF8"));
			String line;
			int lineNum=0;
			while ((line = reader.readLine()) != null) {
				try{
					process(line);
					lineNum++;
				}catch(BrokenLineException e){
					println "Error on ${lineNum}"
				}catch(StringIndexOutOfBoundsException e){
					println "Error in locating username and main block on ${lineNum}";
				}catch(Exception e){
					println "Dunno what happened on line ${lineNum}";
					e.printStackTrace();
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	public static void main(String[] args) throws Exception {
		println "Thread running processing background weibo data files.";
		def timeStart = new Date();

		String path = "D:\\weibo\\UserWeibos201201";
		readFile(path);

		def timeEnd=new Date();
		println "Data processing finished with time of "+TimeCategory.minus(timeEnd, timeStart);
	}
}
