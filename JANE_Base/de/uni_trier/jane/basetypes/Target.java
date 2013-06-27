/*****************************************************************************
 * 
 * Target.java
 * 
 * $Id: Target.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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

import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * TODO: comment class  
 * @author daniel
 **/

public interface Target extends Serializable{//extends ID  {

    
    /**
     * Returns the coding size of this target in bits
     * @return coding size in bits
     */
    public abstract int getCodingSize();
    
    
    
    
    /**
     * Returns a visualization shape of this target
     * @param address TODO
     * @param color 		the color
     * @param filled  		true, if the shape should be filled
     * @return the shape
     */
    public abstract Shape getShape(DeviceID address,Color color, boolean filled);
}