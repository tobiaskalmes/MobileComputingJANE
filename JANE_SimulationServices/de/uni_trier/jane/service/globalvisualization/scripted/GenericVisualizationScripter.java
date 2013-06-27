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

import java.util.Iterator;
import java.util.TreeMap;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.sgui.GuiScripter;
import de.uni_trier.jane.sgui.SimulationMainFrame;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;
import de.uni_trier.jane.simulation.service.GlobalService;
import de.uni_trier.jane.simulation.visualization.Frame;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class GenericVisualizationScripter implements GuiScripter, GlobalService {
    
    
    
    private TreeMap treeMap;
    private SimulationMainFrame mainFrame;
    private GlobalOperatingSystem globalOperatingSystem;
    

    
    public GenericVisualizationScripter(SimulationMainFrame mainFrame){
        this.mainFrame=mainFrame;
        treeMap=new TreeMap();
    }
    

    public void addEvent(ScriptEvent event){
        treeMap.put(new Double(event.getExecutionTime()),event);
    }
    

    public void nextFrame(Frame frame) {
        if (treeMap.isEmpty()) return;
        Double nextTime=(Double)treeMap.firstKey();
        if (frame.getTime()>nextTime.doubleValue()){
            boolean pauseVisualization=false;
            boolean setSpeed=false;
            Iterator iterator=treeMap.values().iterator();
            ScriptEvent element=null;
            int speed=0;
            while (iterator.hasNext()) {
                element = (ScriptEvent) iterator.next();
                if (element.getExecutionTime()<frame.getTime()){
                    iterator.remove();
                    switchServiceOn(element.getServicesToVisualize(),element.getServicesToHide());
                    
                    if (element.pauseVisualization()){
                        pauseVisualization=true;    
                    }
                    if (element.setSpeed()){
                        setSpeed=true;
                        speed=element.getSpeedStep();
                    }
                    
                }else{
                    if (pauseVisualization){
                        mainFrame.pause();
                    }
                    if (setSpeed){
                        mainFrame.setSpeed(speed);
                    }
                    return;
                }
                
                
            }
            if (pauseVisualization){
                mainFrame.pause();
            }
            if (setSpeed){
                mainFrame.setSpeed(speed);
            }
        }


    }


    /**
     * 
     * TODO Comment method
     * @param servicesToVisualize
     * @param servicesToHide
     */


    private void switchServiceOn(Class[] servicesToVisualize, Class[] servicesToHide) {
        if (servicesToVisualize==null&&servicesToHide==null) return;
        DeviceIDIterator iterator=globalOperatingSystem.getGlobalKnowledge().getNodes().iterator();
        while (iterator.hasNext()) {
            DeviceID element =  iterator.next();

            if (servicesToHide!=null){
                for (int i=0;i<servicesToHide.length;i++){
                    ServiceID[] serviceIDs=globalOperatingSystem.getServiceIDs(element,servicesToHide[i]);
                    for (int j=0;j<serviceIDs.length;j++){
                        globalOperatingSystem.setVisualized(element,serviceIDs[j],false);
                    }
                }
            }
            if (servicesToVisualize!=null){
                for (int i=0;i<servicesToVisualize.length;i++){
                    ServiceID[] serviceIDs=globalOperatingSystem.getServiceIDs(element,servicesToVisualize[i]);
                    for (int j=0;j<serviceIDs.length;j++){
                        globalOperatingSystem.setVisualized(element,serviceIDs[j],true);
                    }
                }
            }
        }
     
        
    }


    public void start(GlobalOperatingSystem globalOperatingSystem) {
        this.globalOperatingSystem=globalOperatingSystem;
        
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
