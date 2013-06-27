package de.uni_trier.jane.simulation.parametrized.parameters.base;

import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class StringParameter extends Parameter {

	private String defaultValue;

	public StringParameter(String key, String defaultValue) {
		this(key, defaultValue, "");
	}
	
	public StringParameter(String key, String defaultValue, String description) {
		super(key, description);
		this.defaultValue = defaultValue;
	}

	public String getValue(InitializationContext initializationContext) {
		return initializationContext.getProperty(getKey(), defaultValue);
	}
	
	public String toString() {
		return getKey() + "=string(" + defaultValue + ")";
	}

}
