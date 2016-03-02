package crawler

import groovy.util.logging.Log4j
import codebigbrosub.User

@Log4j
class UserGenerator {

	public static User generateUser(def data){
		User u;
		User.withTransaction{
			//create new user
			u=new User();
			u.weiboName=data["userName"];
			u.weiboId=data["userId"];
			u.url=data["userUrl"];
			u.faceUrl=data["faceUrl"];
			//get correct face url
			
			u.save(flush:true);
			if(u.hasErrors()){
				log.error "An error occurred creating the unknown user!";
				log.error u.errors;
				return;
			}
			else{
				log.info "Successfully created user.\n"+u;
			}
		}
		return u;
	}
}
