/*****************************************************************************
 * 
 * ServiceFactory.java
 * 
 * $Id: ServiceFactory.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.unit;



/**
 * This interface is used for initializing services by the simulation, the hybrid environment and the execution platform.
 * Within simulation the initService() method is called for each device and all services add there are added to each device.
 * Possible service types are <code>SimulationService</code> and <code>PlatformService</code>
 * Within the hybrid environment and the execution platform only <code>PlatformService</code>s are allowed.
 * @see de.uni_trier.jane.service.RuntimeService
 * @see de.uni_trier.jane.service.unit.ServiceUnit 
 *   
 * @author daniel
 *
 */
public interface ServiceFactory {
    
    /**
     * Adds services to the device given in ServiceUnit.
     * Services are added by adding them to the given ServiceUnit
     * 
     * @param serviceUnit ServiceUnit to add services
     */
    public void initServices(ServiceUnit serviceUnit);

}