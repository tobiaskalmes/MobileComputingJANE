/*****************************************************************************
 * 
 * ${Id}$
 *  
 ***********************************************************************
 *  
 * JANE - The Java Ad-hoc Network simulation and evaluation Environment
 *
 ***********************************************************************
 *
 * Copyright (C) 2002-2006
 * Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * Systemsoftware and Distrubuted Systems
 * University of Trier 
 * Germany
 * http://syssoft.uni-trier.de/jane
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
package de.uni_trier.jane.sgui; 

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;
import de.uni_trier.jane.simulation.service.GlobalService;
import de.uni_trier.jane.simulation.visualization.Frame;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class DemoScripter implements GuiScripter, GlobalService {



    /**
     * @author goergen
     *
     * TODO comment class
     */
    private class FinishState implements State {



        public State nextFrame(Frame frame) {
            return this;
        }



    }

    private SimulationMainFrame mainFrame;
    private State state;
    private interface State{
        State nextFrame(Frame frame);
    }
    
    private class StartState implements State{
        public State nextFrame(Frame frame) {
            if (frame.getTime()>10){
                mainFrame.pause();
                return new State1();
            }
          
            return this;
        }
    }
    
    private class State1 implements State{
        public State nextFrame(Frame frame) {
            if (frame.getTime()>40){
                mainFrame.pause();
                return new FinishState();
            }
          
            return this;
        }
    }
    
    

    /**
     * Constructor for class <code>DemoScripter</code>
     * @param mainFrame
     */
    public DemoScripter(SimulationMainFrame mainFrame) {
        this.mainFrame=mainFrame;
        state=new StartState();
    }

    public void nextFrame(Frame frame) {
        state=state.nextFrame(frame);

    }

    public void start(GlobalOperatingSystem globalOperatingSystem) {
        // TODO Auto-generated method stub
        
    }

    public ServiceID getServiceID() {
        // TODO Auto-generated method stub
        return null;
    }

    public void finish() {
        // TODO Auto-generated method stub
        
    }

    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

    public void getParameters(Parameters parameters) {
        // TODO Auto-generated method stub
        
    }


}
