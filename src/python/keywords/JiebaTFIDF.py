#coding=utf-8
from jieba.analyse import extract_tags
import sys, os
from codecs import open

inFile=sys.argv[1]
outFile=sys.argv[2]

# inFile=os.path.join(os.getcwd(), inFile)
# outFile=os.path.join(os.getcwd(), outFile)

i=open(inFile,"r",encoding="utf-8")
print("Start working on file ",inFile)
keyword_list=[]
# input and keyword extraction
with open(inFile,"r",encoding="utf-8") as file:
    lines=file.readlines()
    for i in range(len(lines)):
        line=lines[i]
        if line==None or line=='':
            print("Line is empty")
        else:
            # calculate number of keywords
            length = len(line)
            from math import sqrt, floor
            root = int(floor(sqrt(length)))
            if root >= 500:
                print("Text is too long. Keep only 500 keywords.")
                root = 500
            #print("Expecting number of keywords ", root)
            keywords = extract_tags(line, topK=root, withWeight=False, allowPOS=())
            #print "Got ",len(keywords)," keywords"
            keyword_list.append(keywords)
file.close()
# output
print("Completed keyword extraction on ",len(keyword_list)," items")
with open(outFile,"w",encoding="utf-8") as output:
    for i in range(len(keyword_list)):
        theWords=keyword_list[i]
        #print "The words are: ",theWords
        result=','.join(theWords)
        if i==0:
            output.write(result)
        else:
            output.write('\n'+result)
    print("Output to file")
output.close()
print("Written to file ",outFile)
