package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.network.link_layer.*;

// Mit dieser Klasse lässt sich der dem Frame-Typ entsprechende Header erzeugen.
// Der MAC-Header enthält Informationen über die Übertragungszeit, Datenrate, Typ,
// Informationen über das enthaltene Fragment und die Reservierungs-Dauer für das Medium 
public class MacHeader extends LinkLayerInfoImplementation {

    public static final int DATA = 1;
    public static final int RTS = 2;
    public static final int ACK_CTS = 3;
    public static final int BEACON = 4;
    public static final int BROADCAST = 5;

    private double txTime;
    private double dataRate;
    private int subtype;
    private boolean moreFragments;
    private boolean retry;
    private double duration;
    private int fragmentNumber;
    private int sequenceNumber;
    
    //NON Standart! the sender decides that also others receive this message
    private boolean promiscous;

    public static MacHeader createBroadcastHeader(Address sender, Address receiver, double txTime, double dataRate) {        
		return new MacHeader(sender, receiver, txTime, dataRate, BROADCAST, false, false, 0, 0, 0, false,false);
    } 

    public static MacHeader createBeaconHeader(Address sender, Address receiver, double txTime, double dataRate) {        
		return new MacHeader(sender, receiver, txTime, dataRate, BEACON, false, false, 0, 0, 0, false,false);
    } 

    public static MacHeader createDataHeader(Address sender, Address receiver, double txTime, double dataRate, boolean moreFragments, boolean retry, double duration, int fragmentNumber, int sequenceNumber, boolean promisc) {        
		return new MacHeader(sender, receiver, txTime, dataRate, DATA, moreFragments, retry, duration, fragmentNumber, sequenceNumber, true,promisc);
    } 

    public static MacHeader createRTSHeader(Address sender, Address receiver, double txTime, double dataRate, double duration) {        
		return createControlHeader(sender, receiver, txTime, dataRate, RTS, duration);		
    } 

    public static MacHeader createACK_CTSHeader(Address sender, Address receiver, double txTime, double dataRate, double duration) {        
		return createControlHeader(sender, receiver, txTime, dataRate, ACK_CTS, duration);		
    } 

    public static MacHeader createControlHeader(Address sender, Address receiver, double txTime, double dataRate, int subtype, double duration) {        
		return new MacHeader(sender, receiver, txTime, dataRate, subtype, false, false, duration, 0, 0, true,false);
    } 

    /**
     * 
     * Constructor for class <code>MacHeader</code>
     *
     * @param sender
     * @param receiver
     * @param txTime
     * @param dataRate
     * @param subtype
     * @param moreFragments
     * @param retry
     * @param duration
     * @param fragmentNumber
     * @param sequenceNumber
     * @param isUnicastMessage
     * @param promisc
     */
    public MacHeader(Address sender, Address receiver, 
            double txTime, double dataRate, 
            int subtype, boolean moreFragments, 
            boolean retry, double duration, 
            int fragmentNumber, int sequenceNumber, 
            boolean isUnicastMessage, boolean promisc) {        
		super(sender, receiver, isUnicastMessage,-1);
		this.txTime = txTime;
		this.dataRate = dataRate;
		this.subtype = subtype;
		this.moreFragments = moreFragments;
		this.retry = retry;
		this.duration = duration;
		this.fragmentNumber = fragmentNumber;
		this.sequenceNumber = sequenceNumber;
        promiscous=promisc;
    } 

    public double getDuration() {        
		return duration;
    } 

    public double getTxTime() {        
		return txTime;
    } 

    public double getDataRate() {        
		return dataRate;
    } 

    public int getFrameType() {        
		return subtype;
    } 

    public boolean getMoreFragments() {        
		return moreFragments;
    } 

    public boolean getRetry() {        
		return retry;
    } 

    public int getFragmentNumber() {        
		return fragmentNumber;
    } 

    public int getSequenceNumber() {        
		return sequenceNumber;
    }

    public boolean isPromiscous() {
        return promiscous;
    }
    
    public LinkLayerInfo copy() {
     
        return new MacHeader(getSender(),getReceiver(),txTime,dataRate,subtype,moreFragments,retry,
                duration,fragmentNumber,sequenceNumber,isUnicastMessage(),promiscous);
    }

    public void setSignalStrength(double receivedPowerLevel) {
        signalStrength=receivedPowerLevel;  
    }

    /**
     * TODO Comment method
     * @param macAddress
     */
    public void setReceiver(Address macAddress) {
        receiver=macAddress;
        
    } 
 }
