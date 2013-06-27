/*
 * Created on May 11, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RandomPathNetMobilitySource implements MobilitySource {
	
	private Campus campus;
	
	private ContinuousDistribution speedDistribution;
	private ContinuousDistribution pauseDistribution;
	private ContinuousDistribution sendingRadiusDistribution;
	
	private int numberDevices;
	private int count;

	private HashMap deviceMap;

	private LocationSelect locationSelect;
	
	/**
	 * 
	 * @param campus
	 * @param numberDevices
	 * @param sendingRadiusDistribution
	 * @param speedDistribution
	 * @param pauseDistribution
	 * @param distributionCreator
	 */
	public RandomPathNetMobilitySource(Campus campus,
				int numberDevices, 
				ContinuousDistribution sendingRadiusDistribution, 
				ContinuousDistribution speedDistribution,
				ContinuousDistribution pauseDistribution,
				DistributionCreator distributionCreator){
		
		this.campus=campus;
		this.speedDistribution=speedDistribution;
		this.pauseDistribution=pauseDistribution;
		this.numberDevices=numberDevices;
		this.sendingRadiusDistribution=sendingRadiusDistribution;
		count=1;
		deviceMap=new HashMap();
		locationSelect=new LocationSelect(campus,distributionCreator);
	}
	
    /**
     * 
     * Constructor for class <code>RandomPathNetMobilitySource</code>
     *
     * @param campus
     * @param numberOfDevices
     * @param minSendingRadius
     * @param maxSendingRadius
     * @param minSpeed
     * @param maxSpeed
     * @param pauseTime
     * @param distributionCreator
     */
    public RandomPathNetMobilitySource(Campus campus, int numberOfDevices, double minSendingRadius,double maxSendingRadius,
            double minSpeed, double maxSpeed, double pauseTime,DistributionCreator distributionCreator) {
        this(campus,
                numberOfDevices,
                distributionCreator.getContinuousUniformDistribution(minSendingRadius,maxSendingRadius),
                distributionCreator.getContinuousUniformDistribution(minSpeed,maxSpeed),
                distributionCreator.getExponentialDistribution(1/pauseTime),
                distributionCreator);

    }
    
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#hasNextEnterInfo()
	 */
	public boolean hasNextEnterInfo() {
		
		return count<numberDevices;
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getNextEnterInfo()
	 */
	public EnterInfo getNextEnterInfo() {
		DeviceID address=new SimulationDeviceID(count);
		count++;
		
		DeviceTimeTable timeTable=new DeviceTimeTable(new MobileDeviceParameter(address,address.toString(),
															sendingRadiusDistribution.getNext(),
															speedDistribution),
														new RandomMoveEventProvider(locationSelect,pauseDistribution));
													
//						locationSelect,
//						pauseDistribution,
//						);
		
		deviceMap.put(address,timeTable);
		try {
			return timeTable.getEnterInfo(campus);
		} catch (UnknownLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(e.getLocalizedMessage());
		}
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#hasNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public boolean hasNextArrivalInfo(DeviceID address) {
		
		return true;
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		
		try {
			return ((DeviceTimeTable)deviceMap.get(address)).getNextArrival(campus);
		} catch (UnknownLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(e.getLocalizedMessage());
		}
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getRectangle()
	 */
	public Rectangle getRectangle() {
		
		return campus.getRectangle();
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getShape()
	 */
	public Shape getShape() {
		return campus.getShape();
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.MobilitySource#getTotalDeviceCount()
	 */
	public int getTotalDeviceCount() {
		return numberDevices;
	}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getTerminalCondition(de.uni_trier.jane.basetypes.Clock)
     */
    public Condition getTerminalCondition(Clock clock) {
        // TODO Auto-generated method stub
        return null;
    }

	public double getMinimumTransmissionRange() {
		return sendingRadiusDistribution.getInfimum();
	}

	public double getMaximumTransmissionRange() {
		return sendingRadiusDistribution.getSupremum();
	}
	
}
