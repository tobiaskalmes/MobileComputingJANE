/*****************************************************************************
 * 
 * Estimate.java
 * 
 * $Id: Estimate.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2003 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.util.dijkstra;

// used (inside this package) by DijkstraÄs single source shortest path algorithm and result
class Estimate implements Comparable {
	private double weight;
	private int address;
	public Estimate(double weight, int address) {
		this.weight = weight;
		this.address = address;
	}
	public double getWeight() {
		return weight;
	}
	public int getAddress() {
		return address;
	}
	public int compareTo(Object object) {
		Estimate other = (Estimate)object;
		if(weight < other.weight) {
			return -1;
		}
		else if(weight > other.weight) {
			return 1;
		}
		else {
			if(address < other.address) {
				return -1;
			}
			else if(address > other.address) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
	public int hashCode() {
		long bits = Double.doubleToLongBits(weight);
		int a = (int)(bits ^ (bits >>> 32));
		int c = address;
		return a ^ c;
	}
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		else if (object == null) {
			return false;
		}
		else if (object.getClass() != getClass()) {
			return false;
		}
		else {
			Estimate other = (Estimate)object;
			return weight == other.weight && address == other.address;
		}
	}
}		
