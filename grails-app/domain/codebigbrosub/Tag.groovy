package codebigbrosub

class Tag {

	String name;
	//static belongsTo=[weibo:Weibo]
	static hasMany=[users:User]
	
	static constraints = {
		name(blank:false,nullable:false,unique:true)
		users(blank:true,nullable:true);
	}
	
	@Override
	public String toString(){
		return name;
		
	}
	@Override
	public int hashCode(){
		return name.hashCode();
	}
}
