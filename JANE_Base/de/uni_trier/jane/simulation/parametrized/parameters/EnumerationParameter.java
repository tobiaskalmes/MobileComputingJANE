package de.uni_trier.jane.simulation.parametrized.parameters;

public abstract class EnumerationParameter extends Parameter {

	private int defaultIndex;
	private EnumerationElement[] parameters;
	
	
	public EnumerationParameter(String key, int defaultIndex, EnumerationElement[] parameters, String description) {
		super(key, description);
		this.defaultIndex = defaultIndex;
		this.parameters = parameters;
	}

	public EnumerationElement getEnumerationElement(InitializationContext initializationContext) {
		return parameters[getIndex(initializationContext)];
	}

	public int getIndex(InitializationContext initializationContext) {
		String value = initializationContext.getProperty(getKey(), parameters[defaultIndex].getKey());
		int len = parameters.length;
		for(int i=0; i< len; i++) {
			String name = parameters[i].getKey();
			if(name.equalsIgnoreCase(value)) {
				return i;
			}
		}
		return defaultIndex;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getKey());
		buffer.append("=[");
		int len = parameters.length;
		for(int i=0; i<len; i++) {
			if(i == defaultIndex) {
				buffer.append("*");
			}
			buffer.append(parameters[i].toString());
			if(i < len - 1) {
				buffer.append("|");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	public void printUsage(String prefix) {
		System.out.println(prefix + toString());
		int maxi = parameters.length;
		for(int i=0; i<maxi; i++) {
			Parameter value = parameters[i];
			value.printUsage("  " + prefix + getKey() + ".");
		}
	}

}
