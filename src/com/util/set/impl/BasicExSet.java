package com.util.set.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.util.set.inter.ExSetInter;
import com.util.solution.inter.Solution;

/**
 * A basic external set to store all non-dominated solutions
 * simple but low efficiency
 * @author heaven
 *
 */
public class BasicExSet<T extends Solution> implements ExSetInter<T>{
	/**
	 * a collection to store all solutions
	 */
	private List<T> solutions = new ArrayList<T>();
	
	@Override
	public boolean add(T s) {
		Iterator<T> iter = solutions.iterator();
		List<T> rmSet = new ArrayList<T>();
		boolean addIn = true;
		while(iter.hasNext()){
			T currentSolution = iter.next();
			int ret = currentSolution.compareTo(s);
			if(ret==1){
				addIn = false;
			}else if(ret==-1){ 
				rmSet.add(currentSolution);
			}
		}
		this.solutions.removeAll(rmSet);
		if(!addIn){
			return false;
		}
		this.solutions.add(s);	
		return true;
	}
	
	@Override
	public void add(List<T> list) {
		for(T s : solutions){
			this.add(s);
		}
	}
	
	@Override
	public int getPopSize() {
		return this.solutions.size();
	}

	@Override
	public void saveResults() {
		
	}

	@Override
	public List<T> getSolutions() {
		return solutions;
	}
	@Override  
	protected Object clone() throws CloneNotSupportedException {  
		BasicExSet<T> set = new BasicExSet<>();
		Iterator<T> iter = solutions.iterator();
		while(iter.hasNext()){
			set.solutions.add(iter.next());
		}
		
		return null;  
	}

	@Override
	public void clear() {
		this.solutions.clear();
	}

	@Override
	public void saveObjectiveResults(String fileName, boolean append) {
		try {
			FileWriter fw = new FileWriter(fileName, append);
			for(Solution s : solutions){
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

	@Override
	public void saveSolutionResults(String fileName) {

	}
}  
