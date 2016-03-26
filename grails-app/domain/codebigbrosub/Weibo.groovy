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
	
	/* Added in 1.2 */
	/* Html elements:
	 * fullElement: the element that is extracted from weibo.cn page, the whole element containing the weibo contents
	 * commentElement: from the page where there is only one weibo item displayed
	 * repostElement and likeElement: from two other pages that linked to from comment page
	 * */
	String fullElement;
	String repostElement;
	String commentElement;
	String likeElement;
	Map<String,String> friendMap;//name:user page link pairs

	static save(Weibo w){
		w.save();
	}
	static mapping={
		content sqlType:'text'
		orgContent sqlType:'text'
		
		//use binary because of incorrect string problem
		fullElement sqlType: 'blob'
		commentElement sqlType:'blob'
		repostElement sqlType:'blob'
		likeElement sqlType:'blob'
		//use binary to store map in order to avoid utf8mb related mistakes
		friendMap sqlType:'blob'
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
		
		//fields added in 1.2
		fullElement(blank:true,nullable:true)
		repostElement(blank:true,nullable:true)
		commentElement(blank:true,nullable:true)
		likeElement(blank:true,nullable:true)
		friendMap(blank:true,nullable:true)
	}
	
	
	public void addTag(String tag){
		tag=Tag.find {name==tag}
	}

	@Override
	public String toString(){
		"ID:${weiboId}	Owner:${ownerName}	${content}//${orgContent}";
	}
	@Override
	public int hashCode(){
		id.hashCode()+content.hashCode()
	}
	

}
