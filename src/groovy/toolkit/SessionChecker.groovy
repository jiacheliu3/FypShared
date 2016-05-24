package toolkit

import groovy.util.logging.Log4j

@Log4j
class SessionChecker {
	//print content of session for debug purpose
	static String logBase=PathManager.sessionLogTempFolder;
	public static void printSessionToFile(String raw){
		File sessionLog=new File(logBase+"session.log");
		if(!sessionLog.exists()){
			log.error "The session log doesn't exist.";
			sessionLog.createNewFile();
		}
		sessionLog.append(raw);
	}
	public static void printSession(def session,String titleLine){
		Enumeration keys = session.getAttributeNames();
		String raw=titleLine+"\n";
		while (keys.hasMoreElements())
		{
		  String key = (String)keys.nextElement();
		  raw=raw+key + ": " + session.getAttribute(key)+"\n";
		}
		printSessionToFile(raw);
	}

}
