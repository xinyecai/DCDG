package com.util.bean;

public class CDGBean {
	/**
	 * the number of solutions
	 */
	public int N;
	/**
	 * the number of grids
	 */
	public int K;
	/**
	 * the ideal point
	 */
	public int [] idealPoint = new int[3];
	/**
	 * the nadir point
	 */
	public int [] nadPoint = new int[3];
	public double sigma;
	/**
	 * the width of each grid
	 */
	public double [] grid_width = new double[5];
	/**
	 * update K and N 
	 * @param max_num
	 */
	public void updateKN(int max_num){
		if(N>=max_num) return;
		K = (int) (K + K/(1+Math.pow(Math.E, (double)(N*1.0/max_num))));
		N = Math.min(2*K - 1, max_num);
	}
	
	public void updateKNForThree(int max_num){
		if(N>=max_num) return;
		K = (int) (K + K/(1+Math.pow(Math.E, (double)(N*1.0/max_num))));
		N = Math.min(3*K*K - 3*K + 1, max_num);
	}
	
	public void setK(int k) {
		this.K = k;
		this.N = 2*k - 1;
	}
	
	public int getN() {
		return N;
	}
	public void setN(int n) {
		N = n;
	}
	public int getK() {
		return K;
	}
	public int[] getIdealPoint() {
		return idealPoint;
	}
	public void setIdealPoint(int[] idealPoint) {
		this.idealPoint = idealPoint;
	}
	public int[] getNadPoint() {
		return nadPoint;
	}
	public void setNadPoint(int[] nadPoint) {
		this.nadPoint = nadPoint;
	}
	public double getSigma() {
		return sigma;
	}
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}
	public double[] getGrid_width() {
		return grid_width;
	}
	public void setGrid_width(double[] grid_width) {
		this.grid_width = grid_width;
	}
}
