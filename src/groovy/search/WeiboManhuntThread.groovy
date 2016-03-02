package search


import javax.servlet.http.HttpSession

class WeiboManhuntThread implements Runnable{
	String name;
	String id;
	String url;
	
	boolean hasName;
	boolean hasId;
	boolean hasUrl;
	
	HttpSession session;
	boolean isDone;

	public WeiboManhuntThread(HttpSession session){
		this.session=session;
	}
	public void run(){
		decide();
		//scan all users
		
		//generate report for the user
		
		//find similar users
		
		
	}
	public void decide(){
		if(name!=null)
			hasName=true;
		if(id!=null)
			hasId=true;
		if(url!=null)
			hasUrl=true;
	}
	public boolean check(){
		return isDone;
	}
	public void report(){
		//block till report is finished 
		
	}
}
