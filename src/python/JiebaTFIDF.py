#coding=utf-8
from jieba.analyse import extract_tags
import sys
from idlelib.IOBinding import encoding
import codecs

inFile=sys.argv[1]
outFile=sys.argv[2]

i=open(inFile,"r",encoding="utf-8")
content=i.read()

# calculate number of keywords
length=len(content)
from math import sqrt,floor
root=floor(sqrt(length))
if(root>=500):
    print("Text is too long. Keep only 500 keywords.")
    root=500

print("Expecting number of keywords ",root)

keywords=extract_tags(content, topK=root, withWeight=False, allowPOS=())
o=codecs.open(outFile,"w",'utf-8')
o.write("\n".join(keywords))
i.close()
o.close()
