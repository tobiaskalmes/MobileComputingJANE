package de.uni_trier.jane.simulation.dynamic.mobility_source.campus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.DeviceIDSet;
import de.uni_trier.jane.basetypes.MutableDeviceIDSet;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.ArrivalInfo;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;

public class DeviceLocationCollection implements DeviceLocation {
	
	private DeviceLocation[] deviceLocations;
	private int[] devices;
	private DeviceLocation superLocation;
	
	

	public DeviceLocationCollection(DeviceLocation[] locations,int[] devices, DeviceLocation superLocation) {
		super();
		this.devices=devices;
		deviceLocations = locations;
		this.superLocation=superLocation;
		for (int i=0;i<locations.length;i++){
			if (!superLocation.getRectangle().contains(locations[i].getRectangle())){
				throw new IllegalStateException("super location must contain all other locations");
			}
		}
	}

	public Position[] getDevicePath(DeviceID device, Position newPosition,Position oldPosition) {
		Position[] positions=null;
		for (int i=0;i<deviceLocations.length;i++){
			if (deviceLocations[i].getRectangle().contains(newPosition)){
				positions=deviceLocations[i].getDevicePath(device,newPosition,oldPosition);
			}else if (deviceLocations[i].getRectangle().contains(oldPosition)){
				deviceLocations[i].removeDevice(device,oldPosition);
				
			}
		}
		if (positions==null){
			positions=superLocation.getDevicePath(device,newPosition,oldPosition);
		}
		return positions;
	}

	public void removeDevice(DeviceID deviceID,Position oldPosition) {
		for (int i=0;i<deviceLocations.length;i++){
			deviceLocations[i].removeDevice(deviceID,oldPosition);
		}

	}

	public ArrivalInfo[] getInitialArrivalInfos(DeviceID[] deviceIDs,
			double[] enterTimes) {
		List list=new ArrayList();
		int tmp=0;
		for (int i=0;i<deviceLocations.length;i++){
			DeviceID[] current=new DeviceID[devices[i]];
			double[] currentTimes=new double[devices[i]];
			for (int j=0;j<devices[i];j++){
				current[j]=deviceIDs[j+tmp];
				currentTimes[j]=enterTimes[j+tmp];
			}
			list.addAll(Arrays.asList(deviceLocations[i].getInitialArrivalInfos(current,currentTimes)));
			tmp+=devices[i];
		}
		if (tmp<deviceIDs.length){
			int rest=deviceIDs.length-tmp;
			DeviceID[] current=new DeviceID[rest];
			double[] currentTimes=new double[rest];
			for (int j=0;j<rest;j++){
				current[j]=deviceIDs[j+tmp];
				currentTimes[j]=enterTimes[j+tmp];
			}
			list.addAll(Arrays.asList(superLocation.getInitialArrivalInfos(current,currentTimes)));
		}
		
		return (ArrivalInfo[])list.toArray(new ArrivalInfo[list.size()]);
	}

//	public DeviceIDSet getAddress(Rectangle rectangle) {
//		MutableDeviceIDSet set=new MutableDeviceIDSet();
//		for (int i=0;i<deviceLocations.length;i++){
//			if (rectangle.intersects(deviceLocations[i].getRectangle())){
//				set.addAll(deviceLocations[i].getAddress(rectangle));
//			}
//		}
//		return set;
//	}

	public Rectangle getRectangle() {
		return superLocation.getRectangle();
	}

	public Shape getShape() {
		ShapeCollection collection=new ShapeCollection();
		collection.addShape(superLocation.getShape());
		for (int i=0;i<deviceLocations.length;i++){
			collection.addShape(deviceLocations[i].getShape());
		}
		return collection;
	}

}
