package dev.akaBryan.doodleclassification.matrix;

import java.util.function.Function;

public class Matrix {

	public int rows;
	public int cols;
	public double[][] data;
	
	public Matrix(int rows, int cols) {
		
		this.rows = rows;
		this.cols = cols;
		this.data = new double[rows][cols];
		
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				data[i][j] =0;
			}
		}	
	}
	
	
	public static Matrix fromArray(double[] array) {
		Matrix m = new Matrix(array.length, 1);
		for(int i=0; i<array.length; i++) {
			m.data[i][0] = array[i];
		}
		return m;
	}
	
	public static Matrix subtract(Matrix a, Matrix b) {
		//return new matrix a-b
		Matrix output = new Matrix(a.rows, a.cols);
		for (int i = 0; i < a.rows; i++) {
			for (int j = 0; j < a.cols; j++) {
				output.data[i][j] = a.data[i][j] - b.data[i][j];
			}
		}	
		return output;
	}
	
	
	public double[] toArray() {
		double[] array = new double[this.data.length*this.data[0].length];
		int count = 0;
		for (int i = 0; i < this.data.length; i++) {
			for (int j = 0; j < this.data[i].length; j++) {
				array[count] = this.data[i][j];
				count++;
			}
		}
		return array;	
	}
	
	public void randomize() {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				data[i][j] = Math.random()*2 -1;
			}
		}	
	}
	
	public void add(Matrix n) {
		
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				data[i][j] += n.data[i][j];
			}
		}	
	}
	
	public void add(double n) {
		
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				data[i][j] += n;
			}
		}	
	}
	
	public static Matrix transpose(Matrix matrix){
		Matrix result = new Matrix(matrix.cols, matrix.rows);
		
		for (int i = 0; i < matrix.rows; i++) {
			for (int j = 0; j < matrix.cols; j++) {
				result.data[j][i] = matrix.data[i][j];
			}
		}	
		
		return result;
	}
	
	public static Matrix multiply(Matrix m1, Matrix m2) {
		if(m1.cols!=m2.rows) {
			System.out.println("Columns of A must match Rows of B");
			return null;
		}
		
		Matrix result = new Matrix(m1.rows, m2.cols);
		for(int i=0; i<result.rows; i++) {
			for(int j=0; j<result.cols; j++) {
				double sum = 0;
				for(int k = 0; k<m1.cols; k++) {
					sum += m1.data[i][k]*m2.data[k][j];
				}
				result.data[i][j] = sum;
			}
		}
		return result;
	}
	
	public void hadamard(Matrix n) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				data[i][j] *= n.data[i][j];
			}
		}	
	}
	
	public void multiply(double m2) {
		
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				data[i][j] *= m2;
			}
		}	
	}
	
	public void map(Function<Double, Double> myMethod) {
		for(int i=0;i<this.rows;i++){
			for(int j=0;j<this.cols;j++) {
				double val = this.data[i][j];
				this.data[i][j] = myMethod.apply(val);
			}
		}	
	}
	
	public static Matrix map(Matrix matrix, Function<Double, Double> myMethod) {
		Matrix result = new Matrix(matrix.rows, matrix.cols);
		for(int i=0;i<matrix.rows;i++){
			for(int j=0;j<matrix.cols;j++) {
				double val = matrix.data[i][j];
				result.data[i][j] = myMethod.apply(val);
			}
		}	
		
		return result;
	}
	
	public void print(){
		
		for(int i=0;i<=this.rows;i++){
			for(int j=0;j<=this.cols;j++) {
				if(i==0 && j==0) {
					System.out.print("index     ");
				}else if(i==0) {
					System.out.print(j-1+"        ");
				}else if(j==0){
					System.out.print(i-1+"        ");
				}else {
					System.out.print(this.data[i-1][j-1]);
					System.out.print("      ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void printMatrix(double[] matrix){
		
		for(int i=0;i<=1;i++){
			for(int j=0;j<=matrix.length;j++) {
				if(i==0&&j==0) {
					System.out.print("index   ");
				}else if(i==0) {
					System.out.print("       "+(j-1)+"                 ");
				}else if(i==1&&j==0) {
					System.out.print("element ");
				}else {
					System.out.print(matrix[j-1]+"     ");
				}
			}
			System.out.println();
		}
		System.out.println();
		
	}
	
	public static void printMatrix(double[][] matrix){
		
		for(int i=0;i<=matrix.length;i++){
			for(int j=0;j<=matrix[0].length;j++) {
				if(i==0 && j==0) {
					System.out.print("index     ");
				}else if(i==0) {
					System.out.print(j-1+"        ");
				}else if(j==0){
					System.out.print(i-1+"        ");
				}else {
					System.out.print(matrix[i-1][j-1]);
					System.out.print("      ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}
