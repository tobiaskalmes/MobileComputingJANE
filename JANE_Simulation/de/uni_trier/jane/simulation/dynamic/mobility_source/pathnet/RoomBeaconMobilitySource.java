/*****************************************************************************
 * 
 * RoomBeaconMobilitySource.java
 * 
 * $Id: RoomBeaconMobilitySource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.*;
import de.uni_trier.jane.simulation.kernel.Condition;
import de.uni_trier.jane.visualization.shapes.*;

public class RoomBeaconMobilitySource implements MobilitySource {

    private Position[] positions;
    private int count;
    private Rectangle rectangle;

    public RoomBeaconMobilitySource(Campus campus, int beaconsPerRoom) {
        String[] locations=campus.getLocationNames();
        rectangle=campus.getRectangle();
        positions=new Position[locations.length];
        for (int i=0;i<locations.length;i++){
            
            try {
                positions[i]=campus.getLocationRectangle(locations[i]).getCenter();
            } catch (UnknownLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean hasNextEnterInfo() {

        return count<positions.length;
    }

    public EnterInfo getNextEnterInfo() {
        int number=count++;
        return new EnterInfo(new SimulationDeviceID(number+1),-1,
                new ArrivalInfo(positions[number],0));
    }

    public boolean hasNextArrivalInfo(DeviceID address) {

        return true;
    }

    public ArrivalInfo getNextArrivalInfo(DeviceID address) {
        
        return new ArrivalInfo(positions[(int)((SimulationDeviceID)address).getLong()],Double.POSITIVE_INFINITY);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Shape getShape() {
        
        return EmptyShape.getInstance();
    }

    public int getTotalDeviceCount() {

        return positions.length;
    }

    public Condition getTerminalCondition(Clock clock) {
        // TODO Auto-generated method stub
        return null;
    }

	public double getMinimumTransmissionRange() {
		return -1;
	}

	public double getMaximumTransmissionRange() {
		return -1;
	}


}
