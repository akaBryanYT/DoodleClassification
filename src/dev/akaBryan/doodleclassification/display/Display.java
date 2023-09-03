package dev.akaBryan.doodleclassification.display;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Display{
	
	private JFrame frame;
	private Canvas canvas;
	public JPanel panel;
	public JPanel textPanel;
	
	public JButton testButton;
	public JButton trainButton;
	public JButton guessButton;
	public JButton clearButton;
	public JButton cancelButton;
	
	public JTextArea textArea;
	
	private String title;
	private int width, height;
	
	
	public Display(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
		
		this.createDisplay();
	}
	
	public void createDisplay() {
		frame = new JFrame();
		frame.setTitle(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setLayout(new BorderLayout(10, 0));
		
		trainButton = new JButton("train");
		testButton = new JButton("test");
		guessButton = new JButton("guess");
		clearButton = new JButton("clear");
		cancelButton = new JButton("cancel");
		
		textArea = new JTextArea("", 30, 46);
		textArea.setEditable(false);
		
		textPanel = new JPanel();
		textPanel.setPreferredSize(new Dimension(width, height));
		textPanel.add(textArea);
		
		panel = new JPanel(new GridLayout(1, 4));
		panel.setPreferredSize(new Dimension(width,height/5));
		
		trainButton.setSize(new Dimension(width/3, height/10));
		testButton.setSize(new Dimension(width/3, height/10));
		guessButton.setSize(new Dimension(width/3, height/10));
		clearButton.setSize(new Dimension(width/3, height/10));
		
		panel.add(cancelButton);
		panel.add(trainButton);
		panel.add(testButton);
		panel.add(guessButton);
		panel.add(clearButton);
		
		canvas = new Canvas();
		canvas.setPreferredSize(new	Dimension(width, height));
		
		frame.add(panel, BorderLayout.NORTH);
		frame.add(canvas, BorderLayout.CENTER);
		frame.add(textPanel, BorderLayout.EAST);
		
		frame.pack();
	}
	
	public void print(String s) {
		textArea.append(s);
		
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public JFrame getJFrame() {
		return frame;
	}
	
	public JPanel getJPanel() {
		return panel;
	}
	
	public JButton getTestButton() {
		return testButton;
	}
	
	public JButton getTrainButton() {
		return trainButton;
	}
	
	public JButton getGuessButton() {
		return guessButton;
	}
	
	public JButton getClearButton() {
		return clearButton;
	}
	
	public JTextArea getJTextArea() {
		return textArea;
	}
	
	public JButton getCancelButton() {
		return cancelButton;
	}
}
