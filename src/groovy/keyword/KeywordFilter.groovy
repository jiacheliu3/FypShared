package keyword

import input.Patterns

import java.util.regex.Pattern

class KeywordFilter {
	static Pattern punc=Pattern.compile("[\\pP‘’“”]");
	public static filterList(Collection<String> rawList){
		def doneList=[];
		rawList.each{
			String s=filterPunc(it);
			s=filterStopWords(s);
			if(s.matches(".*"))
				s=s.toLowerCase();
			if(s!=null&&s!=""&&s!="null")
				doneList.add(s);
		}
		return doneList;
	}
	public static String filterStopWords(String content){
		//no stopword filtering supported so far
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
