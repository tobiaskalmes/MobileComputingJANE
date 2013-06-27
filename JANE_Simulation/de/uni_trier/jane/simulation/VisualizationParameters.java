/*
 * Created on 31.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.simulation;

import de.uni_trier.jane.basetypes.Matrix;
import de.uni_trier.jane.simulation.gui.SimulationFrame;
import de.uni_trier.jane.simulation.visualization.FrameRenderer;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * @author sausen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface VisualizationParameters {

	/**
     * Returns the simulation background shape
     * @return the background shape
     */
    public Shape getBackgroundShape();

    /**
     * Sets the shape of the simulation backround
     * @param backgroundShape The backgroundShape to set.
     */
    public void setBackgroundShape(Shape backgroundShape);

    /**
     * get the transformation matrix for visualizing
     * @return the transformation matrix
     */
        public Matrix getTransformationMatrix();
    /**
     * Sets the transformation matrix for visualizing
     * @param transformation the matrix
     */
    public void setTransformationMatrix(Matrix transformation);
    
	/**
	 * @return Returns the frameCacheSize.
	 */
	public int getFrameCacheSize();
	/**
	 * @param frameCacheSize The frameCacheSize to set.
	 */
	public void setFrameCacheSize(int frameCacheSize) ;
	/**
	 * @return Returns the framesPerSecond.
	 */
	public double getFramesPerSecond() ;
	/**
	 * @param framesPerSecond The framesPerSecond to set.
	 */
	public void setFramesPerSecond(double framesPerSecond) ;
	/**
	 * @return Returns the visualize.
	 */
	public boolean isVisualize();
	/**
	 * Turns the default visualisation on
	 */
	public void useVisualisation();
    
    public void useVisualisation(boolean use_visualisation);
	
    /**
     * Turns the visualisation on using the given simulation frame 
     * @param simulationFrame
     */
	public void useVisualisation(SimulationFrame simulationFrame);
    
    

	/**
	 * returns the length of the render queue
	 * @return
	 */
	public int getRenderQueueLength();
	/**
	 * The render queue length. The longer the queue, the higher the time difference 
	 * between visualisation and simulation. Default is 10.
	 * @param frames
	 */
	public void setRenderQueueLength(int frames);

	
    
	public FrameRenderer getFrameRenderer();

	public void setFrameRenderer(FrameRenderer frameRenderer);

	/**
	 * Wait time seconds until the visualisation begins
	 * @param time
	 */
	public void startVisualisationAt(double time);
    
    public SimulationFrame getSimulationFrame() ;

    




    

}
