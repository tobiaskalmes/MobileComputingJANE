/*****************************************************************************
 * 
 * VisualizationEvent.java
 * 
 * $Id: VisualizationEvent.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.simulation.kernel.eventset.*;

/**
 * This event is used to trigger the next simulation visualisation.
 */
public class VisualizationEvent extends Event {

	private final static String VERSION = "$Id: VisualizationEvent.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private VisualisationEventListener listener;
	
	/**
	 * Construct a new visualization event.
	 * @param time the time of this event
	 * @param listener the listener for this event
	 */
	public VisualizationEvent(double time, VisualisationEventListener listener) {
		super(time);
		this.listener = listener;
	}

	protected void handleInternal() {
		listener.handleVisualize();	
	}

}
