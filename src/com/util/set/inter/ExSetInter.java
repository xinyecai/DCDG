package com.util.set.inter;

import java.util.List;

import com.util.solution.inter.Solution;

/**
 * An interface that is used to define the interface of 
 * all the operation for the set to store all non-dominated solutions
 * @author heaven
 *
 */
public interface ExSetInter<T extends Solution> extends Cloneable{
	/**
	 * get all solutions
	 * @return
	 */
	public List<T> getSolutions();
	/**
	 * add a solution to current set
	 */
	public boolean add(T s);
	/**
	 * add solutions to current set
	 * @param list
	 */
	public void add(List<T> list);
	/**
	 * get the population size
	 * @return
	 */
	public int getPopSize();
	/**
	 * save the result to file
	 */
	public void saveResults();
	/**
	 * clear the set
	 */
	public void clear();
	/**
	 * print the results of objective
	 */
	public void saveObjectiveResults(String fileName, boolean append);
	/**
	 * print the results of solutions
	 */
	public void saveSolutionResults(String fileName);
}
