#coding=utf-8
import sys
from idlelib.IOBinding import encoding
from codecs import open
import jieba

inFile=sys.argv[1]
outFile=sys.argv[2]
print("Start working on file ",inFile)
seg_list=[]
# input and keyword extraction
with open(inFile,"r",encoding="utf-8") as file:
    lines=file.readlines()
    for i in range(len(lines)):
        line=lines[i]
        if line==None or line=='':
            print("Line is empty")
        else:
            words = jieba.cut(line)
            #print "Got ",len(keywords)," keywords"
            seg_list.append(words)
file.close()
# output
print("Completed segmentation on ",len(seg_list)," items")
with open(outFile,"w",encoding="utf-8") as output:
    for i in range(len(seg_list)):
        theWords=seg_list[i]
        #print "The words are: ",theWords
        result=','.join(theWords)
        if i==0:
            output.write(result)
        else:
            output.write(result)
    print("Output to file")
output.close()
print("Written to file ",outFile)