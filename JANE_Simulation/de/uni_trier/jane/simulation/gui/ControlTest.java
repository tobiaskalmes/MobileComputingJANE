/*
 * Created on 18.02.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.simulation.gui;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.*;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ControlTest extends Simulation {

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.Simulation#initSimulation(de.uni_trier.jane.simulation.SimulationParameters)
	 */
	public void initSimulation(SimulationParameters parameters) {
		DefaultSimulationFrameControl frameControl=new DefaultSimulationFrameControl(new ConsoleFrame(),new VisualizationFrame(),new XMLScreenshotRenderer());
		frameControl.show(parameters);
		parameters.setMobilitySource(RandomMobilitySource.createRandomWaypointMobilitySource(parameters.getDistributionCreator(),500,40000,500,500,60,25,25,1,1));

	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.kernel.SimulationServiceFactory#initGlobalServices(de.uni_trier.jane.service.unit.ServiceUnit)
	 */
	public void initGlobalServices(ServiceUnit serviceUnit) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.unit.ServiceFactory#initServices(de.uni_trier.jane.service.unit.ServiceUnit)
	 */
	public void initServices(ServiceUnit serviceUnit) {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args) {
		new ControlTest().run();
	}
	

}
