package de.uni_trier.jane.simulation.parametrized.parameters.service;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public abstract class ServiceReference extends Parameter {

	public ServiceReference(String key) {
		this(key, "");
	}
	
	public ServiceReference(String key, String description) {
		super(key, description);
	}

	public ServiceID getInstance(ServiceUnit serviceUnit, InitializationContext initializationContext) {
		String idString = initializationContext.getProperty(getKey(), "");
		if(idString.length() == 0) {
			return getServiceID(serviceUnit);
		}
		return new EndpointClassID(idString);
	}

	protected abstract ServiceID getServiceID(ServiceUnit serviceUnit);

	public void printUsage(String prefix) {
		System.out.println(prefix + toString());
	}
	
	public String toString() {
		return getKey() + "=" + ".";
	}

}
