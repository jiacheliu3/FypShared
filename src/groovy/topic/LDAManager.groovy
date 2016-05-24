package topic
import groovy.util.logging.Log4j

import java.util.regex.Matcher
import java.util.regex.Pattern

import toolkit.PathManager
import cc.mallet.pipe.CharSequence2TokenSequence
import cc.mallet.pipe.Pipe
import cc.mallet.pipe.SerialPipes
import cc.mallet.pipe.TokenSequence2FeatureSequence
import cc.mallet.pipe.TokenSequenceRemoveStopwords
import cc.mallet.pipe.iterator.ArrayIterator
import cc.mallet.topics.ParallelTopicModel
import cc.mallet.types.Alphabet
import cc.mallet.types.FeatureSequence
import cc.mallet.types.IDSorter
import cc.mallet.types.InstanceList
import cc.mallet.types.LabelSequence
import codebigbrosub.Weibo

@Log4j
class LDAManager {
	public static void generateFile(){
		String base=PathManager.ldaTempPath;
		String path=base+"/tag.txt";
		File tagFile=new File(path);
		if(tagFile.exists()){
			tagFile.delete()
		}else{
			
		}
		tagFile.createNewFile();
		tagFile.write("",'utf-8');
	}
	public static topicFlow(def wordbags){
		//def instances=feedFromWeibo(weibos);
		def instances=feedFromSegments(wordbags);
		
		//decide number of topics
		int numTopics=5;//set to 20 by default
		def topics=extractTopics(instances,numTopics);
		
		return topics;
	}
	public static InstanceList feedFromWeibo(Collection<Weibo> weibos){
		//put weibo into list of strings
		List<String> contents=new ArrayList<>();
		log.info "Feeding ${weibos.size()} weibo items into topic model.";
		weibos.each{weibo->
			String content="";
			if(weibo.isForwarded){
				content=weibo.content+"//"+weibo.orgContent;
			}else{
				content=weibo.content;
			}
			//pre-process the weibo items
		}
	}
	public static InstanceList feedFromSegments(List<List<String>> wordbags){
		//put each string list into one string as one line
		def lines=[];
		wordbags.each{words->
			if(words.size()==0){
				//do nothing
			}
			else{
				String line=words.join(" ");
				lines.add(line);
			}
		}
		
		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		//pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+")) );
		pipeList.add( new TokenSequence2FeatureSequence() );
		//generate instances
		InstanceList instances = new InstanceList (new SerialPipes(pipeList));

		//improve the lines by filtering punctuations
		lines=preprocessText(lines);
		println "Now the data is "+lines;
		//instances.addThruPipe(new CsvIterator (fileReader, lineOfFile,3, 2, 1)); // data, label, name fields
		instances.addThruPipe(new ArrayIterator(lines));
		//display instance list
//		println "Now display the read info:";
//		instances.each{ins->
//			def data=ins.getData();
//			println data;
//		}
		return instances;
		
	}
	public static InstanceList readChineseFile(String path){
		//get the file by path
		File inputFile=new File(path);
		if(!inputFile.exists()){
			println "File not existing!";
			return null;
		}
		
		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		//pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+")) );
		File stoplist=new File("C:/zone/fyplog/en.txt");
		if(!stoplist.exists()){
			println "Stop list not existing!";
			return;
		}
		pipeList.add( new TokenSequenceRemoveStopwords(stoplist, "UTF-8", false, false, false) );
		pipeList.add( new TokenSequence2FeatureSequence() );

		InstanceList instances = new InstanceList (new SerialPipes(pipeList));

		//read input file and extract topic
		Reader fileReader = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
		//Pattern lineOfFile=Pattern.compile(/^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$/);
		Pattern lineOfFile=Pattern.compile(/^.*$/);
		//read the lines first
		def lines=inputFile.readLines();
		lines=preprocessText(lines);
		println "Now the data is "+lines;
		//instances.addThruPipe(new CsvIterator (fileReader, lineOfFile,3, 2, 1)); // data, label, name fields
		instances.addThruPipe(new ArrayIterator(lines));
		//display instance list
//		println "Now display the read info:";
//		instances.each{ins->
//			def data=ins.getData();
//			println data;
//		}
		return instances;
	}
	public static ArrayList<String> preprocessText(ArrayList<String> lines){
		for(int i=0;i<lines.size();i++){
			//replace punctuations
			lines[i]=lines[i].replaceAll("[\\p{Punct}]+", "");
			
		}
		return lines;
	}
	public static extractTopics(InstanceList instances,int numTopics){
		// Create a model with assigned topics, alpha_t = 0.01, beta_w = 0.01
		//  Note that the first parameter is passed as the sum over topics, while
		//  the second is the parameter for a single dimension of the Dirichlet prior.
		//ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.1);
		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and combine
		//  statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only,
		//  for real applications, use 1000 to 2000 iterations)
		//model.setNumIterations(50);
		model.setNumIterations(100);
		model.estimate();

		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();
		
		FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
		LabelSequence topics = model.getData().get(0).topicSequence;
		
		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		System.out.println(out);
		
		// Estimate the topic distribution of the first instance,
		//  given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(0);

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		def topicList=[];
		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < numTopics; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
			
			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			//save distribution
			Topic t=new Topic();
			t.index=topic;
			t.weight=topicDistribution[topic];
			
			//get all tokens from this topic
			Map<String,Integer> words=new HashMap<>();
			while (iterator.hasNext()) {
				IDSorter idsorter = iterator.next();
				def content=dataAlphabet.lookupObject(idsorter.getID())
				def weight=idsorter.getWeight();
				words.put(content,weight);
				
			}
			println "Got words under this topic:\n "+words;
			t.words=getTop(words);
			topicList.add(t);
			
			int rank = 0;
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
			}
			System.out.println(out);
		}
		
//		// Create a new instance with high probability of topic 0
//		StringBuilder topicZeroText = new StringBuilder();
//		Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();
//
//		int rank = 0;
//		while (iterator.hasNext() && rank < 5) {
//			IDSorter idCountPair = iterator.next();
//			topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
//			rank++;
//		}
//
//		// Create a new instance named "test instance" with empty target and source fields.
//		InstanceList testing = new InstanceList(instances.getPipe());
//		testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));
//
//		TopicInferencer inferencer = model.getInferencer();
//		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
//		System.out.println("0\t" + testProbabilities[0]);
		
		return topicList;
	}
	public static Map<String,Integer> getTop(Map<String,Integer> rank){
		LinkedHashMap<String,Integer> sorted=rank.sort{ a, b -> b.value <=> a.value }
		int count=0;
		LinkedHashMap<String,Integer> topk=new LinkedHashMap<>();
		int weight;
		for(def e:sorted){
			count++;
			if(count>10){
				if(e.value<weight||e.value==1){
					break;//end the loop when meeting a smaller weight
				}else{
					topk.put(e.key,e.value);
				}
			}
			topk.put(e.key, e.value);
			weight=e.value;
			
			
		}
		println "The top 10 keywords of this topic are:"+topk;
		return topk;
	}
	public static void regexTest(String s){
		Pattern p=Pattern.compile("\\p{L}[\\p{L}\\p{P}]+");
		Matcher m=p.matcher(s);
		while(m.find()){
			println m.group();
		}
	}
	public static class Topic{
		int index;
		double weight;
		Map<String,Integer> words;
	}
	public static void main(String[] args){
		def instances=readChineseFile('C:/zone/fyplog/topics/testOut.txt');//just a file with Chinese in it, utf-8 encoded
		extractTopics(instances,6);
//		regexTest(new String("hello/ / 參加 女兒 畢業 巡遊 典禮 [ 爱 你 ]   [ 位置 ] Kingdon   St ".getBytes(),'utf-8'));
//		regexTest(new String(" 吏部 尚書榮公 （ 榮伯軒 ） … … “ 官場 浮世 繪 ” ".getBytes(),'utf-8'));
	}
}

