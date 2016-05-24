# -*- coding: utf-8 -*-
import os
import xgboost as xgb
from sklearn import datasets
from scipy.sparse import hstack
from scipy.sparse import vstack
from scipy.sparse import csr_matrix
from sklearn.cross_validation import train_test_split
import numpy as np
import operator
from codecs import open
from xgbWrapper import XGBoostClassifier

'''
Helper functions
'''
def convertToInt(array):
    intArr=[]
    for i in range(len(array)):
        a=array[i][0]
        intArr.append(int(a)-1)
    return intArr
def readFeatures(path):
    features=[]
    with open(path,'r+') as f:
        lines=f.readlines()
        for i in range(len(lines)):
            line=lines[i]
            parts=line.split(',')
            if len(parts)==0:
                print "This line cannot be splitted"
            else:
                features.append(parts[1])
    f.close()
    print "Append the last"
    features.append('Default')
    print "Read ",len(features)," features"
    return features
def outputImportance(arr):# sample format [('name',imp_value),...]
    path='/home/bitnami/PycharmProjects/nlp/featureImp.txt'
    with open(path,'w',encoding='utf-8') as f:
        for i in range(len(arr)):
            a=arr[i]
            f.write(a[0]+","+str(a[1])+"\n")
    f.close()
    print "Finished output ",len(arr)," features."
'''
The flow starts here
'''
baseDir='/home/bitnami/PycharmProjects/nlp/classes/'
files=os.listdir(baseDir)
print files

#theFile='/home/bitnami/PycharmProjects/nlp/house'
#matrix=xgb.DMatrix(theFile)
#print matrix.num_col(),"*",matrix.num_row()

# read the csv
allMatrix=csr_matrix((1,73935),dtype=np.float32)
allY=[]
#allMatrix=np.empty([1,73939])
for i in range(len(files)):
    fileName=files[i]
    print "processing the file named ",fileName
    #print type(fileName)
    X,y=datasets.load_svmlight_file(baseDir+fileName,n_features=73935,multilabel=True)
    print "X loaded:",X.shape," y loaded:",len(y)
    allMatrix=vstack((allMatrix,X))
    allY=allY+y
    #allMatrix=np.vstack((allMatrix,matrix))
allMatrix=allMatrix[1:,:]
print "final shape is ",allMatrix.shape
print "labels are",allY

# split the data
X_train, X_test, y_train, y_test = train_test_split(allMatrix, allY, test_size=0.33, random_state=42)

# convert labels to int
train_label=convertToInt(y_train)
test_label=convertToInt(y_test)

# read features
features=readFeatures('/home/bitnami/PycharmProjects/nlp/featureName.txt')
print "Features are ",features[0:10]

# convert to desired dmatrix
dtrain=xgb.DMatrix(X_train,label=train_label,feature_names=features)
dtest=xgb.DMatrix(X_test,label=test_label,feature_names=features)
print "The train set is ",dtrain.num_row(),"*",dtrain.num_col()," and labels ",len(train_label)
print "The test set is ",dtest.num_row(),"*",dtest.num_col()," and labels ",len(test_label)

param = {'bst:max_depth':5, 'bst:eta':0.4, 'silent':1, 'objective': 'multi:softprob','num_class':32 }
param['nthread'] = 4
param['eval_metric'] = 'mlogloss'
evallist  = [(dtest,'eval'), (dtrain,'train')]
num_round=200
bst = xgb.train( param, dtrain, num_round, evallist )

# see feature importance
imp=bst.get_fscore()
print "The importance is ",imp
sorted_x = sorted(imp.items(), key=operator.itemgetter(1),reverse=True)
print "Sorted: ",sorted_x
outputImportance(sorted_x)



