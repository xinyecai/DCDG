package com.algorithm.basic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import com.problem.basic.Problem;
import com.util.bean.Entity;
import com.util.bean.METHOD;

public class BasicMoead {
	/**
	 * the reference line (weight vector)
	 */
	protected double [] weights;
	
	protected int [][] nicheNeighbors;
	
	protected int [] idealPoint;
	
	private int obj_num_clone;
	
	public BasicMoead(int obj_num_clone, int type){
		idealPoint = new int[obj_num_clone];
		this.obj_num_clone = obj_num_clone;
		for(int i=0;i<obj_num_clone;i++){
			this.idealPoint[i] = -type*Integer.MAX_VALUE;
		}
	}
	public void bind(Problem<?> problem, int method){
		for(int i=0;i<problem.sub_num;i++){
			int [] values = problem.solutionArray[i].object_val;
			if(method == METHOD.WS){
				problem.solutionArray[i].fitness = getWsValue(i, values);
			}else if(method == METHOD.TCH){
				problem.solutionArray[i].fitness = getTchValue(i, values);
			}else if(method ==METHOD.PBI){
				problem.solutionArray[i].fitness = getPbiValue(i, values);
			}
		}
	}
	/**
	 * update the ideal point
	 * @param obj_value
	 */
	public void udpateIdealPoint(int [] obj_value){
		for(int i=0;i<this.obj_num_clone;i++){
			if(idealPoint[i]>obj_value[i]){
				idealPoint[i] = obj_value[i];
			}
		}
	}
	public boolean initialWeights(int sub_num, int niche){
		if(obj_num_clone==2){
			for(int i=0;i<sub_num;i++){
				for(int j=0;j<obj_num_clone;j++){
					if(j==0){
						weights[obj_num_clone*i] = (double)(sub_num-i)/(double)(sub_num);
					}else{
						weights[obj_num_clone*i+j] = 1 - weights[obj_num_clone*i];
					}
				}
			}
		}else if(obj_num_clone==3){
			try {
				BufferedReader br = new BufferedReader(new FileReader("files/weights/weights3_300.txt"));
				int index = 0;
				for(String line=br.readLine();line!=null;line=br.readLine()){
					String [] arr = line.split("\t");
					for(int m=0;m<obj_num_clone;m++){
						weights[index*obj_num_clone+m] = Double.parseDouble(arr[m]);
					}
					index++;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			System.err.println("can't initialize the weights for the number of objective than 2");
			return false;
		}
		
		//determine the niche neighbor of each solution
		for(int i=0;i<sub_num;i++){
			ArrayList<Entity> list = new ArrayList<>();
			for(int j=0;j<sub_num;j++){
				//if(i==j) continue;
				double value = 0;
				for(int k=0;k<obj_num_clone;k++){
					value += Math.pow(weights[i*2+k]-weights[j*2+k], 2);
				}
				Entity e = new Entity();
				e.setValue(value);
				e.setIndex(j);
				list.add(e);
			}
			list.sort(new Comparator<Entity>() {
				@Override
				public int compare(Entity e1, Entity e2) {
					if(e1.value>e2.value){
						return 1;
					}else if(e1.value<e2.value){
						return -1;
					}
					return 0;
				}
			});
			for(int k=0;k<niche;k++){
				nicheNeighbors[i][k] = list.get(k).getIndex();
			}
		}
		return true;
	}
//	public boolean initialWeights(int obj_num_clone, int sub_num){
//		if(obj_num_clone!=2){
//			System.err.println("can't initialize the weights for the number of objective than 2");
//			return false;
//		}
//		for(int i=0;i<sub_num;i++){
//			for(int j=0;j<obj_num_clone;j++){
//				if(j==0){
//					weights[obj_num_clone*i] = (double)(sub_num-i)/(double)(sub_num);
//				}else{
//					weights[obj_num_clone*i+j] = 1 - weights[obj_num_clone*i];
//				}
//			}
//		}
//		return true;
//	}
	/**
	 * get the weight sum value
	 * @param w
	 * @param values
	 * @return
	 */
	public double getWsValue(int index, int [] values){
		double total_value = 0;
		for(int i=0;i<obj_num_clone;i++){
			total_value += weights[obj_num_clone*index+i]*(values[i]);
		}
		return total_value;
	}
	/**
	 * get the tch value
	 * @param w
	 * @param values
	 * @return
	 */
	public double getTchValue(int index, int [] values){
		double max_value = 0;
		for(int i=0;i<obj_num_clone;i++){
			if(weights[obj_num_clone*index+i]*Math.abs(values[i]-idealPoint[i])>max_value){
				max_value = weights[obj_num_clone*index+i]*Math.abs(values[i]-idealPoint[i]);
			}
		}
		return max_value;
	}
	/**
	 * get the pbi value
	 * @param w
	 * @param values
	 * @return
	 */
	public double getPbiValue(int index, int [] values){
		double d1, d2, nl;
		double theta = 5.0;

		d1 = d2 = nl = 0.0;

		for (int i = 0; i < this.obj_num_clone; i++) {
			d1 += (values[i] - idealPoint[i]) * weights[obj_num_clone*index+i];
			nl += Math.pow(weights[obj_num_clone*index+i], 2.0);
		}
		nl = Math.sqrt(nl);
		d1 = Math.abs(d1) / nl;

		for (int i = 0; i < this.obj_num_clone; i++) {
			d2 += Math.pow((values[i] - idealPoint[i]) - d1 * (weights[obj_num_clone*index+i] / nl), 2.0);
		}
		d2 = Math.sqrt(d2);
		return (d1 + theta * d2);
	}
}
