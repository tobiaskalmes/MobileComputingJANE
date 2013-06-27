/*****************************************************************************
 * 
 * PositioningListener.java
 * 
 * $Id: PositioningListener.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.positioning;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.*;

/**
 * A registered positioning system listener is notified every time the position
 * of a device has to be updated. Additionally, a (registered or unregistered)
 * positioning system listener may actively request the current device position.
 * 
 * @see de.uni_trier.jane.service.positioning.PositioningService
 */
public interface PositioningListener extends SignalListener {

    /**
	 * This method is called by the positioning system in order to notify new
	 * location information.
	 * 
	 * @param data the current position information
	 */
    public void updatePositioningData(PositioningData data);

    /**
     * This method is called by the positioning system in order to notify that
     * there is currently no position info available (e.g. if a GPS receiver
     * is operated indoors).
     */
    // TODO diese Methode ist überflüssig
//    public void removePositioningData();

    /**
     * Use this signal in order to send all positioning system listeners that
     * device position has to be updated.
     */
    public static class UpdateLocationInfoSignal implements Signal {

        private PositioningData locationSystemInfo;

        /**
         * Construct a new position info signal
         * @param positioningData the new position information
         */
        public UpdateLocationInfoSignal(PositioningData positioningData) {
            this.locationSystemInfo = positioningData;
        }

        public void handle(SignalListener service) {
            PositioningListener listener = (PositioningListener)service;
            listener.updatePositioningData(locationSystemInfo);
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return PositioningListener.class;
        }

    }

    /**
     * Use this signal in order to notify all positioning system listeners that
     * there is no valid position information available at the moment.
     */
    public static class RemoveLocationInfoSignal implements Signal {

        public Class getReceiverServiceClass() {
            return PositioningListener.class;
        }

        public void handle(SignalListener service) {
            PositioningListener listener = (PositioningListener)service;
            listener.updatePositioningData(null);
        }

        public Dispatchable copy() {
            return this;
        }
        
    }

}
