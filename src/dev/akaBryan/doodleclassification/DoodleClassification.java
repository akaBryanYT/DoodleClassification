package dev.akaBryan.doodleclassification;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import dev.akaBryan.doodleclassification.data.Data;
import dev.akaBryan.doodleclassification.data.DataObject;
import dev.akaBryan.doodleclassification.display.Display;
import dev.akaBryan.doodleclassification.neuralnetwork.NeuralNetwork;

public class DoodleClassification implements Runnable, MouseListener,MouseMotionListener, ActionListener{
	private Display display;
	public String title;
	public int width, height;
	
	public final int len = 784;
	public final int maxSets;
	public final int trainingSets;
	
	public final int BEE = 0;
	public final int DOG = 1;
	public final int FORK = 2;
	public final int RAINBOW = 3;
	public final int TRAIN = 4;
	
	public NeuralNetwork nn;
	public boolean canClear;
	
	public Data bees;
	public Data dogs;
	public Data forks;
	public Data rainbows;
	public Data trains;
	public int epochs;
	
	public Data brainData;
	
	public byte[] beesData;
	public byte[] dogsData;
	public byte[] forksData;
	public byte[] rainbowsData;
	public byte[] trainsData;
	
	public int epochCounter;
	
	public ArrayList<ArrayList<Point>> points;
	
	public Random random = new Random();
	
	private Thread thread;
	private boolean running = false;
	private boolean isTraining;
	private boolean isCancelled;
	
	private BufferStrategy bs;
	private Graphics g;
	private Graphics2D g2d;
	private BasicStroke stroke;
	
	public DoodleClassification(String title, int width, int height, int maxSets, int trainingSets, int epochs) {
		this.title=title;
		this.width=width;
		this.height=height;
		
		this.epochs=epochs;
		
		this.maxSets = maxSets;
		this.trainingSets = trainingSets;
	}
	
	private void preLoad() {
		beesData = ByteLoader.loadBytes("/doodleData/bee100000.bin");
		dogsData = ByteLoader.loadBytes("/doodleData/dog100000.bin");
		forksData = ByteLoader.loadBytes("/doodleData/fork100000.bin");
		rainbowsData = ByteLoader.loadBytes("/doodleData/rainbow100000.bin");
		trainsData = ByteLoader.loadBytes("/doodleData/train100000.bin");
	}
	
	private void init() {
		
		points = new ArrayList<>();
		points.add(new ArrayList<>());
		
		bees = new Data();
		dogs = new Data();
		forks = new Data();
		rainbows = new Data();
		trains = new Data();
		prepareData(bees, beesData, maxSets, trainingSets, BEE);
		prepareData(dogs, dogsData, maxSets, trainingSets, DOG);
		prepareData(forks, forksData, maxSets, trainingSets, FORK);
		prepareData(rainbows, rainbowsData, maxSets, trainingSets, RAINBOW);
		prepareData(trains, trainsData, maxSets, trainingSets, TRAIN);
		
		//make neural network
		nn = new NeuralNetwork(len,84,5);
		
		//configure and randomize training and testing data
		int sumTraining = bees.trainingObjects.length+dogs.trainingObjects.length+
				forks.trainingObjects.length+rainbows.trainingObjects.length+trains.trainingObjects.length;
		
		brainData = new Data();
		brainData.trainingObjects = new DataObject[sumTraining];
		
		prepareTraining(sumTraining, brainData);
		shuffleArray(brainData.trainingObjects, true);
		
		int sumTesting = bees.testingObjects.length+dogs.testingObjects.length+
				forks.testingObjects.length+rainbows.testingObjects.length+trains.testingObjects.length;
		
		brainData.testingObjects = new DataObject[sumTesting];
		prepareTesting(sumTesting, brainData);
		shuffleArray(brainData.testingObjects, true);
		
		display = new Display(title, width, height);
		
		display.getCanvas().addMouseListener(this);
		display.getCanvas().addMouseMotionListener(this);
		display.getTestButton().addActionListener(this);
		display.getTrainButton().addActionListener(this);
		display.getGuessButton().addActionListener(this);
		display.getClearButton().addActionListener(this);
		display.getCancelButton().addActionListener(this);
		
	}
	
	public void tick() {	
		this.width = (int)display.getJFrame().getSize().getWidth();
		this.height = (int)display.getJFrame().getSize().getHeight();
		
	}
	
	public void render() {
		bs = display.getCanvas().getBufferStrategy();
		
		if(bs==null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		
		g = bs.getDrawGraphics();
		g2d = (Graphics2D) g;
		//clear screen
		stroke = new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g2d.setStroke(stroke);
		g2d.clearRect(0, 0, width, height);
		//draw
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, width, height);
		
		g2d.setColor(Color.black);

		drawLinesFromPoints(g2d);
//		int total = 100;
//		for(int n =0; n<total; n++) {
//			BufferedImage img = new BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB);
//			int[] temp = new int[len];
//			int offset = (n*len);
//			for(int i=0; i<len; i++) {
//				temp[i] = (255-forks.trainingObjects[n].training[i])*0x00010101;
//				img.setRGB(i%28, i/28, temp[i]);
//			}
//			
//			int x = 28*(n%10);
//			int y = 28*(n/10);
//			
//			g.drawImage(img, x, y, 28, 28, null);
//		}
		
		//end draw
		
		bs.show();
		g.dispose();
		g2d.dispose();
	}
	
	@Override
	public void run() {
		
		preLoad();
		init();
		
		int fps = 60;
		double timePerTick = 1000000000/fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		@SuppressWarnings("unused")
		int ticks = 0;
		
		while(running) {
			now = System.nanoTime();
			delta += (now - lastTime)/timePerTick;
			timer+= now-lastTime;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				render();
				ticks++;
				delta--;
			}
			if(timer>= 1000000000) {
				//System.out.println("Ticks and Frames: " + ticks);
				ticks = 0;
				timer = 0;
			}
		}
		stop();
	}
	
	public synchronized void start() {
		if(running) {
			return;
		}
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		if(!running) {
			return;
		}
		
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	public double testAll(Data training) {
		
		double correct=0;
		for(int i=0; i<training.testingObjects.length;i++) {
			double[] inputs = new double[len];
			DataObject data = training.testingObjects[i];
			for (int j=0; j<data.testing.length; j++) {
				inputs[j] = (data.testing[j]&0xff)/255.0;
			}
			int label = training.testingObjects[i].testingLabel;
			double[] guess = nn.predict(inputs);
			
			double m = max(guess);
			double classification = indexOfMax(guess);
//			Matrix.printMatrix(guess);
//			System.out.println(classification);
//			System.out.println(label);
			
			if(classification==label) {
				correct++;
			}
			
			
		}
		double percentage = 100*correct/training.testingObjects.length;
		return percentage;
	}
	
	public void trainEpoch(Data training) {
		for(int curr = 0; curr<epochs; curr++) {
			if(isCancelled) {
				isCancelled=false;
				break;
			}
			
			
			if(curr>=0 && curr<=4) {
				nn.setLearningRate(0.02);
			}else if(curr>=5 && curr<=8) {
				nn.setLearningRate(0.01);
			}else if(curr>=9) {
				nn.setLearningRate(0.005);
			}
			
			String status = "Training: Epoch "+(curr+1)+"\n";
			display.print(status);
			
			shuffleArray(training.trainingObjects, true);
			//Train for one 
			for(int i=0; i<training.trainingObjects.length;i++) {
				double[] inputs = new double[len];
				DataObject data = training.trainingObjects[i];
				for (int j=0; j<data.training.length; j++) {
					inputs[j] = (data.training[j]&0xFF)/255.0;
				}
				int label = training.trainingObjects[i].trainingLabel;
				double[] targets = {0,0,0,0,0};
				targets[label] = 1;
	//			System.out.println(label);
	//			Matrix.printMatrix(inputs);
	//			Matrix.printMatrix(targets);
				nn.train(inputs, targets);
			}
		}
		display.getJTextArea().append("Done training!\n");		
	}
	
	public void prepareTesting(int sum, Data training) {
		for(int n=0; n<5;n++) {
			for(int i=0; i<sum/5; i++) {
				if(n==0) {
					training.testingObjects[i+(n*sum/5)]=bees.testingObjects[i];
				}else if(n==1) {
					training.testingObjects[i+(n*sum/5)]=dogs.testingObjects[i];
				}else if(n==2) {
					training.testingObjects[i+(n*sum/5)]=forks.testingObjects[i];
				}else if(n==3) {
					training.testingObjects[i+(n*sum/5)]=rainbows.testingObjects[i];
				}else if(n==4){
					training.testingObjects[i+(n*sum/5)]=trains.testingObjects[i];
				}
			}
		}
	}
	
	public void prepareTraining(int sum, Data training) {
		for(int n=0; n<5;n++) {
			for(int i=0; i<sum/5; i++) {
				if(n==0) {
					training.trainingObjects[i]=bees.trainingObjects[i];
				}else if(n==1) {
					training.trainingObjects[i+(n*sum/5)]=dogs.trainingObjects[i];
				}else if(n==2) {
					training.trainingObjects[i+(n*sum/5)]=forks.trainingObjects[i];
				}else if(n==3) {
					training.trainingObjects[i+(n*sum/5)]=rainbows.trainingObjects[i];
				}else if(n==4){
					training.trainingObjects[i+(n*sum/5)]=trains.trainingObjects[i];
				}
			}
		}
	}
	
	public void prepareData(Data data, byte[] fullData, int maxSets, int trainingSets, int label) {
		int sets = maxSets;
		int threshold = trainingSets;
		
		data.trainingObjects = new DataObject[threshold];
		data.testingObjects = new DataObject[sets-threshold];
		data.fullData = fullData;
		
		for(int i=0; i<sets; i++) {
			int offset = i*len;
			if(i<threshold) {
				data.trainingObjects[i] = new DataObject();
				data.trainingObjects[i].training=new byte[len];
				data.trainingObjects[i].training=subArray(data.fullData, offset, offset+len);	
				data.trainingObjects[i].trainingLabel = label;
			}else {
				data.testingObjects[i-threshold] = new DataObject();
				data.testingObjects[i-threshold].testing=new byte[len];
				data.testingObjects[i-threshold].testing=subArray(data.fullData, offset, offset+len);
				data.testingObjects[i-threshold].testingLabel = label;
			}
		}
	}
	
	public byte[] subArray(byte[] bytes, int start, int end){
		int set = end-start;
		byte[] result = new byte[set];
		for(int i=0; i<set; i++) {
			result[i] = bytes[start+i];
		}
		return result;
	}
	
	public static DataObject[] shuffleArray(DataObject[] array) {
		DataObject[] result = new DataObject[array.length];
		for(int i=0; i<array.length; i++) {
			result[i] = array[i];
		}
		Random random = ThreadLocalRandom.current();
		for(int i=result.length-1;i>0;i--) {
			int index = random.nextInt(i+1);
			DataObject a = result[index];
			result[index]=result[i];
			result[i]=a;
		}	
		
		return result;
	}
	
	public static void shuffleArray(DataObject[] array, boolean replace) {
		if(!replace) {
			System.out.println("Do not use replace parameter if you do not set it to true.");
			return;
		}else {
			Random random = ThreadLocalRandom.current();
			for(int i=array.length-1;i>0;i--) {
				int index = random.nextInt(i+1);
				DataObject a = array[index];
				array[index]=array[i];
				array[i]=a;
			}
		}
	}
	
	public double indexOfMax(double[] array) {
		double max = 0;
		double maxIndex = 0;
		double temp = 0;
		int numChange = 0;
		for(int i=0; i<array.length; i++) {
			temp = array[i];
			if(max<temp) {
				numChange++;
				maxIndex=i;
				max=temp;
			}
		}
		if(numChange==0) {
			return -1;
		}
		return maxIndex;
	}
	
	@SuppressWarnings("unused")
	public double max(double[] array) {
		double max = 0;
		double maxIndex = 0;
		double temp = 0;
		int numChange = 0;
		for(int i=0; i<array.length; i++) {
			temp = array[i];
			if(max<temp) {
				numChange++;
				maxIndex=i;
				max=temp;
			}
		}
		if(numChange==0) {
			return -1;
		}
		return max;
	}
	
	@SuppressWarnings("unused")
	public int max(int[] array) {
		int max = 0;
		int maxIndex = 0;
		int temp = 0;
		int numChange = 0;
		for(int i=0; i<array.length; i++) {
			temp = array[i];
			if(max>=temp) {
				numChange++;
				maxIndex=i;
				max=temp;
			}
		}
		return max;
	}
	
	public void drawLinesFromPoints(Graphics2D g2d) {
		
		canClear = false;

		for(int i =0; i<points.size(); i++) {
			if(points.get(i).size()>=2) {
				int lastIndex = points.get(i).size()-1;
				for(int k = 0; k<lastIndex; k++) {
					Point p1 = points.get(i).get(k);
					Point p2 = points.get(i).get(k+1);
					g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
				}
			}else if(points.get(i).size()==1) {
				Point point = points.get(i).get(0);
				g2d.drawLine((int)point.getX(), (int)point.getY(), (int)point.getX(), (int)point.getY());
			}
		}
		
		canClear = true;
	}
	
	public Point getOnScreenPointer() {
		
		int x = (int)MouseInfo.getPointerInfo().getLocation().getX()-(int)display.getCanvas().getLocationOnScreen().getX();
		int y = (int)MouseInfo.getPointerInfo().getLocation().getY()-(int)display.getCanvas().getLocationOnScreen().getY();
		Point p = new Point(x,y);
		return p;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		points.get(points.size()-1).add(getOnScreenPointer());
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {			
		points.get(points.size()-1).add(getOnScreenPointer());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		points.add(new ArrayList<Point>());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
	public void getImage() {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==display.getCancelButton()) {
			display.getJTextArea().append("Cancelling...(waiting for current epoch to finish)\n");
			isCancelled = true;
		}
		
		if(e.getSource()==display.getTrainButton()) {
			if(!isTraining) {
				Thread workThread = new Thread() {
					public void run() {
						isTraining = true;
						trainEpoch(brainData);	
						isTraining = false;
					}
				};
				
				workThread.start();	
			}else {
				display.getJTextArea().append("You cannot train again while training.\n");
			}
			
		}else if(e.getSource()==display.getTestButton()) {
			Thread workThread = new Thread() {
				public void run() {
					
					if(!isTraining) {
						display.getJTextArea().append("Testing...\n");
						double percent = testAll(brainData);
						display.getJTextArea().append("% Correct: " + percent+"%\n");	
					}else {
						display.getJTextArea().append("\nYou cannot test while training. \n");
					}
					
				}
			};
			
			workThread.start();
			
		}else if(e.getSource()==display.getClearButton()) {
			boolean running = true;
			while(running) {
				if(canClear) {
					points =new ArrayList<ArrayList<Point>>();
					points.add(new ArrayList<Point>());
					running=false;
				}
			}
			
		}else if(e.getSource()==display.getGuessButton()) {
			if(!isTraining) {	
				double[] inputs = new double[784];
				BufferedImage image = new BufferedImage(display.getCanvas().getWidth(), display.getCanvas().getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = image.createGraphics();
				graphics.setStroke(stroke);
				
				drawLinesFromPoints(graphics);
				
				image = resizeImage(image, 28, 28);
				
				File outputfile = new File("image.png");
				try {
					ImageIO.write(image, "jpg", outputfile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				int count = 0;
				
				for (int j=0; j<image.getHeight(); j++) {
					for (int i=0; i<image.getWidth(); i++) {
						double bright = (image.getRGB(i, j)&0xFF)/255.0;
						inputs[count] = bright;
						count++;
					}
				}
				double[] guess = nn.predict(inputs);
				
				double classification = indexOfMax(guess);
				if(classification==BEE) {
					display.getJTextArea().append("I guess bee!\n");
				}else if(classification==DOG) {
					display.getJTextArea().append("I guess dog!\n");
				}else if(classification==FORK) {
					display.getJTextArea().append("I guess fork!\n");
				}else if(classification==RAINBOW) {
					display.getJTextArea().append("I guess rainbow!\n");
				}else if(classification==TRAIN) {
					display.getJTextArea().append("I guess train!\n");
				}
			}else {
				display.getJTextArea().append("\nYou cannot guess while training. \n");
			}
		}
		
	}
	
	public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight){
	    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
	    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
	    return outputImage;
	}


}
