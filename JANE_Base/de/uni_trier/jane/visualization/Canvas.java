/*****************************************************************************
 * 
 * Canvas.java
 * 
 * $Id: Canvas.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

import java.awt.Image;
import de.uni_trier.jane.basetypes.*;


/**
 * A canvas is a simple abstraction which can be used to paint simulation components.
 */
public interface Canvas {

	/**
	 * Draw a line.
	 * @param from the start position
	 * @param to the end position
	 * @param color the line color
	 * @param lineWidth TODO
	 */
	public void drawLine(PositionBase from, PositionBase to, Color color, float lineWidth);

	/**
	 * Draw a rectangle.
	 * @param rectangle the rectangle positions
	 * @param color the rectangle color
	 * @param filled true <=> filled rectangle
	 * @param lineWidth TODO
	 */
	public void drawRectangle(Rectangle rectangle, Color color, boolean filled, float lineWidth);

	/**
	 * Draw an ellipse.
	 * @param rectangle the ellipse positions
	 * @param color the ellipse color
	 * @param filled true <=> filled ellipse
	 */
	public void drawEllipse(Rectangle rectangle, Color color, boolean filled);

	/**
	 * Draw a closed polygon.
	 * @param positionIterator an iterator over all polypon vertices
	 * @param color the polygon color
	 * @param filled <code>true</code> if filled
	 */
	public void drawPolygon(PositionIterator positionIterator, Color color, boolean filled);

	/**
	 * Draw a Text.
	 * @param text the text to be drawn
	 * @param rectangle the text extent
	 * @param color the text color
	 */
	public void drawText(String text, Rectangle rectangle, Color color);

	/**
	 * Draw an image into the canvas.
	 * @deprecated
	 * @param fileName the file name of the image
	 * @param rectangle the image dimensions
	 */
	public void drawImage(String fileName, Rectangle rectangle);

	/**
	 * draw an ImageIcon onto the canvas at position x,y
	 * technically correct there should be an Image in the basetypes...
	 */
	public void drawImage(Image image, PositionBase position, Matrix matrix);

	/**
	 * get the width of the visible canvas
	 * @return width
	 */
	public int getVisibleWidth();

	/**
	 * get the height of the visible canvas
	 * @return height
	 */
	public int getVisibleHeight();
}
