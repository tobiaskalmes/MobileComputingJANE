/*****************************************************************************
 * 
 * FixedPositioningService.java
 * 
 * $Id: FixedPositioningService.java,v 1.1 2007/06/25 07:23:46 srothkugel Exp $
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
package de.uni_trier.jane.service.positioning; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class FixedPositioningService implements RuntimeService, PositioningService{

    private double updateDelta;
    private boolean notifyOnChangedOnly;
    protected PositioningData fixedPosition;
    protected RuntimeOperatingSystem operatingSystem;

    /**
     * Constructor for class <code>FixedPositioningService</code>
     * @param updateDelta
     * @param notifyOnChangedOnly
     * @param fixedPosition
     */
    public FixedPositioningService(double updateDelta, boolean notifyOnChangedOnly, Position fixedPosition) {
        
        this.updateDelta=updateDelta;
        this.notifyOnChangedOnly=notifyOnChangedOnly;
        this.fixedPosition=new PositioningData(fixedPosition);
    }

    /**
     * Creates an instance of this service with the default values:
     * notify peridically with an update delta of one simulation second
     * 
     * @param serviceUnit
     * @param fixedPosition
     */
    public static void createInstance(ServiceUnit serviceUnit, Position fixedPosition) {
    	createInstance(serviceUnit, 1.0, false,fixedPosition );
    }

    /**
     * Creates an instance of this service with the given parameters
     * @param serviceUnit		
     * @param updateDelta			determine how often this service checks if the position information
     * 								has changed.
     * @param notifyOnChangedOnly	set this value true if position information has to be updated
     * 								at all listeners only if it has changed
     * @param fixedPosition			the fixed position
     */
    public static void createInstance(ServiceUnit serviceUnit, double updateDelta, boolean notifyOnChangedOnly, Position fixedPosition) {
    	Service simulationLocationSystem = new FixedPositioningService(updateDelta, notifyOnChangedOnly,fixedPosition);
    	serviceUnit.addService(simulationLocationSystem);
    }
    
    
    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        operatingSystem=runtimeOperatingSystem;
        operatingSystem.registerAccessListener(PositioningService.class);
        
        if (!notifyOnChangedOnly){
            runtimeOperatingSystem.setTimeout(new ServiceTimeout(updateDelta){
                
            

                public void handle() {
                    operatingSystem.sendSignal(new PositioningListener.UpdateLocationInfoSignal(fixedPosition));

                }
            });
        }
        
    }

    public ServiceID getServiceID() {
        // TODO Auto-generated method stub
        return null;
    }

    public void finish() {
        // TODO Auto-generated method stub
        
    }

    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

    public void getParameters(Parameters parameters) {
        // TODO Auto-generated method stub
        
    }

    public PositioningData getPositioningData() {
        return fixedPosition;
    }

}
