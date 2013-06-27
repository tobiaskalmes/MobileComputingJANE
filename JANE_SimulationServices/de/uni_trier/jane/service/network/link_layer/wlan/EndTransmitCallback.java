package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.signaling.SignalListener;

// Dieses Callback wird benötigt, damit ein Gerät nach dem Ende einer Übertragung
// in den richtigen Zustand wechselt
public interface EndTransmitCallback extends SignalListener {

	public void stillBusy();
}


