package de.uni_trier.jane.service.dominating_set;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.*;

/**
 * A dominating set construction listener is notified by the dominating set
 * construction method when dominating set membership has been changed.
 */
public interface DominatingSetListener {

    /**
     * Notify the listener that the dominating set membership of this node has
     * been changed.
     * @param member a flag indicating the current membership of this node
     */
    public void updateMembership(boolean membership);
    
}
