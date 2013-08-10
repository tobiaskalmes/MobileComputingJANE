package de.kalmes.jane.dsdv;

import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.RectangleShape;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 02.07.13
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class DSDVMessage implements LinkLayerMessage {
    private DSDVEntry entry;

    public DSDVMessage(DSDVEntry entry) {
        super();
        this.entry = entry;
    }

    @Override
    public void handle(LinkLayerInfo info, SignalListener listener) {
        ((DSDVService) listener).handleMessage(info.getSender(), entry);
    }

    @Override
    public Dispatchable copy() {
        return this;
    }

    @Override
    public Class getReceiverServiceClass() {
        return DSDVService.class;
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
