/*****************************************************************************
 * 
 * Extent.java
 * 
 * $Id: Extent.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.basetypes;

import java.io.*;

/**
 * This class consists of a width and height value.
 */
public class Extent implements Serializable{

	private final static String VERSION = "$Id: Extent.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	/**
	 * This is the empty extent (0,0).
	 */
	public static final Extent NULL_EXTENT = new Extent(0, 0);

	private double width;
	private double height;

	/**
	 * Construct a new <code>Extent</code> value.
	 * @param width the with
	 * @param height the height
	 */
	public Extent(double width, double height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Get the width value.
	 * @return the width value
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Get the height value.
	 * @return the height value
	 */
	public double getHeight() {
		return height;
	}
	

	
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        long temp = Double.doubleToLongBits(width);
        result = PRIME * result + (int) (temp >>> 32);
        result = PRIME * result + (int) (temp & 0xFFFFFFFF);
        temp = Double.doubleToLongBits(height);
        result = PRIME * result + (int) (temp >>> 32);
        result = PRIME * result + (int) (temp & 0xFFFFFFFF);

        return result;
    }

    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth == null) {
            return false;
        }

        if (oth.getClass() != getClass()) {
            return false;
        }

        Extent other = (Extent) oth;

        if (this.width != other.width) {
            return false;
        }

        if (this.height != other.height) {
            return false;
        }

        return true;
    }
    
    //
    public String toString() {
    
        return "(w"+width+":h"+height+")";
    }
    
}
