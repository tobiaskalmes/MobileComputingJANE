/*
 * Created on 03.07.2005
 */
package de.uni_trier.jane.basetypes;

/**
 * @author Klaus Sausen
 * associates point indeces to form a face
 */
public class Face {

	int[] indeces;

	public Face(int idx0, int idx1, int idx2) {
		indeces = new int[3];
		indeces[0] = idx0;
		indeces[1] = idx1;
		indeces[2] = idx2;
	}

	public Face(int idx0, int idx1, int idx2, int idx3) {
		indeces = new int[4];
		indeces[0] = idx0;
		indeces[1] = idx1;
		indeces[2] = idx2;
		indeces[3] = idx3;
	}
	
	public int getIndex(int idx) {
		return indeces[idx];
	}
}
