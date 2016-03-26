#coding=utf-8
from jieba.analyse import textrank
import sys
from idlelib.IOBinding import encoding
import codecs

inFile=sys.argv[1]
outFile=sys.argv[2]

i=open(inFile,"r",encoding="utf-8")
content=i.read()

keywords=textrank(content, withWeight=True)
o=codecs.open(outFile,"w",'utf-8')
for x,w in keywords:
    o.write(x+"\n")
    
i.close()
o.close()
