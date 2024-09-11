package main.java.ml.model;

public class Acume {
	
	private int id;
	private double size;
	private double prob;
	private String actual;
	private String nameFile;
	
	public Acume(int id, double size, double prob, String actual, ClassifierData classData) {
		this.id = id;
		this.size = size;
		this.prob = prob;
		this.actual = actual;
		
		if(classData.isFeatureSel()) {
			if(!classData.getSampling().equals("No")) {
				this.nameFile = classData.getClassifierName()+"_FeatureSelection_"+classData.getSampling();
			}else if(classData.getSampling().equals("No") && classData.isCostSensitive()){
				this.nameFile = classData.getClassifierName()+"_FeatureSelection_CostSensitive";
			}else {
				this.nameFile = classData.getClassifierName()+"_FeatureSelection";
			}
				
		}else {
			this.nameFile = classData.getClassifierName();
		}

		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	

}
