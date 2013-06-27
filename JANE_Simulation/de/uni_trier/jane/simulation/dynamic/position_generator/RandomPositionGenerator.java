/*****************************************************************************
 * 
 * RandomPositionGenerator.java
 * 
 * $Id: RandomPositionGenerator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This is an implementation of the <code>PositionGenerator</code> interface.
 * It uses arbitrary distributions for x and y distribution.
 */
public class RandomPositionGenerator implements PositionGenerator {

	private final static String VERSION = "$Id: RandomPositionGenerator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private ContinuousDistribution xDistribution;
	private ContinuousDistribution yDistribution;
    private ContinuousDistribution zDistribution;
    
	/**
	 * Create a new position generator.
	 * @param xDistribution the distribution of the x component
	 * @param yDistribution the distribution of the y component
	 */
	public RandomPositionGenerator(ContinuousDistribution xDistribution, ContinuousDistribution yDistribution) {
		this(xDistribution,yDistribution,null);
	}
    
    /**
     * Create a new position generator.
     * @param xDistribution the distribution of the x component
     * @param yDistribution the distribution of the y component
     */
    public RandomPositionGenerator(ContinuousDistribution xDistribution, 
                                    ContinuousDistribution yDistribution,
                                    ContinuousDistribution zDistribution) {
        this.xDistribution = xDistribution;
        this.yDistribution = yDistribution;
        this.zDistribution = zDistribution;
    }

	/**
	 * @see de.uni_trier.jane.random.PositionGenerator#getRectangle()
	 */
	public Rectangle getRectangle() {
		return new Rectangle(new Position(xDistribution.getInfimum(), yDistribution.getInfimum()), new Position(xDistribution.getSupremum(), yDistribution.getSupremum()));
	}

	/**
	 * @see de.uni_trier.jane.random.PositionGenerator#getRectangle(double)
	 */
	public Rectangle getRectangle(double time) {
		return new Rectangle(new Position(xDistribution.getInfimum(time), yDistribution.getInfimum(time)), new Position(xDistribution.getSupremum(time), yDistribution.getSupremum(time)));
	}

	/**
	 * @see de.uni_trier.jane.random.PositionGenerator#getNext(double)
	 */
	public Position getNext(double time) {
        if (zDistribution==null){
            return new Position(xDistribution.getNext(time), yDistribution.getNext(time));
        }
        return new Position(xDistribution.getNext(time), yDistribution.getNext(time),zDistribution.getNext());
	}

	/** (non-Javadoc)
	 * @see de.uni_trier.jane.random.PositionGenerator#getShape()
	 */
	public Shape getShape() {
		Extent extent=new Extent(xDistribution.getSupremum()-xDistribution.getInfimum(),yDistribution.getSupremum()-yDistribution.getInfimum());
		return new RectangleShape(new Position(xDistribution.getInfimum()+extent.getWidth()/2,yDistribution.getInfimum()+extent.getHeight()/2)
				,extent,Color.BLACK,false);
	}

}
