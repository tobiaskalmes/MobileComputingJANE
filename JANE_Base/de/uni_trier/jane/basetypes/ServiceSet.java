/*****************************************************************************
 * 
 * ServiceSet.java
 * 
 * $Id: ServiceSet.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.basetypes; 

import java.util.*;


import de.uni_trier.jane.service.*;


/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ServiceSet{

    private List list;

    /**
     * 
     */
    public  ServiceSet() {
        list=new ArrayList();

    }
    
    public int size() {
        return list.size();
    }

//    public boolean isEmpty() {
//        return map.isEmpty();
//    }

//    public boolean contains(ServiceID serviceID) {
//        return map.containsKey(serviceID);
//    }

    /* (non-Javadoc)
     * @see java.util.Set#iterator()
     */
    public ServiceIterator iterator() {
     
        return new ServiceIterator(){
            private Iterator iterator=list.iterator();
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Service next() {
                return (Service)iterator.next();
            }
        };
    }

//    public Service[] toArray() {
//        return (Service[])map.values().toArray(new Service[map.size()]);
//    }

    public boolean add(Service service) {

        return list.add(service);
    }

  

    /* (non-Javadoc)
     * @see java.util.Set#containsAll(java.util.Collection)
     */
//    public boolean containsAll(ServiceSet serviceSet) {
//        return map.keySet().containsAll(serviceSet.map.keySet());
//    }

    /* (non-Javadoc)
     * @see java.util.Set#addAll(java.util.Collection)
     */
//    public void union(ServiceSet serviceSet) {
//        map.putAll(serviceSet.map);
//    }


    /* (non-Javadoc)
     * @see java.util.Set#clear()
     */
//    public void clear() {
//        map.clear();
//        
//    }

    
}
