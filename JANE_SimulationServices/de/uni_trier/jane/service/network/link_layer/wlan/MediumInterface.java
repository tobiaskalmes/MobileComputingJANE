package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.signaling.SignalListener;

// Interface für das Medium, das als Schnittstelle zur Physical-Layer dient
// Hierüber werden das Anfang und das Ende von Übertragungen dem Medium gemeldet
// und neue Geräte müssen sich für das Medium registrieren
public interface MediumInterface extends SignalListener {

    public static final int IDLE = 1;
    public static final int BUSY = 2;
    
    public void newTransmission(double signalStrength, MacHeader macHeader, Frame frame);

    public void endOfTransmission(EndTransmitCallback callback);

    public void registerDevice(Mib mib);
}


