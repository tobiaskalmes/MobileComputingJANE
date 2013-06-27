/*****************************************************************************
 * 
 * RandomIntervalGenerator.java
 * 
 * $Id: RandomIntervalGenerator.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.random;

import de.uni_trier.jane.basetypes.*;

/**
 * This is apossible implementation of the <code>IntervalGenerator</code> interface.
 * It works with arbitrary distributions.
 */
public class RandomIntervalGenerator implements IntervalGenerator {

	private final static String VERSION = "$Id: RandomIntervalGenerator.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private ContinuousDistribution firstDistribution;
	private ContinuousDistribution lastDistribution;

	/**
	 * Construct a new interval generator.
	 * @param firstDistribution the distribution of the left side
	 * @param lastDistribution the distribution of the right side
	 */
	public RandomIntervalGenerator(ContinuousDistribution firstDistribution, ContinuousDistribution lastDistribution) {
		if(firstDistribution.getSupremum() > lastDistribution.getInfimum()) {
			throw new IllegalArgumentException("The first value might be greater than the last value.");
		}
		this.firstDistribution = firstDistribution;
		this.lastDistribution = lastDistribution;
	}
	
	/**
	 * @see de.uni_trier.jane.random.IntervalGenerator#getInfimum()
	 */
	public DoubleInterval getInfimum() {
		return new DoubleInterval(firstDistribution.getInfimum(), lastDistribution.getInfimum());
	}

	/**
	 * @see de.uni_trier.jane.random.IntervalGenerator#getSupremum()
	 */
	public DoubleInterval getSupremum() {
		return new DoubleInterval(firstDistribution.getSupremum(), lastDistribution.getSupremum());
	}

	/**
	 * @see de.uni_trier.jane.random.IntervalGenerator#getInfimum(double)
	 */
	public DoubleInterval getInfimum(double t) {
		return new DoubleInterval(firstDistribution.getInfimum(t), lastDistribution.getInfimum(t));
	}

	/**
	 * @see de.uni_trier.jane.random.IntervalGenerator#getSupremum(double)
	 */
	public DoubleInterval getSupremum(double t) {
		return new DoubleInterval(firstDistribution.getSupremum(t), lastDistribution.getSupremum(t));
	}

	/**
	 * @see de.uni_trier.jane.random.IntervalGenerator#getNext(double)
	 */
	public DoubleInterval getNext(double t) {
		return new DoubleInterval(firstDistribution.getNext(t), lastDistribution.getNext(t));
	}

}
