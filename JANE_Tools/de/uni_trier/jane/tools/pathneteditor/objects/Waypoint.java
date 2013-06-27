package de.uni_trier.jane.tools.pathneteditor.objects;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class Waypoint extends PathNetObject implements PathNetConstants {
	
	// static variables
	static private int objNo = 0;
	
	// datastructure variables
	protected Point position = new Point(0,0);
	protected int symbol_size = Settings.getInt(DEFAULT_WAYPOINT_SIZE);
	
	protected Shape		outline_p = null;
	protected Shape		selection_outline_p = null;	
	
	// constructors
	public Waypoint(String id) {
		super(id);
		this.objectColor = Settings.getColor(DEFAULT_WAYPOINT_COLOR);
	}
	
	public Waypoint() {
		this("WP:" +(objNo++));	
	}	
	
	public Waypoint(int x, int y) {
		this();
		position.move(x, y);		
		
		calculate_outlines();
	}
	
	public Waypoint(int x, int y, String name) {		
		this(x, y);
		this.objDescription = name;
	}
		
	public void setPosition(int x, int y) {
		setPosition(new Point(x,y));	
	}

	public void setPosition(Point p) {
		Point old_position = new Point(position);
		position.setLocation(p);
		
		calculate_outlines();		
		
		firePropertyChanged(ACTION_POSITION_CHANGED, old_position, position );	
	}
	
	/**
	 * @deprecated Use getPosition() instead
	 */
	public int getX() {
		return position.x;
	}
	
	/**
	 * @deprecated Use getPosition() instead
	 */
	public int getY() {
		return position.y;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public int getObjectType() {	return PathNetConstants.WAYPOINT; }
	public Shape getObjectShape() {
		if (outline_p == null)
			calculate_outlines();
		
		return outline_p;
	}
	
	public Rectangle getBounds() {
		return new Rectangle( position.x - symbol_size/2, position.y - symbol_size/2, symbol_size, symbol_size);
	}
	
	public void paint(Graphics2D g, int draw_style) {
		// draw shape
		Shape s = getObjectShape();
		switch(draw_style) {
			case DRAW_STYLE_SELECTED:
				// paint selection_outline_p first
				g.setColor(Settings.getColor(DEFAULT_SELECTION_COLOR));
				g.fill(selection_outline_p);
			default:
				// draw polygon area
				g.setColor(objectColor);		
				g.fill(s);
		}				
	}
	
	/**
	 * returns a point where edges can connect to (for a dot it's just the dot itself, for a polygon of a target object it's the
	 * point of the polygon where the edge is connected to [door]). 
	 * @param edge the edge for which the point to connect to is requested
	 * @return	the point to connect to
	 * @deprecated Use getPosition() instead
	 */
	public Point getBorderPoint() {
		return getPosition();
	}
	
	public void setSymbolSize(int size) {
		int old_size = this.symbol_size;
		
		this.symbol_size = (size>0?size:Settings.getInt(DEFAULT_WAYPOINT_SIZE));
		outline_p = null;
		
		firePropertyChanged(ACTION_SYMBOL_SIZE_CHANGED, new Integer(old_size), new Integer(size));
	}
	public int getSymbolSize() { return symbol_size; }

	public String toXML() {
		return null;
	}
	
	public String getTooltipText(PathNetModel model) {
		return 
			"<html>Waypoint <b>"+getDescription()+"[" + getID() + "]</b><br>No. of edges: " + model.getEdges(this).length + "</html>";
	}
	
	public String toString() {
		return super.toString() +
			"; WAYPOINT (position: " + position + ")";
	}
	
	public boolean containsPoint(Point p) {
		return (selection_outline_p.contains(p));
	}
	
	private void calculate_outlines() {
		// the displayed outline
		outline_p = new Ellipse2D.Float(
				position.x - symbol_size / 2, position.y - symbol_size / 2, symbol_size, symbol_size
		);
		
		// the selection_outline
		int overhead = Settings.getInt(DEFAULT_SELECTION_OVERHEAD) + symbol_size;
		selection_outline_p = new Ellipse2D.Float(
				position.x - overhead/2,	position.y - overhead/2,
				overhead, 			overhead
		);
	}

	public void applyFactor(double factor) {
		setPosition((int)((double)position.x*factor), (int)((double)position.y*factor));
		symbol_size = (int)((double)symbol_size*factor);
		calculate_outlines();
	}
}
