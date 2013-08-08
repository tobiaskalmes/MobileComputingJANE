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
 * Date: 07.08.13
 * Time: 20:21
 * To change this template use File | Settings | File Templates.
 */
public class ChatMessage implements LinkLayerMessage {
    private String message;
    private String sender;
    private String receiver;

    public ChatMessage(String sender, String receiver, String message) {
        super();
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
    }

    @Override
    public void handle(LinkLayerInfo info, SignalListener listener) {
        ((DSDVService) listener).handleMessage(info.getSender(), this);
    }

    @Override
    public Dispatchable copy() {
        return this;
    }

    @Override
    public Class getReceiverServiceClass() {
        return ChatMessage.class;
    }

    @Override
    public int getSize() {
        return 1024;
    }

    @Override
    public Shape getShape() {
        return new RectangleShape(new Extent(10, 10), Color.RED, false);
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
