package com.util.solution.inter;


public abstract class Solution implements Cloneable{
	/**
	 * the number of object
	 */
	protected int obj_num;
	/**
	 * objective value of the solution
	 */
	public int [] object_val;
	/**
	 * 1 for maximum problem
	 * -1 for minimum problem 
	 */
	protected int type;
	/**
	 * the fitness value of aggregation method
	 */
	public double fitness;
	
	/**
	 * if the solution was searched or not
	 */
	private boolean isSearched;
	/**
	 * if the solution was dominated or not
	 */
	private boolean isDominated;
	/**
	 * the index of the current solution
	 */
	public int index;
	/**
	 * the index of the grid system
	 */

	int [] route;
	public int [] gridIndex = new int[3];
	
	public Solution(int obj_num, int type){
		this.obj_num = obj_num;
		this.type = type;
		this.object_val = new int[obj_num];
		this.isSearched = false;
		this.isDominated = false;
	}
	/**
	 * judge if dominating s
	 * @param s
	 * @return
	 */
	public int compareTo(Solution s){
		int count1 = 0;
		int count2 = 0;
		for(int i=0;i<obj_num;i++){
			if(type*this.object_val[i] >= type*s.object_val[i]) count1++;
			if(type*this.object_val[i] <= type*s.object_val[i]) count2++;
		}
		if(count1==obj_num){
			return 1;
		}
		if(count2==obj_num){
			return -1;
		}
		return 0;
	}
	public boolean isSearched() {
		return isSearched;
	}
	public void setSearched(boolean isSearched) {
		this.isSearched = isSearched;
	}
	public int getObj_num() {
		return obj_num;
	}
	public void setObj_num(int obj_num) {
		this.obj_num = obj_num;
	}
	public int[] getObject_val() {
		return object_val;
	}
	public void setObject_val(int[] object_val) {
		this.object_val = object_val;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
//	public void copy(Class<? extends Solution> clazz){
//		try {
//			Class<?> s = Class.forName(clazz.getName());
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
}
