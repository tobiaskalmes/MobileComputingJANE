/*
 * Created on 12.05.2005
 */
package de.uni_trier.jane.visualization.shapes;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.DeviceIDPositionMap;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Matrix;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.visualization.Canvas;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * @author Klaus Sausen
 * useful for visualizing vector/tension fields
 * polar coordinates
 */
public class VectorTensionShape implements Shape {

	private final static String VERSION = "$Id: VectorTensionShape.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private Address fromDevice;
	private double angle;
	private double length;
	private Color color;
	private double headLength;
		
	/**
	 * Construct a line shape.
	 * @param from start position
	 * @param angle in interval [0..1]
	 * @param length not yet relative to Canvas size
	 */
	public VectorTensionShape(DeviceID from, double angle, double length, Color color) {
		this.fromDevice = from;
		this.angle = angle;
		this.length = length;
		this.color = color;
	}

	/**
	 * TODO make length relative to canvas (apply matrix transform)
	 */
    public void visualize(Position position, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		Matrix matrix = worldspace.getTransformation();
		Position from = addressPositionMap.getPosition(fromDevice);
        Position to = new Position(
        				from.getX() + length * Math.sin(2.0*Math.PI*angle),
						from.getY() + length * Math.cos(2.0*Math.PI*angle),
						from.getZ())
					.transform(matrix);
        //FIXME
        worldspace
        	.getCanvas()
        	.drawLine(from.transform(matrix).add(position), to.add(position), color, 1);
    }

    /**
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
	    return new Rectangle(0,0,0,0); // TODO ignored at the moment!!!
		//return new Rectangle(addressPositionMap.getPosition(fromDevice).add(position),to.add(position));
	}
}
