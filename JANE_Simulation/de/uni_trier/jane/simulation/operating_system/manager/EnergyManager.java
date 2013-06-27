/*****************************************************************************
 * 
 * EnergyManager.java
 * 
 * $Id: EnergyManager.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.operating_system.manager;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.simulation.DefaultSimulationParameters;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.operating_system.*;

/**
 * 
 * @author goergen
 *
 * TODO comment class
 */
public class EnergyManager {

	private EventSet eventSet;

    private double totalEnergy;
    private Map idConsumptionMap;
    private Event emptyEvent;
    private double lastMeasureTime;

    /**
     * 
     * Constructor for class <code>EnergyManager</code>
     * @param initializer
     * @param totalEnergy
     */
    public EnergyManager(DefaultSimulationParameters initializer) {
    	this.eventSet = initializer.getEventSet();
        
        this.totalEnergy = initializer.getTotalDeviceEnergy();
        idConsumptionMap = new HashMap();
        emptyEvent = null;
        lastMeasureTime = 0.0;
    }

    /**
     * 
     * TODO Comment method
     * @param runningService
     * @param watt
     */
    public void setCurrentEnergyConsumption(ServiceID runningService,double watt) {
        if(watt < 0.0) {
            throw new OperatingServiceException("Energy consumption has to be zero or positive");
        }
        if(emptyEvent != null) {
        	emptyEvent.disable();
        }
        totalEnergy -= getRemainingEnergy();
        lastMeasureTime = eventSet.getTime();
        
        
        Double consumption = new Double(watt);
        idConsumptionMap.put(runningService, consumption);
        
        double newConsumption = calculateCurrentConsumption();
        if(newConsumption > 0.0) {
        	double currentTime = eventSet.getTime();
            double emptyTime = currentTime + totalEnergy / newConsumption;
            emptyEvent = new EmptyEvent(emptyTime);
            eventSet.add(emptyEvent);
        }

    }

    public double getRemainingEnergy() {
        double time = eventSet.getTime();
        double currentConsumption = calculateCurrentConsumption();
        double energyConsumed = (time - lastMeasureTime) * currentConsumption;
        return Math.max(0.0, totalEnergy - energyConsumed);
    }
    
    public void handleEnergyDepleted() {
        // TODO empty all actions!!! remove all timeouts!!! ignore further inter device signals!!!
    }

    private double calculateCurrentConsumption() {
        double sum = 0.0;
        Iterator iterator = idConsumptionMap.values().iterator();
        while (iterator.hasNext()) {
            Double consumption = (Double) iterator.next();
            sum += consumption.doubleValue();
        }
        return sum;
    }
    
    private final class EmptyEvent extends Event {

        public EmptyEvent(double time) {
            super(time);
        }

		protected void handleInternal() {
            handleEnergyDepleted();
		}

    }
    
}