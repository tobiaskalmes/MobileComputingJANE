/*****************************************************************************
 * 
 * Campus.java
 * 
 * $Id: PathNet.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2003 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.position_generator;

import java.awt.geom.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.visualization.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class implements a position generator intended to be used for fixed network simulation.
 * By applying the rejection method positions are uniformly distributed over the path network.
 * The path network is created initially in the following way. Given the crossing count and area
 * size a number of crossings are created using a uniform distribution. Afterwards a gabriel graph
 * is created form the crossing points.
 */
public class PathNet implements PositionGenerator {

	private final static String VERSION = "$Id: PathNet.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";
	
	private double areaSize;
	private List crossingList;
	private PositionGenerator positionGenerator;
	private Area area;

	/**
	 * Constructs a new path net object.
	 * @param distributionCreator the source of random
	 * @param crossingCount the number of crossings in the plane
	 * @param areaSize the size in x and y dimension of the plane
	 * @param pathWidth the with of all paths connecting the crossings
	 * @param wrapBorder if <code>true</code> the crossings are copied to all neighbors of the simulated plane
	 * in order to avoid accumulations of positions in the middle of the simulated plane
	 */
	public PathNet(DistributionCreator distributionCreator, int crossingCount, double areaSize, double pathWidth, boolean wrapBorder) {
		this.areaSize = areaSize;
		// create the position generator for generating all positions
		DoubleMapping a = new ConstantDoubleMapping(0);
		DoubleMapping b = new ConstantDoubleMapping(areaSize);
		ContinuousDistribution x1 = distributionCreator.getContinuousUniformDistribution(a, b);
		ContinuousDistribution y1 = distributionCreator.getContinuousUniformDistribution(a, b);
		this.positionGenerator = new RandomPositionGenerator(x1, y1);
		// create the position generator for crossings
		ContinuousDistribution x = distributionCreator.getContinuousUniformDistribution(a, b);
		ContinuousDistribution y = distributionCreator.getContinuousUniformDistribution(a, b);
		PositionGenerator positionGenerator = new RandomPositionGenerator(x, y);
		// create the crossing list
		crossingList = new ArrayList();
		for (int i = 0; i < crossingCount; i++) {
			Position center = positionGenerator.getNext(0);
			Crossing crossing = new Crossing(center, pathWidth);
			crossingList.add(crossing);
		}
		// extend the crossing list in order to avoid position acuumulations in the middle of the plane
		if (wrapBorder) {
			List list = crossingList;
			crossingList = new ArrayList();
			addBorder(list, new Position(0, 0));
			addBorder(list, new Position(areaSize, 0));
			addBorder(list, new Position(-areaSize, 0));
			addBorder(list, new Position(areaSize, areaSize));
			addBorder(list, new Position(0, areaSize));
			addBorder(list, new Position(-areaSize, areaSize));
			addBorder(list, new Position(areaSize, -areaSize));
			addBorder(list, new Position(0, -areaSize));
			addBorder(list, new Position(-areaSize, -areaSize));
		}
		// create paths between crossings
		Iterator iterator1 = crossingList.iterator();
		while (iterator1.hasNext()) {
			Crossing source = (Crossing) iterator1.next();
			Iterator iterator2 = crossingList.iterator();
			while (iterator2.hasNext()) {
				Crossing destination = (Crossing) iterator2.next();
				if(isConnected(source, destination)) {
					source.addReachable(destination);
				}
			}
		}
		// create the are for the rejection of positions
		area = createArea();
	}

	/**
	 * Get an outlined shape of the path network
	 * @return the shape
	 */
	public Shape getShapeOutlined() {
		PathIterator pathIterator = area.getPathIterator(null);
		double[] coords = new double[6];
		List positionList = null;
		Position startPosition = null;
		List polygonList = new ArrayList();
		List rectangleList = new ArrayList();
		double xmin = Double.POSITIVE_INFINITY;
		double ymin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymax = Double.NEGATIVE_INFINITY;
		double x, y;
		while(!pathIterator.isDone()) {
			int type = pathIterator.currentSegment(coords);
			switch(type) {
			case PathIterator.SEG_MOVETO :
				x = coords[0];
				y = coords[1];
				startPosition = new Position(x, y);
				xmin = Math.min(xmin, x);
				ymin = Math.min(ymin, y);
				xmax = Math.max(xmax, x);
				ymax = Math.max(ymax, y);
				positionList = new ArrayList();
				positionList.add(startPosition);
				break;
			case PathIterator.SEG_LINETO :
				x = coords[0];
				y = coords[1];
				Position position = new Position(x, y);
				xmin = Math.min(xmin, x);
				ymin = Math.min(ymin, y);
				xmax = Math.max(xmax, x);
				ymax = Math.max(ymax, y);
				positionList.add(position);
				break;
			case PathIterator.SEG_CLOSE :
				positionList.add(startPosition);
				polygonList.add(positionList);
				rectangleList.add(new Rectangle(new Position(xmin,ymin), new Position(xmax, ymax)));
				break;
			default :
				throw new IllegalStateException("only polygonal types are expected");
			}
			pathIterator.next();
		}
		ShapeCollection backgroundShape = new ShapeCollection();
		Iterator it1 = polygonList.iterator();
		Iterator it2 = rectangleList.iterator();
		while (it1.hasNext()) {
			List list = (List) it1.next();
			Rectangle rectangle = (Rectangle) it2.next();
			backgroundShape.addShape(new PolygonShape(new PositionListImpl(list, rectangle), Color.LIGHTGREY, false), new Position(0, 0));
		}
		return backgroundShape;
	}
		
	/**
	 * Get a filled shape of the path network
	 * @return the shape
	 */
	public Shape getShapeFilled() {
		ShapeCollection result = new ShapeCollection();
		Iterator iterator = crossingList.iterator();
		while (iterator.hasNext()) {
			Crossing crossing = (Crossing) iterator.next();
			result.addShape(crossing.getShape(), Position.NULL_POSITION);
		}
		return result;
	}

	/**
	 * @see de.uni_trier.jane.simulation.dynamic.position_generator.PositionGenerator#getShape()
	 */
	public Shape getShape() {
		return getShapeOutlined();
	}

	/**
	 * @see de.uni_trier.jane.simulation.dynamic.position_generator.PositionGenerator#getRectangle()
	 */
	public Rectangle getRectangle() {
		return positionGenerator.getRectangle();
	}

	/**
	 * @see de.uni_trier.jane.simulation.dynamic.position_generator.PositionGenerator#getRectangle(double)
	 */
	public Rectangle getRectangle(double time) {
		return positionGenerator.getRectangle(time);
	}

	/**
	 * @see de.uni_trier.jane.simulation.dynamic.position_generator.PositionGenerator#getNext(double)
	 */
	public Position getNext(double time) {
		while(true) {
			Position position = positionGenerator.getNext(time);
			if(area.contains(position.getX(), position.getY())) {
				return position;
			}
		}
	}

	private Area createArea() {
		Area result = new Area();
		Iterator iterator = crossingList.iterator();
		while (iterator.hasNext()) {
			Crossing crossing = (Crossing) iterator.next();
			result.add(crossing.getArea());
		}
		Rectangle2D.Double rectangle = new Rectangle2D.Double(0,0,areaSize,areaSize);
		result.intersect(new Area(rectangle));
		return result;
	}

	private void addBorder(List list, Position offset) {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Crossing crossing = (Crossing) it.next();
			crossingList.add(new Crossing(crossing.getCenter().add(offset), crossing.getRadius()));
		}
	}

	private boolean isConnected(Crossing source, Crossing destination) {
		double radius = source.getCenter().distance(destination.getCenter()) / 2.0;
		Position center = source.getCenter().sub(destination.getCenter()).scale(0.5).add(destination.getCenter());
		Iterator iterator = crossingList.iterator();
		while (iterator.hasNext()) {
			Crossing crossing = (Crossing) iterator.next();
			if(!source.equals(crossing) && !destination.equals(crossing) && crossing.getCenter().distance(center)<=radius) {
				return false;
			}
		}
		return true;
	}

	private class Crossing {
		private Position center;
		private double radius;
		private HashSet reachableVerticeSet;
		private Crossing(Position center, double radius) {
			reachableVerticeSet = new HashSet();
			this.center = center;
			this.radius = radius;
		}
		private Position getCenter() {
			return center;
		}
		private double getRadius() {
			return radius;
		}
		private Shape getShape() {
			Color color = new Color(240, 240, 240);
			ShapeCollection result = new ShapeCollection();
			result.addShape(new EllipseShape(center, new Extent(radius*2.0, radius*2.0), color, true), Position.NULL_POSITION);
			Position from = center;
			Iterator edgeIterator = getOutgoingEdges();
			while(edgeIterator.hasNext()) {
				Crossing crossing = (Crossing)edgeIterator.next();
				List positionList = new ArrayList();
				Position offset = getOffset(this, crossing);
				Rectangle rectangle = addPosition(getCenter(), offset, positionList);
				offset = getOffset(crossing, this);
				rectangle.union(addPosition(crossing.getCenter(), offset, positionList));
				result.addShape(new PolygonShape(new PositionListImpl(positionList, rectangle), color, true), Position.NULL_POSITION);
			}
			return result;
		}
		private void addReachable(Crossing vertice) {
			reachableVerticeSet.add(vertice);
		}
		private Iterator getOutgoingEdges() {
			return reachableVerticeSet.iterator();
		}
		private Area getArea() {
			Area area = new Area();
			GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			PathIterator pathIterator = new EllipsePathIterator(center, new Extent(2.0 * radius, 2.0 * radius), 16);
			generalPath.append(pathIterator, true);
			area.add(new Area(generalPath));
			Iterator edgeIterator = getOutgoingEdges();
			while(edgeIterator.hasNext()) {
				Crossing destinationCrossing = (Crossing)edgeIterator.next();
				generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
				generalPath.append(getPathIterator(this, destinationCrossing), true);
				area.add(new Area(generalPath));
			}
			return area;
		}
		private PathIterator getPathIterator(Crossing sourceCrossing, Crossing destinationCrossing) {
			List positionList = new ArrayList();
			Position offset = getOffset(sourceCrossing, destinationCrossing);
			Rectangle rectangle = addPosition(sourceCrossing.getCenter(), offset, positionList);
			offset = getOffset(destinationCrossing, sourceCrossing);
			rectangle.union(addPosition(destinationCrossing.getCenter(), offset.scale(1.0), positionList));
			return new PolygonPathIterator(new PositionIteratorImpl(positionList.iterator()));
		}
		private Position getOffset(Crossing source, Crossing destination) {
			Position offset = destination.getCenter().sub(source.getCenter());
			double length = offset.length();
			if(length <= 0.0) {
				return Position.NULL_POSITION;
			}
			else {
				offset = offset.scale(source.getRadius()/length);
				return new Position(-offset.getY(), offset.getX());
			}
		}
		private Rectangle addPosition(Position center, Position offset, java.util.List positionList) {
			Position position1 = center.add(offset);
			Position position2 = center.add(offset.scale(-1.0));
			positionList.add(position1);
			positionList.add(position2);
			return new Rectangle(position1, position2);
		}
	}

}
