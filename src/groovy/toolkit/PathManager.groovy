package toolkit

class PathManager {

    static String pwd=System.getProperty("user.dir");

    //path for the crawler to store webpages
    public static websiteCrawlerBase = pwd + "\\data\\";

    //python scripts for keyword extraction and content segmentation
/// /	public static sepManagerTempFolder="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
//	public static keywordScriptPath="C:\\Users\\jiacheliu3\\workspace\\CodeBigBroRelated";
    public static sepManagerTempFolder = pwd;
//    public static keywordScriptPath = "C:\\Users\\jiacheliu3\\workspace\\CodeBigBroRelated";
    public static keywordScriptPath = pwd+ "/src/python/keywords/";

    //temp folder for temp files, to be used in communication with python scripts
//    public static segFileTempBase = "C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\temp";
//    public static keywordFileTempBase = "C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\temp";
    public static segFileTempBase = pwd+"/temp/";
    public static keywordFileTempBase = pwd+"/temp/";

    /*
     * Python path for python scripts
     * version: 2.7
     * required libs: jieba, scipy, numpy, sklearn etc
     *
     * */
//    public static pythonBasePath = "C:\\Users\\jiacheliu3\\AppData\\Local\\Enthought\\Canopy\\User";
    public static pythonBasePath = "";

    //model for ensemble
    public static ensemblePythonHome = "C:/Users/jiacheliu3/pycharm/";
    public static ensemblePythonExe = "C:/Users/jiacheliu3/AppData/Local/Enthought/Canopy/User/";
    //    public static ensembleTempFileBase = "D:/training/temp/";
    public static ensembleTempFileBase = pwd + "/temp/";
//    public static ensembleLabelFile = "D:/training/labels.txt";
    public static ensembleLabelFile = pwd+"/training/labels.txt";

    //stop words for keyword extraction
//    public static stopWordBasePath = "D:\\FypGitRepo\\models\\stopwords";
    public static stopWordBasePath = pwd+"/models/stopwords";

    /* Added in 1.3beta*/
    public static patternBasePath=pwd+"/temp/"

    //features for ensemble
//	public static ensembleFeaturesFile="D:/training/features/final";
//	public static stopWordsBase="D:/training/stopwords";
//	public static rawFeaturesBase="D:/training/features";
//	public static csvOutputBase="D:/training/samples/";
    public static ensembleFeaturesFile = pwd + "/training/features/final";
    public static stopWordsBase = pwd + "/training/stopwords";
    public static rawFeaturesBase = pwd + "/training/features";
    public static csvOutputBase = pwd + "/training/samples/";

    //for feature extraction
//	public static featureTempBase="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro";
    public static featureTempBase = pwd;

    //for network generation
//	public static networkTempBase="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
    public static networkTempBase = pwd;

    //for clustering
//	public static clusterBasePath="C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\";
    public static clusterBasePath = pwd;
//    public static clusterStorageFile = "D:\\clusters.txt";
    public static clusterStorageFile = pwd + "/temp/clusters.txt";

    //for final report
//    public static websiteBasePath = "D:\\FypGitRepo\\data\\websites";
//    public static keywordBasePath = "D:\\FypGitRepo\\data\\keywords";
    public static websiteBasePath = pwd + "/data/websites";
    public static keywordBasePath = pwd + "/data/keywords";

    //for reading files in the web app resources folder
//    public static appResourceFolder = "C:/Users/jiacheliu3/workspace/CodeBigBroSub/web-app/resources/";
    public static appResourceFolder = pwd + "/web-app/resources/";

    //job log temp folder
//    public static jobLogTempFolder = "D:/fyplog/temp/";
    public static jobLogTempFolder = pwd + "/temp/";

    //session log temp folder
//    public static sessionLogTempFolder = "D:/fyplog/";
    public static sessionLogTempFolder = pwd + "/log/";

    //LDA temp folder
//    public static ldaTempPath = "C:/zone/fyplog";
    public static ldaTempPath = pwd + "/temp/";

    //classifier types file
//    public static classifierTypeFile = "C:\\Users\\jiacheliu3\\git\\projects\\CodeBigBro\\data\\type\\types.txt";
    public static classifierTypeFile = pwd + "/data/type/types.txt";


}
