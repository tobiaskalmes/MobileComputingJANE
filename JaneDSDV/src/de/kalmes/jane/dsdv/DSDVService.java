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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 02.07.13
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class DSDVService implements RuntimeService, NeighborDiscoveryListener {

    private static final long UPDATE_TIME = 60 * 1000L;
    public static ServiceID                     serviceID;
    private       ServiceID                     linkLayerID;
    private       ServiceID                     neighborID;
    private       LinkLayer_async               linkLayer;
    private       NeighborDiscoveryService_sync neighborService;
    private       RuntimeOperatingSystem        runtimeOperatingSystem;
    private       int                           sequenceNumber;
    private       List<DSDVEntry>               routingTable;
    private       Thread                        periodicUpdates;

    public DSDVService(ServiceID linkLayerID, ServiceID neighborID) {
        super();

        //ID des Testservice speichern
        serviceID = new EndpointClassID(DSDVService.class.getName());

        //IDs anderer Services
        this.linkLayerID = linkLayerID;
        this.neighborID = neighborID;
        sequenceNumber = 1000000;
        routingTable = new ArrayList<DSDVEntry>();
        periodicUpdates = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(UPDATE_TIME);
                        //send Broadcast for every entry
                        System.out.println("Executing full update from " + runtimeOperatingSystem.toString());
                        for (DSDVEntry entry : routingTable) {
                            linkLayer.sendBroadcast(new DSDVMessage(entry));
                        }
                        System.out.println("Update from " + runtimeOperatingSystem.toString() + " finished");
                    }
                    catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        };
        periodicUpdates.start();
    }

    @Override
    public void setNeighborData(NeighborDiscoveryData neighborData) {
        linkLayer.sendUnicast(neighborData.getSender(), new DSDVMessage(new DSDVEntry(neighborData.getSender(),
                                                                                      neighborData.getSender(),
                                                                                      ++sequenceNumber)));
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
    public void handleMessage(Address sender, DSDVEntry entry) {
        //ignore messages from myself
        if (!sender.toString().equals(runtimeOperatingSystem.toString())) {
            DSDVEntry newEntry = DSDVEntry.createNewEntry(entry);
            //direct or indirect neighbor?
            if (newEntry.getDestination().toString().equals(sender.toString())) {
                //direct neighbor
                newEntry.resetHopCount();
                newEntry.incHopCount();
                if (upsert(newEntry)) {
                    linkLayer.sendBroadcast(new DSDVMessage(newEntry));
                    //output Table
                    outputTable();
                }
            } else {
                //indirect neighbor
                if (newEntry.getDestination().toString().equals(runtimeOperatingSystem.toString())) {
                    newEntry.resetHopCount();
                    newEntry.setNextHop(newEntry.getDestination());
                    if (upsert(newEntry)) {
                        //output Table
                        linkLayer.sendBroadcast(new DSDVMessage(newEntry));
                        outputTable();
                    }
                } else {
                    newEntry.incHopCount();
                    newEntry.setNextHop(sender);
                    if (upsert(newEntry)) {
                        linkLayer.sendBroadcast(new DSDVMessage(newEntry));
                        //output Table
                        outputTable();
                    }
                }
            }
        }
    }

    /**
     * Either inserts the entry or updates the old entry if an entry for the destination exists
     *
     * @param entry new entry
     * @return true is entry has been upserted, false otherwise
     */
    private boolean upsert(DSDVEntry entry) {
        if (!routingTable.contains(entry)) {
            //No entry yet
            routingTable.add(entry);
            return true;
        } else {
            for (DSDVEntry oldEntry : routingTable) {
                if (oldEntry.getDestination().equals(entry.getDestination())) {
                    if (((oldEntry.getSequenceNumber() < entry.getSequenceNumber()))
                            || (oldEntry.getSequenceNumber() == entry.getSequenceNumber()
                            && oldEntry.getNumberOfHops() > entry.getNumberOfHops())) {
                        //Update routing table
                        oldEntry.update(entry);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void outputTable() {
        System.out.println("---------- Node " + runtimeOperatingSystem + " --------");
        System.out.println
                ("-------------------------------------------------------------------------------------------------");
        System.out.println("|Destination\t|NextHop\t|Number of Hops\t|Sequence Number\t|Install " +
                                   "Time\t\t\t\t\t|");
        System.out.println
                ("-------------------------------------------------------------------------------------------------");
        for (DSDVEntry entry : routingTable) {
            System.out.println("|" + entry.getDestination() + "\t\t\t\t|" + entry.getNextHop() + "\t\t\t|" +
                                       entry.getNumberOfHops() + "\t\t\t\t|" +
                                       entry
                                               .getSequenceNumber() + "\t\t\t|" + entry.getUpdateTime() + "\t|");
        }
        System.out.println
                ("-------------------------------------------------------------------------------------------------");
    }

    public Set getAllReachableDevices() {
        Set<Address> reachableDevices = new HashSet<Address>();
        for (DSDVEntry entry : routingTable) {
            reachableDevices.add(entry.getDestination());
        }
        return reachableDevices;
    }

    public Address getNextHop(Address destination) {
        for (DSDVEntry entry : routingTable) {
            if (destination.toString().equals(entry.getDestination().toString())) {
                return entry.getNextHop();
            }
        }
        return null;
    }

    public int getHopCount(Address destination) {
        for (DSDVEntry entry : routingTable) {
            if (destination.toString().equals(entry.getDestination().toString())) {
                return entry.getNumberOfHops();
            }
        }
        return -1;
    }
}
