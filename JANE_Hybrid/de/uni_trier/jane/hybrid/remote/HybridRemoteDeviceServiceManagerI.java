/*****************************************************************************
 * 
 * HybridLocalDeviceServiceManager.java
 * 
 * $Id: HybridRemoteDeviceServiceManagerI.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.hybrid.remote; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public interface HybridRemoteDeviceServiceManagerI extends ServiceManager {



    /**
     * TODO Comment method
     * 
     */
    void notifyStartBoot();

    /**
     * TODO Comment method
     * @param serviceID
     * @return
     */
    Shape getServiceShape(ServiceID serviceID);

    /**
     * TODO Comment method
     * @return
     */
    boolean hasRemoteService();



}
