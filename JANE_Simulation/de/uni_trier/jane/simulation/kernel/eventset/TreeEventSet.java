/*****************************************************************************
 * 
 * TreeEventSet.java
 * 
 * $Id: TreeEventSet.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
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
package de.uni_trier.jane.simulation.kernel.eventset;

import java.util.TreeSet;

public class TreeEventSet implements EventSet {

    private class UniqueEvent implements Comparable{
        private Event event;
        private long stamp;
        
        
        /**
         * Constructor for class <code>UniqueEvent</code>
         *
         * @param event
         * @param stamp
         */
        public UniqueEvent(Event event, long stamp) {
            super();
            // TODO Auto-generated constructor stub
            this.event = event;
            this.stamp = stamp;
        }
        
        public Event getEvent() {
            return event;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof UniqueEvent)) return false;
            UniqueEvent event=(UniqueEvent)obj;
            return stamp==event.stamp;
        }
        
        public int compareTo(Object obj) {
            UniqueEvent other=(UniqueEvent)obj;
            if (other.event.getTime()<event.getTime()) return 1;
            if (other.event.getTime()>event.getTime()) return -1;
            if (other.stamp<stamp) return 1;
            if (other.stamp>stamp) return -1;
            return 0;
        }


        public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            result = PRIME * result + (int) (stamp >>> 32);
            result = PRIME * result + (int) (stamp & 0xFFFFFFFF);
            long temp = Double.doubleToLongBits(event.getTime());
            result = PRIME * result + (int) (temp >>> 32);
            result = PRIME * result + (int) (temp & 0xFFFFFFFF);

            return result;
        }
        
              
    }
    private TreeSet eventSet;
    private double time;
    private long handledEvents;
    private long uniqueStamp;
    
    public TreeEventSet() {
        eventSet=new TreeSet();
    }
    public double getTime() {
        return time;
    }

    public long getCount() {
        return handledEvents;
    }

    public int getSize() {
        return eventSet.size();
    }

    public void add(Event event) {
        eventSet.add(new UniqueEvent(event,uniqueStamp++));
    }

    public boolean hasNext() {
        return !eventSet.isEmpty();
    }

    public void handleNext() {
        UniqueEvent uEvent=(UniqueEvent)eventSet.first();
        eventSet.remove(uEvent);
        Event event=uEvent.getEvent();
        time=event.getTime();
        handledEvents++;
        event.handle();
        

    }

}
