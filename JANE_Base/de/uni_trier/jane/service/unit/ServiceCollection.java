/*****************************************************************************
 * 
 * ServiceCollection.java
 * 
 * $Id: ServiceCollection.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.unit; 

import java.sql.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;


/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ServiceCollection {

   
    private HashMap serviceMap;
    private List serviceIDs;
    private HashSet visualizeSet;
    private int timestamp;
        
    /**
     * Constructor for class <code>ServiceCollection</code>
     * 
     */
    public ServiceCollection() {
        serviceMap=new HashMap();
        visualizeSet=new HashSet();
        serviceIDs=new ArrayList();
    
     
    }
    
    
    /**
     * @return
     */
    public List getServiceIDs() {
        return serviceIDs;
    }

    /**
     * @param serviceID
     * @return
     */
    public boolean visualize(ServiceID serviceID) {
        return visualizeSet.contains(serviceID);
    }
    
    /**
     * TODO Comment method
     * @param serviceID
     * @return
     */
    public Service getService(ServiceID serviceID) {
     
        return (Service)serviceMap.get(serviceID);
    }

    
    /**
     * 
     * @param service
     */
    public void add(Service service,ServiceID serviceID) {
        
        add(service,serviceID,true);
        
    }

    /**
     * 
     * @param service
     * @param visulize
     */
    public void add(Service service, ServiceID serviceID, boolean visualize) {

        
        serviceIDs.add(serviceID);
        serviceMap.put(serviceID,service);
        if (visualize){
            visualizeSet.add(serviceID);
        }   
    }
    
  

    /**
  


    /**
     * TODO Comment method
     * @return
     */
    public int size() {

        return serviceMap.size();
    }


  

 
}
