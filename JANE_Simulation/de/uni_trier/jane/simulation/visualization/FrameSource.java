/*****************************************************************************
 * 
 * FrameSource.java
 * 
 * $Id: FrameSource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.simulation.visualization;


import de.uni_trier.jane.basetypes.*;

/**
 * An implementor of this interface is passed to a frame renderer.
 */
public interface FrameSource {

	/**
	 * Get the next frame to render.
	 * @return the next frame
	 */
	public Frame getFrame();

	/**
	 * Get the current frame, do not move on to the next frame
	 * @return the current frame
	 */
	public Frame getCurrentFrame();
	
	/**
	 * Get the newest frame that was put to the queue
	 * @return newest Frame
	 */
	public Frame getNewestInsertedFrame();
	
	/**
	 * Set frame interval between two successive frames in seconds.
	 * @param the frame interval
	 */
	public void setFrameInterval(double frameInterval);

	/**
	 * Get the total extent of the frames.
	 * @return the extent
	 */
	public Rectangle getRectangle();

    /**
     * TODO Comment method
     * 
     */
    public void stopRender();

}
