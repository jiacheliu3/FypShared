package input

import groovy.time.TimeCategory

class ReadWeiboFileThread implements Runnable{
	public void run(){

		println "Thread running processing background weibo data files.";
		def timeStart = new Date();

		String path = "D:\\weibo\\UserWeibos201502";
		Reader.readFile(path);

		def timeEnd=new Date();
		println "Data processing finished with time of "+TimeCategory.minus(timeEnd, timeStart);
	}
}
