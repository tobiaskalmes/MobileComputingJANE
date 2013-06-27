/*****************************************************************************
 * 
 * HybridConsole.java
 * 
 * $Id: HybridConsole.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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

import de.uni_trier.jane.console.Console;
import de.uni_trier.jane.hybrid.server.RemoteOperatingSystemServer;

import java.rmi.RemoteException;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class HybridConsole implements Console {

    private RemoteOperatingSystemServer remoteOperatingSystem;
    private Console defaultConsole;

    /**
     * Constructor for class HybridConsole 
     *
     * @param remoteOperatingSystem
     * @param defaultConsole
     */
    public HybridConsole(RemoteOperatingSystemServer remoteOperatingSystem, Console defaultConsole) {
        this.remoteOperatingSystem=remoteOperatingSystem;
        this.defaultConsole=defaultConsole;
 
    }

    //
    public void println(String text) {
        try {
            remoteOperatingSystem.write(text); // TODO: Es gibt keinen Unterschied zwischen println und print!!!
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        defaultConsole.println(text);

    }

    //
    public void print(String text) {
        try {
            remoteOperatingSystem.write(text);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        defaultConsole.print(text);

    }

    //
    public void println() {
        try {
            remoteOperatingSystem.write("");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        defaultConsole.println();


    }

}
