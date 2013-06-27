/*****************************************************************************
 * 
 * PathNetNode.java
 * 
 * $Id: PathNetNode.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;


/**
 * This class implements a node within a pathnet. A node is conneceted to other 
 * nodes, has  a unique name and a <code>Position</code>. It also contains a routing 
 * table including all possible path endpoints e.g. rooms and the possible outgoing 
 * edges (PathNetPath) including a routing propability. 
 * 
 */
public class PathNetNode {

	
	/**
	 * Routing information for a target location (e.g. room)
	 * it contains all outgoing pathes for this location
	 * and a routing propabilitiy for each of these pathes
	 * 
	 */
	private class RoutingInformation {
	    /**
	     * outgoing pathes
	     */
		public PathNetPath[] path;
		/**
		 * routing propability
		 */
		public double[] prob;
		public String toString() {
			String res = "";
			for (int i = 0; i < path.length; i++) {
				res += prob[i] + "->" + path[i].getName();
				if (i < path.length - 1) {
					res += ",";
				}
			}
			return res;
		}
	}
	
	
	private String name;
	private Position position;

	private Hashtable routingTable;
	private ContinuousDistribution selectDistribution;
	private double width;
	
	
	
	/**
	 * Constructor for class <code>PathNetNode</code>
	 * @param name					the unique node name
	 * @param position				the node position
	 * @param width					the width of the node
	 * @param selectDistribution	continous uniform distribution between 0 and 1
	 * 								for randomized routing decisions
	 */
	public PathNetNode(String name, Position position,double width, ContinuousDistribution selectDistribution) {
		routingTable=new Hashtable();
		this.selectDistribution=selectDistribution;
		this.name=name;
		this.position=position;
		this.width=width;
	}

	/**
	 * Returns the Position of the <code>PathNetNode</code>
	 * @return	<code>Position</code>
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Returns the following PathNetNodes on the path to the given location destination 
	 * @param destination the destination location
	 * @return an array of PathNetNodes
	 */
	public PathNetNode[] getFollowingNodes(String destination) {
		ArrayList list = new ArrayList();
		RoutingInformation info = (RoutingInformation) routingTable
				.get(destination);
		if (info != null) {
			for (int i = 0; i < info.path.length; i++) {
				PathNetNode node = info.path[i].getFirstNode();
				if (node == this) {
					node = info.path[i].getLastNode();
				}
				list.add(node);
			}
		}
		return (PathNetNode[]) list.toArray(new PathNetNode[list.size()]);
	}
	/**
	 * Returns the name of the PathNetNode
	 * @return	the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the routing information for location with name destinationName
	 * @param destinationName 	the name of the destination
	 * @param path				the possible pathes
	 * @param prob				the propability of the pathes
	 */
	public void setRoutingInformation(String destinationName, PathNetPath[] path, double[] prob) {
		RoutingInformation info = new RoutingInformation();
		info.path = path;
		info.prob = prob;
		routingTable.put(destinationName, info);
	}
	
	
	/**
	 * 
	 * @param offset
	 */
	public void move(Position offset) {
		position=position.add(offset);
	}	
	/**
	 * Creates a path from this node to the destination location by adding all folowing
	 * routing hops to the given path device path  
	 * the next hop is selected by a randomized routing decision and the devicePath is passed 
	 * to the PathNetPath, where the following hop positions are added. 
	 * @param devicePath	the device path to be extended
	 * @param destination			the routing destination name
	 */
	public void createPath(DevicePath devicePath, String destination) {
		if(name.compareTo(destination) == 0) {
			Position result=position.add(new Position(
					(selectDistribution.getNext()-0.5)*width,
					(selectDistribution.getNext()-0.5)*width));
			devicePath.addNextHop(result,name);		
		}
		else {
			RoutingInformation info =
				(RoutingInformation)routingTable.get(destination);
			double d = selectDistribution.getNext();
			double sum = info.prob[0];
			int i = 0;
			while(sum < d) {
				i++;
				sum += info.prob[i];
			}
			info.path[i].createPath(devicePath, destination, name);
		}
	}

	/**
	 * Returns the width of this node.
	 * The node witdh is used to simulate  device movement on pathes having
	 * a witdh and are not only lines.
	 * @return	the witdh of this node
	 */
	public double getWidth() {
		return width;
	}
}
