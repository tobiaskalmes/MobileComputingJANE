package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.signaling.SignalListener;

// Interface für die Physical-Layer, das als Schnittstelle zum Medium dient
// Enthält Funktionen für das Empfangen von Nachrichten und auftretenden Kollisionen
public interface PhysicalLayerInterface extends SignalListener {

    public static final int CCA = 1;
    public static final int TRANSMIT = 2;
    public static final int RECEIVE = 3;

    public void startReceipt(MacHeader macHeader, Frame frame);

    public void endReceipt();

    public void collision();
}


