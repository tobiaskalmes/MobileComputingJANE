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
package de.uni_trier.jane.service.globalvisualization.scripted; 



/**
 * @author goergen
 *
 * TODO comment class
 */
public  class ScriptEvent {
    private double executionTime;
    private boolean pauseVisualization;
    private Class[] servicesToVisualize;
    private Class[] servicesToHide;
    private int speedStep;
    private boolean setSpeed;
    
    
    /**
     * 
     * Constructor for class <code>ScriptEvent</code>
     * @param executionTime
     * @param pauseVisualization
     * @param servicesToVisualize
     * @param servicesToHide
     */
    public ScriptEvent(double executionTime, boolean pauseVisualization, Class[] servicesToVisualize,Class[] servicesToHide) {
        this(executionTime,pauseVisualization,0,servicesToVisualize,servicesToHide);
        setSpeed=false;
    }
    
    /**
     * 
     * Constructor for class <code>ScriptEvent</code>
     *
     * @param executionTime
     * @param pauseVisualization
     * @param speedStep
     * @param servicesToVisualize
     * @param servicesToHide
     */
    public ScriptEvent(double executionTime, boolean pauseVisualization,int speedStep, Class[] servicesToVisualize,Class[] servicesToHide) {
        this.executionTime = executionTime;
        this.pauseVisualization = pauseVisualization;
        this.servicesToVisualize = servicesToVisualize;
        this.servicesToHide=servicesToHide;
        this.speedStep=speedStep;
        setSpeed=true;
        
    }
    
    public boolean setSpeed(){
        return setSpeed;
    }
    
    /**
     * @return Returns the speedStep.
     */
    public int getSpeedStep() {
        return speedStep;
    }
    /**
     * @return Returns the executionTime.
     */
    public double getExecutionTime() {
        return this.executionTime;
    }
    /**
     * @return Returns the pauseVisualization.
     */
    public boolean pauseVisualization(){
        return pauseVisualization;
    }

    /**
     * @return Returns the servicesToVisualize.
     */
    public Class[] getServicesToVisualize() {
        return this.servicesToVisualize;
    }
    
    /**
     * @return Returns the servicesToHide.
     */
    public Class[] getServicesToHide() {
        return this.servicesToHide;
    }
    
    
    
    

}
