/*
 * Created on Dec 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.physical_layer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.signaling.*;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface PhysicalLayerMessage extends Dispatchable, Sendable{
    public void handle(SignalListener service);

}
