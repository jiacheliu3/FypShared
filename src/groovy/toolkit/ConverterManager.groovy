package toolkit

class ConverterManager {
	//temp solution added in 1.2, conforming the problem that Grails only saves maps when the key and value are string
	public static integerMapToStringMap(Map<String,Integer> theMap){
		Map<String,String> newMap=new HashMap<>();
		theMap.each{k,v->
			newMap.put(k,v.toString());
		}
		return newMap;
	}
	public static stringMapToIntegerMap(Map<String,String> theMap){
		Map<String,Integer> newMap=new HashMap<>();
		theMap.each{k,v->
			newMap.put(k,Integer.parseInt(v));
		}
		return newMap;
	}
}
