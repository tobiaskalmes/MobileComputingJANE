package de.kalmes.jane.twohop;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryListener;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService_sync;
import de.uni_trier.jane.service.network.link_layer.LinkLayer_async;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 16.06.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class TwoHopNeighbourService implements RuntimeService, NeighborDiscoveryListener {
    public static ServiceID serviceID;
    private ServiceID linkLayerID;
    private ServiceID neighborID;
    private LinkLayer_async linkLayer;
    private NeighborDiscoveryService_sync neighborService;
    private RuntimeOperatingSystem runtimeOperatingSystem;

    private Map<String, List<String>> neighbors;

    public TwoHopNeighbourService(ServiceID linkLayerID, ServiceID neighborID) {
        super();

        //ID des Testservice speichern
        serviceID = new EndpointClassID(TwoHopNeighbourService.class.getName());

        //IDs anderer Services
        this.linkLayerID = linkLayerID;
        this.neighborID = neighborID;

        neighbors = new HashMap<String, List<String>>();

        //Beacons werden immer automatisch gesendet, f�r eigenen Inhalt neues Beacon erzeugen und setzen
        //wird nicht unbedingt ben�tigt
        //beacon = new BeaconContent();
    }

    @Override
    public void setNeighborData(NeighborDiscoveryData neighborData) {
        //get direct neighbors
        Set<String> tempNeighbors = neighbors.keySet();
        List<String> neighborsList = new ArrayList<String>();
        for (String item : tempNeighbors) {
            neighborsList.add(item);
        }

        linkLayer.sendUnicast(neighborData.getSender(),
                new TwoHopMessage(neighborsList));
    }

    @Override
    public void updateNeighborData(NeighborDiscoveryData neighborData) {
    }

    @Override
    public void removeNeighborData(Address linkLayerAddress) {
        System.out.println("REMOVE " + runtimeOperatingSystem.getDeviceID() + " from: " + linkLayerAddress.toString());
    }

    @Override
    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        this.runtimeOperatingSystem = runtimeOperatingSystem;

		/*
         * Um den LinkLayer und Nachbarschaftsservice nutzen zu k�nnen, muss sich daran registriert werden.
		 * Nur so k�nnen asynchrone Nachrichten erhalten werden.
		 *
		 * Soll der TestService von einem anderen Service genutzt werden reicht es aus, wenn ein einfaches Interface implementiert wird
		 * und aus dem anderen Service heraus aufgerufen wird.
		 */


        //Am LinkLayer registrieren, um diesen aus TestService heraus nutzen zu k�nnen
        linkLayer = (LinkLayer_async) runtimeOperatingSystem.getSignalListenerStub(linkLayerID,
                LinkLayer_async.class);

        runtimeOperatingSystem.registerAtService(linkLayerID, LinkLayer_async.class);

        //Am Nachbarschaftsservice registrieren, um diesen aus TestService heraus nutzen zu k�nnen
        neighborService = (NeighborDiscoveryService_sync) runtimeOperatingSystem.getSignalListenerStub(neighborID,
                NeighborDiscoveryService_sync.class);

        runtimeOperatingSystem.registerAtService(neighborID,
                NeighborDiscoveryService.class);

        //Eigenes Beacon setzen
        //neighborService.setOwnData(new TestBeacon(beacon));
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

    //Wird aufgerufen, wenn eine Nachricht (sendUnicast) eintrifft
    public void handleMessage(final String sender, final List<String> content) {
        List<String> oldNeighbors = neighbors.get(sender);
        //simplified for non moving, non vanishing nodes
        if (oldNeighbors == null || content.size() != oldNeighbors.size()) {
            //send updates
            linkLayer.sendBroadcast(new TwoHopMessage(content));
            neighbors.put(sender, content);
        }

        //Todo: output neighbors
        System.out.println("Nachricht erhalten: " + content + " (Sender: " + sender + ")");
    }
}
