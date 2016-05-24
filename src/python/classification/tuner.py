import sys
from sk_classify import Trainer

# read the input
in_path='D:\training\temp\3forPython.txt'
out_path='D:\training\temp\temp\forJava.txt'
print "Input path: ",in_path
print "Output path: ",out_path
# create controller
t=Trainer(in_path,out_path)
print "Start tuning"
t.tune_params()