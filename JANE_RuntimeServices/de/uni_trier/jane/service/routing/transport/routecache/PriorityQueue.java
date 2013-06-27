/*****************************************************************************
 * 
 * PriorityQueue.java
 * 
 * $Id: PriorityQueue.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.transport.routecache;

import java.util.*;


public class PriorityQueue {
    
    private Comparator prioComparator = new PrioritySetComparator();
    
    private Vector  queue = new Vector();
    private Map valueToPrioritySetMap = new HashMap();
    
    private class PrioritySet {
        public int      priority;
        public Object   value;
        
        public PrioritySet(int priority, Object value) {
            this.priority = priority;
            this.value = value;
        }
                    
        public String toString() {
            return "[P:"+priority+";V:"+value.toString()+"]";
        }           
    }
    
    private class PrioritySetComparator implements Comparator {
        public int compare(Object o1, Object o2) {              
            return ((PrioritySet)o1).priority - ((PrioritySet)o2).priority;
        }           
    }
            
    public void insert(int priority, Object o) {
        PrioritySet set = new PrioritySet(priority, o);
        if (queue.contains(set))
            return;
        
        int idx = Collections.binarySearch(queue, set, prioComparator);     
        queue.insertElementAt(set, (idx < 0) ? -(idx+1) : idx);
        valueToPrioritySetMap.put(o, set);
    }
    
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    public Object del_min() {
        Object result = ((PrioritySet)(queue.firstElement())).value;
        queue.remove(queue.firstElement());
        
        valueToPrioritySetMap.remove(result);           
        return result;
    }
    
    public void decrease(int priority, Object o) {          
        queue.remove(valueToPrioritySetMap.get(o));
        valueToPrioritySetMap.remove(o);
        
        insert(priority, o);
    }
    

    public int size() {
        return queue.size();
        
    }    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<queue.size(); i++) {                
            sb.append(queue.elementAt(i).toString());
        }
        return sb.toString();
    }

}