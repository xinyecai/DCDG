package com.util.solution.impl;

import com.util.solution.inter.Solution;

public class QAPSolution extends Solution{
	/**
	 * locations[i] : the locations[i]-th facility is located at i-th location
	 */
	public int [] locations;
	
	public QAPSolution(int fac_num, int obj_num, int type) {
		super(obj_num, type);
		this.locations = new int[fac_num];
	}

	public int[] getLocations() {
		return locations;
	}

	public void setLocations(int[] locations) {
		this.locations = locations;
	}
	
	@Override
	public QAPSolution clone() throws CloneNotSupportedException {
		QAPSolution s = new QAPSolution(locations.length, obj_num, type);
		s.locations = this.locations.clone();
		s.object_val = this.object_val.clone();
		s.index = this.index;
		return s;
	}
}
