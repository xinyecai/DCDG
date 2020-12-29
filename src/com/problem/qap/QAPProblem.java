package com.problem.qap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import com.problem.basic.Problem;
import com.util.file.FileUtil;
import com.util.random.RandomGenerator;
import com.util.set.impl.BasicExSet;
import com.util.set.impl.KDTreeSet;
import com.util.set.inter.ExSetInter;
import com.util.solution.impl.QAPSolution;

public class QAPProblem extends Problem<QAPSolution>{
	/**
	 * the number of locations/facilities
	 */
	public int fac_num;
	/**
	 * the distance matrix
	 */
	public int [] distMatrix;
	/**
	 * the flow matrix
	 */
	public int [] flowMatrix;
	
	public String corr;
	/**
	 * the external set to store the outputs
	 */
	public ExSetInter<QAPSolution> exSet = new BasicExSet<QAPSolution>();
	/**
	 * the starting solutions used to PLS
	 */
	public ExSetInter<QAPSolution> startingSet = new BasicExSet<QAPSolution>();
	/**
	 * the KD-Tree set
	 */
	public KDTreeSet<QAPSolution> kdSet;
	/**
	 * the KD-Tree set as starting solutions
	 */
	public ExSetInter<QAPSolution> kdStartingSet;
	
	/**
	 * 
	 * @param run_num
	 * @param des
	 */
	public QAPProblem() {
		this.initialParameters();
		this.solutionArray = new QAPSolution[this.sub_num];
		kdSet = new KDTreeSet<QAPSolution>(this.obj_num);
		kdStartingSet = new KDTreeSet<QAPSolution>(this.obj_num);	
		this.initialPopulation(this.sub_num);
	}
	/**
	 * calculate the objectives of the given solution
	 * @param s
	 */
	public void calculateObjective(QAPSolution s) {
		for(int obj=0;obj<obj_num;obj++){
			int total = 0;
			for(int i=0;i<fac_num;i++){
				for(int j=0;j<fac_num;j++){
					int distPos1 = i;
					int distPos2 = j;
					int flowPos1 = s.locations[i];
					int flowPos2 = s.locations[j];
					total += distMatrix[distPos1*fac_num+distPos2]*
							flowMatrix[obj*fac_num*fac_num+flowPos1*fac_num+flowPos2];
				}
			}
			s.object_val[obj] = total;
		}
	}
	/**
	 * initialize the parameters, mainly including read contents from files
	 */
	@Override
	public void initialParameters() {
		String fileName = "files/instances/qap/";
		String testFileName = fileName+"/testInstance.txt";
		String line = FileUtil.getLine(testFileName);
		String arr[] = line.split(" ");
		this.sub_num = Integer.parseInt(arr[0]);
		this.fac_num = Integer.parseInt(arr[1]);
		this.obj_num = Integer.parseInt(arr[2]);
		this.corr = arr[3];
		fileName += this.fac_num+"-"+this.obj_num+"/"+this.corr; 
		this.distMatrix = new int[fac_num*fac_num];
		this.flowMatrix = new int[fac_num*fac_num*obj_num];
		String disFileName = fileName+"/"+arr[4]+".txt";
		String flowFileName = fileName+"/"+arr[5]+".txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(disFileName));
			int row = 0;
			for(line=br.readLine();line!=null;line=br.readLine()){
				String num_arr [] = line.trim().split("\\s+");
				for(int col=0;col<fac_num;col++){
					this.distMatrix[row*fac_num+col] = Integer.parseInt(num_arr[col]);
				}
				row++;
			}
			br.close();
			br = new BufferedReader(new FileReader(flowFileName));
			for(int obj=0;obj<obj_num;obj++){
				row = 0;
				for(line=br.readLine();line!=null;line=br.readLine()){
					String num_arr [] = line.trim().split("\\s+");
					for(int col=0;col<fac_num;col++){
						this.flowMatrix[obj*fac_num*fac_num+row*fac_num+col] = Integer.parseInt(num_arr[col]);
					}
					row++;
				}
			}
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void initialPopulation(int pop_size) {
		for(int i=0;i<pop_size;i++){
			QAPSolution s = new QAPSolution(this.fac_num, this.obj_num, -1);
			s.setLocations(RandomGenerator.permutation_array(0, fac_num-1));
			this.calculateObjective(s);
			s.setSearched(false);
			exSet.add(s);
			kdSet.add(s);
			s.index = i;
			this.solutionArray[i] = s;
		}
	}
}
