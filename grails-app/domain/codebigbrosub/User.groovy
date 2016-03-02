package codebigbrosub

class User {
	
	
	//weibo related info
	String weiboId;
	String weiboName;
	String url;
	String faceUrl;
	ArrayList<String> terminals=new ArrayList<String>();
	LinkedHashMap<String,Double> keywords=new LinkedHashMap<>();
	HashSet<String> tags=new HashSet<>();
	
	//weibo network
	HashSet<String> forwarding=new HashSet<>();
	HashSet<String> forwarded=new HashSet<>();
	HashSet<String> commenting=new HashSet<>();
	HashSet<String> commented=new HashSet<>();
	
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
		forwarding sqlType: 'VARBINARY(10000)'
		forwarded sqlType: 'VARBINARY(10000)'
		commenting sqlType: 'VARBINARY(10000)'
		commented sqlType: 'VARBINARY(10000)'
		
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
		
		
	}
}
