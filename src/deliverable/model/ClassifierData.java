package deliverable.model;

public class ClassifierData {
	
	private String projName;
	private int iteration;
	private String classifierName;
	private boolean featureSel;
	private boolean sampling;
	private boolean costSensitive;
	private double trainingPerc;
	private double precision;
	private double recall;
	private double auc;
	private double kappa;
	private double tp;
	private double fp;
	private double tn;
	private double fn;
	
	public ClassifierData(String projName, int iteration, String classifierName, boolean featureSel, boolean sampling, boolean costSensitive) {
		this.projName = projName;
		this.iteration = iteration;
		this.classifierName = classifierName;
		this.featureSel = featureSel;
		this.sampling = sampling;
		this.costSensitive = costSensitive;
		
		this.trainingPerc = 0.0;
		this.precision = 0;
		this.recall = 0;
		this.auc = 0;
		this.kappa = 0;
		this.tp = 0;
		this.fp = 0;
		this.tn = 0;
		this.fn = 0;
		
	}

	public String getProjName() {
		return projName;
	}

	public void setProjName(String projName) {
		this.projName = projName;
	}

	public int getIteration() {
		return iteration;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public String getClassifierName() {
		return classifierName;
	}

	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}

	public boolean isFeatureSel() {
		return featureSel;
	}

	public void setFeatureSel(boolean featureSel) {
		this.featureSel = featureSel;
	}

	public boolean isSampling() {
		return sampling;
	}

	public void setSampling(boolean sampling) {
		this.sampling = sampling;
	}

	public boolean isCostSensitive() {
		return costSensitive;
	}

	public void setCostSensitive(boolean costSensitive) {
		this.costSensitive = costSensitive;
	}

	public double getTrainingPerc() {
		return trainingPerc;
	}

	public void setTrainingPerc(double trainingPerc) {
		this.trainingPerc = trainingPerc;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public double getAuc() {
		return auc;
	}

	public void setAuc(double auc) {
		this.auc = auc;
	}

	public double getKappa() {
		return kappa;
	}

	public void setKappa(double kappa) {
		this.kappa = kappa;
	}

	public double getTp() {
		return tp;
	}

	public void setTp(double tp) {
		this.tp = tp;
	}

	public double getFp() {
		return fp;
	}

	public void setFp(double fp) {
		this.fp = fp;
	}

	public double getTn() {
		return tn;
	}

	public void setTn(double tn) {
		this.tn = tn;
	}

	public double getFn() {
		return fn;
	}

	public void setFn(double fn) {
		this.fn = fn;
	}
	
	
}
