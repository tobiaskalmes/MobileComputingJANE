/*****************************************************************************
 * 
 * HybridClock.java
 * 
 * $Id: HybridClock.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.hybrid.remote.manager;

import de.uni_trier.jane.basetypes.Clock;
import de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer;

import java.rmi.RemoteException;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class HybridClock implements Clock {

    private RemoteOperatingSystemServer remoteOperatingSystem;

    /**
     * Constructor for class HybridClock 
     *
     * @param remoteOperatingSystem
     */
    public HybridClock(RemoteOperatingSystemServer remoteOperatingSystem) {
        this.remoteOperatingSystem=remoteOperatingSystem;

    }

    //
    public double getTime() {
        try {
            return remoteOperatingSystem.getTime();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    //
    public void setTime(double time) {
        try {
            remoteOperatingSystem.setTime(time);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
