package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.DeviceIDSet;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.SignalListener;

// Interface für ein höheres Protokoll, das als Schnittstelle zur MAC-Layer dient
// Hierüber erhält man Informationen die Nachrichten undü ber verbundene Geräte
public interface MacToUpperLayerInterface extends SignalListener {

    public void receive(LinkLayerInfo info, LinkLayerMessage message);

    public void sendFailed(LinkLayerMessage message);

    public void sendFinished(LinkLayerMessage message);

    public void connectedDevices(DeviceIDSet connectedDeviceIDSet);
}


