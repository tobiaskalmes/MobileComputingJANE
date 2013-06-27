/*****************************************************************************
 * 
 * MobilitySourceBase.java
 * 
 * $Id: MobilitySourceBase.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.mobility_source;

import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.object.*;


/**
 * This abstract base class provides static initialization objects which can be
 * used by each derived mobility source for initialization purposes.
 * 
 * @author Hannes Frey
 */
public abstract class MobilitySourceBase implements MobilitySource {

	/**
	 * The CVS version number of this class.
	 */
	public static final String VERSION = "$Id: MobilitySourceBase.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";
	
	public static final InitializationObjectElement[] INITIALIZATION_OBJECT_ELEMENTS = new InitializationObjectElement[] {
		FixedNodes.MOBILITY_SOURCE_STATIC_UDG_OBJECT,
		FixedNodes.MOBILITY_SOURCE_STATIC_FILE_OBJECT,
		ClickAndPlayMobilitySourceSimple.MOBILITY_SOURCE_CLICK_AND_PLAY_OBJECT,
		RandomMobilitySource.SIMPLE_RANDOM_WAYPOINT_OBJECT,
		RestrictedRandomWaypoint.INITIALIZATION_OBJECT };

	/**
	 * Parameter which can be used to set the number of simulated nodes.
	 */
	protected static final IntegerParameter NUMBER_OF_NODES = new IntegerParameter("numberOfNodes", 100);

	/**
	 * Parameter which can be used to set the width of the simulated area.
	 */
	protected static final DoubleParameter AREA_WIDTH = new DoubleParameter("areaWidth", 500);

	/**
	 * Parameter which can be used to set the height of the simulated area.
	 */
	protected static final DoubleParameter AREA_HEIGHT = new DoubleParameter("areaHeight", 500);

	/**
	 * Parameter which can be used to set a unique sending radius.
	 */
	protected static final DoubleParameter SENDING_RADIUS = new DoubleParameter("sendingRadius", 100);

	/**
	 * Parameter which can be used to set a unique device moving speed.
	 */
	protected static final DoubleParameter MOVING_SPEED = new DoubleParameter("movingSpeed", 1.0);

	/**
	 * Parameter which can be used to load a mobility source file.
	 */
	protected static final StringParameter MOBILITY_SOURCE_FILE = new StringParameter ("fileName", "network.xml");


}
