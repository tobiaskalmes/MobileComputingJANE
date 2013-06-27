package de.uni_trier.jane.service.operatingSystem;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;


/**
 * This interface describes the "operating system methods" provided to each service.
 * An object of this type is passed to each service before it is started.
 */
//TODO: throws in Javadoc Kommentar!!!

public interface RuntimeOperatingSystem extends RuntimeEnvironment,Clock {

	/**
	 * Get the current time. Note, an elaborate operation service implemetation may provide
	 * an imprecise time value.
	 * @return the current time
	 */
	public double getTime();

	/**
	 * Sets the current time 
	 * Note, this does not change the real simulation time, only the device local time
	 * @param time
	 */
	public void setTime(double time);

	
	//public double getEnergy();
	/**
	 * Finishes all running services on this operating system
	 * Currently does not finish the simulated device!
	 */
	public void shutdown();
	
	/**
	 * Finishes all running services on this operating system and restarts all bootup services
	 */
	public void reboot();

    


}
