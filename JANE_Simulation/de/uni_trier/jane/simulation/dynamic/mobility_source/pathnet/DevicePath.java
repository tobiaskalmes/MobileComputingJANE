/*****************************************************************************
 * 
 * DevicePath.java
 * 
 * $Id: DevicePath.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet;

import java.util.*;

import de.uni_trier.jane.basetypes.*;

/**
 * This class represents a a devices path between two path net locations.
 * The path can be extendet by hop positions. The device is therfore moved linear between each hop.
 * During movement, the method nextHop is called until the path is finished. 
 */
public class DevicePath {

	private static final class HopElement {
	    private String name;
        private Position position;
        
        /**
         * Constructor for class <code>HopElement</code>
         *
         * @param name
         * @param position
         */
        public HopElement(String name, Position position) {
        
            this.name = name;
            this.position = position;
        }
        
        public String getName() {
            return name;
        }
        public Position getPosition() {
            return position;
        }
        
    }

    private ArrayList hops;
    private Map hopMap;
	
	/**
	 * Constructor for class <code>DevicePath</code>
	 *
	 */
	public DevicePath(){
		hops=new ArrayList();
        hopMap=new HashMap();
	}
	
	/**
	 * Adds a hop with the given position to the DevicePath
	 * @param position	the <code>position</code> of the next hop
	 */
	public void addNextHop(Position position) {
        
	    addNextHop(position,null);
        
        
        
	}
    


	/**
	 * Returns true, when the path is finished 
	 * @return	true when the path is finished
	 */
	public boolean isFinished() {
		return hops.isEmpty();
	}

	/**
	 * Removes the next hop position for the device from the device path and returns it
	 * @return	the <code>Position</code> of the next device hop on the device path
	 */
	public Position getNextHop() {
		return ((HopElement)hops.remove(0)).getPosition();
	}

    public void addNextHop(Position position, String name) {
        if (hopMap.containsKey(name)){
            int firstHop=((Integer)hopMap.get(name)).intValue();
            while(hops.size()>firstHop+1){
                HopElement element=(HopElement)hops.remove(firstHop+1);
                hopMap.remove(element.getName());
            }
        }else{
            if (name!=null){
                hopMap.put(name,new Integer(hops.size()));
            }
            hops.add(new HopElement(name,position));
        }
        
        
    }
}
