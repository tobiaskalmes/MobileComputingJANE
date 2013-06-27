/*
 * Created on Feb 21, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.export;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

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
import de.uni_trier.jane.tools.pathneteditor.objects.Edge;
import de.uni_trier.jane.tools.pathneteditor.objects.Target;
import de.uni_trier.jane.tools.pathneteditor.objects.Waypoint;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLPathNetFilter_v2 implements ModelImportExportFilter, XMLConstants, PathNet {
	
	protected String		baseUri;	
	
	private HashMap			waypointMap = new HashMap();
	private HashMap			edgeMap		= new HashMap();
		
	public XMLPathNetFilter_v2(String baseUri) {
		super();		
		this.baseUri = baseUri;
	}

	public XMLPathNetFilter_v2() {
		this(Settings.getString(PathNetConstants.DTD_PATH));
	}
	
	/* (non-Javadoc)
	 * @see pathneteditor.export.ModelImportExportFilter#loadModelData(java.io.InputStream)
	 */
	public boolean loadModelData(InputStream source, PathNetModel model) {		
		// indicates if anything goes wrong
		boolean result = true;
		
		// disable events if possible
		if (model instanceof DefaultPathNetModel)
			((DefaultPathNetModel)model).enableModelEvents(false);
		
		// use the DOM implemetation for getting the model
		InputSource in = new InputSource(source);
		if (baseUri != null) in.setSystemId(baseUri);
		
		Document xmlDocument;	
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
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
		
		// get model name
		NodeList root = xmlDocument.getElementsByTagName(XML_ROOT_V2);
		model.setModelName( getString( XML_ATT_NAME, root.item(0).getAttributes()) );
		
		// get all waypoints
		NodeList wps = xmlDocument.getElementsByTagName(XML_WAYPOINT);
		for (int i=0; i<wps.getLength(); i++) {
			Node wp = wps.item(i);
			
			// retrieve attributes of waypoint wp
			NamedNodeMap attrs = wp.getAttributes();
			
			// create new waypoint and set attributes
			Waypoint w = new Waypoint(getString(XML_ATT_NAME, attrs));
			w.setDescription(
					getString(XML_ATT_DESCR, attrs)
			);
			w.setPosition(
					getInt(XML_ATT_POS_X, attrs),
					getInt(XML_ATT_POS_Y, attrs)
			);
			w.setSymbolSize(
					getInt(XML_ATT_WIDTH, attrs)
			);
			
			// add to model
			result &= model.add(w);
			waypointMap.put( w.getID(), w );
		}
		
		// debug
		System.err.println("Finished parsing of waypoints, found " + waypointMap.size());
		
		// get all targets
		NodeList tgs = xmlDocument.getElementsByTagName(XML_TARGET);
		for (int i=0; i<tgs.getLength(); i++) {
			Node tg = tgs.item(i);
			
			// retrieve attributes of target tg
			NamedNodeMap attrs = tg.getAttributes();
			
			// create new target and set attributes
			Target t = new Target(getString(XML_ATT_NAME, attrs));
			t.setDescription(
					getString(XML_ATT_DESCR, attrs)
			);
			t.setPosition(
					getInt(XML_ATT_POS_X, attrs),
					getInt(XML_ATT_POS_Y, attrs)
			);
			t.setSymbolSize(
					getInt(XML_ATT_WIDTH, attrs)
			);
						
			// add to model and map			
			result &= model.add(t);
			
			waypointMap.put( t.getID(), t );
		}
		
		// debug
		System.err.println("Finished parsing of targets, found " + tgs.getLength());
		
		// get all edges
		NodeList eds = xmlDocument.getElementsByTagName(XML_EDGE);
		for (int i=0; i<eds.getLength(); i++) {
			Node ed = eds.item(i);
			
			// retrieve attributes for edge ed
			NamedNodeMap attrs = ed.getAttributes();
			
			// create new edge and set attributes
			Waypoint	edgeSource, edgeTarget;
			Edge e = new Edge(
					getString(XML_ATT_NAME, attrs), 
					edgeSource = getWaypoint( getString(XML_ATT_EDGE_SOURCE, attrs) ),
					edgeTarget = getWaypoint( getString(XML_ATT_EDGE_TARGET, attrs) )
			);
						
			e.setDescription(
					getString(XML_ATT_DESCR, attrs)
			);
					
			// get inner points and set them
			NodeList ips = ed.getChildNodes();
			for (int j=0; j<ips.getLength(); j++) {
				if (ips.item(j).getNodeType() != Node.ELEMENT_NODE) continue;
				
				NamedNodeMap ip_attrs = ips.item(j).getAttributes();
				e.addInnerPoint(
						new Point(
								getInt(XML_ATT_POS_X, ip_attrs),
								getInt(XML_ATT_POS_Y, ip_attrs)
						),
						getInt(XML_ATT_WIDTH, ip_attrs)
				);				
			}
			
			// add to model and map
			result &= model.add(e);
			edgeMap.put( e.getID(), e);			
		}
		
		// debug
		System.err.println("Finished parsing of edges, found " + edgeMap.size());
				
		// get probs
		NodeList probs = xmlDocument.getElementsByTagName(XML_ROUTING);

		for (int i=0; i<probs.getLength(); i++) {
			// this probability
			Node prob = probs.item(i);			
			
			// get source and target
			Waypoint trg = getWaypoint( getString(XML_ATT_TARGET, prob.getAttributes()) );
			Waypoint src = getWaypoint( getString(XML_ATT_NAME, prob.getParentNode().getAttributes()) );
						
			// get prob values with associated edges and add them to model
			NodeList trg_probs = prob.getChildNodes();
			for (int j=0; j<trg_probs.getLength(); j++) {
				Node trg_prob = trg_probs.item(j);
				if (trg_prob.getNodeType() != Node.ELEMENT_NODE) continue;
				
				Edge   edg = getEdge( getString(XML_ATT_EDGE, trg_prob.getAttributes()) );
				double prb = getDouble( XML_ATT_PROB, trg_prob.getAttributes() );
				
				result &= model.setProb( src, (Target)trg, edg, prb );
			}
			
		}
		
		// reenable events if possible
		if (model instanceof DefaultPathNetModel)
			((DefaultPathNetModel)model).enableModelEvents(true);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see pathneteditor.export.ModelImportExportFilter#saveModelData(java.io.OutputStream)
	 */
	public boolean saveModelData(OutputStream target, PathNetModel model) {
		StringBuffer sb = new StringBuffer();
		
		// Header
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE PATHNET_V2 SYSTEM \"PathNet_v2.dtd\">\n");
		sb.append('\n');
		
		// Document root
		sb.append("<" + XML_ROOT_V2 + " " + XML_ATT_NAME + "=\"" + model.getModelName() + "\">\n");
		
		// Targets
		sb.append("\t<!-- The target nodes -->\n");
		
		Target[] targets = model.getTargets();
		for (int i=0; i<targets.length; i++) {
			Target t = targets[i];
			sb.append('\t');
			sb.append("<"+XML_TARGET+" ")
				.append(XML_ATT_NAME+"=\""+t.getID()+"\" ")
				.append(XML_ATT_DESCR+"=\""+t.getDescription()+"\" ")
				.append(XML_ATT_POS_X+"=\""+t.getPosition().x/100.0+"\" ")
				.append(XML_ATT_POS_Y+"=\""+t.getPosition().y/100.0+"\" ")
				.append(XML_ATT_WIDTH+"=\""+t.getSymbolSize()/100.0+"\" ")
				.append(">\n");
			
			getXmlForWaypoint(t, sb, model);
			
			sb.append("\t</" + XML_TARGET + ">\n");
		}
	
		sb.append("\n\t<!-- The waypoint nodes -->\n");
		
		Waypoint[] waypoints = model.getWaypoints();
		for (int i=0; i<waypoints.length; i++) {
			Waypoint t = waypoints[i];
			sb.append('\t');
			sb.append("<"+XML_WAYPOINT+" ")
				.append(XML_ATT_NAME+"=\""+t.getID()+"\" ")
				.append(XML_ATT_DESCR+"=\""+t.getDescription()+"\" ")
				.append(XML_ATT_POS_X+"=\""+t.getPosition().x/100.0+"\" ")
				.append(XML_ATT_POS_Y+"=\""+t.getPosition().y/100.0+"\" ")
				.append(XML_ATT_WIDTH+"=\""+t.getSymbolSize()/100.0+"\" ")
				.append(">\n");
			
			getXmlForWaypoint(t, sb, model);
			
			sb.append("\t</" + XML_WAYPOINT + ">\n");
		}
		
		sb.append("\n\t<!-- The edges -->\n");
		
		Edge[] edges = model.getEdges();
		for (int i=0; i<edges.length; i++) {
			Edge e = edges[i];
			
			sb.append('\t');
			sb.append("<"+XML_EDGE+" ")
				.append(XML_ATT_NAME+"=\""+e.getID()+"\" ")
				.append(XML_ATT_DESCR+"=\""+e.getDescription()+"\" ")
				.append(XML_ATT_EDGE_SOURCE+"=\""+e.getSource().getID()+"\" ")
				.append(XML_ATT_EDGE_TARGET+"=\""+e.getTarget().getID()+"\" ")
				.append('>');
				
			Point2D[] innerPoints = e.getInnerPoints();
			for (int j=0; j<innerPoints.length; j++) {
				sb.append("\n\t\t")
					.append("<"+XML_INNER_POINT+" ")
					.append(XML_ATT_POS_X+"=\""+innerPoints[j].getX()/100.0+"\" ")
					.append(XML_ATT_POS_Y+"=\""+innerPoints[j].getY()/100.0+"\" ")
					.append(XML_ATT_WIDTH+"=\""+e.getWidth(j+1)/100.0+"\" ")
					.append("/>");			
			}
						
			if (innerPoints.length!=0)
				sb.append("\n\t");
			
			sb.append("</"+XML_EDGE+">\n");			
		}
		
		sb.append("</"+XML_ROOT_V2+">\n");
		
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
					return sb.indexOf("DOCTYPE PATHNET_V2") >= 0;					
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
	
	private Waypoint getWaypoint(String id) {
		Object o = waypointMap.get(id);
		return (o==null)?null:(Waypoint)o;
	}

	private Edge getEdge(String id) {
		Object o = edgeMap.get(id);
		return (o==null)?null:(Edge)o;
	}
	
	private void getXmlForWaypoint(Waypoint t, StringBuffer sb, PathNetModel model) {
		//	 Routing from waypoint to targets
		Target[] targets = model.getTargets();
		for (int j=0; j<targets.length; j++) {
			Target s = targets[j];
			
			if (t==s) continue;
			
			sb.append("\t\t");
			sb.append("<"+XML_ROUTING+" ")
				.append(XML_ATT_TARGET+"=\"" + s.getID() + "\" ")
				.append(">\n");
			
			// All probs for each path from t to s
			Edge[] edges = model.getEdges(t);
			for (int k=0; k<edges.length; k++) {
				Edge e = edges[k];
				
				// zero values will not be saved
				if (model.getProb(t, s, e) == 0.0)
					continue;
				
				sb.append("\t\t\t");
				sb.append("<"+XML_WAYPOINT_PROB+" ")
					.append(XML_ATT_PROB+"=\"" + model.getProb(t, s, e) + "\" ")
					.append(XML_ATT_EDGE+"=\"" + e.getID() + "\" ")
					.append("/>\n");
			}
			
			sb.append("\t\t</" + XML_ROUTING + ">\n");						
		}
	}
}
