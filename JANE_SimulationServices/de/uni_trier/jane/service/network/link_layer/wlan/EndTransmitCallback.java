package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.signaling.SignalListener;

// Dieses Callback wird ben�tigt, damit ein Ger�t nach dem Ende einer �bertragung
// in den richtigen Zustand wechselt
public interface EndTransmitCallback extends SignalListener {

	public void stillBusy();
}


