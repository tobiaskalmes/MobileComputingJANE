package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;

import de.uni_trier.jane.signaling.SignalListener;

// Interface für die MAC-Layer, das für die Verarbeitung von empfangenen Frames benötogt wird
public interface MacLayerInterface extends SignalListener {

    public void handleDataFrame(MacHeader macHeader, LinkLayerMessage message);

    public void handleACKFrame(MacHeader macHeader);

    public void handleRTSFrame(MacHeader macHeader);

    public void handleCTSFrame(MacHeader macHeader);

    public void handleBeaconFrame(MacHeader macHeader);
}


