package de.uni_trier.jane.service.neighbor_discovery.dissemination;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.neighbor_discovery.*;

/**
 * Use this data type in order to disseminate the remaining energy on this device.
 */
public class EnergyStatusData implements Data {

	public static final DataID DATA_ID = new ClassDataID(EnergyStatusData.class);

	public static final double getRemainingJoule(NeighborDiscoveryData neighbor) {
		EnergyStatusData energyStatusData = (EnergyStatusData)neighbor.getDataMap().getData(DATA_ID);
		if(energyStatusData == null) {
			return 0.0;
		}
		return energyStatusData.getRemainingJoule();
	}
	
	private double remainingJoule;
	
	/**
	 * @param remainingJoule the remaining energy in joule.
	 */
	public EnergyStatusData(double remainingJoule) {
		this.remainingJoule = remainingJoule;
	}
	
	public DataID getDataID() {
		return DATA_ID;
	}

	public Data copy() {
		return this;
	}

	public int getSize() {
		return 32;
	}

	public double getRemainingJoule() {
		return remainingJoule;
	}
	
}
