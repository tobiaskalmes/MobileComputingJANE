/*
 * Created on 03.07.2005
 */
package de.uni_trier.jane.visualization.shapes;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.Worldspace;

/**
 * @author Klaus Sausen
 *
 * this is a container for a shape moving from on device to another
 */
public class MovingShape implements Shape {

	/**
	 * the shape contained within this
	 */
	private Shape content;
	/**
	 * the sender (of e.g. a message)
	 */
	private Address senderDeviceID;
	/**
	 * the receiver
	 */
	private Address receiverDeviceID;
	/**
	 * the progress of the move [0.0 .. 1.0]
	 */
	double progress;

	private static final Shape DEFAULT_SHAPE 
		= new RectangleShape( new Extent(4,4), Color.GREY, true);
	
    /**
     * 
     * Constructor for class <code>MovingShape</code>
     *
     * @param content
     * @param senderDeviceID
     * @param receiverDeviceID
     * @param progress
     */
	public MovingShape(Shape content, Address senderDeviceID, Address receiverDeviceID, double progress) {
		if (content == null)
			this.content = DEFAULT_SHAPE;
		else
			this.content = content;
        
        this.senderDeviceID=senderDeviceID;
        this.receiverDeviceID=receiverDeviceID;
        this.progress=progress;
        
	}
	
	/** 
	 * calculate the position relative to the sender using the 
	 * progress information
	 * @see de.uni_trier.jane.visualization.shapes.Shape#visualize(de.uni_trier.jane.basetypes.Position, de.uni_trier.jane.visualization.Worldspace, de.uni_trier.jane.basetypes.DeviceIDPositionMap)
	 */
	public void visualize(Position position, Worldspace worldspace,
			DeviceIDPositionMap addressPositionMap) {

		//Matrix matrix = worldspace.getTransformation();
		Position sender_pos = addressPositionMap.getPosition(senderDeviceID);
		MutablePosition pos = new MutablePosition(addressPositionMap.getPosition(receiverDeviceID))
			.sub(sender_pos)
			.scale(progress)
			.add(sender_pos);

		//TODO make visualize use PositionBase *everywhere*
		content.visualize(new Position(pos), worldspace, addressPositionMap);
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.visualization.shapes.Shape#getRectangle(de.uni_trier.jane.basetypes.Position, de.uni_trier.jane.basetypes.Matrix)
	 */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		return content.getRectangle(position, matrix);
	}

	/**
	 * set the progress information for any moving shape contained in here
	 * @param senderID
	 * @param receiverID
	 * @param progress
	 */
//	public void setProgressInfo(DeviceID senderID, DeviceID receiverID,
//			double progress) {
//		this.senderDeviceID = senderID;
//		this.receiverDeviceID = receiverID;
//		this.progress = progress;
//	}

}
