/*****************************************************************************
* 
* Data.java
* 
* $Id: Data.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
*
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006 
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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
 * Implement this interface in order to provide the beacon service with the desired
 * beaconing information to be transmitted periodically (e.g. the device position).
 */
public interface Data extends Serializable{

    /**
     * Get the data ID of this data object.
     * @return the data ID
     */
    public DataID getDataID();

    /**
     * Create a copy of this beacon data. You may return 'this' if the beacon data object
     * is immutable.
     * @return the copy of the beacon data
     */
   //public Data copy();
    
    /**
     * Get the size in Bits of the data stored in this beacon data object.
     * @return the data size in Bits
     */
    public int getSize();

}
