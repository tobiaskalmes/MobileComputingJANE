/*****************************************************************************
* 
* ReceivedDataMap.java
* 
* $Id: ReceivedDataMap.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;

import java.io.*;

/**
 * TODO: comment!!!
 * This interface descibes a collection of all information about a device
 * which is currently known to this service.
 */
public class ReceivedDataMap implements Serializable{

	
	protected double timeStamp;
    private double validityDelta;
   
	protected DataMap dataMap;
    private LinkLayerInfo receiveInfo;
    
	
	/**
     * 
     * Constructor for class <code>ReceivedDataMap</code>
     *
     * @param receiveInfo
     * @param timeStamp
     * @param validityDelta
     * @param dataMap
	 */
	public ReceivedDataMap(LinkLayerInfo receiveInfo, double timeStamp, double validityDelta, DataMap dataMap) {
		
		this.timeStamp = timeStamp;
        this.receiveInfo=receiveInfo;
        this.validityDelta=validityDelta;
		this.dataMap = dataMap;
	}
	
    /**
     * Get the address of the device this information belongs to.
     * @return the device address
     */
    public Address getSender() {
    	return receiveInfo.getSender();
    }

    /**
     * Get the time value of this device info. Depending on the implementation this may be the receiving or
     * the sending time of this information.
     * @return the time stamp value
     */
    public double getTimeStamp() {
    	return timeStamp;
    }
    
    /**
     * Get the validity delta of this device info. Depending on the implementation 
     * this may be the local or the received deletion delta
     * @return the validity delta
     */
    public double getValidityDelta() {
        return validityDelta;
    }

    /**
     * Get all data stored in this data structure.
     * @return the data IDs
     */
    public DataMap getDataMap() {
    	return dataMap;
    }
    
    
    /**
     * 
     * Returns the last received LinkLayerInfo of this device   
     * @return the LinklayerInfo
     */
    public LinkLayerInfo getReceiveInfo() {
        return receiveInfo;
    }
    

//     private void writeObject(java.io.ObjectOutputStream out)
//         throws IOException{
//         out.defaultWriteObject();
//         out.writeDouble(timeStamp);
//         dataMap.write(out);
//         
//     }
//     private void readObject(java.io.ObjectInputStream in)
//         throws IOException, ClassNotFoundException{
//         in.defaultReadObject();
//         timeStamp=in.readDouble();
//         dataMap=new DefaultDataMap();
//         dataMap.read(in);
//         
//     }
    //
    public String toString() {
        return receiveInfo.getSender().toString();
    }

}
