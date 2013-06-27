/*
 * Created on 28.06.2005
 * File: DeviceGroupsSimulation.java
 */
package de.uni_trier.jane.simulation.device_groups;

import de.uni_trier.jane.service.unit.ServiceFactory;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.DefaultSimulationParameters;
import de.uni_trier.jane.simulation.Simulation;
import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.kernel.ApplicationSimulation;

/**
 * Super class for all JANE Simulations with different device groups. Each device
 * group has its own mobility source and you can specify which services will be
 * started on a device group.
 * 
 * @author christian.hoff
 * @version 0.1, 
 */
public abstract class DeviceGroupsSimulation extends Simulation {
	DeviceGroupsMobilitySource mobilitySrc;
	
	/**
	 * Method for initializing the <code>MobilitySource</code>. The <code>DeviceGroupsSimulation</code>
	 * can only be used in combination with the <code>DeviceGroupsMobilitySource</code>.
	 * This method is called during the initialization process of the simulation.
	 * The returned <code>DeviceGroupsMobilitySource</code> is used as the
	 * <code>MobilitySource</code> for the <code>DeviceGroupsSimulation</code>.
	 * Note: This method is called before <code>Simulation.initSimulation</code>.
	 * 
	 * @param parameters The simulation parameters.
	 * @return The <code>DeviceGroupsMobilitySource</code> to use in combination
	 * with the <code>DeviceGroupsSimulation</code>.
	 */
	public abstract DeviceGroupsMobilitySource initMobilitySource(SimulationParameters parameters);
	
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.unit.ServiceFactory#initServices(de.uni_trier.jane.service.unit.ServiceUnit)
     */
    public void initServices(ServiceUnit serviceUnit) {
    	// forward the initServices request
        ServiceFactory sf = mobilitySrc.getServiceFactory(
        		serviceUnit.getDeviceID()
        );
        sf.initServices(serviceUnit);
    }

    /* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.Simulation#run()
	 */
	public void run() {
        DefaultSimulationParameters parameters = new DefaultSimulationParameters();
        
        mobilitySrc = initMobilitySource(parameters);
        parameters.setMobilitySource(mobilitySrc);
        initSimulation(parameters);
        

        ApplicationSimulation simulation = new ApplicationSimulation(parameters,this);
        simulation.run();
	}
}
