package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;

public abstract class BeaconingInitializer extends IdentifiedServiceElement {

	protected static final DoubleParameter BEACONING_DELTA = new DoubleParameter("beaconingDelta", 1.0);
	protected static final DoubleParameter BEACONING_JITTER = new DoubleParameter("beaconingJitter", 0.5);
	protected static final DoubleParameter CLEANUP_DELTA = new DoubleParameter("cleanupDelta", 1.0);
	protected static final DoubleParameter EXPIRATION_DELTA = new DoubleParameter("expirationDelta", 3.0);
	protected static final DoubleParameter BEACONING_DURATION = new DoubleParameter("beaconingDuration", 10.0);
	protected static final BooleanParameter CUMMULATIVE = new BooleanParameter("cummulative", true,
			"If true, once a neighbor is inserted it will never be removed.");
	
	public BeaconingInitializer(String key) {
		super(key);
		// TODO Auto-generated constructor stub
	}
	
	public BeaconingInitializer(String key, String description) {
		super(key, description);
		// TODO Auto-generated constructor stub
	}

}
