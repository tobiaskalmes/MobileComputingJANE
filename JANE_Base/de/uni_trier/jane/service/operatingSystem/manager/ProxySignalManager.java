/*****************************************************************************
 * 
 * ProxySignalManager.java
 * 
 * $Id: ProxySignalManager.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.operatingSystem.manager; 

import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.reflectionSignal.ProxySignal;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.operatingSystem.Action;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class ProxySignalManager {
    
    private ExecutionManager executionManager;
    
    public SignalListener getProxy(ListenerID listenerID){
        return null;
        
        
    }
    
    public ListenerID registerProxy(SignalListener signalListener){
        return null;
        
    }
    
    public SignalListener getSignalListener(ListenerID listenerID){
        return null;
        
    }
    
    public boolean hasListener(ListenerID listenerID){
        return false;
        
    }

    /**
     * TODO Comment method
     * @param receiverContext
     * @param listenerID
     * @param signal
     */
    public void sendSignal(ServiceContext receiverContext, ListenerID listenerID, ProxySignal signal) {
//        executionManager.schedule(new Action() {
//        
//            public void execute(Service executingService) {
//                // TODO Auto-generated method stub
//        
//            }
//        
//        });
        
    }
    
    
    
    

}
