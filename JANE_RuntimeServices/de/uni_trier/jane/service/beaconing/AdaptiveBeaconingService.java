/*****************************************************************************
* 
* AdaptiveBeaconingService.java
* 
* $Id: AdaptiveBeaconingService.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.ServiceUnit;

/**
 * @author daniel
 * This Beaconing service adapts the beaconing frequency to the amount of locally seen neighbor devices. 
 */
public class AdaptiveBeaconingService extends GenericBeaconingService {

    private RuntimeOperatingSystem operatingSystem;
    private ContinuousDistribution beaconDistribution;
    private double minBeaconInterval;
    private double maxBeaconInterval;
    private double beaconRepeatCount;
    private double beaconJitterFactor;
    private double cleanupDelta;
    private double incStep;
    private AdaptiveBeaconFunction beaconFunction;
    
    
    /**
     * 
     * TODO: comment method 
     * @param serviceUnit
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit){
        return createInstance(serviceUnit,0.5,50,100,0.1,3,0.5);
    }
    
    /**
     * Creates a new <code>AdaptiveBeaconingService</code> within the given service unit.
     * @param serviceUnit   
     * @param minBeaconInterval     minimum interval for beacon propagation
     * @param maxBeaconInterval     maximum interval for beacon propagation
     * @param maxNeighbors amount of neighbors when the maximum beacon interval should be reached
     * @param beaconJitterFactor    the factor to calculate the jitter of the current beacon interval ( 0< beaconJitterFactor <1) 
     * @param beaconRepeatCount     indicates the amount of messages that can get lost before a neighor is removed. 
     * @param cleanupDelta          period for checking for autdated neighbors
     * @return the ID of the <code>AdaptiveBeaconingService</code>
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, double minBeaconInterval,double maxBeaconInterval, int maxNeighbors, double beaconJitterFactor,int beaconRepeatCount, double cleanupDelta) {
        return createInstance(serviceUnit,new LinearBeaconFunction(minBeaconInterval,maxBeaconInterval,maxNeighbors),beaconJitterFactor,beaconRepeatCount,cleanupDelta);
    }
    
    
    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param beaconFunction
     * @param beaconJitterFactor
     * @param beaconRepeatCount
     * @param cleanupDelta
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, AdaptiveBeaconFunction beaconFunction, double beaconJitterFactor,int beaconRepeatCount, double cleanupDelta) {
        ServiceID linkLayer= serviceUnit.getService(LinkLayer.class);
        ServiceID ownServiceID=new StackedClassID(AdaptiveBeaconingService.class.getName(),linkLayer);
        return serviceUnit.addService(
                new AdaptiveBeaconingService(
                        ownServiceID,linkLayer,
                        beaconFunction,
                        beaconJitterFactor,beaconRepeatCount,
                        cleanupDelta));
    }
    
    

    /**
     * 
     * Constructor for class <code>AdaptiveBeaconingService</code>
     *
     * @param ownServiceID
     * @param linkLayerService
     * @param minBeaconInterval
     * @param maxBeaconInterval
     * @param maxNeighbors
     * @param beaconJitterFactor   
     * @param beaconRepeatCount
     * @param cleanupDelta
     */
    public AdaptiveBeaconingService(ServiceID ownServiceID, ServiceID linkLayerService, AdaptiveBeaconFunction beaconFunction, double beaconJitterFactor, int beaconRepeatCount, double cleanupDelta) {
        super(ownServiceID, linkLayerService);
        this.beaconFunction=beaconFunction;
      
        this.beaconJitterFactor=beaconJitterFactor;
     
        this.beaconRepeatCount=beaconRepeatCount;
        this.cleanupDelta=cleanupDelta;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.beaconing.GenericBeaconingService#start(de.uni_trier.jane.service.RuntimeOperatingSystem)
     */
    public void start(RuntimeOperatingSystem operatingSystem) {
        this.operatingSystem=operatingSystem;
        beaconDistribution=operatingSystem.getDistributionCreator().getContinuousUniformDistribution(-1,1);
        super.start(operatingSystem);
    }
    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.beaconing.GenericBeaconingService#getBeaconingDelta()
     */
    protected double getBeaconingDelta() {
        
        
    	double beaconBase=beaconFunction.getBeaconBase(deviceTimestampMap.size());
        double jitter=(beaconBase*beaconJitterFactor)*beaconDistribution.getNext();
        
    	return beaconBase+jitter;//(0.5-beaconDistribution.getNext(operatingSystem.getTime()))*beaconBase;
    }    	
    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.beaconing.GenericBeaconingService#getCleanupDelta()
     */
    protected double getCleanupDelta() {

        return cleanupDelta;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ssds.service.beaconing.GenericBeaconingService#getExpirationDelta()
     */
    protected double getExpirationDelta() {
        return getBeaconingDelta()*beaconRepeatCount;
    }

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub
		
	}

}
