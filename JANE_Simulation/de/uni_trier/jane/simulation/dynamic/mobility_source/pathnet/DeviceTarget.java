/*****************************************************************************
 * 
 * DeviceTarget.java
 * 
 * $Id: DeviceTarget.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet; 


/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeviceTarget {
	
	

	private String placeName;
	private boolean suspend;
    //private double endTime;

	/**
	 * @param placeName
	 * @param endTime
	 * @param suspend
	 */
	public DeviceTarget(String placeName, boolean suspend) {
		this.placeName=placeName;
		this.suspend=suspend;
		
	}
	
	
	
    public String toString() {
        return placeName;
    }
    
	/**
	 * @return Returns the placeName.
	 */
	public String getPlaceName() {
		return placeName;
	}
	/**
	 * @return Returns the suspend.
	 */
	public boolean suspend() {
		return suspend;
	}

}