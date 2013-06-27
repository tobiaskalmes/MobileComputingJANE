/*****************************************************************************
 * 
 * HotspotPositionGenerator.java
 * 
 * $Id: HotspotPositionGenerator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This implementation of the <code>PositionGenerator</code> interface contains
 * an array of position generators and a discrete distribution and works as follows:
 * The index of the position generator to be used is determined from the given
 * discrete distribution. After this the next position is determined from the chosen
 * position generator.
 * 
 */
public class HotspotPositionGenerator implements PositionGenerator {

	private final static String VERSION = "$Id: HotspotPositionGenerator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private PositionGenerator[] hotspotArray ;
	private DiscreteDistribution selectDistribution;

	/**
	 * Construct a new <code>HotspotPositionGenerator</code> object.
	 * @param hotspotArray the array of position generators
	 * @param selectDistribution the discrete distribution to select the generators
	 */
	public HotspotPositionGenerator(PositionGenerator[] hotspotArray, DiscreteDistribution selectDistribution) {
		this.hotspotArray = hotspotArray;
		this.selectDistribution = selectDistribution;
	}

	/**
	 * @see de.uni_trier.jane.random.PositionGenerator#getRectangle()
	 */
	public Rectangle getRectangle() {
		Rectangle result = null;
		IntegerIterator it = selectDistribution.getDomain().iterator();
		while(it.hasNext()) {
			if(result == null) {
				result = hotspotArray[it.next()].getRectangle();
			}
			else {
				result = result.union(hotspotArray[it.next()].getRectangle());
			}
		}
		return result;
	}

	/**
	 * @see de.uni_trier.jane.random.PositionGenerator#getRectangle(double)
	 */
	public Rectangle getRectangle(double time) {
		Rectangle result = null;
		IntegerIterator it = selectDistribution.getDomain(time).iterator();
		while(it.hasNext()) {
			if(result == null) {
				result = hotspotArray[it.next()].getRectangle(time);
			}
			else {
				result = result.union(hotspotArray[it.next()].getRectangle(time));
			}
		}
		return result;
	}

	/**
	 * @see de.uni_trier.jane.random.PositionGenerator#getNext(double)
	 */
	public Position getNext(double time) {
		return hotspotArray[selectDistribution.getNext(time)].getNext(time);
	}
	


	/**
	 * @see de.uni_trier.jane.random.PositionGenerator#getShape()
	 */
	public Shape getShape() {
		ShapeCollection shape = new ShapeCollection();
		for (int i=0;i<hotspotArray.length;i++){
			shape.addShape(hotspotArray[i].getShape(),new Position(0,0));
		}
		return shape;
	}

}
