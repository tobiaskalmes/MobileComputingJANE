package de.uni_trier.jane.simulation.parametrized.parameters;


public interface InitializationContext {

	public String getProperty(String key, String defaultValue);
	public boolean isVerbose();
	public void setVerbose(boolean verbose);

}
