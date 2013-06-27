package de.uni_trier.jane.tools.pathneteditor.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;


public abstract class PathNetObject implements PathNetConstants {
	// datastructure variables
	protected List		listenerList = new LinkedList();
	protected Color		objectColor = Color.black;
	protected String	objDescription = "";	
	private String		objID = "";	

	// abstract methods
	public abstract int 	getObjectType();
	public abstract Shape	getObjectShape();
	public abstract boolean containsPoint( Point p );
	public abstract void	paint(Graphics2D g, int draw_style);
	public abstract Rectangle	getBounds();
	public abstract String	getTooltipText(PathNetModel model);
	public abstract String	toXML();

	// constructors
	public PathNetObject (String id) {
		this.objID = id;		
	}
	
	public PathNetObject (String id, String objDescription) {
		this(id);
		this.objDescription = objDescription;
	}
	
	// protected methods
	protected void	firePropertyChanged(int actionType, Object old_val, Object new_val) {
		ObjectEvent event = new ObjectEvent(this, actionType, old_val, new_val);		
		Iterator it = listenerList.iterator();
		while (it.hasNext()) {
			((ObjectListener)it.next()).propertyChanged(event);
		}
	}
			
	// public methods
	public void addObjectListener(ObjectListener listener) { listenerList.add(listener); }
	public void removeObjectListener(ObjectListener listener) { listenerList.remove(listener); }
	
	public void setDescription(String description) {
		String old_descr = objDescription;
		this.objDescription = description;
		firePropertyChanged(ACTION_DESCRIPTION_CHANGED, old_descr, description);
	}
	public String	getDescription()					{	return objDescription==""?objID:objDescription; }
	
	public String	getID()	{ return objID; }
	
	public Color getObjectColor() {
		return objectColor;
	}
	
	public void setObjectColor(Color objectColor) {
		Color oldColor = this.objectColor;
		this.objectColor = objectColor;
		firePropertyChanged(ACTION_COLOR_CHANGED, oldColor, objectColor);
	}
	
	public String	toString() {
		return super.toString() + "; PATHNETOBJECT (description: " + getDescription() + ")";
	}
	/**
	 * @param factor
	 */
	public abstract void applyFactor(double factor);
}
