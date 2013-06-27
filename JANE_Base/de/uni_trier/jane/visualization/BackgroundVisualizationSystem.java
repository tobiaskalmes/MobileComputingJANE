/*****************************************************************************
 * 
 * BackgroundVisualizationSystem.java
 * 
 * $Id: BackgroundVisualizationSystem.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.visualization;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class is used by each application to visualize device dependent background shapes.
 */
public class BackgroundVisualizationSystem {

	private final static String VERSION = "$Id: BackgroundVisualizationSystem.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private DeviceID address;
	private AddressShapeMap addressShapeMap;

	/**
	 * Constructs a new <code>BackgroundVisualizationSystem</code> object.
	 * @param address the address of the owner of this object
	 * @param addressShapeMap the map to put a shape into
	 */
	public BackgroundVisualizationSystem(DeviceID address, AddressShapeMap addressShapeMap) {
		this.address = address;
		this.addressShapeMap = addressShapeMap;
	}

	/**
	 * Set a new background shape for this device. The previous shape for this device is removed.
	 * @param background the background shape
	 */
	public void set(Shape background) {
		addressShapeMap.set(address, background);
	}

	/**
	 * Remove the actual background shape for this device.
	 */
	public void remove() {
		addressShapeMap.remove(address);
	}

	/**
	 * Set the global background shape. The previous global background shape is removed.
	 * @param shape the background shape
	 */
	public void setGlobal(Shape background) {
		addressShapeMap.setGlobal(background);
	}

	/**
	 * Remove the actual global background shape.
	 */
	public void removeGlobal() {
		addressShapeMap.removeGlobal();
	}

}

