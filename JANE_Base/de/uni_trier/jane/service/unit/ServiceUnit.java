/*****************************************************************************
 * 
 * ServiceUnit.java
 * 
 * $Id: ServiceUnit.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.service.unit;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * A service unit contains all services to be started on a device at boottime
 * Services can be organized hirachically by creating childunits identified by a unique name.
 * For example two different network stacks (with a <code>LinkLayer<\code> Service on the top)
 * can be created in two different childunits. Thus, services depending only on the service interface LinkLayer
 * can be assigned to the correct network.   
 * @author daniel
 *
 */
public interface ServiceUnit {
	/**
	 * The unique ID of the device where the service are started at boottime 
	 * @return	the unique device ID
	 */
	public DeviceID getDeviceID();

	/**
	 * The label of this ServiceUnit
	 *  
	 * @return	string representation of the service units label
	 */
	public String getLabel();


	/**
	 * Returns the serviceID of the service implementing the given interface or class
	 * throws an exeception if no service or more than one service exists for the given classtype
	 * Use childunits in the latter case
	 * @param type 	the super interface or class of the searched service
	 * @return		the serviceID of the searched service
	 */
	public ServiceID getService(Class type);

	/**
	 * Checks wether this unit contains a service implementing the given interface or class
	 * @param type 	the super interface or class of the searched service
	 * @return	true if a service exists
	 */
	public boolean hasService(Class type);

	/**
	 * Adds a Service to this ServiceUnit.
	 * This service is also visible within the created childunits
	 * @param service 	the service to add
	 * @return the serviceID of the added service
	 */
	public ServiceID addService(Service service);

	/**
	 * Adds a Service to this ServiceUnit and sets the visualizable flag
	 * If this flag is true, this service is vizualized within the simulation.
	 * Only used within Simulations
	 * This service is also visible within the created childunits
	 * @param service 		the service to add
	 * @param visualize		visualize the service
	 * @return the serviceID of the added service
	 */	
	public ServiceID addService(Service service, boolean visualize);

	/**
	 * Checks wether this unit has a child unit with the name label
	 * @param label		the label of the child unit
	 * @return	true, if the unit has a child unit with the given label
	 */
	public boolean hasChildUnit(String label);

	/**
	 * Returns the child unit with the given label
	 * @param label		the label of the child unit
	 * @return	the child unit
	 */
	public ServiceUnit getChildUnit(String label);

	/**
	 * Create a child unit with a default label
	 * all service of this ServiceUnit are visible within the child unit.
	 * @return the newly created child unit
	 */
	public ServiceUnit createChildUnit();

	/**
	 * Create a child unit with the name label
	 * all service of this ServiceUnit are visible within the child unit.
	 * @param label 	the label of the child unit
	 * @return the newly created child unit
	 */
	public ServiceUnit createChildUnit(String label);
	
	/**
	 * Adds a service factory for creating services on all devices
	 * Use this only for global services needing a proxy service on each device
	 * For simulation use only!
	 * The given service factory is called for each started devices. Thus all simulation
	 * devices containing the given services
	 * @param serviceFactory	
	 */
	public void addServiceFactory(ServiceFactory serviceFactory);
	
	/**
	 * Sets the device default shape
	 * @param defaultShape the defaultShape
	 */
	public void setDefaultShape(Shape defaultShape);
    
    /**
     * Adds a shape to the Defaultshape
     *  
     * @param shape
     */
    public void addShape(Shape shape);

	/**
	 * By setting this flag all services which are added by the addService(Service) method will be visible.
	 * This is the default operation of a service unit. When setting this flag to false, all services which
	 * are added by this method will not be visualized during the simulation.
	 * @param visualize set to true, if added services have to be visualized
	 */
	public void setVisualizeAddedServices(boolean visualize);

	/**
	 * Determine the setting of the visualization flag for added services.
	 * @see ServiceUnit#addService(Service)
	 * @see ServiceUnit#setVisualizeAddedServices(boolean)
	 * @return true, if added services have to be visualized
	 */
	public boolean getVisualizeAddedServices();

	/**
	 * Get the distribution creator used for this device
	 * @return the distribution creator
	 */
	public DistributionCreator getDistributionCreator();

    

}
