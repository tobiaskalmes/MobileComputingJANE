/*
 * Created on 22.04.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.random;

import de.uni_trier.jane.basetypes.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ConstantDoubleIntervalMapping implements DoubleIntervalMapping {

	private DoubleInterval interval;


	public ConstantDoubleIntervalMapping(DoubleInterval doubleInterval){
		interval=doubleInterval;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleIntervalMapping#getInfimum()
	 */
	public DoubleInterval getInfimum() {
		
		return interval;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleIntervalMapping#getSupremum()
	 */
	public DoubleInterval getSupremum() {
		
		return interval;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleIntervalMapping#getValue(double)
	 */
	public DoubleInterval getValue(double time) {
		
		return interval;
	}

}
