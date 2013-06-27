/*
 * Created on 01.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.operatingSystem.manager;

import java.util.*;

import de.uni_trier.jane.service.operatingSystem.*;


public class ActionScheduler { // TODO finish listener implementieren und bei service manager anmelden??? --> Eigentlich sollte dies in den Managern geschehen!!!
//	 TODO: prioritäten berücksichtigen

    private LinkedList actionList;
    
    public ActionScheduler() {
        actionList = new LinkedList();
    }

    public void addAction(Action action) {
        actionList.addLast(action);
    }
    
    public boolean isEmpty() {
        return actionList.isEmpty();
    }
    
    public Action nextAction() {
        return (Action)actionList.removeFirst();
    }
    
}