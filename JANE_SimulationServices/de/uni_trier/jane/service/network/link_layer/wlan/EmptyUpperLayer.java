package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.visualization.shapes.Shape;

public class EmptyUpperLayer implements RuntimeService, MacToUpperLayerInterface {

	



	public void start(RuntimeOperatingSystem operatingSystem) {
		
		
		//register interface for exporting as stub
		operatingSystem.registerSignalListener(MacToUpperLayerInterface.class);
		
    }

	public ServiceID getServiceID() {
		// TODO Auto-generated method stub
		return null;
	}

	public void finish() {
		// TODO Auto-generated method stub

	}

	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub

	}
	

	public void sendFailed(LinkLayerMessage message) {
		// TODO Auto-generated method stub
		
	}

	public void sendFinished(LinkLayerMessage message) {
		// TODO Auto-generated method stub
		
	}


	public void connectedDevices(DeviceIDSet connectedDeviceIDSet) {
 
	}

	public void receive(LinkLayerInfo info, LinkLayerMessage message) {
		
	}


}
