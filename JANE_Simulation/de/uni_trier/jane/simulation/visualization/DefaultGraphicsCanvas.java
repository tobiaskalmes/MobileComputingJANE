/*****************************************************************************
 * 
 * Graphics2DCanvas.java
 * 
 * $Id: DefaultGraphicsCanvas.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import java.awt.image.ImageObserver;
import java.net.*;
import java.util.*;

import javax.swing.ImageIcon;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.visualization.Canvas;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class is an implementation of the <code>Canvas</code> interface. It uses an
 * AWT <code>Graphics2D</code> object for its output.
 */
public class DefaultGraphicsCanvas implements Canvas {

	private final static String VERSION = "$Id: DefaultGraphicsCanvas.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

    private static final float DEFAULT_WIDTH = 1;

	private double scale;
	
	private Map nameImageMap;

	protected Graphics graphics=null;
	protected Graphics2D graphics2d=null;
	
	public DefaultGraphicsCanvas() {
		scale = 1.0;
		nameImageMap = new HashMap();
	}

	public DefaultGraphicsCanvas(double scale) {
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
	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	//		this.graphics.setStroke(new BasicStroke(10.0f));
		if (graphics instanceof Graphics2D) {
			graphics2d = (Graphics2D)graphics;
		} else {
			graphics2d = null;
		}
	}

	/**
	 * get the <code>Graphics2D</code> reference.
	 */
	public Graphics getGraphics() {
		return this.graphics;
	}
	
	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawLine(Position, Position, Color, float)
	 */
	public void drawLine(PositionBase from, PositionBase to, Color color, float lineWidth) {
		setColor(color);
		drawShape(new Line2D.Double(from.getX() * scale, from.getY() * scale, to.getX() * scale, to.getY() * scale), false,lineWidth);
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawRectangle(Rectangle, Color, boolean, float)
	 */
	public void drawRectangle(de.uni_trier.jane.basetypes.Rectangle rectangle, Color color, boolean filled, float lineWidth) {
		setColor(color);
		drawShape(new Rectangle2D.Double(rectangle.getBottomLeft().getX() * scale, rectangle.getBottomLeft().getY() * scale, rectangle.getWidth() * scale, rectangle.getHeight() * scale), filled,lineWidth);
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawEllipse(Rectangle, Color, boolean)
	 */
	public void drawEllipse(de.uni_trier.jane.basetypes.Rectangle rectangle, Color color, boolean filled) {
		setColor(color);
		drawShape(new Ellipse2D.Double(rectangle.getBottomLeft().getX() * scale,
                    rectangle.getBottomLeft().getY() * scale, rectangle.getWidth() * scale, rectangle.getHeight() * scale), 
                    filled,
                    DEFAULT_WIDTH);
	}

	public void drawPolygon(PositionIterator positionIterator, Color color, boolean filled) {
		setColor(color);
		PathIterator pathIterator = new PolygonPathIterator(positionIterator);
		GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		generalPath.append(pathIterator, true);
		drawShape(generalPath, filled,DEFAULT_WIDTH);
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawText(String, Rectangle, Color)
	 */
	public void drawText(String text, de.uni_trier.jane.basetypes.Rectangle rectangle, Color color) {
		setColor(color);
		graphics.setFont(graphics.getFont().deriveFont(10*(float)scale)); // FIXME
		//if (graphics2d!=null) {
		//	graphics2d.drawString(text, (float)(rectangle.getBottomLeft().getX() * scale), (float)(rectangle.getBottomLeft().getY() * scale));
		//} else {
			graphics.drawString(text,(int)((float)(rectangle.getBottomLeft().getX() * scale)),(int)((float)(rectangle.getBottomLeft().getY() * scale)));
		//	System.out.println("warning: DefaultGraphicsCanvas.drawText graphics context does not provide a drawString() method");
		//}
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

	/**
	 * implement an empty anonymous ImageObserver interface
	 */
	private final ImageObserver imageObserver = new ImageObserver() {

		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	
	/**
	 * draws the image...
	 */
	public void drawImage(Image image, PositionBase position, Matrix matrix) {
		AffineTransform affine;
		//double wh=.5*image.getWidth();
		//double hh=.5*image.getHeight();
		double x = position.getX();//-wh;
		double y = position.getY();//-hh;
		
		affine = new AffineTransform( matrix.v[0].x,-matrix.v[0].y, //matrix.v[0].z,
									 -matrix.v[1].x, matrix.v[1].y,
									  x,y);
		if (graphics2d!=null)
			graphics2d.drawImage(image, affine, null);
		else {
			graphics.drawImage(image, (int)position.getX(), (int)position.getY(), imageObserver);
		}
	}
	
	private Color color;
	private void setColor(Color color) {
		this.color = color;
		graphics.setColor(calcAWTColor(color));
	}

	private final AffineTransform affineTransformIdentity = new AffineTransform();
	private final double[] coords = new double[6];

	private final MutablePosition currentPos = new MutablePosition();
	private final MutablePosition toPos = new MutablePosition();

	
	private void drawShape(java.awt.Shape shape, boolean filled,float width) {
		
		if (graphics2d!=null) {
            graphics2d.setStroke(new BasicStroke(width));
			if (filled)
				graphics2d.fill(shape);
			else
				graphics2d.draw(shape);
		} else { // if do not have a Graphics2D instance fall-back
			PathIterator it = shape.getPathIterator(affineTransformIdentity);
			MutablePosition firstPos = null;
			
			//this at least draws simple shapes..
			while (!it.isDone()) {
				switch (it.currentSegment(coords)) {
				case PathIterator.SEG_LINETO:
					toPos.set(coords[0],coords[1]);
					if (firstPos==null)
						firstPos = new MutablePosition(toPos);
					this.drawLine(currentPos, toPos, color, 1);
					break;
				case PathIterator.SEG_MOVETO:
					currentPos.set(coords[0],coords[1]);
					if (firstPos==null)
						firstPos = new MutablePosition(currentPos);
					break;
				case PathIterator.SEG_CLOSE:
					this.drawLine(currentPos, firstPos, color, 1);
					break;
				case PathIterator.SEG_QUADTO:
					System.out.println("implementme: DefaultGraphicsCanvas.drawShape.SEG_QUADTO");
					break;
				case PathIterator.SEG_CUBICTO:
					System.out.println("implementme: DefaultGraphicsCanvas.drawShape.SEG_CUBICTO");
					break;
				}
				it.next();
			}
		}
	}

	private java.awt.Color calcAWTColor(Color color) {
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	public int getVisibleWidth() {
		java.awt.Rectangle rect = graphics.getClipBounds();
		return rect.width;
	}

	public int getVisibleHeight() {
		java.awt.Rectangle rect = graphics.getClipBounds();
		return rect.height;
	}	

}
