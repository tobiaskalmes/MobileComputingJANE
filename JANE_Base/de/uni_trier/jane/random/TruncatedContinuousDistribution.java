/*****************************************************************************
 * 
 * TruncatedContinuousDistribution.java
 * 
 * $Id: TruncatedContinuousDistribution.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
 * Use this class to truncate an arbitrary distribution to a given interval.
 * The implementation is done by the rejection method.
 */
public class TruncatedContinuousDistribution implements ContinuousDistribution {

	private final static String VERSION = "$Id: TruncatedContinuousDistribution.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private ContinuousDistribution distribution;
	private DoubleIntervalMapping interval;

	/**
	 * Construct a <code>TruncatedContinuousDistribution</code> object.
	 * @param distribution the distribution to truncate
	 * @param interval the interval
	 */
	public TruncatedContinuousDistribution(ContinuousDistribution distribution, DoubleIntervalMapping interval) {
		this.distribution = distribution;
		this.interval = interval;
	}

	/**
	 * @see de.uni_trier.jane.random.ContinuousDistribution#getInfimum()
	 */
	public double getInfimum() {
		return Math.max(interval.getInfimum().getFirst(), distribution.getInfimum());
	}

	/**
	 * @see de.uni_trier.jane.random.ContinuousDistribution#getSupremum()
	 */
	public double getSupremum() {
		return Math.min(interval.getSupremum().getLast(), distribution.getSupremum());
	}

	/**
	 * @see de.uni_trier.jane.random.ContinuousDistribution#getInfimum(double)
	 */
	public double getInfimum(double t) {
		return Math.max(interval.getValue(t).getFirst(), distribution.getInfimum());
	}

	/**
	 * @see de.uni_trier.jane.random.ContinuousDistribution#getSupremum(double)
	 */
	public double getSupremum(double t) {
		return Math.min(interval.getValue(t).getLast(), distribution.getSupremum());
	}

	/**
	 * @see de.uni_trier.jane.random.ContinuousDistribution#getNext(double)
	 */
	public double getNext(double t) {
		double value;
		do {
			value = distribution.getNext(t);
		} while(value < interval.getValue(t).getFirst() || value > interval.getValue(t).getLast());
		return value;
	}

	public double getNext() {
		return getNext(0.0);
	}

}

