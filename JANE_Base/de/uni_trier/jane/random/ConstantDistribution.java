/*****************************************************************************
 * 
 * ConstantDistribution.java
 * 
 * $Id: ConstantDistribution.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ConstantDistribution implements ContinuousDistribution {
    private double value;
    
    /**
     * Constructor for class <code>ConstantDistribution</code>
     * @param value
     */
    public ConstantDistribution(double value) {
        this.value = value;
    }
    /* (non-Javadoc)
     * @see de.uni_trier.ubi.appsim.kernel.random.ContinuousDistribution#getInfimum()
     */
    public double getInfimum() {
        return value;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ubi.appsim.kernel.random.ContinuousDistribution#getSupremum()
     */
    public double getSupremum() {

        return value;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ubi.appsim.kernel.random.ContinuousDistribution#getInfimum(double)
     */
    public double getInfimum(double t) {
        return value;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ubi.appsim.kernel.random.ContinuousDistribution#getSupremum(double)
     */
    public double getSupremum(double t) {
        return value;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.ubi.appsim.kernel.random.ContinuousDistribution#getNext(double)
     */
    public double getNext(double t) {
        return value;
    }

    public double getNext() {
        return value;
    }

}
