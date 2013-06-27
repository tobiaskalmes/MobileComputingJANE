/*****************************************************************************
 * 
 * TransportLayerData.java
 * 
 * $Id: TransportLayerData.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.messageTransport;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.transport.TransportLayerDataReceiver;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

public abstract class TransportLayerData implements RoutingData {

    private transient ShapeCollection shape;
    protected RoutingData data;

    public TransportLayerData(RoutingData data) {
        this.data=data;
        shape=new ShapeCollection();
        Shape dataShape=data.getShape();
        if (dataShape!=null){
            shape.addShape(dataShape);
            shape.addShape(new RectangleShape(shape.getRectangle(Position.NULL_POSITION,Matrix.identity3d()).getExtent(),Color.RED,false));
        }
        
        
        
    }

    public void handle(RoutingHeader routingHeader,
            SignalListener signalListener) {
        ((MessageTransportLayer)signalListener).receiveTransportLayerData(routingHeader,data);

    }



    public Class getReceiverServiceClass() {
        return MessageTransportLayer.class;
    }

    public int getSize() {
        return 1+data.getSize();
    }

    public Shape getShape() {
        return shape;
    }

}
