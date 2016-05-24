# -*- coding: utf-8 -*-
import os
from sklearn import datasets
from scipy.sparse import hstack
from scipy.sparse import vstack
from scipy.sparse import csr_matrix
from sklearn.cross_validation import train_test_split
import numpy as np
import operator
from codecs import open
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import VotingClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import GaussianNB
from sklearn.metrics import log_loss
from tuning import EnsembleClassifier

'''
Helper functions
'''

def convertToInt(array):
    intArr = []
    for i in range(len(array)):
        a = array[i][0]
        intArr.append(int(a) - 1)
    return intArr


def readFeatures(path):
    features = []
    with open(path, 'r+') as f:
        lines = f.readlines()
        for i in range(len(lines)):
            line = lines[i]
            parts = line.split(',')
            if len(parts) == 0:
                print "This line cannot be splitted"
            else:
                features.append(parts[1])
    f.close()
    print "Append the last"
    features.append('Default')
    print "Read ", len(features), " features"
    return features


def outputImportance(arr):  # sample format [('name',imp_value),...]
    path = '/home/bitnami/PycharmProjects/nlp/featureImp.txt'
    with open(path, 'w', encoding='utf-8') as f:
        for i in range(len(arr)):
            a = arr[i]
            f.write(a[0] + "," + str(a[1]) + "\n")
    f.close()
    print "Finished output ", len(arr), " features."


def groupLabels(array):
    map = {}
    for a in array:
        if a in map:
            map[a] += 1
        else:
            map[a] = 1
    return map
'''
wrapper class
'''
class Trainer:
    def __init__(self,input_path,output_path):
        print "Initiate the trainer"
        self.base_dir='D:/training/samples/'
        # set path
        self.input_path=input_path
        self.output_path=output_path
        # initialize fields
        self.features=None
        self.X_train=None
        self.y_train=None
        self.X_test=None
        self.y_test=None
        self.classifier=None
        self.feature_num=73935
        # load data
        self.get_data()
        self.classifier=EnsembleClassifier(self.X_train,self.y_train,self.X_test,self.y_test)
    def get_data(self):
        files = os.listdir(self.base_dir)
        print "The files to load are ", files
        # read the csv
        allMatrix = csr_matrix((1, 73935), dtype=np.float32)
        allY = []
        # allMatrix=np.empty([1,73939])
        for i in range(len(files)):
            fileName = files[i]
            print "processing the file named ", fileName
            X, y = datasets.load_svmlight_file(self.base_dir + fileName, n_features=73935, multilabel=True)
            print "X loaded:", X.shape, " y loaded:", len(y)
            allMatrix = vstack((allMatrix, X))
            allY = allY + y
        allMatrix = allMatrix[1:, :]
        print "final shape is ", allMatrix.shape
        print "labels are", allY[0:100]
        # split the data
        X_train, X_test, y_train, y_test = train_test_split(allMatrix, allY, test_size=0.33, random_state=42)
        # convert labels to int
        train_label = convertToInt(y_train)
        test_label = convertToInt(y_test)
        # check frequency of labels
        print "Train set has distribution ", groupLabels(train_label)
        print "Test set has distribution ", groupLabels(test_label)
        # read features
        features = readFeatures('D:/training/featureNames')
        print "Features are ", features[0:10]
        # store the features
        self.X_train=X_train
        self.y_train=train_label
        self.X_test=X_test
        self.y_test=test_label
        self.features=features
        print "The data are stored in the trainer."
    def train(self):
        print "Train the classifier"
        self.classifier.fit()
        print "Classifier trained"
    def set_feature_num(self,number):
        self.feature_num=number

    def make_prediction(self):
        print "Make prediction on path: ",self.input_path
        X_predict,y_predict=datasets.load_svmlight_file(self.input_path, n_features=73935, multilabel=True)
        pred=self.classifier.make_prediction(X_predict)
        print "Prediction complete: ",pred
        print "Output to ",self.output_path
        self.output_csv(pred)
        print "Output complete"
    def output_csv(self,matrix):
        print "Output ",len(matrix)," predictions to file."
        import csv
        with open(self.output_path,'wb') as csvfile:
            wr=csv.writer(csvfile)
            wr.writerows(matrix)

    def tune_params(self):
        self.classifier.tune_params()

'''
The flow starts here
'''
import sys
#sys.stdout=open('D:/training/temp/python.log','w')
print "The python version is ",sys.version
# read the input
in_path=sys.argv[1]
out_path=sys.argv[2]
print "Input path: ",in_path
print "Output path: ",out_path
# create controller
t=Trainer(in_path,out_path)
t.make_prediction()