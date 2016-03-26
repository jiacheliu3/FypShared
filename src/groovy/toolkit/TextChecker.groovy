package toolkit

import groovy.util.logging.Log4j

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

@Log4j
class TextChecker {

	public static String checkUtf8(String text){
		try
		{
			byte[] bytes = text.getBytes("UTF-8");
			return true;
		}
		catch (UnsupportedEncodingException e)
		{
			println "The string is not encoded in utf-8";
			return false;
		}
	}
	public static String utf8Convert(String raw){
		boolean inUtf8=checkUtf8(raw);
		println "Will replace non-utf-8 string as \\uFFD";
		if(!inUtf8){
			println "The string ${raw} is not in utf-8!";
		}
		return raw.replaceAll("[^\\u0000-\\uFFFF]", "\uFFFD");
	}
	public static void main(String[] args){
		String test="walmart öbama 👽💔";
		println "Before filter: ${test}";
		String filtered=utf8Convert(test);
		println "The filtered string is ${filtered}";
	}
	//ensure the field is string
	public static String ensureStringClass(def content){
		String target;
		if(content==null||content==""){
			return "";
		}
		else if(content instanceof Element){
			log.debug "The content is jsoup Element! Convert to string.";
			target=content.toString();
		}else if(content instanceof Elements){
			log.debug "The content is jsoup ElementS! Convert to string array.";
			def raw=[];
			content.each{e->
				raw.add(e.toString());
			}
			target=raw.toString();
		}else if(content instanceof String){
			target=content;
		}else{
			log.debug "The content is not what's expected!";
			log.debug "${content.class}:${content}";
			target=content.toString();
		}
		return target;
	}
	public static String filterContent(def content){
		String target;
		if(content==null||content==""){
			return "";
		}
		else if(content instanceof Element){
			log.debug "The content is jsoup Element! Only keep text.";
			target=content.text();
		}else if(content instanceof Elements){
			log.debug "The content is jsoup ElementS! Convert to text array.";
			def raw=[];
			content.each{e->
				raw.add(e.text());
			}
			target=raw.toString();
		}else if(content instanceof String){
			target=content;
		}else{
			log.debug "The content is not what's expected!";
			log.debug "${content.class}:${content}";
			target=content.toString();
		}
		
		//filter utf 8 chars that are longer than 3 bytes
		return TextVerifier.replaceSurrogate(target);
	}

}
