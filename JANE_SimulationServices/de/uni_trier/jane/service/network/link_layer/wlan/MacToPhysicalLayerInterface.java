package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.signaling.SignalListener;

// Interface für die Physical-Layer, das als Schnittstelle zur MAC-Layer dient
// Enthält Funktionen für die Übertragung
public interface MacToPhysicalLayerInterface extends SignalListener {

    public void startTransmission(MacHeader macHeader, Frame frame, double signalFraction);

    public void endTransmission();
}


