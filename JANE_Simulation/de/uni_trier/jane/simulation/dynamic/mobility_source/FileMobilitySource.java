/*****************************************************************************
 * 
 * FileMobilitySource.java
 * 
 * $Id: FileMobilitySource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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

import java.io.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.dynamic.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

public class FileMobilitySource implements MobilitySource, TagHandler {
	private int totalDeviceCount;
	private Rectangle rectangle;
	private Shape backgroundShape;
	private SimpleOnDemandPseudoXMLParser enterParser;
	private SimpleOnDemandPseudoXMLParser arrivalParser;
	private EnterInfo nextEnterInfo;
	private ArrivalInfoMap arrivalInfos; 
	
	private double minimumRadius;
	private double maximumRadius;
	
	
	public FileMobilitySource(String enterFilename, String arrivalFilename, String shapeFilename) throws FileNotFoundException {
		
		minimumRadius = Double.MAX_VALUE;
		maximumRadius = 0;
		
        if (shapeFilename!=null){
            this.backgroundShape = new XMLRenderShape(shapeFilename);
        }else{
            backgroundShape=EmptyShape.getInstance();
        }
        
		arrivalInfos = new ArrivalInfoMap();
		try {
			enterParser = new SimpleOnDemandPseudoXMLParser(new FileInputStream(enterFilename), this);
			enterParser.parseNext(); // read the <enterlist>
			enterParser.parseNext(); // read the first action from the list to be ready
			arrivalParser = new SimpleOnDemandPseudoXMLParser(new FileInputStream(arrivalFilename), this);
			arrivalParser.parseNext(); // read the arrivallist
		} catch (PrematureEOFException e) {
			throw new Error(e.getMessage());
		} catch (IOException e) {
			throw new Error(e.getMessage());
		} catch (ParseException e) {
			throw new Error(e.getMessage());
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextEnterInfo()
	 */
	public boolean hasNextEnterInfo() {
		return nextEnterInfo != null;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextEnterInfo()
	 */
	public EnterInfo getNextEnterInfo() {
		if (nextEnterInfo == null) {
			throw new IllegalStateException("no next enter info.");
		}
		EnterInfo result = nextEnterInfo;
		if (enterParser == null) {
			nextEnterInfo = null;
		} else {
			try {
				enterParser.parseNext();
			} catch (PrematureEOFException e) {
				throw new Error(e.getMessage());
			} catch (ParseException e) {
				throw new Error(e.getMessage());
			} catch (IOException e) {
				throw new Error(e.getMessage());
			}
			if (result == nextEnterInfo) {
				// nothing read from file
				nextEnterInfo = null;
			}
		}
		return result;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public boolean hasNextArrivalInfo(DeviceID address) {
		try {
			while(arrivalParser != null && !arrivalInfos.hasMoreArrivalInfo(address)) {
				arrivalParser.parseNext();
			}
			return arrivalInfos.hasMoreArrivalInfo(address);
		} catch (PrematureEOFException e) {
			throw new Error(e.getMessage());
		} catch (ParseException e) {
			throw new Error(e.getMessage());
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}		
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		if (!arrivalInfos.hasMoreArrivalInfo(address)) {
			throw new IllegalStateException("no more arrival info for address "+address);
		}
		return arrivalInfos.getArrivalInfo(address);
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return rectangle;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getTotalDeviceCount()
	 */
	public int getTotalDeviceCount() {
		return totalDeviceCount;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getShape()
	 */
	public Shape getShape() {
		return backgroundShape;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.TagHandler#text(java.lang.String)
	 */
	public void text(String text) {
		// NOP
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.TagHandler#startElement(java.lang.String, de.uni_trier.ubi.appsim.kernel.dynamic.XMLAttributes)
	 */
	public void startElement(String name, XMLAttributes attributes) {
		if (name.equals("enter")) {
			double time = Double.parseDouble(attributes.getAttribute("time").getValue());
			DeviceID address = new SimulationDeviceID(Long.parseLong(attributes.getAttribute("address").getValue()));
			double endTime = Double.parseDouble(attributes.getAttribute("endtime").getValue());
			double startX = Double.parseDouble(attributes.getAttribute("startx").getValue());
			double startY = Double.parseDouble(attributes.getAttribute("starty").getValue());
			double endX = Double.parseDouble(attributes.getAttribute("endx").getValue());
			double endY = Double.parseDouble(attributes.getAttribute("endy").getValue());
			double sendingRadius = Double.parseDouble(attributes.getAttribute("radius").getValue());
			minimumRadius = Math.min(minimumRadius, sendingRadius);
			maximumRadius = Math.max(maximumRadius, sendingRadius);
			EnterInfo enterInfo = new EnterInfo(address, sendingRadius, new ArrivalInfo(new Position(startX, startY), time));
			ArrivalInfo arrivalInfo = new ArrivalInfo(new Position(endX, endY), endTime);
			nextEnterInfo = enterInfo;
			arrivalInfos.addArrivalInfo(address, arrivalInfo);
		} else if (name.equals("arrival")) {
			DeviceID address = new SimulationDeviceID(Long.parseLong(attributes.getAttribute("address").getValue()));
			double endTime = Double.parseDouble(attributes.getAttribute("endtime").getValue());
			//double startX = Double.parseDouble(attributes.getAttribute("startx").getValue());
			//double startY = Double.parseDouble(attributes.getAttribute("starty").getValue());
			double endX = Double.parseDouble(attributes.getAttribute("endx").getValue());
			double endY = Double.parseDouble(attributes.getAttribute("endy").getValue());
			ArrivalInfo arrivalInfo = new ArrivalInfo(new Position(endX, endY), endTime);
			arrivalInfos.addArrivalInfo(address, arrivalInfo);
		} else if (name.equals("enterlist")) {
			double bottomLeftX = Double.parseDouble(attributes.getAttribute("bottomleftx").getValue());
			double bottomLeftY = Double.parseDouble(attributes.getAttribute("bottomlefty").getValue());
			double topRightX = Double.parseDouble(attributes.getAttribute("toprighty").getValue());
			double topRightY = Double.parseDouble(attributes.getAttribute("toprighty").getValue());									
			totalDeviceCount = Integer.parseInt(attributes.getAttribute("totaldevicecount").getValue());
			rectangle = new Rectangle(new Position(bottomLeftX, bottomLeftY), new Position(topRightX, topRightY));
			
		} 
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.TagHandler#endElement(java.lang.String)
	 */
	public void endElement(String name) {
		if (name.equals("arrivallist")) {
			arrivalParser = null;
		} else if (name.equals("enterlist")) {
			enterParser = null;			
		}
	}

	private static class ArrivalInfoMap {
		private HashMap map;
		
		public ArrivalInfoMap() {
			map = new HashMap();
		}
		
		public void addArrivalInfo(DeviceID address, ArrivalInfo info) {
			List entries = (List) map.get(address);
			if (entries == null) {
				entries = new ArrayList();
				map.put(address, entries); 
			}
			entries.add(info);
		}
		
		public boolean hadArrivalInfo(DeviceID address) {
			return map.get(address) != null;
		}
		
		public boolean hasMoreArrivalInfo(DeviceID address) {
			return hadArrivalInfo(address) && !((List) map.get(address)).isEmpty();
		}
		
		public ArrivalInfo getArrivalInfo(DeviceID address) {
			if (!hasMoreArrivalInfo(address)) {
				throw new IllegalArgumentException("No info available!");
			}
			return (ArrivalInfo) ((List) map.get(address)).remove(0);
		}
	}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getTerminalCondition(de.uni_trier.jane.basetypes.Clock)
     */
    public Condition getTerminalCondition(Clock clock) {
        // TODO Auto-generated method stub
        return null;
    }

	public double getMinimumTransmissionRange() {
		return minimumRadius;
	}

	public double getMaximumTransmissionRange() {
		return maximumRadius;
	}

}
