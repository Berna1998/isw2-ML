package main.java.ml.extractor;

import weka.core.Instances;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.RandomForest;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.core.converters.ConverterUtils.DataSource;
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

		List<ClassifierData> samplRandomForestLis = new ArrayList<>();
		List<ClassifierData> samplNaiveBayesLis = new ArrayList<>();
		List<ClassifierData> samplIBkLis = new ArrayList<>();

		List<ClassifierData> costRandomForestLis = new ArrayList<>();
		List<ClassifierData> costNaiveBayesLis = new ArrayList<>();
		List<ClassifierData> costIBkLis = new ArrayList<>();

		List<ClassifierData> featSamplRandomForestLis = new ArrayList<>();
		List<ClassifierData> featSamplNaiveBayesLis = new ArrayList<>();
		List<ClassifierData> featSamplIBkLis = new ArrayList<>();

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
			ClassifierData basicRandomFor = new ClassifierData(projName, i, randomFor, false, false, false);
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
			ClassifierData basicNaiveBa = new ClassifierData(projName, i, naiveBay, false, false, false);
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
			ClassifierData basicIbk = new ClassifierData(projName, i, ibk, false, false, false);
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
			ClassifierData featureRandomFor = new ClassifierData(projName, i, randomFor, true, false, false);
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
			ClassifierData featureNaiveBay = new ClassifierData(projName, i, naiveBay, true, false, false);
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
			ClassifierData featureIBk = new ClassifierData(projName, i, ibk, true, false, false);
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

			// VERSIONE SAMPLING (SMOTE)
			SMOTE smote = new SMOTE();

			smote.setInputFormat(basicTrain);
			Instances newData = Filter.useFilter(basicTrain, smote);

			randomForClassifier.buildClassifier(newData);
			evalClass.evaluateModel(randomForClassifier, basicTes);
			ClassifierData samplRandomFor = new ClassifierData(projName, i, randomFor, false, true, false);
			samplRandomFor.setPrecision(evalClass.precision(0));
			samplRandomFor.setRecall(evalClass.recall(0));
			samplRandomFor.setAuc(evalClass.areaUnderROC(0));
			samplRandomFor.setKappa(evalClass.kappa());
			samplRandomFor.setTp(evalClass.numTruePositives(0));
			samplRandomFor.setFp(evalClass.numFalsePositives(0));
			samplRandomFor.setTn(evalClass.numTrueNegatives(0));
			samplRandomFor.setFn(evalClass.numFalseNegatives(0));
			samplRandomFor.setTrainingPerc(
					100.0 * basicTrain.numInstances() / (basicTrain.numInstances() + basicTes.numInstances()));
			samplRandomForestLis.add(samplRandomFor);

			naiveBayClassifier.buildClassifier(newData);
			evalClass.evaluateModel(naiveBayClassifier, basicTes);
			ClassifierData samplNaiveBay = new ClassifierData(projName, i, naiveBay, false, true, false);
			samplNaiveBay.setPrecision(evalClass.precision(0));
			samplNaiveBay.setRecall(evalClass.recall(0));
			samplNaiveBay.setAuc(evalClass.areaUnderROC(0));
			samplNaiveBay.setKappa(evalClass.kappa());
			samplNaiveBay.setTp(evalClass.numTruePositives(0));
			samplNaiveBay.setFp(evalClass.numFalsePositives(0));
			samplNaiveBay.setTn(evalClass.numTrueNegatives(0));
			samplNaiveBay.setFn(evalClass.numFalseNegatives(0));
			samplNaiveBay.setTrainingPerc(
					100.0 * basicTrain.numInstances() / (basicTrain.numInstances() + basicTes.numInstances()));
			samplNaiveBayesLis.add(samplNaiveBay);

			ibkClassifier.buildClassifier(newData);
			evalClass.evaluateModel(ibkClassifier, basicTes);
			ClassifierData samplIBk = new ClassifierData(projName, i, ibk, false, true, false);
			samplIBk.setPrecision(evalClass.precision(0));
			samplIBk.setRecall(evalClass.recall(0));
			samplIBk.setAuc(evalClass.areaUnderROC(0));
			samplIBk.setKappa(evalClass.kappa());
			samplIBk.setTp(evalClass.numTruePositives(0));
			samplIBk.setFp(evalClass.numFalsePositives(0));
			samplIBk.setTn(evalClass.numTrueNegatives(0));
			samplIBk.setFn(evalClass.numFalseNegatives(0));
			samplIBk.setTrainingPerc(
					100.0 * basicTrain.numInstances() / (basicTrain.numInstances() + basicTes.numInstances()));
			samplIBkLis.add(samplIBk);

			// VERSIONE COST SENSITIVE (CFN = 10*CFP)
			CostMatrix costMatr = new CostMatrix(2);
			costMatr.setCell(0, 0, 0.0);
			costMatr.setCell(1, 0, 10.0);
			costMatr.setCell(0, 1, 1.0);
			costMatr.setCell(1, 1, 0.0);

			CostSensitiveClassifier costSensClass = new CostSensitiveClassifier();

			costSensClass.setClassifier(randomForClassifier);
			costSensClass.setCostMatrix(costMatr);
			costSensClass.buildClassifier(basicTrain);
			evalClass.evaluateModel(costSensClass, basicTes);
			ClassifierData costSensRandomFor = new ClassifierData(projName, i, randomFor, false, false, true);
			costSensRandomFor.setPrecision(evalClass.precision(0));
			costSensRandomFor.setRecall(evalClass.recall(0));
			costSensRandomFor.setAuc(evalClass.areaUnderROC(0));
			costSensRandomFor.setKappa(evalClass.kappa());
			costSensRandomFor.setTp(evalClass.numTruePositives(0));
			costSensRandomFor.setFp(evalClass.numFalsePositives(0));
			costSensRandomFor.setTn(evalClass.numTrueNegatives(0));
			costSensRandomFor.setFn(evalClass.numFalseNegatives(0));
			costSensRandomFor.setTrainingPerc(
					100.0 * basicTrain.numInstances() / (basicTrain.numInstances() + basicTes.numInstances()));
			costRandomForestLis.add(costSensRandomFor);

			costSensClass.setClassifier(naiveBayClassifier);
			costSensClass.setCostMatrix(costMatr);
			costSensClass.buildClassifier(basicTrain);
			evalClass.evaluateModel(costSensClass, basicTes);
			ClassifierData costSensNaiveBa = new ClassifierData(projName, i, naiveBay, false, false, true);
			costSensNaiveBa.setPrecision(evalClass.precision(0));
			costSensNaiveBa.setRecall(evalClass.recall(0));
			costSensNaiveBa.setAuc(evalClass.areaUnderROC(0));
			costSensNaiveBa.setKappa(evalClass.kappa());
			costSensNaiveBa.setTp(evalClass.numTruePositives(0));
			costSensNaiveBa.setFp(evalClass.numFalsePositives(0));
			costSensNaiveBa.setTn(evalClass.numTrueNegatives(0));
			costSensNaiveBa.setFn(evalClass.numFalseNegatives(0));
			costSensNaiveBa.setTrainingPerc(
					100.0 * basicTrain.numInstances() / (basicTrain.numInstances() + basicTes.numInstances()));
			costNaiveBayesLis.add(costSensNaiveBa);

			costSensClass.setClassifier(ibkClassifier);
			costSensClass.setCostMatrix(costMatr);
			costSensClass.buildClassifier(basicTrain);
			evalClass.evaluateModel(costSensClass, basicTes);
			ClassifierData costSensIBk = new ClassifierData(projName, i, ibk, false, false, true);
			costSensIBk.setPrecision(evalClass.precision(0));
			costSensIBk.setRecall(evalClass.recall(0));
			costSensIBk.setAuc(evalClass.areaUnderROC(0));
			costSensIBk.setKappa(evalClass.kappa());
			costSensIBk.setTp(evalClass.numTruePositives(0));
			costSensIBk.setFp(evalClass.numFalsePositives(0));
			costSensIBk.setTn(evalClass.numTrueNegatives(0));
			costSensIBk.setFn(evalClass.numFalseNegatives(0));
			costSensIBk.setTrainingPerc(
					100.0 * basicTrain.numInstances() / (basicTrain.numInstances() + basicTes.numInstances()));
			costIBkLis.add(costSensIBk);

			// FEATURE SELECTION E SAMPLING
			SMOTE smoteFeature = new SMOTE();
			smoteFeature.setOptions(new String[] { "-M", "1.0" });
			smoteFeature.setInputFormat(featureTrain);
			Instances dataWithFeat = Filter.useFilter(featureTrain, smoteFeature);

			randomForClassifier.buildClassifier(dataWithFeat);
			evalClass.evaluateModel(randomForClassifier, featureTest);
			ClassifierData featSamplRandomFor = new ClassifierData(projName, i, randomFor, true, true, false);
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
			featSamplRandomForestLis.add(featSamplRandomFor);

			naiveBayClassifier.buildClassifier(dataWithFeat);
			evalClass.evaluateModel(naiveBayClassifier, featureTest);
			ClassifierData featSamplNaive = new ClassifierData(projName, i, naiveBay, true, true, false);
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
			featSamplNaiveBayesLis.add(featSamplNaive);

			ibkClassifier.buildClassifier(dataWithFeat);
			evalClass.evaluateModel(ibkClassifier, featureTest);
			ClassifierData featSamplIBk = new ClassifierData(projName, i, ibk, true, true, false);
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
			featSamplIBkLis.add(featSamplIBk);

			// FEATURE SELECTION E COST SENSITIVE
			costSensClass.setClassifier(randomForClassifier);
			costSensClass.setCostMatrix(costMatr);
			costSensClass.buildClassifier(featureTrain);
			evalClass.evaluateModel(costSensClass, featureTest);
			ClassifierData featCostRandomFor = new ClassifierData(projName, i, randomFor, true, false, true);
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
			ClassifierData featCostNaive = new ClassifierData(projName, i, naiveBay, true, false, true);
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
			ClassifierData featCostIBk = new ClassifierData(projName, i, ibk, true, false, true);
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

		allClassifier.add(samplRandomForestLis);
		allClassifier.add(samplNaiveBayesLis);
		allClassifier.add(samplIBkLis);

		allClassifier.add(costRandomForestLis);
		allClassifier.add(costNaiveBayesLis);
		allClassifier.add(costIBkLis);

		allClassifier.add(featSamplRandomForestLis);
		allClassifier.add(featSamplNaiveBayesLis);
		allClassifier.add(featSamplIBkLis);

		allClassifier.add(featCostRandomForestLis);
		allClassifier.add(featCostNaiveBayesLis);
		allClassifier.add(featCostIBkLis);

		return allClassifier;
	}

}
