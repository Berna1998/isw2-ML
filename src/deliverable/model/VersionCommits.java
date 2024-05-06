package deliverable.model;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class VersionCommits {
	private Version version;
	private List<RevCommit> commits;
	private RevCommit lastCommit;
	private List<String> javaClasses;
	
	public VersionCommits(Version version,RevCommit lastCommit,List<RevCommit> commits) {
		this.version = version;
		this.lastCommit = lastCommit;
		this.commits = commits;
		this.javaClasses = null;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}


	public List<String> getJavaClasses() {
		return javaClasses;
	}

	public void setJavaClasses(List<String> javaClasses) {
		this.javaClasses = javaClasses;
	}

	public List<RevCommit> getCommits() {
		return commits;
	}

	public void setCommits(List<RevCommit> commits) {
		this.commits = commits;
	}

	public RevCommit getLastCommit() {
		return lastCommit;
	}

	public void setLastCommit(RevCommit lastCommit) {
		this.lastCommit = lastCommit;
	}


	
}
