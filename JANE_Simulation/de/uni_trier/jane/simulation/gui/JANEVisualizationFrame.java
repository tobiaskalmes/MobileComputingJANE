/*
 * Created on 18.02.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.simulation.gui;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface JANEVisualizationFrame extends JANEFrame {
	
	public void addShape(Shape shape, DeviceIDPositionMap deviceIDPositionMap);

	/**
	 * @param rectangle
	 */
	public void setRectangle(Rectangle rectangle);

}
