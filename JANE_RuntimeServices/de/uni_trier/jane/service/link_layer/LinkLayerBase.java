package de.uni_trier.jane.service.link_layer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;

public abstract class LinkLayerBase implements LinkLayer {

	public static final ServiceReference REQUIRED_SERVICE = new ServiceReference("linkLayer") {
		public ServiceID getServiceID(ServiceUnit serviceUnit) {
			return serviceUnit.getService(LinkLayer.class);
		}
	};

}
