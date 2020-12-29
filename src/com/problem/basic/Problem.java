package com.problem.basic;

import java.io.FileWriter;
import java.io.IOException;

import com.util.solution.impl.TSPSolution;
import com.util.solution.inter.Solution;

public abstract class Problem<T extends Solution> {
	/**
	 * description of the problem
	 */
	public String problem_des;
	/**
	 * the number of run times
	 */
	protected int run_num;
	/**
	 * the number of objective
	 */
	public int obj_num;
	/**
	 * the number of subproblems
	 */
	public int sub_num;
	/**
	 * used for moead framework
	 */
	public T [] solutionArray;
	
	/**
	 * initialize all parameters
	 */
	public abstract void initialParameters();
	/**
	 * initialize the population
	 * @param pop_size
	 */
	public abstract void initialPopulation(int pop_size);
	
	public void saveResults(String fileName, boolean append){
		try {
			FileWriter fw = new FileWriter(fileName, append);
			for(T s : this.solutionArray){
				for(int i=0;i<s.getObj_num();i++){
					fw.write(String.valueOf(s.object_val[i]));
					fw.write(" ");
				}
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
