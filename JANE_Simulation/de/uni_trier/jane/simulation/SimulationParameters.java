/*****************************************************************************
 * 
 * SimulationParameters2.java
 * 
 * $Id: SimulationParameters.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation;

import de.uni_trier.jane.basetypes.Output;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.random.ContinuousDistribution;
import de.uni_trier.jane.random.DistributionCreator;
import de.uni_trier.jane.simulation.dynamic.DynamicSource;
import de.uni_trier.jane.simulation.dynamic.linkcalculator.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource;
import de.uni_trier.jane.simulation.gui.SimulationFrame;
import de.uni_trier.jane.simulation.kernel.Condition;
import de.uni_trier.jane.simulation.kernel.ShutdownAnnouncer;
import de.uni_trier.jane.simulation.kernel.eventset.EventSet;
import de.uni_trier.jane.simulation.visualization.FrameRenderer;
import de.uni_trier.jane.simulation.visualization.ShapeBuilder;

/**
 * Parameter object for the simulation.
 * This object contains all user definined and default simulation parameters
 * 
 * @author goergen
 *
 *
 */
public interface SimulationParameters {
    
    /**
     * Returns the simulation name
     * @return the name of the simulation
     */
    public String getSimulationName();

    /**
     * @param simulationName The simulationName to set.
     */
    public void setSimulationName(String simulationName);

    /**
     * @return the EventSet of the simulation
     */
    public EventSet getEventSet();

    /**
     * Changes the used EventSet
     * @param eventSet The eventSet to set.
     */
    public void setEventSet(EventSet eventSet);

    /**
     * Return the user defined TerminalCondition of the simulation
     * @return the terminal condition
     */
    public Condition getTerminalCondition();

    /**
     * Sets the user defined terminal condition for the simulation
     * @param terminalCondition The terminalCondition to set.
     */
    public void setTerminalCondition(Condition terminalCondition);


    /**
     * Gets the output for simulations console output
     *  
     * @return the output
     */
    // TODO: dies wird von der GUI nicht aufgerufen!!!
    public Output getConsoleOutput();

    /**
     * Sets the output for simulations out put
     * @param consoleOutput The consoleOutput to set.
     */
    public void setConsoleOutput(Output consoleOutput);

//    /* (non-Javadoc)
//     * @see de.uni_trier.jane.simulation.SimulationParameters#getSimulationRenderer()
//     */
//    public SimulationRenderer getSimulationRenderer();
//
//    /**
//     * @param simulationRenderer The simulationRenderer to set.
//     */
//    public void setSimulationRenderer(SimulationRenderer simulationRenderer);

    /**
     * Returns the distribution creator
     * @return	the distribution creator
     */
    public DistributionCreator getDistributionCreator();

    /**
     * Sets the distribution creator
     * @param distributionCreator The distributionCreator to set.
     */
    public void setDistributionCreator(DistributionCreator distributionCreator);

    /**
     * Gets the dynamic source of the simulation 
     * @return the dynamic source
     */
    public DynamicSource getDynamicSource();

    /**
     * Sets the dynamic source of the simulation.
     * A dynamic source provides all mobility information for the simulated 
     * mobile devices including all network link information 
     * @param dynamicSource The dynamicSource to set.
     */
    public void setDynamicSource(DynamicSource dynamicSource);

    /**
     * Returns the frame renderer.
     * needed for visualisation
     * @return	the frame renderer
     */
    public FrameRenderer getFrameRenderer();

    /**
     * Sets the frame renderer for visualizing simulation frames.
     * @param frameRenderer The frameRenderer to set.
     */
    public void setFrameRenderer(FrameRenderer frameRenderer);

    /**
     * Returns the global shutdown announcer. This announces a simulation shutdown.
     * With that it is ensured, that all simulation parts are correctly shudowned. 
     * @return	the shutdown announcer
     */
    public ShutdownAnnouncer getGlobalShutdownAnnouncer();

//    /**
//     * @param globalShutdownAnnouncer The globalShutdownAnnouncer to set.
//     */
//    public void setGlobalShutdownAnnouncer(
//            GlobalShutdownAnnouncer globalShutdownAnnouncer);

    /**
     * Returns the visualization parameters
     */
    public VisualizationParameters getVisualizationParameters();
    /**
     * sets the visualization parameters
     * @param visualizationParameters
     */
   // public void setVisualizationParameters(VisualizationParameters visualizationParameters);
    
//    /**
//     * 
//     * TODO: comment method 
//     * @return
//     */
//    public double getMaximumAllowedTransmissionRadius();
//
//    /**
//     * @param maximumAllowedTransmissionRadius The maximumAllowedTransmissionRadius to set.
//     */
//    public void setMaximumAllowedTransmissionRadius(
//            double maximumAllowedTransmissionRadius);
//
//    /**
//     * 
//     * TODO: comment method 
//     * @return
//     */
//    public double getMinimumAllowedTransmissionRadius();
//
//    /**
//     * @param minimumAllowedTransmissionRadius The minimumAllowedTransmissionRadius to set.
//     */
//    public void setMinimumAllowedTransmissionRadius(
//            double minimumAllowedTransmissionRadius);

    /**
     * 
     * TODO: is a shape builder still neccesary? 
     * Another approach is needed: organize the service shapes!
     * @return
     */
    public ShapeBuilder getShapeBuilder();

    /**
     * @param shapeBuilder The shapeBuilder to set.
     */
    public void setShapeBuilder(ShapeBuilder shapeBuilder);

    /**
     * Sets the mobility source for the simulation.
     * It contains all mobility information for the simulated mobile devices
     * The dynamic source is initialized with this one at startup.
     * @param mobilitySource
     */
    public void setMobilitySource(MobilitySource mobilitySource);

    /**
     * @return The mobility source for the simulation.
     */
    public MobilitySource getMobilitySource();
    
    /**
     * Enable or disabel link calculation
     * Default: enabled
     * @param useLinkCalculator		false disables the link calcultation 	
     */
    public void useLinkCalculator(boolean useLinkCalculator);
    
    /**
     * returns true, if the simulation is set to the hybrid mode
     * @return true if the simulation is set to the hybrid mode
     */
    public boolean isHybrid() ;
    
    /**
     * Enables/disables the simulation hybrid mode
     * disabled by default 
     * When hybrid mode is enabled the JANE hybrid server is started for connecting extern devices
     * @param hybrid	true to enabel the hybrid mode
     */
	public void setHybrid(boolean hybrid) ;

    /**
     * Gets the visualization framrate  in frames per second
     * @return frames per second
     */
    public double getSimulationFrameFPS();
    
    /**
     * Sets the visualization framrate to the given value in frames per second
     * @param fps  frames per second
     */
    public void  setSimulationFrameFPS(double fps);
    
    
    /**
     * Sets the lenght of the Renderqueue in frames
     * @param frames	the number of maximum frames in the RenderQueue
     */
    public void setRenderQueueLength(int frames);
    
    /**
     * Gets the lenght of the Renderqueue in frames
     * @return	the number of maximum frames in the RenderQueue
     */
    public int getRenderQueueLength();
    
    /**
     * Visualize the simulation with the DefaultSimulationFrame
     */
    public void useVisualisation();
    

    /**
     * Visualize the simulation with the given simulation frame
     * @param simulationFrame the simulationFrame to be used
     */
    public void useVisualisation(SimulationFrame simulationFrame);
    
    
    /**
     * Returns true if simulation visualisation is turned on
     * 
     * @return	true if visualisation is turned on
     */
    public boolean isUseVisualisationOn();

    /**
     * TODO: comment method 
     * @return
     */
    public Rectangle getRectangle();
    
    /**
     * @return <b>true</b>, if the GUI is synchronized with the simulation
     *         and <b>false</b>, otherwise.
     */
    public boolean isSynchronizedWithGUI();
    
    /**
     * Specifies whether the GUI is synchronized with the simulation or not.
     * If it is synchronized the user can interact through the GUI with the
     * simulation without thinking about synchronization.
     * The default value is <b>false</b>.
     * @param sync If this value is set to <b>true</b> the GUI is synchronized 
     *        with the simuatlion, otherwise the simulation is not synchronized.
     */
    public void setSynchronizedWithGUI(boolean isSynchronizedWithGUI);

    public void printTimeToConsole(boolean printTimeValues);
    
    /**
     * TODO: comment method 
     * @param printIntervall
     */
    public void printTimeToConsole(double printIntervall);

    public void startVisualisationAt(double time);
    
    /**
     * Turns a garbage collector based listener cleanup on.
     * Attention! Currently this causes nondeterminism!
     * Only applicable for demonstration szenarios with user interaction...
     * @param automaticListenerCleanup
     */
//    public void setAutomaticListenerCleanup(boolean automaticListenerCleanup);
    
    public void setProcessingTimeDistribution(
            ContinuousDistribution processingTimeDistribution);
    
    /**
     * Changes the depth of event template matching. Default is 0. This matches only the  event attributes by equals. 
     * Using 1, also the event attributes are matched as templates and their attributes are matched using equals and so forth.
     * 
     * @param depth
     */
    public void setEventReflectionDepth(int depth);

  

    
	
}