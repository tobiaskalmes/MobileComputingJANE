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
public class ClickAndPlayPathNetMobilitySource implements MobilitySource, ClickAndPlayMobilitySource {
	
	private Campus campus;
	
	private ContinuousDistribution speedDistribution;
	
	private ContinuousDistribution sendingRadiusDistribution;
	
	
	
	private int numberDevices;
	private int count;

	private HashMap deviceMap;

    private double movementSteps;

    private HashMap eventProviderMap;

    private PositionDeviceMap positionMap;

    private LocationSelect locationSelect;

	
	
	/**
     * 
     * Constructor for class <code>ClickAndPlayPathNetMobilitySource</code>
     * @param campus
     * @param numberDevices
     * @param speedDistribution
     * @param sendingRadiusDistribution
     * @param locationSelect
     * @param movementSteps
	 */
	public ClickAndPlayPathNetMobilitySource(Campus campus,
				int numberDevices,
				ContinuousDistribution speedDistribution,
				ContinuousDistribution sendingRadiusDistribution, 
				
				LocationSelect locationSelect,
				double movementSteps){
		
		this.campus=campus;
		this.speedDistribution=speedDistribution;
		this.movementSteps=movementSteps;
		this.numberDevices=numberDevices;
		this.sendingRadiusDistribution=sendingRadiusDistribution;
		this.locationSelect=locationSelect;
		count=1;
		deviceMap=new HashMap();
		eventProviderMap=new HashMap();
		positionMap=new PositionDeviceMap();
		
		
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
		ClickAndPlayEventProvider eventProvider = new ClickAndPlayEventProvider(campus,movementSteps,new DeviceTarget(locationSelect.next(),false));
		DeviceTimeTable timeTable=new DeviceTimeTable(new MobileDeviceParameter(address,address.toString(),
				sendingRadiusDistribution.getNext(),
				speedDistribution),eventProvider);		
		deviceMap.put(address,timeTable);
		eventProviderMap.put(address,eventProvider);
		try {
			EnterInfo enterInfo=timeTable.getEnterInfo(campus);
			positionMap.addDevice(enterInfo.getAddress(),enterInfo.getArrivalInfo().getPosition());
			return enterInfo;
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
			ArrivalInfo arrivalInfo=((DeviceTimeTable)deviceMap.get(address)).getNextArrival(campus);
			positionMap.removeDevice(address);
			positionMap.addDevice(address,arrivalInfo.getPosition());
			
			return arrivalInfo;
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

    public void setPosition(DeviceID device, Position newPosition) {
        ClickAndPlayEventProvider eventProvider=(ClickAndPlayEventProvider)eventProviderMap.get(device);
        eventProvider.setPosition(newPosition);
        
    }

    public DeviceID[] getAddress(Rectangle rectangle) {

        return positionMap.getDevices(rectangle);
    }

	public double getMinimumTransmissionRange() {
		return sendingRadiusDistribution.getInfimum();
	}

	public double getMaximumTransmissionRange() {
		return sendingRadiusDistribution.getSupremum();
	}
}
