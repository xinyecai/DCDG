package com.algorithm.tsp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.problem.tsp.TSPProblem;
import com.util.bean.CDGBean;
import com.util.file.FileUtil;
import com.util.solution.impl.TSPSolution;

public class CDG_Adap {
	private TSPProblem problem;
	private int run_num;
	/**
	 * to store the solution set of each sub-problem
	 */
	private HashMap<Integer, ArrayList<TSPSolution>> subProblemSet = new HashMap<Integer, ArrayList<TSPSolution>>();
	
	private CDGBean cdg = new CDGBean();
	
	private int max_num;

	public CDG_Adap(TSPProblem problem, int run_num){
		this.problem = problem;
		this.run_num = run_num;
		this.max_num = this.problem.sub_num;
		this.cdg.setK(10);
	}
	
	public void updateGridSystem(){
		for(int i=0;i<this.problem.obj_num;i++){
			cdg.idealPoint[i] = Integer.MAX_VALUE;
			cdg.nadPoint[i] = Integer.MIN_VALUE;
		}
		List<TSPSolution> list = this.problem.kdSet.getSolutions();
		for(TSPSolution s : list){
			//update nadir point
			for(int i=0;i<this.problem.obj_num;i++){
				if(this.cdg.nadPoint[i]<s.object_val[i])
					cdg.nadPoint[i] = (int) (s.object_val[i]*1.1);
				if(this.cdg.idealPoint[i]>s.object_val[i])
					cdg.idealPoint[i] = (int) (s.object_val[i]*0.9);
			}
		}
		//reset the width of each grid
		for(int i=0;i<this.problem.obj_num;i++)
			cdg.grid_width[i] = 1.0*(cdg.nadPoint[i] - cdg.idealPoint[i]) / cdg.K;
	}
	public void execute(int iteration){
		long current = 0;
		long startMili=System.currentTimeMillis();
		for(int i = 1;i<=iteration;i++){
			System.out.print("\riteration of : "+i+"  ");
			if(i%20==0) this.cdg.updateKN(max_num);
			this.PLSexecute();
			updateGridSystem();
			try {
				this.rankBasedSelection();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			long endMili=System.currentTimeMillis();
			long spend = (endMili-startMili)/1000;
			if(spend > current){
				current = spend;
			}
		}
		long endMili=System.currentTimeMillis();
		System.out.println((endMili-startMili)/1000+" s ");
		String fileName = "files/results/tsp/"+problem.problem_des+problem.city_num+"-R"+run_num+".txt";
		problem.kdSet.saveObjectiveResults(fileName, false);

	}
	public void PLSexecute(){
		Iterator<TSPSolution> iter = problem.kdSet.getSolutions().iterator();
		List<TSPSolution> startList = new ArrayList<>();
		while(iter.hasNext()){
			TSPSolution s = iter.next();
			if(!s.isSearched()){
				try {
					s.setSearched(true);
					startList.add(s.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		//problem.initialCandidateList(startList);
		iter = startList.iterator();
		while(iter.hasNext()){
			this.generateNeighborhood(iter.next());
		}
	}
	/**
	 * generate the neighborhood of s
	 * @param s
	 */
	public void generateNeighborhood(TSPSolution s){
		int [] rounte = new int[problem.city_num+1];
		for(int i=0;i<problem.city_num;i++) rounte[i] = s.route[i];
		rounte[problem.city_num] = rounte[0];
		int [] pos = new int[problem.city_num];
		problem.calculatePosition(rounte, pos);
		int pos1 = 0;
		int pos2 = 0;
		for(int k=0;k<problem.city_num;k++){
			int i = k;
			pos1 = i;
			int curIndex = rounte[i];
			int nextIndex = rounte[i+1];
			ArrayList<Integer> candidate = problem.candidateList.get(curIndex);
			Iterator<Integer> iter = candidate.iterator();
			while(iter.hasNext()){
				int next = iter.next();
				pos2 = pos[next];
				int curIndex1 = rounte[pos2];
				int nextIndex1 = rounte[pos2+1];
				if(curIndex == nextIndex1) continue;
				if(curIndex == curIndex1) continue;
				if(nextIndex == nextIndex1) continue;
				if(curIndex1 == nextIndex) continue;
				int betterTimes = 0;
				TSPSolution solution = new TSPSolution(problem.city_num, problem.obj_num, -1);
				for(int j=0;j<problem.obj_num;j++){
					int gain = problem.disMatrix[j*problem.city_num*problem.city_num+curIndex*problem.city_num+curIndex1] +
							problem.disMatrix[j*problem.city_num*problem.city_num+nextIndex*problem.city_num+nextIndex1] - 
							(problem.disMatrix[j*problem.city_num*problem.city_num+curIndex*problem.city_num+nextIndex]+
							problem.disMatrix[j*problem.city_num*problem.city_num+curIndex1*problem.city_num+nextIndex1]);
					if(gain<0) betterTimes++;
					solution.object_val[j] = s.object_val[j] + gain;
				}
				if(betterTimes==0) continue;
				solution.route = s.route.clone();

				if(problem.kdSet.add(solution)){
					problem.converse(pos1, pos2, solution.route, 0);
				}
			}
		}
	}
	public int index = 0;
	public void rankBasedSelection() throws CloneNotSupportedException{
		if(this.problem.kdSet.getPopSize()<=cdg.N) return;
		this.subProblemSet.clear();
		List<TSPSolution> list = this.problem.kdSet.getSolutions();
		for(TSPSolution s : list){
			for(int i=0;i<this.problem.obj_num;i++){
				int coord = (int) ((s.object_val[i]-cdg.idealPoint[i])/cdg.grid_width[i]);
				if(coord == cdg.K) coord = cdg.K - 1;
				if(subProblemSet.keySet().contains(i*cdg.K+coord)){
					subProblemSet.get(i*cdg.K+coord).add(s);
				}else{
					ArrayList<TSPSolution> t = new ArrayList<>();
					t.add(s);
					subProblemSet.put(i*cdg.K+coord, t);
				}
			}
		}
		for(int i=0;i<this.problem.obj_num;i++){
			index = i;
			for(int d = 0;d < cdg.K; d++){
				List<TSPSolution> cur_list = this.subProblemSet.get(i*cdg.K + d);
				if(cur_list==null) continue;
				cur_list.sort(new Comparator<TSPSolution>() {
					@Override
					public int compare(TSPSolution o1, TSPSolution o2) {
						if(o1.object_val[1-index]>o2.object_val[1-index]){
							return 1;
						}else if(o1.object_val[1-index]<o2.object_val[1-index]){
							return -1;
						}
						return 0;
					}
				});
				int cur = 0;
				for(TSPSolution s : cur_list) s.gridIndex[1-index] = cur++;
			}
		}
		List<TSPSolution> solutions = list;//this.problem.kdSet.getSolutions();
		//sort to obtain R'
		for(TSPSolution s : solutions) Arrays.sort(s.gridIndex,0,this.problem.obj_num);
		this.fastLexSort(solutions);
		this.problem.kdSet.clear();
		for(int i=0;i<cdg.N;i++) {
			this.problem.kdSet.add(solutions.get(i));
		}
	}
	/**
	 * a fast lex sort for 2 objectives
	 * @param list
	 */
	public void fastLexSort(List<TSPSolution> list ){
		list.sort(new Comparator<TSPSolution>(){
			public int compare(TSPSolution o1, TSPSolution o2){
				int num11 = o1.gridIndex[0];
				int num12 = o1.gridIndex[1];
				int num21 = o2.gridIndex[0];
				int num22 = o2.gridIndex[1];
				if (num11*1000+num12 > num21*1000+num22){
					return 1;
				}else if(num11*1000+num12 < num21*1000+num22){
					return -1;
				}
				return 0;
			}
		});
	}
	public static void main(String []args){
		for (int i = 1; i <= 1; i++) {
			TSPProblem tsp = new TSPProblem();
			CDG_Adap cdg_adap = new CDG_Adap(tsp, i);

			int Iteration = 600;
			cdg_adap.execute(Iteration);
		}
	}
}
