/*****************************************************************************
* 
* $Id: RuntimeService.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
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
package de.uni_trier.jane.service;

import de.uni_trier.jane.service.operatingSystem.*;

/**
 * Realistic services do not use any simulation specific information in order to
 * implement its functionality.
 */
public interface RuntimeService extends Service {

    /**
     * This method is called before the service is started. Use this method in order
     * to store the passed operating service. Any method call performed on the passed
     * operating service object should be defered until the start method was called.
     * @param runtimeOperatingSystem the operating service running on this device
     */
	// TODO hat ein LocalOperatingSystem zu bekommen (?)
    public void start(RuntimeOperatingSystem runtimeOperatingSystem);

}
