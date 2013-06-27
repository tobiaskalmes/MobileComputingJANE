/*
 * Created on 23.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.wireless_media;

import de.uni_trier.jane.basetypes.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Media {

    public void startTransmission(DeviceID device, double watt);
    public void finishTransmission(DeviceID device);

}
