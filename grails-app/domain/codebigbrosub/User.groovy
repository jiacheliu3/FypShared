package codebigbrosub

import toolkit.ConverterManager

class User {
	
	
	//weibo related info
	String weiboId;
	String weiboName;
	String url;
	String faceUrl;
	ArrayList<String> terminals=new ArrayList<String>();
	LinkedHashMap<String,Double> keywords=new LinkedHashMap<>();
	HashSet<String> tags=new HashSet<>();
	
	/* Updated in 1.2 */
	//weibo network
//	HashSet<String> forwarding=new HashSet<>();
//	HashSet<String> forwarded=new HashSet<>();
//	HashSet<String> commenting=new HashSet<>();
//	HashSet<String> commented=new HashSet<>();
//	
	/* Updated in 1.2 */
	//Now use maps to store interactions
	Map<String,String> forwarding=new HashMap<>();
	Map<String,String> forwarded=new HashMap<>();
	Map<String,String> commenting=new HashMap<>();
	Map<String,String> commented=new HashMap<>();
	Map<String,String> liking=new HashMap<>();
	Map<String,String> liked=new HashMap<>();
	Map<String,String> mentioning=new HashMap<>();
	Map<String,String> mentioned=new HashMap<>();
	
	/* Updated in 1.2 */
	String infoElement;

	//personal info
	String name;
	String phone;
	String email;
	
	static hasMany=[
		weibos:Weibo
	]
	static mapping={
//		keywords sqltype:'mediumblob';
		//keywords sqltype:'text';
		//keywords type:'text';
		keywords sqlType: 'VARBINARY(100000)'
		
		forwarding sqlType: 'blob'
		forwarded sqlType: 'blob'
		commenting sqlType: 'blob'
		commented sqlType: 'blob'
		liking sqlType: 'blob'
		liked sqlType: 'blob'
		mentioning sqlType: 'blob'
		mentioned sqlType: 'blob'
		
		//use binary because of incorrect string problem
		infoElement sqlType: 'blob'
		
	}
	static constraints = {
		//attributes that cannot be empty
		weiboId(blank:false,nullable:false,unique:true)
		weiboName(blank:false,nullable:false,unique:true)
		
		name(blank:true,nullable:true)
		phone(blank:true,nullable:true)
		email(blank:true,nullable:true)
		url(blank:true,nullable:true)
		faceUrl(blank:true,nullable:true)
		
		terminals(blank:true,nullable:true)
		tags(blank:true,nullable:true)
		keywords(blank:true,nullable:true)
		
		forwarding(blank:true,nullable:true)
		forwarded(blank:true,nullable:true)
		commenting(blank:true,nullable:true)
		commented(blank:true,nullable:true)
		liking(blank:true,nullable:true)
		liked(blank:true,nullable:true)
		mentioning(blank:true,nullable:true)
		mentioned(blank:true,nullable:true)
		
		infoElement(blank:true,nullable:true)
		
	}
	public static void main(String[] args){
		User.withTransaction{
			User u=new User();
			u.weiboId="444";
			u.weiboName="lalala";
			Map<String,Integer> fMap=new HashMap<>();
			fMap.put("lalala",1);
			u.forwarding=ConverterManager.integerMapToStringMap(fMap);
			u.save(flush:true);	
		}
	}
}
