/*****************************************************************************
 * 
 * RenderQueue.java
 * 
 * $Id: RenderQueue.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import java.util.*;

import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.VisualizationParameters;
import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.local.SynchronizedEventSet;
import de.uni_trier.jane.simulationl.visualization.console.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * A render queue is a concrete implementation of the frame renderer and frame source
 * interface. It is used to synchronize a frame producer and a frame consumer.
 */
public class RenderQueue implements FrameRenderer, FrameSource {

	private final static String VERSION = "$Id: RenderQueue.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private int maxLength;
	private double frameInterval;
	private boolean waiting;
	private LinkedList frameList;
	
	private Frame lastRemovedFrame;
	private Frame newestInsertedFrame;
	
    private boolean stopRender;

    private SyncObject syncObject;
	
	/**
	 * Create a new render queue.
	 * @param maxLength the maximum number of frames buffered in the render queue
	 * @param frameInterval the interval length between two successive renderings in seconds
	 * @param initialFrame the initial frame to be rendered
	 */
	public RenderQueue(int maxLength, double frameInterval, Frame initialFrame) {
		this.maxLength = maxLength;
		this.frameInterval = frameInterval;

		this.lastRemovedFrame =
		this.newestInsertedFrame = initialFrame;

		waiting = false;
		frameList = new LinkedList();
	}
	
	/**
     * 
     * Constructor for class <code>RenderQueue</code>
     * @param parameters
	 */
	public RenderQueue(SimulationParameters parameters) {
		
		//public RenderQueue(int maxLength, double frameInterval, Shape dynamicSourceShape) {
		Shape dynamicSourceShape = 
			parameters.getVisualizationParameters().getBackgroundShape();
		if (parameters.getEventSet() instanceof SynchronizedEventSet) {
            syncObject = ((SynchronizedEventSet)parameters.getEventSet()).getSynchronizeObject();   
        }else{
            syncObject=new SyncObject();
        }
		this.maxLength = parameters.getRenderQueueLength();
		this.frameInterval = 1/parameters.getSimulationFrameFPS();
		ConsoleTextIterator it = new ConsoleTextIterator() {
            public boolean hasNext() {
                return false;
            }
            public ConsoleText next() {
                return null;
            }
        };
		//KeyVisualizedValueMap map = new KeyVisualizedValueMapImpl();
		DeviceIDPositionMap addressPositionMap = new DeviceIDPositionMapImpl();
        this.lastRemovedFrame = 
        	new Frame(0, dynamicSourceShape, 
        			  parameters.getVisualizationParameters(), 
      			      addressPositionMap, it);
 //   	new Frame(0, dynamicSourceShape, parameters.getTransformationMatrix(), 
 // 			  addressPositionMap, it);
		waiting = false;
		frameList = new LinkedList();
	}

	
    /**
	 * @see de.uni_trier.jane.simulation.visualization.FrameRenderer#addFrame(Frame)
	 */
	public void addFrame(Frame frame) {
        synchronized (syncObject) {
            
        
            if (stopRender) return;
            if(frameList.size() >= maxLength) {
                try {
                    waiting = true;
                //  syncObject.notify();
                    syncObject.wait();
				//  wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            newestInsertedFrame = frame;
            frameList.addLast(frame);
        }
	}

	/**
	 * @see de.uni_trier.jane.simulation.visualization.FrameSource#getFrame()
	 */
	public Frame getFrame() {
        synchronized (syncObject) {
            if  (!playing)
                return lastRemovedFrame;
            if(waiting) {
                waiting = false;
                syncObject.notify();
			//   notify();
            }
            if(!frameList.isEmpty()) {
                lastRemovedFrame = (Frame)frameList.removeFirst();
            }
            return lastRemovedFrame;
        }
	}

	
	/**
	 * @see de.uni_trier.jane.simulation.visualization.FrameSource#getCurrentFrame()
	 */
	public Frame getCurrentFrame() {
        synchronized (syncObject) {
            return lastRemovedFrame;
        }
	}
	
	/**
	 * necessary for a synced console
	 * @see de.uni_trier.jane.simulation.visualization.FrameSource#getCurrentFrame()
	 */
	public Frame getNewestInsertedFrame() {
        synchronized (syncObject) {
            return newestInsertedFrame;
        }
	}
	
	/**
	 * @see de.uni_trier.jane.simulation.visualization.FrameRenderer#getFrameInterval()
	 */
	public double getFrameInterval(/*double currenttime*/) {
        synchronized (syncObject) {
            return frameInterval;
        }
	}

	/**
	 * @see de.uni_trier.jane.simulation.visualization.FrameSource#setFrameInterval(double)
	 */
	public void setFrameInterval(double frameInterval) {
        synchronized (syncObject) {
            this.frameInterval = frameInterval;
        }
	}

	/**
	 * @see de.uni_trier.jane.simulation.visualization.FrameSource#getRectangle()
	 */
	public Rectangle getRectangle() {
        synchronized (syncObject) {
    		Frame frame;
    		if(!frameList.isEmpty()) {
    			frame = (Frame)frameList.getFirst();
    			return frame.getShape()
    				.getRectangle(new Position(0.0,0.0),frame.getTransformation());
    			//frame.getShape().
    			//return ((Frame)frameList.getFirst()).getShape().getRectangle(new Position(0,0),Matrix.identity3d());
    		}
    		
    		return lastRemovedFrame.getShape()
    				.getRectangle(new Position(0.0,0.0),lastRemovedFrame.getTransformation());
    			//return lastRemovedFrame.getShape().getRectangle(new Position(0,0),Matrix.identity3d());
    		
        }
	}

	/**
	 * flag indicating wether the renderqueue is active currently
	 */
	protected boolean playing=true;
	
	/**
	 * this prevents taking frames out of the queue and therefore stops the rendering
	 * @param playing
	 */
	public void setPlayPauseRender(boolean playing) {
        synchronized (syncObject) {
            this.playing=playing;
		//    notify();
            syncObject.notify();
        }
	}
	
    public  void stopRender() {
        synchronized (syncObject) {
        //  notify();
            syncObject.notify();
            stopRender=true;
        }
    }
}
