package de.kalmes.jane.dsdv;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 02.07.13
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class DSDVService implements RuntimeService, NeighborDiscoveryListener {

    public static ServiceID serviceID;
    private ServiceID linkLayerID;
    private ServiceID neighborID;
    private LinkLayer_async linkLayer;
    private NeighborDiscoveryService_sync neighborService;
    private RuntimeOperatingSystem runtimeOperatingSystem;
    private int sequenceNumber;
    private List<DSDVEntry> dsdvTable;

    public DSDVService(ServiceID linkLayerID, ServiceID neighborID) {
        super();

        //ID des Testservice speichern
        serviceID = new EndpointClassID(DSDVService.class.getName());

        //IDs anderer Services
        this.linkLayerID = linkLayerID;
        this.neighborID = neighborID;
        sequenceNumber = 1000000;
        dsdvTable = new ArrayList<DSDVEntry>();
    }

    @Override
    public void setNeighborData(NeighborDiscoveryData neighborData) {
        linkLayer.sendUnicast(neighborData.getSender(),
                new DSDVMessage(new DSDVEntry(runtimeOperatingSystem.toString(), runtimeOperatingSystem.toString(), sequenceNumber++)));
    }

    @Override
    public void updateNeighborData(NeighborDiscoveryData neighborData) {
    }

    @Override
    public void removeNeighborData(Address linkLayerAddress) {
    }

    @Override
    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        this.runtimeOperatingSystem = runtimeOperatingSystem;

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
    public void handleMessage(String sender, DSDVEntry entry) {
        if (runtimeOperatingSystem.toString().equals(sender)) {
            System.out.println("Skipping message from myself");
        } else {
            for(DSDVEntry oldEntry : dsdvTable)
            {
                if(oldEntry.getDestination().equals(entry.getDestination()))
                {
                    if(oldEntry.getSequenceNumber() < entry.getSequenceNumber())
                    {
                }
            }
        }
    }
}
