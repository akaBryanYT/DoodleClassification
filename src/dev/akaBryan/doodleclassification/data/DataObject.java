package dev.akaBryan.doodleclassification.data;

public class DataObject {

	public byte[] training;
	public byte[] testing;
	public int trainingLabel;
	public int testingLabel;
	
	public DataObject() {
		
	}
	
	public DataObject(byte[] training, byte[] testing, int label) {
		this.training=training;
		this.testing=testing;
		this.trainingLabel=label;
	}
	
	
	//getters
	public byte[] getTraining() {
		return this.training;
	}

	public byte[] getTesting() {
		return this.testing;
	}
	
	public int getTrainingLabel() {
		return this.trainingLabel;
	}
	
	public int getTestingLabel() {
		return this.testingLabel;
	}
	
	
	//setters
	public void setTraining(byte[] training) {
		this.training=training;
	}

	public void setTesting(byte[] testing) {
		this.testing=testing;
	}
	
	public void setTestingLabel(int testingLabel) {
		this.testingLabel=testingLabel;
	}
	
	public void setTrainingLabel(int testingLabel) {
		this.testingLabel=testingLabel;
	}
	
}
