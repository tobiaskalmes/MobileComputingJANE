/*****************************************************************************
* 
* Mib.java
* 
* $Id: Mib.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
*
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006 
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
* 
* This program is free software; you can redistribute it and/or 
* modify it under the terms of the GNU General Public License 
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
* General Public License for more details.
* 
* You should have received a copy of the GNU General Public License 
* along with this program; if not, write to the Free Software 
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
* 
*****************************************************************************/
package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.SimulationLinkLayerAddress;

import java.lang.reflect.*;
import java.util.HashMap;

// Die Klasse Mib enthält alle benötigten Parameter, Konstanten und Zähler
// für die Simulation von WLAN
public class Mib {
    //public static double timeFactor = 1000000.0;
    public static double timeFactor = 1000000.0;
    public static double moveTimeout = 100000;
	public static final Address BROADCAST = new SimulationLinkLayerAddress(new SimulationDeviceID(-2));
 
// Physical attributes
// Default Werte zu 802.11b
    private int aSlotTime = 20; // microseconds
    private int aSIFSTime = 10; // microseconds
    private int aCWMin = 31;
    private int aCWMax = 1023;
    private int aPreambleLength = 144; // microseconds
    private int aPLCPHeaderTime = 48; // microseconds
    
// Default Werte gängiger 802.11b Wlan Karten
    private double dataRate = 11; // Mbit/s; nur Werte 1, 2, 5.5, 11 hier erlaubt
    private double signalStrength = 15; // dBm
    private double antennaGain = 1; // Verstärkungsfaktor (1 entspricht 0 dB)
    private HashMap receiveThresholdMap;
    private HashMap snrThresholdMap;

// MAC attributes
// Default Werte
    //private double beaconIntervall = 100 * 1024; // microseconds
    private double beaconIntervall = 100 * 1024; // microseconds
    private Address macAddress;
    private int rtsThreshold = 2347; // bytes
    private int fragmentationThreshold = 2346; // bytes
    private int shortRetryLimit = 7; // 7
    private int longRetryLimit = 4; // 4
    private int maxReceiveLifetime = 51200 * 1024; // microseconds
    private int maxTransmitMSDULifetime = 512 * 1024; // microseconds
    
    private boolean promiscous =false;

// Counters
    private int transmittedFragmentCount; // TODO überall richtig gesetzt? -> Broadcast
    //private int multicastTransmittedFragmentCount;
    private int failedCount;
    private int retryCount;
    private int multipleRetryCount;
    private int frameDuplicateCount;
    private int rtsSuccessCount;
    private int rtsFailureCount;
    private int ackFailureCount;
    private int receivedFragmentCount;// TODO überall richtig gesetzt? -> Broadcast
    //private int multicastReceivedFrameCount;
    private int fcsErrorCount;
    private int transmittedFrameCount;
	private double throughput;
    
    public Mib copy(){
        Mib mib=new Mib();
        Class mibClass=getClass();
        Field[] fields=mibClass.getDeclaredFields();
        for (int i=0;i<fields.length;i++){
            fields[i].setAccessible(true);
            if (Modifier.isFinal(fields[i].getModifiers())) continue;
            try {
                fields[i].set(mib,fields[i].get(this));
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return mib;
        
    }

    public Mib() {        
		// representative Empfänger-Empfindlichkeiten der 802.11b Karte 
		// Lucent/Orinoco PCMCIA Silver Gold 
		receiveThresholdMap = new HashMap();
		receiveThresholdMap.put(new Double(1), new Double(-94));
		receiveThresholdMap.put(new Double(2), new Double(-91));
		receiveThresholdMap.put(new Double( 5.5), new Double(-87));
		receiveThresholdMap.put(new Double(11), new Double(-82));
		snrThresholdMap = new HashMap();
		snrThresholdMap.put(new Double(1), new Double(4));
		snrThresholdMap.put(new Double(2), new Double(7));
		snrThresholdMap.put(new Double( 5.5), new Double(11));
		snrThresholdMap.put(new Double(11), new Double(16));
		transmittedFragmentCount = 0;
		failedCount = 0;
		retryCount = 0;
		multipleRetryCount = 0;
		frameDuplicateCount = 0;
		rtsSuccessCount = 0;
		rtsFailureCount = 0;
		ackFailureCount = 0;
		receivedFragmentCount = 0;
		fcsErrorCount = 0;
		transmittedFrameCount = 0;		
    } 
    
    /**
     * @param moveTimeout The moveTimeout to set.
     */
    public void setMoveTimeout(double moveTimeoutInMicroseonds) {
        Mib.moveTimeout = moveTimeout;
    }

    public void initPhysicalMib(int aSlotTime, int aSIFSTime, int aCWMin, int aCWMax, int aPreambleLength, int aPLCPHeaderTime, double dataRate, double signalStrength, double antennaGain, HashMap receiveThresholdMap, HashMap snrThresholdMap) {        
		this.aSlotTime = aSlotTime;
		this.aSIFSTime = aSIFSTime;
		this.aCWMin = aCWMin;
		this.aCWMax = aCWMax;
		this.aPreambleLength = aPreambleLength;
		this.aPLCPHeaderTime = aPLCPHeaderTime;
		this.dataRate = dataRate;
		this.signalStrength = signalStrength;
		this.antennaGain = antennaGain;
		this.receiveThresholdMap = receiveThresholdMap;
		this.snrThresholdMap = snrThresholdMap;
    } 

    public void initMacMib(double beaconIntervall, int rtsThreshold, int fragmentationThreshold, int shortRetryLimit, int longRetryLimit, int maxReceiveLifetime, int maxTransmitMSDULifetime) {        
		this.beaconIntervall = beaconIntervall;
		this.rtsThreshold = rtsThreshold;
		this.fragmentationThreshold = fragmentationThreshold;
		this.shortRetryLimit = shortRetryLimit;
		this.longRetryLimit = longRetryLimit;
		this.maxReceiveLifetime = maxReceiveLifetime;
		this.maxTransmitMSDULifetime = maxTransmitMSDULifetime;
    } 

    public void setBeaconIntervall(double beaconIntervall) {        
		this.beaconIntervall = beaconIntervall;
    } 

    public double getBeaconIntervall() {        
		return MacLayer80211.roundUsec(beaconIntervall / timeFactor);
    } 

    public void setPreambleLength(int preambleLength) {        
		this.aPreambleLength = preambleLength;
    } 

    public int getPreambleLength() {        
	
		return aPreambleLength;
    } 

    public void setPLCPHeaderTime(int plcpHeaderTime) {        
		this.aPLCPHeaderTime = plcpHeaderTime;
    } 

    public int getPLCPHeaderTime() {        
		
		return aPLCPHeaderTime;
    } 

    public void setSlotTime(int slotTime) {        
		this.aSlotTime = slotTime;
    } 

    public double getSlotTime() {        
		return MacLayer80211.roundUsec(aSlotTime / timeFactor);
    } 

// IFS-Inervalle: siehe 9.2.10
    public void setSIFS(int sifsTime) {        
		this.aSIFSTime = sifsTime;
    } 

    public double getSIFS() {        
		return MacLayer80211.roundUsec(aSIFSTime / timeFactor); 
    } 

    public double getDIFS() {        
		return MacLayer80211.roundUsec((aSIFSTime + 2 * aSlotTime) / timeFactor); 
    } 

    public double getEIFS() {        
		// EIFS = SIFS + ACKTime + DIFS
		// wobei ACKTime = Übertragungsdauer eines ACKFrame mit 1 Mbps
		double eifsTime = aSIFSTime + (ACKFrame.size + aPreambleLength + aPLCPHeaderTime) + (aSIFSTime + 2 * aSlotTime);
		return MacLayer80211.roundUsec(eifsTime / timeFactor); 
    } 

    public void setMaxTransmitMSDULifetime(int maxTransmitMSDULifetime) {        
		this.maxTransmitMSDULifetime = maxTransmitMSDULifetime;
    } 

    public double getMaxTransmitMSDULifetime() {        
		return MacLayer80211.roundUsec(maxTransmitMSDULifetime / timeFactor);
    } 

    public void setMaxReceiveLifetime(int maxReceiveLifetime) {        
		this.maxReceiveLifetime = maxReceiveLifetime;
    } 

    public double getMaxReceiveLifetime() {        
		return MacLayer80211.roundUsec(maxReceiveLifetime / timeFactor);
    } 

    public void setMacAddress(Address macAddress) {        
		this.macAddress = macAddress;		
    } 

    public Address getMacAddress() {        
		return macAddress;
    } 

    public void setSignalStrength(double signalStrength) {        
		this.signalStrength = signalStrength;
    } 

    public double getSignalStrength() {        
		return signalStrength;
    } 

    public void setDataRate(double dataRate) {        
		this.dataRate = dataRate;
    } 

    public double getDataRate() {        
		return dataRate;
    } 

    public void setAntennaGain(double antennaGain) {        
		this.antennaGain = antennaGain;
    } 

    public double getAntennaGain() {        
		return antennaGain;
    } 

    public double getReceiveThreshold(double dataRate) {        
    	Double receiveThreshold = (Double) (receiveThresholdMap.get(new Double(dataRate)));
		if (receiveThreshold == null)
			throw new IllegalArgumentException("Falsche Datenrate");
    	return receiveThreshold.doubleValue();
    } 

    public double getSNRThreshold(double dataRate) {        
    	Double snrThreshold = (Double) (snrThresholdMap.get(new Double(dataRate)));
		if (snrThreshold == null)
			throw new IllegalArgumentException("Falsche Datenrate");
    	return snrThreshold.doubleValue();
    } 

    public void setFragmentationThreshold(int fragmentationThreshold) {        
		this.fragmentationThreshold = fragmentationThreshold;
    } 

    public int getFragmentationThreshold() {        
		return fragmentationThreshold;
    } 

    public void setRTSThreshold(int rtsThreshold) {        
		this.rtsThreshold = rtsThreshold;
    } 

    public int getRTSThreshold() {        
		return rtsThreshold;
    } 

    public void setShortRetryLimit(int shortRetryLimit) {        
		this.shortRetryLimit = shortRetryLimit;
    } 

    public int getShortRetryLimit() {        
		return shortRetryLimit;
    } 

    public void setLongRetryLimit(int longRetryLimit) {        
		this.longRetryLimit = longRetryLimit;
    } 

    public int getLongRetryLimit() {        
		return longRetryLimit;
    } 

    public void setCWMin(int aCWMin) {        
		this.aCWMin = aCWMin;
    } 

    public int getCWMin() {        
		return aCWMin;
    } 

    public void setCWMax(int aCWMax) {        
		this.aCWMax = aCWMax;
    } 

    public int getCWMax() {        
		return aCWMax;
    } 

    public void incTransmittedFragmentCount() {        
		transmittedFragmentCount++;
    } 

    public int getTransmittedFragmentCount() {        
		return transmittedFragmentCount;
    } 

    public void incFailedCount() {        
		failedCount++;
    } 

    public int getFailedCount() {        
		return failedCount;
    } 

    public void incRetryCount() {        
		retryCount++;
    } 

    public int getRetryCount() {        
		return retryCount;
    } 

    public void incMultipleRetryCount() {        
		multipleRetryCount++;
    } 

    public int getMultipleRetryCount() {        
		return multipleRetryCount;
    } 

    public void incFrameDuplicateCount() {        
		frameDuplicateCount++;
    } 

    public int getFrameDuplicateCount() {        
		return frameDuplicateCount;
    } 

    public void incRTSSuccessCount() {        
		rtsSuccessCount++;
    } 

    public int getRTSSuccessCount() {        
		return rtsSuccessCount;
    } 

    public void incRTSFailureCount() {        
		rtsFailureCount++;
    } 

    public int getRTSFailureCount() {        
		return rtsFailureCount;
    } 

    public void incACKFailureCount() {        
		ackFailureCount++;
    } 

    public int getACKFailureCount() {        
		return ackFailureCount;
    } 

    public void incReceivedFragmentCount() {        
		receivedFragmentCount++;
    } 

    public int getReceivedFragmentCount() {        
		return receivedFragmentCount;
    } 

    public void incFCSErrorCount() {        
		fcsErrorCount++;
    } 

    public int getFCSErrorCount() {        
		return fcsErrorCount;
    } 

    public void incTransmittedFrameCount() {        
		transmittedFrameCount++;
    } 

    public int getTransmittedFrameCount() {        
		return transmittedFrameCount;
    }

	public void setThroughput(double throughput) {
		this.throughput = throughput;
	} 
	
    public double getThroughput() {        
		return throughput;
    }

    public boolean isPromiscous() {
        return promiscous;
    }
    
    public void setPromiscous(boolean promiscous){
        this.promiscous=promiscous;
    }
    
 
 }
