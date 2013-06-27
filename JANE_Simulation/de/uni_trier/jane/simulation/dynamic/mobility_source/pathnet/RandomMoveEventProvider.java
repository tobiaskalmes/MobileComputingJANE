/*****************************************************************************
 * 
 * RandomMoveEventProvider.java
 * 
 * $Id: RandomMoveEventProvider.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable.*;
import de.uni_trier.jane.simulation.gui.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class RandomMoveEventProvider implements MoveEventProvider {
    private LocationSelect locationSelect;
	private ContinuousDistribution pauseDistribution;
    private boolean first;
    
    

    /**
     * Constructor for class <code>RandomMoveEventProvider</code>
     * @param locationSelect
     * @param pauseDistribution
     */
    public RandomMoveEventProvider(LocationSelect locationSelect,
            ContinuousDistribution pauseDistribution) {
        this.locationSelect = locationSelect;
        this.pauseDistribution = pauseDistribution;
        first=true;
    }
    
    public DeviceTarget getNextTarget() {
        
        return new DeviceTarget(locationSelect.next(),false);
    }


    public boolean hasNextTarget() {

        return true;
    }

    public double lastPathFinished(double currentTime) {
        if (first){
            //no initial pause...
            first=false;
            return currentTime;
        }
        return currentTime+pauseDistribution.getNext();
    }

    public boolean hasNewTarget() {
        return true;
    }

}
