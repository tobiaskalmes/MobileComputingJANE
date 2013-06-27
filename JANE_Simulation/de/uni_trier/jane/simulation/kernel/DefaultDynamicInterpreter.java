/*****************************************************************************
 * 
 * DefaultDynamicInterpreter.java
 * 
 * $Id: DefaultDynamicInterpreter.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.kernel;

import com.sun.jndi.cosnaming.IiopUrl.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.device.*;
import de.uni_trier.jane.simulation.dynamic.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;

/**
 * Default implementation of a DynamicInterpreter mapping Actions to corresponding Events.
 */
public class DefaultDynamicInterpreter implements DynamicInterpreter {

	private final static String VERSION = "$Id: DefaultDynamicInterpreter.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private DynamicScheduler dynamicScheduler;
	private DeviceManager deviceManager;
	private EventSet eventSet;


	/**
	 * Constructor for class <code>DefaultDynamicInterpreter</code>
	 * @param dynamicScheduler
	 * @param deviceManager
	 * @param eventSet
	 */
	public DefaultDynamicInterpreter(DynamicScheduler dynamicScheduler, DeviceManager deviceManager, EventSet eventSet) {		
	    this.dynamicScheduler = dynamicScheduler;
		this.deviceManager = deviceManager;
		this.eventSet = eventSet;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicInterpreter#enter(double, Address, TrajectoryMapping, boolean, DoubleMapping)
	 */
	public void enter(double time, DeviceID device, TrajectoryMapping trajectoryMapping, boolean suspended, DoubleMapping sendingRadius) {
		eventSet.add(new EnterEvent(time, dynamicScheduler, deviceManager, device, trajectoryMapping, suspended, sendingRadius));
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicInterpreter#exit(double, Address)
	 */
	public void exit(double time, DeviceID device) {
		eventSet.add(new ExitEvent(time, dynamicScheduler, deviceManager, device));
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicInterpreter#detach(double, Address, Address)
	 */
	public void detach(double time, DeviceID sender, DeviceID receiver) {
		eventSet.add(new DetachEvent(time, dynamicScheduler, sender, receiver, deviceManager));
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicInterpreter#attach(double, Address, Address, DoubleMappingInterval)
	 */
	public void attach(double time, DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability) {
		eventSet.add(new AttachEvent(time, dynamicScheduler, sender, receiver, linkReliability, deviceManager));
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicInterpreter#setSendingRadius(double, Address, DoubleMapping)
	 */
	public void setSendingRadius(double time, DeviceID address, DoubleMapping sendingRadius) {
		eventSet.add(new SetSendingRadiusEvent(time, sendingRadius, address, dynamicScheduler, deviceManager));
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicInterpreter#setLinkReliability(double, Address, Address, DoubleMappingInterval)
	 */
	public void setLinkReliability(double time, DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability) {
		eventSet.add(new SetLinkReliabilityEvent(time, linkReliability, sender, receiver, dynamicScheduler, deviceManager));
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicInterpreter#setTrack(double, Address, TrajectoryMapping, boolean)
	 */
	public void setTrack(double time, DeviceID address, TrajectoryMapping trajectoryMapping, boolean suspended) {
		eventSet.add(new SetTrackEvent(time, trajectoryMapping, suspended, address, dynamicScheduler, deviceManager));
	}

}

