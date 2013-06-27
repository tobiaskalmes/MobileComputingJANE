/*****************************************************************************
 * 
 * PathNetPath.java
 * 
 * $Id: PathNetPath.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class implements a path between two PathNetNodes. 
 * It has a start node and an end node and a series of Positions representing 
 * the path. Each position can have a width which represents the paths witdh.
 */
public class PathNetPath {

	/**
     * This class implements a quadrangle shape
	 */
	private class QuadrangleShape implements Shape {
		private Shape shape;
		/**
		 * Constructor for class <code>QuadrangleShape</code>
		 * It extents a line to to a quadrangle with a given witdhs at start and endpoint. 
		 * @param startPosition		the starting point of the line
		 * @param startWidth		the width of the Quadrangle at the starting point
		 * @param endPosition		the end point of the line
		 * @param endWidth			the width of the Quadrangle at the end point
		 * @param color				the color of the Quadrangle
		 */
		public QuadrangleShape(Position startPosition, double startWidth, Position endPosition, double endWidth, Color color) {
			if (!startPosition.equals(endPosition)){
				Position dir=startPosition.sub(endPosition);
				
				Position orth=dir.turnZ(90);
				Position longStartPosition=endPosition.add(dir.scale((startWidth/2+dir.length())/(dir.length())));
				Position longEndPosition=endPosition.sub(dir.scale(endWidth/(2*dir.length())));
				Position orthStart=orth.scale(startWidth/(2*orth.length()));
				Position orthEnd=orth.scale(endWidth/(2*orth.length()));
				//double test=orth.length();
				ArrayList list=new ArrayList();
				if (startWidth>0){
					list.add(longStartPosition.add(orthStart));
					list.add(longStartPosition.sub(orthStart));
				}else{
					list.add(startPosition);
				}
				if (endWidth>0){
					list.add(longEndPosition.sub(orthEnd));
					list.add(longEndPosition.add(orthEnd));
				}else{
					list.add(endPosition);
				}
				Rectangle rect=(new Rectangle(startPosition.add(orthStart),endPosition.add(orthStart)).union(
							   new Rectangle(startPosition.sub(orthEnd),endPosition.sub(orthEnd))));
				shape=new PolygonShape(new PositionListImpl(list,rect),color, true);
			}else{
				shape=EmptyShape.getInstance();
			}
			
			
			
		}
		
		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.visualization.Shape#getRectangle(de.uni_trier.ubi.appsim.kernel.basetype.Position)
		 */
		public Rectangle getRectangle(Position position, Matrix matrix) {
			
			return shape.getRectangle(position,matrix);
		}

        /* (non-Javadoc)
         * @see de.uni_trier.ubi.appsim.kernel.visualization.shapes.Shape#visualize(de.uni_trier.ubi.appsim.kernel.basetype.Position, de.uni_trier.ubi.appsim.kernel.visualization.Worldspace, de.uni_trier.ubi.appsim.kernel.AddressPositionMap)
         */
        public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
            shape.visualize(position, worldspace, addressPositionMap);
        }
	}
	private String name;
	private PathNetNode firstNode;
	private PathNetNode lastNode;
	private Position[] positions;
	private double[] width;
	private ContinuousDistribution selectDistribution;

	/**
	 * Constructor for class <code>PathNetNode</code>
	 * @param name				the uniqe path name
	 * @param firstNode			the starting node of the path
	 * @param lastNode			the end node of the path
	 * @param positions			the paths positions
	 * @param widths			the witdh of the path at each position
	 * @param selectDistribution  continous uniform distribution between 0 and 1 for selecting entry positions for each sub line of the path  
	 */
	public PathNetPath(	String name,
						PathNetNode firstNode, 
						PathNetNode lastNode, 
						Position[] positions, 
						double[] widths, 
						ContinuousDistribution selectDistribution) {
		this.selectDistribution=selectDistribution;
		this.name=name;
		this.firstNode=firstNode;
		this.lastNode=lastNode;
		this.positions=positions;
		this.width=widths;
	}

	/**
	 * Returns the end node of the path
	 * @return	the <code>PathNetNode</code> at the end of the path
	 */
	public PathNetNode getLastNode() {
		return lastNode;
	}

	/**
	 * Returns the start node of the path
	 * @return	the <code>PathNetNode</code> at the beginning of the path
	 */
	public PathNetNode getFirstNode() {
		return firstNode;
	}

	/**
	 * Returns the unique name of the path
	 * @return	the path name
	 */
	public String getName() {
		return name;
	}

	/**
	 * returnt a Rectangle containing the path
	 * @return	the <code>Rectangle</code>
	 */
	public Rectangle getRectangle() {
		Position minPos=firstNode.getPosition().sub(new Position(firstNode.getWidth()/2,firstNode.getWidth()/2));
		Position maxPos=firstNode.getPosition().add(new Position(firstNode.getWidth()/2,firstNode.getWidth()/2));
		
		for (int i=0; i<positions.length;i++){
			if (minPos.compare(positions[i].sub(new Position(width[i]/2,width[i]/2)))<0){
				minPos=positions[i].sub(new Position(width[i]/2,width[i]/2));
			}
			if (maxPos.compare(positions[i].add(new Position(width[i]/2,width[i]/2)))>0){
				maxPos=positions[i].add(new Position(width[i]/2,width[i]/2));
			}
				
		}
		if (minPos.compare(lastNode.getPosition().sub(new Position(lastNode.getWidth()/2,lastNode.getWidth()/2)))<0){
			minPos=lastNode.getPosition().sub(new Position(lastNode.getWidth()/2,lastNode.getWidth()/2));
		}
		if (maxPos.compare(lastNode.getPosition().add(new Position(lastNode.getWidth()/2,lastNode.getWidth()/2)))>0){
			maxPos=lastNode.getPosition().add(new Position(lastNode.getWidth()/2,lastNode.getWidth()/2));
		}

		
		return new Rectangle(minPos,maxPos);
	}

	/**
	 * Returns the shape of the path for visualisation
	 * @return	the path shape 
	 */
	public Shape getShape() {
		ShapeCollection result=new ShapeCollection();
		Position startPosition=positions[0];
		double startWidth=width[0];
		
		for (int i=1;i<positions.length;i++){
			if (startWidth>0||width[i]>0){
				result.addShape(new QuadrangleShape(startPosition,startWidth,positions[i],width[i],Color.LIGHTBLUE),Position.NULL_POSITION);
			}else{
				result.addShape(new LineShape(startPosition,positions[i],Color.LIGHTBLUE),Position.NULL_POSITION);
			}
			startPosition=positions[i];
			startWidth=width[i];
		}
		

		return result;
	}
	
	/**
	 * Moves the complete path relativ to this offset
	 * All postions of the path are changed by adding the offset
	 * @param offset	the offset for moving
	 */
	public void move(Position offset) {
		for (int i=1; i<positions.length-1; i++) {
			positions[i]=positions[i].add(offset);
		}
	}

	/**
	 * Creates the device path by adding the immidiate positions of this path to the given <code>DevicePath</code> starting 
	 * at node startNode. The result is passed to the other end node of the path and is completed there.
	 * 
	 * @param devicePath				the <code>DevicePath</code> to be extended
	 * @param destinationLocationName	the name of the devices destination
	 * @param startNodeName				the name of the node where the device starts the movement on this path
	 */
	public void createPath(DevicePath devicePath, String destinationLocationName, String startNodeName) {


			if(startNodeName.compareTo(firstNode.getName()) == 0) {
                devicePath.addNextHop(getPosition(0),startNodeName);
				for (int i=1;i<positions.length-1;i++){
					
					devicePath.addNextHop(getPosition(i));
				}
				lastNode.createPath(devicePath,destinationLocationName);
				
			}
			else {
                devicePath.addNextHop(getPosition(positions.length-1),startNodeName);
				for (int i=positions.length-2;i>=1;i--){
					devicePath.addNextHop(getPosition(i));
				}
                
				firstNode.createPath(devicePath,destinationLocationName);
			}
			
		
	}

	/**
	 * Varies the actual path position within the given width at this position randomly
	 * @param i the index of the current position
	 * @return 	the position
	 */
	private Position getPosition(int i) {
		Position var=new Position((selectDistribution.getNext(0)-0.5)*width[i],
								  (selectDistribution.getNext(0)-0.5)*width[i]);
		return var.add(positions[i]);
		
	}
}
