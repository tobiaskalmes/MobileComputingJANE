package de.uni_trier.jane.simulation.parametrized.parameters.object;

import java.util.*;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class InitializationObjectList extends ListParameter {

	public InitializationObjectList(String key, InitializationObjectElement[] parameters) {
		this(key, parameters, "");
	}

	public InitializationObjectList(String key, InitializationObjectElement[] parameters, String description) {
		super(key, parameters, description);
	}

	public List getValues(InitializationContext initializationContext, SimulationParameters simulationParameters) {
		List result = new ArrayList();
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		EnumerationElement[] elements = getEnumerationElements(initializationContext);
		for(int i=0; i<elements.length; i++) {
			ici.push(getKey() + "-" + (i+1));
			InitializationObjectElement element = (InitializationObjectElement)elements[i];
			Object obj = element.getValueInternal(initializationContext, simulationParameters);
			result.add(obj);
			ici.pop();
		}
		return result;
	}

}
