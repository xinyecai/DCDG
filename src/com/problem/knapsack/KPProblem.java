package com.problem.knapsack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.problem.basic.Problem;
import com.util.file.FileUtil;
import com.util.random.RandomGenerator;
import com.util.set.impl.BasicExSet;
import com.util.set.impl.KDTreeSet;
import com.util.set.inter.ExSetInter;
import com.util.solution.impl.KPSolution;

public class KPProblem extends Problem<KPSolution>{
	/**
	 * the number of items
	 */
	public int item_num;
	/**
	 * profit for each item
	 */
	public int [] profitArray;
	/**
	 * weight for each item
	 */
	public int [] weightArray;
	/**
	 * capacity for each knapsack
	 */
	public int [] capacityArray;
	/**
	 * the external set to store the outputs
	 */
	public ExSetInter<KPSolution> exSet = new BasicExSet<KPSolution>();
	/**
	 * the starting solutions used to PLS
	 */
	public ExSetInter<KPSolution> startingSet = new BasicExSet<KPSolution>();
	/**
	 * the KD-Tree set
	 */
	public KDTreeSet<KPSolution> kdSet;
	/**
	 * the KD-Tree set as starting solutions
	 */
	public ExSetInter<KPSolution> kdStartingSet;
	
	public KPProblem(String des){
		this.problem_des = des;
		this.initialParameters();
		kdSet = new KDTreeSet<>(obj_num);
		kdStartingSet = new KDTreeSet<>(obj_num);
		this.initialPopulation(this.sub_num);
	}
	public void execute(int iteration){
		long startMili=System.currentTimeMillis();
		List<KPSolution> startList = new ArrayList<>();
		Iterator<KPSolution> iter = kdSet.getSolutions().iterator();		
		while(iter.hasNext()){
			KPSolution t = iter.next();
			try {
				startList.add(t.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		for(int i = 0;i<iteration;i++){
			System.out.println("iteration of : "+(i+1));
			this.PLSexecute(startList);
			startList.clear();
			iter = kdStartingSet.getSolutions().iterator();		
			while(iter.hasNext()){
				KPSolution t = iter.next();
				try {
					startList.add(t.clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			kdStartingSet.clear();
			kdSet.clearAndReset();
		}
		long endMili=System.currentTimeMillis();
		System.out.println((endMili-startMili)/1000+" s ");
		kdSet.saveObjectiveResults(problem_des+item_num+"-"+obj_num+"_"+run_num+".txt", false);
	}
	public void PLSexecute(List<KPSolution> list){
		Iterator<KPSolution> iter = list.iterator();
		while(iter.hasNext()){
			KPSolution s;
			try {
				s = iter.next().clone();
				this.generateNeighborhood(s);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("resource")
	@Override
	public void initialParameters() {
		String fileName = "files/instances/knapsack";
		String testFileName = fileName+"/testInstance.txt";
		String line = FileUtil.getLine(testFileName);
		String arr[] = line.split(" ");
		this.item_num = Integer.parseInt(arr[0]);
		this.obj_num = Integer.parseInt(arr[1]);
		this.sub_num = Integer.parseInt(arr[2]);
		this.profitArray = new int[this.item_num*this.obj_num];
		this.weightArray = new int[this.item_num*this.obj_num];
		this.capacityArray = new int[this.obj_num];
		//load the profit information
		String profitFileName = fileName+"/profit"+this.item_num+"_"+this.obj_num+".txt";
		String weightFileName = fileName+"/weight"+this.item_num+"_"+this.obj_num+".txt";
		String capacityFileName = fileName+"/capacity"+this.item_num+"_"+this.obj_num+".txt";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(profitFileName));
			int index = 0;
			for(line = br.readLine();line!=null;line=br.readLine()){
				this.profitArray[index++] = Integer.parseInt(line);
			}
			index = 0;
			br = new BufferedReader(new FileReader(weightFileName));
			for(line = br.readLine();line!=null;line=br.readLine()){
				this.weightArray[index++] = Integer.parseInt(line);
			}
			index = 0;
			br = new BufferedReader(new FileReader(capacityFileName));
			for(line = br.readLine();line!=null;line=br.readLine()){
				this.capacityArray[index++] = Integer.parseInt(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialPopulation(int pop_size) {
		for(int i=0;i<this.sub_num;i++){
			KPSolution s = new KPSolution(this.obj_num, this.item_num, 1);
			for(int r=0;r<this.item_num;r++) s.item_array[r] = 0; 
			int [] randArray = RandomGenerator.permutation_array(0, this.item_num-1);
			int [] sum = new int[this.obj_num]; for(int r=0;r<this.obj_num;r++) sum[r] = 0;
			for(int j=0;j<this.item_num;j++){
				boolean flag = true;
				int index = randArray[j];
				for(int k=0;k<this.obj_num;k++){
					 int temp = sum[k] + this.weightArray[this.item_num*k+index];
					if(temp > this.capacityArray[k]){
						flag = false;
						break;
					}
				}
				if(flag){
					s.item_array[index] = 1;
					for(int k=0;k<this.obj_num;k++){
						sum[k] += this.weightArray[this.item_num*k+index];
					}
				}else break;
				
			}
			if(!this.calculateObjective(s)) System.out.println("error");
			this.kdSet.add(s);
		}
	}
	/**
	 * calculate the objective value
	 * @param s
	 */
	public boolean calculateObjective(KPSolution s){
	
		for(int i=0;i<this.obj_num;i++) { 
			int sumForWeight = 0;
			int sumForProfit = 0;
			for(int j=0;j<this.item_num;j++){
				if(s.item_array[j] == 1){
					sumForWeight += this.weightArray[this.item_num*i+j];
					sumForProfit += this.profitArray[this.item_num*i+j];
				}
			}
			if(sumForWeight > this.capacityArray[i]) return false;
			s.current_weight[i] = sumForWeight;
			s.object_val[i] = sumForProfit;
		}
		return true;
	}
	public void intitalPopulationByGreedy(int pop_size){
		
	}
	/**
	 * generate the neighborhood of s
	 * @param s
	 */
	public void generateNeighborhood(KPSolution s){
		ArrayList<Integer> selectSet = new ArrayList<>();
		ArrayList<Integer> unSelectSet = new ArrayList<>();
		for(int i=0;i<this.item_num;i++){
			if(s.item_array[i]==1){
				selectSet.add(i);
			}else{
				unSelectSet.add(i);
			}
		}
		int len1 = selectSet.size();
		int len2 = unSelectSet.size();
		int loop = 20;
		int opt = 2;
		for(int i=0;i<loop;i++){
			int [] rand_array = RandomGenerator.permutation_array(0, len1-1);
			for(int j = 0;j <= len1-opt;j+=opt){
				for(int x = 0;x < len2;x++){
					try {
						KPSolution temp = s.clone();
						for(int k=0;k<opt;k++){
							int pos = selectSet.get(rand_array[j+k]);
							temp.item_array[pos] = 0;
							for(int m=0;m<obj_num;m++){
								temp.object_val[m] -= this.profitArray[m*item_num+pos];
								temp.current_weight[m] -= this.weightArray[m*item_num+pos];
							}
						}
						boolean canAdd = false;
						int betterTimes = 0;
						for(int y=x;y<len2;y++){
							int chosePos = unSelectSet.get(y);
							boolean isValid = true;
							for(int n=0;n<obj_num;n++){
								int tempWeight = temp.current_weight[n] + this.weightArray[n*item_num+chosePos];
								if(tempWeight > this.capacityArray[n]){
									isValid = false;
									break;
								}
							}
							if(isValid){
								canAdd = true;
								temp.item_array[chosePos] = 1;
								for(int n=0;n<obj_num;n++){
									temp.object_val[n] += this.profitArray[n*item_num+chosePos];
									if(temp.object_val[n] > s.object_val[n]){
										betterTimes++;
									}
									temp.current_weight[n] += this.weightArray[n*item_num+chosePos];
								}
							}
						}
						if(canAdd && betterTimes > 0){
							if(this.kdSet.add(temp)){
								this.startingSet.add(temp);
							}
						}
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void main(String[]args){
		KPProblem problem = new KPProblem("kp");
		problem.execute(100);
	}
}
