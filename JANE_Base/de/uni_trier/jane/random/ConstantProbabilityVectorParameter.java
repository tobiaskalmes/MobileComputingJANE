/*****************************************************************************
 * 
 * ConstantProbabilityVectorParameter.java
 * 
 * $Id: ConstantProbabilityVectorParameter.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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

/**
 * This implementation of the <code>ProbabilityVectorParameter<code> is independent of the current time.
 */
public class ConstantProbabilityVectorParameter implements ProbabilityVectorParameter {

	private final static String VERSION = "$Id: ConstantProbabilityVectorParameter.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private ProbabilityVector probabilityVector;

	/**
	 * Construct a new <code>ConstantProbabilityVectorParameter</code> object.
	 * @param probabilityVector the constant probability vector
	 */
	public ConstantProbabilityVectorParameter(ProbabilityVector probabilityVector) {
		this.probabilityVector = probabilityVector;
	}

	/**
	 * @see de.uni_trier.jane.random.ProbabilityVectorParameter#value(double)
	 */
	public ProbabilityVector value(double t) {
		return probabilityVector;
	}

}
