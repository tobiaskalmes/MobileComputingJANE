/*****************************************************************************
 * 
 * ImageShape.java
 * 
 * $Id: ImageShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.visualization.shapes;

import java.awt.Image;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;

/**
 * This class is used to draw images on the canvas.
 */
public class ImageShape implements Shape {

	private final static String VERSION = "$Id: ImageShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private String fileName;
	private DeviceID address;
	private Image image;
	private Position center;
	private Extent extent;

	public ImageShape(Image image, Position center, Extent extent) {
		this.address = null;
		this.image = image;
		this.center = center;
		this.extent = extent;
	}

	public ImageShape(DeviceID address, Image icon, Position center, Extent extent) {
		this.address = address;
		this.image = icon;
		this.center = center;
		this.extent = extent;
	}
	
	public ImageShape(String image, Position center, Extent extent) {
		System.out.println("warning unimplemented Image Shape constructor");
	}
	
	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
	public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		Matrix matrix = worldspace.getTransformation();
		
		if (address==null)			//the absolute case
			worldspace.drawImage(
					image,
					position
				);
		else						//the case relative to deviceID
			worldspace.drawImage(
					image,
					new MutablePosition(addressPositionMap.getPosition(address))
						.add(position).add(center) 
				);
	}
	
	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(de.uni_trier.ubi.appsim.kernel.basetype.Position)
     * TODO: this one is unused
     */
    public Rectangle getRectangle(Position position, Matrix matrix) {
		Position absoluteCenter = center.add(position);
		Position offset = new Position(extent.getWidth()/2, extent.getHeight()/2);
		return new Rectangle(absoluteCenter.sub(offset), absoluteCenter.add(offset));
	}
}
