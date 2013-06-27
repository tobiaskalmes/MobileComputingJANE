/*****************************************************************************
 * 
 * PositioningService_Sync.java
 * 
 * $Id: PositioningService.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;

/**
 * A positioning service provides information about the current physical device
 * position (GPS is such a service, for instance). A simple implementation may
 * used the simulation global knowledge in order to determine the exact device
 * location. A more elaborate implementation may introduce variations to the
 * real position information.
 * 
 * @see de.uni_trier.jane.service.positioning.PositioningListener
 */
public interface PositioningService {
    
    /**
	 * This method is used in order to request location system info actively.
	 * 
	 * @return the current information about the device position.
	 */
    public PositioningData getPositioningData();

    // TODO PositioningServiceFassade ist ein Sub-Set von PositioningServiceStub
    // TODO Eigentlich braucht man diese Klassen mit der automatischen Erzeugung
    // von Stubs nicht mehr. 
    public static final class PositioningServiceFassade{
        private RuntimeOperatingSystem operatingSystem;
        private ServiceID positioningService;
        
        /**
         * Constructor for class <code>PositioningServiceFassade</code>
         * @param positioningService
         * @param operatingSystem
         */
        public PositioningServiceFassade(
                ServiceID positioningService,
                RuntimeOperatingSystem operatingSystem) {
            this.operatingSystem = operatingSystem;
            this.positioningService = positioningService;
        }
        
        /**
         * Use this signal in order to request the current location system info actively.
         * TODO comment
         */
        // TODO: umbenennen in PositioningServiceData
        private static class LocationServiceData implements ListenerAccess {

    		public Object handle(SignalListener service) {
    			PositioningService positioningService = (PositioningService)service;
    			return positioningService.getPositioningData();
    		}

    		public Dispatchable copy() {
    			return this;
    		}

    		public Class getReceiverServiceClass() {
                return PositioningService.class;
    		}		
        }
        
        
        public PositioningData getPositioningData(){
            return (PositioningData)operatingSystem.accessSynchronous(positioningService,new LocationServiceData());
            
        }
    }
    public static final class PositioningServiceStub {
        private RuntimeOperatingSystem operatingSystem;
        private ServiceID PositioningServiceServiceID;
        public PositioningServiceStub(
            RuntimeOperatingSystem operatingSystem,
            ServiceID PositioningServiceServiceID) {
            this.operatingSystem = operatingSystem;
            this.PositioningServiceServiceID = PositioningServiceServiceID;
        }

        public void registerAtService() {
            operatingSystem.registerAtService(
                PositioningServiceServiceID,
                PositioningService.class);
        }

        public void unregisterAtService() {
            operatingSystem.unregisterAtService(
                PositioningServiceServiceID,
                PositioningService.class);
        }

        private static final class GetPositioningDataSyncAccess
            implements
                ListenerAccess {
            public GetPositioningDataSyncAccess() {
            }

            public Dispatchable copy() {
                return this;
            }

            public Class getReceiverServiceClass() {
                return PositioningService.class;
            }

            public Object handle(SignalListener service) {
                return ((PositioningService) service).getPositioningData();
            }

        }
        public PositioningData getPositioningData() {
            return (PositioningData) operatingSystem.accessSynchronous(
                PositioningServiceServiceID,
                new GetPositioningDataSyncAccess());
        }

    }}
