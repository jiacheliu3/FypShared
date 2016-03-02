package codebigbrosub

class Comment {
	String ownerName;
	String content;

	
    static constraints = {
		ownerName(nullable:false,blank:false)
		content(nullable:true,blank:true)
    }

}
