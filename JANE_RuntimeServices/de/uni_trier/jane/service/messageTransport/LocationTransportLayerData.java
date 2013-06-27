/*****************************************************************************
 * 
 * TransportLayerData.java
 * 
 * $Id: LocationTransportLayerData.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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

public class LocationTransportLayerData implements RoutingData {

    private transient Shape shape;
    private RoutingData data;
    private Address receiver;
    private double finalUnicastTimeout;

    public LocationTransportLayerData(RoutingData data, Address receiver, double finalUnicastTimeout) {
        this.data=data;
        
        
        Shape dataShape=data.getShape();
        if (dataShape!=null){
            ShapeCollection collection = new ShapeCollection();
            collection.addShape(dataShape);
            collection.addShape(new RectangleShape(shape.getRectangle(Position.NULL_POSITION,Matrix.identity3d()).getExtent(),Color.RED,false));
            shape=collection;
        }else{
            shape=new RectangleShape(new Extent(5,5),Color.RED,false);
        }
        this.receiver=receiver;
        this.finalUnicastTimeout=finalUnicastTimeout;
        
        
    }

    public void handle(RoutingHeader routingHeader,
            SignalListener signalListener) {
        ((MessageTransportLayer)signalListener).receiveTransportLayerData(routingHeader,data,receiver,finalUnicastTimeout);

    }

    public Dispatchable copy() {
        RoutingData dataCopy=(RoutingData)data.copy();
        if (data==dataCopy) return this;
        return new LocationTransportLayerData(data,receiver,finalUnicastTimeout);
    }

    public Class getReceiverServiceClass() {
        return MessageTransportLayer.class;
    }

    public int getSize() {
        return 9*8+data.getSize()+receiver.getCodingSize();
    }

    public Shape getShape() {
        return shape;
    }

}
