/*****************************************************************************
 * 
 * Graphics2DCanvas.java
 * 
 * $Id: Graphics2DCanvas.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.visualization;


import java.awt.*;
import java.awt.geom.*;
import java.net.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.visualization.Canvas;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class is an implementation of the <code>Canvas</code> interface. It uses an
 * AWT <code>Graphics2D</code> object for its output.
 * @deprecated
 */
public class Graphics2DCanvas implements Canvas {

	private final static String VERSION = "$Id: Graphics2DCanvas.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private double scale;
	private Map nameImageMap;

	private Graphics2D graphics;

	public Graphics2DCanvas() {
		scale = 1.0;
		nameImageMap = new HashMap();
	}

	public Graphics2DCanvas(double scale) {
		this.scale = scale;
		nameImageMap = new HashMap();
	}
	
	public void setScale(double scale) {
		this.scale = scale;
	}
	
	/**
	 * Set the <code>Graphics2D</code> object.
	 * @param graphics the graphicst object
	 */
	public void setGraphics(Graphics2D graphics) {
		this.graphics = graphics;
//		this.graphics.setStroke(new BasicStroke(10.0f));
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawLine(Position, Position, Color, float)
	 */
	public void drawLine(PositionBase from, PositionBase to, Color color, float lineWidth) {
		setColor(color);
		drawShape(new Line2D.Double(from.getX() * scale, from.getY() * scale, to.getX() * scale, to.getY() * scale), false);
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawRectangle(Rectangle, Color, boolean, float)
	 */
	public void drawRectangle(de.uni_trier.jane.basetypes.Rectangle rectangle, Color color, boolean filled, float lineWidth) {
		setColor(color);
		drawShape(new Rectangle2D.Double(rectangle.getBottomLeft().getX() * scale, rectangle.getBottomLeft().getY() * scale, rectangle.getWidth() * scale, rectangle.getHeight() * scale), filled);
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawEllipse(Rectangle, Color, boolean)
	 */
	public void drawEllipse(de.uni_trier.jane.basetypes.Rectangle rectangle, Color color, boolean filled) {
		setColor(color);
		drawShape(new Ellipse2D.Double(rectangle.getBottomLeft().getX() * scale, rectangle.getBottomLeft().getY() * scale, rectangle.getWidth() * scale, rectangle.getHeight() * scale), filled);
        
	}

	public void drawPolygon(PositionIterator positionIterator, Color color, boolean filled) {
		setColor(color);
		PathIterator pathIterator = new PolygonPathIterator(positionIterator);
		GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		generalPath.append(pathIterator, true);
		drawShape(generalPath, filled);
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawText(String, Rectangle, Color)
	 */
	public void drawText(String text, de.uni_trier.jane.basetypes.Rectangle rectangle, Color color) {
		setColor(color);
		graphics.setFont(graphics.getFont().deriveFont(10*(float)scale)); // FIXME
		graphics.drawString(text, (float)(rectangle.getBottomLeft().getX() * scale), (float)(rectangle.getBottomLeft().getY() * scale));
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawImage(java.lang.String, de.uni_trier.ubi.appsim.kernel.basetype.Rectangle)
	 */
	public void drawImage(String fileName, de.uni_trier.jane.basetypes.Rectangle rectangle/*, Color color*/) {

		Image image = (Image)nameImageMap.get(fileName);		
		if(image == null) {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			try {
				image = toolkit.getImage(new URL(fileName));
			} catch (MalformedURLException e) {
				image = toolkit.getImage(fileName); //FIXME
			}
			nameImageMap.put(fileName, image);
		}
//		MediaTracker mediaTracker = new MediaTracker(this);
//		mediaTracker.addImage(image, 0);
//		try
//		{
//			mediaTracker.waitForID(0);
//		}
//		catch (InterruptedException ie)
//		{
//			System.err.println(ie);
//			System.exit(1);
//		}
		graphics.drawImage(image, (int)(rectangle.getBottomLeft().getX() * scale), (int)(rectangle.getBottomLeft().getY() * scale), (int)(rectangle.getWidth() * scale), (int)(rectangle.getHeight() * scale), null/*calcAWTColor(color)*/, null);
		
	}


	private void setColor(Color color) {
		graphics.setColor(calcAWTColor(color));
	}

	private void drawShape(java.awt.Shape shape, boolean filled) {
		if(filled) {
			graphics.fill(shape);
		}
		else {
			graphics.draw(shape);
		}
	}

	private java.awt.Color calcAWTColor(Color color) {
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
	}

    public void setTransformation(Matrix transformation) {
        // TODO Auto-generated method stub
        
    }

	public void drawImage(Image image, PositionBase position, Matrix matrix) {
		AffineTransform affine;
		//double wh=.5*image.getWidth();
		//double hh=.5*image.getHeight();
		double x = position.getX();//-wh;
		double y = position.getY();//-hh;
		
		affine = new AffineTransform( matrix.v[0].x,-matrix.v[0].y, //matrix.v[0].z,
									 -matrix.v[1].x, matrix.v[1].y,
									  x,y);
		graphics.drawImage(image, affine, null);
		
	}

	public int getVisibleWidth() {
		return 0;
	}

	public int getVisibleHeight() {
		return 0;
	}	

}
