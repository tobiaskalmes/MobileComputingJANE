/*****************************************************************************
 * 
 * FibonacciHeap.java
 * 
 * $Id: FibonacciHeap.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen  and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.simulation.dynamic;

import java.util.*;

/**
 * This class implements the fibonacci heap data structure.
 */
public class FibonacciHeap {
	private Comparator comparator;
	private int numberOfNodes;
	private Node minimumNode;

	/**
	 * Constructs a new FibonacciHeap object with no special comparator.
	 * In this case all elements of the heap should implement Comparable.
	 */
	public FibonacciHeap() {
		this(null);
	}
		
	/**
	 * Constructs a new FibonacciHeap object with the comparator supplied.
	 * All elements in the heap will be compared using this comparator.
	 * @param comparator the comparator to use to compare elements in the heap.
	 */
	public FibonacciHeap(Comparator comparator) {
		this.comparator = comparator;
		numberOfNodes = 0;
		minimumNode = null;
	}
	
	/**
	 * Method isEmpty.
	 * @return boolean
	 */
	public boolean isEmpty() {
		return numberOfNodes == 0;
	}

	/**
	 * Inserts a new element in the heap.
	 * @param object the element to insert.
	 */
	public void insert(Object object) {
		Node node = new Node(object);
		if (minimumNode != null) {
			minimumNode.right.left = node;
			node.right = minimumNode.right;
			minimumNode.right = node;
			node.left = minimumNode;
		}
		if (minimumNode == null || compare(object, minimumNode.data) < 0) {
			minimumNode = node;
		}
		numberOfNodes++;
	}
	
	/**
	 * Returns the minimum element in the heap but does not remove it from the
	 * heap.
	 * @return Object the minimum element in the heap.
	 * @throws IllegalStateException if the heap is empty
	 */
	public Object minimum() {
		if (isEmpty()) {
			throw new IllegalStateException("Empty heap!");
		}
		return minimumNode.data;
	}
	
	/**
	 * Extracts the smallest element from the heap.
	 * @return Object the smallest element
	 * @throws IllegalStateException if the heap is empty
	 */
	public Object extractMinimum() {
		if (isEmpty()) {
			throw new IllegalStateException("Empty heap!");
		}		
		Node z = minimumNode;
		if (z.child != null) {
			Node child = z.child;
			Node tmp = child;
			do {
				tmp.parent = null;
				tmp = tmp.right;
			} while (tmp != child);
			// add child list to the root list
			minimumNode.left.right = child.right;
			child.right.left = minimumNode.left;
			minimumNode.left = child;
			child.right = minimumNode;
		}
		// remove z from the root list
		z.left.right = z.right;
		z.right.left = z.left;
		if (z == z.right) {
			minimumNode = null;				
		} else {
			minimumNode = z.right;
			consolidate();
		}
		numberOfNodes--;
		return z.data;
	}
	
	private void consolidate() {
		int dnh = (int)Math.floor(Math.sqrt(numberOfNodes))+2; // FIXME!! 
		Node[] a = new Node[dnh];		
		Node start = minimumNode;
		Node tmp = start;
		tmp = start;
		do {
			Node x = tmp;
			int d = x.degree;	
			if (a[d] != x) {		
				while(a[d] != null) {
					Node y = a[d];
					if (compare(x.data,y.data) > 0) {
						Node t = x;
						x = y;
						y = t;
					}
					heapLink(y,x);
					start = x;
					tmp = x;
					a[d] = null;
					d++;
				}
				a[d] = x;
			}		
			tmp = tmp.right;				
		} while (tmp != start);
		minimumNode = start;
		tmp = start;
		do {
			if(compare(tmp.data, minimumNode.data)<0) {
				minimumNode = tmp;
			}
			tmp = tmp.right;
		} while ( tmp != start );		
	}

	private void heapLink(Node y, Node x) {
		// remove y from root list
		y.left.right = y.right;
		y.right.left = y.left;
		// make y child of x, increment degree
		if (x.child == null) {
			x.child = y;
			y.right = y;
			y.left = y;
		} else {
			y.right = x.child.right;
			y.left = x.child;
			x.child.right.left = y;
			x.child.right = y;			
		}
		y.parent = x;
		x.degree++;
		y.mark = false;
	}

	private int compare(Object o1, Object o2) {
		if (comparator != null) {
			return comparator.compare(o1, o2);
		} else {
			Comparable c1 = (Comparable) o1;
			Comparable c2 = (Comparable) o2;
			return c1.compareTo(c2);
		}
	}

	private static class Node {
		/**
		 * Comment for <code>degree</code>
		 */
		public int degree;
		/**
		 * Comment for <code>parent</code>
		 */
		public Node parent;
		/**
		 * Comment for <code>child</code>
		 */
		public Node child;
		/**
		 * Comment for <code>left</code>
		 */
		public Node left;
		/**
		 * Comment for <code>right</code>
		 */
		public Node right;
		/**
		 * Comment for <code>mark</code>
		 */
		public boolean mark;
		/**
		 * Comment for <code>data</code>
		 */
		public Object data;
		
		/**
		 * @param data
		 */
		public Node(Object data) {
			this.data = data;
			degree = 0;
			parent = null;
			child = null;
			left = this;
			right = this;
			mark = false;
		}
	}
}
