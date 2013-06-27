package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.DeviceIDSet;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.SignalListener;

// Interface f�r ein h�heres Protokoll, das als Schnittstelle zur MAC-Layer dient
// Hier�ber erh�lt man Informationen die Nachrichten und� ber verbundene Ger�te
public interface MacToUpperLayerInterface extends SignalListener {

    public void receive(LinkLayerInfo info, LinkLayerMessage message);

    public void sendFailed(LinkLayerMessage message);

    public void sendFinished(LinkLayerMessage message);

    public void connectedDevices(DeviceIDSet connectedDeviceIDSet);
}


