package de.uni_trier.jane.hybrid.util;

import java.net.InetAddress;

import de.uni_trier.jane.basetypes.DeviceID;

public class HybridConf {
	private InetAddress serverAddress;
	private DeviceID deviceID;
	public DeviceID getDeviceID() {
		return deviceID;
	}
	public InetAddress getServerAddress() {
		return serverAddress;
	}
	/**
	 * @param deviceid
	 * @param address
	 */
	public HybridConf(DeviceID deviceid, InetAddress address) {
		super();
		// TODO Auto-generated constructor stub
		deviceID = deviceid;
		serverAddress = address;
	}

}
