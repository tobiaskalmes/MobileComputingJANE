package de.uni_trier.jane.simulation.parametrized.parameters.service;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public abstract class ServiceElement extends EnumerationElement implements ServiceCreator {

	public ServiceElement(String key) {
		this(key, "");
	}

	public ServiceElement(String key, String description) {
		super(key, description);
	}

	public void createInstanceInternal(InitializationContext initializationContext, ServiceUnit serviceUnit) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		ici.push(getKey());
		createInstance(initializationContext, serviceUnit);
		ici.pop();
	}
	
	public abstract void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit);

}
