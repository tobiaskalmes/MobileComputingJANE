/*****************************************************************************
 * 
 * ApplicationSimulation.java
 * 
 * $Id: ApplicationSimulation.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.kernel;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.console.*;
import de.uni_trier.jane.hybrid.local.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.device.*;
import de.uni_trier.jane.simulation.dynamic.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.visualization.*;
import de.uni_trier.jane.simulationl.visualization.console.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * The main application class. It is initialized by a SimulationInitializer
 * and contains the main loop that extracts events from the event set and
 * executes them until the terminal condition is reached.
 */
public class ApplicationSimulation  {

	private final static String VERSION = "$Id: ApplicationSimulation.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private EventSet eventSet;
	private String simulationName;
	private TerminalCondition terminalCondition;
	
	//private Output resultOutput;
	private Output consoleOutput;
	//private SimulationRenderer simulationRenderer;
	//private StatisticsRenderer statisticsRenderer;
	//private GlobalsRenderer globalsRenderer;
	
	//private GlobalStatistics globalStatistics;
	//private LocalStatistics localStatistics;
	
	//private Globals constantGlobals;
	//private Globals variableGlobals;
	
	

	private ConsoleTextBuffer consoleTextBuffer;
	private DeviceManager deviceManager;

    private DefaultSimulationParameters initializer;

    private TerminalCondition terminalConditionDevice;

    private boolean shutdown;

	/**
	 * Constructs a new ApplicationSimulation implementing the Simulation interface. 
	 * All initial settings are retrieved from the supplied SimulationInitializer.
	 * @param initializer the SimulationInitializer for this ApplicationSimulation
	 * 
	 * @see de.uni_trier.ubi.appsim.kernel.Simulation
	 * @see de.uni_trier.ubi.appsim.kernel.SimulationInitializer
	 */
	public ApplicationSimulation(DefaultSimulationParameters initializer, SimulationServiceFactory serviceFactory) {
		this.simulationName = initializer.getSimulationName();
		if (initializer.isHybrid()||initializer.isSynchronizedWithGUI()){
            
			//eventSet=new SynchronizedEventSet(initializer.getEventSet());
            initializer.setEventSet(new SynchronizedEventSet(initializer.getEventSet()));
		}
        if(initializer.isUseVisualisationOn()){
            initializer.getVisualizationParameters().getSimulationFrame().show(initializer);
        }
		this.eventSet = initializer.getEventSet();
				
		//this.resultOutput = initializer.getResultOutput();
		this.consoleOutput = initializer.getConsoleOutput();
        this.initializer=initializer;
		//this.simulationRenderer = initializer.getSimulationRenderer();
		//this.statisticsRenderer = initializer.getStatisticsRenderer();
		//this.globalsRenderer = initializer.getGlobalsRenderer();
		//SimulationNetwork network = initializer.getNetwork();
		DistributionCreator distributionCreator = initializer.getDistributionCreator();
		DynamicSource dynamicSource = initializer.getDynamicSource();
		FrameRenderer frameRenderer = initializer.getFrameRenderer();
		//globalStatistics = new GlobalStatistics(new SimulationClock(eventSet));
		//localStatistics = new LocalStatistics(new SimulationClock(eventSet));
		//CalculatorMap calculatorMap = new CalculatorMap();
		//initializer.initializeStatistics(new StatisticsInitializer(globalStatistics), new StatisticsMonitorSystemImplementation(null, calculatorMap));
		//constantGlobals = new ConstantGlobals();
		//variableGlobals = new VariableGlobals();
		//initializer.initializeGlobals(new GlobalsInitializer(constantGlobals, variableGlobals));
		Clock simulationClock = new SimulationClock(initializer.getEventSet());

		Console bypassConsole = null;
		if(consoleOutput != null) {
			bypassConsole = new OutputConsole(consoleOutput);
		}
		if(frameRenderer == null) {
			consoleTextBuffer = new QuietConsoleTextBuffer(bypassConsole);
		}
		else {
			consoleTextBuffer = new ConsoleTextBufferImpl(bypassConsole);
		}
		//ApplicationUserSource applicationUserSource = initializer.getApplicationUserSource();
		//NetworkRenderer networkRenderer = initializer.getNetworkRenderer();
		AddressShapeMap addressShapeMap = new AddressShapeMap();
		//Creating an efficient terminal condition
		
		Condition mobilityTerminalCondition=initializer.getDynamicSource().getTerminalCondition(simulationClock);
		Condition userTerminalCondition=  initializer.getTerminalCondition();
		if (userTerminalCondition!=null){
		    if (mobilityTerminalCondition!=null){
		        terminalCondition=new TerminalCondition3(userTerminalCondition,mobilityTerminalCondition);
		    }else{
		        terminalCondition=new TerminalCondition2(userTerminalCondition);
		    }
		}else{
		    if (mobilityTerminalCondition!=null){
		        terminalCondition=new TerminalCondition2(mobilityTerminalCondition);
		    }else{
		        terminalCondition=new TerminalCondition();
		    }
		}
        terminalConditionDevice=new TerminalCondition();
		SimulationShutdownAnnouncer shutdownAnnouncer=(SimulationShutdownAnnouncer)initializer.getGlobalShutdownAnnouncer();
		shutdownAnnouncer.init(eventSet);
		Shape shape=initializer.getVisualizationParameters().getBackgroundShape();
		
		
		//shutdownAnnouncer.addShutdownListener()
		deviceManager = new DeviceManager(
				initializer, 
				this, 
				serviceFactory, 
		        consoleTextBuffer, 
		        addressShapeMap,
                terminalConditionDevice, 
		        initializer.getMobilitySource().getMinimumTransmissionRange(), 
				initializer.getMobilitySource().getMaximumTransmissionRange(),
				shutdownAnnouncer);
		
//		VisualisationManager visualisationManager = new VisualisationManager(eventSet, deviceManager, frameRenderer, consoleTextBuffer, dynamicSource, addressShapeMap, shape, initializer.getShapeBuilder(),shutdownAnnouncer);
		VisualisationManager visualisationManager 
			= new VisualisationManager(
					eventSet, deviceManager, 
					frameRenderer, consoleTextBuffer, 
					dynamicSource, addressShapeMap, 
                    initializer.getVisualisationStartTime(),
					initializer.getVisualizationParameters(), //shape,
					initializer.getShapeBuilder(),
					shutdownAnnouncer);
		visualisationManager.scheduleFirstEvent();
		//.addShutdownListener(deviceManager);
		//network.initialize(deviceManager, new ReceiveSchedulerImpl(eventSet), deviceManager, simulationClock);
		//networkRenderer.initialize(network, deviceManager, network, deviceManager);
		
		DynamicScheduler scheduler = new DefaultDynamicScheduler();
		DynamicInterpreter interpreter = new DefaultDynamicInterpreter(scheduler, deviceManager, eventSet);
		scheduler.initialize(interpreter, dynamicSource);
		scheduler.scheduleNextEvent();
	}


	/**
     * 
     * TODO: comment method
	 */

	public void run() {
		
		long startTimeMillis = System.currentTimeMillis();
        if (initializer.printTimeToConsole()>0) runPrint(initializer.printTimeToConsole());
        else runSimple();
       // initializer.getGlobalShutdownAnnouncer().shutdown();
		long endTimeMillis = System.currentTimeMillis();
		System.out.println("duration of simulation '" + initializer.getSimulationName() + "' in seconds: " + (endTimeMillis-startTimeMillis)/1000.0);
        //System.exit(0); // TODO: Das ist böse!!!!
	}
    
    private void runSimple() {
        while (!finished() && eventSet.hasNext()) {
            
            eventSet.handleNext();//next().handle();
            
        }
    }
    
    /**
     * TODO Comment method
     * @return
     */
    private boolean finished() {
        if (terminalCondition.reached()&&!shutdown){
            initializer.getGlobalShutdownAnnouncer().shutdown();
            shutdown=true;
            return false;    
        }
        return terminalConditionDevice.reached();
        
    }


    private void runPrint(double printInc) {
        double sysOutTime = 0;
        while (!finished()&& eventSet.hasNext()) {
            if ((long) eventSet.getTime() >= sysOutTime) {
                StringBuffer timeStr = new StringBuffer(25);
                timeStr.append("CurrentTime: ");
                double time = eventSet.getTime();
                int msec = ((int) (time * 60.0)) % 60;
                int sec = ((int) time) % 60;
                int min = ((int) (time / 60)) % 60;
                int hour = (int) time / 3600;

                timeStr.append(hour);
                timeStr.append(".");
                if (min < 10)
                    timeStr.append("0");
                timeStr.append(min);
                timeStr.append(":");
                if (sec < 10)
                    timeStr.append("0");
                timeStr.append(sec);
                timeStr.append(":");
                if (msec < 10)
                    timeStr.append("0");
                timeStr.append(msec);
                timeStr.append(" (");
                timeStr.append((long) time);
                timeStr.append(")");

                consoleOutput.println(timeStr.toString());
                //sysOutTime += printInc;
                sysOutTime=((long)(time/printInc))*printInc+printInc;
            }
            eventSet.handleNext();//next().handle();
        }

    }
  //  addAsyncEvent()



	/**
	 * @return
	 */
	public GlobalKnowledge getGlobalKnowledge() {
		return deviceManager.getGlobalKnowledge();
	}

	/**
	 * @return
	 */
	public ConsoleTextBuffer getConsoleTextBuffer() {
		return consoleTextBuffer;
	}

    /**
     * TODO: comment method 
     * @return
     * 
     */
    public DefaultServiceUnit getServiceUnit() {
        return deviceManager.getGlobalServiceUnit();
        
    }


    /**
     * TODO Comment method
     * @return
     */
    public DeviceServiceManager getServiceManager() {
        return deviceManager.getGlobalDevice().getServiceManager();
    }

}
