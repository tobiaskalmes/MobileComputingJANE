package de.uni_trier.jane.simulation.parametrized;

import java.io.*;
import java.util.*;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.initialization.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;

public abstract class ParametrizedSimulation extends Simulation {

	private InitializationContextImpl initializationContext;

	public ParametrizedSimulation(String[] args) {
		
		// read global properties file
		Properties properties = new Properties();
		String fileNameList = System.getProperty("simulation.properties");
		
		if(fileNameList != null) {
			
			String[] fileNames = fileNameList.split(",");
			
			for(int i=0; i<fileNames.length; i++) {
				try {
					properties.load(new FileInputStream(fileNames[i]));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}

//		//
//		if(properties.containsKey("globalProperties")) {
//			String globalPropertiesName = properties.getProperty("globalProperties");
//			try {
//				properties.load(new FileInputStream(globalPropertiesName));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		
		// read additional command line properties
		int len = args.length;
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<len; i++) {
			buffer.append(args[i]);
			buffer.append("\n");
		}
		ByteArrayInputStream stream = new ByteArrayInputStream(buffer.toString().getBytes());
		try {
			properties.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create the initialization context
		initializationContext = new InitializationContextImpl(properties);
		
	}
	
	public void initSimulation(SimulationParameters parameters) {
		getInitSimulationSequence().initSimulation(initializationContext, parameters);
	}

	public void initGlobalServices(ServiceUnit serviceUnit) {
		getInitGlobalServiceSequence().initialize(initializationContext, serviceUnit);
	}

	public void initServices(ServiceUnit serviceUnit) {
		getInitServiceSequence().initialize(initializationContext, serviceUnit);
	}

	public void printUsage() {
		getInitSimulationSequence().printUsage();
		getInitGlobalServiceSequence().printUsage();
		getInitServiceSequence().printUsage();
	}
	
	public InitializationContextImpl getInitializationContext() {
		return initializationContext;
	}

	public abstract SimulationInitializerSequence getInitSimulationSequence();
	public abstract ServiceCreatorSequence getInitGlobalServiceSequence();
	public abstract ServiceCreatorSequence getInitServiceSequence();

}
