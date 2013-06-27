/*****************************************************************************
 * 
 * FileDynamicSource.java
 * 
 * $Id: FileDynamicSource.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic;

import java.io.*;
import java.util.zip.ZipFile;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * Dynamic source implementation that reads all actions from an XML file 
 * complying to the CommandList DTD. You can create such a file by supplying 
 * an output to the LinkCalculator. Since background shapes are not saved by 
 * the LinkCalculator you have to submit a suitable background shape.
 * The file is read "by need", thus saving memory.
 */
public class FileDynamicSource implements DynamicSource, TagHandler {
	
	private final static String VERSION = "$Id: FileDynamicSource.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private double scale;
	private SimpleOnDemandPseudoXMLParser parser;
	private boolean hasNext = true;
	private Action action = null;
	private Action tmpAction = null;
	private String tmpName = null;
	private Shape backgroundShape;
	private boolean first = true;
	private Rectangle rectangle;

    private double lastExit;

	/**
	 * Constructs a new FileDynamicSource.
	 * @param inputFile the name of the XML file to read the actions from
	 * @param backgroundShape the background shape to use
	 * @throws FileNotFoundException if the input file can't be found
	 */
	public FileDynamicSource(String inputFile, Shape backgroundShape) throws FileNotFoundException {
		this(new FileInputStream(inputFile), backgroundShape, 1.0);
	}
	
	/**
	 * Constructs a new FileDynamicSource.
	 * @param in the InputStream of the XML file to read the actions from
	 * @param backgroundShape the background shape to use
	 * @throws FileNotFoundException if the input file can't be found
	 */
	public FileDynamicSource(InputStream in, Shape backgroundShape, double scale) throws FileNotFoundException {
		this.backgroundShape = backgroundShape;
		this.scale = scale;
		try {
			this.parser = new SimpleOnDemandPseudoXMLParser(in, this);
			parser.parseNext(); // read <commandline>
			parser.parseNext(); // read the first action from the list to be ready 
		} catch (FileNotFoundException e) {
			throw e;//new Error(e);
		} catch (PrematureEOFException e) {
			throw new Error(e.getMessage());
		} catch (IOException e) {
			throw new Error(e.getMessage());
		} catch (ParseException e) {
			throw new Error(e.getMessage());
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#hasNext()
	 */
	public boolean hasNext() {
		return action != null;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#next()
	 */
	public Action next() {
		Action toReturn = action;
		try {
			parser.parseNext();
		} catch (PrematureEOFException e) {
			throw new Error(e.getMessage());
		} catch (ParseException e) {
			throw new Error(e.getMessage());
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}
		return toReturn;
	}
    public Condition getTerminalCondition(final Clock clock) {     
        return new Condition() {
            /* (non-Javadoc)
             * @see de.uni_trier.jane.simulation.kernel.Condition#reached()
             */
            public boolean reached() {
                if (action!=null) return false;
                return lastExit>clock.getTime();
            }
        };
    }


	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#getShape()
	 */
	public Shape getShape() {
		return backgroundShape;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return rectangle;
	}

	public void startElement(String name, XMLAttributes attributes) {
		if (name.equals("enter")) {
			double time = Double.parseDouble(attributes.getAttribute("time").getValue());
			DeviceID address = new SimulationDeviceID(Long.parseLong(attributes.getAttribute("address").getValue()));
			double endTime = Double.parseDouble(attributes.getAttribute("endtime").getValue());
			double startX = Double.parseDouble(attributes.getAttribute("startx").getValue()) * scale;
			double startY = Double.parseDouble(attributes.getAttribute("starty").getValue()) * scale;
			double endX = Double.parseDouble(attributes.getAttribute("endx").getValue()) * scale;
			double endY = Double.parseDouble(attributes.getAttribute("endy").getValue()) * scale;
			double sendingRadius = Double.parseDouble(attributes.getAttribute("radius").getValue()) * scale;
			TrajectoryMapping pmt = calculateTrajectoryMapping(time, endTime, startX, startY, endX, endY);
			tmpAction = new EnterAction(time, address, pmt, false, new ConstantDoubleMapping(sendingRadius));
			tmpName = name;
		} else if (name.equals("arrival")) {
			double time = Double.parseDouble(attributes.getAttribute("time").getValue());
			DeviceID address = new SimulationDeviceID(Long.parseLong(attributes.getAttribute("address").getValue()));
			double endTime = Double.parseDouble(attributes.getAttribute("endtime").getValue());
			double startX = Double.parseDouble(attributes.getAttribute("startx").getValue()) * scale;
			double startY = Double.parseDouble(attributes.getAttribute("starty").getValue()) * scale;
			double endX = Double.parseDouble(attributes.getAttribute("endx").getValue()) * scale;
			double endY = Double.parseDouble(attributes.getAttribute("endy").getValue()) * scale;
			TrajectoryMapping pmt = calculateTrajectoryMapping(time, endTime, startX, startY, endX, endY);
			tmpAction = new SetPositionMappingAction(time, pmt,false, address);
			tmpName = name;
		} else if (name.equals("exit")) {
			double time = Double.parseDouble(attributes.getAttribute("time").getValue());
			if (lastExit<time) lastExit=time;
			DeviceID address = new SimulationDeviceID(Long.parseLong(attributes.getAttribute("address").getValue()));
			tmpAction = new ExitAction(time, address);
			tmpName = name;
		} else if (name.equals("attach")) {
			double time = Double.parseDouble(attributes.getAttribute("time").getValue());
			DeviceID sender = new SimulationDeviceID(Long.parseLong(attributes.getAttribute("sender").getValue()));
			DeviceID receiver = new SimulationDeviceID(Long.parseLong(attributes.getAttribute("receiver").getValue()));
			tmpAction = new AttachAction(time, sender, receiver, new DoubleMappingIntervalImplementation(new ConstantDoubleMapping(1.0),time,Double.POSITIVE_INFINITY));
			tmpName = name;
		} else if (name.equals("detach")) {
			double time = Double.parseDouble(attributes.getAttribute("time").getValue());
			DeviceID sender = new SimulationDeviceID(Long.parseLong(attributes.getAttribute("sender").getValue()));
			DeviceID receiver = new SimulationDeviceID(Long.parseLong(attributes.getAttribute("receiver").getValue()));
			tmpAction = new DetachAction(time, sender, receiver);
			tmpName = name;
		} else if (name.equals("commandlist")) {
			double bottomLeftX = Double.parseDouble(attributes.getAttribute("bottomleftx").getValue()) * scale;
			double bottomLeftY = Double.parseDouble(attributes.getAttribute("bottomlefty").getValue()) * scale;
			double topRightX = Double.parseDouble(attributes.getAttribute("toprighty").getValue()) * scale;
			double topRightY = Double.parseDouble(attributes.getAttribute("toprighty").getValue()) * scale;
			rectangle = new Rectangle(new Position(bottomLeftX, bottomLeftY), new Position(topRightX, topRightY));
		}
	}

	private TrajectoryMapping calculateTrajectoryMapping(double time, double endTime, double startX, double startY, double endX, double endY) {
		Position startPosition = new Position(startX, startY);
		Position endPosition = new Position(endX, endY);
		Position relativeDirection = endPosition.sub(startPosition).scale(1/(endTime-time));
		LinearPositionMapping positionMapping =
			new LinearPositionMapping(time, endTime, startPosition, endPosition);
		LinearPositionMapping directionMapping =
			new LinearPositionMapping(time, endTime, relativeDirection, relativeDirection);
		TrajectoryMapping pmt = new PositionMappingTrajectory(positionMapping, directionMapping);
		return pmt;
	}

//	private TrajectoryMapping calculateTrajectoryMapping(startX, startY,

	public void endElement(String name) {
		if (name.equals("commandlist")) {
			action = null;
			return;
		}
		if (name.equals(tmpName)) {
			action = tmpAction;
		}
	}
	
	public void text(String string) {
		//NOP
	}


	/**
	 * NUR F?R TESTZWECKE!!! Diese Methode darf jederzeit gel?scht werden.
	 * @deprecated
	 */
//	private double sr = 0.0;
//	public double getSendingRadius() {
//		EnterAction enterAction = (EnterAction)action;
//		enterAction.execute(new DynamicInterpreter() {
//			public void enter(double time, Address address, TrajectoryMapping trajectoryMapping,boolean suspended, DoubleMapping sendingRadius) {
//				sr = sendingRadius.getValue(0.0);
//			}
//			public void exit(double time, Address address) {
//			}
//			public void attach(double time, Address sender, Address receiver, DoubleMappingInterval linkReliability) {
//			}
//			public void detach(double time, Address sender, Address receiver) {
//			}
//			public void setTrack(double time, Address address, TrajectoryMapping trajectoryMapping, boolean suspended) {
//			}
//			public void setLinkReliability(double time, Address sender, Address receiver, DoubleMappingInterval linkReliability) {
//			}
//			public void setSendingRadius(double time, Address address, DoubleMapping sendingRadius) {
//			}});
//		return sr;
//	}


}