package main.java.ml.extractor;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;

import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;

import org.eclipse.jgit.internal.storage.file.FileRepository;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import org.eclipse.jgit.util.io.DisabledOutputStream;

import main.java.ml.model.JavaClass;

public class MetricsCalculator {

	private List<JavaClass> listOfClass;
	private Repository repository;

	public MetricsCalculator(List<JavaClass> listOfClass, String projName) throws IOException {
		this.listOfClass = listOfClass;
		String url = "C:\\Users\\franc\\Desktop\\CartellaGit\\" + projName + "/.git";

		this.repository = new FileRepository(url);
	}

	public void calculateSize(JavaClass jClass) {
		// SIZE LOC

		int numLines = 0;
		String[] lines = jClass.getContent().split("\r\n|\r|\n");
		numLines = lines.length;

		jClass.setSize(numLines);
	}

	public void calculateNR(JavaClass classJ) {
		// nR = numero di revisioni
		int nR;
		nR = classJ.getCommits().size();
		classJ.setNr(nR);
	}

	public void calculateNAuth(JavaClass classJ) {
		// nAuth = numero di autori
		List<String> authorL = new ArrayList<>();
		for (RevCommit comm : classJ.getCommits()) {
			String author = comm.getAuthorIdent().getName();
			if (!(authorL.contains(author))) {
				authorL.add(author);
			}
		}
		classJ.setnAuth(authorL.size());

	}

	public void calculateLOCAndChurnMetrics(JavaClass jClass) {
		// TOuched LOC, Added Loc, MAX Added LOC, AVG Added LOC, Churn, Max Churn e Avg
		// Churn
		int touchLOC = 0;
		int addLOC = 0;
		int maxLOC = 0;
		float avgAddLOC = 0;
		int churn = 0;
		int maxChurn = 0;
		float avgChurn = 0;

		int i = 0;
		int lenLinesLis = jClass.getAddedLinesLis().size();
		for (i = 0; i < lenLinesLis; i++) {
			int currAddLoc = jClass.getAddedLinesLis().get(i);
			int currRemLoc = jClass.getRemovedLinesLis().get(i);

			addLOC += currAddLoc; // LOC Added

			if (currAddLoc >= maxLOC) {
				maxLOC = currAddLoc; // Max LOC Added
			}

			touchLOC += Math.abs(currAddLoc + currRemLoc); // LOC Touched

			churn += Math.abs(currAddLoc - currRemLoc); // churn

			if (churn >= maxChurn) {
				maxChurn = churn; // max churn
			}
		}
		if (lenLinesLis > 0) { // Se ho elementi nella lista faccio la media, sennò è 0
			avgAddLOC = (float) addLOC / (lenLinesLis); // Avg LOC Added
			BigDecimal bd = new BigDecimal(Float.toString(avgAddLOC));
	        bd = bd.setScale(2, RoundingMode.DOWN);
	        avgAddLOC = bd.floatValue();
			
			avgChurn = (float) churn / (lenLinesLis); // Avg Churn
			BigDecimal bd2 = new BigDecimal(Float.toString(avgChurn));
	        bd2 = bd2.setScale(2, RoundingMode.DOWN);
	        avgChurn = bd2.floatValue();
		}

		// I vari set
		jClass.setAddedLoc(addLOC);
		jClass.setTouchLoc(touchLOC);
		jClass.setMaxAddedLoc(maxLOC);
		jClass.setAvgAddedLoc(avgAddLOC);
		jClass.setChurn(churn);
		jClass.setMaxChurn(maxChurn);
		jClass.setAvgChurn(avgChurn);

	}

	private void retrieveAddedAndDeletedLines(JavaClass jClass) throws IOException {
		List<RevCommit> commList = jClass.getCommits();
		for (RevCommit commit : commList) {
			try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
				RevCommit parentComm = commit.getParent(0);
				diffFormatter.setRepository(repository);
				diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
				diffFormatter.setDetectRenames(true);

				List<DiffEntry> diffEntrList = diffFormatter.scan(parentComm.getTree(), commit.getTree());
				for (DiffEntry diffEntry : diffEntrList) {
					if (diffEntry.getNewPath().equals(jClass.getName())) {
						jClass.getAddedLinesLis().add(getChangeAtLines(diffFormatter, diffEntry, 1));
						jClass.getRemovedLinesLis().add(getChangeAtLines(diffFormatter, diffEntry, 2));
					}
				}

			} catch (ArrayIndexOutOfBoundsException ignored) {
				// se non trova un parent è ignorato
			}
		}

	}

	private int getChangeAtLines(DiffFormatter diffFormatter, DiffEntry diffEntry, int selection) throws IOException {

		int changeLines = 0;
		if (selection == 1) { // linee aggiunte
			for (Edit edit : diffFormatter.toFileHeader(diffEntry).toEditList()) {
				changeLines += edit.getEndB() - edit.getBeginB();

			}
		} else { // linee eliminate
			for (Edit edit : diffFormatter.toFileHeader(diffEntry).toEditList()) {
				changeLines += edit.getEndA() - edit.getBeginA();

			}
		}

		return changeLines;
	}

	public void calculateMetrics() throws IOException {
		// QUI CHIAMO LE VARIE FUNZIONI CHE CALCOLANO LE VARIE METRICHE
		for (JavaClass jClass : listOfClass) {
			// chiamo i singoli metodi passando la classe singola
			calculateSize(jClass);
			retrieveAddedAndDeletedLines(jClass);
			calculateNR(jClass);
			calculateNAuth(jClass);
			calculateLOCAndChurnMetrics(jClass);

		}
	}

}
