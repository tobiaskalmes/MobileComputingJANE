/*
 * Created on 22.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.simulation.kernel;


/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TerminalCondition implements Condition {

    private boolean reached;

    public TerminalCondition() {
        reached = false;
    }

    public boolean reached() {
        return reached;
    }
    
    public void setTrue() {
        reached = true;
    }

}
