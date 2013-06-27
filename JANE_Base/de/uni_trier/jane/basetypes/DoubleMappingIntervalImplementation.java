/*
 * Created on 29.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.basetypes;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DoubleMappingIntervalImplementation implements DoubleMappingInterval {

	private double endTime;

	private double startTime;

	private DoubleMapping doubleMapping;

	/**
	 * 
	 */
	public DoubleMappingIntervalImplementation(DoubleMapping doubleMapping, double startTime, double endTime) {
		this.doubleMapping=doubleMapping;
		this.startTime=startTime;
		this.endTime=endTime;
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMappingInterval#getInfimum()
	 */
	public double getInfimum() {
		return startTime;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMappingInterval#getSupremum()
	 */
	public double getSupremum() {
		return endTime;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.basetype.DoubleMappingInterval#getValue(double)
	 */
	public double getValue(double time) {
		// TODO Auto-generated method stub
		return doubleMapping.getValue(time);
	}

	
}
