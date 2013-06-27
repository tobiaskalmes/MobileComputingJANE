/*
 * Created on 03.07.2005
 */
package de.uni_trier.jane.visualization.shapes;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.DeviceIDPositionMap;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Matrix;
import de.uni_trier.jane.basetypes.MutablePosition;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * @author Klaus Sausen
 */
public class TetraShape implements Shape {

	DeviceID address;
	double radius;
	Color color;

	public TetraShape(double radius, Color color) {
		this.address = null;
		this.radius = radius;
		this.color = color;
	}

	public TetraShape(DeviceID address, double radius, Color color) {
		this.address = address;
		this.radius = radius;
		this.color = color;
	}
	
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.visualization.shapes.Shape#visualize(de.uni_trier.jane.basetypes.Position, de.uni_trier.jane.visualization.Worldspace, de.uni_trier.jane.basetypes.DeviceIDPositionMap)
	 */
	public void visualize(Position position, Worldspace worldspace,
							DeviceIDPositionMap addressPositionMap) {
		if (address==null)			//the absolute case
			worldspace.drawTetrahedron(
					position,
					radius, 
					color
				);
		else						//the case relative to deviceID
			worldspace.drawTetrahedron(
					new MutablePosition(addressPositionMap.getPosition(address))
						.add(position), 
					radius, 
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
