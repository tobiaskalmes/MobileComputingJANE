package de.uni_trier.jane.simulation.parametrized.parameters.service;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class ServiceEnumeration extends EnumerationParameter implements ServiceCreator {

	public ServiceEnumeration(String key, int defaultIndex, ServiceElement[] elements) {
		this(key, defaultIndex, elements, "");
	}

	public ServiceEnumeration(String key, int defaultIndex, ServiceElement[] elements, String description) {
		super(key, defaultIndex, elements, description);
	}

	public void createInstanceInternal(InitializationContext initializationContext, ServiceUnit serviceUnit) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		ServiceElement element = (ServiceElement)getEnumerationElement(initializationContext);
		ici.push(getKey());
		element.createInstanceInternal(initializationContext, serviceUnit);
		ici.pop();
	}

}
