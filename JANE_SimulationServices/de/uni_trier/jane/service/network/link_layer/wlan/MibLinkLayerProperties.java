/*****************************************************************************
 * 
 * MibLinkLayerProperties.java
 * 
 * $Id: MibLinkLayerProperties.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 *  
 * Copyright (C) 2002-2006 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.network.link_layer.*;

public class MibLinkLayerProperties extends LinkLayerProperties {

    private Mib mib;

    public MibLinkLayerProperties(Mib mib, Address linkLayerAddress, double minimumTransmissionRadius, double maximumTransmissionRadius) {
        super(linkLayerAddress, mib.isPromiscous(), minimumTransmissionRadius,
                maximumTransmissionRadius);
        this.mib=mib;
        
    }
    
    public Mib getMib(){
        return mib;
    }
    
    
    public boolean isPromiscuousMode() {
        return mib.isPromiscous();
    }



}
