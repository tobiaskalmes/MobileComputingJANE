/*
 * Created on 14.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.link_layer.winfra;

import java.awt.Image;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.network.link_layer.LinkLayer;
import de.uni_trier.jane.service.network.link_layer.LinkLayerAddress;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.SimulationLinkLayerAddress;
import de.uni_trier.jane.service.network.link_layer.LinkLayer.LinkLayerStub;
import de.uni_trier.jane.service.network.link_layer.winfra.interfaces.InterfaceBS;
import de.uni_trier.jane.service.network.link_layer.winfra.interfaces.InterfaceBSO;
import de.uni_trier.jane.service.network.link_layer.winfra.interfaces.InterfaceUCN;
import de.uni_trier.jane.service.network.link_layer.winfra.signals.ModifiedMessageSignal;
import de.uni_trier.jane.service.network.link_layer.winfra.signals.ModifyMessageSignal;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.ImageShape;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;
import de.uni_trier.jane.visualization.shapes.TextShape;

/**
 * @author christian.hiedels
 *
 * This Service represents a Base Station. It forwards incoming Messages to other
 * Base Stations or directly to Clients.
 * Furthermore it has the ability to signal incoming messages to an optional service,
 * implementing the <code>MessageModification</code> interface, where messages can be
 * modified in an application dependant way.
 */
public class WinfraBaseStation implements RuntimeService, InterfaceBS, ModifiedMessageSignal {

    //Runtime operating system.
	private RuntimeOperatingSystem runtimeOperatingSystem;
	// The DeviceID of the hosting Device
	private DeviceID deviceID;
	// A Stub to the BaseStationOrganizer
	InterfaceBSO.BSOStub bso;
	// A Stub to the ClientNetwork
	InterfaceUCN.UCNStub ucn;
	// The Position of the BaseStation
	Position position;
	// The ServiceID of the Message Modification Service
	ServiceID messageModifierServiceID = null;
	
//	// A Map of Devices which are currently registered with this BaseStation
//	private HashMap registeredDevices;	// <DeviceID/RuntimeOperatingSystem>
	// Sending Range
	public static int SENDING_RANGE = 100;
	// The ServiceID
	protected static final ServiceID SERVICE_ID = new EndpointClassID( "Winfra Base Station" );

	// Service ID for the link layer service.
	private ServiceID linkLayerServiceID;
	// A Stub for the link layer service which allows to send messages.
	private LinkLayerStub linkLayerStub;
	// Link layer address of the current device.
	private LinkLayerAddress deviceLLAddress;
	
	/**
	 * Constructor
	 * @param pos The Position of the BaseStation
	 */
	public WinfraBaseStation( ServiceID linkLayerServiceID, Position position ) {
		this.linkLayerServiceID = linkLayerServiceID;
		this.position = position;
//		registeredDevices = new HashMap();
	}
	
	public static WinfraBaseStation createInstance(ServiceUnit serviceUnit, Position position) {
		ServiceID linkLayerServiceID = serviceUnit.getService(LinkLayer.class);
		WinfraBaseStation baseStationService = new WinfraBaseStation( linkLayerServiceID, position );
		serviceUnit.addService( baseStationService );
		return baseStationService;
	}
	
	/**
	 * Starts the Service. Register the BaseStation at the BaseStationOrganizer Network.
	 * Also register it at the ClientNetwork with its Position.
	 */
	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		this.runtimeOperatingSystem = runtimeOperatingSystem;
		deviceID = runtimeOperatingSystem.getDeviceID();
		
		// Register this BaseStation at the BaseStationNetwork
		ServiceID serviceID = runtimeOperatingSystem.getServiceIDs( InterfaceBSO.class ) [0];
		bso = new InterfaceBSO.BSOStub( runtimeOperatingSystem, serviceID );
		bso.registerBaseStation( this.runtimeOperatingSystem );

		// create a stub to the link layer
		linkLayerStub = new LinkLayerStub( runtimeOperatingSystem, linkLayerServiceID );
		linkLayerStub.registerAtService();

		// register at the client network
		try {
			ServiceID serviceID2 = runtimeOperatingSystem.getServiceIDs( InterfaceUCN.class ) [0];
			ucn = new InterfaceUCN.UCNStub( runtimeOperatingSystem, serviceID2 );
			ucn.registerBS( deviceID, this.runtimeOperatingSystem );
		}
		catch ( ArrayIndexOutOfBoundsException aioob ) {
			System.out.println( "Basestation Organizer Network is not running! No Connection between BaseStations!!!" );
		}
		// set the link layer address of the current device
		deviceLLAddress = linkLayerStub.getLinkLayerAddress();
		
		// check if some kind of messagemodification service is running
		if( runtimeOperatingSystem.hasService(MessageModification.class) )
			this.messageModifierServiceID = runtimeOperatingSystem.getServiceIDs(MessageModification.class)[0];
	}

	public ServiceID getServiceID() {
		return WinfraBaseStation.SERVICE_ID;
	}

	public void finish() {
		// nop
	}

	/**
	 * The Shape of the Devices that are running this Service.
	 */
	public Shape getShape() {
        ShapeCollection shape = new ShapeCollection();
        Image img = java.awt.Toolkit.getDefaultToolkit().getImage(
        		WinfraBaseStation.class.getResource("radio_tower.jpg")
        );
        shape.addShape(new ImageShape(
        		deviceID, 
        		img,
        		new Position(-img.getWidth(null)/2, -img.getHeight(null)/2),
        		new Extent(15, 15)
        ));
//        shape.addShape( new ImageShape("radio_tower.jpg", position, new Extent(15, 15)));
//        shape.addShape( new EllipseShape( runtimeOperatingSystem.getDeviceID(), new Extent( 7, 7 ), Color.BLUE, true ) );
        shape.addShape(new TextShape(
        		deviceID.toString(),
        		deviceID,
        		Color.BLACK,
        		new Position(-1.5, -img.getHeight(null)/2 - 3)
        ));
		return shape; 
	}

	public void getParameters(Parameters parameters) {
	}

	public void sendCellBroadcast( LinkLayerMessage llm ) {
		if( messageModifierServiceID != null ) {	// send a signal
			ModifyMessageSignal mms =
				(ModifyMessageSignal) runtimeOperatingSystem.getSignalListenerStub(
						messageModifierServiceID,
						ModifyMessageSignal.class);
			mms.receiveMessage2ModifySignal( llm, deviceLLAddress );
		}
		else {	// no modification
			linkLayerStub.sendBroadcast( llm );
		}
	}
	
	public void receiveMessageFromBSO( LinkLayerMessage llm, DeviceID finalReceiver ) {
		if( messageModifierServiceID != null ) {	// send a signal
				ModifyMessageSignal mms =
					(ModifyMessageSignal) runtimeOperatingSystem.getSignalListenerStub(
							messageModifierServiceID,
							ModifyMessageSignal.class);
				mms.receiveMessage2ModifySignal( llm, deviceLLAddress, finalReceiver );
		}
		else {	// no modification
			linkLayerStub.sendUnicast( new SimulationLinkLayerAddress(finalReceiver), llm );
		}
	}
	
	public void receiveMessageFromCN( LinkLayerMessage llm, DeviceID targetBSreceiver, DeviceID finalReceiver ) {
		if( messageModifierServiceID != null ) {	// send a signal
			ModifyMessageSignal mms =
				(ModifyMessageSignal) runtimeOperatingSystem.getSignalListenerStub(
						messageModifierServiceID,
						ModifyMessageSignal.class);
			mms.receiveMessage2ModifySignal( llm, deviceLLAddress, targetBSreceiver, finalReceiver );
		}
		else {	// no modification
			bso.distributeMessage( llm, targetBSreceiver, finalReceiver );
		}
	}
	
	/**
	 * Send a modified LinkLayerMessage as a Broadcast
	 */
	public void receiveModifiedMessageSignal( LinkLayerMessage llm ) {
		linkLayerStub.sendBroadcast( llm );
	}

	/**
	 * Send a modified LinkLayerMessage as a Unicast
	 */
	public void receiveModifiedMessageSignal( LinkLayerMessage llm, DeviceID finalReceiver ) {
		linkLayerStub.sendUnicast( new SimulationLinkLayerAddress(finalReceiver), llm );
	}

	/**
	 * Distribute a modified LinkLayerMessage among other Base Stations
	 */
	public void receiveModifiedMessageSignal( LinkLayerMessage llm, DeviceID targetBSreceiver, DeviceID finalReceiver ) {
		bso.distributeMessage( llm, targetBSreceiver, finalReceiver );
	}
}