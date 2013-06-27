package de.uni_trier.jane.simulation.parametrized.parameters;


public abstract class EnumerationElement extends Parameter {

	public EnumerationElement(String key, String description) {
		super(key, description);
	}

	public Parameter[] getParameters() {
		return null;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getKey());
		Parameter[] parameters = getParameters();
		if(parameters != null) {
			buffer.append("(");
			int len = parameters.length;
			for(int i=0; i<len; i++) {
				buffer.append(parameters[i].getKey());
				if(i < len - 1) {
					buffer.append(",");
				}
			}
			buffer.append(")");
		}
		return buffer.toString();
	}
	
	public void printUsage(String prefix) {
		Parameter[] pars = getParameters();
		if(pars != null) {
			int maxj = pars.length;
			for(int j=0; j<maxj; j++) {
				Parameter par = pars[j];
				par.printUsage(prefix + getKey() + ".");
			}
		}
	}
	
}
