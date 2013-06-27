/*
 * Created on Sep 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.link_layer.packetNetwork;






/**
 * @author daniel
 *
 * 
 * Packetpart: packetnumber | sumpackets | sequencenumber | data 
 * 
 */
public class SendPacket {
	
	
	public static final int PACKET_NR=0;
	public static final int SUM_PACKETS=1;
	public static final int SEQUENCE_NR=2;
	//public static int MAX_UDP_PACKETSIZE=64550;
	private byte[] data;
	private int dataLeft;
	private byte currentPacket;
	private byte maxPacket;
	private byte sequenceNumber;
	//public static final int PACKETSIZE = MAX_UDP_PACKETSIZE+3;
    private int maxUDPPacketSize;
    private int maxPacketSize;

	/**
	 * @param data
	 */
	public SendPacket(byte[] data, byte sequenceNumber,int maxUDPPacketSize) {
		this.maxUDPPacketSize=maxUDPPacketSize;
		maxPacketSize=maxUDPPacketSize-3;
		this.data=data;
		this.sequenceNumber=sequenceNumber;
		dataLeft=data.length;
		int tmp=(dataLeft/maxPacketSize)+1;
		if (tmp>128) throw new IllegalStateException("Message too long");
		maxPacket=(byte)tmp;
		currentPacket=0;	
	}

	/**
	 * @return
	 */
	public boolean hasData() {
		return currentPacket<maxPacket;
	}

	/**
	 * @return
	 */
	public byte[] getNextData() {
		if (currentPacket<0) throw new IllegalStateException("No data left!");
		byte[] nextPacket;
		if (dataLeft<maxPacketSize){
			nextPacket=new byte[dataLeft+3];
			dataLeft=0;
		}else{
			nextPacket=new byte[maxUDPPacketSize];
			dataLeft-=maxPacketSize;
		}
		nextPacket[PACKET_NR]=currentPacket;
		nextPacket[SUM_PACKETS]=maxPacket;
		nextPacket[SEQUENCE_NR]=sequenceNumber;
		for (int i=3;i<nextPacket.length;i++){
			nextPacket[i]=data[i-3+currentPacket*maxPacketSize];
		}
		currentPacket++;
		return nextPacket;
	}

	
}
