package de.uni_trier.jane.tools.pathneteditor.tools;

/**
 * ;-)
 */
public class Pair {
	public int min = 0;
	public int max = 0;
	
	/*
	 * a bit more than this: a constructor !!!
	 */
	public Pair(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	/*
	 * and because its so nice: another one :D
	 */
	public Pair() {}
	
	/*
	 * Some nice output
	 */
	public String toString() {
		return "["+min+","+max+"]";
	}
}
