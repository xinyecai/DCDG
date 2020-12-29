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
import com.util.set.inter.ExSetInter;
import com.util.solution.impl.TSPSolution;
import com.util.solution.inter.KDNode;
import com.util.solution.inter.Solution;

public class CDG_AdapForThree {
	private TSPProblem problem;
	private int run_num;
	/**
	 * to store the solution set of each sub-problem
	 */
	private HashMap<Integer, ArrayList<TSPSolution>> subProblemSet = new HashMap<Integer, ArrayList<TSPSolution>>();
	
	private CDGBean cdg = new CDGBean();
	
	private int max_num;

	public List<int []> routeList = new ArrayList<>();


	public CDG_AdapForThree(TSPProblem problem, int run_num){
		this.problem = problem;
		this.run_num = run_num;
		this.max_num = this.problem.sub_num;
		this.cdg.setK(15);
		for(TSPSolution s : this.problem.solutionArray){
			this.startList.add(s);
		}
	}
	
	public void updateGridSystem(){
		for(int i=0;i<this.problem.obj_num;i++){
			cdg.idealPoint[i] = Integer.MAX_VALUE;
			cdg.nadPoint[i] = Integer.MIN_VALUE;
		}
		//List<TSPSolution> list = this.problem.kdSet.getSolutions();
		Iterator<KDNode<TSPSolution>> iter = problem.kdSet.getKDNodes().iterator();
		while(iter.hasNext()){
			KDNode<TSPSolution> node = iter.next();
			if(node.isDominated) continue;
			TSPSolution s = node.solution;
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

		long startMili=System.currentTimeMillis();
		for(int i = 1;i<=iteration;i++){
			System.out.print("\riteration of : "+i);
			if(i%20==0) this.cdg.updateKNForThree(max_num);
			this.PLSexecute(i);
			updateGridSystem();
			try {
				this.rankBasedSelection();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		long endMili=System.currentTimeMillis();
		System.out.println("\t" + (endMili-startMili)/1000+" s ");
		String fileName = "files/results/tsp/"+problem.problem_des+problem.city_num+"-R"+run_num+".txt";
		problem.kdSet.saveObjectiveResults(fileName, false);
	}
	List<TSPSolution> startList = new ArrayList<>();
	public void PLSexecute(int it){
		Iterator<TSPSolution> iter2 = startList.iterator();
		while(iter2.hasNext()){
			this.generateNeighborhood(iter2.next(), it);
		}
	}
	/**
	 * generate the neighborhood of s
	 * @param s
	 */
	public void generateNeighborhood(TSPSolution s, int it){
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
		//if(this.problem.kdSet.getKDNodes().size()<=cdg.N) return;
		this.subProblemSet.clear();
		List<TSPSolution> list = this.problem.kdSet.getSolutions();
		for(TSPSolution s : list){
			for(int i=0;i<this.problem.obj_num;i++){
				int xIndex = 0, yIndex = 0;
				if(i==0){xIndex = 1; yIndex = 2;}
				if(i==1){xIndex = 2; yIndex = 0;}
				if(i==2){xIndex = 0; yIndex = 1;}
				int xAxis = (int) ((s.object_val[xIndex]-cdg.idealPoint[xIndex])/cdg.grid_width[xIndex]);
				int yAxis = (int) ((s.object_val[yIndex]-cdg.idealPoint[yIndex])/cdg.grid_width[yIndex]);

				
				if(xAxis==cdg.K) xAxis = cdg.K - 1;
				if(yAxis==cdg.K) yAxis = cdg.K - 1;
				
				int pos = i*cdg.K*cdg.K+xAxis*cdg.K+yAxis;
				if(subProblemSet.keySet().contains(pos)){
					subProblemSet.get(pos).add(s);
				}else{
					ArrayList<TSPSolution> t = new ArrayList<>();
					t.add(s);
					subProblemSet.put(pos, t);
				}
			}
		}
		for(int i=0;i<this.problem.obj_num;i++){
			index = i;
			for(int d = 0;d < cdg.K; d++){
				for(int h = 0;h < cdg.K; h++){
					List<TSPSolution> cur_list = this.subProblemSet.get(i*cdg.K*cdg.K + d*cdg.K +h);
					if(cur_list==null) continue;
					cur_list.sort(new Comparator<TSPSolution>() {
						@Override
						public int compare(TSPSolution o1, TSPSolution o2) {
							if(o1.object_val[index]>o2.object_val[index]){
								return 1;
							}else if(o1.object_val[index]<o2.object_val[index]){
								return -1;
							}
							return 0;
						}
					});
					int cur = 0;
					for(TSPSolution s : cur_list) s.gridIndex[i] = cur++;
				}
			}
		}
		List<TSPSolution> solutions = list;//this.problem.kdSet.getSolutions();
		//sort to obtain R'
		for(TSPSolution s : solutions) Arrays.sort(s.gridIndex,0,this.problem.obj_num);
		this.fastLexSort(solutions);
		this.problem.kdSet.clear();
		startList.clear();
		for(int n=0;n<cdg.N;n++) {
			TSPSolution s = solutions.get(n);
			this.problem.kdSet.add(s);
			if(!s.isSearched())startList.add(s.clone());
			s.setSearched(true);
		}
	}
	/**
	 * a fast lex sort for 3 objectives
	 * @param list
	 */
	public void fastLexSort(List<TSPSolution> list ){
		list.sort(new Comparator<TSPSolution>(){
			@Override
			public int compare(TSPSolution o1, TSPSolution o2){
				
				if(o1.gridIndex[0] > o2.gridIndex[0]) return 1;
				if(o1.gridIndex[0] < o2.gridIndex[0]) return -1;
				
				if(o1.gridIndex[1] > o2.gridIndex[1]) return 1;
				if(o1.gridIndex[1] < o2.gridIndex[1]) return -1;
				
				if(o1.gridIndex[2] > o2.gridIndex[2]) return 1;
				if(o1.gridIndex[2] < o2.gridIndex[2]) return -1;
				return 0;
			}
		});
	}
	public static void main(String []args){
		for (int i = 1; i <= 1; i++) {
			TSPProblem tsp = new TSPProblem();
			CDG_AdapForThree cdg_adapForThree = new CDG_AdapForThree(tsp, i);

			int Iteration = 600;
			cdg_adapForThree.execute(Iteration);
		}
	}
}
