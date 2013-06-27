package de.uni_trier.jane.simulation.parametrized.parameters.base;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public abstract class ServiceObjectElement extends EnumerationElement {

	public ServiceObjectElement(String key) {
		this(key, "");
	}

	public ServiceObjectElement(String key, String description) {
		super(key, description);
	}

	public Object getValueInternal(InitializationContext initializationContext, ServiceUnit serviceUnit) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		ici.push(getKey());
		Object result = getValue(initializationContext, serviceUnit);
		ici.pop();
		return result;
	}
	
	protected abstract Object getValue(InitializationContext initializationContext, ServiceUnit serviceUnit);

}
