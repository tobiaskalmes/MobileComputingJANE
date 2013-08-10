package de.kalmes.jane.chat;


import de.kalmes.jane.dsdv.DSDVService;
import de.kalmes.jane.dsdv.IMessageReceiver;
import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.network.link_layer.LinkLayer_async;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 10.08.13
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public class ChatService implements RuntimeService {
    public static ServiceID              serviceID;
    private       ServiceID              linkLayerID;
    private       LinkLayer_async        linkLayer;
    private       RuntimeOperatingSystem runtimeOperatingSystem;
    private       IMessageReceiver       messageReceiver;
    private       DSDVService            dsdvService;

    public ChatService(ServiceID linkLayerID, DSDVService dsdvService) {
        super();
        this.dsdvService = dsdvService;
        serviceID = new EndpointClassID(ChatService.class.getName());
        this.linkLayerID = linkLayerID;
    }

    public void setMessageReceiver(IMessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        this.runtimeOperatingSystem = runtimeOperatingSystem;
        linkLayer = (LinkLayer_async) runtimeOperatingSystem.getSignalListenerStub(linkLayerID, LinkLayer_async.class);
        runtimeOperatingSystem.registerAtService(linkLayerID, LinkLayer_async.class);
    }

    @Override
    public ServiceID getServiceID() {
        return serviceID;
    }

    @Override
    public void finish() {
    }

    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public void getParameters(Parameters parameters) {
    }

    public void handleMessage(Address sender, ChatMessage message) {
        if (message.getReceiver().equals(runtimeOperatingSystem.toString())) {
            //message for you
            messageReceiver.receiveMessage(message.getSender(), message.getMessage());
            System.out.println("Received message for me.");
        } else {
            //send to next hop
            sendMessage(message);
            System.out.println("Received message for someone else.");
        }
    }

    public void sendMessage(String sender, String receiver, String message) {
        Address nextHop = dsdvService.getNextHop(receiver);
        if (nextHop != null) {
            linkLayer.sendUnicast(nextHop, new ChatMessage(sender, receiver, message));
            //linkLayer.sendUnicast(nextHop, new DSDVMessage(new DSDVEntry(nextHop, nextHop, 224976234)));
            System.out.println("Send message to " + nextHop.toString());
        } else {
            System.out.println("No next Hop!");
        }
    }

    public void sendMessage(ChatMessage message) {
        Address nextHop = dsdvService.getNextHop(message.getReceiver());
        if (nextHop != null) {
            linkLayer.sendUnicast(nextHop, message);
            System.out.println("Send message to " + nextHop.toString());
        } else {
            System.out.println("No next Hop!");
        }
    }
}