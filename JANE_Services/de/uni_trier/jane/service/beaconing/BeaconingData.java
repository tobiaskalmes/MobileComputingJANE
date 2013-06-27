/*
 * Created on 07.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
// TODO: benennung???
public class BeaconingData extends ReceivedDataMap {

	/**
	 * @param sender
	 * @param timeStamp
	 * @param dataMap
	 */
	public BeaconingData(LinkLayerInfo receiveInfo, double timeStamp,  double valitityDelta, DataMap dataMap) {
		super(receiveInfo, timeStamp, valitityDelta,  dataMap);
		// TODO Auto-generated constructor stub
	}
	
    // TODO comment
//    public BeaconingData copy() {
//    	return new BeaconingData(sender, timeStamp, dataMap.copy());
//    }

}
