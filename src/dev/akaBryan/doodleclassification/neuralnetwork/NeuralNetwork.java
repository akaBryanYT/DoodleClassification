package dev.akaBryan.doodleclassification.neuralnetwork;

import dev.akaBryan.doodleclassification.matrix.Matrix;

public class NeuralNetwork {
	
	public int input_nodes;
	public int hidden_nodes;
	public int output_nodes;
	
	public double learningRate;
	
	public Matrix weights_ih;
	public Matrix weights_ho;
	public Matrix biases_h;
	public Matrix biases_o;

	public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes) {
		this.input_nodes = inputNodes;
		this.hidden_nodes = hiddenNodes;
		this.output_nodes = outputNodes;
		
		weights_ih = new Matrix(this.hidden_nodes, this.input_nodes);
		weights_ho = new Matrix(this.output_nodes, this.hidden_nodes);
		weights_ih.randomize();
		weights_ho.randomize();
		
		biases_h = new Matrix(this.hidden_nodes, 1);
		biases_o = new Matrix(this.output_nodes, 1);
		biases_h.randomize();
		biases_o.randomize();
		this.learningRate = 0.01;
	}
	
	public double[] predict(double[] inputArray) {
		
		//Generating the Hidden Outputs
		Matrix inputs = Matrix.fromArray(inputArray);
		Matrix hidden = Matrix.multiply(this.weights_ih, inputs);
		hidden.add(this.biases_h);
		//activation function
		hidden.map(x->sigmoid(x));
		
		//Generating final output for output
		Matrix outputs = Matrix.multiply(this.weights_ho, hidden);
		outputs.add(this.biases_o);
		outputs.map(x->sigmoid(x));
		
		//return output as array
		return outputs.toArray();
	}
	
	public void train(double[] inputArray, double[] targetArray) {
		
		//Generating the Hidden Outputs
		Matrix inputs = Matrix.fromArray(inputArray);
		Matrix hidden = Matrix.multiply(this.weights_ih, inputs);
		hidden.add(this.biases_h);
		//activation function
		hidden.map(x->sigmoid(x));
		
		//Generating final output for output
		Matrix outputs = Matrix.multiply(this.weights_ho, hidden);
		outputs.add(this.biases_o);
		outputs.map(x->sigmoid(x));
		
		//convert array to matrix
		Matrix targets = Matrix.fromArray(targetArray);
		
		//calculate error
		//error = targets-outputs
		Matrix output_errors = Matrix.subtract(targets, outputs);
		
		//calculate gradient
		Matrix gradients = Matrix.map(outputs, x->dSigmoid(x));
		gradients.hadamard(output_errors);
		gradients.multiply(this.learningRate);
		
		//calculate deltas
		Matrix hidden_T = Matrix.transpose(hidden);
		Matrix weights_ho_deltas = Matrix.multiply(gradients, hidden_T);
		
		//calculate hidden layer errors
		Matrix who_t = Matrix.transpose(this.weights_ho);
		Matrix hidden_errors = Matrix.multiply(who_t, output_errors);
		
		Matrix hidden_gradients = Matrix.map(hidden, (x)->dSigmoid(x));
		hidden_gradients.hadamard(hidden_errors);
		hidden_gradients.multiply(this.learningRate);
		
		//calculate hiddenDeltas
		Matrix inputs_T = Matrix.transpose(inputs);
		Matrix weights_ih_deltas = Matrix.multiply(hidden_gradients, inputs_T);
		
		//adjust weights by deltas
		this.weights_ho.add(weights_ho_deltas);
		//adjust bias by its deltas (gradients)
		this.biases_o.add(gradients);
		
		//adjust hidden weights by hiddenDeltas
		weights_ih.add(weights_ih_deltas);
		//adjust hidden bias by its deltas (hidden gradients)
		biases_h.add(hidden_gradients);
		
//		targets.printMatrix();
//		outputs.printMatrix();
//		outputErrors.printMatrix();	
	}
	
	public void setLearningRate(double lr) {
		this.learningRate = lr;
	}
	
	public Double sigmoid(Double x) {
		return 1/(1+Math.exp(-x));
	}
	
	public Double dSigmoid(Double x) {
		return x*(1-x);
	}
	
}