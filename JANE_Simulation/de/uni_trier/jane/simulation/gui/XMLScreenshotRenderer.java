/*
 * Created on 18.02.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.simulation.gui;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.visualization.*;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class XMLScreenshotRenderer implements OutputFrameRenderer {

	private String filename="screenshot";
	

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.gui.OutputFrameRenderer#renderFrame(de.uni_trier.jane.simulation.visualization.Frame)
	 * FIXME use worldspace instead! (not visualize canvas... is old)
	 */
	public void renderFrame(Frame frame) {
		XMLRenderCanvas canvas=new XMLRenderCanvas(filename+(int)(frame.getTime()*10.0)+".xml");
		DefaultWorldspace worldspace=new DefaultWorldspace(canvas);
		canvas.beginRendering();
		frame.getShape().visualize(Position.NULL_POSITION,worldspace,frame.getAddressPositionMap());
		canvas.endRendering();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.gui.OutputFrameRenderer#setFilename(java.lang.String)
	 */
	public void setFilename(String filename) {
		this.filename=filename;
		
	}

	

}
