package de.uni_trier.jane.simulation.service;

import de.uni_trier.jane.service.*;

/**
 * This specialized type of a service is used in order to simulate services which use
 * global information provided by the simulation. Only use this feature if a realistic
 * implemetation of the service is not needed or possible at the moment.
 */
public interface SimulationService extends Service {

    /**
     * The service collection will tell the implementor of this interface about global
     * knowledge provided by the simulation.
     * @param operatingService the operating system providing standard device information
     * @param globalKnowledge the information about the network graph and device position
     * @param simulationClock the precise simulation clock
     */
    public void start(SimulationOperatingSystem simulationOperatingSystem);

}
