/*
 * Created on Sep 14, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.link_layer.packetNetwork;





/**
 * @author daniel
 *
 * Packetpart: packetnumber | sumpackets | sequencenumber | data 
 */
public class ReceivePacket {
	
	
	
	private byte sumPackets;
	private byte sequenceNumber;
	private double receiveTime;
	private byte[][] datas;
	private int sumdata;
    private int maxUDPPacketSize;
	

	/**
     * 
     * Constructor for class <code>ReceivePacket</code>
     * @param firstData
     * @param packetSize
     * @param receiveTime
     * @param maxUDPPacketSize
	 */
	public ReceivePacket(byte[] firstData,int packetSize,double receiveTime,int maxUDPPacketSize) {
	    this.maxUDPPacketSize=maxUDPPacketSize;
		sumPackets=firstData[SendPacket.SUM_PACKETS];
		sequenceNumber=firstData[SendPacket.SEQUENCE_NR];
		this.receiveTime=receiveTime;
		datas=new byte[sumPackets][];
		if (packetSize<firstData.length){
			datas[firstData[SendPacket.PACKET_NR]]=new byte[packetSize];
			System.arraycopy(firstData,0,datas[firstData[SendPacket.PACKET_NR]],0,packetSize);
		}else{
			datas[firstData[SendPacket.PACKET_NR]]=firstData;
		}
		sumdata=packetSize-3;
		
		
		
	}

	/**
	 * @param followingData
	 * @param receiveTime
	 */
	public boolean addPacket(byte[] followingData, int packetSize, double receiveTime) {
		
		if (sequenceNumber!=followingData[SendPacket.SEQUENCE_NR]) return false;
		if (sumPackets!=followingData[SendPacket.SUM_PACKETS]) {
			System.out.println("Packet is corrupt");
			return false;
		}
		this.receiveTime=receiveTime;
		if (datas[followingData[SendPacket.PACKET_NR]]==null||datas[followingData[SendPacket.PACKET_NR]].length<=0){
			if (packetSize<followingData.length){
				datas[followingData[SendPacket.PACKET_NR]]=new byte[packetSize];
				System.arraycopy(followingData,0,datas[followingData[SendPacket.PACKET_NR]],0,packetSize);
			}else{
				datas[followingData[SendPacket.PACKET_NR]]=followingData;
			}
			
			sumdata+=packetSize-3;
		}
		return true;
	}

	/**
	 * @return Returns the receiveTime.
	 */
	public double getReceiveTime() {
		return receiveTime;
	}

	/**
	 * @return
	 */
	public boolean isComplete() {
		for (int i=0;i<sumPackets;i++){
			if (datas[i]==null||datas[i].length<=0) return false;
		}
		return true;
	}

	/**
	 * @return
	 */
	public byte[] getCompleteData() {
		byte[] returnData=new byte[sumdata];
		for (int i=0;i<sumPackets;i++){
			System.arraycopy(datas[i],3,returnData,i*(maxUDPPacketSize-3),datas[i].length-3);
		}
		return returnData;
	}
}
