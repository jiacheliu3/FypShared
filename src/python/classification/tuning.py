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
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.ensemble import AdaBoostClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import MultinomialNB
from sklearn.metrics import log_loss
from sklearn.grid_search import GridSearchCV
from sklearn.externals import joblib
'''
    Ensemble wrapper
'''
class EnsembleClassifier():
    def __init__(self,X_train,y_train,X_test,y_test):
        self.clfs = {}
        # set data
        self.X_train=X_train
        self.y_train=y_train
        self.X_test=X_test
        self.y_test=y_test
        self.ensembleClf=None # this will be set
        print "Initiated ensemble object with data"
        # load model
        self.model_path="D:/training/temp/model"
        self.load_model(self.model_path)

    # load model from file, if no model file then train a model to initialize
    def load_model(self,path):
        model_path=self.model_path
        if os.path.isfile(model_path):
            print "Model file exists, load it."
            self.ensembleClf=joblib.load(model_path)
            print "Model is loaded."
        else:
            print "No model file exists. Train it."
            self.prepare_model(model_path)
            print "Model prepared."
    # train a model and output to path
    def prepare_model(self,path):
        # train a model
        self.fit()
        # output
        ensemble_model=self.ensembleClf
        if ensemble_model is None:
            print "The ensemble model is null!"
            return
        joblib.dump(ensemble_model,path)
        print "Model saved to ",path

    def set_data(self,X_train, train_label,X_test,test_label):
        print "Storing the training and test data"
        # Set params
        self.X_train=X_train
        self.X_test=X_test
        self.y_train=train_label
        self.y_test=test_label

    def fit(self):
        clf_list=[]
        # # KNN
        # print "KNN"
        # knn = KNeighborsClassifier(n_neighbors=35, weights='distance', leaf_size=2)
        # print "Fitting KNN"
        # knn.fit(self.X_train, self.y_train)
        # print('KNN {score}'.format(score=log_loss(self.y_test, knn.predict_proba(self.X_test))))
        # self.clfs['knn'] = knn
        # clf_list.append(knn)
        # Random forests
        print "Random forest on gini"
        rfc = RandomForestClassifier(n_estimators=43,
                                     criterion='gini',
                                     random_state=4141,
                                     n_jobs=-1,
                                     max_depth=21,
                                     max_features=0.12)
        print "Fitting random forest with gini"
        rfc.fit(self.X_train, self.y_train)
        print('RFC LogLoss {score}'.format(score=log_loss(self.y_test, rfc.predict_proba(self.X_test))))
        self.clfs['rfc']=rfc
        clf_list.append(rfc)
        print "Random forest with entropy"
        rfc2 = RandomForestClassifier(n_estimators=80,
                                      criterion='entropy',
                                      random_state=1337,
                                      n_jobs=-1,
                                      max_depth=36,
                                      max_features=0.06)
        print "Fitting random forest with entropy"
        rfc2.fit(self.X_train, self.y_train)
        print('RFC2 LogLoss {score}'.format(score=log_loss(self.y_test, rfc2.predict_proba(self.X_test))))
        self.clfs['rfc2']=rfc2
        clf_list.append(rfc2)
        # Logistic regression
        print "Logistic regression on logloss"
        logreg = LogisticRegression(C=1.05, penalty='l2')
        print "Fitting logistic regression"
        logreg.fit(self.X_train, self.y_train)
        print('LR LogLoss {score}'.format(score=log_loss(self.y_test, logreg.predict_proba(self.X_test))))
        self.clfs['lr']=logreg
        clf_list.append(logreg)

        # # gradient boosting
        # gbt1=GradientBoostingClassifier(n_estimators=100, learning_rate=1.0,max_depth = 1, random_state = 0)
        # print "Fitting gradient boosting tree"
        # gbt1.fit(self.X_train, self.y_train)
        # print('Gbt1 LogLoss {score}'.format(score=log_loss(self.y_test, gbt1.predict_proba(self.X_test))))
        # self.clfs['gbt1']=gbt1
        # clf_list.append(gbt1)

        # # Bad performance
        # # Multinomial Naive Bayes
        # print "Multinomial naive bayes"
        # mnb = MultinomialNB(fit_prior=False,alpha=0.25)
        # print "Fitting multinomial naive bayes"
        # mnb.fit(self.X_train, self.y_train)
        # print('MNB {score}'.format(score=log_loss(self.y_test, mnb.predict_proba(self.X_test))))
        # self.clfs['mnb'] = mnb
        # clf_list.append(mnb)

        # Adaboost
        print "Adaboost trees"
        abc = AdaBoostClassifier(n_estimators=100,learning_rate=0.5)
        print "Fitting Adaboost trees"
        abc.fit(self.X_train, self.y_train)
        print('ABC {score}'.format(score=log_loss(self.y_test, abc.predict_proba(self.X_test))))
        self.clfs['abc'] = abc
        clf_list.append(abc)


        # Ensemble to models
        eclf3 = VotingClassifier(estimators=[('lr', logreg), ('rf', rfc), ('rf2', rfc2),('abc',abc)], voting='soft',
                                 weights=[2, 2, 2, 1])
        eclf3.estimators_ = clf_list
        print "Dig into the voting classifier"
        innerClfs = eclf3.estimators_
        print "Check estimators"
        print innerClfs
        print('Ensemble LogLoss {score}'.format(score=log_loss(self.y_test, eclf3.predict_proba(self.X_test))))
        self.ensembleClf=eclf3
        print "Ensemble fitting finished"
    def reportParams(self, best_parameters, score):
        print('score:', score)
        for param_name in sorted(best_parameters.keys()):
            print("%s: %r" % (param_name, best_parameters[param_name]))
    def tune_params(self):
        if self.clfs is None or len(self.clfs)==0:
            print "The classifiers are not yet initialized"
            self.prepare_model(self.model_path)

        # # Gradient Boosting Tree
        # print "Gradient boosting tree"
        # params={
        #     'n_estimators':[100,200,300,400],
        #     'max_features':[0.25,0.5,0.75,1.0],
        #     'subsample':[0.25,0.5,0.75],
        #     'max_depth':[5,10,15,20,25]
        # }
        # gbt1 = GradientBoostingClassifier(presort=False)
        # gs = GridSearchCV(gbt1, params, n_jobs=1, cv=5)
        # gs.fit(self.X_train, self.y_train)
        # print("Report best params for gradient boosting tree")
        # best_parameters, score, _ = max(gs.grid_scores_, key=lambda x: x[1])
        # self.reportParams(best_parameters, score)

        # K Nearest Neighbors
        print "KNN"
        params={
            'n_neighbors':[35],
            'weights':['uniform','distance'],
            'leaf_size':[2]
        }
        knn = KNeighborsClassifier()
        gs = GridSearchCV(knn, params, n_jobs=1, cv=5)
        gs.fit(self.X_train, self.y_train)
        print("Report best params for KNN")
        best_parameters, score, _ = max(gs.grid_scores_, key=lambda x: x[1])
        self.reportParams(best_parameters, score)

        # # Adaboost
        # print "Adaboost trees"
        # abc = AdaBoostClassifier()
        # print "Fitting Adaboost trees"
        # params={
        #     'n_estimators' : [100,200,300,400,500],
        #     'learning_rate' : [0.25,0.5,0.75,1.0]
        # }
        # gs = GridSearchCV(abc, params, n_jobs=1, cv=5)
        # gs.fit(self.X_train, self.y_train)
        # print("Report best params for ada boost")
        # best_parameters, score, _ = max(gs.grid_scores_, key=lambda x: x[1])
        # self.reportParams(best_parameters, score)



        # logistic regression
        print "Logistic regression on logloss"
        logreg = self.clfs['lr']
        params = {
            'penalty': ['l2'],
            'C': [1.05]
        }
        gs = GridSearchCV(logreg, params, n_jobs=1, cv=5)
        gs.fit(self.X_train, self.y_train)
        print("Report best params for logistic regression")
        best_parameters, score, _ = max(gs.grid_scores_, key=lambda x: x[1])
        self.reportParams(best_parameters,score)
        # # For random forest with gini
        # print "Random forest on gini"
        # params={
        #     'n_estimators':[43],
        #     'max_features':[0.12],
        #     'max_depth':[21]
        #         }
        # rfc = RandomForestClassifier(criterion='gini',random_state=4141, n_jobs=-1)
        # gs = GridSearchCV(rfc, params, n_jobs=1, cv=5)
        # gs.fit(self.X_train, self.y_train)
        # print("Report best params for random forest gini")
        # best_parameters, score, _ = max(gs.grid_scores_, key=lambda x: x[1])
        # self.reportParams(best_parameters, score)
        # # Another random forest
        # print "Random forest with entropy"
        # params = {
        #     'n_estimators': [80],
        #     'max_features': [0.06],
        #     'max_depth': [36]
        # }
        # rfc2 = RandomForestClassifier(criterion='entropy', random_state=1337, n_jobs=-1)
        # gs = GridSearchCV(rfc2, params, n_jobs=1, cv=5)
        # gs.fit(self.X_train, self.y_train)
        # print("Report best params for random forest entropy")
        # best_parameters, score, _ = max(gs.grid_scores_, key=lambda x: x[1])
        # self.reportParams(best_parameters, score)

        # # Multimonial Naive Bayes
        # print "Multinomial Naive Bayes"
        # mnb=MultinomialNB()
        # params={
        #     'fit_prior':[True,False],
        #     'alpha':[0.25]
        # }
        # gs = GridSearchCV(mnb, params, n_jobs=1, cv=5)
        # gs.fit(self.X_train, self.y_train)
        # print("Report best params for multinomial naive bayes")
        # best_parameters, score, _ = max(gs.grid_scores_, key=lambda x: x[1])
        # self.reportParams(best_parameters, score)

    def make_prediction(self,X_predict):
        print "The matrix to predict: ",X_predict.shape
        prediction=self.ensembleClf.predict_proba(X_predict)
        print "Predictions made: ",prediction
        print "Prediction shape: ",len(prediction)
        return prediction



