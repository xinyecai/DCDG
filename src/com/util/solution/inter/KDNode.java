package com.util.solution.inter;
/**
 * a struct used to be the node of kd-tree
 * @author heaven
 *
 */
public class KDNode<T extends Solution>{
	public T solution;
	/**
	 * the current coordinate position
	 */
	public int axis;
	/**
	 * the left node
	 */
	public KDNode<T> left;
	/**
	 * the right node
	 */
	public KDNode<T> right;
	/**
	 * the parent node
	 */
	public KDNode<T> parent;
	/**
	 * if the node is dominatd or not
	 */
	public boolean isDominated;

	public int [] route;
	
	public KDNode(){
		
	}
	
	public KDNode(T s) {
		this.solution = s;
		this.axis = 0;
		this.isDominated = false;
		this.route = s.route;
	}
}
