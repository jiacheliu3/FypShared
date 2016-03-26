package input;

import java.util.regex.Pattern;

public enum Patterns {
	DATE("\\d{4}-\\d{1,2}-\\d{1,2}"),
	TIME("\\d{2}:\\d{2}:\\d{2}"),
	NUM("\\d+"),
	URL("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"),
	USERID("\\d{9,11}"),
	WEIBOID("\\d{16}"),
	AT("@[\\u4e00-\\u9fa5a-zA-Z\\-_0-9]+"),
	PUNC("[^\\u4e00-\\u9fa5a-zA-Z0-9]+"),
	TEXT("[A-Za-z0-9\u4e00-\u9fa5]"),
	STARTID("\\d*||"),
	ENDID("||\\d*");
	
	
	Pattern pat;
	String reg;
	Patterns(String s){
		reg=s;
		pat=Pattern.compile(s);
	}
	public Pattern value(){
		return pat;
	}
	public String reg(){
		return reg;
	}
}
