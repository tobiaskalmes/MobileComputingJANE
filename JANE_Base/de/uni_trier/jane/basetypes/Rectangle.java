/*****************************************************************************
 * 
 * Rectangle.java
 * 
 * $Id: Rectangle.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.basetypes;

import java.io.Serializable;

/**
 * This class implements a rectangle consisting of a bottom left and top right corner.
 */
public class Rectangle implements Serializable{

	private final static String VERSION = "$Id: Rectangle.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	/**
	 * This is the empty rectangle (0,0,0,0).
	 */
	public static final Rectangle NULL_RECTANGLE = new Rectangle(Position.NULL_POSITION, Extent.NULL_EXTENT);

	private Position bottomLeft;
	private Position topRight;

	/**
	 * Construct a new <code>Rectangle</code> object in the z-plane 
	 * @param x1 the first x coordinate
	 * @param y1 the first y coordinate
	 * @param x2 the second x coordinate
	 * @param y2 the second y coordinate
	 */
	public Rectangle(double x1, double y1, double x2, double y2) {
	    this(new Position(x1, y1), new Position(x2, y2));
	}

	/**
	 * Construct a new <code>Rectangle</code> object in the z-plane
	 * @param pos1 the first corner
	 * @param pos2 the second corner
	 */
	public Rectangle(Position pos1, Position pos2) {
		bottomLeft = new Position(
				Math.min(pos1.getX(),pos2.getX()),
				Math.min(pos1.getY(),pos2.getY()));
		topRight = new Position(
				Math.max(pos1.getX(),pos2.getX()),
				Math.max(pos1.getY(),pos2.getY()));
	}
	
	/**
	 * Construct a new <code>Rectangle</code> object.
	 * @param center the center of the rectangle
	 * @param extent the extent of the rectangle
	 */
	public Rectangle(PositionBase center, Extent extent) {
		double w,h;
		w = .5*extent.getWidth();
		h = .5*extent.getHeight();
		bottomLeft = new Position(center.getX()-w, center.getY()-h, center.getZ());
		topRight   = new Position(center.getX()+w, center.getY()+h, center.getZ());
	}
    
    
	
	/**
	 * Construct a new <code>Rectangle</code> object.
	 * Topleft corner is (0,0)
	 * @param extent the extent of the rectangle
	 */
	public Rectangle(Extent extent){
        this(new Position(extent.getWidth(),0),
                new Position(0,extent.getHeight()));	
    }

	/**
	 * Get the bottom left corner.
	 * @return the bottom left corner
	 */
	public Position getBottomLeft() {
		return bottomLeft;
	}

	/**
	 * Get the top right corner.
	 * @return the top right corner
	 */
	public Position getTopRight() {
		return topRight;
	}

	/**
	 * Get the bottom right corner.
	 * @return the bottom right corner
	 */
	public Position getBottomRight() {
		return new Position(topRight.getX(), bottomLeft.getY());
	}
	/**
	 * Get the top left corner.
	 * @return the top left corner
	 */
	public Position getTopLeft() {
		return new Position(bottomLeft.getX(),topRight.getY());
	}

	/**
	 * Get the width of the rectangle.
	 * @return the width
	 */
	public double getWidth() {
		return bottomLeft.distanceX(topRight);
	}

	/**
	 * Get the height of the rectangle.
	 * @return the height
	 */
	public double getHeight() {
		return bottomLeft.distanceY(topRight);
	}

	/**
	 * Get the center of the rectangle.
	 * @return the center
	 */
	public Position getCenter() {
		return bottomLeft.add(topRight.sub(bottomLeft).scale(0.5));
	}
	
	/**
	 * Get the extent of the rectangle.
	 * @return the extent
	 */
	public Extent getExtent() {
		return new Extent(getWidth(), getHeight());
	}

	/**
	 * Test if the rectangle contains the given position.
	 * @param position the position to be tested
	 * @return true <=> it contains the position
	 */
	public boolean contains(Position position) {
		return bottomLeft.getX() <= position.getX() && position.getX() <= topRight.getX() && bottomLeft.getY() <= position.getY() && position.getY() <= topRight.getY();
	}

	/**
	 * Tests if the given rectangle is inside this one.
	 * @param rectangle the rectangle to be tested
	 * @return true if it is inside
	 */

	public boolean contains(Rectangle rectangle) {
		return contains(rectangle.getBottomLeft()) && contains(rectangle.getTopRight());
	}

	/**
	 * Move the rectangle by the given offset.
	 * @param position the offset
	 * @return the moved rectangle
	 */
	public Rectangle add(Position position) {
		return new Rectangle(bottomLeft.add(position), topRight.add(position));
	}

	/**
	 * Calculate the union with the given rectangle
	 * @param rectangle the other rectangle
	 * @return the union
	 */
	public Rectangle union(Rectangle rectangle) {
        if (rectangle==null) return this;
		return new Rectangle(new Position(Math.min(bottomLeft.getX(), rectangle.bottomLeft.getX()),Math.min(bottomLeft.getY(), rectangle.bottomLeft.getY())),new Position(Math.max(topRight.getX(), rectangle.topRight.getX()),Math.max(topRight.getY(), rectangle.topRight.getY())));
	}

	public Rectangle intersection(Rectangle rectangle) {
		bottomLeft.getX();
		double bottom = Math.max(bottomLeft.getY(), rectangle.getBottomLeft().getY());
		double left = Math.max(bottomLeft.getX(), rectangle.getBottomLeft().getX());
		double top = Math.min(topRight.getY(), rectangle.getTopRight().getY());
		double right = Math.min(topRight.getX(), rectangle.getTopRight().getX());
		if(bottom <= top && left <= right) {
			return new Rectangle(new Position(left, bottom), new Position(right, top));
		}
		return null;
	}

	/**
	 * Test if the rectangle intersects with the given one.
	 * @param rectangle the other rectangle
	 * @return true <=> the intersection is not empty
	 */
	public boolean intersects(Rectangle rectangle) {
		return intersection(rectangle) != null;
	}

    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        if (bottomLeft != null) {
            result = PRIME * result + bottomLeft.hashCode();
        }
        if (topRight != null) {
            result = PRIME * result + topRight.hashCode();
        }

        return result;
    }

    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth == null) {
            return false;
        }

        if (oth.getClass() != getClass()) {
            return false;
        }

        Rectangle other = (Rectangle) oth;
        if (this.bottomLeft == null) {
            if (other.bottomLeft != null) {
                return false;
            }
        } else {
            if (!this.bottomLeft.equals(other.bottomLeft)) {
                return false;
            }
        }
        if (this.topRight == null) {
            if (other.topRight != null) {
                return false;
            }
        } else {
            if (!this.topRight.equals(other.topRight)) {
                return false;
            }
        }

        return true;
    }
}
