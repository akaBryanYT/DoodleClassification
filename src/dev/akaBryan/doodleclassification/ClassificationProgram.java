package dev.akaBryan.doodleclassification;

public class ClassificationProgram {

	public static void main(String[] args) {
		
		int width = 500;
		int height = 500;
		int maxSets = 100000;
		int trainingSets = 80000;
		int epochs = 11;
		
		for(int i = 2; i<=10; i++) {
			System.out.println(hiddenCalculator(i, 400000, 784, 5));			
		}
		
		DoodleClassification dc = new DoodleClassification("DoodleClassifier", width, height, maxSets, trainingSets, epochs);

		dc.start();
	}

	public static int hiddenCalculator(int alpha, int trainingSamples, int inputNeurons, int outputNeurons) {

		return trainingSamples/(alpha*(inputNeurons+outputNeurons));
		
	}
	
}
