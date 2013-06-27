/*
 * Created on Feb 16, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.tools;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GeometricTools {

	/**
	 * 
	 * @author steffen
	 *
	 * This class is representing an infinit straight. The parameter representation
	 * of a straight g is given by:
	 * 
	 * 		g:	[a, b] + t * [u, v]
	 * 
	 * where t is the parameter and can be any value. [a, b] is the start vector of
	 * the ParameterLine and [u, v] is the direction vector.
	 */
	public static class ParameterLine {
		/**
		 * start vector of this ParameterLine
		 */
		public Point2D 	s_vec;
		
		/**
		 * direction vector of this ParameterLine
		 */
		public Point2D	d_vec;
		
		public ParameterLine(Point s_vec, Point d_vec) {
			this( new Point2D.Float(s_vec.x, s_vec.y), new Point2D.Float(d_vec.x, d_vec.y) );
			
		}
		
		public ParameterLine(Point2D s_vec, Point2D d_vec) {
			this.s_vec = s_vec;
			this.d_vec = d_vec;
		}
		
		public ParameterLine(Line2D line) {
			this( line.getP1(), new Point2D.Double(line.getX2() - line.getX1(), line.getY2() - line.getY1()) );
		}
		
		public void normalize() {
			double distance = d_vec_length(); 
			d_vec.setLocation(d_vec.getX() / distance, d_vec.getY() / distance);
		}
		
		public double d_vec_length() {
			return Math.sqrt( d_vec.getX()*d_vec.getX() + d_vec.getY()*d_vec.getY() );
		}
		
		public String toString() {
			return "PARAMETER_LINE[s_vec: "+s_vec+", d_vec: " + d_vec+"]";
		}
	};
	
	/**
	 * 
	 * @author steffen
	 *
	 * A polyline is defined by a number of points. Its a path from a startpoint A to an endpoint B.
	 */
	public static class Polyline {
		private Vector	points = new Vector();
		
		private boolean	cache_has_changed = false;
		private Point2D[] cache = new Point2D[0];
		
		public Polyline() {}
		public Polyline(Line2D line) {
			addPoint(new Point((int)line.getP1().getX(), (int)line.getP1().getY()));
			addPoint(new Point((int)line.getP2().getX(), (int)line.getP2().getY()));
		}
		
		public boolean isEmpty() { return points.isEmpty(); }
		
		public int noOfPoints() { return points.size(); }
		
		public void reversePoints() {
			for (int i=0; i<points.size()/2; i++) {
				Point2D temp = (Point2D) points.get(i);
				points.set(i, points.get(points.size() - 1 - i));
				points.set(points.size() - 1 - i, temp);
			}
			cache_has_changed = true;
		}
		
		public void addPoint(Point2D p) {
			points.add(p);
			cache_has_changed = true;
		}
		
		public void insertPoint(Point2D p, int index) {
			points.insertElementAt(p, index);
			cache_has_changed = true;
		}
		
		public void removePoint(Point2D p) {
			points.remove(p);
			cache_has_changed = true;
		}
		
		public void removePointAt(int i) {
			points.remove(i);
			cache_has_changed = true;
		}
		
		public void updatePoint(Point2D p, int i) {
			if (i<0||i>=points.size())
				return;
			
			((Point2D)(points.get(i))).setLocation(p);
		}
		
		public Point2D[] getPoints() {
			if (cache_has_changed) {
				if (points.size() != cache.length)
					cache = new Point2D[points.size()];
				for (int i=0; i<cache.length; i++)
					cache[i] = (Point2D)(points.get(i));
				cache_has_changed = false;
			}
			
			return cache;
		}
		
		public Line2D[] getLines() {
			if (points.size()<2)
				return new Line2D[0];
			
			Point2D[] p = getPoints();
			Line2D[] lines = new Line2D[p.length - 1];
			
			for (int i=1; i<p.length; i++)
				lines[i-1] = new Line2D.Double( p[i-1], p[i] );
			
			return lines;
		}
		
//		public void applyFactor(double factor) {
//			for (int i=0; i<points.size(); i++) {
//				Point p = (Point) points.get(i);
//				p.setLocation((int)((double)p.x*factor), (int)((double)p.y*factor));
//			}
//		}
//		
		public String toString() {
			return "POLYLINE[#points:"+points.size()+"; points:["+points+"]]";
		}
		
		public Object clone() {
			Polyline pl = new Polyline();
			for (int i=0; i<points.size(); i++) {
				Point p = (Point) points.get(i);
				pl.addPoint(new Point(p.x, p.y));
			}
			return pl;
		}

		public void setPoint(int inner_point, Point point) {
			((Point)points.get(inner_point+1)).setLocation(point);
		}
	}
		
	/**
	 * Works on a polyline and returns a polygon witch has the shape of the polyline but is bufSize
	 * pixel width. The buffer is created around the original polyline. The polyline must contain at
	 * least two points.
	 * 
	 * @param bufSize	The size of the buffer to create
	 * @param pl		The polyline to process
	 * @param round		If this is set to true, the start and end of the buffer will be rounded
	 * @return			A buffer of the polyline or empty polygon, if there are less than 2 points in pl
	 */
//	public static Polygon createBuffer(double bufSize, Polyline pl, boolean round) {
//		// pl has not enough points? return empty polygon
//		if (pl.noOfPoints()<2)
//			return new Polygon();
//		// TODO: Implement rounded ends
//		if (round) {
//			System.out.println("createBuffer: Rounded ends not implemented yet");
//			round = false;
//		}
//		
//		// get points, create polygon
//		Polygon buffer = new Polygon();
//		Point2D[] points = pl.getPoints();
//		
//		// create buffer here:
//		// first from start to end, then via inversion of points array, from end to start
//		createBuffer(bufSize/2, points, round, buffer);
//		reverse(points);
//		createBuffer(bufSize/2, points, round, buffer);
//		reverse(points);
//	
//		return buffer;
//	}

	/**
	 * Works on a polyline and returns a polygon witch has the shape of the polyline but is bufSize
	 * pixel width at each node. The buffer is created around the original polyline. The polyline must
	 * contain at least two points. The bufSizes must be given by a HashMap in form of (Point2D --> double).
	 * 
	 * @param dists		A HAshMap with the sizes corresponding to the points of pl; form: (Point2D --> double)
	 * @param pl		The polyline to process	 
	 * @param round		If this is set to true, the start and end of the buffer will be rounded
	 * @return			A buffer of the polyline or an empty polygon, if there are less than 2 points in pl
	 */
	public static Polygon createBuffer(Vector distVector, double additionalDist, Polyline pl, boolean round) {
		// pl has not enough points? return empty polygon
		if (pl.noOfPoints()<2)
			return new Polygon();
		// TODO: Implement rounded ends
		if (round) {
			System.out.println("createBuffer: Rounded ends not implemented yet");
			round = false;
		}
		
		// get points, create polygon
		Polygon buffer = new Polygon();
		Point2D[] points = pl.getPoints();
		double[]  dists = new double[distVector.size()];
		for (int i=0; i<dists.length; i++)
			dists[i] = ((Double)(distVector.get(i))).doubleValue() / 2.0;
		
		// create buffer here:
		// first from start to end, then via inversion of points array, from end to start
		createBuffer(dists, additionalDist, points, round, buffer);
		reverse(points);
		reverse(dists);
		createBuffer(dists, additionalDist, points, round, buffer);
		reverse(points);
		reverse(dists);
	
		return buffer;
	}
	
	public static Polygon createBuffer(Vector dists, Polyline pl, boolean round) {
		return createBuffer(dists, 0.0, pl, round);
	}
	
//	private static void createBuffer(double dist, Point2D[] points, boolean round, Polygon buffer) {		
//		if (round) {
//			// not yet implemented
//		} else {
//			// add start point to buffer
//			Point2D p = getLot(dist, points[0], points[1], false);
//			buffer.addPoint( (int)(p.getX()), (int)(p.getY()) );
//		}
//		
//		// process on points from start+1 to end-1
//		for (int i=1; i<points.length-1; i++) {
//			// ignore if any of the points i-1, i or i+1 are equal
//			if (points[i-1].equals(points[i]) || points[i].equals(points[i+1]))
//				continue;
//			
//			Point2D p = getBufferPoint(
//					points[i-1],
//					points[i],
//					points[i+1],
//					dist,
//					dist,
//					dist
//			);
//			
////			// avoid too sharp buffers by adding an additional point to the buffer
////			if (p.distance(points[i]) > dist*1.1) {
////				Point2D point1 = getDistancePoint(-dist, points[i], points[i-1]);
////				Point2D point2 = getDistancePoint(-dist, points[i], points[i+1]);
////				
////				point1 = getLot(dist, point1, points[i-1], true);
////				point2 = getLot(dist, point2, points[i+1], false);
////				
////				buffer.addPoint( (int)(point1.getX()), (int)(point1.getY()) );
////				buffer.addPoint( (int)(point2.getX()), (int)(point2.getY()) );
////			}
////			
////			else 
//				buffer.addPoint( (int)(p.getX()), (int)(p.getY()) );
//		}
//		
//		if (round) {
//			// not yet implemented
//		} else {
//			// add end point to buffer
//			Point2D p = getLot(dist, points[points.length-1], points[points.length-2], true);
//			buffer.addPoint( (int)(p.getX()), (int)(p.getY()) );
//		}
//	}	
	
	private static void reverse(Object[] objs) {
		for (int i=0; i<objs.length/2; i++) {
			Object temp = objs[i];
			objs[i] = objs[objs.length - 1 - i];
			objs[objs.length - 1 - i] = temp;
		}
	}
		
	private static void reverse(double[] values) {
		for (int i=0; i<values.length/2; i++) {
			double temp = values[i];
			values[i] = values[values.length - 1 - i];
			values[values.length - 1 - i] = temp;
		}
	}
	
	private static void createBuffer(double dists[], double additionalDist, Point2D[] points, boolean round, Polygon buffer) {
		if (round) {
			// not yet implemented
		} else {
			// add start point to buffer
			Point2D p = getLot(additionalDist + dists[0], points[0], points[1], false);
			buffer.addPoint( (int)(p.getX()), (int)(p.getY()) );
		}
		
		// process on points from start+1 to end-1
		for (int i=1; i<points.length-1; i++) {
			// ignore if any of the points i-1, i or i+1 are equal
			if (points[i-1].equals(points[i]) || points[i].equals(points[i+1]))
				continue;
			
			Point2D p = getBufferPoint(
					points[i-1],
					points[i],
					points[i+1],
					additionalDist + dists[i-1],
					additionalDist + dists[i],
					additionalDist + dists[i+1]
			);			
			
			buffer.addPoint( (int)(p.getX()), (int)(p.getY()) );
		}
		
		if (round) {
			// not yet implemented
		} else {
			// add end point to buffer
			Point2D p = getLot( additionalDist + dists[points.length-1], points[points.length-1], points[points.length-2], true);
			buffer.addPoint( (int)(p.getX()), (int)(p.getY()) );
		}
	}
	
	private static Point2D getBufferPoint(Point2D p0, Point2D p1, Point2D p2, double dist0, double dist1, double dist2) {
		ParameterLine pl1 = new ParameterLine( new Line2D.Double(getLot(dist0, p0, p1, false), getLot(dist1, p1, p0, true)) );
		ParameterLine pl2 = new ParameterLine( new Line2D.Double(getLot(dist1, p1, p2, false), getLot(dist2, p2, p1, true)) );
		
		Point2D p = crossPoint(pl1, pl2);
				
		if (Double.isNaN(p.getX()) || Double.isNaN(p.getY())) {
			System.out.println("Skipping NaN values, points: " + p0 + ", " + p1 + ", " + p2);
			System.out.flush();
		}
		
		if (Double.isInfinite(p.getX()) || Double.isInfinite(p.getY())) {
			System.out.println("Skipping Infinit values, points: " + p0 + ", " + p1 + ", " + p2);
			System.out.flush();
		}
		
		return p;
	}
	
	private static double getDouble(Object obj) {
		return ((Double)obj).doubleValue();
	}
	
	/**
	 * Creates a buffer polygon, which is dist larger on each edge as the original one.
	 * @param dist		the additional space for each edge
	 * @param poly		the original polygon for which the buffer should be calculated
	 * @return			the buffer polygon which is dist larger on each edge as the original one
	 */
	public static Polygon createBuffer(double dist, Polygon poly) {
		Polygon buffer = new Polygon();
		
		// get points of polygon
		Point2D[] points = new Point2D[poly.npoints];
		for (int i=0; i<poly.npoints; i++)
			points[i] = new Point2D.Double(poly.xpoints[i], poly.ypoints[i]);
		
		for (int i=0; i<points.length; i++) {
			// check on which side the buffer should be created for this line
			Point2D a = getLot( 1, getMidPoint( points[i==0?points.length-1:i-1], points[i] ), points[i], true );
			Point2D b = getLot( 1, getMidPoint( points[i], points[(i+1)%points.length] ), points[(i+1)%points.length], true );
			
			ParameterLine p1 = getParallelLine(dist, points[i==0?points.length-1:i-1], points[i], !poly.contains(a));
			ParameterLine p2 = getParallelLine(dist, points[i], points[(i+1)%points.length], !poly.contains(b));
			
			Point2D p = crossPoint(p1, p2);		
			buffer.addPoint( (int)(p.getX()), (int)(p.getY()) );
		}
		
		return buffer;
	}
	
	/**
	 * Calculates the cross point of two ParameterLines. These lines will not cross only if they are
	 * parallel or are the same.
	 *  
	 * @param p1
	 * @param p2
	 * @return The cross point and null, if p1 is not crossing p2
	 */
	public static Point2D crossPoint( ParameterLine p1, ParameterLine p2) {
		Point2D result = null;
		
		// map p1 parameters
		double a = p1.s_vec.getX();
		double b = p1.s_vec.getY();
		double u = p1.d_vec.getX();
		double v = p1.d_vec.getY();
		
		// map p2 parameters
		double c = p2.s_vec.getX();
		double d = p2.s_vec.getY();
		double w = p2.d_vec.getX();
		double x = p2.d_vec.getY();
		
		// calculate parameter variable s with: p2 = [c, d] + s * [x, y]
		try {
			double s = ( (u*(d-b) + v*(a-c)) / (v*w - x*u) );
			return new Point2D.Double( c + s * w, d + s * x	);
		} catch (ArithmeticException e) {
			return null;
		}		
	}
	
	/**
	 * Convert p of type Point2D to a Point with integer values
	 * @param p
	 * @return The converted point
	 */
	public static Point toPoint(Point2D p) {
		return new Point( (int)(Math.round(p.getX())), (int)(Math.round(p.getY())) );
	}
	
	/**
	 * Returns a point that is 90 degree from the line defined by s and t in distance dist over point s. The side is given by clockwise. 
	 * @param dist			The distance the point should have
	 * @param s				Startpoint of the defined line
	 * @param t				Endpoint of the defined line
	 * @param clockwise		Either if the lot should be calculated clockwise (turned-right) or not (turned-left)
	 * @return				A 90 degree point (lot) over s
	 */
	public static Point2D getLot(double dist, Point2D s, Point2D t, boolean clockwise) {
		double d = s.distance(t);
		Point2D.Double r = new Point2D.Double( -(t.getY()-s.getY()) / d, (t.getX()-s.getX()) / d );
		
		if (clockwise)
			r.setLocation( -r.x, -r.y );
		
		return new Point2D.Double( s.getX() + r.getX() * dist, s.getY() + r.getY() * dist );
	}
	
	public static Point2D getMidPoint(Point2D s, Point2D t) {
		return new Point2D.Double(s.getX() + (t.getX() - s.getX()) / 2, s.getY() + (t.getY() - s.getY()) / 2);
	}
	
	public static ParameterLine getParallelLine( double dist, Point2D p1, Point2D p2, boolean clockwise ) {		
		return new ParameterLine(
				getLot(dist, p1, p2, clockwise),
				new Point2D.Double( p2.getX() - p1.getX(), p2.getY() - p1.getY() )
		);
	}
	
	/**
	 * 
	 * @param dist
	 * @param start
	 * @param end
	 * @return
	 */
	public static Point2D getDistancePoint(double dist, Point2D start, Point2D end) {
		double d = start.distance(end);
		
		// calculate direction vector (normalized)
		Point2D.Double r = new Point2D.Double(end.getX() - start.getX(), end.getY() - start.getY());
		r.setLocation(r.getX() / d, r.getY() / d);
		
		// calculate point with distance dist on the line between start and end
		r.setLocation(start.getX() + r.getX() * dist, start.getY() + r.getY() * dist);
		
		return r;
	}
}
