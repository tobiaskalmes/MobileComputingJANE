package de.uni_trier.jane.simulation.parametrized.parameters.base;

import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class IntegerListParameter extends Parameter {

	private int[] defaultValues;

	public IntegerListParameter(String key) {
		this(key, new int[0]);
	}

	public IntegerListParameter(String key, int[] defaultValues) {
		this(key, defaultValues, "");
	}

	public IntegerListParameter(String key, String description) {
		this(key, new int[0], description);
	}

	public IntegerListParameter(String key, int[] defaultValues, String description) {
		super(key, description);
		this.defaultValues = defaultValues;
	}

	public int[] getValues(InitializationContext initializationContext) {
		String value = initializationContext.getProperty(getKey(), "");
		if(value.length() == 0) {
			return defaultValues;
		}
		String[] values = value.split(",");
		int[] result = new int[values.length];
		for(int i=0; i<values.length; i++) {
			result[i] = Integer.valueOf(values[i].trim()).intValue();
		}
		return result;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<defaultValues.length; i++) {
			buffer.append(defaultValues[i]);
			if(i<defaultValues.length-1) {
				buffer.append(",");
			}
		}
		return getKey() + "=int[](" + buffer.toString() + ")";
	}

}
