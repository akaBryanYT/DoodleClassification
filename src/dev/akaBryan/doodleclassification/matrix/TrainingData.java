package dev.akaBryan.doodleclassification.matrix;

import java.util.Random;

public class TrainingData {

	public double[] inputs;
	public double[] targets;
	
	public static Random random = new Random();
	
	public TrainingData(double[] inputs, double[] targets) {
		this.inputs = new double[inputs.length];
		this.targets = new double[targets.length];
		
		for(int i=0; i<inputs.length; i++) {
			this.inputs[i] = inputs[i];
		}
		
		for(int i=0; i<targets.length; i++) {
			this.targets[i] = targets[i];
		}	
	}
	
	public double[]	getInputs() {
		return inputs;
	}
	
	public double[]	getTargets() {
		return targets;
	}
	
	public void	setInputs(double[] inputs) {
		this.inputs = new double[inputs.length];
		
		for(int i=0; i<inputs.length; i++) {
			this.inputs[i] = inputs[i];
		}
	}
	
	public void setTargets(double[] targets) {
		this.targets = new double[targets.length];
		
		for(int i=0; i<targets.length; i++) {
			this.targets[i] = targets[i];
		}
	}
	
	public static TrainingData randomChoice(TrainingData[] array) {
		return array[random.nextInt(array.length)];
	}
	
}
