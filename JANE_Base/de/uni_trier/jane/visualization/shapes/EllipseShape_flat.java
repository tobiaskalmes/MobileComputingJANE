/*****************************************************************************
 * 
 * EllipseShape.java
 * 
 * $Id: EllipseShape_flat.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.visualization.shapes;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;

/**
 * This class is used to draw an ellipse on the canvas.
 */
public class EllipseShape_flat implements Shape {

	private final static String VERSION = "$Id: EllipseShape_flat.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Address center;
	private Extent extent;
	private Color color;
	private boolean filled;

    private Position centerPos;

	
	/**
	 * Construct an ellipse shape.
	 * @param center the center as device address of the ellipse
	 * @param extent the extent of the ellipse
	 * @param color the color of the ellipse
	 * @param filled true <=> filled ellipse
	 */
	public EllipseShape_flat(Address center, Extent extent, Color color, boolean filled) {
		this.center = center;
		this.extent = extent;
		this.color = color;
		this.filled = filled;
	}

    /**
     * 
     * Constructor for class <code>EllipseShape_flat</code>
     * @param center
     * @param radius
     * @param color
     * @param filled
     */
    public EllipseShape_flat(Address center, double radius, Color color, boolean filled) {
        this(center, new Extent(radius*2, radius*2), color, filled);
    }

    /**
     * Construct an ellipse shape.
     * @param center the center as position of the ellipse
     * @param extent the extent of the ellipse
     * @param color the color of the ellipse
     * @param filled true <=> filled ellipse
     */
    public EllipseShape_flat(Position center, Extent extent, Color color, boolean filled){
        centerPos=center;
        this.extent = extent;
        this.color = color;
        this.filled = filled;
    }

    /**
     * Construct an ellipse shape.
     * @param extent the extent of the ellipse
     * @param color the color of the ellipse
     * @param filled true <=> filled ellipse
     */
	public EllipseShape_flat(Extent extent, Color color, boolean filled) {
	    this(Position.NULL_POSITION,extent,color,filled);
    }

    /**
     * Constructor for class <code>EllipseShape</code>
     * Constructs a circle with the given radius
     * @param center  the center of the circle
     * @param radius  the radius of the circle 
     * @param color     the color
     * @param filled    filled true <=> filled circle
     */
    public EllipseShape_flat(Position center, double radius, Color color, boolean filled) {
        this(center,new Extent(radius*2,radius*2),color,filled);
    }

    /**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
	public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
        Position newCenter;

        if (center!=null){
            newCenter=addressPositionMap.getPosition(center).add(position);      
        }else{
            newCenter=centerPos.add(position);
        }
		worldspace.drawXYEllipse(newCenter, 
								0.5*extent.getWidth(), 
								0.5*extent.getHeight(),
								color,
								filled);
    }

	/**
	 * @see de.uni_trier.jane.visualization.Shape#getRectangle(Position)
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
	    return new Rectangle(position.transform(matrix), new Extent(0,0)); // TODO ignored at the moment!!!
	}
}
