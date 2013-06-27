package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.signaling.SignalListener;

// Interface f�r das Medium, das als Schnittstelle zur Physical-Layer dient
// Hier�ber werden das Anfang und das Ende von �bertragungen dem Medium gemeldet
// und neue Ger�te m�ssen sich f�r das Medium registrieren
public interface MediumInterface extends SignalListener {

    public static final int IDLE = 1;
    public static final int BUSY = 2;
    
    public void newTransmission(double signalStrength, MacHeader macHeader, Frame frame);

    public void endOfTransmission(EndTransmitCallback callback);

    public void registerDevice(Mib mib);
}


