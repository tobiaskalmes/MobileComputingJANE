/*****************************************************************************
 * 
 * NullLinkCalculator.java
 * 
 * $Id: NullLinkCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.linkcalculator; 

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.dynamic.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NullLinkCalculator implements DynamicSource, MobilityDynamicSource {
    /**
     * @author goergen
     *
     * To change the template for this generated type comment go to
     * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
     */
    public class NextActionInfo {

        private Action action;
        private ArrivalInfo nextArrivalInfo;
        private DeviceID address;

        /**
         * Constructor for class <code>NextActionInfo</code>
         * @param action
         * @param nextArrivalInfo
         * @param address
         */
        public NextActionInfo(Action action, ArrivalInfo nextArrivalInfo, DeviceID address) {
            this.action=action;
            this.nextArrivalInfo=nextArrivalInfo;
            this.address=address;
            
        }

        /**
         * @return Returns the action.
         */
        public Action getAction() {
            return action;
        }
        /**
         * @return Returns the nextArrivalInfo.
         */
        public ArrivalInfo getNextArrivalInfo() {
            return nextArrivalInfo;
        }

        /**
         * @return
         */
        public DeviceID getAddress() {
            return address;
        }
    }
    /**
     * @author goergen
     *
     * To change the template for this generated type comment go to
     * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
     */
    public class TimeAddressInfo implements Comparable {
        private double time;
        private DeviceID address;


        
        /**
         * Constructor for class <code>TimeAddressInfo</code>
         * @param time
         * @param address
         */
        public TimeAddressInfo(double time, DeviceID address) {
            this.time = time;
            this.address = address;
        }
        

        public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            long temp = Double.doubleToLongBits(time);
            result = PRIME * result + (int) (temp >>> 32);
            result = PRIME * result + (int) (temp & 0xFFFFFFFF);
            if (address != null) {
                result = PRIME * result + address.hashCode();
            }

            return result;
        }

        public boolean equals(Object oth) {
            if (this == oth) {
                return true;
            }

            if (oth == null) {
                return false;
            }

            if (oth.getClass() != getClass()) {
                return false;
            }

            TimeAddressInfo other = (TimeAddressInfo) oth;

            if (this.time != other.time) {
                return false;
            }
            if (this.address == null) {
                if (other.address != null) {
                    return false;
                }
            } else {
                if (!this.address.equals(other.address)) {
                    return false;
                }
            }

            return true;
        }


        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) {
            TimeAddressInfo info=(TimeAddressInfo)o;
            if (time==info.time){
                return address.compareTo(info.address);
            }
            if (time<info.time){
                return -1;
            }
            return 1;
        }
    }

    private MobilitySource mobilitySource;
    private HashSet deviceSet;
    private TreeMap timeArrivalQueue;



    private TrajectoryMapping getTrajectoryMapping(Position startPosition, Position endPosition, double startTime, double endTime){
        Position pos=startPosition.sub(endPosition);
        return new PositionMappingTrajectory(new LinearPositionMapping(startTime, endTime, startPosition, endPosition), new ConstantPositionMapping(pos));
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.DynamicSource#hasNext()
     */
    public boolean hasNext() {
        
        return !deviceSet.isEmpty();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.DynamicSource#next()
     */
    public Action next() {
        TimeAddressInfo time=(TimeAddressInfo)timeArrivalQueue.firstKey();
        NextActionInfo actionInfo=(NextActionInfo)timeArrivalQueue.remove(time);
        DeviceID address=actionInfo.getAddress();
        if (mobilitySource.hasNextArrivalInfo(address)){
            MobilitySource.ArrivalInfo nextArrivalInfo=mobilitySource.getNextArrivalInfo(address);
            TrajectoryMapping trajectoryMapping=getTrajectoryMapping(
                    actionInfo.getNextArrivalInfo().getPosition(),nextArrivalInfo.getPosition(),
                    actionInfo.getNextArrivalInfo().getTime(),nextArrivalInfo.getTime());
            SetPositionMappingAction action=new SetPositionMappingAction(actionInfo.getNextArrivalInfo().getTime(),
                    trajectoryMapping,actionInfo.getNextArrivalInfo().isSuspended(),actionInfo.getAddress());
                    

            if (actionInfo.getNextArrivalInfo().getTime()>nextArrivalInfo.getTime())
            {
                throw new RuntimeException();
            }
            timeArrivalQueue.put(new TimeAddressInfo(actionInfo.getNextArrivalInfo().getTime(),address),
            		new NextActionInfo(action,nextArrivalInfo,address));
        }else{
            if (actionInfo.getNextArrivalInfo()!=null){
                
                ExitAction action=new ExitAction(actionInfo.getNextArrivalInfo().getTime(),actionInfo.getAddress());
                timeArrivalQueue.put(new TimeAddressInfo(actionInfo.getNextArrivalInfo().getTime(),address),
                		new NextActionInfo(action,null,address));
            }else{
                deviceSet.remove(address);
            }
            
            
        }
        
        return actionInfo.getAction();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.DynamicSource#getShape()
     */
    public Shape getShape() {

        return mobilitySource.getShape();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.DynamicSource#getRectangle()
     */
    public Rectangle getRectangle() {

        return mobilitySource.getRectangle();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.linkcalculator.MobilityDynamicSource#start(de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource)
     */
    public void start(MobilitySource mobilitySource) {
        this.mobilitySource=mobilitySource;
        deviceSet=new HashSet();
        timeArrivalQueue=new TreeMap();
        while (mobilitySource.hasNextEnterInfo()){
            MobilitySource.EnterInfo enterInfo=mobilitySource.getNextEnterInfo();
            MobilitySource.ArrivalInfo arrivalInfo=enterInfo.getArrivalInfo();
            DeviceID address=enterInfo.getAddress();
            MobilitySource.ArrivalInfo nextArrivalInfo;
            if (mobilitySource.hasNextArrivalInfo(address)){
                nextArrivalInfo=enterInfo.getArrivalInfo();
            }else{
                 throw new IllegalStateException("An entered device ("+address+") is not allowed to exit immediately.");
            }
            TrajectoryMapping trajectoryMapping=getTrajectoryMapping(arrivalInfo.getPosition(),nextArrivalInfo.getPosition(),arrivalInfo.getTime(),nextArrivalInfo.getTime());
            
            
            deviceSet.add(address);
            EnterAction action=new EnterAction(enterInfo.getArrivalInfo().getTime(),
                    enterInfo.getAddress(),trajectoryMapping,arrivalInfo.isSuspended(),
                    new ConstantDoubleMapping(enterInfo.getSendingRadius()));
            
            timeArrivalQueue.put(new TimeAddressInfo(enterInfo.getArrivalInfo().getTime(),address),
                    		new NextActionInfo(action,nextArrivalInfo,address));
            
        }
        
    }

    public Condition getTerminalCondition(Clock clock) {     
        return mobilitySource.getTerminalCondition(clock);
    }


  
}
