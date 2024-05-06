package deliverable.extractor;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import deliverable.model.*;


public class Execution {
	
	private Execution() {
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(Execution.class);
	
	public static void exec(String projName) throws Exception {
		
		JiraExtractor je = new JiraExtractor(projName.toUpperCase());
		GitExtractor ge = new GitExtractor(projName);
		List<Version> versionsList;
		List<Ticket> ticketsList;
		String mex;
		versionsList = je.getVersions(); //prendo le varei versioni da jira
		int i = 0;
		
		ticketsList = je.getIssues(versionsList); //mi faccio una lista di ticket da jira (i ticket sarebbero i bug)
		
		mex = "I TICKET: "+ticketsList.size();
		logger.info(mex);

		//PROPORTION BOZZA INIZIALE
		ProportionControl prop = new ProportionControl();
		List<Ticket> ticketWithProportion = prop.doProportion(ticketsList, versionsList);
		
		mex = "HO FATTO PROPORTION, TICKET CON PROP: "+ticketWithProportion.size();
		logger.info(mex);
		
		List<RevCommit> allCommList = ge.getCommit2();  //prendo tutti i commit
		
		mex = "I COMMIT TOTALI SONO: "+allCommList.size();
		logger.info(mex);
		
		List<VersionCommits> commitListVer;
		commitListVer = ge.seeCommitVersion(allCommList,versionsList);  //associo i commit alle versioni, mi tornerà il tipo VersionCommits dove per ogni versiono ho lista di commit associati
		
		mex = "Le Versioni con COMMIT PRESI SONO: "+commitListVer.size();
		logger.info(mex);
	
		int contaArdo = 0;

		for(i =0;i<commitListVer.size();i++) {
			contaArdo+=commitListVer.get(i).getCommits().size();
			mex = "INDICE RELEASE "+commitListVer.get(i).getVersion().getIndex()+", NOME RELEASE: "+commitListVer.get(i).getVersion().getName();
			logger.info(mex);

		}
		
		mex = "IN TOTALE SONO FILTRATI: "+contaArdo;
		logger.info(mex);
		
		ge.associateVersionAndClasses(commitListVer);  //ora le versioni le associo anche alle classi Java

		List<JavaClass> classList;
		classList = ge.createClassesIstances(commitListVer, ticketWithProportion); //ottengo una lista con tutte le classi ed inoltre grazie ai ticket metto se la classe è buggy
		
		mex = "LE CLASSI PRESE SONO: "+classList.size();
		logger.info(mex);
		
		ge.associateCommitAndClasses(commitListVer,classList,allCommList); //infine associo le classi ai commit
		
		int contaBuggy = 0;
		for(JavaClass j: classList) {
			if(j.isBuggy()) contaBuggy++;
		}
		mex = "LE CLASSI BUGGY SOMO: "+contaBuggy;
		logger.info(mex);
		
		MetricsCalculator mc = new MetricsCalculator(classList, projName);
		mc.calculateMetrics();
		
		logger.info("METRICHE FATTE");
		
		
		
		int halfSize = commitListVer.size() / 2;
		commitListVer = commitListVer.subList(0, halfSize); //prendo metà delle release
		
		for(i=1;i<=commitListVer.size()-1;i++) {
			int actualRel = i;
			
			FilesWriter fl = new FilesWriter();
			fl.writeFilesTraining(projName, commitListVer, classList, actualRel);
			fl.writeFilesTesting(projName, commitListVer, classList, actualRel);
			
			 //suddivisione falla nella funz writeFiles, o semmai dopo fai una funzione per il training e una per itesting e separi
		
		}
		logger.info("HO CREATO I CSV E ARFF");
		
		List<List<ClassifierData>> listClassifier = WekaExtractor.computeWeka(halfSize, projName);
		
		FilesWriter.writeFinalCSV(listClassifier, projName, halfSize);
		logger.info("FINITO");
	}

}
