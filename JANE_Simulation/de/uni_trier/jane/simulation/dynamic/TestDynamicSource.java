/*****************************************************************************
 * 
 * TestDynamicSource.java
 * 
 * $Id: TestDynamicSource.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * A very simple implementation of a dynamic source for testing purposes. It contains
 * an array with a fixed number of actions.
 */
public class TestDynamicSource implements DynamicSource {

	private final static String VERSION = "$Id: TestDynamicSource.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";

	private int current;

	private Action[] action = {
		new EnterAction(
			1.0,
			new SimulationDeviceID(1),
			new PositionMappingTrajectory(new ConstantPositionMapping(new Position(0.0, 0.0)), new ConstantPositionMapping(new Position(0, 0))),
			false,
			new ConstantDoubleMapping(10)),

		new EnterAction(
			2.0,
			new SimulationDeviceID(2),
			new PositionMappingTrajectory(new ConstantPositionMapping(new Position(50.0, 50.0)), new ConstantPositionMapping(new Position(0, 0))),
			false,
			new ConstantDoubleMapping(10)),

		new EnterAction(
			3.0,
			new SimulationDeviceID(3),
			new PositionMappingTrajectory(new ConstantPositionMapping(new Position(55.0, 50.0)), new ConstantPositionMapping(new Position(0, 0))),
			false,
			new ConstantDoubleMapping(10)),

		new EnterAction(
			4.0,
			new SimulationDeviceID(4),
			new PositionMappingTrajectory(new ConstantPositionMapping(new Position(52.5, 55.0)), new ConstantPositionMapping(new Position(0, 0))),
			false,
			new ConstantDoubleMapping(10)),

		new AttachAction(5.0,new SimulationDeviceID(2), new SimulationDeviceID(3), new DoubleMappingIntervalImplementation(new ConstantDoubleMapping(1),5.0,100.0)),
		new AttachAction(5.0, new SimulationDeviceID(3), new SimulationDeviceID(2), new DoubleMappingIntervalImplementation(new ConstantDoubleMapping(1),5.0,101.0)),
		new AttachAction(5.0, new SimulationDeviceID(2), new SimulationDeviceID(4), new DoubleMappingIntervalImplementation(new ConstantDoubleMapping(1),5.0,102.0)),
		new AttachAction(5.0, new SimulationDeviceID(4), new SimulationDeviceID(2), new DoubleMappingIntervalImplementation(new ConstantDoubleMapping(1),5.0,103.0)),


		new DetachAction(100.0, new SimulationDeviceID(2), new SimulationDeviceID(3)),
		new DetachAction(101.0, new SimulationDeviceID(3), new SimulationDeviceID(2)),
		new DetachAction(102.0, new SimulationDeviceID(2), new SimulationDeviceID(4)),
		new DetachAction(103.0, new SimulationDeviceID(4), new SimulationDeviceID(2)),

		new ExitAction(200.0, new SimulationDeviceID(1)),
		new ExitAction(201.0, new SimulationDeviceID(2)),
		new ExitAction(202.0, new SimulationDeviceID(3)),
		new ExitAction(203.0, new SimulationDeviceID(4)),

	};

	/**
	 * Constructs a new TestDynamicSource.
	 */
	public TestDynamicSource() {
		current = 0;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#hasNext()
	 */
	public boolean hasNext() {
		return current < action.length;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#next()
	 */
	public Action next() {
		return action[current++];
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return new Rectangle(new Position(-50,-50), new Position(950,950));
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#getShape()
	 */
	public Shape getShape() {
		return new EllipseShape(new Position(0,0), new Extent(100,100), Color.BLACK, true);
	}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.DynamicSource#getTerminalCondition(de.uni_trier.jane.basetypes.Clock)
     */
    public Condition getTerminalCondition(Clock clock) {
        // TODO Auto-generated method stub
        return null;
    }
}
