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
public class TerminalCondition2 extends TerminalCondition {

    private boolean reached;
    private Condition condition;

    public TerminalCondition2(Condition condition) {
        this.condition=condition;
        reached = false;
    }

    public boolean reached() {
        return reached||condition.reached();
    }
    
    public void setTrue() {
        reached = true;
    }

}
