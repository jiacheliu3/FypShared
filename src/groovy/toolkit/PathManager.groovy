package toolkit

class PathManager {
	/*
	 * Python path for python scripts
	 * version: 2.7
	 * required libs: jieba, scipy, numpy, sklearn etc
	 * 
	 * */
	public static pythonBasePath="C:\\Users\\jiacheliu3\\AppData\\Local\\Enthought\\Canopy\\User";
	
	//model for ensemble
	public static ensemblePythonHome="C:/Users/jiacheliu3/pycharm/";
	public static ensembleTempFileBase="D:/training/temp/";
	public static ensembleLabelFile="D:/training/labels.txt";
	public static ensemblePythonExe="C:/Users/jiacheliu3/AppData/Local/Enthought/Canopy/User/";
	
	//path for the crawler to store webpages 
	public static websiteCrawlerBase="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\";

	//stop words for keyword extraction
	public static stopWordBasePath="D:\\FypGitRepo\\models\\stopwords";
	
	//features for ensemble
	public static ensembleFeaturesFile="D:/training/features/final";
	public static stopWordsBase="D:/training/stopwords";
	public static rawFeaturesBase="D:/training/features";
	public static csvOutputBase="D:/training/samples/";
	
	//python scripts for keyword extraction and content segmentation
	public static sepManagerTempFolder="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
	public static keywordScriptPath="C:\\Users\\jiacheliu3\\workspace\\CodeBigBroRelated";

	//temp folder for temp files, to be used in communication with python scripts
	public static segFileTempBase="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\temp";
	public static keywordFileTempBase="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\temp";

	//for feature extraction
	public static featureTempBase="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro";
	
	//for network generation
	public static networkTempBase="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
	
	//for clustering
	public static clusterBasePath="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
	public static clusterStorageFile="D:\\clusters.txt";
	
	//for final report
	public static websiteBasePath="D:\\FypGitRepo\\data\\websites";
	public static keywordBasePath="D:\\FypGitRepo\\data\\keywords";
	
	//for reading files in the web app resources folder
	public static appResourceFolder="C:/Users/jiacheliu3/workspace/CodeBigBroSub/web-app/resources/";
	
	//job log temp folder
	public static jobLogTempFolder="D:/fyplog/temp/";
	
	//session log temp folder
	public static sessionLogTempFolder="D:/fyplog/";
	
	//LDA temp folder
	public static ldaTempPath="C:/zone/fyplog";
	
	//classifier types file
	public static classifierTypeFile="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\type\\types.txt";
	
	
	
}
