package com.util.set.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.util.set.inter.ExSetInter;
import com.util.solution.inter.KDNode;
import com.util.solution.inter.Solution;
//import net.sf.cglib.beans.BeanCopier;
/**
 * implement kd-tree to store the non-dominated solutions
 * if can accelerate the process
 * @author heaven
 *
 * @param <T>
 */
public class KDTreeSet<T extends Solution> implements ExSetInter<T>{
	/**
	 * the root node of the tree
	 */
	private KDNode<T> root = null;
	/**
	 * the dimension of the tree
	 */
	private int dimension;
	/**
	 * store all nodes of the tree
	 */
	private List<KDNode<T>> KDList = new ArrayList<>();
	
	//private BeanCopier copier;
	
	public KDTreeSet(int dimension){
		this.dimension = dimension;
		//this.copier = BeanCopier.create(KDNode.class, KDNode.class, false);
	}
	
	/**
	 * find the parent of the given node
	 * @param node
	 * @return
	 */
	private KDNode<T> findParent(KDNode<T> node){
		KDNode<T> parent = null;
		KDNode<T> next = root;
		int split = 0;
		while(next!=null){
			split = next.axis;
			parent = next;
			if(node.solution.getType()*node.solution.object_val[split]<=next.solution.getType()*next.solution.object_val[split]){
				next = next.left;
			}else{
				next = next.right;
			}
		}
		return parent;
	}
	/**
	 * mark the dominated nodes
	 * @param node
	 */
	private void markDominatedNodes(KDNode<T> node){
		this.markDominatedNodes(root, node);
	}
	/**
	 * mark the dominated nodes
	 * @param root
	 * @param node
	 */
	private void markDominatedNodes(KDNode<T> root, KDNode<T> node){
		Solution s1 = root.solution;
		Solution s2 = node.solution;
		boolean ret = true;
		//if any objectives is greater than the root, thus it won't be dominated
		for(int i=0;i<dimension;i++){
			if(s1.getType()*s1.object_val[i] > s2.getType()*s2.object_val[i]){
				ret = false;
				break;
			}
		}
		//mark the node as dominated
		if(ret==true){
			root.isDominated = true;
		}
		int curAxis = root.axis;
		//come into left child
		if(s2.getType()*s2.object_val[curAxis] <= s1.getType()*s1.object_val[curAxis]){
			if(root.left!=null){
				this.markDominatedNodes(root.left, node);
			}
		}else{//come into right child
			if(root.left!=null){
				this.markDominatedNodes(root.left, node);
			}
			if(root.right!=null){
				this.markDominatedNodes(root.right, node);
			}
		}
	}
	/**
	 * add a solution to this set
	 */
	@Override
	public boolean add(T s) {
		KDNode<T> node = new KDNode<T>(s);
		if(root==null){
			root = node;
			root.axis = 0;
			this.KDList.add(root);
		}else{
			boolean ret = this.isDominated(node);
			if(ret==true){
				return false;
			}
			this.markDominatedNodes(node);
			this.insert(node);
			this.KDList.add(node);
		}
		return true;
	}
	/**
	 * insert a node into the tree
	 * @param node
	 */
	private void insert(KDNode<T> node){
		KDNode<T> parent = this.findParent(node);
		int curAxis = parent.axis;
		node.axis = curAxis + 1 < dimension ? curAxis + 1 : 0;
		node.parent = parent;
		if(node.solution.getType()*node.solution.object_val[curAxis] > parent.solution.getType()*parent.solution.object_val[curAxis]){
			parent.right = node;
		}else{
			parent.left = node;
		}
	}
	private boolean isDominated(KDNode<T> node){
		return this.isDominated(root, node);
	}
	/**
	 * determine if node is dominated by any nodes of the tree 
	 * @param root
	 * @param node
	 * @return
	 */
	private boolean isDominated(KDNode<T> root, KDNode<T> node){
		Solution s1 = root.solution;
		Solution s2 = node.solution;
		boolean ret = true;
		//if any objectives is greater than the root, thus it won't be dominated
		for(int i=0;i<dimension;i++){
			if(s1.getType()*s1.object_val[i] < s2.getType()*s2.object_val[i]){
				ret = false;
				break;
			}
		}
	
		//node is dominated by root
		if(ret == true){
			return true;
		}
		//the current axis that should be taken
		int curAxis = root.axis;
		//come into left child
		if(s2.getType()*s2.object_val[curAxis] <= s1.getType()*s1.object_val[curAxis]){
			if(root.left!=null){
				boolean leftRet = isDominated(root.left, node);
				if(leftRet==true){
					return true;
				}
			}
			if(root.right!=null){
				return this.isDominated(root.right, node);
			}
		}else{//come into right child
			//if the right child is not exist, thus the node can be located here
			//it also shows that the node is no-dominated
			if(root.right==null){
				return false;
			}
			return this.isDominated(root.right, node);
		}
		return false;
	}
	@Override
	public void add(List<T> list) {
		for(T t : list){
			this.add(t);
		}
	}
	
	/**
	 * this function is time consume, thus, you should
	 * avoid to use it
	 */
	@Override
	public int getPopSize() {
		int count = 0;
		for(KDNode<T> node : KDList){
			if(node.isDominated==false){
				count++;
			}
		}
		return count;
	}

	@Override
	public void saveResults() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<T> getSolutions() {
		List<T> solutions = new ArrayList<T>();
		for(KDNode<T> node : KDList){
			if(node.isDominated==false)
			solutions.add((T) node.solution);
		}
		return solutions;
	}
	/**
	 * get all nodes
	 * @return
	 */
	public List<KDNode<T>> getKDNodes(){
		return this.KDList;
	}
	/**
	 * clear the kd-tree and reset it
	 */
	public void clearAndReset(){
		List<T> tempList = new ArrayList<>();
		for(KDNode<T> node : KDList){
			if(node.isDominated) continue;
			tempList.add(node.solution);
		}
		this.KDList.clear();
		this.root = null;
		this.add(tempList);
	}
	/**
	 * clear the tree
	 */
	@Override
	public void clear() {
		this.KDList.clear();
		this.root = null;
	}
	/**
	 * save the results to the file
	 */

	@Override
	public void saveObjectiveResults(String fileName, boolean append) {
		try {
			FileWriter fw = new FileWriter(fileName, append);
			for(KDNode<T> node : KDList){
				if(node.isDominated) continue;
				Solution s = node.solution;
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
		try {
			FileWriter fw = new FileWriter(fileName, false);
			for(KDNode<T> node : KDList){
				if(node.isDominated) continue;
				for(int i=0;i<node.route.length;i++){
					fw.write(String.valueOf(node.route[i]));
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
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
