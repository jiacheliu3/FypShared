package codebigbrosub

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

class Weibo {

	String weiboId;
	String subId;
	Date createdTime;
	String ownerName;
	String url;
	String content;
	int forwardCount;
	int commentCount;
	//ArrayList<Comment> comments;
	String terminal;
	String imageUrl;//Url of image if any
	Tag tag;
	ArrayList<String> links;//links in the content
	
	
	//if the weibo is forwarded message
	boolean isForwarded;
	//Date forwardedTime;
	String orgOwnerName;
	String orgContent;

	static save(Weibo w){
		w.save();
	}
	static mapping={
		content sqlType:'text'
		orgContent sqlType:'text'
		
	}
	static constraints = {
		weiboId(blank:false,nullable:false,unique:true)
		subId(blank:false,nullable:true)
		ownerName(blank:false,nullable:false)
		createdTime(blank:true,nullable:true)
		content(blank:true,nullable:true)
		url(blank:true,nullable:true)
		imageUrl(blank:true,nullable:true)
		tag(blank:true,nullable:true)
		terminal(blank:true,nullable:true)
		orgOwnerName(blank:true,nullable:true)
		orgContent(blank:true,nullable:true)
	}
	
	
	public void addTag(String tag){
		tag=Tag.find {name==tag}
	}

	@Override
	public String toString(){
		"""ID:${weiboId}
			Owner:${ownerName}"""
	}
	@Override
	public int hashCode(){
		id.hashCode()+content.hashCode()
	}
	

}
