package crawler

import groovy.util.logging.Log4j

import java.sql.SQLException
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.core.NestedRuntimeException
import org.springframework.dao.DataAccessException

import timeline.DateExtractor
import toolkit.TextChecker
import codebigbrosub.Weibo
import crawler.CrackedCookieRepo.CrackedCookie

@Log4j
class WeiboCrawler {
	String userName;
	WeiboCrawlerMaster boss;
	boolean fastMode;
	boolean forceSave;
	boolean needSave;

	int retry=5;
	int timeout=2000;

	CrackedCookie cookies;

	//Pattern to extract @user
	static Pattern repostFromUser=Pattern.compile("//@");


	public boolean extractFromWeibo(Element weibo,Element zone){
		//check if user name exists
		if(userName==null||userName==""){
			log.error "No user name for the weibo!";
			return false;
		}

		/* Study the information first */
		log.debug "The weibo is "+weibo.text();
		String text=zone.text();


		//find forward message
		String allText=weibo.text();
		//check if is a repost
		def isForward={t->
			//utf16 as unicode
			if(t.startsWith('\u8f6c\u53d1\u4e86')){
				log.debug "Got repost indicator";
				return true;
			}else
				return false;

		}
		boolean isRepost=isForward(allText);

		//extract url from the weibo
		def commentZone=weibo.select("a[class=cc][href~=uid]");

		//create a friend map to store the elements and names
		List<Element> friends=[];
		if(commentZone.size()==0){
			log.error "No comment zone found";//cannot continue if no comment zone?
			return false;
		}else{
			log.debug "Found comment zone. Continue extracting data from weibo.";
		}

		Element comment;
		comment=commentZone.get(0);
		String url=comment.attr("href");
		//extract id from the url
		int widEnd=url.indexOf("uid")-1;
		int widStart=url.indexOf("comment")+8;
		String weiboId=url.substring(widStart,widEnd);
		if(weiboId==null||weiboId==""){
			log.error "Weibo id is not found for url";
			return false
		}
		log.info "Got id of this weibo "+weiboId+" at ${url}";
		String repostComment;//greater scope for the sake of save
		if(isRepost){
			log.info "This weibo is repost. Extract @ from repost and content.";
			def alLDiv=weibo.select("div");
			Element repostZone=weibo.select("div").get(2);
			String repostText=repostZone.text();
			//extract wanted zone
			def getMiddle={t->
				int start=t.indexOf('\u8f6c\u53d1\u7406\u7531');//转发理由
				if(start<0)
					return "";
				int end=t.indexOf('\u8d5e\u005b');//赞[
				//println "Found string in between ${start} and ${end}";
				String wanted=t.substring(start+5,end);
				//log.debug "Result string is ${wanted}";
				return wanted;
			}
			repostComment=getMiddle(repostText);
			//extract the @s from both the repost and org content
			Elements atsRepost=extractAtFromRepost(repostComment,repostZone);
			Elements atsContent=extractAtFromContent(zone);
			boss.storeAts(atsRepost);
			boss.storeAts(atsContent);
			//store in the list
			friends.add(atsRepost);
			friends.add(atsContent);
			log.info "Extracted friend links: ${atsRepost}, ${atsContent}";
		}else{
			log.info "This weibo is original. Extract @ from content.";
			Elements atsContent=extractAtFromContent(zone);
			boss.storeAts(atsContent);
			friends.add(atsContent);
			log.info "Extracted friend links ${atsContent}";
		}

		/* Persistence related work flow */
		//check if same weibo already exists
		boolean foundDup=false;
		Weibo dup;
		Weibo.withTransaction{
			dup=Weibo.find("from Weibo as w where w.weiboId=?",[weiboId]);
			if(dup!=null){
				foundDup=true;
			}
		}

		//if a weibo element exists already, update it
		if(foundDup){
			log.info "Same weibo already exists!";

			//check if the full html element is there
			boolean hasFullElement=false;
			if(dup.fullElement==null||dup.fullElement==""){
				log.error "The weibo doesn't have its html element stored!";
			}else{
				hasFullElement=true;
			}
			//check date before leaving the weibo
			boolean hasDate=false;
			if(dup.createdTime!=null){
				def d=dup.createdTime;
				int y=d.getYear();
				//if the year is less than 100 then the date is wrong, needs update
				if(y<100){
					log.error "The weibo has a date that is before year 2000!";
				}else{
					hasDate=true;
				}
			}
			boolean needsUpdate=false;
			if(hasDate==false){
				Date d=getDateFromSpan(weibo);
				dup.createdTime=d;
				//save the change
				log.debug "Adding date to existing weibo.";
				needsUpdate=true;
			}
			if(hasFullElement==false){
				dup.fullElement=weibo.toString();
				log.debug "Adding html element to existing weibo";
				needsUpdate=true;
			}
			//update the element
			if(needsUpdate){
				log.info "Now update the weibo item";
				boolean updated=saveWeibo(dup);
				if(updated){
					log.info "Successfully updated the existing weibo element.";
					return true;
				}else{
					log.error "Failed to update existing weibo.";
					return false;
				}
			}
			else{
				log.info "No need to update the existing weibo.";
				return true;
			}
		}
		else{
			//create weibo item and store
			Weibo w=new Weibo();
			w.weiboId=weiboId;
			w.url=url;
			w.ownerName=userName;
			/* added in 1.2 */
			w.fullElement=weibo.toString();

			//extract time from contents
			Date d=getDateFromSpan(weibo);
			w.createdTime=d;

			def getOrgOwnerName={t->
				//println t;
				int start=t.indexOf('\u8f6c\u53d1\u4e86');
				int end=t.indexOf('\u7684\u5fae\u535a');
				//println "Owner name from ${start} to ${end}";
				String name=t.substring(start+3,end);
				return name;
			}

			if(isRepost){
				log.debug "Contain forward message";
				w.isForwarded=true;
				w.content=repostComment;
				w.orgContent=zone.text();

				//get org owner
				Element orgOwnerSpan=weibo.select("div").get(1);
				def reg=orgOwnerSpan.select("span[class=cmt]");
				if(reg.size()==0){
					log.debug "Org owner name span not located. ";
				}else{
					def nameSpanReg=reg.get(0).select("a");
					if(nameSpanReg.size()==0){
						log.debug "Org owner name span is found but no hyperlink element located.";
					}else{
						String orgName=nameSpanReg.get(0).text();
						log.debug "Got org owner name ${orgName}";
						//orgOwnerName=getOrgOwnerName(weibo.text());
						w.orgOwnerName=orgName;
					}

				}
			}else{
				w.content=zone.text();
			}
			//convert the friend list to a map
//			Map<String,String> friendMap=convertFriendMap(friends);
//			w.friendMap=friendMap;

			//save weibo element
			boolean weiboSaved=saveWeibo(w);
			if(weiboSaved){
				log.info "Saved the new weibo.";
				return true;
			}else{
				log.error "Failed to save the new weibo!";
				return false;
			}
		}


	}
	//convert a list of html elements to a map that can be stored in db together with the weibo element
	public Map<String,String> convertFriendMap(List<Element> friends){
		Map<String,String> friendMap=new HashMap<>();
		friends.each{element->
			//extract name from the element
			String text=element.text();
			text=text.replaceAll("@","");
			friendMap.put(text,element.toString());
		}
		log.info "Got ${friendMap.size()} friends outta the friend elements";
		return friendMap;
	}
	public Elements extractAtFromRepost(String text,Element zone){

		if(text==""){
			return [];
		}
		//find how many are needed, count the @ before the 3rd //
		Matcher repostMatcher=repostFromUser.matcher(text);
		int atCount=0;
		int pos=0;//record how long of string to keep if too long
		int x=0;
		boolean shouldCut;
		while(repostMatcher.find()){
			if(x>=2){
				shouldCut=true;
				pos=repostMatcher.start();
				break;
			}
			x++;
		}
		String kept=text;
		if(shouldCut){
			kept=text.substring(0,pos);
			log.debug "Kept only first 2 reposts from repost content ${kept}";
		}

		//count the occurrence of @
		int withoutAt=kept.replaceAll("@","").length();
		atCount=kept.length()-withoutAt;
		if(atCount<0)
			atCount=0;
		log.debug "Found ${atCount} @ in the text";
		//keep the first n links from content
		Elements ats=zone.select("a");
		log.debug "Going to select the first ${atCount} @ from ${ats.size()}.";
		def returnList=ats[0..atCount-1];
		if(atCount==0)
			return [];
		else
			return returnList;

	}
	//simply extract all @s from the content
	public Elements extractAtFromContent(Element zone){
		Elements ats=zone.select("a");
		log.debug "Extract ${ats.size()} @s from content";
		return ats;
	}

	public Date getDateFromSpan(Element weibo){

		def timeHolder=weibo.select("span[class=ct]");
		if(timeHolder.size()==0){
			log.info "No date or time found.";
			return null;
		}else{
			String timeSpan=timeHolder.get(0).text();
			Date d=DateExtractor.extractDate(timeSpan);
			if(d!=null){
				log.info "Date ${timeSpan} matching format";
				return d;
			}else{
				log.debug "${timeSpan} is not a formatted date. Need to add to the date regex list!";
				return null;
			}
		}
	}
	public boolean saveWeibo(Weibo w){
		if(!needSave){
			log.debug "Crawler set to no-save mode. No need to save the weibo item.";
			return true;
		}
		boolean weiboSaved;
		//save
		try{
			Weibo.withTransaction{
				boolean valid=w.validate();
				w.merge(flush:true);
				if(w.hasErrors()){
					log.error "Weibo has error:"+w;
					log.error w.errors;
					weiboSaved=false;
				}else{
					log.info "Weibo saved successfully.";
					weiboSaved=true;
				}
			}
		}catch(NestedRuntimeException e){
			log.error "Caught spring related exception.";
			log.error e.getMessage();
			weiboSaved=false;

		}catch(SQLException e){
			log.error "SQL exception thrown when saving the weibo.";
			e.printStackTrace();
			weiboSaved=false;	
		}catch(DataAccessException e){
			log.error "Data access exception saving the weibo.";
			e.printStackTrace();
			weiboSaved=false;
		}
		if(weiboSaved){
			log.info "Weibo is successfully saved.";
			boss.gotOneMore();
			return true;
		}else{
			log.error "Weibo is not saved! Try again with all surrogate utf8 chars filtered.";
			boolean savedAgain=saveWeiboAgain(w);
			if(savedAgain==false){
				log.error "Error saving the weibo even with utf-8 content filtered.";
				return false;
			}else{
				log.info "Successfully saved weibo on the 2nd time.";
				boss.gotOneMore();
				return true;
			}
		}
	}
	public boolean saveWeiboAgain(Weibo w){
		log.debug "Trying to replace all non-utf8 char in the weibo.";
		log.debug "The weibo has content: ${w.content}//${w.orgContent}";
		def content=w.content;
		w.content=TextChecker.filterContent(content);
		def orgContent=w.orgContent;
		w.orgContent=TextChecker.filterContent(orgContent);
		log.debug "After filtering now the weibo has content: ${w.content}//${w.orgContent}";

		//check the possible wrong class matching
		def full=w.fullElement;
		w.fullElement=TextChecker.ensureStringClass(full);
		def repost=w.repostElement;
		w.repostElement=TextChecker.ensureStringClass(repost);
		def like=w.likeElement;
		w.likeElement=TextChecker.ensureStringClass(like);
		def comment=w.commentElement;
		w.commentElement=TextChecker.ensureStringClass(comment);

		deepCheck(w);

		//save again
		boolean saved;
		log.debug "Now try to save again.";
		Weibo.withTransaction{
			w.merge(flush:true);
			if(w.hasErrors()){
				log.error "Still errors saving the weibo";
				log.error w.errors;
			}else{
				log.info "Succeeded on the 2nd time.";
				saved=true;
			}
		}
		return saved;
	}
	public void deepCheck(Weibo w){
		log.debug "Deep check the class of weibo item.";
		//compulsory fields
		def weiboId;
		def ownerName;
		def url;
		log.debug "Compulsory fields are: id:${weiboId}(${weiboId?.class}, owner:${ownerName}(${ownerName?.class}), url:${url}(${url?.class}))";
		//content
		def content=w.content;
		log.debug "Content: ${content}(${content?.class})";

		//repost info
		boolean isForwarded;
		if(isForwarded){
			def orgContent=w.orgContent;
			def orgOwnerName;
			log.debug "Org content: ${orgContent}(${orgContent?.class}), Org owner: ${orgOwnerName}(${orgOwnerName?.class})";
		}

		//html elements
		def full=w.fullElement;
		def repost=w.repostElement;
		def like=w.likeElement;
		def comment=w.commentElement;
		log.debug "FULL(${full?.class}): ${full}";
		log.debug "REPOST(${repost?.class}):  ${repost}";
		log.debug "COMMENT(${comment?.class}): ${comment}";
		log.debug "LIKE(${like?.class}): ${like}";


		//friends
		def friendMap=w.friendMap;
		if(friendMap instanceof Map){
			if(friendMap.size()==0){
				log.debug "Friend map is empty map.";
			}else{
				int count=0;
				for(def e:friendMap){
					if(count>0)
						break;
					def k=e.key;
					def v=e.value;
					log.debug "Friend map is map of key: ${k}(${k?.class}), value: ${v}(${v?.class})";
				}
			}
		}else{
			log.debug "Friend map is ${friendMap}(${friendMap?.class})";
		}
		//date
		def createdTime;
		log.debug "Created time is ${createdTime}(${createdTime?.class})";

	}

	public void setCookies(CrackedCookie cookies){
		this.cookies=cookies;
	}
	public Map crawlLikePage(String likeUrl,Weibo w,boolean needCrawl){
		boolean gotLike=false;
		def likeMap=[:];
		def liked=[:];
		def liking=[:];
		String likeElement;
		Element likeBody;
		if(needCrawl){
			Document doc=PageRetriever.fetchPage(likeUrl);
			if(doc==null){
				log.error "No like page fetched from ${likeUrl}";
			}else{
				likeBody=doc.body();
				likeElement=likeBody.toString();
				log.debug "Got like page "+likeBody.text();
			}
		}
		else{
			likeElement=w.likeElement;
			try{
				likeBody=Jsoup.parse(likeElement);
				log.debug "Got like page from user field "+likeBody.text();
			}catch(Exception e){
				log.error "Error parsing the like element of weibo ${w}";
				e.printStackTrace();
			}

		}
		if(likeBody==null){
			log.error "No like element is found!";
		}else{
			//extract info from the element body
			Elements likes=likeBody.select("div[class=c]:not([id^=M]");
			likes.each{attitude->
				//a valid zone for like will contain an <a> and a <span>
				boolean isValid=checkValidLikeZone(attitude);
				if(isValid){
					Element a=attitude.select("a").get(0);
					String name=a.text();
					if(!liking.containsKey(name)){
						liking.put(name,1);
					}else{
						liking[name]=liking[name]+1;
					}
				}else{
					//nothing
				}
			}

		}
		likeMap.put("element",likeElement);
		likeMap.put("liking",liking);
		likeMap.put("liked",liked);
		return likeMap;
	}
	public boolean checkValidLikeZone(Element zone){
		def a=zone.select("a");
		def span=zone.select("span[class=ct]");
		if(a.size()!=1){
			log.debug "No user name zone in this like element.";
			return false;
		}else if(span.size()!=1){
			log.debug "No time span in this like element.";
			return false;
		}else{
			//log.debug "Valid like element: ${zone}";
			return true;
		}
		
	}
	public Map crawlRepostPage(String forwardUrl,Weibo w,boolean needCrawl){
		boolean gotForward=false;
		def repostMap=[:];
		def forwarding=[:];
		def forwarded=[:];
		String repostElement;
		Element repostBody;
		if(needCrawl){
			Document doc=PageRetriever.fetchPage(forwardUrl);
			if(doc==null){
				log.error "No forward page got. Cannot proceed.";
			}else{
				repostBody=doc.body();
				log.debug "Got the repost body: "+repostBody.text();
				//store the element with the weibo
				w.repostElement=repostBody.toString();
				repostElement=repostBody.toString();
			}

		}else{
			repostElement=w.repostElement;//in string format
			try{
				repostBody=Jsoup.parse(repostElement);
				log.debug "Got the repost body from user field: "+repostBody.text();
			}catch(Exception e){
				log.error "Error parsing the like element of weibo ${w}";
				e.printStackTrace();
			}
		}
		//check if any content got
		if(repostBody==null){
			log.error "No repost body element got for weibo ${w}!";
		}
		else{
			//extract info from the body element
			String subquery1="c";
			Elements forwards=repostBody.select("div[class=${subquery1}]");
			//iterate in reverse order
			int siz=forwards.size();
			if(siz==0){
				println "No forward found";
			}
			else{
				boolean forwardStart;
				boolean forwardEnd;
				for(int i=siz-1;i>0&&forwardEnd==false;i--){
					Element f=forwards.get(i);
					//log.debug "Content of this forward element is "+f;
					String t=f.text();
					println "Text of this element is"+ t;
					int index=t.indexOf(":");
					int neg=t.indexOf("@");
					if(index==-1||neg<index){
						if(forwardStart){
							println "Now reached the end of forwardings.";
							forwardEnd=true;
						}else{
							println "No forward in this one";
						}
					}
					else{
						if(!forwardStart){
							println "Now start the forward messages.";
							forwardStart=true;
						}
						log.debug "Located : in text "+t;
						String fName=t.substring(0,index).trim().replaceAll("\\?","");
						if(fName.contains("\\u8f6c\\u53d1\\u4e86")||fName.contains(" ")){
							println "Text ${fName} is not a user name. Dispose.";
						}
						else{
							println "User ${fName} forwarded this user's weibo.";
							//record
							//										u.forwarded.add(fName);
							//										nameList.add(fName);
							if(!forwarded.containsKey(fName)){
								forwarded.put(fName,1);
							}else{
								int fNow=forwarded[fName];
								forwarded[fName]=fNow+1;
							}
						}
						//find whose weibo this user has forwarded
						Pattern fwp=Pattern.compile("//@\\S+?:");
						Matcher fwm=fwp.matcher(t);
						while(fwm.find()){
							String fwt=fwm.group();
							if(fwt.length()<=3){}
							else{
								String fwn=fwt.substring(3,fwt.length()-1);

								log.info "User has forwarded ${fwn}'s weibo.";
								if(!forwarding.containsKey(fName)){
									forwarding.put(fName,1);
								}else{
									int fNow=forwarding[fName];
									forwarding[fName]=fNow+1;
								}
							}
						}
					}
				}
			}
		}
		repostMap.put("forwarding",forwarding);
		repostMap.put("forwarded",forwarded);
		repostMap.put("element",repostElement);
		return repostMap;
	}
}
