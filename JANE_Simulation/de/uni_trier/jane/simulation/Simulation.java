/*****************************************************************************
 * 
 * Simulation.java
 * 
 * $Id: Simulation.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.simulation.kernel.*;

/**
 * Super class for all JANE Simulations
 */
public abstract class Simulation implements SimulationServiceFactory {

	// increase version number whenever there were essential changes in any relevant simulation class
	protected static final String VERSION = "Simulation V0.4";

    /**
     * Method for initializing the simulation environment.
     * The simulation is initialized with default parameters, to change this change the parameters object.
     *  
     * @param parameters 	containing the default simulation parameters
     */
    public abstract void initSimulation(SimulationParameters parameters);
    
    // TODO:
    // Eignetlich möchte man auch noch eine Methode haben, die bei Ende der Simulation aufgerufen wird.
    // Das braucht man z.B. um eventuell geöffnete Dateien wirder zu schliessen.
    // dg:
    // Bei jedem Service wird finish() am Ende aufgerufen - 
    // Datein lassen sich auch nach run schließen
    
    /**
     * Start the simulation
     */
    public void run(){
        DefaultSimulationParameters parameters= new DefaultSimulationParameters();
        initSimulation(parameters);
       
       // /*SimulationMainFrame mainFrame = */new SimulationMainFrame();//this, parameters);

        ApplicationSimulation simulation=new ApplicationSimulation(parameters,this);
        simulation.run();
    }
    

}
