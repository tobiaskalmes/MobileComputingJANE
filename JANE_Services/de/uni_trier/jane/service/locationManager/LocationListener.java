/*****************************************************************************
 * 
 * LocationListener.java}
 * 
 * $Id: LocationListener.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.locationManager;

import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.locationManager.basetypes.Location;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * TODO: comment class  
 * @author daniel
 **/

public interface LocationListener extends SignalListener {
    /**
     * 
     * TODO: comment method 
     *
     */
    public void enteredLocation(Location location);
    /**
     * 
     * TODO: comment method 
     *
     */
    public void leftLocation(Location location);

  
        public static final class EnteredLocationSignal
            implements Signal{


            private Location location;

            /**
             * Constructor for class <code>EnteredLocationSignal</code>
             *
             * @param location
             */
            public EnteredLocationSignal(Location location) {
                super();
                // TODO Auto-generated constructor stub
                this.location = location;
            }

            public Dispatchable copy() {
                return this;
            }

            public Class getReceiverServiceClass() {
                return LocationListener.class;
            }

            public void handle(SignalListener service) {
                ((LocationListener) service).enteredLocation(location);
            }

        }
    

        public static final class LeftLocationSignal implements Signal {
            

            private Location location;

            /**
             * Constructor for class <code>LeftLocationSignal</code>
             *
             * @param location
             */
            public LeftLocationSignal(Location location) {
                super();
                // TODO Auto-generated constructor stub
                this.location = location;
            }

            public Dispatchable copy() {
                return this;
            }

            public Class getReceiverServiceClass() {
                return LocationListener.class;
            }

            public void handle(SignalListener callbackHandler) {
                ((LocationListener) callbackHandler).leftLocation(location);
            }

        }
       

    }
