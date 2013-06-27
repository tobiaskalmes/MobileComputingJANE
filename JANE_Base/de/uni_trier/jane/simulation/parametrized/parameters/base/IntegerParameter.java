package de.uni_trier.jane.simulation.parametrized.parameters.base;

import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class IntegerParameter extends Parameter {

	private int defaultValue;

	public IntegerParameter(String key, int defaultValue) {
		this(key, defaultValue, "");
	}
	
	public IntegerParameter(String key, int defaultValue, String description) {
		super(key, description);
		this.defaultValue = defaultValue;
	}

	public int getValue(InitializationContext initializationContext) {
		String value = initializationContext.getProperty(getKey(), Integer.toString(defaultValue));
		return Integer.valueOf(value).intValue();
	}
	
	public String toString() {
		return getKey() + "=int(" + defaultValue + ")";
	}

}
