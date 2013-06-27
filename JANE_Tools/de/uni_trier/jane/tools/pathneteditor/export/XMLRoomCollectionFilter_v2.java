/*
 * Created on Feb 21, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.export;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.constants.XMLConstants;
import de.uni_trier.jane.tools.pathneteditor.model.DefaultPathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.*;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLRoomCollectionFilter_v2 implements ModelImportExportFilter, XMLConstants, RoomCollection {

	protected String		baseUri;
	
	public XMLRoomCollectionFilter_v2(String baseUri) {
		super();
		this.baseUri = baseUri;
	}
	
	public XMLRoomCollectionFilter_v2() {
		this(Settings.getString(PathNetConstants.DTD_PATH));
	}

	/* (non-Javadoc)
	 * @see pathneteditor.export.ModelImportExportFilter#loadModelData(java.io.InputStream)
	 */
	public boolean loadModelData(InputStream source, PathNetModel model) {
		// indicates if anything goes wrong
		boolean result = true;
		
		// use the DOM implemetation for getting the areas
		InputSource in = new InputSource(source);
		if (baseUri != null) in.setSystemId(baseUri);
		
		Document xmlDocument;	
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmlDocument = builder.parse(in);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		// TODO: Name of room collection ignored yet. Necessary?
		
		// get the areas
		NodeList areas = xmlDocument.getElementsByTagName(XML_AREA);
		for (int i=0; i<areas.getLength(); i++) {
			Node area = areas.item(i);
			
			// get attributes for this area
			NamedNodeMap attr = area.getAttributes();
			
			// get all entries (although we only need entry #1)
			NodeList doors = area.getChildNodes();						
			Vector entries = new Vector();
			for (int j=0; j<doors.getLength(); j++) {
				if (doors.item(j).getNodeType() != Node.ELEMENT_NODE) continue;				
				entries.add( new Point(
						getInt( XML_ATT_POS_X, doors.item(j).getAttributes()),
						getInt( XML_ATT_POS_Y, doors.item(j).getAttributes())
				));
				// TODO: Z coordinate ignored yet
			}
						
			// get areas name
			String area_name = getString( XML_ATT_NAME, attr );
			
			// if there are no entries: return no points
			if (entries.size() == 0) {
				System.err.println(getClass().getName()+".loadAreas(): No entries defined for area: " + area_name );
				result = false;
				continue;
			}
			
			// get first entry of area, and calculate delta values		
			Point ref = (Point)(entries.get(0));
			
			String refName = area_name + ":1";	// defined by chair of prof. sturm		
			Waypoint wp = getWaypoint( refName, model );
			if ( wp == null ) {
				System.err.println(getClass().getName()+".loadAreas(): No correct named waypoint found. name:" +refName+ ", area:" +area_name);
				result = false;
				continue;
			}
				
			int area_x = wp.getPosition().x - ref.x;
			int area_y = wp.getPosition().y - ref.y;
												
			// set area points
			Area a = new Area( area_name );
			a.addVertex( new Point(area_x, area_y ) );
			a.addVertex( new Point(area_x + getInt( XML_ATT_AREA_WIDTH, attr ), area_y));
			a.addVertex( new Point(area_x + getInt( XML_ATT_AREA_WIDTH, attr ), area_y + getInt( XML_ATT_AREA_DEPTH, attr )));
			a.addVertex( new Point(area_x, area_y + getInt( XML_ATT_AREA_DEPTH, attr)));
			
			// other properties
			a.setDescription(
					getString(XML_ATT_DESCR, attr)
			);
			
			// add area to model
			model.add(a);
			
			// concatenate area with its targets
			concatenateArea(a, model);
		}

		//debug
		System.out.println("Areas loaded, size: " + model.getAreas().length);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see pathneteditor.export.ModelImportExportFilter#saveModelData(java.io.OutputStream)
	 */
	public boolean saveModelData(OutputStream target, PathNetModel model) {
		// TODO: The RoomCollection.dtd cannot hold the complete outline of a polygonal area
		// So the area's informations have to be reduced
		StringBuffer sb = new StringBuffer();
		
		// Header
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE ROOMCOLLECTION_V2 SYSTEM \"RoomCollection_v2.dtd\">\n");
		sb.append('\n');
		
		// Document root, writes model name as collection name TODO: Has this to be changed?
		sb.append("<" + XML_AREA_ROOT_V2 + " " + XML_ATT_NAME + "=\"" + model.getModelName() + "\">\n");
		
		// Areas
		sb.append("\t<!-- The area nodes -->\n");
		
		Area[] areas = model.getAreas();
		for (int i=0; i<areas.length; i++) {
			Area a = areas[i];
			Rectangle bounds = a.getBounds();
			
			// TODO: Height is set to 0. Is height necessary?
			sb.append('\t');
			sb.append("<"+XML_AREA+" ")
				.append(XML_ATT_NAME+"=\""+a.getID()+"\" ")
				.append(XML_ATT_DESCR+"=\""+a.getDescription()+"\" ")
				.append(XML_ATT_AREA_WIDTH+"=\""+bounds.width/100.0+"\" ")
				.append(XML_ATT_AREA_DEPTH+"=\""+bounds.height/100.0+"\" ")
				.append(XML_ATT_AREA_HEIGHT+"=\"0.0\" ")
				.append(">\n");
			
			Target[] entries = model.getTargets(a);
			for (int j=0; j<entries.length; j++) {
				Target t = entries[j];
				int relX = t.getPosition().x - bounds.x;
				int relY = t.getPosition().y - bounds.y;
				
				//TODO: Z position is set to 0.0. Is z position necessary?
				sb.append("\t\t");
				sb.append("<"+XML_ENTRY+" ")
					.append(XML_ATT_POS_X+"=\""+relX/100.0+"\" ")
					.append(XML_ATT_POS_Y+"=\""+relY/100.0+"\" ")
					.append(XML_ATT_POS_Z+"=\"0.0\" ")
					.append("/>\n");
			}
			
			sb.append("\t</"+XML_AREA+">\n");
		}
		
		sb.append("</"+XML_AREA_ROOT_V2+">\n");
		
		// Save to stream
		try {
			for (int i=0; i<sb.length(); i++)				
				target.write(sb.charAt(i));
		} catch(IOException e) {
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see pathneteditor.export.ModelImportExportFilter#acceptsStream(java.io.InputStream)
	 */
	public boolean acceptsStream(InputStream source) {
		try {
			int b;
			StringBuffer sb = new StringBuffer();
			
			while((b = source.read()) != -1) {
				sb.append((char)b);
				
				if (b == '>' && sb.indexOf("<!DOCTYPE") >= 0) {					
					return sb.indexOf("DOCTYPE ROOMCOLLECTION_V2") >= 0;					
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		
			return false;
		}
				
		return false;
	}
	
	private int getInt(String attr_name, NamedNodeMap attrs) {				
		return (int)( getDouble(attr_name, attrs)*100 );
	}
	
	private double getDouble(String attr_name, NamedNodeMap attrs) {
		double result = 0.0;
		
		try {
			result = Double.parseDouble( getString(attr_name, attrs) );			
		} catch(NumberFormatException e) {
			System.err.println("Illegal DOUBLE value for " + attr_name + ": " + getString(attr_name, attrs));
		}
		
		return result;
	}
	
	private String getString(String attr_name, NamedNodeMap attrs) {
		if (attrs == null) {
			System.err.println("Attributes is null, attr_name: " + attr_name);
			return "";
		}
		
		Node item = attrs.getNamedItem(attr_name);
		
		if (item == null) {
			System.err.print("No item found: "+attr_name+", avaiable: ");
			for (int i=0; i<attrs.getLength(); i++)
				System.err.print(attrs.item(i).getNodeName() + " ");
			System.err.println();
			return "";
		}			
		
		return item.getNodeValue();
	}
	
	private Waypoint getWaypoint(String id, PathNetModel model) {
		PathNetObject o = model.getObjectById(id);
		return (o==null)?null:(Waypoint)o;
	}

	private void concatenateArea(Area a, PathNetModel model) {
		// get Area name
		String name = a.getID();
		
		// disable model events, if possible
		boolean oldEventState = (model instanceof DefaultPathNetModel) ? ((DefaultPathNetModel)model).isModelEventsEnabled() : true;
		if (model instanceof DefaultPathNetModel)
			((DefaultPathNetModel)model).enableModelEvents(false);
		
		// look for corresponding targets
		Target[] targets = model.getTargets();
		for (int i=0; i<targets.length; i++) {
			if (targets[i].getID().startsWith(name+":")) {
				model.connectAreaToTarget(a, targets[i]);
			}				
		}
		
		// reenable model events, if possible
		if (model instanceof DefaultPathNetModel)
			((DefaultPathNetModel)model).enableModelEvents(oldEventState);
		
		/*
		 * TODO Note: The method above is very, very inefficient: The overall runtime will be #areas * O(n)
		 */
	}
}
