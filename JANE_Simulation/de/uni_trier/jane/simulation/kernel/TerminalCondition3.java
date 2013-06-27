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
public class TerminalCondition3 extends TerminalCondition {

    private boolean reached;
    private Condition condition1;
    private Condition condition2;

    public TerminalCondition3(Condition condition1, Condition condition2) {
        this.condition1=condition1;
        this.condition2=condition2;
        reached = false;
    }

    public boolean reached() {
        return reached||condition1.reached()||condition2.reached();
    }
    
    public void setTrue() {
        reached = true;
    }

}
