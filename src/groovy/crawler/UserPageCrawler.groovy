package crawler

import groovy.util.logging.Log4j
import input.Patterns

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.core.NestedRuntimeException

import segmentation.SepManager
import toolkit.DocumentChecker
import toolkit.UrlConverter
import codebigbrosub.User
import crawler.CrackedCookieRepo.CrackedCookie

@Log4j
class UserPageCrawler {
	static def provNames=["北京":"Beijing","天津":"Tianjin","重庆":"Chongqing","上海":"Shanghai","河北":"Hebei","山西":"Shanxi","辽宁":"Liaoning",
		"吉林":"Jilin","黑龙江":"Heilongjiang","江苏":"Jiangsu","浙江":"Zhejiang","安徽":"Anhui","福建":"Fujian","江西":"Jiangxi","山东":"Shandong",
		"河南":"Henan","湖北":"Hubei","湖南":"Hunan","广东":"Guangdong","海南":"Hainan","四川":"Sichuan","贵州":"Guizhou","云南":"Yunnan","陕西":"Shaanxi","甘肃":"Gansu",
		"青海":"Qinghai","台湾":"Taiwan","内蒙古":"Inner Mongol","广西":"Guangxi","西藏":"Xizang","宁夏":"Ningxia","新疆":"Xinjiang","香港":"Xianggang","澳门":"Macau"];

	static List<String> unknown=new ArrayList<>();
	static int currentYear=2016;
	static int retry=3;

	//the cookies are not really used
	public Document fetchDocument(String url,CrackedCookie cookies){
		boolean urlIsUseful=UrlConverter.isUseful(url);
		if(!urlIsUseful){
			log.info "The url ${url} is not useful.";
			return null;
		}
		url=UrlConverter.fillUrl(url);
		Document doc=PageRetriever.fetchPage(url);
		if(doc==null){
			log.error "Still no luck to get the page from ${url}";
		}
		return doc;
	}
	public Document getUserPage(String url){
		//get the page
		def cookies=CrackedCookieRepo.getOneCookie();
		Document doc;
		boolean gotPage=false;
		doc=fetchDocument(url,cookies);
		if(doc==null){
			return null;
		}
		//extract data from page
		Elements zones=doc.select("div[class=tip]");
		Elements tips=doc.select("a[href~=info]");
		log.info "${zones.size()} areas of user";
		if(zones.size()==0&&tips.size()>0){
			log.debug "This is possibly just the user entrance page.";
			String link=tips[0].attr("href");
			doc=fetchDocument(link,cookies);
			if(doc==null){
				return null;
			}
			zones=doc.select("div[class=tip]");
			if(zones.size()>0){
				log.info "Redirected to user stat page.";
				gotPage=true;
			}else{
				log.info "Still no luck to get the user page";
			}
		}else if(zones.size()>0){
			log.info "Found the user page.";
			gotPage=true;
		}else{
			log.info "The page seems irrelevant";
			log.info doc.text();
		}

		if(gotPage)
			return doc;
		else
			return null;
	}
	public Document checkSavedElement(String userName){
		log.debug "Checking if user with name ${userName} has html user page saved already.";
		//validate and filter the name firstly
		String name=userName;
		name=name.replace('@',"");
		log.debug "Looking for existing user ${name}";
		User u;
		User.withTransaction{
			u=User.find("from User as u where u.weiboName=?",[name]);
		}
		if(u==null){
			log.info "No user named ${name} so no html user page element.";
			return null;
		}else{
			String raw=u.infoElement;
			boolean valid=DocumentChecker.validateHtmlElement(raw);
			if(valid){
				Document doc=Jsoup.parse(raw);
				log.info "User ${name} has valid info page stored.";
				return doc;
			}else{
				log.info "No info page stored for user ${name}";
				return null;
			}
		}
	}
	//study links to users and get their stats
	public Map studyLinkToUsers(List<UserLink> links){
		log.info "Ready to study ${links?.size()} friends of user";
		//keep unique links
		Set<UserLink> uniqueLinks=new HashSet<>();
		uniqueLinks.addAll(links);
		log.info "${uniqueLinks.size()} unique links to study";

		def all=[:];
		for(int i=0;i<uniqueLinks.size();i++){
			Element link=uniqueLinks[i];
			//check if the link is meaningful
			String url=link.attr('href');
			log.info "The next url to go to is "+url;
			boolean urlIsUseful=UrlConverter.isUseful(url);
			if(!urlIsUseful){
				log.info "Url ${url} is not useful.";
				continue;
			}
			
			Document doc;
			//first check if user has html element
			Document savedDoc=checkSavedElement(link.text());
			//if not saved
			boolean needSave;
			if(savedDoc==null){
				//study the page
				url=UrlConverter.fillUrl(url);
				doc=getUserPage(url);
				needSave=true;
			}
			else{
				doc=savedDoc;
				needSave=false;
			}
			
			def data=studyUserPage(doc);
			//merge with all data
			String name;
			if(data['basic'].containsKey('nickName')){
				name=data['basic']['nickName'];
				all.put(name,data);

				//save the info page with user
				if(needSave){
					log.info "Saving info page element to user.";
					boolean saved=saveUserInfo(name,doc);
					if(!saved){
						log.error "Failed saving user info page!";
					}else{
						log.info "Info page saved to user ${name}";
					}
				}else{
					log.info "No need to save the info page to user.";
				}
				
			}else{
				log.error "No name included in the study result.";
				log.error data;
			}
			//sleep if the page is from webpage
			if(needSave){
				sleep(1000);
			}
		}
		log.info "Now merge the data collected from each user page.";
		def merged=aggregateAllData(all);
		log.info "The data is merged:"+merged;
		return merged;
	}
	//save the document with user
	public boolean saveUserInfo(String userName,Document page){
		log.info "Saving user info page with user ${userName}";
		//delete useless elements from the page
		Elements n=page.select("div[class=n]");
		if(n.size()>0){
			n[0].remove();
		}
		Elements pms=page.select("div[class=pms]");
		if(pms.size()>0){
			pms[0].remove();
		}
		Elements b=page.select("div[class=b]");
		if(b.size()>0){
			b[0].remove();
		}
		log.info "Removed useless elements from the doc";
		//save with user
		User.withTransaction{
			boolean hasDup=false;
			boolean needSave=false;
			User u;//store the existing user
			def dupUser=User.findAll("from User as u where u.weiboName=?",[userName]);
			if(dupUser.size()>1){
				log.error "Found more than one user with name ${userName}:${dupUser}";
				return false;
			}
			//this is the correct case where an existing user is there
			else if(dupUser.size()>0){
				hasDup=true;
				log.info "Located existing user";
				u=dupUser[0];
				def infoElement=u.infoElement;
				boolean validInfo=DocumentChecker.validateHtmlElement(infoElement);
				if(validInfo){
					log.debug "The user already has a valid info page";
					return true;
				}else{
					u.infoElement=page.body().toString();
					boolean updatedDupUser=saveUser(u);
					if(!updatedDupUser){
						log.error "Existing user is now updated!";
						return false;
					}else{
						log.info "Existing user updated!";
						return true;
					}
				}
			}
			//this is the correct case where a new user should be created
			else{
				log.info "No existing user yet. Need to create one.";
				//get user weibo id from the page
				Elements a=page.select("div[class=cd]");
				boolean hasId;
				String weiboIdRaw;
				if(a.size()==0){
					log.error "No info zone in the page: ${page.toString()}";

				}else{
					//error here
					Element infoLink=a[0].select("a")?.get(0);
					if(infoLink==null){
						log.error "No <a> element in the div: "+a[0].toString();
					}else{
						hasId=true;
						weiboIdRaw=infoLink.absUrl("href");
					}

				}
				//extract id from user info page link
				boolean canSave=false;
				User newUser;
				if(hasId){
					log.info "Extract id from link ${weiboIdRaw}";
					//create user and save
					newUser=new User();
					newUser.weiboName=userName;
					Pattern idPattern=Patterns.USERID.value();
					Matcher idMatcher=idPattern.matcher(weiboIdRaw);
					String wid;
					if(idMatcher.find()){
						wid=idMatcher.group();
						newUser.weiboId=wid;
						newUser.infoElement=page.toString();
						//url
						String wUrl=UrlConverter.userIdToUrl(wid);
						//face url: later
						Elements c=page.select("div[class=c]");
						Element avatar=c?.get(0);
						if(avatar!=null){
							log.info "Got user avatar ${avatar.toString()}";
							newUser.faceUrl=avatar.attr("href");
						}else{
							log.info "No avatar url found for user.";
						}

						canSave=true;
					}else{
						log.error "No weibo id found in string ${weiboIdRaw}";
						canSave=false;
					}

				}else{
					log.error "No weibo id div found.";
					canSave=false;
				}

				//save if required fields are got
				if(canSave){
					boolean savedNewUser=saveUser(newUser);
					if(savedNewUser==false){
						log.error "Failed to save the new user!";
						return false;
					}else{
						log.info "Successfully saved the new user";
						return true;
					}
				}else{
					log.info "No need to save the new user.";//return true if no need to save the user
					return true;
				}

			}
		}

	}
	public boolean saveUser(User u){
		try{
		u.merge(flush:true);
		if(u.hasErrors()){
			log.error "An error occurred saving the new user.";
			log.error u.errors;
			return false;
		}else{
			log.info "Saved the user.";
			return true;
		}
		}catch(NestedRuntimeException e){
			log.error "Caught spring related exception saving the user.";
			log.error e.getMessage();
			return false;

		}
	}
	//collect stats from each user's info page
	public Map studyUserPage(Document page){
		def data=["basic":[:],"education":[:],"work":[:],"other":[:]];
		if(page==null){
			log.info "User page is null!";
			return data;
		}
		//extract data from page
		Elements zones=page.select("div[class=tip]");
		log.info "${zones.size()} areas of user";

		for(int i=0;i<zones.size();i++){
			Element title=zones[i];
			Element content=title.nextElementSibling();
			log.info "Element zone ${title.text()} has content ${content.text()}";
			//check if it's the element that's desired
			if(content.attr("class")!='c'){
				log.error "This content element does not conform to format!";
				log.error content;
			}
			else{
				switch(title.text()){
					case '\u57FA\u672C\u4FE1\u606F'://基本信息
						data['basic']=studyBasicInfo(content);
						break;
					case '\u5DE5\u4F5C\u7ECF\u5386'://工作经历
						data['work']=studyWorkExp(content);
						break;
					case '\u5B66\u4E60\u7ECF\u5386'://学习经历
						data['education']=studyEducation(content);
						break;
					case '\u5176\u4ED6\u4FE1\u606F'://其他信息
						data['other']=studyOtherInfo(content);
						break;

				}


			}
		}
		return data;
	}
	public void mapIncrement(Map m,String key,double value){
		if(m.containsKey(key)){
			m[key]+=value;
		}else{
			m.put(key,value);
		}
	}
	//
	public Map aggregateAllData(Map all){
		def gender=["male":0,"female":0,"unknown":0];
		def age=[:];
		def geo=[:];

		//introduction
		String introRepo="";
		String introUser="";//store the user name whose introduction is the longest
		String introLong="";//store the content of the longest intro
		//authentication message
		int authCount=0;
		def authList=[];
		String authRepo="";
		String authUser="";//the user who has the longest auth message
		String authLong="";

		def tags=[:];
		def edu=[:];
		def work=[:];
		def workPeriod=[:];
		
		def tagNameMap=[:];

		all.each{name,data->
			def basic=data['basic'];
			//aggregate gender count
			def thisGender=basic["gender"];
			String sex=convertGender(thisGender);
			mapIncrement(gender,sex,1.0);
			//aggregate age information
			def thisBir=basic["birthday"];
			def thisAge=convertAge(thisBir);
			if(thisAge!=null){
				mapIncrement(age,thisAge,1.0);
			}
			//aggregate geo info
			def thisGeo=basic["location"];
			def thisProv=convertLocation(thisGeo);
			if(thisProv!=null){
				mapIncrement(geo,thisProv,1.0);
			}
			//aggregate brief intro
			String thisIntro=basic["intro"];
			introRepo=introRepo+thisIntro+" ";
			//find the longest intro
			if(thisIntro==null||thisIntro==""){
				//nothing
			}
			else if(thisIntro?.length()>introLong.length()){
				log.debug "Update the introUser to ${name}";
				introUser=name;
				introLong=thisIntro;
			}
			//aggregate authentication info
			String thisAuth=basic["auth"];
			if(thisAuth!=null){
				authCount++;
				authList.add(thisAuth);
				authRepo=authRepo+thisAuth+" ";
				if(thisAuth?.length()>authLong?.length()){
					log.debug "Update the authUser to ${name}";
					authUser=name;
					authLong=thisAuth;
				}
			}
			
			//aggregate tags
			def thisTags=basic["tags"];//list of string
			thisTags.each{
				mapIncrement(tags,it,1.0);
				if(tagNameMap.containsKey(it)){
					tagNameMap[it].add(name);
				}else{
					def l=[];
					l.add(name);
					tagNameMap.put(it,l);
				}
			}

			//on education data
			def thisEdu=data["education"];
			//aggregate education info
			thisEdu.each{school,period->
				//				String g=convertGrade(period);
				String g=school;//do not include grade info for now
				mapIncrement(edu,g,1.0);
			}

			//on working info
			def thisWork=data["work"];
			//aggregate working info
			thisWork.each{corp,period->
				String p=convertWorkPeriod(period);
				mapIncrement(work,corp,1.0);
				mapIncrement(workPeriod,p,1.0);
			}
		}

		//format the ages
		def formatAge=formatAge(age);

		//format the geographical info
		def formatGeo=formatGeo(geo);
		
		//format the tags
		def formatTags=formatTag(tags,tagNameMap);
		

		//get keywords from intro repo
		def introKeywords=SepManager.getSepManager().extractKeywords(introRepo);
		log.info "The introduction makes keywords "+introKeywords;
		//get keywords from authentication repo
		def authKeywords=SepManager.getSepManager().extractKeywords(authRepo);
		log.info "The authentication message makes keywords "+authKeywords;
		//format auth and intro info
		def longestIntro=["content":introLong,"user":introUser];
		def longestAuth=["content":authLong,"user":authUser];

		def intro=["introKeyword":introKeywords,"introLong":longestIntro,"introUser":introUser];
		def auth=["authKeyword":authKeywords,"authLong":longestAuth,"authUser":authUser,"authCount":authCount];


		def result=['gender':gender,'age':formatAge,'geo':formatGeo,'tag':formatTags,'edu':edu,'work':work,'workPeriod':workPeriod,"auth":auth,"intro":intro,"allCount":all.size()];
	}
	//filter the tags avoiding too many outputs
	public Map formatTag(Map tags,Map tagNameMap){
		Map sorted=tags.sort { a, b -> b.value <=> a.value }
		String maxTag=tags.max {it.value}.key;
		List maxNameList=tagNameMap[maxTag];
		log.debug "Tag ${maxTag} has most count from ${maxNameList}.";
		def newTags=[:];
		int count=0;
		for(def e:sorted){
			if(count>=10){
				break;
			}
			newTags.put(e.key,e.value);
			count++;
		}
		def tagMap=[:];
		tagMap.put("tags",newTags);
		tagMap.put("maxTag",maxTag);
		tagMap.put("names",maxNameList);
		return tagMap;
	}
	public Map formatGeo(Map geo){
		def geoMap=[:];
		def list=[];
		def unknown=[:];
		//get the valid province names
		Set<String> validNames=new HashSet<>();
		provNames.each{k,v->
			validNames.add(v);
		}
		log.debug "The valid province names are: "+validNames;
		geo.each{k,v->
			if(validNames.contains(k)){
				def pos=[];
				pos.add(k);
				pos.add(v);
				list.add(pos);
			}else{
				unknown.put(k,v);
			}
		}
		geoMap.put("known",list);
		geoMap.put("unknown",unknown);
		return geoMap;
	}
	public Map formatAge(Map age){
		int unknownCount;
		if(age.containsKey("0")){
			unknownCount=age["0"];
			age.remove("0");
		}
		def knownCount=[];
		age.each{k,v->
			if(k=="0"){
				log.error "The age 0 is not removed!";
			}else{
				log.debug "The age ${k} appeared ${v} times";
				for(int i=0;i<v;i++){
					knownCount.add(k);
				}
			}
		}
		def ageMap=["known":knownCount,"unknown":unknownCount];
		return ageMap;
	}
	public String convertLocation(String geo){
		//pre-processing
		String kept;
		if(geo==null){
			return "";
		}else if (geo.contains(" ")){
			def parts=geo.split(" ");
			kept=parts[0];
			kept=kept.replaceAll("·", "");
		}else{
			kept=geo;
		}
		//重庆 渝中区
		if(provNames.containsKey(kept)){
			return provNames[kept];
		}else{
			log.error "Found unknown place "+kept;
			unknown.add(kept);
			return kept;
		}

	}
	//not used
	public String convertGrade(String grade){
		if(grade==null)
			return "";
		//01级
		grade=grade.replaceAll("·", "");


	}
	//calculate the working length of one
	public String convertWorkPeriod(String period){
		if(period==null)
			return -2+"";
		//2001年-至今
		if(period.contains("\u4FDD\u5BC6")){
			log.debug "The period is kept as secret";
			return -1+"";

		}
		else{
			return 0+"";
		}
	}
	public String convertGender(String gender){
		if(gender.contains('\u7537')){//男
			return "male";
		}else if(gender.contains('\u5973')){//女
			return "female";
		}else{
			log.error "What is gender ${gender}?";
			return "unknown";
		}
	}
	public String convertAge(String date){
		if(date==null)
			return 0+"";
		date=date.trim();
		if(date.matches("\\d{4}-\\d{2}-\\d{2}")){
			log.debug "Got a year in the birthday! ${date}";
			String year=date.substring(0,4);
			int y=Integer.parseInt(year);
			if(y<1900){
				log.error "Invalid year of date ${date}";
				return 0+"";
			}
			int d= currentYear-y;
			log.debug "Extracted user's age ${d}!";
			return d+"";
		}else{
			log.info "The data has not matching pattern: ${date}";
			return 0+"";
		}
	}
	public static class UserLink{
		String name;
		String link;
		public UserLink(String uName,String uLink){
			name=uName;
			link=uLink;
		}
		@Override
		int hashCode(){
			return name.hashCode()+link.hashCode();
		}
	}
	public List element2string(content){
		String raw=content.toString();
		raw=raw.replaceAll("<br[/ ]*>", "\n");
		raw=raw.replaceAll("<.*?>", "");
		raw=raw.replaceAll("\uFF1A", ":");//Replace full angle colon to half angle one
		raw=raw.replaceAll("&nbsp;", " ");//Replace html space by real space
		raw=raw.replaceAll("&.*?;", "");//Remove html punctuations
		def parts=raw.split("\n");
		//make sure there is no whitespace in the 1st part
		if(parts.size()>0)
			parts[0]=parts[0].replace(" ", "");
		log.info "The raw content has parts\n"+parts;
		return parts;
	}
	public List breakLine(String line){
		def parts=line.split(':',2);
		if(parts.size()>0)
			parts[0]=parts[0].replace(" ","");
		//log.debug "The line is splitted into ${parts}";
		if(parts.size()>2){
			log.error "More than 2 parts from line splitting! Size: ${parts.size()} ${parts}";
		}
		return parts;
	}
	public String cleanString(String raw){
		if(raw==""||raw==" ")
			return "";
		if(raw.startsWith(" "))
			raw=raw.replaceFirst("[ \\t]+", "");
		return raw.replaceAll("·", "");
	}
	public Map studyWorkExp(Element content){
		// Eg
		//·重庆市协和心理顾问事务所 2001年-至今
		//·北京乐闻携尔教育咨询有限公司
		def data=[:];
		def parts=element2string(content);
		for(int i=0;i<parts.size();i++){
			String line=cleanString(parts[i]);
			if(line==""||line==" "){
				continue;
			}
			def subLine=line.split(" ");
			if(subLine.size()==1){
				data.put(line, "")
			}else{
				String work=subLine[0];
				String dateRange=subLine[1];
				data.put(work,dateRange);
			}

		}
		log.info "Finished work experience data extraction: "+data;
		return data;
	}
	public Map studyEducation(Element content){
		//·重庆文理学院 77级
		//·北京大学 01级
		//·清华大学
		def data=[:];
		def parts=element2string(content);
		for(int i=0;i<parts.size();i++){
			String line=cleanString(parts[i]);
			if(line==""||line==" "){
				continue;
			}
			def subLine=line.split(" ");

			if(subLine.size()==1){
				data.put(subLine[0], "");

			}else{
				String edu=subLine[0];
				String dateRange=subLine[1];
				data.put(edu, dateRange);
			}
		}
		log.info "Finished education data extraction: "+data;
		return data;
	}
	public Map studyOtherInfo(Element content){
		log.error "What to do with the nonsense here!";
		return [:];
	}
	public Map studyBasicInfo(Element content){
		def data=[:];
		def parts=element2string(content);
		boolean hasTags;
		for(int i=0;i<parts.size();i++){
			String line=parts[i];
			if(line==""||line==" ")
				continue;
			def subLine=breakLine(line);
			String key=subLine[0];
			String value=subLine[1];
			switch(key){
				case '\u6635\u79F0'://昵称
					data.put('nickName', value);
					break;
				case '\u751F\u65E5'://生日
					data.put('birthday',value);
					break;
				case '\u6027\u522B'://性别
					data.put("gender", value);
					break;
				case '\u5730\u533A'://地区
					data.put("location", value);
					break;
				case '\u8BA4\u8BC1\u4FE1\u606F'://认证信息
					data.put("authInfo", value);
					break;
				case '\u8BA4\u8BC1'://认证
					data.put("auth", value);
					break;
				case '\u7B80\u4ECB'://简介
					data.put("intro", value);
					break;
				case '\u6807\u7B7E'://标签
					log.info "Found tags in the zone";
					hasTags=true;
					break;
				//				default:
				//					if(isMeaningful(key)){
				//					println "Assigning unknow key:${key} value:${value}";
				//					data.put(key, value);
				//}
			}

		}
		if(hasTags){
			def links=content.select("a");
			if(links.size()<1){
				log.error "No link to more tags found";
			}else{
				Element more=links[links.size()-1];
				String t=more.text();
				if(t=="\u66F4\u591A"+">>"){
					log.info "Found link to more tags "+t;
					String tagLink=more.attr('href');
					log.info "Will go to full tag page "+tagLink;
					def tags=studyTagLink(tagLink);
					data.put("tags", tags);
				}



			}
		}
		log.info "Finished basic data extraction: "+data;
		return data;

	}
	public List studyTagLink(String url){
		boolean urlIsUseful=UrlConverter.isUseful(url);
		if(!urlIsUseful){
			log.info "The url ${url} is not useful.";
			return [];
		}
		url=UrlConverter.fillUrl(url);
		Document doc=PageRetriever.fetchPage(url);
		if(doc==null){
			log.error "No document fetched!";
			return [];
		}
		Elements zones=doc.select("div[class=c]");
		Element tagsZone;
		for(int i=0;i<zones.size();i++){
			Element e=zones[i];
			String t=e.text();
			if(t.contains('\u7684\u6807\u7B7E')){
				log.debug "Found the zone of tags";
				tagsZone=e;
			}
		}
		if(tagsZone==null){
			log.error "No zone found that contains all tags.";
		}else{
			//			def parts=element2string(tagsZone);
			//			println "${parts.size()} lines for tag extraction";

			//			for(int i=0;i<parts.size();i++){
			//				String line=parts[i];
			String line=tagsZone.text();
			line=line.replaceAll("&.*?;", "");//remove all html elements
			//				if(line==""||line==" ")
			//					continue;
			def subLine=line.split(":",2);
			if(subLine.size()<2){
				log.error "No : found in the string ${line}";
			}else{
				String tags=subLine[1];
				tags=tags.replaceAll("\\pZ+", " ");
				tags=tags.replaceAll("[\\s ]+", "\t");
				def tagParts=tags.split("\t");//&nbsp; was replaced by space
				tagParts=tagParts.findAll{it->it!=""};
				log.info "Found ${tagParts.size()} tags for user: ${tagParts}";
				return tagParts;
			}
			//			}

		}
		return [];
	}
	//decide whether the key has any meaning
	public boolean isMeaningful(String key){
		if(key=="")
			return false;
		else if(key==" ")
			return false;
		else if(key.startsWith("&"))
			return false;
		return true;

	}
	//Test with a few users
	public static void main(String[] args){

		String raw1="""
		<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!-- saved from url=(0031)http://weibo.cn/1652431847/info -->
<html xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><meta http-equiv="Cache-Control" content="no-cache"><meta id="viewport" name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0, maximum-scale=2.0"><link rel="icon" sizes="any" mask="" href="http://h5.sinaimg.cn/upload/2015/05/15/28/WeiboLogoCh.svg" color="black"><meta name="MobileOptimized" content="240"><title>刘文勇的资料</title><style type="text/css" id="internalStyle">html,body,p,form,div,table,textarea,input,span,select{font-size:12px;word-wrap:break-word;}body{background:#F8F9F9;color:#000;padding:1px;margin:1px;}table,tr,td{border-width:0px;margin:0px;padding:0px;}form{margin:0px;padding:0px;border:0px;}textarea{border:1px solid #96c1e6}textarea{width:95%;}a,.tl{color:#2a5492;text-decoration:underline;}/*a:link {color:#023298}*/.k{color:#2a5492;text-decoration:underline;}.kt{color:#F00;}.ib{border:1px solid #C1C1C1;}.pm,.pmy{clear:both;background:#ffffff;color:#676566;border:1px solid #b1cee7;padding:3px;margin:2px 1px;overflow:hidden;}.pms{clear:both;background:#c8d9f3;color:#666666;padding:3px;margin:0 1px;overflow:hidden;}.pmst{margin-top: 5px;}.pmsl{clear:both;padding:3px;margin:0 1px;overflow:hidden;}.pmy{background:#DADADA;border:1px solid #F8F8F8;}.t{padding:0px;margin:0px;height:35px;}.b{background:#e3efff;text-align:center;color:#2a5492;clear:both;padding:4px;}.bl{color:#2a5492;}.n{clear:both;background:#436193;color:#FFF;padding:4px; margin: 1px;}.nt{color:#b9e7ff;}.nl{color:#FFF;text-decoration:none;}.nfw{clear:both;border:1px solid #BACDEB;padding:3px;margin:2px 1px;}.s{border-bottom:1px dotted #666666;margin:3px;clear:both;}.tip{clear:both; background:#c8d9f3;color:#676566;border:1px solid #BACDEB;padding:3px;margin:2px 1px;}.tip2{color:#000000;padding:2px 3px;clear:both;}.ps{clear:both;background:#FFF;color:#676566;border:1px solid #BACDEB;padding:3px;margin:2px 1px;}.tm{background:#feffe5;border:1px solid #e6de8d;padding:4px;}.tm a{color:#ba8300;}.tmn{color:#f00}.tk{color:#ffffff}.tc{color:#63676A;}.c{padding:2px 5px;}.c div a img{border:1px solid #C1C1C1;}.ct{color:#9d9d9d;font-style:italic;}.cmt{color:#9d9d9d;}.ctt{color:#000;}.cc{color:#2a5492;}.nk{color:#2a5492;}.por {border: 1px solid #CCCCCC;height:50px;width:50px;}.me{color:#000000;background:#FEDFDF;padding:2px 5px;}.pa{padding:2px 4px;}.nm{margin:10px 5px;padding:2px;}.hm{padding:5px;background:#FFF;color:#63676A;}.u{margin:2px 1px;background:#ffffff;border:1px solid #b1cee7;}.ut{padding:2px 3px;}.cd{text-align:center;}.r{color:#F00;}.g{color:#0F0;}.bn{background: transparent;border: 0 none;text-align: left;padding-left: 0;}</style><script>if(top != self){top.location = self.location;}</script></head><body><div class="n" style="padding: 6px 4px;"><a href="http://weibo.cn/?tf=5_009" class="nl">首页<span class="tk">!</span></a>|<a href="http://weibo.cn/msg/?tf=5_010" class="nl">消息</a>|<a href="http://huati.weibo.cn/" class="nl">话题</a>|<a href="http://weibo.cn/search/?tf=5_012" class="nl">搜索</a>|<a href="http://weibo.cn/1652431847/info?rand=3913&amp;p=r" class="nl">刷新</a></div><div class="c"><img src="./刘文勇的资料_files/1" alt="头像"></div><div class="c">会员等级：6级&nbsp;<a href="http://weibo.cn/member/present/comfirmTime?uid=1652431847">送Ta会员</a><br><img src="./刘文勇的资料_files/433_s.gif" alt="微身份">&nbsp;<img src="./刘文勇的资料_files/553_s.gif" alt="五级会员">&nbsp;<img src="./刘文勇的资料_files/552_s.gif" alt="四级会员">&nbsp;<img src="./刘文勇的资料_files/534_s.gif" alt="二级会员">&nbsp;<img src="./刘文勇的资料_files/533_s.gif" alt="一级会员">&nbsp;<a href="http://weibo.cn/medal/owned?uid=1652431847">更多勋章</a></div><div class="tip">基本信息</div><div class="c">昵称:刘文勇<br>认证:北京乐闻携尔教育咨询有限公司创始人 刘文勇<br>性别:男<br>地区:北京 海淀区<br>生日:01-01<br>认证信息：北京乐闻携尔教育咨询有限公司创始人 刘文勇<br>简介:博士，现供职北京乐闻携尔教育咨询有限公司；原新东方兼职教师，集团培训师；邮箱：liuwenyong@lasedu.com；<br>标签:<a href="http://weibo.cn/search/?keyword=%E4%B9%90%E9%97%BB%E6%90%BA%E5%B0%94&amp;stag=1">乐闻携尔</a>&nbsp;<a href="http://weibo.cn/search/?keyword=%E9%BB%84%E9%87%91%E9%98%85%E8%AF%BB&amp;stag=1">黄金阅读</a>&nbsp;<a href="http://weibo.cn/search/?keyword=%E5%8B%87%E5%93%A5&amp;stag=1">勇哥</a>&nbsp;<a href="http://weibo.cn/account/privacy/tags/?uid=1652431847&amp;st=c71159">更多&gt;&gt;</a><br></div><div class="tip">工作经历</div><div class="c">·北京乐闻携尔教育咨询有限公司&nbsp;<br></div><div class="tip">其他信息</div><div class="c">互联网:http://weibo.com/liuwenyong<br>手机版:http://weibo.cn/liuwenyong<br><a href="http://weibo.cn/album/albumlist?fuid=1652431847">他的相册&gt;&gt;</a></div><div class="cd"><a href="http://weibo.cn/1652431847/info#top"><img src="./刘文勇的资料_files/5e990ec2.gif" alt="TOP"></a></div><div class="pms"> <a href="http://weibo.cn/">首页<span class="tk">!</span></a>.<a href="http://weibo.cn/topic/240489">反馈</a>.<a href="http://weibo.cn/page/91">帮助</a>.<a href="http://down.sina.cn/weibo/default/index/soft_id/1/mid/0">客户端</a>.<a href="http://weibo.cn/spam/?rl=1&amp;type=3&amp;fuid=1652431847" class="kt">举报</a>.<a href="http://passport.sina.cn/sso/logout?r=http%3A%2F%2Fweibo.cn%2Fpub%2F%3Fvt%3D&amp;entry=mweibo">退出</a></div><div class="c">设置:<a href="http://weibo.cn/account/customize/skin?tf=7_005&amp;st=c71159">皮肤</a>.<a href="http://weibo.cn/account/customize/pic?tf=7_006&amp;st=c71159">图片</a>.<a href="http://weibo.cn/account/customize/pagesize?tf=7_007&amp;st=c71159">条数</a>.<a href="http://weibo.cn/account/privacy/?tf=7_008&amp;st=c71159">隐私</a></div><div class="c">彩版|<a href="http://m.weibo.cn/?tf=7_010">触屏</a>|<a href="http://weibo.cn/page/521?tf=7_011">语音</a></div><div class="b">weibo.cn[03-02 21:41]</div></body></html>
""";
		Document doc1=Jsoup.parse(raw1);
		println "Document parse complete "+doc1.text();

		UserPageCrawler uc=new UserPageCrawler();
		def data1=uc.studyUserPage(doc1);
		println "Finally got data from user page "+data1;

		String raw2="""<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!-- saved from url=(0031)http://weibo.cn/1401753974/info -->
<html xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><meta http-equiv="Cache-Control" content="no-cache"><meta id="viewport" name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0, maximum-scale=2.0"><link rel="icon" sizes="any" mask="" href="http://h5.sinaimg.cn/upload/2015/05/15/28/WeiboLogoCh.svg" color="black"><meta name="MobileOptimized" content="240"><title>美国留学的资料</title><style type="text/css" id="internalStyle">html,body,p,form,div,table,textarea,input,span,select{font-size:12px;word-wrap:break-word;}body{background:#F8F9F9;color:#000;padding:1px;margin:1px;}table,tr,td{border-width:0px;margin:0px;padding:0px;}form{margin:0px;padding:0px;border:0px;}textarea{border:1px solid #96c1e6}textarea{width:95%;}a,.tl{color:#2a5492;text-decoration:underline;}/*a:link {color:#023298}*/.k{color:#2a5492;text-decoration:underline;}.kt{color:#F00;}.ib{border:1px solid #C1C1C1;}.pm,.pmy{clear:both;background:#ffffff;color:#676566;border:1px solid #b1cee7;padding:3px;margin:2px 1px;overflow:hidden;}.pms{clear:both;background:#c8d9f3;color:#666666;padding:3px;margin:0 1px;overflow:hidden;}.pmst{margin-top: 5px;}.pmsl{clear:both;padding:3px;margin:0 1px;overflow:hidden;}.pmy{background:#DADADA;border:1px solid #F8F8F8;}.t{padding:0px;margin:0px;height:35px;}.b{background:#e3efff;text-align:center;color:#2a5492;clear:both;padding:4px;}.bl{color:#2a5492;}.n{clear:both;background:#436193;color:#FFF;padding:4px; margin: 1px;}.nt{color:#b9e7ff;}.nl{color:#FFF;text-decoration:none;}.nfw{clear:both;border:1px solid #BACDEB;padding:3px;margin:2px 1px;}.s{border-bottom:1px dotted #666666;margin:3px;clear:both;}.tip{clear:both; background:#c8d9f3;color:#676566;border:1px solid #BACDEB;padding:3px;margin:2px 1px;}.tip2{color:#000000;padding:2px 3px;clear:both;}.ps{clear:both;background:#FFF;color:#676566;border:1px solid #BACDEB;padding:3px;margin:2px 1px;}.tm{background:#feffe5;border:1px solid #e6de8d;padding:4px;}.tm a{color:#ba8300;}.tmn{color:#f00}.tk{color:#ffffff}.tc{color:#63676A;}.c{padding:2px 5px;}.c div a img{border:1px solid #C1C1C1;}.ct{color:#9d9d9d;font-style:italic;}.cmt{color:#9d9d9d;}.ctt{color:#000;}.cc{color:#2a5492;}.nk{color:#2a5492;}.por {border: 1px solid #CCCCCC;height:50px;width:50px;}.me{color:#000000;background:#FEDFDF;padding:2px 5px;}.pa{padding:2px 4px;}.nm{margin:10px 5px;padding:2px;}.hm{padding:5px;background:#FFF;color:#63676A;}.u{margin:2px 1px;background:#ffffff;border:1px solid #b1cee7;}.ut{padding:2px 3px;}.cd{text-align:center;}.r{color:#F00;}.g{color:#0F0;}.bn{background: transparent;border: 0 none;text-align: left;padding-left: 0;}</style><script>if(top != self){top.location = self.location;}</script></head><body><div class="n" style="padding: 6px 4px;"><a href="http://weibo.cn/?tf=5_009" class="nl">首页</a>|<a href="http://weibo.cn/msg/?tf=5_010" class="nl">消息</a>|<a href="http://huati.weibo.cn/" class="nl">话题</a>|<a href="http://weibo.cn/search/?tf=5_012" class="nl">搜索</a>|<a href="http://weibo.cn/1401753974/info?rand=6806&amp;p=r" class="nl">刷新</a></div><div class="c"><img src="./美国留学的资料_files/0" alt="头像"></div><div class="c">会员等级：6级&nbsp;<a href="http://weibo.cn/member/present/comfirmTime?uid=1401753974">送Ta会员</a><br><img src="./美国留学的资料_files/1273_s.gif" alt="盘龙藏虎">&nbsp;<img src="./美国留学的资料_files/107_s.gif" alt="斗酒百篇">&nbsp;<img src="./美国留学的资料_files/538_s.gif" alt="三级会员">&nbsp;<img src="./美国留学的资料_files/1163_s.gif" alt="扬V耀5">&nbsp;<img src="./美国留学的资料_files/1162_s.gif" alt="理性爱国">&nbsp;<a href="http://weibo.cn/medal/owned?uid=1401753974">更多勋章</a></div><div class="tip">基本信息</div><div class="c">昵称:美国留学<br>性别:女<br>地区:北京<br>生日:0001-00-00<br>简介:QQ:1208598907<br>标签:<a href="http://weibo.cn/search/?keyword=%E7%BE%8E%E5%9B%BD%E7%95%99%E5%AD%A6&amp;stag=1">美国留学</a>&nbsp;<a href="http://weibo.cn/account/privacy/tags/?uid=1401753974&amp;st=600287">更多&gt;&gt;</a><br></div><div class="tip">学习经历</div><div class="c">·清华大学<br>·中国人民大学附属中学<br>·海淀实验中学<br>·中关村第三小学<br></div><div class="tip">工作经历</div><div class="c">·保密&nbsp;2008年-<br>·保密 保密 保密&nbsp;<br></div><div class="tip">其他信息</div><div class="c">互联网:http://weibo.com/cglx<br>手机版:http://weibo.cn/cglx<br><a href="http://weibo.cn/album/albumlist?fuid=1401753974">她的相册&gt;&gt;</a></div><div class="cd"><a href="http://weibo.cn/1401753974/info#top"><img src="./美国留学的资料_files/5e990ec2.gif" alt="TOP"></a></div><div class="pms"> <a href="http://weibo.cn/">首页</a>.<a href="http://weibo.cn/topic/240489">反馈</a>.<a href="http://weibo.cn/page/91">帮助</a>.<a href="http://down.sina.cn/weibo/default/index/soft_id/1/mid/0">客户端</a>.<a href="http://weibo.cn/spam/?rl=1&amp;type=3&amp;fuid=1401753974" class="kt">举报</a>.<a href="http://passport.sina.cn/sso/logout?r=http%3A%2F%2Fweibo.cn%2Fpub%2F%3Fvt%3D&amp;entry=mweibo">退出</a></div><div class="c">设置:<a href="http://weibo.cn/account/customize/skin?tf=7_005&amp;st=600287">皮肤</a>.<a href="http://weibo.cn/account/customize/pic?tf=7_006&amp;st=600287">图片</a>.<a href="http://weibo.cn/account/customize/pagesize?tf=7_007&amp;st=600287">条数</a>.<a href="http://weibo.cn/account/privacy/?tf=7_008&amp;st=600287">隐私</a></div><div class="c">彩版|<a href="http://m.weibo.cn/?tf=7_010">触屏</a>|<a href="http://weibo.cn/page/521?tf=7_011">语音</a></div><div class="b">weibo.cn[03-03 19:16]</div></body></html>
""";
		Document doc2=Jsoup.parse(raw2);
		println "Document parse complete "+doc2.text();
		def data2=uc.studyUserPage(doc2);
		println "Finally got 2nd data from user page "+data2;
	}

}

