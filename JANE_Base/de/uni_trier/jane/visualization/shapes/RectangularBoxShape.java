/*****************************************************************************
 * 
 * RectangularBoxShape.java
 * 
 * $Id: RectangularBoxShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;

/**
 * This class is used to draw a rectangle on the canvas.
 */
public class RectangularBoxShape implements Shape {

	private final static String VERSION = "$Id: RectangularBoxShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private DeviceID address;
	
	private Position center;
	private Extent extent;
	private double height;
	private Color color;
	private boolean filled;

	/**
	 * Construct a RectangularBox shape.
	 * @param center the center of the rectangle
	 * @param extent the extent of the rectangle
	 * @param height the height of the box
	 * @param color the color of the rectangle
	 * @param filled true <=> filled rectangle
	 */
	public RectangularBoxShape(Position center, Extent extent, double height, Color color, boolean filled) {
		this.address = null;
		this.center = center;
		this.extent = extent;
		this.color = color;
		this.filled = filled;
		this.height = -height;
	}

	/**
	 * Construct a RectangularBoxShape. Relative to the given address
	 * @param address the device the shape is painted relatively
	 * @param center the center of the rectangle
	 * @param extent the extent of the rectangle
	 * @param height the height of the box
	 * @param color the color of the rectangle
	 * @param filled true <=> filled rectangle
	 */
	public RectangularBoxShape(DeviceID address, Extent extent, double height, Color color, boolean filled) {
		this.address = address;
		//this.center = center;
		this.extent = extent;
		this.color = color;
		this.filled = filled;
		this.height = -height;
	}
	
	/**
	 * Construct a RectangularBoxShape.
	 * @param rectangle the rectangle
	 * @param height the height of the box
	 * @param color the color of the rectangle
	 * @param filled true <=> filled rectangle
	 */
	public RectangularBoxShape(Rectangle rectangle, double height, Color color, boolean filled) {
		this(rectangle.getCenter(), rectangle.getExtent(), height, color, filled);
	}

	/**
     * Constructor for class RectangularBoxShape 
     *
     * @param extent the extent of the rectangle
	 * @param height the height of the box
	 * @param color the color of the rectangle
	 * @param filled true <=> filled rectangle
     */
    public RectangularBoxShape(Extent extent, double height, Color color, boolean filled) {
        this(Position.NULL_POSITION,extent,height,color,filled);
    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
    public void visualize(Position pos, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		MutablePosition p = new MutablePosition(pos);

		//in case we paint relatively to a device
		if (address!=null)
			p.add(addressPositionMap.getPosition(address));
		else
			p.add(center);
		p.add(new Position(0,0,height/2));
		worldspace.drawRectangularBox(p, extent.getWidth(), extent.getHeight(), height, color, filled);
    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position pos, Matrix matrix) {
		
		Extent ext = extent;
		Position position = pos.transform(matrix);
		Position absoluteCenter = center.add(position);
		Position offset = new Position(0.5*ext.getWidth(), 0.5*ext.getHeight());
		return new Rectangle(absoluteCenter.sub(offset), absoluteCenter.add(offset));
	}
}
