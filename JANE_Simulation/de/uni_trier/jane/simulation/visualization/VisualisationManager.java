/*****************************************************************************
 * 
 * VisualisationManager.java
 * 
 * $Id: VisualisationManager.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.device.*;
import de.uni_trier.jane.simulation.dynamic.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulationl.visualization.console.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * The visualization manager handles the visualization event. It combines the
 * shapes of all visualizable simulation components and all buffered text output
 * in one simulation frame. The frame is passed to a frame renderer.
 */
public class VisualisationManager implements VisualisationEventListener {

	private final static String VERSION = "$Id: VisualisationManager.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private EventSet eventSet;
	private DeviceManager deviceManager;
	//private NetworkRenderer networkRenderer;
	private FrameRenderer frameRenderer;
	private ConsoleTextBuffer consoleTextBuffer;
	
	private DynamicSource dynamicSource;
	private AddressShapeMap addressShapeMap;

	//private Shape backgroundShape;
	private VisualizationParameters visualizationParameters;
	private ShapeBuilder shapeBuilder;

	
    protected boolean shuttingDown;

    private double startVisualizationTime;

	/**
	 * Construct a new <code>VisualisationManager</code> object.
	 * @param eventSet the event set used to schedule the visulaization events
	 * @param deviceManager the devicemanager used to determine the device shapes
	 * @param frameRenderer the frame renderer responsible to render all simulation frames
	 * @param consoleTextBuffer a buffer of pending text lines to be shown
	 * @param calculatorMap the map of statistical calculators intendet to be visualized on the fly
	 * @param dynamicSource the dynamic source used to determine the shape of the source
	 * @param addressShapeMap the map used to create device dependent background shapes
	 * @param backgroundShape the background shape
	 * @param shutdownAnnouncer
	 */
	//public VisualisationManager(EventSet eventSet, DeviceManager deviceManager, final FrameRenderer frameRenderer, ConsoleTextBuffer consoleTextBuffer,
	//        DynamicSource dynamicSource, AddressShapeMap addressShapeMap, Shape backgroundShape, ShapeBuilder shapeBuilder, SimulationShutdownAnnouncer shutdownAnnouncer) {
	public VisualisationManager(
			EventSet eventSet, DeviceManager deviceManager, 
			final FrameRenderer frameRenderer, ConsoleTextBuffer consoleTextBuffer,
	        DynamicSource dynamicSource, AddressShapeMap addressShapeMap, 
			//Shape backgroundShape, ShapeBuilder shapeBuilder, 
            double startVisualizationTime,
			VisualizationParameters parameters, ShapeBuilder shapeBuilder,
			SimulationShutdownAnnouncer shutdownAnnouncer) {

		this.eventSet = eventSet;
		this.deviceManager = deviceManager;
		this.frameRenderer = frameRenderer;
		this.consoleTextBuffer = consoleTextBuffer;
		this.dynamicSource = dynamicSource;
		this.addressShapeMap = addressShapeMap;
		//this.backgroundShape = backgroundShape;
		this.visualizationParameters = parameters;
        this.startVisualizationTime=startVisualizationTime;
		this.shapeBuilder = shapeBuilder;
		shutdownAnnouncer.addShutdownListener(new ShutdownListener() {

            public void shutdown() {
            	if(frameRenderer != null) {
                    frameRenderer.stopRender();
            	}
                shuttingDown=true;
            }
        });
	}

	/**
	 * Schedule the first visualization event.
	 */
	public void scheduleFirstEvent() {
		if(frameRenderer != null) {
            double currentTime=eventSet.getTime();
            if (currentTime<startVisualizationTime){
                currentTime=startVisualizationTime;
            }
			eventSet.add(new VisualizationEvent(currentTime, this));
		}
	}

	/**
	 * @see de.uni_trier.jane.simulation.visualization.VisualisationEventListener#handleVisualize()
	 */
	public void handleVisualize() {
	    if (shuttingDown) return;
		Shape frameShape = shapeBuilder.build(
			//networkRenderer.getNetworkShapes(),
			dynamicSource.getShape(),
			addressShapeMap.getShape(),
			visualizationParameters.getBackgroundShape(), //backgroundShape,
			deviceManager.getShape()
		);

		DeviceIDPositionMap addressPositionMap = deviceManager.getAddressPositionMap();
		
		frameRenderer.addFrame(
			new de.uni_trier.jane.simulation.visualization.Frame(
		        eventSet.getTime(), frameShape, visualizationParameters, 
				addressPositionMap, consoleTextBuffer.flush())
			);
		double interval = frameRenderer.getFrameInterval();
		eventSet.add(new VisualizationEvent(eventSet.getTime()+interval, this));
	}

}
