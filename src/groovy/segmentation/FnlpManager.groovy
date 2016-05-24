package segmentation

import org.fnlp.app.keyword.AbstractExtractor
import org.fnlp.app.keyword.WordExtract
import org.fnlp.nlp.cn.CNFactory
import org.fnlp.nlp.cn.tag.CWSTagger
import org.fnlp.nlp.corpus.StopWords

/* Deprecated:
 * This class is not used.
 * Disable all parts related to it.
 * */
class FnlpManager {
	static String base;
	static StopWords sw;
	static CWSTagger seg;

	public static void init(){
		def pool=["C:\\Users\\jiacheliu3\\git\\TextRank\\TextRank\\","C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\"];
		boolean initiated=false;
		int index=0;
		int size=pool.size();
		while(!initiated){
			if(index>=size){
				println "Failed to initialize at last";
				break;
			}
				
			setBase(pool[index]);
			initiated=debrief();
			index++
		}
	}
	public static void setBase(String path){
		base=path;
		File file=new File(base+"seg.m");
		if(file.exists()){
			println "Successfully set the base path to ${base}.";
		}else{
			println "The path is not valid!";
		}
		sw= new StopWords(base+"models\\stopwords");
		seg = new CWSTagger(base+"models\\seg.m");
	}
	public static boolean debrief(){
		println "Seg models are ${seg}, stopwords are ${sw}"
		return seg==null||sw==null;

	}
	public static void main(String[] args) throws Exception {

		//		// �������Ĵ��?�����󣬲�ʹ�á�models��Ŀ¼�µ�ģ���ļ���ʼ��
		//		CNFactory factory = CNFactory.getInstance("models");
		//		String str="��ע��Ȼ���Դ��?����ʶ�����ѧϰ�ȷ����ǰ�ؼ�����ҵ�綯̬��";
		//		// ʹ�÷ִ��������ľ��ӽ��зִʣ��õ��ִʽ��
		//		String[] words = factory.seg(str);
		//
		//
		//		CWSTagger tagger=new CWSTagger(str);
		//
		//		// ��ӡ�ִʽ��
		//		for(String word : words) {
		//			System.out.print(word + " ");
		//		}
		//		System.out.println();

		checkStatus();

		String str="�ж���˹�����Ա��9�����罻��վ���ر�ʾ�����������ǰ��Ա˹ŵ�ǣ��Ѿ�����ί�������ıӻ������������ڷ��������Ӻ��漴ɾ�����˹���־ܾ�����ۣ���һֱЭ��˹ŵ�ǵ�ά����ܷ�����Ͷ��ί����������������˹���������ίԱ����ϯ��ʲ�Ʒ��ڸ�������������¶˹ŵ���ѽ���ί�������ıӻ����飬�������Ϊ˹ŵ�ǵĶ����������½�չ���������������ڼ���������ɾ����ʲ�Ʒ�������ǿ�������˹��Ӫ����̨�����Ų�����˵�������̨�Ѿ��������ϣ�����ʲ�Ʒ���������������ݡ�����ί������פĪ˹�ƴ�ʹ�ݡ�����˹��ͳ�������ˡ��Լ��⽻�����ܾ�����ۡ���ά����ܾͷ���˹ŵ������ʽ����ί�������ıӻ���˵�����ʵ�ʱ�乫���йؾ���������˹ŵ������Ŀǰ����Ī˹��л��÷���ֻ����������������ڡ�����ǰ��Լ20������ύ�ӻ����룬ί��������������ϺͲ���ά�ǣ��Ⱥ��ʾ��Ӧ������˹ŵ�ǻ�û������������������һ���⽻�粨������ά����ͳĪ����˹��ר�������ڱ�ŷ�޶���Ի���˹ŵ���ڻ���Ϊ�ɾܾ���¼������¹��֮һ��������ͻȻת�ڷ磬�ⳤ�����]�ű�ʾԸ����κ������Ǹ����ǿ����ʱ����û�йر���ջ���ר���䡣";
		print justSegment(str);
	}
	public static checkStatus(){
		if(sw==null||seg==null||base==null||base=="")
			init();
	}
	public static ArrayList<String> separate(String content) throws Exception{
		checkStatus();
		//num is the integer no less than the sqrt of length
		int num=Math.ceil(Math.sqrt(content.length()));

		ArrayList<String> keywords=new ArrayList<String>();

		AbstractExtractor key = new WordExtract(seg,sw);

		//you need to set the keywords number, here you will get 10 keywords
		Map<String,Integer> ans = key.extract(content, num);

		for (Map.Entry<String, Integer> entry : ans.entrySet()) {
			String keymap = entry.getKey().toString();
			String value = entry.getValue().toString();
			keywords.add(keymap);
			//System.out.println("key=" + keymap + " value=" + value);
		}
		return keywords;
	}
	public static ArrayList<String> mash(String content) throws Exception{
		checkStatus();
		println "Expect keywords from FNLP";
		//use 0.3 as expected keyword ratio
		int num=content.length()*0.3;

		AbstractExtractor key = new WordExtract(seg,sw);

		//you need to set the keywords number, here you will get 10 keywords
		Map<String,Integer> ans = key.extract(content, num);

		ArrayList<String> result=new ArrayList<>();
		result.addAll(ans.keySet());

		return result;
	}
	//just segment the sentence
	public static ArrayList<String> justSegment(Collection<String> c) throws Exception{
		checkStatus();
		println "Expect sentence segments from FNLP";
		// �������Ĵ��?�����󣬲�ʹ�á�models��Ŀ¼�µ�ģ���ļ���ʼ��
		CNFactory factory = CNFactory.getInstance("models");
		//				String str="��ע��Ȼ���Դ��?����ʶ�����ѧϰ�ȷ����ǰ�ؼ�����ҵ�綯̬��";
		ArrayList<String> result=new ArrayList<>();
		c.each{
			// ʹ�÷ִ��������ľ��ӽ��зִʣ��õ��ִʽ��
			String[] words = factory.seg(it);
			result.addAll(words);
		}
		return result;
	}
	public static ArrayList<String> justSegment(String s) throws Exception{
		checkStatus();
		println "Expect sentence segments from FNLP";
		// �������Ĵ��?�����󣬲�ʹ�á�models��Ŀ¼�µ�ģ���ļ���ʼ��
		CNFactory factory = CNFactory.getInstance("models");
		//				String str="��ע��Ȼ���Դ��?����ʶ�����ѧϰ�ȷ����ǰ�ؼ�����ҵ�綯̬��";
		ArrayList<String> result=new ArrayList<>();

		// ʹ�÷ִ��������ľ��ӽ��зִʣ��õ��ִʽ��
		String[] words = factory.seg(s);
		result.addAll(words);

		return result;
	}
	public static ArrayList<String> mashCollection(Collection<String> c) throws Exception{
		checkStatus();
		println "Expect keywords from FNLP";
		int length=0;
		String content="";
		c.each{
			length+=it.length();
			content+=it+"\n";
		}

		//use 0.3 as ratio of keyword
		int num=0.3*length;
		println "Expect ${num} keywords from content lenghth of ${length}."

		AbstractExtractor key = new WordExtract(seg,sw);

		//you need to set the keywords number, here you will get 10 keywords
		Map<String,Integer> ans = key.extract(content, num);

		ArrayList<String> result=new ArrayList<>();
		result.addAll(ans.keySet());

		return result;
	}
}
