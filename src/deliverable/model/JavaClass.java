package deliverable.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class JavaClass {
	private String name;
	private List<RevCommit> commits;
	private Version version;
	private boolean buggy;
	
	private double size;
	private int touchLoc;
	private int nr;
	private int nFix;
	private int nAuth;
	private int addedLoc;

	private int maxAddedLoc;
	private double avgAddedLoc;
	private int churn;
	private int maxChurn;
	private double avgChurn;
	private String content;
	
	private List<Integer> addedLinesLis;
	private List<Integer> removedLinesLis;
	
	public JavaClass(String name, Version version, String content) {
		this.name = name;
		this.version = version;
		this.content = content;
		this.commits = new ArrayList<>();
		this.buggy = false;
		
		this.addedLinesLis = new ArrayList<>();
		this.removedLinesLis = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<RevCommit> getCommits() {
		return commits;
	}

	public void setCommits(List<RevCommit> commits) {
		this.commits = commits;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}
	
	public boolean isBuggy() {
		return buggy;
	}

	public void setBuggy(boolean buggy) {
		this.buggy = buggy;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public int getTouchLoc() {
		return touchLoc;
	}

	public void setTouchLoc(int touchLoc) {
		this.touchLoc = touchLoc;
	}

	public int getNr() {
		return nr;
	}

	public void setNr(int nr) {
		this.nr = nr;
	}

	public int getnFix() {
		return nFix;
	}

	public void setnFix(int nFix) {
		this.nFix = nFix;
	}

	public int getnAuth() {
		return nAuth;
	}

	public void setnAuth(int nAuth) {
		this.nAuth = nAuth;
	}

	public int getAddedLoc() {
		return addedLoc;
	}

	public void setAddedLoc(int addedLoc) {
		this.addedLoc = addedLoc;
	}

	public int getMaxAddedLoc() {
		return maxAddedLoc;
	}

	public void setMaxAddedLoc(int maxAddedLoc) {
		this.maxAddedLoc = maxAddedLoc;
	}

	public double getAvgAddedLoc() {
		return avgAddedLoc;
	}

	public void setAvgAddedLoc(double avgAddedLoc) {
		this.avgAddedLoc = avgAddedLoc;
	}

	public int getChurn() {
		return churn;
	}

	public void setChurn(int churn) {
		this.churn = churn;
	}

	public int getMaxChurn() {
		return maxChurn;
	}

	public void setMaxChurn(int maxChurn) {
		this.maxChurn = maxChurn;
	}

	
	public double getAvgChurn() {
		return avgChurn;
	}

	public void setAvgChurn(double avgChurn) {
		this.avgChurn = avgChurn;
	}

	public List<Integer> getAddedLinesLis() {
		return addedLinesLis;
	}

	public void setAddedLinesLis(List<Integer> addedLinesLis) {
		this.addedLinesLis = addedLinesLis;
	}

	public List<Integer> getRemovedLinesLis() {
		return removedLinesLis;
	}

	public void setRemovedLinesLis(List<Integer> removedLinesLis) {
		this.removedLinesLis = removedLinesLis;
	}
	
	
	
}
