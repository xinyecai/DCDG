package com.util.tool;

import java.util.ArrayList;
import java.util.Comparator;

import com.util.bean.Entity;

public class MoeadHelper {
//	public static boolean initialWeights(int obj_num, int sub_num, int niche, double [] weights, int [][]nicheNeighbors){
//		if(obj_num!=2){
//			System.err.println("can't initialize the weights for the number of objective than 2");
//			return false;
//		}
//		for(int i=0;i<sub_num;i++){
//			for(int j=0;j<obj_num;j++){
//				if(j==0){
//					weights[obj_num*i] = (double)(sub_num-i)/(double)(sub_num);
//				}else{
//					weights[obj_num*i+j] = 1 - weights[obj_num*i];
//				}
//			}
//		}
//		//determine the niche neighbor of each solution
//		for(int i=0;i<sub_num;i++){
//			ArrayList<Entity> list = new ArrayList<>();
//			for(int j=0;j<sub_num;j++){
//				//if(i==j) continue;
//				double value = 0;
//				value += Math.pow(weights[i*2]-weights[j*2], 2);
//				value += Math.pow(weights[i*2+1]-weights[j*2+1], 2);
//				Entity e = new Entity();
//				e.setValue(value);
//				e.setIndex(j);
//				list.add(e);
//			}
//			list.sort(new Comparator<Entity>() {
//				@Override
//				public int compare(Entity e1, Entity e2) {
//					if(e1.value>e2.value){
//						return 1;
//					}else if(e1.value<e2.value){
//						return -1;
//					}
//					return 0;
//				}
//			});
//			for(int k=0;k<niche;k++){
//				nicheNeighbors[i][k] = list.get(k).getIndex();
//			}
//		}
//		return true;
//	}
//	public static boolean initialWeights(int obj_num, int sub_num, double [] weights){
//		if(obj_num!=2){
//			System.err.println("can't initialize the weights for the number of objective than 2");
//			return false;
//		}
//		for(int i=0;i<sub_num;i++){
//			for(int j=0;j<obj_num;j++){
//				if(j==0){
//					weights[obj_num*i] = (double)(sub_num-i)/(double)(sub_num);
//				}else{
//					weights[obj_num*i+j] = 1 - weights[obj_num*i];
//				}
//			}
//		}
//		return true;
//	}
//	/**
//	 * get the weight sum value
//	 * @param w
//	 * @param values
//	 * @return
//	 */
//	public static double getWsValue(int index, int obj_num, double [] weights, int [] values){
//		double total_value = 0;
//		for(int i=0;i<obj_num;i++){
//			total_value += weights[obj_num*index+i]*values[i];
//		}
//		return total_value;
//	}
//	/**
//	 * get the tch value
//	 * @param w
//	 * @param values
//	 * @return
//	 */
//	public static double getTchValue(int index, int obj_num, double [] weights, int [] values, int [] idealPoint){
//		double max_value = 0;
//		for(int i=0;i<obj_num;i++){
//			if(weights[obj_num*index+i]*Math.abs(values[i]-idealPoint[i])>max_value){
//				max_value = weights[obj_num*index+i]*Math.abs(values[i]-idealPoint[i]);
//			}
//		}
//		return max_value;
//	}
//	/**
//	 * get the pbi value
//	 * @param w
//	 * @param values
//	 * @return
//	 */
//	public static double getPbiValue(int index, double [] values){
//		
//		return 0;
//	}
}
