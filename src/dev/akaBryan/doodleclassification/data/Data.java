package dev.akaBryan.doodleclassification.data;

public class Data {

	public byte[] fullData;
	public DataObject[] trainingObjects;
	public DataObject[] testingObjects;
	
	public Data() {
		
	}
	
	public Data(byte[] fullData, DataObject[] training, DataObject[] testing, int label) {
		this.fullData=fullData;
		this.trainingObjects=training;
		this.testingObjects=testing;
	}
	
	public byte[] getFullData() {
		return this.fullData;
	}
	
	public DataObject[] getTrainingDataObjects() {
		return this.trainingObjects;
	}

	public DataObject[] getTestingDataObjects() {
		return this.testingObjects;
	}
	
	public void setData(byte[] fullData) {
		this.fullData=fullData;
	}
	
	public void setTraining(DataObject[] trainingObjects) {
		this.trainingObjects=trainingObjects;
	}

	public void setTesting(DataObject[] testingObjects) {
		this.testingObjects=testingObjects;
	}
	
}
