package com.util.solution.impl;

import com.util.solution.inter.Solution;

public class KPSolution extends Solution{
	/**
	 * the number of items
	 */
	public int item_num;
	/**
	 * the array of items
	 */
	public int [] item_array;
	/**
	 * current weight for knapsack
	 */
	public int [] current_weight;
	
	public KPSolution(int obj_num, int item_num, int type) {
		super(obj_num, type);
		this.item_array = new int[item_num];
		this.current_weight = new int[obj_num];
	}

	@Override
	public KPSolution clone() throws CloneNotSupportedException {
		KPSolution s = new KPSolution(this.obj_num, this.item_num, this.type);
		s.object_val = this.object_val.clone();
		s.item_array = this.item_array.clone();
		s.current_weight = this.current_weight.clone();
		s.index = this.index;
		return s;
	}
	
}
