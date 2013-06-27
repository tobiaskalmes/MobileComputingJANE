/*****************************************************************************
 * 
 * DefaultSimulationParameters.java
 * 
 * $Id: DefaultSimulationParameters.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.random.prng.MergedRandomNumberGeneratorFactory;
import de.uni_trier.jane.sgui.SimulationMainFrame;
import de.uni_trier.jane.simulation.dynamic.*;
import de.uni_trier.jane.simulation.dynamic.linkcalculator.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.*;
import de.uni_trier.jane.simulation.dynamic.position_generator.*;
import de.uni_trier.jane.simulation.gui.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.visualization.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * Default implementation of the Simulation parameters.
 * alls parameters are set to a default value
 * @author goergen
 *
 *
 */
public class DefaultSimulationParameters implements  SimulationParameters {

    //private static DefaultSimulationParameters parameters;
    
//    public static DefaultSimulationParameters getSimulationParameters(){
//        if (parameters==null) throw new IllegalStateException("Simulation parameters are not initialized!");
//        return parameters;
//    }
//    
    private ShapeBuilder shapeBuilder;
//    private double maximumAllowedTransmissionRadius;
//    private double minimumAllowedTransmissionRadius;
    
    private DefaultVisualizationParameters visualizationParameters;
    
    private ShutdownAnnouncer globalShutdownAnnouncer;
  //  private FrameRenderer frameRenderer;
    private DistributionCreator distributionCreator;
    //private SimulationRenderer simulationRenderer;
    
    private Condition terminalCondition;
    private EventSet eventSet;
    private String simulationName;
    private DynamicSource dynamicSource;
    private Output resultOutput;
    private Output consoleOutput;
    private MobilitySource mobilitySource;
    private boolean dynamicInitialized;
    private boolean hybrid;


    private int eventTemplateReflectionDepth=0;
    
    
	/**
	 * Specifies whether the GUI is synchronized with the simulation or not.
	 */
	private boolean isSynchronizedWithGUI;
    private double printIntervall;
    
    private ContinuousDistribution processingTimeDistribution=new ConstantDistribution(0);
    
   
//    static SimulationParameters getSimulationParameters({
//        if (parameters==null)
//    }
	
    /**
     * Constructor for class <code>DefaultSimulationParameters</code>
     * 
     */
    public DefaultSimulationParameters() {
//        if (parameters!=null){
//            throw new IllegalStateException("Only one simulation parameters object can be created");
//        }else{
//            parameters=this;
//        }
        
   
//	    maximumAllowedTransmissionRadius = Double.MAX_VALUE;
//	    minimumAllowedTransmissionRadius = -1;

	
	    this.simulationName = "Default Simulation";
		eventSet = new CascadeEventSet(10, 25);

		visualizationParameters = new DefaultVisualizationParameters();
		resultOutput = new DefaultOutput();
		consoleOutput = new DefaultOutput();
	//	simulationRenderer = new DefaultSimulationRenderer();
		shapeBuilder = new DefaultShapeBuilder();
		globalShutdownAnnouncer = new SimulationShutdownAnnouncer();
        distributionCreator =new DistributionCreator(
                new MergedRandomNumberGeneratorFactory(
                        MergedRandomNumberGeneratorFactory.RANECU,
                        MergedRandomNumberGeneratorFactory.LCG).getPseudoRandomNumberGenerator(41));
//		distributionCreator = new DistributionCreator(
//				new long[] { 2992515376L, 2278929704L,
//						2220834997L, 2580257810L, 4102705091L, 2323110552L,
//						3932568423L, 2726271371L, 2915158958L, 3209013225L,
//						2451125634L, 2790182054L, 3174286243L, 2946319777L,
//						2658270748L, 3798086406L, 3721595981L, 4138230070L,
//						2894504813L, 4198898146L, 4209437256L, 3353908892L,
//						3777241970L, 4012596588L, 3700701375L, 3006727708L,
//						3021678370L, 2695257626L, 3940206439L, 3390587984L,
//						3689292477L, 2791185772L, 3119876180L, 3809634552L,
//						2357978999L, 3815754978L, 2150105929L, 4222981334L,
//						2743901562L, 2972333909L, 4266298526L, 3764380222L,
//						4166426483L, 2191737416L, 3459612483L, 3589411244L,
//						4235161741L, 2692700196L, 3049326775L, 2240639017L,
//						2214260825L, 4100106086L, 3290868482L, 4153290466L,
//						4043026814L, 3345470120L, 4257932121L, 2768328448L,
//						3569880499L, 2567379269L, 4115025113L, 2425754172L,
//						3955518023L, 2784295484L, 2652223495L, 3251235549L,
//						4283747673L, 3969610748L, 2483344807L, 3831027129L,
//						2482004287L, 3654719834L, 3069436291L, 2295069938L,
//						3781709309L, 3453834308L, 4042917813L, 3528137750L,
//						3999032117L, 2692473174L, 3763587115L, 3057809419L,
//						2467004861L, 2647401658L, 2305545939L, 4095494918L,
//						3173446098L, 3942105797L, 2871190919L, 2888677175L,
//						2398563468L, 2168884391L, 2375568598L, 2813259816L,
//						3505883198L, 3952497072L, 2766111656L, 4218410491L,
//						3734243328L, 2292209605L });
		
		
		mobilitySource=null;//createMobilitySource();
		dynamicSource=new LinkCalculator3D(null);
		

		// by default the GUI is not synchronized with the simulation
		isSynchronizedWithGUI = false;
        printIntervall=60;
    }
    
	/**
	 * 
	 *  
	 * @return the default mobility source
	 */
    private MobilitySource createMobilitySource() {
		ContinuousDistribution xDistribution = distributionCreator.getContinuousUniformDistribution(0,500);
		ContinuousDistribution yDistribution = distributionCreator.getContinuousUniformDistribution(0,500);
		PositionGenerator positionGenerator = new RandomPositionGenerator(xDistribution, yDistribution);
		ContinuousDistribution sendingRadiusDistribution = distributionCreator.getContinuousUniformDistribution(100,100);
		return FixedNodes.createRandom(50, positionGenerator, sendingRadiusDistribution, new RectangleShape(new Rectangle(new Position(0,0), new Position(500,500)), Color.BLACK, false));
    }

  
//    public boolean automaticListenerCleanup(){
//        return automaticListenerCleanup;
//    }
//    
//    /**
//     * @param automaticListenerCleanup The automaticListenerCleanup to set.
//     */
//    public void setAutomaticListenerCleanup(boolean automaticListenerCleanup) {
//        this.automaticListenerCleanup = automaticListenerCleanup;
//    }
    

    public String getSimulationName() {
        return simulationName;
    }

    /**
     * @param simulationName The simulationName to set.
     */
    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.SimulationParameters#getEventSet()
     */
    public EventSet getEventSet() {
        return eventSet;
    }

    /**
     * @param eventSet The eventSet to set.
     */
    public void setEventSet(EventSet eventSet) {
        this.eventSet = eventSet;
    }
    
    
    public Condition getTerminalCondition() {
        return terminalCondition;
    }

    /**
     * @param terminalCondition The terminalCondition to set.
     */
    public void setTerminalCondition(Condition terminalCondition) {
        this.terminalCondition = terminalCondition;
    }
    
//    /* (non-Javadoc)
//     * @see de.uni_trier.jane.simulation.SimulationParameters#getResultOutput()
//     */
//    public Output getResultOutput() {
//        return resultOutput;
//    }
//    
//    /**
//     * @param resultOutput The resultOutput to set.
//     */
//    public void setResultOutput(Output resultOutput) {
//        this.resultOutput = resultOutput;
//    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.SimulationParameters#getConsoleOutput()
     */
    public Output getConsoleOutput() {
        return consoleOutput;
    }

    /**
     * @param consoleOutput The consoleOutput to set.
     */
    public void setConsoleOutput(Output consoleOutput) {
        this.consoleOutput = consoleOutput;
    }
    
//    /* (non-Javadoc)
//     * @see de.uni_trier.jane.simulation.SimulationParameters#getSimulationRenderer()
//     */
//    public SimulationRenderer getSimulationRenderer() {
//        return simulationRenderer;
//    }
//
//    /**
//     * @param simulationRenderer The simulationRenderer to set.
//     */
//    public void setSimulationRenderer(SimulationRenderer simulationRenderer) {
//        this.simulationRenderer = simulationRenderer;
//    }
//    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.SimulationParameters#getDistributionCreator()
     */
    public DistributionCreator getDistributionCreator() {
        return distributionCreator;
    }

    /**
     * @param distributionCreator The distributionCreator to set.
     */
    public void setDistributionCreator(DistributionCreator distributionCreator) {
        this.distributionCreator = distributionCreator;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.SimulationParameters#getDynamicSource()
     */
    public DynamicSource getDynamicSource() {

        if (!dynamicInitialized&&dynamicSource instanceof MobilityDynamicSource){
            dynamicInitialized=true;
            
            ((MobilityDynamicSource)dynamicSource).start(getMobilitySource());
        }
        return dynamicSource;
    }

    /**
     * @param dynamicSource The dynamicSource to set.
     */
    public void setDynamicSource(DynamicSource dynamicSource) {
        dynamicInitialized=false;
        this.dynamicSource = dynamicSource;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.SimulationParameters#getFrameRenderer()
     */
    public FrameRenderer getFrameRenderer() {
        return visualizationParameters.getFrameRenderer();
    }

    /**
     * @param frameRenderer The frameRenderer to set.
     */
    public void setFrameRenderer(FrameRenderer frameRenderer) {
        visualizationParameters.setFrameRenderer(frameRenderer);
    }
    
 
    public ShutdownAnnouncer getGlobalShutdownAnnouncer() {
        return globalShutdownAnnouncer;
    }

//    /**
//     * @param globalShutdownAnnouncer The globalShutdownAnnouncer to set.
//     */
//    public void setGlobalShutdownAnnouncer(
//            GlobalShutdownAnnouncer globalShutdownAnnouncer) {
//        this.globalShutdownAnnouncer = globalShutdownAnnouncer;
//    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.SimulationParameters#getMaximumAllowedTransmissionRadius()
     */
    
//    public double getMaximumAllowedTransmissionRadius() {
//        return maximumAllowedTransmissionRadius;
//    }
//    
//    
//    /**
//     * @param maximumAllowedTransmissionRadius The maximumAllowedTransmissionRadius to set.
//     */
//    public void setMaximumAllowedTransmissionRadius(
//            double maximumAllowedTransmissionRadius) {
//        this.maximumAllowedTransmissionRadius = maximumAllowedTransmissionRadius;
//    }
//
//    /* (non-Javadoc)
//     * @see de.uni_trier.jane.simulation.SimulationParameters#getMinimumAllowedTransmissionRadius()
//     */
//    public double getMinimumAllowedTransmissionRadius() {
//        return minimumAllowedTransmissionRadius;
//    }
//
//    /**
//     * @param minimumAllowedTransmissionRadius The minimumAllowedTransmissionRadius to set.
//     */
//    public void setMinimumAllowedTransmissionRadius(
//            double minimumAllowedTransmissionRadius) {
//        this.minimumAllowedTransmissionRadius = minimumAllowedTransmissionRadius;
//    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.SimulationParameters#getShapeBuilder()
     */
    public ShapeBuilder getShapeBuilder() {
        return shapeBuilder;
    }
    
    /**
     * @param shapeBuilder The shapeBuilder to set.
     */
    public void setShapeBuilder(ShapeBuilder shapeBuilder) {
        this.shapeBuilder = shapeBuilder;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.SimulationParameters#setMobilitySource(de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource)
     */
    public void setMobilitySource(MobilitySource mobilitySource) {
        dynamicInitialized=false;
        this.mobilitySource=mobilitySource;
        
    }

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.SimulationParameters#getMobilitySource()
	 */
	public MobilitySource getMobilitySource() {
        if (mobilitySource==null)mobilitySource=createMobilitySource();
		return mobilitySource;
	}
	
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.SimulationParameters#useLinkCalculator(boolean)
     */
    public void useLinkCalculator(boolean useLinkCalculator) {
        dynamicInitialized=false;
        if (useLinkCalculator){
            dynamicSource=new LinkCalculator(null);
        }else{
            dynamicSource=new NullLinkCalculator();
        }
        
    }
    
    public boolean isHybrid() {
		return hybrid;
	}
    
	public void setHybrid(boolean hybrid) {
		this.hybrid = hybrid;
	}

	public int getRenderQueueLength() {
		
		return visualizationParameters.getRenderQueueLength();
	}
	
	public void setRenderQueueLength(int frames) {
		visualizationParameters.setRenderQueueLength(frames);
	}
	
    public double getSimulationFrameFPS() {
        return visualizationParameters.getFramesPerSecond();
    }
    

    public void setSimulationFrameFPS(double fps) {
        visualizationParameters.setFramesPerSecond(fps);
    }
    
    public boolean isUseVisualisationOn() {
        return visualizationParameters.isVisualize();
    }
    
    

    public void useVisualisation() {
    	visualizationParameters.useVisualisation();
    }
    
    /**
     *
     */

    public void useVisualisation(SimulationFrame simulationFrame) {
    	visualizationParameters.useVisualisation(simulationFrame);
        
    }
    
   
    /**
     * Returns the simulationFrame
     * @return the SimulationFrame
     * @deprecated uses VisualisationParameters.getSimulationFrame instead
     */
    public SimulationFrame getSimulationFrame() {
        return visualizationParameters.getSimulationFrame();
    }
    
    /**
     *
     */

    public Rectangle getRectangle() {
     
        return getMobilitySource().getRectangle();
    }

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.SimulationParameters#getVisualizationParameters()
	 */
	public VisualizationParameters getVisualizationParameters() {
		return visualizationParameters;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.SimulationParameters#setVisualizationParameters(de.uni_trier.jane.simulation.VisualizationParameters)
	 */
//	public void setVisualizationParameters(VisualizationParameters visualizationParameters) {
//		// TODO Auto-generated method stub
//		this.visualizationParameters = visualizationParameters; 
//	}
    
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.SimulationParameters#isSychronized()
	 */
	public boolean isSynchronizedWithGUI() {
		return isSynchronizedWithGUI;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.SimulationParameters#
	 *      setSynchronizedWithGUI(boolean)
	 */
	public void setSynchronizedWithGUI(boolean isSynchronizedWithGUI) {
		this.isSynchronizedWithGUI = isSynchronizedWithGUI;
	}
    
    public void printTimeToConsole(boolean printTimeValues) {
        if (printTimeValues){
            this.printIntervall=60.0;
        }else{
            this.printIntervall=-1;
        }
      
    }
    
    //
    public void printTimeToConsole(double printIntervall) {
        this.printIntervall=printIntervall;
        
    }
    
    
    
    
    public double printTimeToConsole(){
        return printIntervall;
    }
    
    public void startVisualisationAt(double time) {
    	visualizationParameters.startVisualisationAt(time);
         
    }
    
    public double getVisualisationStartTime() {
        return visualizationParameters.getVisualisationStartTime();
    }

    /**
     * TODO Comment method
     * @return
     */
    public ContinuousDistribution getProcessingTimeDistribution() {
        return processingTimeDistribution;
    }
    
    /**
     * @param processingTimeDistribution The processingTimeDistribution to set.
     */
    public void setProcessingTimeDistribution(
            ContinuousDistribution processingTimeDistribution) {
        this.processingTimeDistribution = processingTimeDistribution;
    }

    /**
     * TODO Comment method
     * @return
     */
    public double getTotalDeviceEnergy() {

        return Double.POSITIVE_INFINITY;
    }
    
    public void setEventReflectionDepth(int depth) {
        eventTemplateReflectionDepth=depth;
    }
    
    /**
     * @return Returns the eventTemplateReflectionDepth.
     */
    public int getEventReflectionDepth() {
        return this.eventTemplateReflectionDepth;
    }
	
}
