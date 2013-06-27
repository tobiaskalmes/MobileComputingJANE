package de.uni_trier.jane.simulation.dynamic.mobility_source.campus;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.DeviceIDSet;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.ArrivalInfo;
import de.uni_trier.jane.visualization.shapes.EmptyShape;
import de.uni_trier.jane.visualization.shapes.Shape;

public class FixedPositionLocation implements DeviceLocation {
	private Position[] positions;
	private Rectangle rectangle;
	
	
	
	

	public FixedPositionLocation(Position[] positions, Rectangle rectangle) {
		super();
		// TODO Auto-generated constructor stub
		this.positions = positions;
		this.rectangle = rectangle;
		for (int i=0;i<positions.length;i++){
			if (!rectangle.contains(positions[i])){
				throw new IllegalStateException("The given positions must be inside the given rectangle");
			}
		}
	}

	public Position[] getDevicePath(DeviceID device, Position newPosition,
			Position oldPosition) {
		return new Position[]{newPosition};
		
	}

	public void removeDevice(DeviceID deviceID, Position oldPosition) {
		// ignore

	}

	public ArrivalInfo[] getInitialArrivalInfos(DeviceID[] deviceIDs,
			double[] enterTimes) {
		if (deviceIDs.length> positions.length){
            throw new IllegalStateException("Not enough initial positions given!");
		}
		
		ArrivalInfo[] arrivalInfos=new ArrivalInfo[deviceIDs.length];
		for (int i=0;i<deviceIDs.length;i++){
			arrivalInfos[i]=new ArrivalInfo(positions[i],enterTimes[i]);
		}
		return arrivalInfos;
	}

	public DeviceIDSet getAddress(Rectangle rectangle) {
		
		return null;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public Shape getShape() {
		return EmptyShape.getInstance();
	}

}
