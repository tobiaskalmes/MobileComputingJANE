/*****************************************************************************
 * 
 * XMLRenderCanvas.java
 * 
 * $Id: XMLRenderCanvas.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.visualization;

import java.awt.Image;
import java.io.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;

/**
 * This class is used to render canvas operations into XML.
 */
public class XMLRenderCanvas implements Canvas {

	private final static String VERSION = "$Id: XMLRenderCanvas.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";
	
	private static final String INDENT = "    ";

	private String fileName;
	private PrintWriter output;
	private boolean finished;

	private Matrix transformation = Matrix.identity3d();


	/**
	 * Constructs an new <code>XMLRenderCanvas</code>.
	 * @param fileName the name of the XML file.
	 */
	public XMLRenderCanvas(String fileName) {
		this.fileName = fileName;
		output = null;
		finished = false;
	}

	/**
	 * This method has to be called before any canvas command is invoked.
	 * The XML file is opened and all specific XML header lines are written to
	 * the file.
	 */
	public void beginRendering() {
		if(finished) {
			throw new IllegalStateException("The file was previously written.");
		}
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			output = new PrintWriter(fos);
			output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			output.println("<!DOCTYPE canvasCommandList SYSTEM \"CanvasCommandList.dtd\">");
			output.println("<canvasCommandList>");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**	
	 * This method has to be called after all canvas commands. All
	 * specific XML footer lines are written to the and the file and the file
	 * is closed.
	 */
	public void endRendering() {
		finished = true;
		output.println("</canvasCommandList>");
		output.flush();
		output.close();
	}
	
	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawLine(PositionBase, PositionBase, Color, float)
	 */
	public void drawLine(PositionBase from, PositionBase to, Color color, float lineWidth) {
		checkValidity();
		output.println(INDENT + "<drawLine>");
		output.print(INDENT + INDENT);
		writePosition(from);
		output.println("");
		output.print(INDENT + INDENT);
		writePosition(to);
		output.println("");
		output.print(INDENT + INDENT);
		writeColor(color);
		output.println("");
		output.println(INDENT + "</drawLine>");
		
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawRectangle(de.uni_trier.ubi.appsim.kernel.basetype.Rectangle, de.uni_trier.ubi.appsim.kernel.visualization.Color, boolean, float)
	 */
	public void drawRectangle(Rectangle rectangle, Color color, boolean filled, float lineWidth) {
		checkValidity();
		output.println(INDENT + "<drawRectangle>");
		writeRectangleColorFilled(rectangle, color, filled);
		output.println("");
		output.println(INDENT + "</drawRectangle>");
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawEllipse(de.uni_trier.ubi.appsim.kernel.basetype.Rectangle, de.uni_trier.ubi.appsim.kernel.visualization.Color, boolean)
	 */
	public void drawEllipse(Rectangle rectangle, Color color, boolean filled) {
		checkValidity();
		output.println(INDENT + "<drawEllipse>");
		writeRectangleColorFilled(rectangle, color, filled);
		output.println("");
		output.println(INDENT + "</drawEllipse>");
	}

	public void drawPolygon(PositionIterator positionIterator, Color color, boolean filled) {

		checkValidity();
		output.println(INDENT + "<drawPolygon>");
		output.print(INDENT + INDENT);
		
		output.print("<positionList>");
		while(positionIterator.hasNext()){
			output.println("");
			output.print(INDENT + INDENT + INDENT);
			
			writePosition(positionIterator.next());
		}
		output.println("");
		output.print(INDENT + INDENT);

		output.print("</positionList>");
		output.println("");
		output.print(INDENT + INDENT);
		writeColor(color);
		output.println("");
		output.print(INDENT + INDENT);
		output.print("<boolean>");
		output.print(filled);
		output.print("</boolean>");
		output.println("");
		output.println(INDENT + "</drawPolygon>");
		//throw new IllegalStateException("Method is not implemented.");
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawText(java.lang.String, de.uni_trier.ubi.appsim.kernel.basetype.Rectangle, de.uni_trier.ubi.appsim.kernel.visualization.Color)
	 */
	public void drawText(String text, Rectangle rectangle, Color color) {
		checkValidity();
		output.println(INDENT + "<drawText>");
		output.print(INDENT + INDENT);
		writeString(text);
		output.println("");
		output.print(INDENT + INDENT);
		writeRectangle(rectangle);
		output.println("");
		output.print(INDENT + INDENT);
		writeColor(color);
		output.println("");
		output.println(INDENT + "</drawText>");
	}

	/**
	 * @see de.uni_trier.jane.visualization.Canvas#drawImage(java.lang.String, de.uni_trier.ubi.appsim.kernel.basetype.Rectangle)
	 */
	public void drawImage(String fileName, Rectangle rectangle) {
		checkValidity();
		output.println(INDENT + "<drawImage>");
		output.print(INDENT + INDENT);
		writeString(fileName);
		output.println("");
		output.print(INDENT + INDENT);
		writeRectangle(rectangle);
		output.println("");
		output.println(INDENT + "</drawImage>");
	}

	private void checkValidity() {
		if(output == null || finished) {
			throw new IllegalStateException("Graphic operations are only allowed between start and end rendering.");
		}
	}

	private void writePosition(PositionBase position) {
		output.print("<position><x>" + position.getX() + "</x><y>" + position.getY() + "</y></position>");
	}

	private void writeRectangle(Rectangle rectangle) {
		output.print("<rectangle>");
		output.println("");
		output.print(INDENT + INDENT + INDENT);
		writePosition(rectangle.getBottomLeft());
		output.println("");
		output.print(INDENT + INDENT + INDENT);
		writePosition(rectangle.getTopRight());
		output.println("");
		output.print(INDENT + INDENT);
		output.print("</rectangle>");
	}

	private void writeColor(Color color) {
		output.print("<color><r>" + color.getRed() + "</r><g>" + color.getGreen() + "</g><b>" + color.getBlue() + "</b></color>");
	}

	private void writeRectangleColorFilled(Rectangle rectangle, Color color, boolean filled) {
		output.print(INDENT + INDENT);
		writeRectangle(rectangle);
		output.println("");
		output.print(INDENT + INDENT);
		writeColor(color);
		output.println("");
		output.print(INDENT + INDENT);
		output.print("<boolean>");
		output.print(filled);
		output.print("</boolean>");
	}

	private void writeString(String string) {
		output.print("<string>" + string + "</string>");
	}

	/**
	 * set the transformation matrix for elementary shapes
	 */
	public void setTransformation(Matrix transformation) {
		this.transformation = transformation;
	}
	/**
	 * get the transformation matrix for the canvas
	 */
	public Matrix getTransformation() {
		return transformation; 
	}


	public void drawImage(Image image, PositionBase position, Matrix matrix) {
		// TODO Auto-generated method stub
		
	}

	public int getVisibleWidth() {
		return 0;
	}

	public int getVisibleHeight() {
		return 0;
	}

}
