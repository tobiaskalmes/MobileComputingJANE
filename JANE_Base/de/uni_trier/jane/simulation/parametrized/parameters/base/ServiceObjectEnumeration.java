package de.uni_trier.jane.simulation.parametrized.parameters.base;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class ServiceObjectEnumeration extends EnumerationParameter {

	public ServiceObjectEnumeration(String key, int defaultIndex, ServiceObjectElement[] parameters) {
		this(key, defaultIndex, parameters, "");
	}

	public ServiceObjectEnumeration(String key, int defaultIndex, ServiceObjectElement[] parameters, String description) {
		super(key, defaultIndex, parameters, description);
	}

	public Object getValue(InitializationContext initializationContext, ServiceUnit serviceUnit) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		ServiceObjectElement objectParameter = (ServiceObjectElement)getEnumerationElement(initializationContext);
		ici.push(getKey());
		Object result = objectParameter.getValueInternal(initializationContext, serviceUnit);
		ici.pop();
		return result;
	}

}
