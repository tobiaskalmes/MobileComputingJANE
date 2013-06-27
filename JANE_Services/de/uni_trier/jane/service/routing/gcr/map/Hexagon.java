package de.uni_trier.jane.service.routing.gcr.map;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerAddress;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * this class implements a hexagon in this case a hexagon is a regular
 * polyhedron with six faces in R*R having the same lenght, where R denotes the
 * real numbers
 * 
 * @author Hamid
 */

public class Hexagon implements Cluster, Comparable {
	
	private transient static final double cte = Math.sqrt(3.0) / 2.0;
	
	/**
	 * The Hexagon center position
	 */
	private Position center;
	
	/**
	 * the Hexagon diameter
	 */
	private double diameter;
	
	/**
	 * the Hexagon height
	 */
	private double height;
	
	/**
	 * the Hexagon address
	 */
	private LinkLayerAddress address;
	
	/**
	 * Contructs a Hexagon with aCenter as Center and aDiameter as diameter and
	 * aAddress as address
	 * 
	 * @param aCenter
	 *            the Hexagon center
	 * @param aDiameter
	 *            the Hexagon diamater
	 * @param aAddress
	 *            the Hexagon address
	 */
	public Hexagon(Position aCenter, double aDiameter, LinkLayerAddress aAddress) {
		this.center = aCenter;
		this.diameter = aDiameter;
		this.address = aAddress;
		this.height = diameter * cte;
		
	}
	
	/**
	 * Returns the cluster address in the zero positive form
	 * 
	 * @return the cluster address
	 */
	public LinkLayerAddress getAddress() {
		
		return address;
	}
	
	/**
	 * Returns the cluster position
	 * 
	 * @return the cluster position
	 */
	public Position getCenter() {
		
		return center;
	}
	
	public int hashCode() {
		final int seed = 1000003;
		int result = seed;
		result = result * seed + (center == null ? 0 : center.hashCode());
		result = result * seed + (address == null ? 0 : address.hashCode());
		return result;
	}
	
	public boolean equals(Object aThat) {
		if (this == aThat)
			return true;
		if (!(aThat instanceof Hexagon))
			return false;
		
		Hexagon that = (Hexagon) aThat;
		
		return (this.address == null ? that.address == null : this.address
				.equals(that.address))
				&& (this.center == null ? that.center == null : this.center
						.equals(that.center));
		
	}
	
	/**
	 * Returns true if the node with the given position is inside this
	 * polyhedron
	 * 
	 * @param position
	 *            the position of the node which must be checked
	 */
	public boolean isInside(Position position) {
		double a = height / 2.0;
		double b = diameter / 4.0;
		double c = (height / 2.0) * center.getX();
		double d = diameter / 2.0;
		double e = (height * diameter) / 4.0;
		double f = (diameter * center.getY()) / 4.0;
		double g = (diameter * center.getY()) / 2.0;
		double x = position.getX();
		double y = position.getY();
		
		if (((a * x) - (b * y) - c - e + f) <= 0
				&& ((a * x) + (b * y) - c - e - f) <= 0
				&& ((d * y) - g - e) <= 0 && ((d * y) - g + e) > 0
				&& ((a * x) + (b * y) - c + e - f) > 0
				&& ((a * x) - (b * y) - c + e + f) > 0) {
			return true;
		} else
			return false;
	}
	
	/**
	 * Returns the distance between this cluster and the given cluster according
	 * to the zero positive form distance definition between two clusters
	 * 
	 * @return returns the distance
	 */
	public int getDistance(Cluster cluster) {
		int x = ((ClusterAddress) this.address).getXc()
		- ((ClusterAddress) cluster.getAddress()).getXc();
		int y = ((ClusterAddress) this.address).getYc()
		- ((ClusterAddress) cluster.getAddress()).getYc();
		if (x < 0 && y >= 0) {
			y = y - x;
			x = x - x;
			return Math.max(x, y);
		} else if (y < 0 && x >= 0) {
			x = x - y;
			y = y - y;
			return Math.max(x, y);
		}
		
		return Math.max(Math.abs(x), Math.abs(y));
	}
	
	/**
	 * Returns a hexagon shape
	 */
	public Shape getShape() {
		ShapeCollection result = new ShapeCollection();
		Rectangle rectangle = new Rectangle(center.add(new Position(
				-diameter / 2, -height / 2)), center.add(new Position(
						diameter / 2, height / 2)));
		result
		.addShape(new TextShape(address.toString(), rectangle,
				Color.RED), center);
		return result;
	}
	
	/**
	 * @return Returns a string object of this hexagon
	 */
	public String toString() {
		return "Cluster Address " + address.toString()+ "Center: " + center.getX() + " "
		+ center.getY();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (this == o)
			return 0;
		
		final Hexagon that = (Hexagon) o;
		
		return this.address.compareTo(that.address);
		
	}

}