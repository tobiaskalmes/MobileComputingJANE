package de.uni_trier.jane.tools.pathneteditor.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.tools.GeometricTools;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;
import de.uni_trier.jane.tools.pathneteditor.tools.GeometricTools.Polyline;


public final class Edge extends PathNetObject implements PathNetConstants {

	// static variables
	static private int objNo = 0;
	
	// datastructure variables
	private Waypoint source			= null;
	private Waypoint target			= null;	
	private Polyline path			= new Polyline();
	private Vector	 pathPointSizes = new Vector(2);
	
	private Polygon  outline_p 		= null;
	private Polygon  selection_outline_p = null;	
	private Color    innerPointColor	= Settings.getColor(DEFAULT_INNER_POINT_COLOR);
	
	private int hashCode;
	
	private ObjectListener source_or_target_listener = new ObjectListener() {
		public void propertyChanged(ObjectEvent e) {
			if (e.getActionType() == ACTION_POSITION_CHANGED || e.getActionType() == ACTION_SYMBOL_SIZE_CHANGED) {
				
				if (e.getSource() == target)
					setWidth(path.noOfPoints() - 1, ((Waypoint)(e.getSource())).getSymbolSize());
				else
					setWidth(0, ((Waypoint)(e.getSource())).getSymbolSize());
				
				// force shape to be updated
				outline_p = null;
			}
		}		
	};
	
	/*
	 * constructors
	 */	 
	private Edge(String id) {
		super(id);
		this.objectColor = Settings.getColor(DEFAULT_EDGE_COLOR);
	}
	
	private Edge() {
		this("E:" + (objNo++));
	}
	
	private Edge(Waypoint source, Waypoint target, boolean add_object_listener) {
		this("");
		
		if (source == null || target == null)
			throw new IllegalArgumentException("Null not allowed for target or source here");

		
		if (source.hashCode() < target.hashCode()) {
			this.source = source;
			this.target = target;
		} else {
			this.source = target;
			this.target = source;
		}
		
		if (add_object_listener) {
			source.addObjectListener(source_or_target_listener);
			target.addObjectListener(source_or_target_listener);
		}
		
		path.addPoint(this.source.getPosition());
		path.addPoint(this.target.getPosition());
		pathPointSizes.add(new Double(this.source.getSymbolSize()));
		pathPointSizes.add(new Double(this.target.getSymbolSize()));
		
		hashCode = source.hashCode() * 7 + target.hashCode();
			
		// force shape to be updated
		outline_p = null;
	}
		
	public Edge(String id, Waypoint source, Waypoint target) {
		this(id);

		if (source == null || target == null)
			throw new IllegalArgumentException("Null not allowed for target or source here");

		if (source.hashCode() < target.hashCode()) {
			this.source = source;
			this.target = target;
		} else {
			this.source = target;
			this.target = source;
		}
						
		source.addObjectListener(source_or_target_listener);
		target.addObjectListener(source_or_target_listener);
		
		path.addPoint(this.source.getPosition());
		path.addPoint(this.target.getPosition());
		pathPointSizes.add(new Double(this.source.getSymbolSize()));
		pathPointSizes.add(new Double(this.target.getSymbolSize()));
		
		hashCode = source.hashCode() * 7 + target.hashCode();
			
		// force shape to be updated
		outline_p = null;
	}
	
	public Edge(Waypoint source, Waypoint target) {
		this("E:" + (objNo++), source, target);		
	}
		
	/*
	 *  implemented abstract methods
	 */
	public int getObjectType() { return PathNetConstants.EDGE; }
	
	public Shape getObjectShape() {
		if (outline_p==null)			
			calculate_outlines();		
		
		return outline_p;
	}
	
	public Rectangle getBounds() {
		// source and target positions
		Point p1 = source.getPosition();
		Point p2 = target.getPosition();
		
		// get rectangle values		
		int minX = p1.x < p2.x ? p1.x : p2.x;
		int minY = p1.y < p2.y ? p1.y : p2.y;
		int maxX = p1.x > p2.x ? p1.x : p2.x;
		int maxY = p1.y > p2.y ? p1.y : p2.y;
		
		// return bounds
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	
	public void paint(Graphics2D g, int draw_style) {		
		
		// be sure that the object shapes are up-to-date
		getObjectShape();
		
		switch(draw_style) {
			case DRAW_STYLE_SELECTED:
				g.setColor(Settings.getColor(DEFAULT_SELECTION_COLOR));
				g.fill(selection_outline_p);
				// no break
			
			default:		
				// draw polygon		
				g.setColor(objectColor);
				
				g.fill(outline_p);
				
				// draw inner points
				g.setColor(innerPointColor);
				Point2D[] ip = path.getPoints();				
				for (int i=1; i<ip.length-1; i++) {
					double innerPointSize = getWidth(i);
					g.fill(new Ellipse2D.Double(
							ip[i].getX() - innerPointSize / 2,
							ip[i].getY() - innerPointSize / 2,
							innerPointSize,
							innerPointSize
					));
				}
		}
	}
	
	/**
	 * returns the middle position on the polyline
	 * @return
	 */
	public Point getMiddle() {
		return getMiddle(path);
	}
	
	public String toXML() {	
		// TODO: Move code from defaultPathNetModel to here!
		return null;		
	}	
	
	public boolean containsPoint(Point u) {
		// be sure that the object shapes are up-to-date
		getObjectShape();
		
		return selection_outline_p.contains(u);
	}

	public String getTooltipText(PathNetModel model) {
		return "<html>Edge <b>"+getDescription()+"[" + getID() + "]</b><br>Connecting Waypoints:" +
				"<b>"+source.getDescription()+"[" + source.getID() + "]</b> and <b>"+target.getDescription()+"[" + target.getID() + "]</b></html>";
	}
	
	public Waypoint getSource() { return source; }	
	public Waypoint getTarget() { return target; }
		
	public void setWidth(int pos, double width) {
		double oldWidth = ((Double)(pathPointSizes.remove(pos))).doubleValue();
		pathPointSizes.insertElementAt(new Double(width), pos);
		
		// force shape to be updated
		outline_p = null;
	}
	
	public double getWidth(int pos) { return ((Double)(pathPointSizes.get(pos))).doubleValue(); }
	public double[] getWidths() {
		double[] result = new double[pathPointSizes.size()];
		for (int i=0; i<result.length; i++)
			result[i] = getWidth(i);
		return result;
	}
	
	public int getInnerPointsSize() {
		return path.noOfPoints() - 2;
	}
	
	public void addInnerPoint(Point p, double size) {
		// resolve insertion point
		Point nearestP = getNearestOutlinePoint(p);
		
		// go through all lines
		Line2D[] lines = path.getLines();
		
		// for debugging: save old amount of points
		int pointsBefore = path.noOfPoints();
		
		for (int i=0; i<lines.length; i++) {
			Vector dists = new Vector();
			dists.add(new Double(getWidth(i)));
			dists.add(new Double(getWidth(i+1)));
			if (GeometricTools.createBuffer(dists, Settings.getInt(DEFAULT_SELECTION_OVERHEAD), new GeometricTools.Polyline(lines[i]), false).contains(nearestP)) {			
				
				path.insertPoint(p, i+1);
				pathPointSizes.insertElementAt(new Double(size), i+1);
				
				break;
			}				
		}
						
		// for debugging: error message if no of points do not differ
		if (pointsBefore == path.noOfPoints()) {
			System.err.println("INNER POINT was not inserted: " + p + " / " + nearestP);
		}
		
		// force shape to be updated
		outline_p = null;
		
		firePropertyChanged(ACTION_VERTICES_CHANGED, null, p);
	}
	
	public Point getNearestOutlinePoint(Point p) {
		// set smallest distance to max value
		double minDist = Double.MAX_VALUE;
		Point minPoint = null;
		
		// for all vertices
		Point2D[] v = path.getPoints();
		for (int i=1; i<v.length; i++) {
			
			// work on edge between src and trg
			Point2D src = v[i-1];
			Point2D trg = v[i];
			
			// get distances
			double d = p.distance(src);
			double e = p.distance(trg);
			double f = src.distance(trg);
			
			// calc dist from src to outline point ( phytagoas )
			double g = ( d*d - e*e + f*f ) / (2*f);
			
			// shrink to interval [0, f]
			g = g<0 ? 0 : ( g>f ? f : g );
					
			// Normalized vector from src to trg
			Point2D.Double vec = new Point2D.Double( (trg.getX() - src.getX()) / f, (trg.getY() - src.getY()) / f );
			
			// the outline point
			Point op = new Point( (int)(src.getX() + vec.getX() * g), (int)(src.getY() + vec.getY() * g));
			
			// smallest distance for now? remember point and distance
			double dist = p.distance(op);
			if (dist < minDist) {
				minDist = dist;
				minPoint = op;
			}
		}
		
		return minPoint;		
	}
	
	public Point2D[] getInnerPoints() {
		Point2D[] points = path.getPoints();
		Point2D[] result = new Point[points.length-2];
		
		for (int i=1; i<points.length-1; i++)
			result[i-1] = points[i];
		
		return result;
	}
		
	public void removeInnerPoint(Point p) {
		path.removePoint(p);
		pathPointSizes.remove(p);
		
		// force shape to be updated
		outline_p = null;					
	}
	
	public void removeInnerPoint(int i) {
		// invalid range?
		if (i<0 && i>=path.noOfPoints()-2)
			return;
		
		pathPointSizes.remove(i);
		path.removePointAt(i);
		
		// force shape to be updated
		outline_p = null;
	}
	
	public String toString() {
		return super.toString() +
			"; EDGE (source: " + source + "; target: " + target + ")";
	}
	
	public int hashCode() {
		return hashCode;
	}
	
	public boolean equals(Object o) {
		return (o.hashCode() == hashCode);
	}
	
	/**
	 * @return Returns the innerPointColor.
	 */
	public Color getInnerPointColor() {
		return innerPointColor;
	}
	
	/**
	 * @param innerPointColor The innerPointColor to set.
	 */
	public void setInnerPointColor(Color innerPointColor) {
		this.innerPointColor = innerPointColor;
	}
	
	/*
	 * protected methods
	 */
	protected void calculate_outlines() {
		if (source == null || target == null)
			return;
				
		outline_p =
			GeometricTools.createBuffer(pathPointSizes, path, false);
		
		selection_outline_p =
			GeometricTools.createBuffer(pathPointSizes, Settings.getInt(DEFAULT_SELECTION_OVERHEAD), path, false);
	}

	public void applyFactor(double factor) {
		Point2D[] points = getInnerPoints();
		for (int i=0; points!=null && i<points.length; i++) {
			points[i].setLocation((int)(points[i].getX()*factor), (int)(points[i].getY()*factor));
			setWidth(i, getWidth(i) * factor );
		}
		
		// force shape to be updated
		outline_p = null;
	}

	/**
	 * returns the number of the inner point that lies in an area around the specified point. (-1 if no point is close enough).
	 * @param p  the point to look for
	 * @return  the number of the inner point that is close enough to the specified point, -1 if no point close enough
	 */
	public int getInnerPoint(Point p) {
		Point2D[] points = path.getPoints();
		for (int i=1; i<points.length-1; i++)
			if (points[i].distance(p) < Settings.getInt(PathNetConstants.SELECTION_TOLERANCE))
				return i-1;
		return -1;
	}
	
	public Object clone() {
		Edge edge = new Edge(source, target, false);
		edge.path = (Polyline)path.clone();
		edge.pathPointSizes = (Vector)pathPointSizes.clone();
		return edge;
	}
	
	public Edge clone(Waypoint dragged_point, Waypoint new_end) {
		Edge clone = (Edge)clone();
		if (clone.target==dragged_point) {
			clone.target = new_end;
			clone.path.updatePoint(new_end.getPosition(), clone.path.noOfPoints()-1);
		} else {
			clone.source = new_end;
			clone.path.updatePoint(new_end.getPosition(), 0);
		}
		
		// force shape to be updated
		clone.outline_p = null;
		
		return clone;
	}

	public void setInnerPointPosition(int inner_point, Point real_point) {
		path.setPoint(inner_point, real_point);
		
		// force shape to be updated
		outline_p = null;
	}
	
	private double getLength(Polyline path) {
		return getLength(path, path.noOfPoints()-1);
	}
	
	private double getLength(Polyline path, int last_point) {
		double sum = 0;
		Point2D[] points = path.getPoints();
		for (int i=0; i<last_point; i++)
			sum += points[i].distance(points[i+1]);
		return sum;
	}
	
	private double getNormalizationFactor(Point2D p) {
		return Math.abs(1.0 / Math.sqrt(p.getX()*p.getX() + p.getY()*p.getY()));
	}
	
	/**
	 * returns a normzalized vector for the line from p1 to p2
	 */
	private Point2D getNormalizedVector(Point2D p1, Point2D p2) {
		Point2D p = new Point2D.Double(p2.getX() - p1.getX(), p2.getY() - p1.getY());
		double factor = getNormalizationFactor(p);
		p.setLocation(factor*p.getX(), factor*p.getY());
		return p;
	}
	
	private Point getMiddle(Polyline path) {
		Point2D[] points = path.getPoints();
		
		// calculate length of entire path
		double length = getLength(path);
		if (length == 0.0)
			return new Point((int)(points[0].getX()), (int)(points[0].getY()));

		// calculate length along the path until the middle is reached
		int i = 1;
		double sum = 0;
		while (sum<length/2)
			sum = getLength(path, i++);
		
		// the middle is between point (i-1) and (i)
		Point2D p1 = points[i-2];
		Point2D p2 = points[i-1];

		Point2D vector = getNormalizedVector(p1, p2);
		
		double middle = length/2.0;
		double diff = sum - middle;
		Point p = new Point((int)(p2.getX()-diff*vector.getX()), (int)(p2.getY()-diff*vector.getY())); 

		return p;
	}
}
