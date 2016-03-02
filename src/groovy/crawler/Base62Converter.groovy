package crawler

import groovy.util.logging.Log4j

@Log4j
class Base62Converter {
	static String ALPHABET="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static String repo="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static int chickenSoup(String axiom){
		int sum=0;
		for(def s:axiom){
			int index=ALPHABET.indexOf(s.toUpperCase());
			if(index==-1){
				log.debug "${s} is not in the alphabet";
			}else{
				sum+=index+1;
			}
		}
		return sum;
	}
	public static ArrayList<String> rsplit(String raw,int interval){
		int siz=raw.length();
		int count=(siz-siz%interval)/interval;
		int left=siz%interval;
		log.debug "For raw string of length ${siz} cut ${count} times";
		def l=[];
		for(int i=0;i<count;i++){
			int start=siz-i*interval-interval;
			int end=siz-i*interval;
			//log.debug "Start is ${start} and end is ${end}";
			String s=raw.substring(start,end);
			//log.debug "Extracted "+s;
			l.add(s);
		}
		l.add(raw.substring(0,left));
		return l;
	}
	public static String convert(long number, int base)
	{
		
		long quotient = number / base;
		int remainder = number % base;
	
		if(quotient == 0) // base case
		{
			//return Long.toString(remainder);
			return repo[remainder];
		}
		else
		{
			return convert(quotient, base) + repo[remainder];
		}
	}
	public static String convertToId(String mid){
		def l=rsplit(mid,4);
		String r="";
		for(int i=l.size()-1;i>=0;i--){
			r+=mid2id(l[i]);
		}
		while(r[0]=='0'){
			r=r.substring(1);
		}
		return r;
	}
	public static String mid2id(String mid){
		long sum=0;
		int q=mid.length()-1;
		//log.debug "The first char converts to the ${q} power of 62";
		for(int i=0;i<mid.length();i++){
			def s=mid.getAt(i);
			//log.debug "Now convert char ${s}";
			long l=Math.pow(62, q-i)*repo.indexOf(s);
			//log.debug "Converted to ${l}";
			sum+=l;
		}
		String result=sum.toString();
		while(result.length()<7){
			result='0'+result;
		}
		return result;
	}
	public static convertToMid(String id){
		def l=rsplit(id,7);
		String r="";
		for(int i=l.size()-1;i>=0;i--){
			String s=id2mid(l[i]);
			//log.debug "Converted part to mid "+s;
			r+=s;
		}
		while(r[0]=='0'){
			r=r.substring(1);
		}
		return r;
	}
	public static String id2mid(String id){
		return convert(Long.parseLong(id),62);
	}
	public static convertToHKId(String mid){
		String convertedId=Base62Converter.convertToId(mid);
		log.debug "Converted to id"+convertedId;
		return "http://hk.weibo.com/p/weibostatus/content/index/"+convertedId;
	}
	public static void main(String[] args){
		String raw='CeaOU15IT';
		println rsplit(raw,4);
		println rsplit('3833781880260331', 7);
		println mid2id('15IT');
		println mid2id('eaOU');
		println convertToId('CeaOU15IT');
		println convertToMid('3833781880260331');
		
	}
}

