/*****************************************************************************
* 
* $Id: MediumMobileDevicesSNR.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
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
import de.uni_trier.jane.random.ContinuousDistribution;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.global.VisualizableNetwork;
import de.uni_trier.jane.service.network.link_layer.wlan.events.ReceivePowerLevelEvent;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

import java.io.BufferedWriter;
import java.util.*;

// Diese Klasse repräsentiert das Medium für WLAN.
// Es arbeitet korrekt mit mobilen Geräten.
// Die Stärke des empfangenen Signals wird über das Two Ray Ground Reflection Modell berechnet.
// Für den Empfang wird die Signal to Noise Ratio (SNR) berücksichtigt
public class MediumMobileDevicesSNR implements GlobalService, MediumInterface, VisualizableNetwork {

	private GlobalOperatingSystem operatingSystem;
    private GlobalKnowledge globalKnowledge;
    private AddressDeviceInfoMap addressDeviceInfoMap;
    
    private PropagationModell propagationModell;

    //private boolean visualize;
    private BufferedWriter outputFile;
    private boolean visualizeCommunicationLinks;
    private boolean visualizeSendingRadius;
    private boolean visualizeMessages;
    private ContinuousDistribution randomizedNoise;

    
    public MediumMobileDevicesSNR() {        
    	this(true,true,true,null);
    } 

    public MediumMobileDevicesSNR(boolean visualizeMessages,boolean visualizeSendingRadius, boolean visualizeCommunicationLinks, ContinuousDistribution randomizedNoise) {        
    	this(null, visualizeMessages,visualizeSendingRadius,visualizeCommunicationLinks,randomizedNoise);
    } 

    public MediumMobileDevicesSNR(BufferedWriter outputFile,boolean visualizeMessages, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks, ContinuousDistribution randomizedNoise) {        
       	this.visualizeCommunicationLinks = visualizeCommunicationLinks;
        this.visualizeSendingRadius=visualizeSendingRadius;
        this.visualizeMessages=visualizeMessages;
       	this.outputFile = outputFile;
        this.randomizedNoise=randomizedNoise;
       	addressDeviceInfoMap = new AddressDeviceInfoMap();
    } 

    public void start(GlobalOperatingSystem globalOperatingSystem) {        
		this.operatingSystem = globalOperatingSystem;
		globalKnowledge = operatingSystem.getGlobalKnowledge();
		//register interface for exporting as stub
		operatingSystem.registerSignalListener(MediumInterface.class);
        globalKnowledge.addDeviceListener(new DeviceListener() {
            public void enter(DeviceID deviceID) {
             }
            public void exit(DeviceID deviceID) {
        		DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);  
                deviceInfo.getMotionTimeout().stop();
            	if (deviceInfo.isSending()) {
            		DeviceIDIterator iterator = deviceInfo.getDevicesInRangeIterator();
            		while (iterator.hasNext()) {
            			DeviceID key = iterator.next();
            			if (addressDeviceInfoMap.containsKey(key)) {
	            			DeviceInfo keyInfo = addressDeviceInfoMap.getDeviceInfo(key);
	            			keyInfo.setMediumState(MediumInterface.IDLE);
            			}
            		}
            		deviceInfo.clearDevicesInRangeSet();           		
            	}           		
            	addressDeviceInfoMap.removeDeviceInfo(deviceID);
            }
            
            public void changeTrack(final DeviceID deviceID, final TrajectoryMapping trajectoryMapping, boolean suspended) {
        		// zur Realisierung der Mobiltät
                DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);  
                if (deviceInfo==null){
            	
        			// beim start von RandomMobility wird die Funktion zu früh aufgerufen
            		operatingSystem.setTimeout(new ServiceTimeout( 0.000001) {
						public void handle() {
			               	DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);            	     	
		        			if (trajectoryMapping.getValue(operatingSystem.getSimulationTime()).getDirection().length() != 0) {
		        				// Das Device beginnt sich zu bewegen
		            			if (!deviceInfo.getMotionTimeout().isBusy()) {
		            				deviceInfo.getMotionTimeout().set(deviceID);
		            				deviceInfo.getMotionTimeout().start();
		            			}
		                	}            		
						}           			
            		});
            	}
            	else {
                            	     	
        			if (trajectoryMapping.getValue(operatingSystem.getSimulationTime()).getDirection().length() != 0) {
        				// Das Device beginnt sich zu bewegen
            			if (!deviceInfo.getMotionTimeout().isBusy()) {
            				deviceInfo.getMotionTimeout().set(deviceID);
            				deviceInfo.getMotionTimeout().start();
            			}
                	}            		
            	}
            }
        });
		operatingSystem.setTimeout(new ServiceTimeout(1) {
			public void handle() {
				updateCounter();
			}
			
		});
    } 

    // Ein Device muss sich zuerst registrieren, um über das Medium Senden oder empfangen zu können
    // Im Medium werden die Eigenschaften des Device gespeichert 
    public void registerDevice(Mib mib) {        
        DeviceID deviceID = operatingSystem.getCallingDeviceID();
        ServiceID sender = operatingSystem.getCallingServiceID();
        DeviceInfo deviceInfo = new DeviceInfo(deviceID, sender, mib);
        addressDeviceInfoMap.setDeviceInfo(deviceID,deviceInfo);
    } 

    public ServiceID getServiceID() {        
		// TODO Auto-generated method stub
		return null;
    } 

    public void finish() {        
		// TODO Auto-generated method stub
    } 

    public void getParameters(Parameters parameters) {        
		// TODO Auto-generated method stub
    } 

    public Shape getShape() {
        
		if (visualizeCommunicationLinks||visualizeSendingRadius||visualizeMessages){
            ShapeCollection shape = new ShapeCollection();
			DeviceIDIterator iterator = globalKnowledge.getNodes().iterator();
			while (iterator.hasNext()) {
                DeviceID deviceID = iterator.next();
                DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
                double signalStrength = deviceInfo.getSignalStrength();
                if (visualizeCommunicationLinks){
                    DeviceIDIterator neighborIterator = globalKnowledge.getNodes().iterator();
                    while (neighborIterator.hasNext()) {
                        DeviceID neighbor = neighborIterator.next();
                        DeviceInfo neighborInfo = addressDeviceInfoMap.getDeviceInfo(neighbor);
                        double receivedPowerLevel = signalStrength - calculatePathLoss(deviceID, neighbor);
                        double receiveThreshold = neighborInfo.getReceiveThreshold(deviceInfo.getDataRate());
                        if (receivedPowerLevel >= receiveThreshold) {
						//    Verbindungslinie zwischen den Devices, die sich in Reichweite befinden
                            shape.addShape(new ArrowShape(deviceID,neighbor,Color.LIGHTGREY,10),Position.NULL_POSITION);	    			
                        }	
                    }
                }
            
				if (visualizeSendingRadius){
				    // Geschätzter maximaler Senderadius
                    double sendingRadius = calculateSendingRadius(deviceID);
                    shape.addShape(new EllipseShape(deviceID, new Extent(
                            sendingRadius * 2, sendingRadius * 2),
                            Color.LIGHTBLUE, false), Position.NULL_POSITION);
                    if (deviceInfo.isSending()) {
                        // Kreise als Kennzeichen des Sendens die nach außen hin
                        // abschwächen
                        // Senderadius:
                        for (int i = 1; i <= 10; i++) {
                            double sig = sendingRadius * Math.pow(i / 10.0, 2);
                            Extent extend = new Extent(sig * 2, sig * 2);
                            shape.addShape(new EllipseShape(deviceID, extend,
                                    Color.GREEN, false));
                        }
                        // Interferenz-radius:
                        double interferenceRadius = calculateInterferenceRadius(deviceID);
                        Extent extend = new Extent(interferenceRadius * 2,
                                interferenceRadius * 2);
                        shape.addShape(new EllipseShape(deviceID, extend,
                                Color.LIGHTRED, false));
                    }
                }
                if (visualizeMessages){
                    if (deviceInfo.isSending()) {

                        // Das Symbol für die Nachricht und deren Fortschritt
                        Set receiverSet;
                        if (deviceInfo.getReceiver() == Mib.BROADCAST) {
                            receiverSet = deviceInfo.getDevicesInRangeSet();
                        } else {
                            receiverSet = new HashSet();
                            receiverSet.add(globalKnowledge
                                    .getDeviceID(deviceInfo.getReceiver()));
                        }
                        Iterator receiverIterator = receiverSet.iterator();
                        while (receiverIterator.hasNext()) {
                            DeviceID receiver = (DeviceID) (receiverIterator
                                    .next());
                            DeviceInfo receiverInfo = addressDeviceInfoMap
                                    .getDeviceInfo(receiver);
                            double receivedPowerLevel = signalStrength
                                    - calculatePathLoss(deviceID, receiver);
                            double receiveThreshold = receiverInfo
                                    .getReceiveThreshold(deviceInfo
                                            .getDataRate());
                            if (receivedPowerLevel >= receiveThreshold) {
                                if (deviceInfo.getMessageShape() != null) {
                                    Shape messageShape = deviceInfo
                                            .getMessageShape();
                                    MovingShape movingShape = new MovingShape(
                                            messageShape, deviceID, receiver,
                                            deviceInfo.getProgress());
                                    shape.addShape(movingShape);
                                }
                            }
                        }
                    }
				}			
			}
			
			return shape;
		}
		else {
			return null;
		}
    } 

    public static double log10(double x) {        
		return Math.log(x)/Math.log( 10.0);
    } 
    
    // geschätzter Senderadius eines Device
    // Nur zur Visualisierung benötigt
    private double calculateSendingRadius(DeviceID deviceID) {    
        
		DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
        
		double signalStrength = deviceInfo.getSignalStrength();
		double receiveThreshold = deviceInfo.getReceiveThreshold(deviceInfo.getDataRate());
		// Maximaler Senderadius für 
		// receiveThreshold = receivedPowerLevel = signalStrength - pathLoss
		double pathLoss = signalStrength - receiveThreshold;
		return calculateRadius(deviceID, pathLoss);
    } 
    
    // geschätzter Interferenz-Radius eines Device
    // Nur zur Visualisierung benötigt
    private double calculateInterferenceRadius(DeviceID deviceID) {        
		DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
		double signalStrength = deviceInfo.getSignalStrength();
		double snrThreshold = deviceInfo.getSNRThreshold(deviceInfo.getDataRate());
		double receiveThreshold = deviceInfo.getReceiveThreshold(deviceInfo.getDataRate());
		// Maximaler Interferenz-Radius für 
		// (signalStrength - pathLoss = receivedPowerLevel) = receiveThreshold - snrThreshold
		double pathLoss = signalStrength - receiveThreshold + snrThreshold;
		return calculateRadius(deviceID, pathLoss);
    } 
    

    // geschätzter Radius eines Device für gegebenen PathLoss aufgrund des Two-Ray Ground Reflection Model
    // Nur zur Visualisierung benötigt
    private double calculateRadius(DeviceID deviceID, double pathLoss) {        
		DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
		Position devicePosition = globalKnowledge.getTrajectory(deviceID).getPosition();
		double deviceHeight = devicePosition.getZ();
		if (deviceHeight <= 0)
			deviceHeight = 1;
		double antennaGain = deviceInfo.getAntennaGain();
		return calculateRadius(pathLoss, deviceHeight, antennaGain);
    }

    /**
     * TODO Comment method
     * @param pathLoss
     * @param deviceHeight
     * @param antennaGain
     * @return
     */
    public static double calculateRadius(double pathLoss, double deviceHeight, double antennaGain) {
        double sendingRadius = Math.pow(10, ((pathLoss + 20*log10(antennaGain) + 40*log10(deviceHeight)) /  40.0)); 
		return sendingRadius;
    }
    

    /**
     * 
     * TODO Comment method
     * @param mib
     * @param deviceHight
     * @return 
     */
    public static double calculateRadius(Mib mib, double deviceHight) {
        return calculateRadius(mib.getSignalStrength()-mib.getReceiveThreshold(mib.getDataRate()),deviceHight,mib.getAntennaGain());
    }
    public static double calculateSignalStrength(double radius,Mib mib, double deviceHeight) {
        return mib.getReceiveThreshold(mib.getDataRate())+40*log10(radius)-(20*log10(mib.getAntennaGain()) + 40*log10(deviceHeight));
    }
    
 
    
    //public static void 
    
    // Path Loss nach dem Two-Ray Ground Reflection Model
    // siehe Wireless Communications: Principles and Practice, Second Edition, Theodore Rappaport
    // Formel 4.53
    private double calculatePathLoss(DeviceID sender, DeviceID deviceID) {        
		DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);
		DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
		Position senderPosition = globalKnowledge.getTrajectory(sender).getPosition();
		Position devicePosition = globalKnowledge.getTrajectory(deviceID).getPosition();
		double dx = senderPosition.distanceX(devicePosition);
		double dy = senderPosition.distanceY(devicePosition);
		double distance = Math.sqrt(dx*dx + dy*dy);
		double senderHeight = senderPosition.getZ();
		if (senderHeight <= 0)
			senderHeight = 1;
		double deviceHeight = devicePosition.getZ();
		if (deviceHeight <= 0)
			deviceHeight = 1;
		double pathLoss = 40*log10(distance) - (10*log10(senderInfo.getAntennaGain()) + 10*log10(deviceInfo.getAntennaGain()) + 20*log10(senderHeight) + 20*log10(deviceHeight));
		return pathLoss;
    } 
    
    // Berechnung der Signal/Noise Ratio (SNR)
    // Falls die SNR zu klein ist, kann das Signal nicht richtig empfangen werden
    private double calculateSignalToNoiseRatio(DeviceID sender, DeviceID deviceID) {        
		// I: SNR[dB] = 10*log(receivedPowerLevel[mW]/(Noise[mW] + ?(receivedPowerLevel(i)[mW])))
    	// II: P[dBm] = 10*log(P[W]/0.001 W) => P[W] = 10^(P[dBm]/10.0) * 0.001 => P[mW] = 10^(P[dBm]/10.0)
    	// I, II => SNR[dB] = receivedPowerLevel[dBm] - 10*log(10^(Noise[dBm]/10.0) + ?(10^(receivedPowerLevel(i)[dBm]/10.0)))
		DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
		if (sender==null)
			throw new IllegalStateException("Fehler");

		double receivedPowerLevel = -100;
		double noise = getNoise(); 
		HashMap receivedPowerLevelMap = deviceInfo.getReceivedPowerLevelMap();
		Iterator iterator = receivedPowerLevelMap.keySet().iterator();
		while (iterator.hasNext()) {
			DeviceID key = (DeviceID) iterator.next();
			double powerLevel = ((Double) receivedPowerLevelMap.get(key)).doubleValue();
			if (key.equals(sender)) {
				receivedPowerLevel = powerLevel;
			}
			else {
				noise += Math.pow(10, (powerLevel/10.0));
			}
		}
		if (receivedPowerLevel == -100)
			throw new IllegalStateException("Fehler");
		double snr = receivedPowerLevel - 10*log10(noise);
		return snr;
    }   

    /**
     * TODO Comment method
     * @return
     */
    private double getNoise() {
        if (randomizedNoise!=null){
            return  randomizedNoise.getNext();
        }
        //      noise umgerechnet in Milliwatt mW
        // gewöhnlicher Noise-Level ohne andere Wlan Netze etc. liegt bei etwa -100 dBm
        return Math.pow(10,(-100/10.0));
    }

    // Ein Device hat eine neue Übertragung begonnen und belegt das Medium
    public void newTransmission(final double signalStrength, final MacHeader macHeader, final Frame frame) {        
		final DeviceID sender = globalKnowledge.getDeviceID(macHeader.getSender());
		DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);
		senderInfo.setSending(true, macHeader, frame);
		
		// warten, ob noch andere Devices zur selben Zeit anfangen oder aufhören zu senden
		operatingSystem.setTimeout(new ServiceTimeout(0) {
			
			public void handle() {
				// Bei allen Devices in Empfangsreichweite wird das Medium als BUSY gesetzt
				// und die Nachricht weitergeleitet
				DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);
				DeviceIDIterator iterator = globalKnowledge.getNodes().iterator();
				while (iterator.hasNext()) {
					DeviceID deviceID = iterator.next();
					DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
					double signalToNoiseRatio;
					// Die Empfangsstärke des Signals wird im Device Info gespeichert
					double receivedPowerLevel = signalStrength - calculatePathLoss(sender, deviceID);
					deviceInfo.setReceivedPowerLevel(sender, receivedPowerLevel);
					
					double receiveThreshold = deviceInfo.getReceiveThreshold(senderInfo.getDataRate());
					double snrThreshold = deviceInfo.getSNRThreshold(senderInfo.getDataRate());
					if (receivedPowerLevel >= receiveThreshold) {
						// Das Signal ist stark genug, damit Device die Nachricht empfangen könnte
						// nur Devices in Reichweite des Senders sind von der Übertragung betroffen
						Set devicesInRangeSet = senderInfo.getDevicesInRangeSet();
						devicesInRangeSet.add(deviceID);
						if (!deviceInfo.isSending()) {
							// Devices können nicht gleichzeitig Senden und Empfangen, also wird den sendenden Devices nichts weitergemeldet
							if (deviceInfo.getMediumState() == MediumInterface.IDLE) {
								// Das Device ist nichts am empfangen
								signalToNoiseRatio = calculateSignalToNoiseRatio(sender, deviceID);
								if (signalToNoiseRatio >= snrThreshold) {
									// Das Device empfängt das Signal korrekt.
									// Der Empfang einer neuen Nachricht beginnt und gibt das Frame an alle Devices weiter, 
									// die es empfangen können, so dass diese es weiter verarbeiten können
									operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new StartReceiptSignal(macHeader, frame));	
									deviceInfo.setReceiving(true, sender);
								}
								else {
									// Das Signal ist eigentlich stark genug, aber wird durch andere Signale zu stark gestört
									// so dass die Nachricht fehlerhaft empfangen wird.
									operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new StartReceiptSignal());
								}
							}
							else if (deviceInfo.getMediumState() == MediumInterface.BUSY) {
								// Mehrere Signale sind stark genug um empfangen zu werden und stören sich gegenseitig
								// In diesem Fall liegt eine Kollision vor, die dem Device gemeldet wird						
								if (!deviceInfo.hadCollision()) {
									deviceInfo.setCollision(true);
									//operatingSystem.write("Zeit " + operatingSystem.getSimulationTime() + ": Kollision bei Device " + deviceID + " durch Sender " + sender + " gemeldet.");
									operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new CollisionSignal());
								}
							}
						}										
						deviceInfo.setMediumState(MediumInterface.BUSY);
					}
					else {
						// Das Signal ist nicht stark genug, um empfangen zu werden, aber es kann evtl. andere Signale stören
						if (deviceInfo.getMediumState() == MediumInterface.BUSY) {
							if (!deviceInfo.isSending() && deviceInfo.isReceiving() && !deviceInfo.hadCollision()) {
								signalToNoiseRatio = calculateSignalToNoiseRatio(deviceInfo.getSender(), deviceID);
								if (signalToNoiseRatio < snrThreshold) {
									// Das neue Signal stört den Empfang einer laufenden Übertragung.  
									// In diesem Fall liegt eine Kollision vor, die dem Device gemeldet wird
									deviceInfo.setCollision(true);
									operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new CollisionSignal());
								}
							}						
						}
					}
				}
			}		
		});		
    } 
    
    // Ein Device hat die Übertragung beendet und Medium wird evtl. frei
    public void endOfTransmission(final EndTransmitCallback callback) {        
		final DeviceID sender = operatingSystem.getCallingDeviceID();
		DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);
		// Bei allen Devices in Empfangsreichweite wird überprüft, ob das Medium IDLE ist
		DeviceIDIterator iterator = senderInfo.getDevicesInRangeIterator();
		while (iterator.hasNext()) {
			DeviceID deviceID = iterator.next();
			DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
			deviceInfo.setReceiving(false, null);
			deviceInfo.setMediumState(MediumInterface.IDLE);	
			if (!deviceInfo.isSending()) {
				if (deviceInfo.getMediumState() == MediumInterface.IDLE) {
					deviceInfo.setCollision(false);
					operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new EndReceiptSignal());
				}
			}
		}
		
		// warten, ob noch andere Devices zur selben Zeit aufhören oder anfangen zu senden
		operatingSystem.setTimeout(new ServiceTimeout(0) {
			public void handle() {
				DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);
				senderInfo.setSending(false, null, null);
				senderInfo.clearDevicesInRangeSet();
				// allen Devices wird mitgeteilt, dass der Sender fertig gesendet hat
				DeviceIDIterator iterator = globalKnowledge.getNodes().iterator();
				while (iterator.hasNext()) {
					DeviceID deviceID = iterator.next();
					DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
					deviceInfo.removeReceivedPowerLevel(sender);
				}
				// Falls nach Ende des Sendens das Medium immer noch Busy ist, muss dies dem Device extra mitgeteilt werden
				if (senderInfo.getMediumState() == MediumInterface.BUSY) {
					callback.stillBusy();						
				}
				else {
					senderInfo.setCollision(false);
				}
				operatingSystem.finishListener(callback);				
			}				
		});
    } 

    // Überprüft während der Bewegung, ob sich etwas am Zustand eines Device ändert,
    // also ob dadurch ein Fehler bei einem Device auftritt
    private void checkMotion(DeviceID sender, DeviceID deviceID) {        
		DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);		
		double signalStrength = senderInfo.getSignalStrength();
		Set devicesInRangeSet = senderInfo.getDevicesInRangeSet();
		DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
		double signalToNoiseRatio;
		// Die Empfangsstärke des Signals wird im Device Info gespeichert
		double receivedPowerLevel = signalStrength - calculatePathLoss(sender, deviceID);
		deviceInfo.setReceivedPowerLevel(sender, receivedPowerLevel);
		double receiveThreshold = deviceInfo.getReceiveThreshold(senderInfo.getDataRate());
		double snrThreshold = deviceInfo.getSNRThreshold(senderInfo.getDataRate());
		if (receivedPowerLevel >= receiveThreshold) {
			// Das Signal ist stark genug, damit das Device die Nachricht empfangen könnte
			// nur Devices in Reichweite des Senders sind von der Übertragung betroffen
			if (!devicesInRangeSet.contains(deviceID)) {
				// Ein Device ist in die Reichweite des sendenden Device geraten und muss auf Busy gesetzt werden
				devicesInRangeSet.add(deviceID);
				// Sendenden Devices wird nichts weiter mitgeteilt, da diese das Medium im Moment nicht abhören
				if (!deviceInfo.isSending()) {
					if (deviceInfo.getMediumState() == MediumInterface.IDLE) {
						// meldet allen untätigen Devices, dass das Medium BUSY ist
						// Die Nachricht kann das Device aber nicht empfangen, da es das Signal nicht komplett gehört hat 
						operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new StartReceiptSignal());	
					}
					else if (deviceInfo.getMediumState() == MediumInterface.BUSY) {
						// Mehrere Signale sind stark genug um empfangen zu werden und stören sich gegenseitig
						// In diesem Fall liegt eine Kollision vor, die dem Device gemeldet wird						
						if (!deviceInfo.hadCollision()) {
							deviceInfo.setCollision(true);
							operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new CollisionSignal());
						}
					}
				}
				deviceInfo.setMediumState(MediumInterface.BUSY);
			}
			else {
				// Das Device war vorher in Sendereichweite und jetzt auch,
				// aber da sich die empfangene Signalstärke verändert hat, ist jetzt evtl die Übertragung gestört
				if (!deviceInfo.isSending() && deviceInfo.isReceiving() && !deviceInfo.hadCollision()) {
					signalToNoiseRatio = calculateSignalToNoiseRatio(deviceInfo.getSender(), deviceID);
					if (signalToNoiseRatio < snrThreshold) {
						// Durch die Veränderung der Signalstärke ist jetzt die laufenden Übertragung gestört.  
						// In diesem Fall liegt eine Kollision vor, die dem Device gemeldet wird
						deviceInfo.setCollision(true);
						operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new CollisionSignal());
					}
				}						
			}
		}
		else {
			// Das Signal ist nicht stark genug, damit das Device die Nachricht empfangen könnte
			if (devicesInRangeSet.contains(deviceID)) {
				// Device ist aus der Empfangsreichweite des Senders geraten und muss ggf. auf IDLE gesetzt werden
				devicesInRangeSet.remove(deviceID);
				deviceInfo.setMediumState(MediumInterface.IDLE);	
				if (deviceInfo.getMediumState() == MediumInterface.IDLE) {
					deviceInfo.setCollision(false);
					if (!deviceInfo.isSending()) {
						operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new EndReceiptSignal());
					}
				}
			}
			else {
				// Das Device war vorher nicht in Sendereichweite und jetzt auch nicht
				// aber es stört vielleicht eine laufende Übertragung, da sich die empfangene Signalstärke verändert hat
				if (!deviceInfo.isSending() && deviceInfo.isReceiving() && !deviceInfo.hadCollision()) {
					signalToNoiseRatio = calculateSignalToNoiseRatio(deviceInfo.getSender(), deviceID);
					if (signalToNoiseRatio < snrThreshold) {
						// Durch die Veränderung der Signalstärke stört das Signal den Empfang einer laufenden Übertragung.  
						// In diesem Fall liegt eine Kollision vor, die dem Device gemeldet wird
						deviceInfo.setCollision(true);
						operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new CollisionSignal());
					}
				}						
			}				
		}
    } 

    // Enthält alle benötigten Informationen eines Device
    private class DeviceInfo {

    	private DeviceID deviceID;
    	private ServiceID serviceID;
    	private int mediumState;
    	private int busyCount;
    	private boolean sending;
    	private boolean receiving;
	    private boolean collision;
	    private Mib mib;
	    private Set devicesInRangeSet;
	    private MotionTimeout motionTimeout;
	    private MacHeader macHeader;
	    private Frame frame;
	    private double currentStartTime;
	    private HashMap receivedPowerLevelMap;
	    private DeviceID sender;
	
	    public  DeviceInfo(DeviceID deviceID, ServiceID serviceID, Mib mib) {        
            this.deviceID = deviceID;
            this.serviceID = serviceID;
            this.mib = mib;
            mediumState = MediumInterface.IDLE;
            busyCount = 0;
            devicesInRangeSet = new HashSet();
            receivedPowerLevelMap = new HashMap();
            motionTimeout = new MotionTimeout();
            sending = false;
            receiving = false;
            currentStartTime = 0;
            collision = false;
            sender = null;
	    } 
	
	    public void removeReceivedPowerLevel(DeviceID sender) {        
			receivedPowerLevelMap.remove(sender);
           // operatingSystem.sendEvent(new ReceivePowerLevelEvent(deviceID,sender,0));
	    } 
	
	    public void setReceivedPowerLevel(DeviceID sender, double receivedPowerLevel) {        
			receivedPowerLevelMap.put(sender, new Double(receivedPowerLevel));
           // operatingSystem.sendEvent(new ReceivePowerLevelEvent(deviceID,sender,receivedPowerLevel));
	    } 
	
	    public HashMap getReceivedPowerLevelMap() {        
			return receivedPowerLevelMap;
	    } 
	
	    public double getProgress() {        
            double progress = (operatingSystem.getSimulationTime() - currentStartTime) / macHeader.getTxTime();
            return progress;                       
	    } 
	
	    public Shape getMessageShape() {        
			if (frame == null)
				return null;
		    return frame.getShape();
	    } 
	
	    public DeviceID getSender() {        
			return sender;
	    } 
	
	    public Address getReceiver() {        
			return macHeader.getReceiver();
	    } 
	
	    public double getSignalStrength() {        
			return mib.getSignalStrength();
	    } 
	
	    public double getDataRate() {        
			return mib.getDataRate();
	    } 
	
	    public double getAntennaGain() {        
			return mib.getAntennaGain();
	    } 
	
	    public double getReceiveThreshold(double dataRate) {        
        	return mib.getReceiveThreshold(dataRate);
	    } 
	
	    public double getSNRThreshold(double dataRate) {        
			return mib.getSNRThreshold(dataRate);
	    } 
	
	    public boolean hadCollision() {        
			return collision;
	    } 
	
	    public void setCollision(boolean collision) {        
			this.collision = collision;
			setReceiving(false, null);
	    } 
	
	    public int getMediumState() {        
		return mediumState;
	    } 
	
	    public DeviceID getDeviceID() {        
            return deviceID;
	    } 
	
	    public Address getAddress() {        
            return mib.getMacAddress();
	    } 
	
	    public ServiceID getServiceID() {        
            return serviceID;
	    } 
	
	    public void setMediumState(int state) {        
			if (state == BUSY) {
				busyCount += 1;
				mediumState = BUSY;
			}
			else if (state == IDLE){
				if (busyCount > 0)
					busyCount -= 1;
				if (busyCount == 0) 
					mediumState = IDLE;
			}			
	    } 
	
	    public MotionTimeout getMotionTimeout() {        
			return motionTimeout;
	    } 
	
	    public void addDeviceInRange(DeviceID deviceInRange) {        
			devicesInRangeSet.add(deviceInRange);			
	    } 
	
	    public Set getDevicesInRangeSet() {        
			return devicesInRangeSet;			
	    } 
	
	    public DeviceIDIterator getDevicesInRangeIterator() {        
			return new DeviceIDIterator() {
				private Iterator iterator = devicesInRangeSet.iterator();
				public DeviceID next() {
					return (DeviceID) iterator.next();
				}
				public boolean hasNext() {
					return iterator.hasNext();
				}
				public void remove() {
					throw new IllegalAccessError("Remove not supported");					
				}				
			};
	    } 
	
	    public void clearDevicesInRangeSet() {        
			devicesInRangeSet.clear();
			
	    } 
	
	    public void setReceiving(boolean receiving, DeviceID sender) {        
			this.receiving = receiving;
			this.sender = sender;
	    } 
	
	    public boolean isReceiving() {        
			return receiving;
	    } 
	
	    public void setSending(boolean sending, MacHeader macHeader, Frame frame) {        
			this.sending = sending;
			this.macHeader = macHeader;
			this.frame = frame;
			if (sending)
				currentStartTime = operatingSystem.getSimulationTime();
			else
				currentStartTime = 0;
	    } 
	
	    public boolean isSending() {        
			return sending;
	    } 
	}
    
    // Eine HashMap, die für jedes Device die zugehörige DeviceInfo enthält
    // Klasse übernommen aus LinkCalculator3D
    private static class AddressDeviceInfoMap {

	    private HashMap addressDeviceInfoMap;
	
	    public AddressDeviceInfoMap() {        
			addressDeviceInfoMap = new HashMap();
	    } 
	
	    public boolean containsKey(DeviceID key) {        
			return addressDeviceInfoMap.containsKey(key);
	    } 
	
	    public void setDeviceInfo(DeviceID address, DeviceInfo deviceInfo) {        
			addressDeviceInfoMap.put(address, deviceInfo);
	    } 
	
	    public DeviceInfo getDeviceInfo(DeviceID address) {        
			return (DeviceInfo)addressDeviceInfoMap.get(address);
	    } 
	
	    public void removeDeviceInfo(DeviceID address) {        
			addressDeviceInfoMap.remove(address);
	    } 
	
	    public DeviceIDIterator getAddressIterator() {        
			return new DeviceIDIterator() {
				private Iterator iterator = addressDeviceInfoMap.keySet().iterator();
				public boolean hasNext() {
					return iterator.hasNext();
				}
				public DeviceID next() {
					return (DeviceID)iterator.next();
				}
				/* (non-Javadoc)
				 * @see de.uni_trier.ubi.appsim.kernel.basetype.AddressIterator#remove()
				 */
				public void remove() {
					//iterator.remove();
					throw new IllegalAccessError("Remove not supported");
				}
			};
	    } 
	 }
    
	// Mobilität realisieren
	// Er überwacht, ob ein sendendes Device, das mobile Device beeinflusst.
    // Ruft während der Bewegung eines Device die Funktion checkMotion() in regelmäßigem Abstand auf.
	// Idee: nur bei Beginn einer Bewegung Timer starten und nach Ende der Bewegung stoppen
	private class MotionTimeout extends ServiceTimeout {

		private DeviceID mobileDevice;
	    private boolean busy;
	    private boolean motionFinished;
	
	    public MotionTimeout() {        
			super(0);
			busy = false;
			motionFinished = true;
	    } 
	
	    /**
         * TODO Comment method
         */
        public void stop() {
            operatingSystem.removeTimeout(this);
            
        }

        public void set(DeviceID mobileDevice) {        
			this.mobileDevice = mobileDevice;
	    } 
	
	    public void start() {        
			busy = true;
			motionFinished = false;
			delta = Mib.moveTimeout / Mib.timeFactor;
			operatingSystem.setTimeout(this);
	    } 
	
	    public void handle() {        
			busy = false;
			
			DeviceInfo mobileDeviceInfo = addressDeviceInfoMap.getDeviceInfo(mobileDevice);
			DeviceIDIterator iterator = globalKnowledge.getNodes().iterator();
			while (iterator.hasNext()) {
				DeviceID deviceID = iterator.next();
				DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
				
				if (!mobileDevice.equals(deviceID)) {
					// Wenn das bewegende Device am senden ist, müssen alle anderen Devices auf evtl. Zustandsänderung überprüft werden
					if (mobileDeviceInfo.isSending()) {
						checkMotion(mobileDevice, deviceID);
					}
					
					// Bei allen sendenden Devices muss überprüft werden, ob sie das bewegende Device beeinflussen oder bereits beeinflusst haben
					// und evtl. dessen Zustand ändern
					if (deviceInfo.isSending()) {
						checkMotion(deviceID, mobileDevice);				
					}
				}
			}
			
    		// In regelmäßigem Intervall bis zum Ende der Bewegung 
			// werden die sendenden Devices überprüft, ob sich durch die Bewegung etwas am Zustand ändert.
			if (!motionFinished) {
				start();
			}
			if (globalKnowledge.getTrajectory(mobileDevice).getDirection().length() == 0) {
				// Die Bewegung endet
				motionFinished = true;
			}
	    } 
	
	    public boolean isBusy() {        
			return busy;
	    } 
	}

	// Signal, dass einem Device den Start des Empfangs mitteilt
	public static class StartReceiptSignal implements Signal {

		private MacHeader macHeader;
	    private Frame frame;
	
	    public  StartReceiptSignal() {        
        	this(null,null);
	    } 
	
	    public  StartReceiptSignal(MacHeader macHeader, Frame frame) {        
           	this.macHeader = macHeader;
           	this.frame = frame;
	    } 
	
	    public void handle(SignalListener service) {        
        	((PhysicalLayerInterface) service).startReceipt(macHeader, frame);
	    } 
	
	    public Dispatchable copy() {        
        	return this;
	    } 
	
	    public Class getReceiverServiceClass() {        
            return PhysicalLayerInterface.class;
	    } 
	}

	// Signal, dass einem Device das Ende des Empfangs mitteilt
	public static class EndReceiptSignal implements Signal {

		public void handle(SignalListener service) {        
        	((PhysicalLayerInterface) service).endReceipt();
	    } 
	
	    public Dispatchable copy() {        
        	return this;
	    } 
	
	    public Class getReceiverServiceClass() {        
            return PhysicalLayerInterface.class;
	    } 
	}

	// Signal, dass einem Device eine Kollision/Fehler mitteilt
	public static class CollisionSignal implements Signal {

		public void handle(SignalListener service) {        
        	((PhysicalLayerInterface) service).collision();
	    } 
	
	    public Dispatchable copy() {        
        	return this;
	    } 
	
	    public Class getReceiverServiceClass() {        
            return PhysicalLayerInterface.class;
	    } 
	}

//	Ab hier folgen Funktionen und Variablen, die für eine eventuelle Ausgabe benötigt werden
	
    private int transmittedFragmentCount;
    private int failedCount;
    private int retryCount;
    private int multipleRetryCount;
    private int frameDuplicateCount;
    private int rtsSuccessCount;
    private int rtsFailureCount;
    private int ackFailureCount;
    private int receivedFragmentCount;
    private int fcsErrorCount;
    private int transmittedFrameCount;
    private double throughput;

    public void updateCounter() {
        
//    	operatingSystem.setTimeout(new ServiceTimeout(99) {
//
//			public void handle() {
//				transmittedFragmentCount = 0;
//				failedCount = 0;
//				retryCount = 0;
//				multipleRetryCount = 0;
//				frameDuplicateCount = 0;
//				rtsSuccessCount = 0;
//				rtsFailureCount = 0;
//				ackFailureCount = 0;
//				receivedFragmentCount = 0;
//				fcsErrorCount = 0;
//				transmittedFrameCount = 0;
//				throughput = 0;
//				DeviceIDIterator iterator = globalKnowledge.getNodes().iterator();
//		    	while (iterator.hasNext()) {
//		        	DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(iterator.next());
//		        	Mib mib = deviceInfo.mib;
//		    		transmittedFragmentCount += mib.getTransmittedFragmentCount();
//		    		failedCount += mib.getFailedCount();
//		    		retryCount += mib.getRetryCount();
//		    		multipleRetryCount += mib.getMultipleRetryCount();
//		    		frameDuplicateCount += mib.getFrameDuplicateCount();
//		    		rtsSuccessCount += mib.getRTSSuccessCount();
//		    		rtsFailureCount += mib.getRTSFailureCount();
//		    		ackFailureCount += mib.getACKFailureCount();
//		    		receivedFragmentCount += mib.getReceivedFragmentCount();
//		    		fcsErrorCount += mib.getFCSErrorCount();
//		    		transmittedFrameCount += mib.getTransmittedFrameCount();
//		    		throughput += mib.getThroughput();
//		    	}
//				
//				try {
//					// Alle Devices haben ihre Counterstände mitgeteilt
//					outputFile.write("Zeit " + operatingSystem.getSimulationTime() + " Gesamtstand");
//					outputFile.write("--------------- Counter Ausgabe ---------------\n");
//					outputFile.write("Durchsatz: " + (throughput/(double)globalKnowledge.getNodes().size()) + " Mbit/s\n");
//					outputFile.write("transmittedFragmentCount " + transmittedFragmentCount + "\n");
//					outputFile.write("failedCount " + failedCount + "\n");
//					outputFile.write("retryCount " + retryCount + "\n");
//					outputFile.write("multipleRetryCount " + multipleRetryCount + "\n");
//					outputFile.write("frameDuplicateCount " + frameDuplicateCount + "\n");
//					outputFile.write("rtsSuccessCount " + rtsSuccessCount + "\n");
//					outputFile.write("rtsFailureCount " + rtsFailureCount + "\n");
//					outputFile.write("ackFailureCount " + ackFailureCount + "\n");
//					outputFile.write("receivedFragmentCount " + receivedFragmentCount + "\n");
//					outputFile.write("fcsErrorCount " + fcsErrorCount + "\n");
//					outputFile.write("transmittedFrameCount " + transmittedFrameCount + "\n");
//					outputFile.write("--------------- Counter Ausgabe Ende ---------------\n");
//					outputFile.flush();
//				}
//				catch (Exception e) {
//					System.out.println(e);
//				}
//				delta = 100;
//				operatingSystem.setTimeout(this);
//			}
//    		
//    	});
    }

    public void visualize(boolean messages, boolean sendingRadii, boolean links) {
        visualizeCommunicationLinks=links;
        visualizeMessages=messages;
        visualizeSendingRadius=sendingRadii;
        
    }

}
