package de.uni_trier.jane.simulation.parametrized.parameters.base;

import java.util.*;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class ServiceObjectList extends ListParameter {

	public ServiceObjectList(String key, ServiceObjectElement[] parameters) {
		this(key, parameters, "");
	}

	public ServiceObjectList(String key, ServiceObjectElement[] parameters, String description) {
		super(key, parameters, description);
	}

	public List getValues(InitializationContext initializationContext, ServiceUnit serviceUnit) {
		List result = new ArrayList();
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		EnumerationElement[] elements = getEnumerationElements(initializationContext);
		for(int i=0; i<elements.length; i++) {
			ici.push(getKey() + "-" + (i+1));
			ServiceObjectElement element = (ServiceObjectElement)elements[i];
			Object obj = element.getValueInternal(initializationContext, serviceUnit);
			result.add(obj);
			ici.pop();
		}
		return result;
	}

}
