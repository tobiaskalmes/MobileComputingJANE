/*****************************************************************************
 * 
 * PositionDeviceMap.java
 * 
 * $Id: PositionDeviceMap.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.mobility_source; 

import java.util.*;

import de.uni_trier.jane.basetypes.*;



public class PositionDeviceMap{
	private TreeMap xMap;
	private TreeMap yMap;
    private HashMap addressPositionMap;
	private class PositionEntity implements Comparable{
		private Double position;
		private DeviceID address;
		private static final int PRIME = 1001;
		
		/**
		 * @param position
		 * @param address
		 */
		public PositionEntity(double position, DeviceID address) {
			this.position = new Double(position);
			this.address = address;
		}
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (obj==null) return false;
			if (obj==this) return true;
			if (!getClass().isInstance(obj)) return false;
			PositionEntity positionEntity=(PositionEntity)obj;
			return position==positionEntity.position&&address.equals(positionEntity.address);
		}
		
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			int result = 0;
			int doubleHash=position.hashCode();
			result = PRIME*result + (int)(doubleHash >>> 32);
			result = PRIME*result + (int)(doubleHash & 0xFFFFFFFF);		
			result = PRIME*result + (int)(address.hashCode() >>> 32);
			result = PRIME*result + (int)(address.hashCode() & 0xFFFFFFFF);
			return result;
		}
		
		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			PositionEntity positionEntity=(PositionEntity)o;
			if (position.equals(positionEntity.position)){
				return address.compareTo(positionEntity.address);
			}else{
				return position.compareTo(positionEntity.position);
			}
		}
	}
	
	public PositionDeviceMap(){
		xMap=new TreeMap();
		yMap=new TreeMap();
		addressPositionMap=new HashMap();
	}
	
	public void addDevice(DeviceID address, Position position){
		
		
		addressPositionMap.put(address,position);
		
		xMap.put(new PositionEntity(position.getX(),address),address);
		
		yMap.put(new PositionEntity(position.getY(),address),address);
	}
	
	public void removeDevice(DeviceID address){
	    Position position=(Position) addressPositionMap.remove(address);
		xMap.remove(new PositionEntity(position.getX(),address));
		
		yMap.remove(new PositionEntity(position.getY(),address));
	}
	
	public DeviceID[] getDevices(Rectangle rectangle){
		Set subXset=new HashSet(xMap.subMap(
				new PositionEntity(rectangle.getBottomLeft().getX(),new SimulationDeviceID(-1)),
				new PositionEntity(rectangle.getTopRight().getX(), new SimulationDeviceID(Integer.MAX_VALUE))).values());
		
		Collection subYCollection=yMap.subMap(
				new PositionEntity(rectangle.getBottomLeft().getY(),new SimulationDeviceID(-1)),
				new PositionEntity(rectangle.getTopRight().getY(), new SimulationDeviceID(Integer.MAX_VALUE))).values();
		subXset.retainAll(subYCollection);
		
		return (DeviceID[])subXset.toArray(new DeviceID[subXset.size()]);
	}
	
	
	
	
}