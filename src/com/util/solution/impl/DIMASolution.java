package com.util.solution.impl;

import com.util.solution.inter.Solution;

public class DIMASolution extends Solution{
	/**
	 * the number of devices
	 */
	public int devNum;
	/**
	 * the number of position
	 */
	public int posNum;
	/**
	 * the sequence of the allocated devices
	 */
	public int [] sequence;
	/**
	 * the resource that has been allocated in each position
	 */
	public int [] allocated;
	
	public DIMASolution(int obj_num, int type) {
		super(obj_num, type);
	}
}
