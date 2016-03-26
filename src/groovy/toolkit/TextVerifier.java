package toolkit;

import org.apache.commons.lang.CharUtils;

public class TextVerifier {
	public static final String LAST_3_BYTE_UTF_CHAR = "\uFFFF";
	public static final String REPLACEMENT_CHAR = "\uFFFD"; 

	public static String toValid3ByteUTF8String(String s)  {
	    final int length = s.length();
	    StringBuilder b = new StringBuilder(length);
	    for (int offset = 0; offset < length; ) {
	       final int codepoint = s.codePointAt(offset);

	       // do something with the codepoint
	       if (codepoint > LAST_3_BYTE_UTF_CHAR.codePointAt(0)) {
	           b.append(REPLACEMENT_CHAR);
	       } else {
	           if (Character.isValidCodePoint(codepoint)) {
	               b.appendCodePoint(codepoint);
	           } else {
	               b.append(REPLACEMENT_CHAR);
	           }
	       }
	       offset += Character.charCount(codepoint);
	    }
	    return b.toString();
	}
	public static String replaceSurrogate(String text){
		if(text==null||text.equals(""))
			return "";
		return text.replaceAll( "([\\ud800-\\udbff\\udc00-\\udfff])", "");
	}
	public static void main(String args[]){
		String test="walmart öbama 👽💔";
		System.out.println("The test string is "+test);
		String filter=toValid3ByteUTF8String(test);
		System.out.println("After filter the string is "+filter);
		String str  = "𠀀"; //U+20000, represented by 2 chars in java (UTF-16 surrogate pair)
		System.out.println("Before filter the string is "+str);
		String after=replaceSurrogate(str);
		System.out.println(after); //0
		System.out.println("Before filter the string is "+test);
		after=replaceSurrogate(test);
		System.out.println(after); 
	}

}
