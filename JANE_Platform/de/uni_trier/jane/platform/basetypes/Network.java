/*****************************************************************************
 * 
 * Network.java
 * 
 * $Id: Network.java,v 1.1 2007/06/25 07:23:00 srothkugel Exp $
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
package de.uni_trier.jane.platform.basetypes; 


import java.net.*;
import java.util.*;

import de.uni_trier.jane.service.network.link_layer.*;


/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Network {
    /**
     * @return
     * @throws NetworkException
     */
    public static InetAddress getFirstInetAddress() throws NetworkException {
        //try {
            return getAllInetAddresses()[0];
        //} catch (UnknownHostException e) {
        //    throw new NetworkException(e.getMessage());
        //}
    }
    
    public static NetworkInterface[] getAllNetworkInterface() throws NetworkException{
        Enumeration enumeration=null;
        try {
            enumeration=NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new NetworkException(e.getMessage());
            
        }
        ArrayList
        	list=new ArrayList();
        while (enumeration!=null&&enumeration.hasMoreElements()){
            list.add(enumeration.nextElement());
        }
        return (NetworkInterface[])list.toArray(new NetworkInterface[list.size()]);
        
    }
    
    /**
     * 
     * @return
     * @throws NetworkException
     */
    public static InetAddress[] getAllInetAddresses() throws NetworkException{
        ArrayList list=new ArrayList();
        Enumeration enumeration;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new NetworkException(e.getMessage());
        }
        while (enumeration.hasMoreElements()){
            NetworkInterface ni=(NetworkInterface)enumeration.nextElement();
            Enumeration addresses=ni.getInetAddresses();
            while (addresses.hasMoreElements()){
                list.add(addresses.nextElement());
            }
        }
        return (InetAddress[])list.toArray(new InetAddress[list.size()]);
    }

 
}
