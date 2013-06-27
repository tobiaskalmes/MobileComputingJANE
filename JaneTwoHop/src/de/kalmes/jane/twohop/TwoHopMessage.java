package de.kalmes.jane.twohop;

import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.RectangleShape;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 19.06.13
 * Time: 13:39
 * To change this template use File | Settings | File Templates.
 */
public class TwoHopMessage implements LinkLayerMessage {

    private List<String> neighbors;

    public TwoHopMessage(List<String> neighbors) {
        super();
        this.neighbors = neighbors;
    }

    @Override
    public void handle(LinkLayerInfo info, SignalListener listener) {
        String sender = info.getSender().toString();
        ((TwoHopNeighbourService) listener).handleMessage(sender, neighbors);
    }

    @Override
    public Dispatchable copy() {
        return this;
    }

    @Override
    public Class getReceiverServiceClass() {
        return TwoHopNeighbourService.class;
    }

    @Override
    public int getSize() {
        return 1024;
    }

    @Override
    public Shape getShape() {
        return new RectangleShape(new Extent(10, 10), Color.RED, false);
    }
}
