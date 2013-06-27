/*****************************************************************************
 * 
 * MobileDeviceParameter.java
 * 
 * $Id: MobileDeviceParameter.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.pathnet.timetable.*;

/**
 * This class represents a mobile device after the parameters has been parsed from file
 * It contains all nessecary device parameters, the name of the application class ruinning 
 * on this device and its initilize parameters and the class name of the user behaviour 
 * implementation using this device and its parameters.
 
 */
public class MobileDeviceParameter {



	
	private DeviceID address;
    private HashSet serviceParameterSet;
    private String deviceName;
    private ContinuousDistribution speedDistribution;
    private double power;


	/**
	 * Constructor for class <code>MobileDeviceParameter</code>
	 * @param address		the device address
	 * @param power			the sending power (e.g. the sending range)
	 * @param minSpeed		the minimum moving speed when moving
	 * @param maxSpeed		the maximum moving speed when moving
	 */
	public MobileDeviceParameter(DeviceID address, String deviceName,  double power, ContinuousDistribution speedDistribution) {
		this.address=address;
		this.deviceName=deviceName;
		this.speedDistribution=speedDistribution;
		serviceParameterSet=new HashSet();
		this.power=power;
		
	}
	
	
	/**
	 * @return Returns the device name.
	 */
	public String getName() {
		return deviceName;
	}

	/**
	 * @return Returns the device power (sending range).
	 */
	public double getPower() {
		return power;
	}
	
	/**
	 * @return Returns the device maxSpeed.
	 */
	public double getCurrentSpeed() {
		return speedDistribution.getNext();
	}


	/**
	 * Sets the application parameters
	 * @param appParameters
	 */
	public void addServiceClassParameters(ServiceParameters serviceParameters) {
		this.serviceParameterSet.add(serviceParameters);
	}
	
	/**
	 * Returns the user behaviour parameters
	 * @return Returns the applicationClassParameters.
	 */
	public ServiceParameters[] getServiceClassParameters() {
		return (ServiceParameters[])serviceParameterSet.toArray(new ServiceParameters[serviceParameterSet.size()]);
	}
	

	/**
	 * @return Returns the device address.
	 */
	public DeviceID getAddress() {
		return address;
	}
}
