import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.beaconing.events.BeaconDataEvent;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryListener;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService_sync;
import de.uni_trier.jane.service.network.link_layer.LinkLayer_async;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.visualization.shapes.Shape;


public class TestService implements RuntimeService, NeighborDiscoveryListener {

	public static ServiceID serviceID;
	private ServiceID linkLayerID;
	private ServiceID neighborID;

	private LinkLayer_async linkLayer;
	private NeighborDiscoveryService_sync neighborService;
	private RuntimeOperatingSystem runtimeOperatingSystem;



	public TestService(ServiceID linkLayerID, ServiceID neighborID) {
		super();

		//ID des Testservice speichern
		serviceID = new EndpointClassID(TestService.class.getName());

		//IDs anderer Services
		this.linkLayerID = linkLayerID;
		this.neighborID = neighborID;

		//Beacons werden immer automatisch gesendet, f�r eigenen Inhalt neues Beacon erzeugen und setzen
		//wird nicht unbedingt ben�tigt
		//beacon = new BeaconContent();
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
		linkLayer=(LinkLayer_async)runtimeOperatingSystem.getSignalListenerStub(linkLayerID,
					LinkLayer_async.class);

		runtimeOperatingSystem.registerAtService(linkLayerID, LinkLayer_async.class);

		//Am Nachbarschaftsservice registrieren, um diesen aus TestService heraus nutzen zu k�nnen
		neighborService = (NeighborDiscoveryService_sync)runtimeOperatingSystem.getSignalListenerStub(neighborID,
				NeighborDiscoveryService_sync.class);

		runtimeOperatingSystem.registerAtService(neighborID,
				NeighborDiscoveryService.class);

		//Eigenes Beacon setzen
		//neighborService.setOwnData(new TestBeacon(beacon));

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub

	}

	@Override
	public ServiceID getServiceID() {
		// TODO Auto-generated method stub
		return serviceID;
	}

	@Override
	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

	//Wird aufgerufen, wenn eine Verbindung zu einem anderen Ger�t getrennt wird
	@Override
	public void removeNeighborData(Address linkLayerAddress) {
		System.out.println("REMOVE " + runtimeOperatingSystem.getDeviceID() + " from: " + linkLayerAddress.toString());

	}

	//Wird aufgerufen, wenn eine Verbindung zu einem anderen Ger�t aufgebaut wird
	@Override
	public void setNeighborData(NeighborDiscoveryData neighborData) {
		System.out.println("SET " + runtimeOperatingSystem.getDeviceID() + " to: " + neighborData.getSender());

	//Nachricht an ein anderes Ger�t senden
	linkLayer.sendUnicast(neighborData.getSender(),
			new TestMessage("Hallo von "+ runtimeOperatingSystem.getDeviceID()));

	}

	//Wird aufgerufen, wenn ein periodisches Beacon eintrifft
	@Override
	public void updateNeighborData(NeighborDiscoveryData neighborData) {
		/*
		System.out.println("UPDATE " + runtimeOperatingSystem.getDeviceID() + 
				" to: " + neighborData.getSender());*/
	}

	//Wird aufgerufen, wenn eine Nachricht (sendUnicast) eintrifft
	public void handleMessage(final String sender, final String content){
		System.out.println("Nachricht erhalten: "+content);
	}

}
