/*****************************************************************************
 * 
 * PlatformParameters.java
 * 
 * $Id: PlatformParameters.java,v 1.1 2007/06/25 07:23:00 srothkugel Exp $
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
package de.uni_trier.jane.platform; 


import de.uni_trier.jane.console.*;
import de.uni_trier.jane.random.*;

import java.net.*;
import java.util.*;


/**
 * @author goergen
 *
 * JANE Platform default parameters 
 */
public interface PlatformParameters {

    /**
     * @return Returns the debugConsole.
     */
    public Console getDebugConsole() ;
    
    /**
     * @param debugConsole The debugConsole to set.
     */
    public void setDebugConsole(Console debugConsole);
    
    /**
     * @return Returns the defaultConsole.
     */
    public Console getDefaultConsole() ;
    
    /**
     * @param defaultConsole The defaultConsole to set.
     */
    public void setDefaultConsole(Console defaultConsole);
    
    /**
     * @return Returns the distributionCreator.
     */
    public DistributionCreator getDistributionCreator();
    
    /**
     * @param distributionCreator The distributionCreator to set.
     */
    public void setDistributionCreator(DistributionCreator distributionCreator);

    /**
     * Switch debugging on/off 
     * @param debugMode	true for switching debugging on
     */
    public void setDebugMode(boolean debugMode);
    
    /**
     * Returns true if debug mode is switched on
     * @return true if debug mode is switched on
     */
    public boolean isDebugMode();

  
    /**
     * Changes the depth of event template matching. Default is 0. This matches only the  event attributes by equals. 
     * Using 1, also the event attributes are matched as templates and their attributes are matched using equals and so forth.
     * 
     * @param depth
     */
    public void setEventReflectionDepth(int depth);
    
}
