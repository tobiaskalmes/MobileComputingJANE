package de.uni_trier.jane.tools.pathneteditor.objects;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Vector;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.tools.GeometricTools;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


public class Area extends PathNetObject {

	// static variables
	static private int objNo = 0;
	
	// datastructure variables
	private Vector	vertices = new Vector();
	private Polygon	outline_p = null;
	private Polygon selection_outline_p = null;	
	
	// caches
	private Point[]		vertex_cache = new Point[0];
	private boolean		vertex_cache_has_changed = true;
	
	public Area() {
		this("A:"+(objNo++));
	}
	
	public Area(String id) {
		super(id);
		this.objectColor = Settings.getColor(PathNetConstants.DEFAULT_AREA_COLOR);
	}

	/**
	 * move the area according to the specified coordinates
	 * @param mx  move mx horizontally
	 * @param my  move my vertically
	 */
	public void translate(int mx, int my) {
		for (int i=0; i<vertices.size(); i++) {
			((Point)vertices.get(i)).translate(mx, my);
		}
		calculate_outlines();
	}
	
	public Object clone() {
		Area area = new Area(this.getID());
		for (int i=0; i<vertices.size(); i++)
			area.addVertex(new Point(((Point)vertices.get(i))), i);
		return area;
	}
	
	public int getObjectType() { return PathNetConstants.AREA; }
	
	public Shape getObjectShape() {
		if (outline_p==null)
			calculate_outlines();
		return outline_p;
	}
	
	public Rectangle getBounds() {
		// no vertices? "empty" rectangle
		if (vertices.size()==0)
			return new Rectangle(0,0,0,0);
		
		// get min and max values of vertices
		int minX = ((Point)(vertices.get(0))).x;
		int minY = ((Point)(vertices.get(0))).y;
		int maxX = minX;
		int maxY = minY;
		
		for (int i=1; i<vertices.size(); i++) {
			Point p = (Point)(vertices.get(i));
			if (p.x < minX) minX = p.x;
			if (p.x > maxX) maxX = p.x;
			if (p.y < minY) minY = p.y;
			if (p.y > maxY) maxY = p.y;
		}
		
		// return bounds
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	
	public boolean containsPoint(Point p) {
		if (selection_outline_p == null)
			calculate_outlines();
		
		return selection_outline_p.contains(p);
	}

	public void paint(Graphics2D g, int draw_style) {

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
	
	public String getTooltipText(PathNetModel model) {	
		return "<html>Area <b>"+getDescription()+"[" + getID() + "]</b><br>No of vertices: " + getVertices().length + "</html>";
	}

	public String toXML() {	
		return null;
	}

	/*
	 * Additional public methods do begin here:
	 * 
	 */
	public void addVertex(Point p) {
		addVertex(p, 0);
	}
	
	public void addVertex(Point p, int pos) {
		vertices.add(pos, p);
		vertex_cache_has_changed = true;
		outline_p = null;
		
		firePropertyChanged(ACTION_VERTICES_CHANGED, null, p);
	}
	
	public void removeVertex(Point p) {
		vertices.remove(p);
		vertex_cache_has_changed = true;
		
		firePropertyChanged(ACTION_VERTICES_CHANGED, p, null);
	}
	
	public void removeVertex(int pos) {
		removeVertex((Point)(vertices.get(pos)));
	}
	
	public Point[] getVertices() {
		if (vertex_cache_has_changed) {
			if (vertex_cache.length!=vertices.size())
				vertex_cache = new Point[vertices.size()];
			
			for (int i=0; i<vertices.size(); i++)
				vertex_cache[i] = (Point)vertices.get(i);
						
			vertex_cache_has_changed = false;
		}
		
		return vertex_cache;
	}
	
	public Point getNearestOutlinePoint(Point p) {
		// set smallest distance to max value
		double minDist = Double.MAX_VALUE;
		Point minPoint = null;
		
		// for all vertices
		Point[] v = getVertices();
		for (int i=1; i<=v.length; i++) {
			
			// work on edge between src and trg
			Point src = v[i-1];
			Point trg = v[i%v.length];
			
			// get distances
			double d = p.distance(src);
			double e = p.distance(trg);
			double f = src.distance(trg);
			
			// calc dist from src to outline point ( phytagoas )
			double g = ( d*d - e*e + f*f ) / (2*f);
			
			// shrink to interval [0, f]
			g = g<0 ? 0 : ( g>f ? f : g );
					
			// Normalized vector from src to trg
			Point2D.Double vec = new Point2D.Double( (trg.x - src.x) / f, (trg.y - src.y) / f );
			
			// the outline point
			Point op = new Point( (int)(src.x + vec.x * g), (int)(src.y + vec.y * g));
			
			// smallest distance for now? remember point and distance
			double dist = p.distance(op);
			if (dist < minDist) {
				minDist = dist;
				minPoint = op;
			}
		}
		
		return minPoint;		
	}
	
	/*
	 * private methods
	 * 
	 */
	private void calculate_outlines() {
		Point[] v = getVertices();
		if (v.length <= 2) return;
		
		// the outline_p :
		outline_p = new Polygon();
		
		for (int i=0; i<v.length; i++)
			outline_p.addPoint(v[i].x, v[i].y);	
				
		selection_outline_p = GeometricTools.createBuffer(Settings.getInt(DEFAULT_SELECTION_OVERHEAD), outline_p);		
	}
	
	public String toString() {
		return "AREA[vertices:"+vertices+", outline_p:"+outline_p+", selection_outline_p:"+selection_outline_p+", super: "
			+ super.toString() + "]";
	}

	/**
	 * sets the area to the target area
	 * @param area
	 */
	public Point getTranslation(Area area) {
		if (area==null || area.vertices.size()==0) {
			return null;
		}
		Point op = (Point)vertices.get(0);
		Point np = (Point)area.vertices.get(0);
		return new Point(np.x - op.x, np.y - op.y);
	}

	public void applyFactor(double factor) {
		for (int i=0; i<vertices.size(); i++) {
			Point p = (Point) vertices.get(i); 
			p.setLocation((int)((double)p.x*factor), (int)((double)p.y*factor));
		}
		calculate_outlines();
	}

}
