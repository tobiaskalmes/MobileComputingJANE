/*****************************************************************************
 * 
 * XMLRenderShape.java
 * 
 * $Id: XMLRenderShape.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import java.io.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

public class XMLRenderShape implements Shape {

	private final static String VERSION = "$Id: XMLRenderShape.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private String fileName;
	private Shape shape;
	private Rectangle rectangle;

	public XMLRenderShape(String fileName) {
		this.fileName = fileName;
		shape = null;
		rectangle = null;
	}




	public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		prepareShape();
		shape.visualize(position,worldspace,addressPositionMap);
		
	}

	public Rectangle getRectangle(Position position, Matrix matrix) {
		prepareShape();
		return rectangle.add(position);
	}


	private Shape parseCommand(Element command) {
		String name = command.getNodeName();
		if(name.equalsIgnoreCase("drawLine")) {
			Position from = parsePosition((Element)(command.getElementsByTagName("position").item(0)));
			Position to = parsePosition((Element)(command.getElementsByTagName("position").item(1)));
			Color color = parseColor((Element)(command.getElementsByTagName("color").item(0)));
			extendRectangle(new Rectangle(from, to));
			return new LineShape(from, to, color);
		}
		else if(name.equalsIgnoreCase("drawRectangle")) {
			Rectangle rectangle = parseRectangle((Element)(command.getElementsByTagName("rectangle").item(0)));
			Color color = parseColor((Element)command.getElementsByTagName("color").item(0));
			boolean filled = parseBoolean((Element)command.getElementsByTagName("boolean").item(0));
			extendRectangle(rectangle);
			return new RectangleShape(rectangle.getCenter(), rectangle.getExtent(), color, filled);
		}
		else if(name.equalsIgnoreCase("drawEllipse")) {
			Rectangle rectangle = parseRectangle((Element)(command.getElementsByTagName("rectangle").item(0)));
			Color color = parseColor((Element)command.getElementsByTagName("color").item(0));
			boolean filled = parseBoolean((Element)command.getElementsByTagName("boolean").item(0));
			extendRectangle(rectangle);
			return new EllipseShape(rectangle.getCenter(), rectangle.getExtent(), color, filled);
		}
		else if(name.equalsIgnoreCase("drawText")) {
			String string = parseString((Element)(command.getElementsByTagName("string").item(0)));
			Rectangle rectangle = parseRectangle((Element)(command.getElementsByTagName("rectangle").item(0)));
			Color color = parseColor((Element)command.getElementsByTagName("color").item(0));
			extendRectangle(rectangle);
			return new TextShape(string, rectangle, color);
		}
		else if(name.equalsIgnoreCase("drawImage")) {
			String string = parseString((Element)(command.getElementsByTagName("string").item(0)));
			Rectangle rectangle = parseRectangle((Element)(command.getElementsByTagName("rectangle").item(0)));
			extendRectangle(rectangle);
			return new ImageShape(string, rectangle.getCenter(), rectangle.getExtent());
		}
		else {
			throw new IllegalArgumentException("Not yet implemented!");
		}
	}

	private void prepareShape() {
		if(shape == null) {
			ShapeCollection sc = new ShapeCollection();
			Position nullPosition = new Position(0,0);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			dbf.setIgnoringComments(true);
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				Document doc = db.parse(fileName);
				Element canvasCommandList = (Element)doc.getElementsByTagName("canvasCommandList").item(0);
				Node node = canvasCommandList.getFirstChild();
				while(node != null) {
					if (node instanceof Element) {
						Element element = (Element)node;
						sc.addShape(parseCommand(element), nullPosition);
					}
					node = node.getNextSibling();
				}
			}
			catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			catch (SAXException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			shape = sc;
		}
	}

	private void extendRectangle(Rectangle r) {
		if(rectangle == null) {
			rectangle = r;
		}
		else {
			rectangle = rectangle.union(r);
		}
	}

	private String parseValue(Element articleElement, String tagName) {
		Element element = (Element)(articleElement.getElementsByTagName(tagName).item(0));
		CharacterData cdata = (CharacterData)element.getChildNodes().item(0);
		return cdata.getData();
	}

	private Position parsePosition(Element element) {
		double x = Double.parseDouble(parseValue(element, "x"));
		double y = Double.parseDouble(parseValue(element, "y"));
		return new Position(x, y);
	}

	private Color parseColor(Element element) {
		int r = Integer.parseInt(parseValue(element, "r"));
		int g = Integer.parseInt(parseValue(element, "g"));
		int b = Integer.parseInt(parseValue(element, "b"));
		return new Color(r, g, b);
	}

	private Rectangle parseRectangle(Element element) {
		Position bottomLeft = parsePosition((Element)(element.getElementsByTagName("position").item(0)));
		Position topRight = parsePosition((Element)(element.getElementsByTagName("position").item(1)));
		return new Rectangle(bottomLeft, topRight);
	}

	private String parseString(Element element) {
		return parseValue(element, "string");
	}

	private boolean parseBoolean(Element element) {
		CharacterData cdata = (CharacterData)element.getChildNodes().item(0);
		return Boolean.valueOf(cdata.getData()).booleanValue();
	}


}
