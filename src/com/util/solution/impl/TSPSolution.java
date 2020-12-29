package com.util.solution.impl;

import com.util.solution.inter.Solution;

public class TSPSolution extends Solution{
	/**
	 * the number of the city
	 */
	protected int city_num;
	/**
	 * the route of a tsp
	 */
	public int [] route;
	
	public TSPSolution(int city_num, int obj_num, int type) {
		super(obj_num, type);
		this.city_num = city_num;
	}

	public int getCity_num() {
		return city_num;
	}

	public void setCity_num(int city_num) {
		this.city_num = city_num;
	}

	public int[] getRoute() {
		return route;
	}

	public void setRoute(int[] route) {
		this.route = route;
	}

	@Override
	public TSPSolution clone() throws CloneNotSupportedException {
		TSPSolution s = new TSPSolution(city_num, obj_num, -1);
		s.route = this.route.clone();
		s.object_val = this.object_val.clone();
		s.index = this.index;
		return s;
	}
}
