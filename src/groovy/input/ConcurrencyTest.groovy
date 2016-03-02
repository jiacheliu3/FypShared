package input
import static grails.async.Promises.*


class ConcurrencyTest {

	public static void main(String[] args){
		def p = task {
			Reader.main();
			String s="complete";
		}
		p.onError { Throwable err -> println "An error occured ${err.message}" }
		p.onComplete { result -> println "Promise returned $result" }
		for(int i in 1..100){
			println "Doing whatever i want"
		}// block until result is called
		def result = p.get()
		println result;
	}
}
