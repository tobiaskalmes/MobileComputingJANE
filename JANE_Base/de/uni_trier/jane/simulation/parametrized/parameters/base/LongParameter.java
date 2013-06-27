package de.uni_trier.jane.simulation.parametrized.parameters.base;

import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class LongParameter extends Parameter {

	private long defaultValue;

	public LongParameter(String key, long defaultValue) {
		this(key, defaultValue, "");
	}
	
	public LongParameter(String key, long defaultValue, String description) {
		super(key, description);
		this.defaultValue = defaultValue;
	}

	public long getValue(InitializationContext initializationContext) {
		String value = initializationContext.getProperty(getKey(), Long.toString(defaultValue));
		return Long.valueOf(value).longValue();
	}
	
	public String toString() {
		return getKey() + "=long(" + defaultValue + ")";
	}

}
