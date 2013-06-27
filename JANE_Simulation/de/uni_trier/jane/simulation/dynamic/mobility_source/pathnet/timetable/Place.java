/*****************************************************************************
 * 
 * PathNetNode.java
 * 
 * $Id: Place.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable;



/**
 *This class represents a location for a timetable event.
 *It has a unique name and a probability for selecting this location.  
 *
 */
class Place implements Comparable {
	private String name;
	private double probability;
	/**
	 * Constructor for class <code>Place</code>
	 * @param name	
	 * @param probability
	 */
	public Place(String name, double probability) {
		this.name = name;
		this.probability = probability;
	}
	
	
	
    /**
     * @return Returns the probability.
     */
    public double getProbability() {
        return probability;
    }
    /**
     * @param probability The probability to set.
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other.getClass() != getClass()) {
			return false;
		}
		Place oth = (Place) other;
		if (!name.equals(oth.name)) {
			return false;
		}	
		if (probability != oth.probability) {
			return false;
		}				
		return true;
	}
	
	public int compareTo(Object other) {
		if (other.getClass() != getClass()) {
			throw new ClassCastException("Can't compare Place with "+other.getClass().getName());
		}
		Place oth = (Place) other;
		if (oth.probability > probability) {
			return -1;
		} else if (oth.probability == probability) {
			return 0;
		} else {
			return 1;
		}
	}
}