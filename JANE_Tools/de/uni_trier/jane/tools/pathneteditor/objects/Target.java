package de.uni_trier.jane.tools.pathneteditor.objects;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public final class Target extends Waypoint {

	// static variables
	static private int objNo = 0;
	
	// constructors
	public Target(String id) {
		super(id);
		this.objectColor = Settings.getColor(DEFAULT_TARGET_COLOR);
		this.symbol_size = Settings.getInt(DEFAULT_TARGET_SIZE);
	}
	
	public Target() {
		this("T:" + (objNo++) );
	}
	
	public Target(int x, int y) {
		this();
		position = new Point(x, y);
		
		calculate_outlines();
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
	
	public int getMinX() {
		return position.x;
//		if (outline==null||outline.npoints<1)
//			return -1;
//		int min = outline.xpoints[0];
//		for (int i=1; i<outline.npoints; i++)
//			if (outline.xpoints[i]<min)
//				min=outline.xpoints[i];
//		return min;
	}
	
	public int getMinY() {
		return position.y;
//		if (outline==null||outline.npoints<1)
//			return -1;
//		int min = outline.ypoints[0];
//		for (int i=1; i<outline.npoints; i++)
//			if (outline.ypoints[i]<min)
//				min=outline.ypoints[i];
//		return min;
	}
	
	public int getMaxX() {
		return position.x;
//		int max = -1;
//		for (int i=0; outline!=null&&i<outline.npoints; i++)
//			if (outline.xpoints[i]>max)
//				max = outline.xpoints[i];
//		return max;
	}
	
	public int getMaxY() {
		return position.y;
//		int max = -1;
//		for (int i=0; outline!=null&&i<outline.npoints; i++)
//			if (outline.ypoints[i]>max)
//				max = outline.ypoints[i];
//		return max;
	}
	
	public int getObjectType() {	return PathNetConstants.TARGET;	}
	public Shape getObjectShape() {
		if (outline_p == null)
			calculate_outlines();
		return outline_p;
	}
	
	public String getTooltipText(PathNetModel model) {
		return
			"<html>Target <b>"+getDescription()+"[" + getID() + "]</b><br>No of edges: " + model.getEdges(this).length + "</html>";
	}
	
	public String toString() {
		return super.toString() +
			"; TARGET ";
	}
	
	public boolean containsPoint(Point p) {
		return (selection_outline_p.contains(p));
	}
	
	public void setPosition(Point p) {
		super.setPosition(p);
		calculate_outlines();
	}
	
//	public void setPosition(int x, int y) {
//		int old_x = this.x, old_y = this.y;
//		
//		this.x = x;
//		this.y = y;
//		
//		calculate_outlines();		
//		
//		firePropertyChanged(ACTION_POSITION_CHANGED, new Point(old_x, old_y), new Point(x, y) );
//	}
	
	private void calculate_outlines() {
		// calculate displayed outline
		outline_p = new Rectangle2D.Float (
				position.x - symbol_size / 2, position.y - symbol_size / 2, symbol_size, symbol_size
		);
		
		// calculate selection outline
		int overhead = Settings.getInt(DEFAULT_SELECTION_OVERHEAD) + symbol_size;
		selection_outline_p = new Rectangle2D.Float(
				position.x - overhead/2, position.y - overhead/2,
				overhead, overhead
		);		
	}
	
}
