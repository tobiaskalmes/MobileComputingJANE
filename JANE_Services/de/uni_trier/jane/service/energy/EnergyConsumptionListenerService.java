/*
 * Created on 19.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.energy;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface EnergyConsumptionListenerService {
	
	public void setCurrentEnergyConsumption(double watt);

	public void reduceEnergy(double joule);

}
