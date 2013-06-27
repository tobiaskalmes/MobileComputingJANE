package de.uni_trier.jane.simulation.parametrized.parameters.base;

import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class DoubleParameter extends Parameter {

	private double defaultValue;

	public DoubleParameter(String key, double defaultValue) {
		this(key, defaultValue, "");
	}

	public DoubleParameter(String key, double defaultValue, String description) {
		super(key, description);
		this.defaultValue = defaultValue;
	}

	public double getValue(InitializationContext initializationContext) {
		String value = initializationContext.getProperty(getKey(), Double.toString(defaultValue));
		return Double.valueOf(value).doubleValue();
	}
	
	public String toString() {
		return getKey() + "=double(" + defaultValue + ")";
	}

}
