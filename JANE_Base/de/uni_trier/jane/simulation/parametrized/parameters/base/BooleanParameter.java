package de.uni_trier.jane.simulation.parametrized.parameters.base;

import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class BooleanParameter extends Parameter {

	private boolean defaultValue;

	public BooleanParameter(String key, boolean defaultValue) {
		this(key, defaultValue, "");
	}

	public BooleanParameter(String key, boolean defaultValue, String description) {
		super(key, description);
		this.defaultValue = defaultValue;
	}

	public boolean getValue(InitializationContext initializationContext) {
		String value = initializationContext.getProperty(getKey(), Boolean.toString(defaultValue));
		return Boolean.valueOf(value).booleanValue();
	}

	public String toString() {
		return getKey() + "=boolean(" + defaultValue + ")";
	}

}
