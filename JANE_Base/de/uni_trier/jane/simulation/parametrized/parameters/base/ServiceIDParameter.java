package de.uni_trier.jane.simulation.parametrized.parameters.base;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class ServiceIDParameter extends Parameter {

	private String defaultValue;

	public ServiceIDParameter(String key) {
		this(key, "");
	}

	public ServiceIDParameter(String key, String defaultValue) {
		this(key, defaultValue, "");
	}
	
	public ServiceIDParameter(String key, String defaultValue, String description) {
		super(key, description);
		this.defaultValue = defaultValue;
	}

	public ServiceID getValue(InitializationContext initializationContext) {
		String idString = initializationContext.getProperty(getKey(), defaultValue);
		if(idString.length() == 0) {
			return null;
		}
		return new EndpointClassID(idString);
	}
	
	public String toString() {
		return getKey() + "=string(" + defaultValue + ")";
	}

}
