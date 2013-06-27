/*****************************************************************************
* 
* $Id: SimulationOperatingSystem.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
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
package de.uni_trier.jane.simulation.service;

import de.uni_trier.jane.basetypes.*;

import de.uni_trier.jane.service.operatingSystem.*;


/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
//TODO: throws in Javadoc Kommentar!!!
public interface SimulationOperatingSystem extends GlobalOperatingSystem, RuntimeOperatingSystem {

    /**
     * Returns the deviceID of the (virtual) global device hosting the global services
     * @return	the the global deviceID
     */
	public DeviceID getGlobalDeviceID();

	/**
	 * Sets current energy consumption in watt 
	 * @param watt	the energy consumtion
	 * TODO: passiert hier ueberhaupt etwas?!
     * nein - der enerymanager stellt zwar den aufbrauch der energie fest; es passiert aber nix
	 */
	public void setCurrentEnergyConsumption(double watt);
	
	/**
	 * 
	 * TODO Comment method
	 * @param address
	 */
	public void registerAddress(Address address);
	
	/**
	 * 
	 * TODO Comment method
	 * @param address
	 */
	public void deregisterAddress(Address address);


    

    




	
}
