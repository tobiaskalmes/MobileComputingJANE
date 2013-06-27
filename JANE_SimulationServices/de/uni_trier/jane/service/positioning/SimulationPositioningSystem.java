/*****************************************************************************
 * 
 * SimulationPositioningSystem.java
 * 
 * $Id: SimulationPositioningSystem.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class utilizes global knowledge provided by the simulation in order to
 * return the most current position information. Register a
 * <code>SignalListener</code> implementing the
 * <code>PositioningListener</code> interface at this service to get
 * positioning updates. These updates can be send periodically or on change
 * only.
 * 
 * @see de.uni_trier.jane.service.positioning.PositioningListener
 */
public class SimulationPositioningSystem implements SimulationService, PositioningService {

    private static final ServiceID serviceID = new EndpointClassID(SimulationPositioningSystem.class.getName());

    // initialized in constructor
    private Position currentPosition;
    private Position currentDirection;
    private ServiceTimeout updateTimeout;
    private boolean notifyOnChangedOnly;
    private double updateDelta;
   

    // initialized on startup
    private SimulationOperatingSystem operatingService;
    private GlobalKnowledge globalKnowledge;
    private DeviceID address;

    /**
     * Creates an instance of this service with the default values:
     * notify peridically with an update delta of one simulation second
     * 
     * @param serviceUnit
     */
    public static void createInstance(ServiceUnit serviceUnit) {
    	createInstance(serviceUnit, 1.0, false);
    }

    /**
     * Creates an instance of this service with the given parameters
     * @param serviceUnit		
     * @param updateDelta			determine how often this service checks if the position information
     * 								has changed.
     * @param notifyOnChangedOnly	set this value true if position information has to be updated
     * 								at all listeners only if it has changed
     */
    public static void createInstance(ServiceUnit serviceUnit, double updateDelta, boolean notifyOnChangedOnly) {
    	Service simulationLocationSystem = new SimulationPositioningSystem(updateDelta, notifyOnChangedOnly);
    	serviceUnit.addService(simulationLocationSystem);
    }
    
    /**
     * Construct a new instance of this class.
     * @param updateDelta determine how often this service checks if the position information
     * has changed.
     * @param notifyOnChangedOnly set this value true if position information has to be updated
     * at all listeners only if it has changed
     */
    public SimulationPositioningSystem(double updateDelta, boolean notifyOnChangedOnly) {
        currentPosition = null;
        currentDirection = null;
        updateTimeout = new ServiceTimeout(updateDelta) {
            public void handle() {
                handleUpdate();
            }
    	};
    	this.notifyOnChangedOnly = notifyOnChangedOnly;
    	this.updateDelta = updateDelta;
    }

    public ServiceID getServiceID() {
        return serviceID;
    }

	public void start(SimulationOperatingSystem simulationOperatingSystem) {
		this.operatingService = simulationOperatingSystem;
		operatingService.registerAccessListener(PositioningService.class);
        address = operatingService.getDeviceID();
    	operatingService.setTimeout(updateTimeout);
    	globalKnowledge = operatingService.getGlobalKnowledge();
        currentPosition = globalKnowledge.getTrajectory(address).getPosition();
        currentDirection = globalKnowledge.getTrajectory(address).getDirection();
	}

    public void finish() {
        // ignore
    }

    public Shape getShape() {
        return null;
    }

	public void getParameters(Parameters parameters) {
		parameters.addParameter("updateDelta", updateDelta);
		parameters.addParameter("notifyOnChangedOnly", notifyOnChangedOnly);
	}

	public PositioningData getPositioningData() {
        return new PositioningData(currentPosition, currentDirection);
	}

    // Possibly notify all listeners the about new location information
    private void handleUpdate() {
        if(storeNewPosition()) {
            PositioningData info = new PositioningData(currentPosition, currentDirection);
            Signal signal = new PositioningListener.UpdateLocationInfoSignal(info);
            operatingService.sendSignal(signal);
        }
        operatingService.setTimeout(updateTimeout);
    }

    // Store the new location information and return if device location has changed.
    private boolean storeNewPosition() {
        Trajectory trajectory = globalKnowledge.getTrajectory(address);
        Position position = trajectory.getPosition();
        if(notifyOnChangedOnly && currentPosition != null && currentPosition.equals(position)) {
            return false;
        }
        currentPosition = position;
        currentDirection = trajectory.getDirection();
        return true;
    }

}
