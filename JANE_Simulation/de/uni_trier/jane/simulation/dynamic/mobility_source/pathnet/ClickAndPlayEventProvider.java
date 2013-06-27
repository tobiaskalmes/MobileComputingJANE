/*****************************************************************************
 * 
 * ClickAndPlayEventProvider.java
 * 
 * $Id: ClickAndPlayEventProvider.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.gui.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class ClickAndPlayEventProvider implements MoveEventProvider {

    /**
     * @author goergen
     *
     * TODO comment class
     */
    private static final class Location {

        private String name;
        private Rectangle rectangle;

        /**
         * Constructor for class <code>Location</code>
         * @param rectangle
         * @param name
         * 
         */
        public Location(String name, Rectangle rectangle) {
            this.name=name;
            this.rectangle=rectangle;
        }
        
        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }
        
        public boolean isInside(Position position){
            return rectangle.contains(position);
        }

    }
    
    private DeviceTarget curentTarget;
    private double movementSteps;
    private HashSet locationSet;
    private boolean newTarget;





    /**
     * Constructor for class <code>ClickAndPlayEventProvider</code>
     * @param campus
     * @param movementSteps
     */
    public ClickAndPlayEventProvider(Campus campus, double movementSteps, DeviceTarget initialTarget) {
        curentTarget=initialTarget;
        if (movementSteps<=0) throw new IllegalStateException("movement step delta must be greater than 0");
        this.movementSteps=movementSteps;
        newTarget=true;
        String names[]=campus.getLocationNames();
        locationSet=new HashSet();
        for (int i=0;i<names.length;i++){
            Location location;
            try {
                location = new Location(names[i],campus.getLocationRectangle(names[i]));
                locationSet.add(location);
            } catch (UnknownLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           
            
        }
    }


    public DeviceTarget getNextTarget() {
        newTarget=false;
        return curentTarget;
    }

    public boolean hasNewTarget(){
        return newTarget;
    }
    

    public boolean hasNextTarget() {
        return true;
    }

    

    public double lastPathFinished(double currentTime) {
        return currentTime+movementSteps;
    }


    /**
     * TODO Comment method
     * @param newPosition
     */
    public void setPosition(Position newPosition) {
        Iterator iterator=locationSet.iterator();
        newTarget=true;
        while (iterator.hasNext()) {
            Location element = (Location) iterator.next();
            if (element.isInside(newPosition)){
                curentTarget=new DeviceTarget(element.getName(),false);
                return;
            }
            
        }
        
    }


}
