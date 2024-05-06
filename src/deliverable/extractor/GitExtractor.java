package deliverable.extractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import deliverable.model.*;

public class GitExtractor {
	String projName;
	private Repository repo;
	private Git git;
	private String pointJava = ".java";
	private String test = "/test/";
	private static final Logger logger = LoggerFactory.getLogger(GitExtractor.class);
	public GitExtractor(String name) throws IOException {
		this.projName = name;
		String url = "C:\\Users\\franc\\Desktop\\CartellaGit\\"+name+"/.git";
		
		this.repo = new FileRepository(url);
		this.git = new Git(this.repo);
	}
	
	private RevCommit getLastCom(List<RevCommit> valideCommits) {

		RevCommit lastCom = valideCommits.get(0);
		for(RevCommit com: valideCommits) {
			if(com.getCommitterIdent().getWhen().after(lastCom.getCommitterIdent().getWhen())) {
				lastCom = com;
			}
		}
		return lastCom;
	}
	
	public List<VersionCommits> seeCommitVersion(List<RevCommit> allCommit, List<Version> versionsList) throws ParseException{
		List<VersionCommits> commitList = new ArrayList<>();
		int i = 0;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date prevDate = formatter.parse("1950-05-10");
		
		for(Version ver: versionsList) {
			Date verDate = ver.getDate();
			List<RevCommit> valideCommits = new ArrayList<>();
			
			for(RevCommit com: allCommit) {
				Date commDate = com.getCommitterIdent().getWhen();
				if(commDate.after(prevDate)&&commDate.before(verDate)||commDate.equals(verDate)) {
					valideCommits.add(com);
					
				}
			}

			if(!valideCommits.isEmpty()) {
				RevCommit lastCommit = getLastCom(valideCommits);
				VersionCommits comVer = new VersionCommits(ver,lastCommit,valideCommits);
				commitList.add(comVer);
			}
			prevDate = verDate;
		}
		
		for(i = 1;i<=commitList.size();i++) {
			commitList.get(i-1).getVersion().setIndex(i);
		}
		return commitList;
	}
	
	public List<RevCommit> getCommit2() throws GitAPIException, IOException{
		List<RevCommit> allCommitList = new ArrayList<>();
		List<Ref> branchesList = this.git.branchList().setListMode(ListMode.ALL).call();
		
		//Branches loop
		for(Ref branch : branchesList) {
			Iterable<RevCommit> commitsList = this.git.log().add(this.repo.resolve(branch.getName())).call();
			
			for(RevCommit commit : commitsList) {
			
				if(!allCommitList.contains(commit)) {
					allCommitList.add(commit);				
				}
				
			}
			
		}

		
		return allCommitList;
	}

	public void associateVersionAndClasses(List<VersionCommits> commitVerList) throws IOException {
		
		for(VersionCommits ver: commitVerList) {
			
			List<String> javaClass = new ArrayList<>();
			
			TreeWalk treeW = new TreeWalk(repo);
			
			RevCommit commit = ver.getLastCommit();
			ObjectId treeId = commit.getTree().getId();
			
			treeW.reset(treeId);			
			treeW.setRecursive(true);
			
			
			while(treeW.next()) {
				if(treeW.isSubtree()) {
					treeW.enterSubtree();
				}else {
					if(!treeW.getPathString().contains(test) && treeW.getPathString().contains(pointJava) ) {
						String className = treeW.getPathString();
						
						javaClass.add(className);
					}
				}
			}		
			treeW.close();
			
			ver.setJavaClasses(javaClass);
			
		}
		
		
	}


	public void associateCommitAndClasses(List<VersionCommits> versionCommList, List<JavaClass> classList, List<RevCommit> allCommList) throws IOException {
		
		for(RevCommit commit: allCommList) {
			
			Version versionOfCom = getVersionOfCommit(commit,versionCommList);
			
			if(versionOfCom!=null) {		
				List<String> modifiedClasses = getModifiedClasses(commit);

				for(String modifClass : modifiedClasses) {
					updateClassCommits(classList, modifClass, versionOfCom, commit);
					
				}
				
			}
		}
		
	}

	private Version getVersionOfCommit(RevCommit commit, List<VersionCommits> versionCommList) {
		for(VersionCommits verComm : versionCommList) {
			for(RevCommit c : verComm.getCommits()) {
				if(c.equals(commit)) {
					return verComm.getVersion();
				}
				
			}
			
		}
		return null;
	}
	
	private List<RevCommit> getCommitForTicket(Ticket ticket, List<VersionCommits> commitsForVers){
		List<RevCommit> listCom = new ArrayList<>();
		
		for(VersionCommits commitsOfVer: commitsForVers) {
			List<RevCommit> commitsList = commitsOfVer.getCommits();
			for(RevCommit commit : commitsList) {
				String comment = commit.getFullMessage();	
				//PROVA POI CON comment.matches(".*\\b" + ticket.getKey() + "\\b.*"
				if((comment.contains(ticket.getKey() + ":") || comment.contains(ticket.getKey() + "]") || comment.contains(ticket.getKey() + " ") || comment.contains("/"+ticket.getKey())) && !listCom.contains(commit)) {	
				
					listCom.add(commit);
				}				
							
			}
		
		}
	
		return listCom;
	}
	
	private void setBuggyness(List<JavaClass> javaClasses, String singleClass, Version versionOfComm, Version iV) {
		
		for(JavaClass jClass : javaClasses) {

			if(jClass.getVersion().getIndex() >= iV.getIndex() && jClass.getName().equals(singleClass) && jClass.getVersion().getIndex() < versionOfComm.getIndex()) {
				jClass.setBuggy(true);
				
			}
			
		}
	}

	public List<JavaClass> createClassesIstances(List<VersionCommits> gitList, List<Ticket> tickList ) throws IOException {
		List<JavaClass> javaClasses = new ArrayList<>();
		
		for(VersionCommits verComm : gitList) {
			for(String name : verComm.getJavaClasses()) {
				String content = getContentOfClass(verComm, name);
				JavaClass jC = new JavaClass(name,verComm.getVersion(),content);
				javaClasses.add(jC);
				
			}
			
		}
		int contaTicketConCommit = 0;
		for(Ticket tick: tickList) {
			List<RevCommit> commForTick = getCommitForTicket(tick,gitList);
			contaTicketConCommit+=commForTick.size();
			for( RevCommit commit: commForTick) {
				Version versionOfCom = getVersionOfCommit(commit,gitList);
				
				if(versionOfCom!=null) {		
					List<String> modifiedClasses = getModifiedClasses(commit);
					
					for(String modifClass : modifiedClasses) {
						setBuggyness(javaClasses, modifClass, versionOfCom, tick.getIv());
					
					}
					
				}

			}
		}
		String mex = "IN TOTALE CI SONO QUESTI COMMIT CON I TICKET: "+contaTicketConCommit;
		logger.info(mex);
		return javaClasses;
		
	}
	
	private String getContentOfClass(VersionCommits verCom, String className) throws  IOException {
		String content ="";
	
		TreeWalk treeW = new TreeWalk(repo);
		
		RevCommit commit = verCom.getLastCommit();
		ObjectId treeId = commit.getTree().getId();
		
		treeW.reset(treeId);			
		treeW.setRecursive(true);
				
		while(treeW.next()) {
			if(treeW.isSubtree()) {
				treeW.enterSubtree();
			}else {
				if(!treeW.getPathString().contains(test) && treeW.getPathString().contains(pointJava) ) {
					String path = treeW.getPathString();
					
					if(path.equals(className)) {
						content = new String(repo.open(treeW.getObjectId(0)).getBytes(), StandardCharsets.UTF_8);
					}
				}
			}
		}		
		treeW.close();
		
		return content;
	}
	
	private List<String> getModifiedClasses(RevCommit commit) throws IOException {
		
		List<String> modifiedClasses = new ArrayList<>();	
		
		try(DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
			ObjectReader reader = this.repo.newObjectReader()) {			
						
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			ObjectId newTree = commit.getTree();
			newTreeIter.reset(reader, newTree);
		
			RevCommit commitParent = commit.getParent(0);	
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			ObjectId oldTree = commitParent.getTree();
			oldTreeIter.reset(reader, oldTree);
	
			diffFormatter.setRepository(this.repo);
			List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
		
			
			for(DiffEntry entry : entries) {
				
				if(entry.getNewPath().contains(pointJava) && !entry.getNewPath().contains(test)) {
					modifiedClasses.add(entry.getNewPath());
				}
			
			}
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//NOTHING
			
		}
		
		return modifiedClasses;
		
	}
	
	public static void updateClassCommits(List<JavaClass> javaClasses, String className, Version versionRel, RevCommit commit) {
		
		for(JavaClass jC : javaClasses) {
			
			if(jC.getName().equals(className) && jC.getVersion().getIndex() == versionRel.getIndex() && !jC.getCommits().contains(commit)) {
				jC.getCommits().add(commit);
				
			}
			
		}
		
	}
	
}
