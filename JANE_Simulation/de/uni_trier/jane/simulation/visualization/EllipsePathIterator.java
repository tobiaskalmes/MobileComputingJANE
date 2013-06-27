/*****************************************************************************
 * 
 * EllipsePathIterator.java
 * 
 * $Id: EllipsePathIterator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.visualization;

import java.awt.geom.*;

import de.uni_trier.jane.basetypes.*;

/**
 * This is an implementation of the <code>PathIterator</code> interface
 * for arbitrary ellipses.
 */
public class EllipsePathIterator implements PathIterator {

	private double x;
	private double y;
	private double xRadius;
	private double yRadius;
	private int segmentCount;
	private int currentSegment;
	private double xCalculated;
	private double yCalculated;

	/**
	 * @param center the ellipse center
	 * @param extent the ellipse extent
	 * @param segmentCount the number of ellipse segments
	 */
	public EllipsePathIterator(Position center, Extent extent, int segmentCount) {
		this.x = center.getX();
		this.y = center.getY();
		this.xRadius = extent.getWidth()/2.0;
		this.yRadius = extent.getHeight()/2.0;
		this.segmentCount = segmentCount;
		currentSegment = 0;
	}

	public int getWindingRule() {
		return WIND_EVEN_ODD;
	}

	public void next() {
		currentSegment++;
	}

	private void calculate() {
		double a = xRadius;
		double b = yRadius;
		double angle = 2 * Math.PI / segmentCount;
		int i = currentSegment;
		double c = a * Math.cos(i * angle + Math.PI / segmentCount);
		double s = b * Math.sin(i * angle + Math.PI / segmentCount);
		xCalculated = c + x;
		yCalculated = s + y;
	}

	public boolean isDone() {
		return currentSegment >= segmentCount + 1;
	}

	public int currentSegment(double[] coords) {
		calculate();
		coords[0] = xCalculated;
		coords[1] = yCalculated;
		return getCommand();
	}

	public int currentSegment(float[] coords) {
		calculate();
		coords[0] = (float) xCalculated;
		coords[1] = (float) yCalculated;
		return getCommand();
	}

	private int getCommand() {
		if (currentSegment == 0) {
			return SEG_MOVETO;
		}
		else if (currentSegment < segmentCount) {
			return SEG_LINETO;
		}
		else {
			return SEG_CLOSE;
		}
	}

}
