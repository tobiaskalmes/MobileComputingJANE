package de.uni_trier.jane.util;

import de.uni_trier.jane.basetypes.*;


/**
 * This class provides standard methods for geometry calculations frequently needed by the
 * position-based routing algorithms.
 */
public class GeometryCalculations {

	/**
	 * Value indicating a clockwise turn.
	 */
	public static final int CLOCKWISE_TURN = 1;

	/**
	 * Value indicating a counterclockwise turn.
	 */
	public static final int COUNTERCLOCKWISE_TURN = -1;

	/**
	 * Value indicating no turn.
	 */
	public static final int NO_TURN = 0;

	/**
	 * Check if the line ab is less than cd. This method uses the line ordering
	 * ab < cb iff (a < c) or (a = c and b < d).
	 * @param a the start point of the first line
	 * @param b the end point of the first line
	 * @param c the start point of the second line
	 * @param d the end point of the second line
	 * @return true if the first line is smaller
	 */
	public static boolean lessThan(Position a, Position b, Position c, Position d) {
		int comp = a.compare(c);
		return comp < 0 || (comp == 0 && b.compare(d) < 0);
	}

	/**
	 * Check wether a point is above, below or on a line.
	 * @param start the start position of the line
	 * @param stop the stop position of the line
	 * @param point the point
	 * @return a value indicating, if the point is above, below or on the line
	 */
	public static int checkTurn(Position start, Position stop, Position point) {
		Position p1 = stop.sub(start);
		Position p2 = point.sub(start);
		double crossProduct = p2.determinant2D(p1);
		if(crossProduct == 0.0) {
			return NO_TURN;
		}
		if(crossProduct > 0.0) {
			return CLOCKWISE_TURN;
		}
		else {
			return COUNTERCLOCKWISE_TURN;
		}
	}

	/**
	 * Calculate the angle between the line ab and line bc ignoring the z-coordinate.
	 * This method returns the smaller one of both possible angles.
	 * @param a the first position
	 * @param b the second position
	 * @param c the third position
	 * @return the angle
	 */
	public static double getAngle(Position a, Position b, Position c) {
		Position p1 = a.sub(b);
		Position p2 = c.sub(b);
		double angle1 = p1.getAngleZ();
		p2 = p2.turnZ(-angle1);
		double angle2 = p2.getAngleZ();
		
		if(angle2 > 180) {
			return 360 - angle2;
		}
		else {
			return angle2;
		}
	}

	/**
	 * Calculate the angle between the line ab and line bc measured in counter clockwise direction
	 * while ignoring the z-coordinate. When a = c holds, the methos return 0 instead of 360.
	 * @param a the first position
	 * @param b the second position
	 * @param c the third position
	 * @return the angle
	 */
	public static double getCCWAngle(Position a, Position b, Position c) {
		if(a.equals(c)) {
			return 0.0;
		}
		Position p1 = a.sub(b);
		Position p2 = c.sub(b);
		double angle1 = p1.getAngleZ();
		p2 = p2.turnZ(-angle1);
		double angle2 = p2.getAngleZ();
		return angle2;
	}

	/**
	 * Calculate the angle between the line ab and line bc measured in clockwise direction
	 * while ignoring the z-coordinate. When a = c holds, the methos return 0 instead of 360.
	 * @param a the first position
	 * @param b the second position
	 * @param c the third position
	 * @return the angle
	 */
	public static double getCWAngle(Position a, Position b, Position c) {
		if(a.equals(c)) {
			return 0.0;
		}
		double ccwAngle = getCCWAngle(a, b, c);
		if(ccwAngle == 0.0) {
			return 0.0;
		}
		return 360 - ccwAngle;
	}
	
	/**
	 * Calculate the distance between 'source' and the projection of 'position' on the straight line
	 * connecting 'source' and 'destination'. The function returns Double.NaN if 'source' and 'destination'
	 * are the same points.
	 * @param source the source point
	 * @param destination the desination point
	 * @param position the point projected on the straight line
	 * @return the distance between 'source' and the projection
	 */
	public static double getProgress(Position source, Position destination, Position position) {
		Position dir = destination.sub(source);
		if(dir.length() > 0.0) {
			double angle = dir.getAngleZ();
			return position.sub(source).turnZ(-angle).getX();
		}
		else {
			return Double.NaN;
		}
	}

	/**
	 * Check whether the given point c is inside the area defined by the circle with diameter (a,b). 
	 * @param a the first point defining the circle
	 * @param b the second point defining the circle
	 * @param c the point to be tested
	 * @param includeBorder specify if the circle border is also used to define the area
	 * @return true if the point c is inside the area
	 */
	public static boolean isInCircle(Position a, Position b, Position c, boolean includeBorder) {
		Position dir = b.sub(a).scale(0.5);
		double radius = dir.length();
		if(radius > 0.0) {
			Position mid = a.add(dir);
			double dist = mid.distance(c);
			if(includeBorder) {
				return dist <= radius;
			}
			else {
				return dist < radius;
			}
		}
		else {
			if(includeBorder) {
				return a.distance(c) <= 0.0;
			}
			else {
				return false;
			}
		}
	}

	/**
	 * Calculate the intersection between the lines (a,b) and (c,d).
	 * @param a start of first line
	 * @param b end of first line
	 * @param c start of second line
	 * @param d end of second line
	 * @return the intersection point, or null if there exist no intersection
	 */
	public static Position calculateIntersection(Position a, Position b, Position c, Position d) {
		
		// we make computation of ab x cd unique, i.e. ab x cd will be the same as
		// ba x cd, ab x dc, and so on.
		if(a.compare(b) > 0) {
			Position tmp = a;
			a = b;
			b = tmp;
		}
		if(c.compare(d) > 0) {
			Position tmp = c;
			c = d;
			d = tmp;
		}
		if(!lessThan(a, b, c, d)) {
			Position tmp1 = a;
			Position tmp2 = b;
			a = c;
			b = d;
			c = tmp1;
			d = tmp2;
		}
		
		double xa = a.getX();
		double xb = b.getX();
		double xc = c.getX();
		double xd = d.getX();
		double ya = a.getY();
		double yb = b.getY();
		double yc = c.getY();
		double yd = d.getY();
		
		// Strecke ab is eine einzelner Punkt // TODO: was ist mit Strecke cd ein Punkt ????
		if (xb == xa && yb == ya) {
			if (xd == xc && yd == yc) {
				if (xa == xc && ya == yc) {
					return a;
				}
				else {
					return null;
				}
			}
			else {
				if (xd != xc && yd != yc) {
					double v1 = (xa - xc) / (xd - xc);
					double v2 = (ya - yc) / (yd - yc);
					if (v1 == v2) {
						return a;
					}
					else {
						return null;
					}
				}
				else {
					if (xd == xc) {
						if (xa == xc) {
							if (yc <= yd) {
								if (ya >= yc && ya <= yd) {
									return a;
								}
								else {
									return null;
								}
							}
							else {
								if (ya >= yd && ya <= yc) {
									return a;
								}
								else {
									return null;
								}
							}
						}
						else {
							return null;
						}
					}
					else {
						if (ya == yc) {

							if (xc <= xd) {
								if (xa >= xc && xa <= xd) {
									return a;
								}
								else {
									return null;
								}
							}
							else {
								if (xa >= xd && xa <= xc) {
									return a;
								}
								else {
									return null;
								}
							}
						}
						else {
							return null;
						}
					}
				}
			}
		}
		else {
			
			// check whether a=c, a=d, b=c, or b=d
			if(a.equals(c) || a.equals(d)) {
				return a;
			}
			if(b.equals(c) || b.equals(d)) {
				return b;
			}

			// the lines have no common end point
			double v1;
			double v2;
			if (xb != xa) {
				v1 = (yc - ya) - (((xc - xa) * (yb - ya)) / (xb - xa));
				v2 = (((xd - xc) * (yb - ya)) / (xb - xa)) - (yd - yc);
			}
			else {
				v1 = (xc - xa) - (((yc - ya) * (xb - xa)) / (yb - ya));
				v2 = (((yd - yc) * (xb - xa)) / (yb - ya)) - (xd - xc);
			}
			if (v2 == 0) {
				return null;
			}
			double mu = v1 / v2;
			double lambda;
			if (xb != xa) {
				lambda = ((xc - xa) + mu * (xd - xc)) / (xb - xa);
			}
			else {
				lambda = ((yc - ya) + mu * (yd - yc)) / (yb - ya);
			}
			if (mu >= 0.0 && mu <= 1.0 && lambda >= 0.0 && lambda <= 1.0) {
				return a.add(b.sub(a).scale(lambda));
			}
			else {
				return null;
			}
		}
	}

	
	
	/**
	 * Check if there exists an intersection between the lines (p1,p2) and (p3,p4).
	 * @param p1 start of first line
	 * @param p2 end of first line
	 * @param p3 start of second line
	 * @param p4 end of second line
	 * @return true if there exist an intersection between the lines
	 */
	public static boolean checkIntersect(Position p1, Position p2, Position p3, Position p4) {
		
		// we make computation of ab x cd unique, i.e. ab x cd will be the same as
		// ba x cd, ab x dc, and so on.
		if(p1.compare(p2) > 0) {
			Position tmp = p1;
			p1 = p2;
			p2 = tmp;
		}
		if(p3.compare(p4) > 0) {
			Position tmp = p3;
			p3 = p4;
			p4 = tmp;
		}
		if(!lessThan(p1, p2, p3, p4)) {
			Position tmp1 = p1;
			Position tmp2 = p2;
			p1 = p3;
			p2 = p4;
			p3 = tmp1;
			p4 = tmp2;
		}

		
		return checkQuickRejection(p1, p2, p3, p4) && checkStraddles(p1, p2, p3, p4) && checkStraddles(p3, p4, p1, p2);
	}

	private static boolean checkQuickRejection(Position p1, Position p2, Position p3, Position p4) {
		double x1 = Math.min(p1.getX(), p2.getX());
		double x2 = Math.max(p1.getX(), p2.getX());
		double x3 = Math.min(p3.getX(), p4.getX());
		double x4 = Math.max(p3.getX(), p4.getX());
		double y1 = Math.min(p1.getY(), p2.getY());
		double y2 = Math.max(p1.getY(), p2.getY());
		double y3 = Math.min(p3.getY(), p4.getY());
		double y4 = Math.max(p3.getY(), p4.getY());
		return x2 >= x3 && x4 >= x1 && y2 >= y3 && y4 >= y1;
	}

	private static boolean checkStraddles(Position p1, Position p2, Position p3, Position p4) {
		double crossProduct1 = p3.sub(p1).determinant2D(p2.sub(p1));
		double crossProduct2 = p4.sub(p1).determinant2D(p2.sub(p1));
		return crossProduct1 < 0 && crossProduct2 > 0 || crossProduct1 > 0 && crossProduct2 < 0 || crossProduct1 == 0 || crossProduct2 == 0;
	}

}
