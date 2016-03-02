package codebigbrosub

import groovy.json.JsonSlurper

class BackgroundController {
	String jsonPath="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\userFiles\\json\\"

	def init={ render "Here read the json and generate all other objects" }

	//read the user json
	public static User read(File jsonFile){
		String json=jsonFile.getText("UTF8");
		def jsonSlurper = new JsonSlurper();
		def object = jsonSlurper.parseText(json);

		//generate User, Weibo and Tags
		User user=new User();
		user.weiboName=object.userName;
		user.weiboId=object.userId;
		user.faceUrl=object.faceUrl;
		user.url=object.url;

		user.keywords=object.keywords;
		user.weibos=new HashSet<>();
		user.tags=object.tags;
		//String w=object.weibo;


		user.save();
		if(user.hasErrors())
			println user.errors;
		else
			println "User saved";

		//for each tag establish usertag
		ArrayList<String> tagList=new ArrayList<>();
		tagList.addAll(user.tags);
		tagList.each{
			log.info "Find tag with name ${it}";
			Tag t=Tag.find("from Tag as t where t.name=?",[it]);
			t.addToUsers(user).save();
			//println t.users;
		}

		return user;


	}
	//load types from file
	public static loadTypes(){
		File typeFile=new File("C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\type\\types.txt");

		def content;
		typeFile.withReader("UTF-8"){content=it.readLines();}
		content.each{
			String[] s=it.split(" ");
			Tag t=new Tag(name:s[1]);

			t.save();
			//println t.name+" completed"
			if(t.hasErrors()){
				println t.errors;
			}


		}
		println "All types loaded"
	}

	public static convert(String name){
		File jsonFile=new File("C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\userFiles\\${name}.json");
		String json=jsonFile.getText("UTF8");
		def jsonSlurper = new JsonSlurper();
		def object = jsonSlurper.parseText(json);
		return object;
	}
}
