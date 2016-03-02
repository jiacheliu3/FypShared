package crawler

import groovy.time.TimeCategory
import groovy.util.logging.Log4j

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.core.NestedRuntimeException

import timeline.DateExtractor
import codebigbrosub.Weibo




@Log4j
class CrawlerThread implements Runnable{
	String url;
	// the start page and end page index
	int start;
	int end;
	//cookies to use in crawling
	def cookies;
	String userId;
	String userName;

	int retry;
	//keep a local record of how many are collected from this thread
	int gotCount=0;

	WeiboCrawlerMaster boss;
	
	//Pattern to extract @user
	static Pattern repostFromUser=Pattern.compile("//@");

	public void debrief(){
		log.info "I cover the url ${url} and page from ${start} to ${end}. I belong to user ${userId}:${userName}";
	}
	@Override
	public void run(){
		debrief();
		//timing start
		def startTime=new Date();

		for(int i=start;i<=end;i++){
			String nextUrl=url;
			boolean fetched;
			
			for(int j=0;j<retry&&fetched==false;j++){

				try{
					Document doc = Jsoup.connect(nextUrl)
							//.data("query", "Java")
							.data("page", i+"")
							.userAgent("Mozilla")
							.cookies(cookies)
							.timeout(1500)
							.post();
					log.info "Got page ${i} "+doc.text();
					//check if the page is valid
					boolean isUseful=WeiboCrawlerMaster.checkPageContent(doc);
					if(!isUseful){
						log.error "Uh-oh it seems I'm caught on page ${i}. ";
						sleep(30000);
						continue;
					}

					//extract weibo
					Elements contents=doc.select("span[class=ctt]");
					log.info "Found ${contents.size()} items.";
					//when page=1 there are 3 spans containing only the user info that should be disposed
					int startIndex;
					if(i==1)
						startIndex=3;
					else
						startIndex=0;
					for(int k=startIndex;k<contents.size();k++){
						Element zone=contents.get(k);
						Element weibo=zone.parent().parent();
						log.debug "The weibo is "+weibo.text();
						String text=zone.text();
						//extract url from the weibo
						def commentZone=weibo.select("a[class=cc][href~=uid]");
						Element comment;
						if(commentZone.size()==0){
							log.error "No comment zone found";
						}else{
							comment=commentZone.get(0);
							String url=comment.attr("href");
							//extract id from the url
							int widEnd=url.indexOf("uid")-1;
							int widStart=url.indexOf("comment")+8;
							String weiboId=url.substring(widStart,widEnd);
							if(weiboId==null||weiboId==""){
								log.error "Weibo id is not found for url";
								continue;
							}
							log.info "Got id of this weibo "+weiboId+" at ${url}";

							//check if same weibo already exists
							boolean foundDup=false;
							Weibo bro;
							Weibo.withTransaction{
								bro=Weibo.find("from Weibo as w where w.weiboId=?",[weiboId]);
								if(bro!=null){
									foundDup=true;
								}
							}
							if(foundDup){
								log.info "Same weibo already exists!";
								boss.gotOneMore();
								gotCount++;
								//check date before leaving the weibo
								if(bro.createdTime==null){
									Date d=getDateFromSpan(weibo);
									bro.createdTime=d;
									//save the change
									log.debug "Adding date to exisitng weibo.";
									boolean merged=saveWeibo(bro);
									if(!merged){
										println "Failed to update the date of weibo element";
									}

								}
								continue;
							}

							//create weibo item and store
							Weibo w=new Weibo();
							w.weiboId=weiboId;
							w.url=url;
							w.ownerName=userName;

							//extract time from contents
							Date d=getDateFromSpan(weibo);
							w.createdTime=d;

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
							def getOrgOwnerName={t->
								//println t;
								int start=t.indexOf('\u8f6c\u53d1\u4e86');
								int end=t.indexOf('\u7684\u5fae\u535a');
								//println "Owner name from ${start} to ${end}";
								String name=t.substring(start+3,end);
								return name;
							}
							boolean isRepost=isForward(allText);
							if(isRepost){
								log.debug "Contain forward message";
								//attach user comment to the contents
								//find the 2nd div in the zone
								def alLDiv=weibo.select("div");
								Element repostZone=weibo.select("div").get(2);
								
								String repostText=repostZone.text();
								//extract wanted zone
								def getMiddle={t->
									int start=t.indexOf('\u8f6c\u53d1\u7406\u7531');
									int end=t.indexOf('\u8d5e\u005b');
									//println "Found string in between ${start} and ${end}";
									String wanted=t.substring(start+5,end);
									//log.debug "Result string is ${wanted}";
									return wanted;
								}
								String repostComment=getMiddle(repostText);
								
								log.info "There are ${ats.size()} at in the repost zone.";
								//find those that are close to the user
								
								w.isForwarded=true;
								w.content=repostComment;
								w.orgContent=zone.text();
								//extract the @s from both the repost and org content
								Elements atsRepost=extractAtFromRepost(repostComment,repostZone);
								Elements atsContent=extractAtFromContent(zone);
								boss.storeAts(atsRepost);
								boss.storeAts(atsContent);
								
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
								Elements atsContent=extractAtFromContent(zone);
								boss.storeAts(atsContent);
							}
							//save weibo element
							boolean weiboSaved=saveWeibo(w);
							
							/* obsolete: Updating user will cause optimistic lock failures */
							//add this weibo to user
							//							if(weiboSaved){
							//								User.withTransaction{
							//									User u=User.find("from User as u where u.weiboName=?",[w.ownerName]);
							//									u.weibos.add(w);
							//									u.save();
							//									if(u.hasErrors()){
							//										log.error "Error when add weibo to the user.";
							//										log.error u.errors;
							//
							//									}else{
							//										println "Owner updated.";
							//									}
							//								}
							//							}

						}



					}
					fetched=true;

				}
				catch(SocketTimeoutException e){
					log.error "Time out for the ${j}th try of page ${i}.";
					sleep(1000);
					continue;
				}
				if(fetched==false){
					log.error "Failed to fetch the ${i}th page at last.";

				}

				//sleep
				sleep(2000);
			}


		}
		//timing end
		def endTime=new Date();
		def period=TimeCategory.minus(endTime, startTime);
		log.info "Crawler thread spent ${period}";

		//report the number collected from this thread
		log.info "Thread ${generateName()} collected ${gotCount} weibo items.";


	}
	public Elements extractAtFromRepost(String text,Element zone){
		//find how many are needed, count the @ before the 3rd //
		Matcher repostMatcher=repostFromUser.matcher(text);
		int atCount=0;
		int pos=0;//record how long of string to keep
		for(int x=0;x<2&&repostMatcher.find();x++){
			pos=repostMatcher.start();
			
		}
		String kept=text.substring(0,pos+1);
		log.debug "Kept first 3 reposts from repost content ${kept}";
		//count the occurrence of @
		int withoutAt=kept.replaceAll("@","").length();
		atCount=text.length()-withoutAt;
		if(atCount<0)
			atCount=0;
		log.debug "Found ${atCount} @ in the text";
		//keep the first n links from content
		Elements ats=zone.select("a");
		log.debug "Going to select the first ${atCount} @ from ${ats.size()}.";
		if(atCount==0)
			return [];
		else
			return ats[0..atCount]
		
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
		boolean weiboSaved;
		//save
		try{
			Weibo.withTransaction{
				boolean valid=w.validate();
				if(valid==false){
					log.error "Weibo doesn't pass validation! The error is "+w.errors;
				}else{
					w.merge();
					if(w.hasErrors()){
						log.error "Weibo has error:"+w;
						log.error w.errors;
					}else{
						log.info "Weibo saved successfully.";
						weiboSaved=true;
						boss.gotOneMore();
						gotCount++;
					}
				}

			}
			return true;
		}catch(NestedRuntimeException e){
			log.error "Caught spring related exception.";
			log.error e.getMessage();
			
		}finally{
			return weiboSaved;
		}
	}
	public String generateName(){
		return "User ${userName} for pages ${start}-${end}";
	}

}
