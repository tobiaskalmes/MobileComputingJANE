/*****************************************************************************
 * 
 * MobilitySourceCollection.java
 * 
 * $Id: MobilitySourceCollection.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2003 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.initialization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class allows the combination of several mobility sources into one.
 */
public class MobilitySourceCollection implements MobilitySource {

	public static final InitializationElement INITIALIZATION_ELEMENT = new InitializationElement("collection") {
		public void initialize(InitializationContext initializationContext,
				SimulationParameters simulationParameters) {
			List list = MobilitySource.INITIALIZATION_OBJECT_LIST.getValues(initializationContext,
					simulationParameters);
			MobilitySource[] mobilitySources = (MobilitySource[])list.toArray(new MobilitySource[list.size()]);
			MobilitySource mobilitySource = new MobilitySourceCollection(mobilitySources);
			simulationParameters.setMobilitySource(mobilitySource);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { MobilitySource.INITIALIZATION_OBJECT_LIST };
		}
	};

	
	/**
	 * The CVS version of this class.
	 */
	public final static String VERSION = "$Id: MobilitySourceCollection.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private MobilitySource[] mobilitySources;
	private Map addressMobilitySourceMap;
	private Map addressAddressMap;
	private int currentEnterIndex;
	private int currentAddress;
	private Rectangle rectangle;
	private ShapeCollection shape;
	private int totalDeviceCount;

	/**
	 * Construct a new <code>MobilitySourceCollection</code> object.
	 * @param mobilitySources the mobility sources to be combined
	 */
	public MobilitySourceCollection(MobilitySource[] mobilitySources) {
		this.mobilitySources = mobilitySources;
		addressMobilitySourceMap = new HashMap();
		addressAddressMap = new HashMap();
		currentEnterIndex = 0;
		currentAddress = 0;
		rectangle = null;
		shape = new ShapeCollection();
		totalDeviceCount = 0;
		for(int i=0; i<mobilitySources.length; i++) {
			rectangle = rectangle == null ? mobilitySources[i].getRectangle() : rectangle.union(mobilitySources[i].getRectangle());
			shape.addShape(mobilitySources[i].getShape(), Position.NULL_POSITION);
			totalDeviceCount += mobilitySources[i].getTotalDeviceCount();
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextEnterInfo()
	 */
	public boolean hasNextEnterInfo() {
		if(currentEnterIndex < mobilitySources.length) {
			if(mobilitySources[currentEnterIndex].hasNextEnterInfo()) {
				return true;
			}
			else {
				currentEnterIndex++;
				return hasNextEnterInfo();
			}
		}
		else {
			return false;
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextEnterInfo()
	 */
	public EnterInfo getNextEnterInfo() {
		EnterInfo enterInfo = mobilitySources[currentEnterIndex].getNextEnterInfo();
		DeviceID address = new SimulationDeviceID(currentAddress+1);
		currentAddress++;
		addressAddressMap.put(address, enterInfo.getAddress());
		addressMobilitySourceMap.put(address, mobilitySources[currentEnterIndex]);
		return new EnterInfo(address, enterInfo.getSendingRadius(), enterInfo.getArrivalInfo());
	}

	public boolean hasNextArrivalInfo(DeviceID address) {
		return getMobilitySource(address).hasNextArrivalInfo(getAddress(address));
	}

	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		return getMobilitySource(address).getNextArrivalInfo(getAddress(address));
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public Shape getShape() {
		return shape;
	}

	public int getTotalDeviceCount() {
		return totalDeviceCount;
	}

	private MobilitySource getMobilitySource(DeviceID address) {
		return (MobilitySource)addressMobilitySourceMap.get(address);
	}
	
	private DeviceID getAddress(DeviceID address) {
		return (DeviceID)addressAddressMap.get(address);
	}

    public Condition getTerminalCondition(Clock clock) {
    	return new TerminalConditionCollection(mobilitySources, clock);
    }

	public double getMinimumTransmissionRange() {
		double minimumRadius = Double.MAX_VALUE;
		for(int i=0; i<mobilitySources.length; i++) {
			minimumRadius = Math.min(minimumRadius, mobilitySources[i].getMinimumTransmissionRange());
		}
		return minimumRadius;
	}

	public double getMaximumTransmissionRange() {
		double maximumRadius = 0;
		for(int i=0; i<mobilitySources.length; i++) {
			maximumRadius = Math.max(maximumRadius, mobilitySources[i].getMaximumTransmissionRange());
		}
		return maximumRadius;
	}

	private static class TerminalConditionCollection implements Condition {
		private Condition[] conditions;
		public TerminalConditionCollection(MobilitySource[] mobilitySources, Clock clock) {
			conditions = new Condition[mobilitySources.length];
			for(int i=0; i<mobilitySources.length; i++) {
				conditions[i] = mobilitySources[i].getTerminalCondition(clock);
			}
		}
		public boolean reached() {
			boolean reached = true;
			for(int i=0; i<conditions.length; i++) {
				if(conditions[i] != null) {
					reached &= conditions[i].reached();
				}
				else {
					reached = false;
				}
			}
			return reached;
		}
	}
	
}
