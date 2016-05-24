package keyword

import groovy.io.FileType
import groovy.util.logging.Log4j
import input.Patterns

import java.util.regex.Pattern

import toolkit.PathManager

@Log4j
class KeywordFilter {
	static TreeSet<String> uselessWords=new TreeSet<>();
	static Pattern punc=Pattern.compile("[\\pP‘’“”]");
	static Pattern pureEng=Pattern.compile("[A-Za-z]+");
	static{
		loadStopWords();
	}
	public static loadStopWords(){
		/* Deprecated way */
		//		//load useless words
		//		File stopWords=new File(uselessWordPath);
		//		String[] useless=stopWords.readLines();
		//		useless.each {
		//			uselessWords.add(it);
		//		}

		//load all files under this path into stop words
		String stopwordsBase=PathManager.stopWordBasePath;
		File stopFolder=new File(stopwordsBase);
		if(!stopFolder.exists()){
			log.error "No stop words folder found! No keyword filtering will be performed successfully!";
			return;
		}

		stopFolder.eachFile(FileType.FILES) {file->
			log.info "Loading stop words file: "+file.name;
			def lines=file.readLines('utf-8');
			//each line is a keyword
			lines.each{word->
				uselessWords.add(word);
			}
		}

		log.info "Keyword Filter is initialized with ${uselessWords.size()} stop words."
	}
	public static filterNestedList(Collection rawList){
		if(rawList==null){
			log.error "The input list for filtering is null!";
			return;
		}
		def nestedList=[];
		log.debug "Filtering stop words on ${rawList.size()} rows of keywords.";
		rawList.each{theList->
			def innerList=theList as ArrayList<String>;
			if(!(innerList instanceof List)&&!(innerList instanceof Set)){
				log.error "The inner list is of type ${innerList.class}";
			}
			def words=filterList(innerList as List);
			nestedList.add(words);
		}
		log.debug "Nested filtering complete getting ${nestedList.size()} results.";
		return nestedList;
	}
	public static filterList(Collection<String> rawList){
		def doneList=[];
		for(String word:rawList){
			if(!(word instanceof String)){
				log.error "Error! The input is of type ${word.class}";
				continue;
			}
			String s=filterPunc(word);
			if(s.matches("[+-]?\\d+%"))
				continue;//no pure percentage
			if(s.matches(".*"))//pure english word then convert to lower case
				s=s.toLowerCase();
			s=filterStopWords(s);
			if(s!=null&&s!=""&&s!="null")
				doneList.add(s);
		}
		return doneList;
	}
	public static String filterStopWords(String content){
		if(uselessWords.contains(content))
			return "";
		else
			return content;
	}
	public static String filterPunc(String raw){
		//replace all punctuations
		return punc.matcher(raw).replaceAll("");
	}
	public static String filterUrl(String content){
		//filter the url
		Pattern url=Patterns.URL.value();
		return url.matcher(content).replaceAll("");
	}
	public static boolean isUselessWord(String word){
		if(word.matches(pureEng)){
			word=word.toLowerCase();
		}
		return uselessWords.contains(word);
	}
	public static void main(String[] args){
		String raw=new String(""" / / 由 @ 黎耀祥 Wayne   @ 姚子羚 elaine   @ 楊秀惠 VivienYeo   @ Tony 洪 永城   @ 唐詩 詠 Natalie   @ 羅 鈞 滿 Moon   @ 張景淳 Stanley   @ 陳 庭欣 _ Toby   @ benjamin 袁伟豪   @ 岑杏賢 _ Jennifer   @ 李成昌   等 主演 的 電視劇 《 載得 有情人 》 將於 8 月 11 日 晚上 8 : 30 分在 高清 翡翠台 播映 
 昨天 出席 了   " 香港 防癌 會 52 次 AGM "   㳟 喜朱 楊 栢 瑜 女士 再 庹當選 主席 。 
 廣州 地鐵 
 陪 老婆 , 到 廣州 , 唔 拍戲 , 睇 玉器 [ 哈哈 ] [ 哈哈 ] [ 哈哈 ]   多謝 ( banana   wifi _ hk ) [ 嘻嘻 ] [ 嘻嘻 ] [ 嘻嘻 ] 
 / / 小靚 仔 [ 萌 ] [ 萌 ] [ 萌 ] [ 哈哈 ] [ """.getBytes(),'utf-8');
		def l=[];
		l.add(raw);
		println filterList(l);
	}
}
