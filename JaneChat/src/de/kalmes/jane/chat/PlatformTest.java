package de.kalmes.jane.chat;

import de.htw.moco.ExecutionPlatform2;
import de.kalmes.jane.dsdv.DSDVService;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.platform.DefaultPlatformParameters;
import de.uni_trier.jane.platform.PlatformParameters;
import de.uni_trier.jane.platform.basetypes.NetworkException;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.OneHopNeighborDiscoveryService;
import de.uni_trier.jane.service.network.link_layer.LinkLayer;
import de.uni_trier.jane.service.network.link_layer.packetNetwork.PacketPlatformNetwork;
import de.uni_trier.jane.service.unit.ServiceUnit;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 28.07.13
 * Time: 17:23
 * To change this template use File | Settings | File Templates.
 */
public class PlatformTest extends ExecutionPlatform2 {

    private PacketPlatformNetwork network;

    public static void main(String[] args) {
        try {
            PlatformTest test = new PlatformTest();
            test.run();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initPlatform(PlatformParameters parameters) {
        try {
            network = new PacketPlatformNetwork();
        }
        catch (NetworkException e) {
            e.printStackTrace();
        }
        try {
            ((DefaultPlatformParameters) parameters).setNetworkAddress(
                    InetAddress.getByName(network.getNetworkAddress().toString()));
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initServices(ServiceUnit serviceUnit) {
        serviceUnit.addService(network);

        ServiceID linkLayerID = serviceUnit.getService(LinkLayer.class);

        OneHopNeighborDiscoveryService.createInstance(serviceUnit, false);
        ServiceID neighborID = serviceUnit.getService(NeighborDiscoveryService.class);

        DSDVService dsdvService = new DSDVService(linkLayerID, neighborID);
        serviceUnit.addService(dsdvService);

        try {
            ChatGUI chatGUI = new ChatGUI(network.getNetworkAddress().toString(), dsdvService);
            chatGUI.setVisible(true);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
