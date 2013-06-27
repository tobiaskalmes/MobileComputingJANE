/*
 * Created on 31.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.simulation;

import de.uni_trier.jane.basetypes.Matrix;
import de.uni_trier.jane.sgui.SimulationMainFrame;
import de.uni_trier.jane.simulation.gui.SimulationFrame;
import de.uni_trier.jane.simulation.visualization.FrameRenderer;
import de.uni_trier.jane.visualization.shapes.EmptyShape;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * @author nico
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultVisualizationParameters implements VisualizationParameters {

	private Shape backgroundShape = EmptyShape.getInstance();
    private Matrix transformation = Matrix.identity3d();
    
    private boolean visualize = false;
    private double framesPerSecond=5;
    private int frameCacheSize=50;
	private SimulationFrame simulationFrame;
	private int renderQueueLength=10;
	private double visualisationStartTime;
	private FrameRenderer frameRenderer;
    

	/**
	 * @return Returns the frameCacheSize.
	 */
	public int getFrameCacheSize() {
		return frameCacheSize;
	}

	/**
	 * @param frameCacheSize The frameCacheSize to set.
	 */
	public void setFrameCacheSize(int frameCacheSize) {
		this.frameCacheSize = frameCacheSize;
	}

	/**
	 * @return Returns the framesPerSecond.
	 */
	public double getFramesPerSecond() {
		return framesPerSecond;
	}

	/**
	 * @param framesPerSecond The framesPerSecond to set.
	 */
	public void setFramesPerSecond(double framesPerSecond) {
		this.framesPerSecond = framesPerSecond;
	}

	/**
	 * @return Returns the visualize.
	 */
	public boolean isVisualize() {
		return visualize;
	}

	/**
	 * @param visualize
	 *            The visualize to set.
	 */
	public void useVisualisation() {
		if (!visualize) {
			// old:
			// simulationFrame=new DefaultSimulationFrame();
			simulationFrame = new SimulationMainFrame();
			visualize = true;
		} else {
			throw new IllegalStateException(
					"Visualisation is already turned on");
		}

	}
    
    public void useVisualisation(boolean use_visualisation) {
        if (use_visualisation) useVisualisation();
        
    }
	
    public void useVisualisation(SimulationFrame simulationFrame) {
        if (!visualize){
            this.simulationFrame=simulationFrame; 
            visualize=true;
        }else{
         //   throw new IllegalStateException("Visualisation is already turned on");
        }
    }
	
	public int getRenderQueueLength() {
		
		return renderQueueLength;
	}
	
	public void setRenderQueueLength(int frames) {
		renderQueueLength=frames;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_trier.jane.simulation.VisualizationParameters#getBackgroundShape()
	 */
	public Shape getBackgroundShape() {
		return backgroundShape;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.VisualizationParameters#setBackgroundShape(de.uni_trier.jane.visualization.shapes.Shape)
	 */
	public void setBackgroundShape(Shape backgroundShape) {
		this.backgroundShape = backgroundShape;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.VisualizationParameters#getTransformationMatrix()
	 */
	public Matrix getTransformationMatrix() {
		//System.out.println("getTransformationMatrix this:"+this);
		return transformation;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.VisualizationParameters#setTransformationMatrix(de.uni_trier.jane.basetypes.Matrix)
	 */
	public void setTransformationMatrix(Matrix transformation) {
		this.transformation = transformation;
	}

	public SimulationFrame getSimulationFrame() {
		return simulationFrame;
	}

	public FrameRenderer getFrameRenderer() {
		return frameRenderer;
	}

	public void setFrameRenderer(FrameRenderer frameRenderer) {
		this.frameRenderer=frameRenderer;
		
	}

	public void startVisualisationAt(double time) {
		visualisationStartTime=time;
		
	}

	public double getVisualisationStartTime() {
		return visualisationStartTime;
	}

}
