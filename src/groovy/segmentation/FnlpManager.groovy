package segmentation

import org.fnlp.app.keyword.AbstractExtractor
import org.fnlp.app.keyword.WordExtract
import org.fnlp.nlp.cn.CNFactory
import org.fnlp.nlp.cn.tag.CWSTagger
import org.fnlp.nlp.corpus.StopWords

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

		//		// 创建中文处理工厂对象，并使用“models”目录下的模型文件初始化
		//		CNFactory factory = CNFactory.getInstance("models");
		//		String str="关注自然语言处理、语音识别、深度学习等方向的前沿技术和业界动态。";
		//		// 使用分词器对中文句子进行分词，得到分词结果
		//		String[] words = factory.seg(str);
		//
		//
		//		CWSTagger tagger=new CWSTagger(str);
		//
		//		// 打印分词结果
		//		for(String word : words) {
		//			System.out.print(word + " ");
		//		}
		//		System.out.println();

		checkStatus();

		String str="有俄罗斯国会议员，9号在社交网站推特表示，美国中情局前雇员斯诺登，已经接受委内瑞拉的庇护，不过推文在发布几分钟后随即删除。俄罗斯当局拒绝发表评论，而一直协助斯诺登的维基解密否认他将投靠委内瑞拉。　　俄罗斯国会国际事务委员会主席普什科夫，在个人推特率先披露斯诺登已接受委内瑞拉的庇护建议，令外界以为斯诺登的动向终于有新进展。　　不过推文在几分钟内旋即被删除，普什科夫澄清他是看到俄罗斯国营电视台的新闻才这样说，而电视台已经作出否认，称普什科夫是误解了新闻内容。　　委内瑞拉驻莫斯科大使馆、俄罗斯总统府发言人、以及外交部都拒绝发表评论。而维基解密就否认斯诺登已正式接受委内瑞拉的庇护，说会在适当时间公布有关决定。　　斯诺登相信目前还在莫斯科谢列梅捷沃机场，已滞留两个多星期。他早前向约20个国家提交庇护申请，委内瑞拉、尼加拉瓜和玻利维亚，先后表示答应，不过斯诺登还没作出决定。　　而另一场外交风波，玻利维亚总统莫拉莱斯的专机上星期被欧洲多国以怀疑斯诺登在机上为由拒绝过境事件，涉事国家之一的西班牙突然转口风，外长马加略]号表示愿意就任何误解致歉，但强调当时当局没有关闭领空或不许专机降落。";
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
		// 创建中文处理工厂对象，并使用“models”目录下的模型文件初始化
		CNFactory factory = CNFactory.getInstance("models");
		//				String str="关注自然语言处理、语音识别、深度学习等方向的前沿技术和业界动态。";
		ArrayList<String> result=new ArrayList<>();
		c.each{
			// 使用分词器对中文句子进行分词，得到分词结果
			String[] words = factory.seg(it);
			result.addAll(words);
		}
		return result;
	}
	public static ArrayList<String> justSegment(String s) throws Exception{
		checkStatus();
		println "Expect sentence segments from FNLP";
		// 创建中文处理工厂对象，并使用“models”目录下的模型文件初始化
		CNFactory factory = CNFactory.getInstance("models");
		//				String str="关注自然语言处理、语音识别、深度学习等方向的前沿技术和业界动态。";
		ArrayList<String> result=new ArrayList<>();

		// 使用分词器对中文句子进行分词，得到分词结果
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
