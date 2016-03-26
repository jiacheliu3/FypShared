#coding=utf-8
import sys
from idlelib.IOBinding import encoding
import codecs
import jieba

inFile=sys.argv[1]
outFile=sys.argv[2]
print("Target output file is ",outFile)

i=open(inFile,"r",encoding="utf-8")
content=i.read()
#print("Got content from input ",content.encode("utf-8"));

seg_list = jieba.cut(content)


# o=codecs.open(outFile,"w",'utf-8')
o=open(outFile,"w",encoding="utf-8")
theString=str(" ".join(seg_list))
#print(theString)

o.write("hello")
o.write(theString)
sys.stdout.flush()
    
i.close()
o.close()