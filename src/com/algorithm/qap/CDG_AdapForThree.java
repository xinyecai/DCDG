package com.algorithm.qap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.problem.qap.QAPProblem;
import com.util.bean.CDGBean;
import com.util.solution.impl.QAPSolution;

public class CDG_AdapForThree{
	private QAPProblem problem;
	private int run_num;
	/**
	 * to store the solution set of each sub-problem
	 */
	private HashMap<Integer, ArrayList<QAPSolution>> subProblemSet = new HashMap<Integer, ArrayList<QAPSolution>>();
	
	private CDGBean cdg = new CDGBean();
	
	private int max_num;
	
	public CDG_AdapForThree(QAPProblem problem, int run_num){
		this.problem = problem;
		this.run_num = run_num;
		this.max_num = this.problem.sub_num;
		this.cdg.setK(15);
//		for(QAPSolution s : this.problem.solutionArray){
//			this.startList.add(s);
//		}
	}
	
	public void updateGridSystem(){
		for(int i=0;i<this.problem.obj_num;i++){
			cdg.idealPoint[i] = Integer.MAX_VALUE;
			cdg.nadPoint[i] = Integer.MIN_VALUE;
		}
		List<QAPSolution> list = this.problem.kdSet.getSolutions();
		for(QAPSolution s : list){
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
			System.out.println("iteration of : "+i);
			if(i%20==0) this.cdg.updateKNForThree(max_num);
			this.PLSexecute();
			updateGridSystem();
			try {
				this.rankBasedSelection();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		long endMili=System.currentTimeMillis();
		System.out.println((endMili-startMili)/1000+" s ");
		problem.kdSet.saveObjectiveResults("files/results/qap/"+problem.fac_num+"-"+problem.obj_num+"/"+this.problem.corr+"/adp_"+problem.fac_num+"_"+problem.obj_num+"_"+run_num+".txt", false);
	}
	public void PLSexecute(){
		Iterator<QAPSolution> iter = problem.kdSet.getSolutions().iterator();
		List<QAPSolution> startList = new ArrayList<>();
		while(iter.hasNext()){
			QAPSolution s = iter.next();
			if(!s.isSearched()){
				try {
					s.setSearched(true);
					startList.add(s.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		iter = startList.iterator();
		while(iter.hasNext()){
			this.generateNeighborhood(iter.next());
		}
	}
	/**
	 * generate the neighborhood
	 * @param s
	 */
	public void generateNeighborhood(QAPSolution s){
		int [] seq = s.getLocations().clone();
		for(int i=0;i<problem.fac_num;i++){
			for(int j=i+1;j<problem.fac_num;j++){
				if(i==j) continue;
				int [] changedVal = new int[problem.obj_num];
				for(int k=0;k<problem.obj_num;k++) changedVal[k] = 0;
				for(int k=0;k<problem.obj_num;k++){
					for(int t=0;t<this.problem.fac_num;t++){
						changedVal[k] += 2*(this.problem.distMatrix[i*this.problem.fac_num+t]*this.problem.flowMatrix[k*this.problem.fac_num*this.problem.fac_num+seq[i]*this.problem.fac_num+seq[t]]);
						changedVal[k] += 2*(this.problem.distMatrix[j*this.problem.fac_num+t]*this.problem.flowMatrix[k*this.problem.fac_num*this.problem.fac_num+seq[j]*this.problem.fac_num+seq[t]]);
					}
				}
				QAPSolution temp = new QAPSolution(this.problem.fac_num, this.problem.obj_num, -1);
				temp.setLocations(seq.clone());
				temp.locations[i] = seq[j];
				temp.locations[j] = seq[i];
				for(int k=0;k<this.problem.obj_num;k++){
					for(int t=0;t<this.problem.fac_num;t++){
						changedVal[k] -= 2*(this.problem.distMatrix[i*this.problem.fac_num+t]*this.problem.flowMatrix[k*this.problem.fac_num*this.problem.fac_num+temp.locations[i]*this.problem.fac_num+temp.locations[t]]);
						changedVal[k] -= 2*(this.problem.distMatrix[j*this.problem.fac_num+t]*this.problem.flowMatrix[k*this.problem.fac_num*this.problem.fac_num+temp.locations[j]*this.problem.fac_num+temp.locations[t]]);
					}
				}
				int t_count = 0;
				for(int k=0;k<this.problem.obj_num;k++){
					if(changedVal[k]<=0){
						t_count++;
					}
				}
				if(t_count==this.problem.obj_num){
					continue;
				}
				for(int k=0;k<this.problem.obj_num;k++) temp.object_val[k] = s.object_val[k] - changedVal[k];
				this.problem.kdSet.add(temp);
			}
		}
	}
	List<QAPSolution> startList = new ArrayList<>();
	public int index = 0;
	public void rankBasedSelection() throws CloneNotSupportedException{
		if(this.problem.kdSet.getPopSize()<=cdg.N) return;
		//if(this.problem.kdSet.getKDNodes().size()<=cdg.N) return;
		this.subProblemSet.clear();
		List<QAPSolution> list = this.problem.kdSet.getSolutions();
		for(QAPSolution s : list){
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
					ArrayList<QAPSolution> t = new ArrayList<>();
					t.add(s);
					subProblemSet.put(pos, t);
				}
			}
		}
		for(int i=0;i<this.problem.obj_num;i++){
			index = i;
			for(int d = 0;d < cdg.K; d++){
				for(int h = 0;h < cdg.K; h++){
					List<QAPSolution> cur_list = this.subProblemSet.get(i*cdg.K*cdg.K + d*cdg.K +h);
					if(cur_list==null) continue;
					cur_list.sort(new Comparator<QAPSolution>() {
						@Override
						public int compare(QAPSolution o1, QAPSolution o2) {
							if(o1.object_val[index]>o2.object_val[index]){
								return 1;
							}else if(o1.object_val[index]<o2.object_val[index]){
								return -1;
							}
							return 0;
						}
					});
					int cur = 0;
					for(QAPSolution s : cur_list) s.gridIndex[i] = cur++;
				}
			}
		}
		List<QAPSolution> solutions = list;
		//sort to obtain R'
		for(QAPSolution s : solutions) Arrays.sort(s.gridIndex,0,this.problem.obj_num);
		this.fastLexSort(solutions);
		this.problem.kdSet.clear();
		for(int i=0;i<cdg.N;i++) {
			this.problem.kdSet.add(solutions.get(i));
		}
	}
	/**
	 * a fast lex sort for 3 objectives
	 * @param list
	 */
	public void fastLexSort(List<QAPSolution> list ){
		list.sort(new Comparator<QAPSolution>(){
			@Override
			public int compare(QAPSolution o1, QAPSolution o2){
				
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
	public static void main(String[]args){
		for(int i=0;i<20;i++){
			QAPProblem problem = new QAPProblem();
			CDG_AdapForThree cdg_adap = new CDG_AdapForThree(problem, i);
			cdg_adap.execute(1500);
		}
	}
}
