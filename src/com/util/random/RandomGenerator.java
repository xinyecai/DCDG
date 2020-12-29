package com.util.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class RandomGenerator {
	/**
	 * generate a permutation from begin to end
	 * @param begin
	 * @param end
	 * @return
	 */
	public static int [] permutation_array(int begin, int end){
		int len = end - begin + 1;
		int [] array = new int[len];
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		for(int i=begin;i<=end;i++){
			tempList.add(i);
		}
		Collections.shuffle(tempList);
		for(int i=begin;i<=end;i++){
			array[i] = tempList.get(i);
		}
		return array;
	}
	public static ArrayList<Integer> permutation_arrayList(int begin, int end){
		ArrayList<Integer> retList = new ArrayList<Integer>();
		for(int i=begin;i<=end;i++){
			retList.add(i);
		}
		Collections.shuffle(retList);
		return retList;
	}
	public static LinkedList<Integer> permutation_linkList(int begin, int end){
		LinkedList<Integer> retList = new LinkedList<Integer>();
		for(int i=begin;i<=end;i++){
			retList.add(i);
		}
		Collections.shuffle(retList);
		return retList;
	}
}
