package main.java.ml.extractor;

import weka.core.AttributeStats;
import weka.core.Instances;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
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
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import java.util.ArrayList;
import java.util.List;

import main.java.ml.model.ClassifierData;

public class WekaExtractor {
	private static String randomFor = "RandomForest";
	private static String naiveBay = "NaiveBayes";
	private static String ibk = "IBk";

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
			DataSource sourceTr = new DataSource("csvAndArffFiles/" + projName + "_" + i + "_Training.arff"); // FILE
																												// DAL
																												// TRAINING
			Instances basicTrain = sourceTr.getDataSet();

			DataSource sourceTes = new DataSource("csvAndArffFiles/" + projName + "_" + i + "_Testing.arff"); // FILE
																												// DAL
																												// TESTING
			Instances basicTes = sourceTes.getDataSet();

			RandomForest randomForClassifier = new RandomForest();
			NaiveBayes naiveBayClassifier = new NaiveBayes();
			IBk ibkClassifier = new IBk();

			int numAttrBasic = basicTrain.numAttributes();
			basicTrain.setClassIndex(numAttrBasic - 1);
			basicTes.setClassIndex(numAttrBasic - 1);

			// evaluation with no filtered
			Evaluation evalClass = new Evaluation(basicTes);

			// Basic Random Forest
			randomForClassifier.buildClassifier(basicTrain);
			evalClass.evaluateModel(randomForClassifier, basicTes);
			ClassifierData basicRandomFor = new ClassifierData(projName, i, randomFor, false, "No", false);
			basicRandomFor.setPrecision(evalClass.precision(0));
			basicRandomFor.setRecall(evalClass.recall(0));
			basicRandomFor.setAuc(evalClass.areaUnderROC(0));
			basicRandomFor.setKappa(evalClass.kappa());
			basicRandomFor.setTp(evalClass.numTruePositives(0));
			basicRandomFor.setFp(evalClass.numFalsePositives(0));
			basicRandomFor.setTn(evalClass.numTrueNegatives(0));
			basicRandomFor.setFn(evalClass.numFalseNegatives(0));
			basicRandomFor.setTrainingPerc(
					100.0 * basicTrain.numInstances() / (basicTrain.numInstances() + basicTes.numInstances()));
			basicRandomForestLis.add(basicRandomFor);

			// Basic Naive Bayes
			naiveBayClassifier.buildClassifier(basicTrain);
			evalClass.evaluateModel(naiveBayClassifier, basicTes);
			ClassifierData basicNaiveBa = new ClassifierData(projName, i, naiveBay, false, "No", false);
			basicNaiveBa.setPrecision(evalClass.precision(0));
			basicNaiveBa.setRecall(evalClass.recall(0));
			basicNaiveBa.setAuc(evalClass.areaUnderROC(0));
			basicNaiveBa.setKappa(evalClass.kappa());
			basicNaiveBa.setTp(evalClass.numTruePositives(0));
			basicNaiveBa.setFp(evalClass.numFalsePositives(0));
			basicNaiveBa.setTn(evalClass.numTrueNegatives(0));
			basicNaiveBa.setFn(evalClass.numFalseNegatives(0));
			basicNaiveBa.setTrainingPerc(
					100.0 * basicTrain.numInstances() / (basicTrain.numInstances() + basicTes.numInstances()));
			basicNaiveBayesLis.add(basicNaiveBa);

			// Basic IBk
			ibkClassifier.buildClassifier(basicTrain);
			evalClass.evaluateModel(ibkClassifier, basicTes);
			ClassifierData basicIbk = new ClassifierData(projName, i, ibk, false, "No", false);
			basicIbk.setPrecision(evalClass.precision(0));
			basicIbk.setRecall(evalClass.recall(0));
			basicIbk.setAuc(evalClass.areaUnderROC(0));
			basicIbk.setKappa(evalClass.kappa());
			basicIbk.setTp(evalClass.numTruePositives(0));
			basicIbk.setFp(evalClass.numFalsePositives(0));
			basicIbk.setTn(evalClass.numTrueNegatives(0));
			basicIbk.setFn(evalClass.numFalseNegatives(0));
			basicIbk.setTrainingPerc(
					100.0 * basicTrain.numInstances() / (basicTrain.numInstances() + basicTes.numInstances()));
			basicIBkLis.add(basicIbk);

			// Versioni Feature Selection

			AttributeSelection filter = new AttributeSelection();
			CfsSubsetEval cfsEval = new CfsSubsetEval();
			GreedyStepwise search = new GreedyStepwise();

			search.setSearchBackwards(true);

			filter.setEvaluator(cfsEval);
			filter.setSearch(search);
			filter.setInputFormat(basicTrain);

			Instances featureTrain = Filter.useFilter(basicTrain, filter);

			int numAttrFeature = featureTrain.numAttributes();

			featureTrain.setClassIndex(numAttrFeature - 1);
			Instances featureTest = Filter.useFilter(basicTes, filter);
			featureTest.setClassIndex(numAttrFeature - 1);

			randomForClassifier.buildClassifier(featureTrain);
			evalClass.evaluateModel(randomForClassifier, featureTest);
			ClassifierData featureRandomFor = new ClassifierData(projName, i, randomFor, true, "No", false);
			featureRandomFor.setPrecision(evalClass.precision(0));
			featureRandomFor.setRecall(evalClass.recall(0));
			featureRandomFor.setAuc(evalClass.areaUnderROC(0));
			featureRandomFor.setKappa(evalClass.kappa());
			featureRandomFor.setTp(evalClass.numTruePositives(0));
			featureRandomFor.setFp(evalClass.numFalsePositives(0));
			featureRandomFor.setTn(evalClass.numTrueNegatives(0));
			featureRandomFor.setFn(evalClass.numFalseNegatives(0));
			featureRandomFor.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featureRandomForestLis.add(featureRandomFor);

			naiveBayClassifier.buildClassifier(featureTrain);
			evalClass.evaluateModel(naiveBayClassifier, featureTest);
			ClassifierData featureNaiveBay = new ClassifierData(projName, i, naiveBay, true, "No", false);
			featureNaiveBay.setPrecision(evalClass.precision(0));
			featureNaiveBay.setRecall(evalClass.recall(0));
			featureNaiveBay.setAuc(evalClass.areaUnderROC(0));
			featureNaiveBay.setKappa(evalClass.kappa());
			featureNaiveBay.setTp(evalClass.numTruePositives(0));
			featureNaiveBay.setFp(evalClass.numFalsePositives(0));
			featureNaiveBay.setTn(evalClass.numTrueNegatives(0));
			featureNaiveBay.setFn(evalClass.numFalseNegatives(0));
			featureNaiveBay.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featureNaiveBayesLis.add(featureNaiveBay);

			ibkClassifier.buildClassifier(featureTrain);
			evalClass.evaluateModel(ibkClassifier, featureTest);
			ClassifierData featureIBk = new ClassifierData(projName, i, ibk, true, "No", false);
			featureIBk.setPrecision(evalClass.precision(0));
			featureIBk.setRecall(evalClass.recall(0));
			featureIBk.setAuc(evalClass.areaUnderROC(0));
			featureIBk.setKappa(evalClass.kappa());
			featureIBk.setTp(evalClass.numTruePositives(0));
			featureIBk.setFp(evalClass.numFalsePositives(0));
			featureIBk.setTn(evalClass.numTrueNegatives(0));
			featureIBk.setFn(evalClass.numFalseNegatives(0));
			featureIBk.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featureIBkLis.add(featureIBk);
			
			//STATS CLASSI SAMPLING
			AttributeStats statsSampl = featureTrain.attributeStats(numAttrFeature-1);
			int majority = statsSampl.nominalCounts[1];
			int minority = statsSampl.nominalCounts[0];
			
			// FEATURE SELECTION E SAMPLING SMOTE OK
			SMOTE smote = new SMOTE();
			double percentageSMOTE;
			if(minority==0 || minority > majority){
	            percentageSMOTE = 0;
	        }else{
	            percentageSMOTE = (100.0*(majority-minority))/minority;
	        }
			smote.setClassValue("1");
	        smote.setPercentage(percentageSMOTE);
			smote.setInputFormat(featureTrain);
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(smote);
			
			fc.setClassifier(randomForClassifier);
			fc.buildClassifier(featureTrain);
			
			evalClass.evaluateModel(fc, featureTest);
			ClassifierData featSamplRandomFor = new ClassifierData(projName, i, randomFor, true, "SMOTE", false);
			featSamplRandomFor.setPrecision(evalClass.precision(0));
			featSamplRandomFor.setRecall(evalClass.recall(0));
			featSamplRandomFor.setAuc(evalClass.areaUnderROC(0));
			featSamplRandomFor.setKappa(evalClass.kappa());
			featSamplRandomFor.setTp(evalClass.numTruePositives(0));
			featSamplRandomFor.setFp(evalClass.numFalsePositives(0));
			featSamplRandomFor.setTn(evalClass.numTrueNegatives(0));
			featSamplRandomFor.setFn(evalClass.numFalseNegatives(0));
			featSamplRandomFor.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featSMOTERandomForestLis.add(featSamplRandomFor);

			fc.setClassifier(naiveBayClassifier);
			fc.buildClassifier(featureTrain);
			evalClass.evaluateModel(fc, featureTest);
			ClassifierData featSamplNaive = new ClassifierData(projName, i, naiveBay, true, "SMOTE", false);
			featSamplNaive.setPrecision(evalClass.precision(0));
			featSamplNaive.setRecall(evalClass.recall(0));
			featSamplNaive.setAuc(evalClass.areaUnderROC(0));
			featSamplNaive.setKappa(evalClass.kappa());
			featSamplNaive.setTp(evalClass.numTruePositives(0));
			featSamplNaive.setFp(evalClass.numFalsePositives(0));
			featSamplNaive.setTn(evalClass.numTrueNegatives(0));
			featSamplNaive.setFn(evalClass.numFalseNegatives(0));
			featSamplNaive.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featSMOTENaiveBayesLis.add(featSamplNaive);

			fc.setClassifier(ibkClassifier);
			fc.buildClassifier(featureTrain);
			evalClass.evaluateModel(fc, featureTest);
			ClassifierData featSamplIBk = new ClassifierData(projName, i, ibk, true, "SMOTE", false);
			featSamplIBk.setPrecision(evalClass.precision(0));
			featSamplIBk.setRecall(evalClass.recall(0));
			featSamplIBk.setAuc(evalClass.areaUnderROC(0));
			featSamplIBk.setKappa(evalClass.kappa());
			featSamplIBk.setTp(evalClass.numTruePositives(0));
			featSamplIBk.setFp(evalClass.numFalsePositives(0));
			featSamplIBk.setTn(evalClass.numTrueNegatives(0));
			featSamplIBk.setFn(evalClass.numFalseNegatives(0));
			featSamplIBk.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featSMOTEIBkLis.add(featSamplIBk);
			
			
			//FEATURE SELECTION E OVERSAMPLING
			double percentageOver = ((100.0*majority)/(majority + minority))*2;
			Resample resample = new Resample();
			resample.setInputFormat(featureTrain);
	        resample.setOptions(new String[] {"-B", "1.0","-Z", Double.toString(2*percentageOver)});
	        fc = new FilteredClassifier();
	        fc.setFilter(resample);
			
	        fc.setClassifier(randomForClassifier);
			fc.buildClassifier(featureTrain);
			evalClass.evaluateModel(fc, featureTest);
			ClassifierData featSamplRandomFor2 = new ClassifierData(projName, i, randomFor, true, "OverSampling", false);
			featSamplRandomFor2.setPrecision(evalClass.precision(0));
			featSamplRandomFor2.setRecall(evalClass.recall(0));
			featSamplRandomFor2.setAuc(evalClass.areaUnderROC(0));
			featSamplRandomFor2.setKappa(evalClass.kappa());
			featSamplRandomFor2.setTp(evalClass.numTruePositives(0));
			featSamplRandomFor2.setFp(evalClass.numFalsePositives(0));
			featSamplRandomFor2.setTn(evalClass.numTrueNegatives(0));
			featSamplRandomFor2.setFn(evalClass.numFalseNegatives(0));
			featSamplRandomFor2.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featOverRandomForestLis.add(featSamplRandomFor2);
			
			fc.setClassifier(naiveBayClassifier);
			fc.buildClassifier(featureTrain);
			evalClass.evaluateModel(fc, featureTest);
			ClassifierData featSamplNaive2 = new ClassifierData(projName, i, naiveBay, true, "OverSampling", false);
			featSamplNaive2.setPrecision(evalClass.precision(0));
			featSamplNaive2.setRecall(evalClass.recall(0));
			featSamplNaive2.setAuc(evalClass.areaUnderROC(0));
			featSamplNaive2.setKappa(evalClass.kappa());
			featSamplNaive2.setTp(evalClass.numTruePositives(0));
			featSamplNaive2.setFp(evalClass.numFalsePositives(0));
			featSamplNaive2.setTn(evalClass.numTrueNegatives(0));
			featSamplNaive2.setFn(evalClass.numFalseNegatives(0));
			featSamplNaive2.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featOverNaiveBayesLis.add(featSamplNaive2);
			
			fc.setClassifier(ibkClassifier);
			fc.buildClassifier(featureTrain);
			evalClass.evaluateModel(fc, featureTest);
			ClassifierData featSamplIBk2 = new ClassifierData(projName, i, ibk, true, "OverSampling", false);
			featSamplIBk2.setPrecision(evalClass.precision(0));
			featSamplIBk2.setRecall(evalClass.recall(0));
			featSamplIBk2.setAuc(evalClass.areaUnderROC(0));
			featSamplIBk2.setKappa(evalClass.kappa());
			featSamplIBk2.setTp(evalClass.numTruePositives(0));
			featSamplIBk2.setFp(evalClass.numFalsePositives(0));
			featSamplIBk2.setTn(evalClass.numTrueNegatives(0));
			featSamplIBk2.setFn(evalClass.numFalseNegatives(0));
			featSamplIBk2.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featOverIBkLis.add(featSamplIBk2);
			
			
			// FEATURE SELECTION E COST SENSITIVE
			CostMatrix costMatr = new CostMatrix(2);
			costMatr.setCell(0, 0, 0.0);
			costMatr.setCell(1, 0, 1.0);
			costMatr.setCell(0, 1, 10.0);
			costMatr.setCell(1, 1, 0.0);
			CostSensitiveClassifier costSensClass = new CostSensitiveClassifier();
			
			costSensClass.setMinimizeExpectedCost(false);
			costSensClass.setClassifier(randomForClassifier);
			costSensClass.setCostMatrix(costMatr);
			costSensClass.buildClassifier(featureTrain);
			evalClass.evaluateModel(costSensClass, featureTest);
			ClassifierData featCostRandomFor = new ClassifierData(projName, i, randomFor, true, "No", true);
			featCostRandomFor.setPrecision(evalClass.precision(0));
			featCostRandomFor.setRecall(evalClass.recall(0));
			featCostRandomFor.setAuc(evalClass.areaUnderROC(0));
			featCostRandomFor.setKappa(evalClass.kappa());
			featCostRandomFor.setTp(evalClass.numTruePositives(0));
			featCostRandomFor.setFp(evalClass.numFalsePositives(0));
			featCostRandomFor.setTn(evalClass.numTrueNegatives(0));
			featCostRandomFor.setFn(evalClass.numFalseNegatives(0));
			featCostRandomFor.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featCostRandomForestLis.add(featCostRandomFor);

			costSensClass.setClassifier(naiveBayClassifier);
			costSensClass.setCostMatrix(costMatr);
			costSensClass.buildClassifier(featureTrain);
			evalClass.evaluateModel(costSensClass, featureTest);
			ClassifierData featCostNaive = new ClassifierData(projName, i, naiveBay, true, "No", true);
			featCostNaive.setPrecision(evalClass.precision(0));
			featCostNaive.setRecall(evalClass.recall(0));
			featCostNaive.setAuc(evalClass.areaUnderROC(0));
			featCostNaive.setKappa(evalClass.kappa());
			featCostNaive.setTp(evalClass.numTruePositives(0));
			featCostNaive.setFp(evalClass.numFalsePositives(0));
			featCostNaive.setTn(evalClass.numTrueNegatives(0));
			featCostNaive.setFn(evalClass.numFalseNegatives(0));
			featCostNaive.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
			featCostNaiveBayesLis.add(featCostNaive);

			costSensClass.setClassifier(ibkClassifier);
			costSensClass.setCostMatrix(costMatr);
			costSensClass.buildClassifier(featureTrain);
			evalClass.evaluateModel(costSensClass, featureTest);
			ClassifierData featCostIBk = new ClassifierData(projName, i, ibk, true, "No", true);
			featCostIBk.setPrecision(evalClass.precision(0));
			featCostIBk.setRecall(evalClass.recall(0));
			featCostIBk.setAuc(evalClass.areaUnderROC(0));
			featCostIBk.setKappa(evalClass.kappa());
			featCostIBk.setTp(evalClass.numTruePositives(0));
			featCostIBk.setFp(evalClass.numFalsePositives(0));
			featCostIBk.setTn(evalClass.numTrueNegatives(0));
			featCostIBk.setFn(evalClass.numFalseNegatives(0));
			featCostIBk.setTrainingPerc(
					100.0 * featureTrain.numInstances() / (featureTrain.numInstances() + featureTest.numInstances()));
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

}
