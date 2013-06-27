package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.signaling.SignalListener;

// Interface f�r die Physical-Layer, das als Schnittstelle zur MAC-Layer dient
// Enth�lt Funktionen f�r die �bertragung
public interface MacToPhysicalLayerInterface extends SignalListener {

    public void startTransmission(MacHeader macHeader, Frame frame, double signalFraction);

    public void endTransmission();
}


