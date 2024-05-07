package main.java.ml.extractor;

import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.ml.model.*;
import main.java.ml.model.ClassifierData;
import main.java.ml.model.JavaClass;
import main.java.ml.model.VersionCommits;

public class FilesWriter {
	private static final Logger logger = LoggerFactory.getLogger(FilesWriter.class);
	
	public void writeFilesTraining(String nameProj, List<VersionCommits> versionsList, List<JavaClass> classList,
			int actualRel) {
		int i = 0; 
		String directoryStr = "csvAndArffFiles/";
		Path directory = Paths.get(directoryStr);

		if (!Files.exists(directory)) {
			try {
				Files.createDirectories(directory);
			} catch (IOException e) {
				logger.warn("context", e);
				return;
			}
		}

		// Scrivi nel file CSV
		String csvFilePath = directoryStr + nameProj + "_" + actualRel + "_Training.csv";

		try (FileWriter writerCSV = new FileWriter(csvFilePath)) {

			writerCSV.append(
					"Release;Class Name;Size;nR;nAuth;LOC Touched;LOC Added;Max LOC Added;Avg LOC Added;Churn;Max Churn;Avg Churn;Is Buggy");
			writerCSV.append("\n");
			// Ciclo for dove metto i dati, ogni riga è witer.writeNext
			for (i = 0; i < actualRel; i++) {
				VersionCommits trainVer = versionsList.get(i);
				writeDataInCSV(writerCSV, trainVer, classList);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scrivi nel file ARFF
		String arffFilePath = directoryStr + nameProj + "_" + actualRel + "_Training.arff";
		try (FileWriter arffWriter = new FileWriter(arffFilePath)) {
			String name = nameProj + "_" + actualRel + "_Training.arff";
			arffWriter.write("@relation " + name + "\n");
			arffWriter.write("@attribute Size numeric\n");
			arffWriter.write("@attribute nR numeric\n");
			arffWriter.write("@attribute nAuth numeric\n");
			arffWriter.write("@attribute LOC_Touched numeric\n");
			arffWriter.write("@attribute LOC_Added numeric\n");
			arffWriter.write("@attribute Max_LOC_Added numeric\n");
			arffWriter.write("@attribute Avg_LOC_Added numeric\n");
			arffWriter.write("@attribute Churn numeric\n");
			arffWriter.write("@attribute Max_Churn numeric\n");
			arffWriter.write("@attribute Avg_Churn numeric\n");
			arffWriter.write("@attribute Is_Buggy {'Yes', 'No'}\n\n");
			arffWriter.write("@data\n"); // FINO A QUA DATA, SCRIVI I VALORI CHE ANALIZZI

			for (i = 0; i < actualRel; i++) {
				VersionCommits trainVer = versionsList.get(i);
				for (JavaClass classJ : classList) {
					if (classJ.getVersion() == trainVer.getVersion()) { // VEDI SE LASCIARE COSI' O METTENDO INDEX
																		// CAMBIA, AMCHE NEGLI ALTRI
						writeDataInArff(arffWriter, classJ);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeFilesTesting(String nameProj, List<VersionCommits> versionsList, List<JavaClass> classList,
			int actualRel) {

		String directoryStr = "csvAndArffFiles/";
		Path directory = Paths.get(directoryStr);
		if (!Files.exists(directory)) {
			try {
				Files.createDirectories(directory);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		// Scrivi nel file CSV
		String csvFilePath = directoryStr + nameProj + "_" + actualRel + "_Testing.csv";

		try (FileWriter writerCSV = new FileWriter(csvFilePath);) {

			writerCSV.append(
					"Release;Class Name;Size;nR;nAuth;LOC Touched;LOC Added;Max LOC Added;Avg LOC Added;Churn;Max Churn;Avg Churn;Is Buggy");
			writerCSV.append("\n");

			VersionCommits testVer = versionsList.get(actualRel);
			writeDataInCSV(writerCSV, testVer, classList);

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Scrivi nel file ARFF
		String arffFilePath = directoryStr + nameProj + "_" + actualRel + "_Testing.arff";
		try (FileWriter arffWriter = new FileWriter(arffFilePath)) {
			String name = nameProj + "_" + actualRel + "_Testing.arff";
			arffWriter.write("@relation " + name + "\n\n");
			arffWriter.write("@attribute Size numeric\n");
			arffWriter.write("@attribute nR numeric\n");
			arffWriter.write("@attribute nAuth numeric\n");
			arffWriter.write("@attribute LOC_Touched numeric\n");
			arffWriter.write("@attribute LOC_Added numeric\n");
			arffWriter.write("@attribute Max_LOC_Added numeric\n");
			arffWriter.write("@attribute Avg_LOC_Added numeric\n");
			arffWriter.write("@attribute Churn numeric\n");
			arffWriter.write("@attribute Max_Churn numeric\n");
			arffWriter.write("@attribute Avg_Churn numeric\n");
			arffWriter.write("@attribute Is_Buggy {'Yes', 'No'}\n\n");
			arffWriter.write("@data\n"); // FINO A QUA DATA, SCRIVI I VALORI CHE ANALIZZI

			VersionCommits testVer = versionsList.get(actualRel);
			for (JavaClass classJ : classList) {
				if (classJ.getVersion() == testVer.getVersion()) { // VEDI SE LASCIARE COSI' O METTENDO INDEX CAMBIA,
																	// AMCHE NEGLI ALTRI
					writeDataInArff(arffWriter, classJ);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeDataInCSV(FileWriter writer, VersionCommits vers, List<JavaClass> classList) throws IOException {

		for (JavaClass jClass : classList) {
			if (jClass.getVersion() == vers.getVersion()) { // VEDI SE LASCIARE COSI' O METTENDO INDEX CAMBIA, AMCHE
															// NEGLI ALTRI
				String className = jClass.getName();
				String version = vers.getVersion().getName();
				String size = String.valueOf(jClass.getSize());
				String buggy = "";
				if (jClass.isBuggy()) {
					buggy = "Yes\n";
				} else {
					buggy = "No\n";
				}
				String touchLoc = String.valueOf(jClass.getTouchLoc());
				String nR = String.valueOf(jClass.getNr());
				String nAuth = String.valueOf(jClass.getnAuth());
				String addedLoc = String.valueOf(jClass.getAddedLoc());
				String maxAddedLoc = String.valueOf(jClass.getMaxAddedLoc());
				String avgAddedLoc = String.valueOf(jClass.getAvgAddedLoc());
				String churn = String.valueOf(jClass.getChurn());
				String maxChurn = String.valueOf(jClass.getMaxChurn());
				String avgChurn = String.valueOf(jClass.getAvgChurn());

				writer.append(version);
				writer.append(";");
				writer.append(className);
				writer.append(";");
				writer.append(size);
				writer.append(";");
				writer.append(nR);
				writer.append(";");
				writer.append(nAuth);
				writer.append(";");
				writer.append(touchLoc);
				writer.append(";");
				writer.append(addedLoc);
				writer.append(";");
				writer.append(maxAddedLoc);
				writer.append(";");
				writer.append(avgAddedLoc);
				writer.append(";");
				writer.append(churn);
				writer.append(";");
				writer.append(maxChurn);
				writer.append(";");
				writer.append(avgChurn);
				writer.append(";");
				writer.append(buggy);
				writer.append("\n");
			}
		}

	}

	private void writeDataInArff(FileWriter writer, JavaClass jClass) throws IOException {

		String size = String.valueOf(jClass.getSize());
		String buggy = "";
		if (jClass.isBuggy()) {
			buggy = "Yes\n";
		} else {
			buggy = "No\n";
		}
		String touchLoc = String.valueOf(jClass.getTouchLoc());
		String nR = String.valueOf(jClass.getNr());
		String nAuth = String.valueOf(jClass.getnAuth());
		String addedLoc = String.valueOf(jClass.getAddedLoc());
		String maxAddedLoc = String.valueOf(jClass.getMaxAddedLoc());
		String avgAddedLoc = String.valueOf(jClass.getAvgAddedLoc());
		String churn = String.valueOf(jClass.getChurn());
		String maxChurn = String.valueOf(jClass.getMaxChurn());
		String avgChurn = String.valueOf(jClass.getAvgChurn());

		writer.append(size).append(",").append(nR).append(",").append(nAuth).append(",").append(touchLoc).append(",")
				.append(addedLoc).append(",").append(maxAddedLoc).append(",").append(avgAddedLoc).append(",")
				.append(churn).append(",").append(maxChurn).append(",").append(avgChurn).append(",").append(buggy);

	}

	public static void writeFinalCSV(List<List<ClassifierData>> allValueWeka, String nameProj, int iterations) {
		// SCRIVI CSV FINALE
		String directoryStr = "FinalCSV/";
		String csvFilePath = directoryStr + nameProj + "_Final.csv";
		Path directory = Paths.get(directoryStr);

		if (!Files.exists(directory)) {
			try {
				Files.createDirectories(directory);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		try (FileWriter writerCSV = new FileWriter(csvFilePath);) {

			writerCSV.append(
					"Project;N° Iteration;Classifier;Feature Selection;Sampling;Cost Sensitive;Training %;Precision;Recall;AUC;Kappa;True Positive;False Positive;True Negative;False Negative");
			writerCSV.append("\n");
			// For iterazioni, prendi di ogni lista presente la posizione i-1
			int i;
			for (i = 1; i < iterations; i++) {
				for (List<ClassifierData> singleList : allValueWeka) {
					ClassifierData classifier = singleList.get(i - 1);
					writeDataFinalCSV(classifier, writerCSV, i);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeDataFinalCSV(ClassifierData classifier, FileWriter writer, int iteration)
			throws IOException {
		String project = classifier.getProjName();
		String numIteration = String.valueOf(iteration);
		String classifierName = classifier.getClassifierName();
		String featureSel = "";
		if (classifier.isFeatureSel()) {
			featureSel = "Yes";
		} else {
			featureSel = "No";
		}
		String sampling = "";
		if (classifier.isSampling()) {
			sampling = "Yes";
		} else {
			sampling = "No";
		}
		String costSens = "";
		if (classifier.isCostSensitive()) {
			costSens = "Yes";
		} else {
			costSens = "No";
		}
		String trainingPerc = String.valueOf(classifier.getTrainingPerc());
		String precision = String.valueOf(classifier.getPrecision());
		String recall = String.valueOf(classifier.getRecall());
		String auc = String.valueOf(classifier.getAuc());
		String kappa = String.valueOf(classifier.getKappa());
		String tp = String.valueOf(classifier.getTp());
		String fp = String.valueOf(classifier.getFp());
		String tn = String.valueOf(classifier.getTn());
		String fn = String.valueOf(classifier.getFn());

		writer.append(project);
		writer.append(";");
		writer.append(numIteration);
		writer.append(";");
		writer.append(classifierName);
		writer.append(";");
		writer.append(featureSel);
		writer.append(";");
		writer.append(sampling);
		writer.append(";");
		writer.append(costSens);
		writer.append(";");
		writer.append(trainingPerc);
		writer.append(";");
		writer.append(precision);
		writer.append(";");
		writer.append(recall);
		writer.append(";");
		writer.append(auc);
		writer.append(";");
		writer.append(kappa);
		writer.append(";");
		writer.append(tp);
		writer.append(";");
		writer.append(fp);
		writer.append(";");
		writer.append(tn);
		writer.append(";");
		writer.append(fn);
		writer.append("\n");
	}
}
