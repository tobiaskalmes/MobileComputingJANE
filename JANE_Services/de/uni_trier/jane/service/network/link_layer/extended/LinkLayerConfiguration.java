/*****************************************************************************
 * 
 * ${Id}$
 *  
 ***********************************************************************
 *  
 * JANE - The Java Ad-hoc Network simulation and evaluation Environment
 *
 ***********************************************************************
 *
 * Copyright (C) 2002-2006
 * Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * Systemsoftware and Distrubuted Systems
 * University of Trier 
 * Germany
 * http://syssoft.uni-trier.de/jane
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
package de.uni_trier.jane.service.network.link_layer.extended; 

/**
 * @author goergen
 *
 * TODO comment class
 */
public class LinkLayerConfiguration {
    protected double timeout;
    protected int retries;
    /**
     * Constructor for class <code>LinkLayerConfiguration</code>
     * @param timeout
     * @param retries
     */
    public LinkLayerConfiguration(double timeout, int retries) {
        this.timeout = timeout;
        this.retries = retries;
    }
    
     /**
     * TODO Comment method
     * @param defaultConfiguration
     * @return
     */
    public LinkLayerConfiguration setDefaults(LinkLayerConfiguration defaultConfiguration) {
        if (timeout<=0) timeout=defaultConfiguration.getTimeout();
        if (retries<0) retries=defaultConfiguration.getRetries();
        return this;
    }
    
    /**
     * @return Returns the retries.
     */
    public int getRetries() {
        return this.retries;
    }
    
    /**
     * @return Returns the timeout.
     */
    public double getTimeout() {
        return this.timeout;
    }


    
    
   

}
