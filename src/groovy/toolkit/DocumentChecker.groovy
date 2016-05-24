package toolkit

import groovy.util.logging.Log4j
import input.Patterns

import java.util.regex.Matcher;
import java.util.regex.Pattern

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

@Log4j
class DocumentChecker {

	public static String getUserIdFromPage(Document doc){
		if(doc==null){
			log.debug "Document of user homepage is null!";
			return null;
		}
		
		def elements=doc.select("div[class=c] a[href~=comment]");
		log.debug "Found ${elements.size()} candidate links that contain the user id.";
		String uid;
		boolean found=false;
		Pattern idPattern=Pattern.compile("uid=\\d{9,10}");
		Pattern numPattern=Pattern.compile("\\d{9,10}");
		for(int i=0;i<elements.size()&&found==false;i++){
			Element link=elements[i];
			String href=link.attr("href");
			Matcher m=idPattern.matcher(href);
			if(m.find()){
				String here=m.group();
				Matcher inner=numPattern.matcher(here);
				if(inner.find()){
					uid=inner.group();
					log.info "Found user id ${uid}!";
					found=true;
				}
			}
		}
		return uid;
	}
	//check whether a string is a valid html element, return if valid
	public static boolean validateHtmlElement(String element){

		if(element==null||element=="")
			return new Boolean(false);
		//a proper informative html element cannot be without a div
		if(!element.contains("</div>")){
			return new Boolean(false);
		}
		//if the string is not null and it contains a <div>, deem it as ok
		return new Boolean(true);

	}
	//check if the cookie still works, if not the request will be redirected to login
	public static boolean checkPageContent(Document doc){
		String title=doc.title();
		if(title.contains('\u65b0\u6d6a\u901a\u884c\u8bc1')){//新浪通行证
			log.error "This page is useless. Better check what's wrong.";
			log.error doc.text();
			return false;
		}
		String content=doc.text().trim();
		if(content.startsWith('\u65b0\u6d6a\u901a\u884c\u8bc1')){//新浪通行证
			log.error "This page is useless. Better check what's wrong.";
			log.error doc.text();
			return false;
		}else if(title=="\u5fae\u535a"){//微博
			log.debug 'The title of page is "微博", need to further check.';
			//whitelist indicators
			String tag="\u7684\u6807\u7B7E";//的标签
			String info="\u7684\u4E2A\u4EBA\u8D44\u6599"//的个人资料
			if(content.contains(tag)&&content.contains(info)){
				log.debug 'Content contains "标签" and "个人资料", so it is valid.';
				return true;
			}else{
				log.debug "The content is deemed invalid:";
				String pageInText=doc.body().toString().replace("\n","");
				log.debug pageInText;
				return false;
			}
		}
		return true;
	}
}
