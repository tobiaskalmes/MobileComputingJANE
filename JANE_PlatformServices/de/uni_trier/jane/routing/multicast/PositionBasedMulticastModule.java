package de.uni_trier.jane.routing.multicast;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.network.link_layer.PlatformLinkLayerAddress;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.positioning.*;
import de.uni_trier.jane.service.routing.DefaultRoutingHeader;
import de.uni_trier.jane.service.routing.RoutingAlgorithm;
import de.uni_trier.jane.service.routing.RoutingHeader;
import de.uni_trier.jane.service.routing.RoutingTaskHandler;
import de.uni_trier.jane.service.routing.multicast.MulticastGroupID;
import de.uni_trier.jane.service.routing.multicast.MulticastRoutingAlgorithm;
import de.uni_trier.jane.service.routing.multicast.MulticastRoutingAlgorithm_Sync;
import de.uni_trier.jane.service.routing.multicast.MulticastRoutingHeader;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.jspbm.JSPBMConfigRemoteClient;

public class PositionBasedMulticastModule implements RoutingAlgorithm,
		MulticastRoutingAlgorithm_Sync, MulticastRoutingAlgorithm, RuntimeService, PositioningListener {
	
	private JSPBMConfigRemoteClient configClient = null;
	
	
    /**
     * TODO Comment method
     * @param serviceUnit
     */
    public static void createInstance(ServiceUnit serviceUnit) {
        serviceUnit.addService(new PositionBasedMulticastModule());
        
    }
    
    
	public static final ServiceID SERVICE_ID = new EndpointClassID(PositionBasedMulticastModule.class.getName());

	public void handleStartRoutingRequest(RoutingTaskHandler handler,
			RoutingHeader routingHeader) {
		//handler.createOpenTask();
		PlatformLinkLayerAddress address=createMulticastAddress(((SimplePBMHeader)routingHeader).getMulticastGroupID());
		((DefaultRoutingHeader)routingHeader).setPromiscousHeader(false);
		((DefaultRoutingHeader)routingHeader).setPromiscousMessage(false);
		handler.forwardAsUnicast(routingHeader,address);
		//deliver locally?
		//handler.finishOpenTask();

	}

	public static PlatformLinkLayerAddress createMulticastAddress(MulticastGroupID multicastGroupID) {
		// TODO change to multicast address, test with broadcast...
        try {
            return new PlatformLinkLayerAddress(InetAddress.getByName("10.255.255."+(short)multicastGroupID.getGroupID()));
        } catch (UnknownHostException exeception) {
            // TODO Auto-generated catch block
            exeception.printStackTrace();
        }
		return null;
	}
	
	public static MulticastGroupID createMulticastGroupID(PlatformLinkLayerAddress address) {
		// we hope to parse the group id from sender...
        return new MulticastGroupID(1);
		//return null;
	}

	public void handleMessageReceivedRequest(RoutingTaskHandler handler,
			RoutingHeader header, Address sender) {
		MulticastGroupID groupID=createMulticastGroupID((PlatformLinkLayerAddress)sender);
		((SimplePBMHeader)header).setMulticastGroupID(groupID);
		handler.deliverMessage(header);

	}

	
	public void handleUnicastErrorRequest(RoutingTaskHandler handler,
			RoutingHeader header, Address receiver) {
		//should never happen!

	}

	public void handleMessageForwardProcessed(RoutingHeader header) {
		// ignore

	}
	
    public void handlePromiscousHeader(RoutingHeader routingHeader) {
    	// ignore
    }

	public void handleMessageDelegateRequest(RoutingTaskHandler handler,
			RoutingHeader routingHeader) {
		//should never happen
		handleStartRoutingRequest(handler,routingHeader);

	}
	
	public boolean isInGroup(MulticastGroupID multicastGroupID){
		//access module
		long questionedID = multicastGroupID.getGroupID();
		int[] currentGroups=null;
		try{
			currentGroups = configClient.getJoinedGroups();
			
			for (int i = 0; i< currentGroups.length; i++){
				if((long)currentGroups[i] == questionedID ){
					return true;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}

	public MulticastGroupID[] getJoinedGroups() {
		//access module
		int[] groups = null;
		MulticastGroupID[] result = null;
		try{
			groups = this.configClient.getJoinedGroups();
			
			if(groups != null){
				result = new MulticastGroupID[groups.length];
				
				for (int i = 0; i < groups.length; i++){
					result[i] = new MulticastGroupID( (long) groups[i]);
				}	
			}
		}
		catch(Exception e){
			System.err.println("-- Error getting joined groups from JSPBM daemon");
			e.printStackTrace();
		}
		return result;//is null if no groups are joined
	}



	public void joinGroup(MulticastGroupID multicastGroupID) {
		//access module
		try{
			configClient.joinGroup((int) multicastGroupID.getGroupID());
		}catch(Exception e){
			System.err.println("-- Error while joining multicast group");
			e.printStackTrace();
		}
	}

	public void leaveGroup(MulticastGroupID multicastGroupID) {
		//access module
		try{
			configClient.leaveGroup((int) multicastGroupID.getGroupID());
		}catch(Exception e){
			System.err.println("-- Error while leaving multicast group");
			e.printStackTrace();
		}
	}
	
	
	public RoutingHeader getMulticastRoutingHeader(
			MulticastGroupID multicastGroupID) {
		SimplePBMHeader header = new SimplePBMHeader();
		header.setMulticastGroupID(multicastGroupID);
		return header;
	}
	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
	    runtimeOperatingSystem.registerAccessListener(MulticastRoutingAlgorithm_Sync.class);
        runtimeOperatingSystem.registerSignalListener(MulticastRoutingAlgorithm.class);
        if (runtimeOperatingSystem.hasService(PositioningService.class)){
            ServiceID posService=runtimeOperatingSystem.getServiceIDs(PositioningService.class)[0];
            runtimeOperatingSystem.registerAtService(posService,PositioningService.class);
        }
   
        try{
        	this.configClient = JSPBMConfigRemoteClient.getSingleton();
        }catch (Exception e){
        	System.err.println("-- error initializing JSPBM configuration client");
        	e.printStackTrace();
        }
        
        
    }
    
    public void updatePositioningData(PositioningData info) {
        //set Position in module!
        Position myPos = info.getPosition();
        long xPos = (long) (myPos.getX()* 100);
        long yPos = (long) (myPos.getY() * 100);
        try{
        	configClient.setPosition(xPos, yPos);
        }catch(Exception e){
        	System.err.println("-- Error while setting position in JSPBM daemon");
        	e.printStackTrace();
        }
    }

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	public void finish() {
		// leave all groups
		MulticastGroupID[] currentGroups= getJoinedGroups();
		if (currentGroups != null){
			
		}
	}

	public Shape getShape() {
		/*ignore*/
		return null;
	}

	public void getParameters(Parameters parameters) {/*ignore*/}



}
