#coding=utf-8
'''
Created on 2015��10��3��

@author: Tassadar
'''



import urllib
import json
import time
import urllib.request
from urllib.parse   import quote
from socket import *
from urllib.error import URLError

#global params
inputPath="D:\\weibo\\UserWeibos201502"
outputPath="D:\\weibo\\UserWeibos201502_en"
s=""
tempHolder=""
#check whether to run
counter=1;
outputFile=open(outputPath, "a",encoding="utf-8")

import re
uselessStart = re.compile("^[0-9]+\t?\|\|\t?\|\|\t?-?[0-9]+\t[\u4e00-\u9fa5a-zA-Z\-_0-9]+\t")
atUser=re.compile('@[\u4e00-\u9fa5a-zA-Z\-_0-9]+')
blanks=re.compile('(\\N\t)*')

#def readNextItem(input):
    
def wait():
    print("Reached threshold, wait 1h")
    time.sleep(3600)
    
def translate(string):
    #get string from file
    orgStr=string
    #pre-processing before translation
    #strip out the names and tagging
    s=uselessStart.sub("", orgStr)
    s=atUser.sub("",s)
    s=blanks.sub("",s)
    print("Translation src: "+s)
    URL = 'http://openapi.baidu.com/public/2.0/bmt/translate?from=%s&to=%s&q=%s&client_id=%s'
    API_Key = 'AQdkTPm4GH7dQz6fZaGFdmpU' #换成自己的APIKey
    url = URL % ("zh", "en", quote(s), API_Key)
    
    header={}
    header['from']="zh"
    header['to']="en"
    header['client_id']=u'AQdkTPm4GH7dQz6fZaGFdmpU'
    header['q']=quote(s);
    
    #request = urllib.request.Request(url)
    #print(request.read())
    print(url)
    try:
        response = urllib.request.urlopen(url,timeout=20).read().decode('utf-8')
    except (timeout,URLError) as e:
        print("Timeout experienced, retrying")
        time.sleep(2)
        translate(string)
        return
        
    print ("Got response "+response)
    rs = json.loads(response)
    # check error
    if 'error_code' in rs:
        err_code=rs['error_code']
        print("Error detected!")
        err_msg=rs["error_msg"]
        print(str(err_code)+": "+err_msg)
        print("Stuck at "+str(counter)+"th translation")
        # retry after 1 h
        time.sleep(60)
        translate(string)
        return
    # if no error
    global outputFile
    print("Translation complete, output to file")
    print(orgStr,file=outputFile)
    inEng=rs['trans_result']
    
    for i in inEng:
        
        print(i['dst'].encode(encoding='utf_8', errors='ignore'))
        print(i['dst'].encode(encoding='utf_8', errors='ignore'),file=outputFile)
    print("End of this translation")
    #print the result to the 
    


#entrance
with open(inputPath,"r",encoding="utf-8") as input:
    for line in input:
        if counter<950:
            print(str(counter)+"th translation of the hour")
            set=[line[i:i+999] for i in range(0, len(line), 999)]
            for next in set:
                translate(next)
                time.sleep(10)
                counter+=1
        else:
            wait()
            counter=0;
outputFile.close()
