package toolkit

import groovy.util.logging.Log4j

@Log4j
class SessionChecker {
	public static void printSession(def session){
		Enumeration keys = session.getAttributeNames();
		while (keys.hasMoreElements())
		{
		  String key = (String)keys.nextElement();
		  log.info(key + ": " + session.getAttribute(key));
		}
	}

}
