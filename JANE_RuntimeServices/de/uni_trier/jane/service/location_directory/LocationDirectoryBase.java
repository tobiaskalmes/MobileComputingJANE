package de.uni_trier.jane.service.location_directory;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;

public abstract class LocationDirectoryBase implements LocationDirectoryService {

	private static final ServiceIDParameter LOCATION_DIRECTORY_ID = new ServiceIDParameter("");

	public static final ServiceID getInstance(ServiceUnit serviceUnit, InitializationContext initializationContext) {
    	ServiceID locationDirectoryID = LOCATION_DIRECTORY_ID.getValue(initializationContext);
    	if(locationDirectoryID == null) {
    		// TODO eine Billigimplementierung realisieren und darauf createInstance aufrufen
        	locationDirectoryID = serviceUnit.getService(LocationDirectoryService.class);
    	}
    	return locationDirectoryID;
	}
	
}
