/*
 * Created on 03.07.2005
 */
package de.uni_trier.jane.visualization.shapes;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.DeviceIDPositionMap;
import de.uni_trier.jane.basetypes.Matrix;
import de.uni_trier.jane.basetypes.MutablePosition;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * @author Klaus Sausen
 */
public class DiamondShape implements Shape {

	DeviceID address;
	double edgelen;
	Color color;

	public DiamondShape(double edgelen, Color color) {
		this.address = null;
		this.edgelen = edgelen;
		this.color = color;
	}

	public DiamondShape(DeviceID address, double edgelen, Color color) {
		this.address = address;
		this.edgelen = edgelen;
		this.color = color;
	}
	
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.visualization.shapes.Shape#visualize(de.uni_trier.jane.basetypes.Position, de.uni_trier.jane.visualization.Worldspace, de.uni_trier.jane.basetypes.DeviceIDPositionMap)
	 */
	public void visualize(Position position, Worldspace worldspace,
							DeviceIDPositionMap addressPositionMap) {
		if (address==null)			//the absolute case
			worldspace.drawDiamond(
					position,
					edgelen, 
					color
				);
		else						//the case relative to deviceID
			worldspace.drawDiamond(
					new MutablePosition(addressPositionMap.getPosition(address))
						.add(position), 
					edgelen, 
					color
				);
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.visualization.shapes.Shape#getRectangle(de.uni_trier.jane.basetypes.Position, de.uni_trier.jane.basetypes.Matrix)
	 */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		return Rectangle.NULL_RECTANGLE;
	}
}
