package main.java.ml.extractor;

import weka.core.Instances;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

import java.util.ArrayList;
import java.util.List;

import main.java.ml.model.Acume;
import main.java.ml.model.ClassifierData;

public class WekaExtractor {
	private static String randomFor = "RandomForest";
	private static String naiveBay = "NaiveBayes";
	private static String ibk = "IBk";
	private static String underSampl = "UnderSampling";
	private static String smoteStr = "SMOTE";

	private WekaExtractor() {
		// EMPTY
	}
	
	public static List<List<ClassifierData>> computeWeka(int iterations, String projName) throws Exception {
		int i = 0;
		List<List<ClassifierData>> allClassifier = new ArrayList<>();
		List<ClassifierData> basicRandomForestLis = new ArrayList<>();
		List<ClassifierData> basicNaiveBayesLis = new ArrayList<>();
		List<ClassifierData> basicIBkLis = new ArrayList<>();

		List<ClassifierData> featureRandomForestLis = new ArrayList<>();
		List<ClassifierData> featureNaiveBayesLis = new ArrayList<>();
		List<ClassifierData> featureIBkLis = new ArrayList<>();

		List<ClassifierData> featSMOTERandomForestLis = new ArrayList<>();
		List<ClassifierData> featSMOTENaiveBayesLis = new ArrayList<>();
		List<ClassifierData> featSMOTEIBkLis = new ArrayList<>();
		
		List<ClassifierData> featOverRandomForestLis = new ArrayList<>();
		List<ClassifierData> featOverNaiveBayesLis = new ArrayList<>();
		List<ClassifierData> featOverIBkLis = new ArrayList<>();

		List<ClassifierData> featCostRandomForestLis = new ArrayList<>();
		List<ClassifierData> featCostNaiveBayesLis = new ArrayList<>();
		List<ClassifierData> featCostIBkLis = new ArrayList<>();

		for (i = 1; i < iterations; i++) {

			// Versioni basic
			DataSource sourceTr = new DataSource("csvAndArffFiles/" + projName + "_" + i + "_Training.arff"); // FILE DAL TRAINING
			Instances basicTrain = sourceTr.getDataSet();

			DataSource sourceTes = new DataSource("csvAndArffFiles/" + projName + "_" + i + "_Testing.arff"); // FILE DAL TESTING
			Instances basicTes = sourceTes.getDataSet();
			
			int numAttrBasic = basicTrain.numAttributes();
			basicTrain.setClassIndex(numAttrBasic - 1);
			basicTes.setClassIndex(numAttrBasic - 1);

			// evaluation with no filtered
			Evaluation evalClass = new Evaluation(basicTes);

			// Basic Random Forest
			ClassifierData basicRandomFor = new ClassifierData(projName, i, randomFor, false, "No", false);
			evalRandomFor(basicRandomFor,basicTrain,basicTes,evalClass,projName,i);
			basicRandomForestLis.add(basicRandomFor);

			// Basic Naive Bayes
			ClassifierData basicNaiveBa = new ClassifierData(projName, i, naiveBay, false, "No", false);
			evalClass = new Evaluation(basicTes);
			evalNaiveBay(basicNaiveBa,basicTrain,basicTes,evalClass,projName,i);
			basicNaiveBayesLis.add(basicNaiveBa);

			// Basic IBk
			ClassifierData basicIbk = new ClassifierData(projName, i, ibk, false, "No", false);
			evalClass = new Evaluation(basicTes);
			evalIBk(basicIbk,basicTrain,basicTes,evalClass,projName,i);
			basicIBkLis.add(basicIbk);

			
			//FEATURE SELECTION
			AttributeSelection filter = new AttributeSelection();
			CfsSubsetEval cfsEval = new CfsSubsetEval();
			BestFirst bestFirst = new BestFirst();

			//bestFirst.setOptions(Utils.splitOptions("-D 0")); //0 backward, 2 bidirectional, 1 forward
			filter.setEvaluator(cfsEval);
			filter.setSearch(bestFirst);
			filter.setInputFormat(basicTrain);

			Instances featureTrain = Filter.useFilter(basicTrain, filter);
			int numAttrFeature = featureTrain.numAttributes();
			featureTrain.setClassIndex(numAttrFeature - 1);
			
			Instances featureTest = Filter.useFilter(basicTes, filter);
			
			ClassifierData featureRandomFor = new ClassifierData(projName, i, randomFor, true, "No", false);
			evalClass = new Evaluation(basicTes);
			evalRandomFor(featureRandomFor, featureTrain, featureTest, evalClass,projName,i);
			featureRandomForestLis.add(featureRandomFor);

			ClassifierData featureNaiveBay = new ClassifierData(projName, i, naiveBay, true, "No", false);
			evalClass = new Evaluation(basicTes);
			evalNaiveBay(featureNaiveBay, featureTrain, featureTest, evalClass,projName,i);
			featureNaiveBayesLis.add(featureNaiveBay);

			ClassifierData featureIBk = new ClassifierData(projName, i, ibk, true, "No", false);
			evalClass = new Evaluation(basicTes);
			evalIBk(featureIBk, featureTrain, featureTest, evalClass,projName,i);
			featureIBkLis.add(featureIBk);

	        
			//FEATURE SELECTION E UNDERSAMPLING
	        SpreadSubsample sampl = new SpreadSubsample ();
	        sampl.setOptions(new String[] {"-M", "1.0"}); 
	        sampl.setInputFormat(featureTrain);
	        
			ClassifierData featSamplRandomFor2 = new ClassifierData(projName, i, randomFor, true, underSampl, false);
			evalClass = new Evaluation(basicTes);
			evalFeatureUnderSamp(featSamplRandomFor2,featureTrain,featureTest, evalClass,projName,i,sampl);
			featOverRandomForestLis.add(featSamplRandomFor2);

			ClassifierData featSamplNaive2 = new ClassifierData(projName, i, naiveBay, true, underSampl, false);
			evalClass = new Evaluation(basicTes);
			evalFeatureUnderSamp(featSamplNaive2, featureTrain, featureTest, evalClass,projName,i,sampl);
			featOverNaiveBayesLis.add(featSamplNaive2);
			
			ClassifierData featSamplIBk2 = new ClassifierData(projName, i, ibk, true, underSampl, false);
			evalClass = new Evaluation(basicTes);
			evalFeatureUnderSamp(featSamplIBk2, featureTrain, featureTest, evalClass,projName,i,sampl);
			featOverIBkLis.add(featSamplIBk2);
	        
	        
	        
			// FEATURE SELECTION E SAMPLING SMOTE OK
			SMOTE smote = new SMOTE();
			smote.setInputFormat(basicTrain);
			Instances trainSMOTE = Filter.useFilter(basicTrain,smote);
			
			filter.setInputFormat(trainSMOTE);
			
			Instances trainSmoteFeat = Filter.useFilter(trainSMOTE,filter);
			Instances testSmoteFeat = Filter.useFilter(basicTes,filter);

			ClassifierData featSamplRandomFor = new ClassifierData(projName, i, randomFor, true, smoteStr, false);
			evalClass = new Evaluation(basicTes);
			evalRandomFor(featSamplRandomFor, trainSmoteFeat, testSmoteFeat, evalClass,projName,i);
			featSMOTERandomForestLis.add(featSamplRandomFor);

			ClassifierData featSamplNaive = new ClassifierData(projName, i, naiveBay, true, smoteStr, false);
			evalClass = new Evaluation(basicTes);
			evalNaiveBay(featSamplNaive, trainSmoteFeat, testSmoteFeat, evalClass,projName,i);
			featSMOTENaiveBayesLis.add(featSamplNaive);

			ClassifierData featSamplIBk = new ClassifierData(projName, i, ibk, true, smoteStr, false);
			evalClass = new Evaluation(basicTes);
			evalIBk(featSamplIBk, trainSmoteFeat, testSmoteFeat, evalClass,projName,i);
			featSMOTEIBkLis.add(featSamplIBk);	
			
			// FEATURE SELECTION E COST SENSITIVE LEARNING 
			CostMatrix costMatr = new CostMatrix(2);
			costMatr.setCell(0, 0, 0.0);
			costMatr.setCell(1, 0, 1.0);
			costMatr.setCell(0, 1, 10.0);
			costMatr.setCell(1, 1, 0.0);
			CostSensitiveClassifier costSensClass = new CostSensitiveClassifier();

			costSensClass.setCostMatrix(costMatr);

			ClassifierData featCostRandomFor = new ClassifierData(projName, i, randomFor, true, "No", true);
			evalClass = new Evaluation(basicTes);
			List<Instances> listIstan = new ArrayList<>();
			listIstan.add(featureTrain);
			listIstan.add(featureTest);
			evalCostSensitive(featCostRandomFor, listIstan, evalClass, costSensClass,randomFor,projName,i);
			featCostRandomForestLis.add(featCostRandomFor);

			costSensClass.setCostMatrix(costMatr);
			ClassifierData featCostNaive = new ClassifierData(projName, i, naiveBay, true, "No", true);
			evalClass = new Evaluation(basicTes);
			evalCostSensitive(featCostNaive, listIstan, evalClass, costSensClass,naiveBay,projName,i);
			featCostNaiveBayesLis.add(featCostNaive);

			costSensClass.setCostMatrix(costMatr);
			ClassifierData featCostIBk = new ClassifierData(projName, i, ibk, true, "No", true);
			evalClass = new Evaluation(basicTes);
			evalCostSensitive(featCostIBk, listIstan, evalClass, costSensClass,ibk,projName,i);
			featCostIBkLis.add(featCostIBk);

		}

		allClassifier.add(basicRandomForestLis);
		allClassifier.add(basicNaiveBayesLis);
		allClassifier.add(basicIBkLis);

		allClassifier.add(featureRandomForestLis);
		allClassifier.add(featureNaiveBayesLis);
		allClassifier.add(featureIBkLis);

		allClassifier.add(featSMOTERandomForestLis);
		allClassifier.add(featSMOTENaiveBayesLis);
		allClassifier.add(featSMOTEIBkLis);
		
		allClassifier.add(featOverRandomForestLis);
		allClassifier.add(featOverNaiveBayesLis);
		allClassifier.add(featOverIBkLis);
		
		allClassifier.add(featCostRandomForestLis);
		allClassifier.add(featCostNaiveBayesLis);
		allClassifier.add(featCostIBkLis);

		return allClassifier;
	}
	
	
	private static void evalRandomFor(ClassifierData classifier, Instances train, Instances test, Evaluation eval, String projName, int iter) throws Exception {
		RandomForest rf = new RandomForest();
		rf.buildClassifier(train);
		
		eval.evaluateModel(rf,test);
		
		classifier.setPrecision(eval.precision(0));
		classifier.setRecall(eval.recall(0));
		classifier.setAuc(eval.areaUnderROC(0));
		classifier.setKappa(eval.kappa());
		classifier.setTp(eval.numTruePositives(0));
		classifier.setFp(eval.numFalsePositives(0));
		classifier.setTn(eval.numTrueNegatives(0));
		classifier.setFn(eval.numFalseNegatives(0));
		classifier.setTrainingPerc(
				100.0 * train.numInstances() / (train.numInstances() + test.numInstances()));
		computePredicted(projName, test,rf,iter,classifier);
	}
	
	private static void evalNaiveBay(ClassifierData classifier, Instances train, Instances test, Evaluation eval, String projName, int iter) throws Exception {
		NaiveBayes nb = new NaiveBayes();
		nb.buildClassifier(train);
		
		eval.evaluateModel(nb,test);
		classifier.setPrecision(eval.precision(0));
		classifier.setRecall(eval.recall(0));
		classifier.setAuc(eval.areaUnderROC(0));
		classifier.setKappa(eval.kappa());
		classifier.setTp(eval.numTruePositives(0));
		classifier.setFp(eval.numFalsePositives(0));
		classifier.setTn(eval.numTrueNegatives(0));
		classifier.setFn(eval.numFalseNegatives(0));
		classifier.setTrainingPerc(
				100.0 * train.numInstances() / (train.numInstances() + test.numInstances()));
		computePredicted(projName, test,nb,iter,classifier);
	}
	
	private static void evalIBk(ClassifierData classifier, Instances train, Instances test, Evaluation eval, String projName, int iter) throws Exception {
		IBk ibk = new IBk();
		ibk.buildClassifier(train);
		
		eval.evaluateModel(ibk,test);
		classifier.setPrecision(eval.precision(0));
		classifier.setRecall(eval.recall(0));
		classifier.setAuc(eval.areaUnderROC(0));
		classifier.setKappa(eval.kappa());
		classifier.setTp(eval.numTruePositives(0));
		classifier.setFp(eval.numFalsePositives(0));
		classifier.setTn(eval.numTrueNegatives(0));
		classifier.setFn(eval.numFalseNegatives(0));
		classifier.setTrainingPerc(
				100.0 * train.numInstances() / (train.numInstances() + test.numInstances()));
		computePredicted(projName, test,ibk,iter,classifier);
	}
	
	private static void evalFeatureUnderSamp(ClassifierData classifier, Instances train, Instances test, Evaluation eval, String projName, int iter, SpreadSubsample sampl) throws Exception {
		FilteredClassifier filtered = new FilteredClassifier();
		filtered.setFilter(sampl);
		
		if(classifier.getClassifierName().equals(randomFor)) {
			filtered.setClassifier(new RandomForest());
		}else if (classifier.getClassifierName().equals(naiveBay)) {
			filtered.setClassifier(new NaiveBayes());
		}else {
			filtered.setClassifier(new IBk());
		}
		
		filtered.buildClassifier(train);
		eval.evaluateModel(filtered,test);
		
		classifier.setPrecision(eval.precision(0));
		classifier.setRecall(eval.recall(0));
		classifier.setAuc(eval.areaUnderROC(0));
		classifier.setKappa(eval.kappa());
		classifier.setTp(eval.numTruePositives(0));
		classifier.setFp(eval.numFalsePositives(0));
		classifier.setTn(eval.numTrueNegatives(0));
		classifier.setFn(eval.numFalseNegatives(0));
		classifier.setTrainingPerc(
				100.0 * train.numInstances() / (train.numInstances() + test.numInstances()));
		computePredicted(projName, test,filtered,iter,classifier);
		
	}
	
	private static void evalCostSensitive(ClassifierData classifier, List<Instances> istList, Evaluation eval,CostSensitiveClassifier costSensClass,String type, String projName, int iter) throws Exception {
		Instances train = istList.get(0);
		Instances test = istList.get(1);
		RandomForest rf = new RandomForest();
		NaiveBayes nb = new NaiveBayes();
		IBk ibk = new IBk();
		if (type.equals(randomFor)) {
			rf.buildClassifier(train);
			costSensClass.setClassifier(rf);
		}else if (type.equals(naiveBay)) {
			nb.buildClassifier(train);
			costSensClass.setClassifier(nb);
		}else {
			ibk.buildClassifier(train);
			costSensClass.setClassifier(ibk);
		}
		costSensClass.buildClassifier(train);
		eval.evaluateModel(costSensClass,test);
		
		classifier.setPrecision(eval.precision(0));
		classifier.setRecall(eval.recall(0));
		classifier.setAuc(eval.areaUnderROC(0));
		classifier.setKappa(eval.kappa());
		classifier.setTp(eval.numTruePositives(0));
		classifier.setFp(eval.numFalsePositives(0));
		classifier.setTn(eval.numTrueNegatives(0));
		classifier.setFn(eval.numFalseNegatives(0));
		classifier.setTrainingPerc(
				100.0 * train.numInstances() / (train.numInstances() + test.numInstances()));
		if(type.equals(randomFor)){
			computePredicted(projName, test,rf,iter,classifier);
		}else if(type.equals(naiveBay)) {
			computePredicted(projName, test,nb,iter,classifier);
		}else {
			computePredicted(projName, test,ibk,iter,classifier);
		}
	}
	
	
	public static void computePredicted(String projName, Instances test, Classifier classif, int iteration, ClassifierData clasData) throws Exception {

		int i;
		    
		int numtesting = test.numInstances();
		List<Acume> acumeList = new ArrayList<>();
		String fileName="";

		// Loop over each test instance.
	    for (i = 0; i < numtesting; i++){
		     // Get the true class label from the instance's own classIndex.
		     String trueClassLabel = test.instance(i).toString(test.classIndex());

		     // Get the prediction probability distribution.
		     double[] predictionDistribution = classif.distributionForInstance(test.instance(i)); 

			 // Get the probability.
			 double predictionProbability =  predictionDistribution[0];
			    
			 double size = test.instance(i).value(0);
			 Acume ac = new Acume(i,size,predictionProbability, trueClassLabel,clasData);
			 acumeList.add(ac);
			 
			 if(i==0) {
				 fileName = ac.getNameFile();
			 }

		 }
		    
		 FilesWriter.writeCSVForACUME(projName, acumeList,iteration,fileName);
		 
	}

}
