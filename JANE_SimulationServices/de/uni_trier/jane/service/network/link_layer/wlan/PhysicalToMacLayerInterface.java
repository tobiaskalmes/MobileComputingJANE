package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.signaling.SignalListener;

// Interface f�r die MAC-Layer, das als Schnittstelle zur Physical-Layer dient
// Enth�lt Funktionen f�r den Zustand des Ger�tsm zum Empfang eines Frames
// und zum Erkennen einer Kollision/Fehlers
public interface PhysicalToMacLayerInterface extends SignalListener {

    public void setPhysicalState(int physicalState);

    public void receiveFrame(MacHeader macHeader, Frame frame);

    public void collision();
}


