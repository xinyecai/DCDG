package com.problem.tsp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.problem.basic.Problem;
import com.util.file.FileUtil;
import com.util.random.RandomGenerator;
import com.util.set.impl.BasicExSet;
import com.util.set.impl.KDTreeSet;
import com.util.set.inter.ExSetInter;
import com.util.solution.impl.TSPSolution;

import javax.net.ssl.SSLContext;

public class TSPProblem extends Problem<TSPSolution>{
	/**
	 * the number of the city
	 */
	public int city_num;
	/**
	 * the distance matrix
	 */
	public int [] disMatrix;
	public int [][][] D;

	/**
	 * the candidate list of each city
	 */
	public Map<Integer, ArrayList<Integer>> candidateList = new HashMap<Integer, ArrayList<Integer>>();
	/**
	 * the external set to store the outputs
	 */
	public ExSetInter<TSPSolution> exSet = new BasicExSet<TSPSolution>();
	/**
	 * the starting solutions used to PLS
	 */
	public ExSetInter<TSPSolution> startingSet = new BasicExSet<TSPSolution>();
	/**
	 * the KD-Tree set
	 */
	public KDTreeSet<TSPSolution> kdSet;
	/**
	 * the KD-Tree set as starting solutions
	 */
	public ExSetInter<TSPSolution> kdStartingSet;

	public TSPProblem(){
		this.initialParameters();
		solutionArray = new TSPSolution[sub_num];
		kdSet = new KDTreeSet<>(obj_num);
		kdStartingSet = new KDTreeSet<>(obj_num);
		this.initialPopulation(this.sub_num);
		this.initialCandidateList();
//		this.initialCandidateList(kdSet.getSolutions());
	}
	/**
	 * initialize the population
	 */
	public void initialPopulation(int pop_size){
		for(int i=0;i<pop_size;i++){
			TSPSolution s = new TSPSolution(city_num, obj_num, -1);
			s.setObj_num(obj_num);
			s.setRoute(RandomGenerator.permutation_array(0, city_num-1));
			this.calculateObjective(s);
			s.setSearched(false);
			exSet.add(s);
			kdSet.add(s);
			s.index = i;
			solutionArray[i] = s;
		}
	}
	/**
	 * calculate the objectives of the given solution
	 * @param s
	 */
	public void calculateObjective(TSPSolution s) {
		int [] route = s.route.clone();
		for(int i=0;i<obj_num;i++){
			int total_dis = 0;
			for(int j=0;j<city_num-1;j++){
				int pos = route[j]*city_num + route[j+1];
				total_dis += this.disMatrix[i*city_num*city_num+pos];
			}
			//handle the last and the first
			int pos = route[city_num-1]*city_num + route[0];
			total_dis += disMatrix[pos];
			s.object_val[i] = total_dis;
		}
	}

	/**
	 * use the method proposed in 
	 * Lust T, Jaszkiewicz A. Speed-up techniques for solving large-scale biobjective TSP[J]. 
	 * Computers & Operations Research, 2010, 37(3):521-533.
	 */
	public void initialCandidateList(){
		int layer = 5;
 		for(int i=0;i<this.city_num;i++){
 			ArrayList<TSPSolution> temp = new ArrayList<>();
			for(int j=0;j<this.city_num;j++){
				if(i==j) continue;
				TSPSolution s = new TSPSolution(this.city_num, this.obj_num, -1);
				for(int k=0;k<this.obj_num;k++){
					s.object_val[k] = this.disMatrix[city_num*city_num*k + i*city_num + j];
				}
				s.index = j;
				temp.add(s);
			}
			ArrayList<Integer> list = new ArrayList<>();
			int iter = 0;
			while(iter<layer){
				KDTreeSet<TSPSolution> kdSet = new KDTreeSet<>(this.obj_num);
				for(TSPSolution s : temp){
					kdSet.add(s);
				}
				for(TSPSolution s : kdSet.getSolutions()){
					list.add(s.index);
				}
				temp.removeAll(kdSet.getSolutions());
				iter++;
			}
			temp.addAll(kdSet.getSolutions());
			candidateList.put(i, list);
		}
//		for(TSPSolution s : temp){
//			System.out.println(s.object_val[0]+" "+s.object_val[1]);
//		}
	}
	/**
	 * initialize the candidate list proposed in
	 * Ke L, Zhang Q, Battiti R. Hybridization of decomposition and local search for multiobjective optimization.[J]. 
	 * IEEE Transactions on Cybernetics, 2014, 44(44):1808-1820.
	 * 
	 */
	public void initialCandidateList(List<TSPSolution> list){
		for(TSPSolution s : list){
			int [] tour = s.route.clone();
			for(int i=0;i<city_num-1;i++){
				if(!candidateList.keySet().contains(tour[i])){
					ArrayList<Integer> subList = new ArrayList<>();
					candidateList.put(tour[i], subList);
				}
				candidateList.get(tour[i]).add(tour[i+1]);
			}
			if(!candidateList.keySet().contains(tour[city_num-1])){
				ArrayList<Integer> subList = new ArrayList<>();
				candidateList.put(tour[city_num-1], subList);
			}
			candidateList.get(tour[city_num-1]).add(tour[0]);
		}
	}
	/**
	 * read the parameters from the files
	 */
	@Override
	public void initialParameters() {
		String fileName = "files/instances/tsp";
		String testFileName = fileName+"/testInstance.txt";
		String line = FileUtil.getLine(testFileName);
		String arr[] = line.split(" ");
		this.city_num = Integer.parseInt(arr[0]);
		this.obj_num = Integer.parseInt(arr[1]);
		this.sub_num = Integer.parseInt(arr[2]);
		this.disMatrix = new int[city_num*city_num*obj_num];
		this.D = new int[obj_num][city_num][city_num];
		this.problem_des = arr[arr.length-1];
		for(int i=0;i<obj_num;i++){
			String disFileName = fileName+"/"+arr[arr.length-1]+"/"+arr[3+i]+".tsp";
			int [] X = new int[city_num];
			int [] Y = new int[city_num];
			try {
				BufferedReader br = new BufferedReader(new FileReader(disFileName));
				br.readLine();
				int index = 0;
				for(line=br.readLine();line!=null;line=br.readLine()){
					String [] ss = line.split("\\s++");
					if(line.equals("EOF")) break;
					X[index] = Integer.parseInt(ss[1]);
					Y[index] = Integer.parseInt(ss[2]);
					index++;
				}
				for(int j=0;j<city_num;j++){
					for(int k=j;k<city_num;k++){
						double temp1 = Math.pow(X[j] - X[k], 2) + Math.pow(Y[j] - Y[k], 2);
						int eucDis1 = (int)(Math.sqrt(temp1)+0.5);
						disMatrix[i*city_num*city_num+j*city_num+k] = eucDis1;
						disMatrix[i*city_num*city_num+k*city_num+j] = eucDis1;
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * get the index of the array of each city number
	 * @param rounte
	 * @param pos
	 */
	public void calculatePosition(int [] rounte, int [] pos){
		for(int i=0;i<city_num;i++){
			pos[rounte[i]] = i;
		}
	}
	
	/**
	 * converse the sequence between pos1 to pos2
	 * type means the direction
	 * @param pos1
	 * @param pos2
	 * @param type
	 */
	public void converse(int pos1, int pos2, int [] rounte, int type){
		int h1 = pos1;
		int h2 = pos2;
		int mid = (h1 + h2)/2;
		int temp;
		if(type==0){
			if(pos1>pos2){
				h2 = pos1;
				h1 = pos2;
			}
			h1++;
			
			for(int i = h1;i <= mid;i++){
				temp = rounte[i];
				rounte[i] = rounte[h1 + h2 - i];
				rounte[h1 + h2 - i] = temp;
			}
		}else{
			if(pos1<pos2){
				h2 = pos1;
				h1 = pos2;
			}
			h1--;
			for(int i=h2;i<=mid;i++){
				temp = rounte[i];
				rounte[i] = rounte[h1 + h2 -i];
				rounte[h1 + h2 -i] = temp;
			}
		}
	}
}
