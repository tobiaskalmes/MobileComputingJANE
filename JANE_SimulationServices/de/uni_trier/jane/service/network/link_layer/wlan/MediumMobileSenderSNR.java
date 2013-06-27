package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

import java.util.*;

public class MediumMobileSenderSNR implements GlobalService, MediumInterface {

	private GlobalOperatingSystem operatingSystem;
	private GlobalKnowledge globalKnowledge;
    private AddressDeviceInfoMap addressDeviceInfoMap;
    private boolean visualize;
    private int count;
    
    public MediumMobileSenderSNR() {
    	this(true);
    }

    public MediumMobileSenderSNR(boolean visualize) {
       	this.visualize = visualize;
    	addressDeviceInfoMap = new AddressDeviceInfoMap();
    	deviceCount = 0;
    	count = 0;
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
            
            public void changeTrack(DeviceID deviceID, TrajectoryMapping trajectoryMapping, boolean suspended) {}
        });

 	}

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
		if (visualize) {
	        ShapeCollection shape = new ShapeCollection();
	        
			DeviceIDIterator iterator = globalKnowledge.getNodes().iterator();
			while (iterator.hasNext()) {
				DeviceID deviceID = iterator.next();
				DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
				double signalStrength = deviceInfo.getSignalStrength();
				DeviceIDIterator neighborIterator = globalKnowledge.getNodes().iterator();
				while (neighborIterator.hasNext()) {
					DeviceID neighbor = neighborIterator.next();
					DeviceInfo neighborInfo = addressDeviceInfoMap.getDeviceInfo(neighbor);
					double receivedPowerLevel = signalStrength - calculatePathLoss(deviceID, neighbor);
					double receiveThreshold = neighborInfo.getReceiveThreshold(deviceInfo.getDataRate());
					if (receivedPowerLevel >= receiveThreshold) {
						// Verbindungslinie zwischen den Devices, die sich in Reichweite befinden
						shape.addShape(new ArrowShape(deviceID,neighbor,Color.LIGHTGREY,10),Position.NULL_POSITION);	    			
					}	
				}
				
				// Geschätzter maximaler Senderadius
				double sendingRadius = calculateSendingRadius(deviceID);
	    		shape.addShape(new EllipseShape(deviceID, new Extent(sendingRadius*2, sendingRadius*2), Color.LIGHTBLUE, false), Position.NULL_POSITION);
				if (deviceInfo.isSending()) {
					// Kreise als Kennzeichen des Sendens die nach außen hin abschwächen
					// Senderadius:
					for (int i = 1; i <= 10; i++) {
		        		double sig = sendingRadius * Math.pow(i/ 10.0, 2);
		            	Extent extend = new Extent(sig * 2, sig * 2);
		            	shape.addShape(new EllipseShape(deviceID, extend, Color.GREEN, false));        		
		        	}	     
		        	// Interferenz-radius:
					double interferenceRadius = calculateInterferenceRadius(deviceID);
		            Extent extend = new Extent(interferenceRadius * 2, interferenceRadius * 2);
		            shape.addShape(new EllipseShape(deviceID, extend, Color.LIGHTRED, false));        		
				
		        	// Das Symbol für die Nachricht und deren Fortschritt
		        	Set receiverSet;
		        	if (deviceInfo.getReceiver() == Mib.BROADCAST) {
			        	receiverSet = deviceInfo.getDevicesInRangeSet();
		        	}
		        	else {
		        		receiverSet = new HashSet();
		        		receiverSet.add(globalKnowledge.getDeviceID(deviceInfo.getReceiver()));
		        	}
		        	Iterator receiverIterator = receiverSet.iterator();
		        	while (receiverIterator.hasNext()) {
		            	DeviceID receiver = (DeviceID) (receiverIterator.next());
		            	DeviceInfo receiverInfo = addressDeviceInfoMap.getDeviceInfo(receiver);
						double receivedPowerLevel = signalStrength - calculatePathLoss(deviceID, receiver);
						double receiveThreshold = receiverInfo.getReceiveThreshold(deviceInfo.getDataRate());
						if (receivedPowerLevel >= receiveThreshold) {
				            if (deviceInfo.getMessageShape() != null){
				                Shape messageShape = deviceInfo.getMessageShape();
				                MovingShape movingShape = new MovingShape(messageShape, deviceID, receiver, deviceInfo.getProgress());
				    	        shape.addShape(movingShape);
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

    public double log10(double x) {        
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
		double sendingRadius = Math.pow(10, ((pathLoss + 20*log10(antennaGain) + 40*log10(deviceHeight)) /  40.0)); 
		return sendingRadius;
    } 
    
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
		// gewöhnlicher Noise-Level ohne andere Wlan Netze etc. liegt bei etwa -100 dBm
		double receivedPowerLevel = -100;
		double noise = Math.pow(10, (-100/10.0)); // noise umgerechnet in Milliwatt mW
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
    
// Ein Device hat eine neue Übertragung begonnen und belegt das Medium
    public void newTransmission(final double signalStrength, final MacHeader macHeader, final Frame frame) {        
		final DeviceID sender = globalKnowledge.getDeviceID(macHeader.getSender());
		DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);
		senderInfo.setSending(true, macHeader, frame);
		// zur realisierung der Mobiltät 
		senderInfo.getSendTimeout().set(sender);
		senderInfo.getSendTimeout().start();
				
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
				senderInfo.getSendTimeout().stop();
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

    private void checkMotion(DeviceID sender) {        
		DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);	
		double signalStrength = senderInfo.getSignalStrength();
		Set devicesInRangeSet = senderInfo.getDevicesInRangeSet();
		// Bei allen Devices in Empfangsreichweite wird das Medium als BUSY gesetzt
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
				// Das Signal ist stark genug, damit das Device die Nachricht empfangen könnte
				// nur Devices in Reichweite des Senders sind von der Übertragung betroffen
				if (!devicesInRangeSet.contains(deviceID)) {
					count++;
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
					count++;
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
    } 

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
		private SendTimeout sendTimeout;
		private MacHeader macHeader;
		private Frame frame;
		private double currentStartTime;
		private HashMap receivedPowerLevelMap;
		private DeviceID sender;

		public DeviceInfo(DeviceID deviceID, ServiceID serviceID, Mib mib) {
            this.deviceID = deviceID;
            this.serviceID = serviceID;
            this.mib = mib;
            mediumState = MediumInterface.IDLE;
            busyCount = 0;
            devicesInRangeSet = new HashSet();
            receivedPowerLevelMap = new HashMap();
            sendTimeout = new SendTimeout();
            sending = false;
            receiving = false;
            currentStartTime = 0;
            collision = false;
            sender = null;
		}

		public void removeReceivedPowerLevel(DeviceID sender) {
			receivedPowerLevelMap.remove(sender);
		}

		public void setReceivedPowerLevel(DeviceID sender, double receivedPowerLevel) {
			receivedPowerLevelMap.put(sender, Double.valueOf(receivedPowerLevel));
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
		
		public SendTimeout getSendTimeout() {
			return sendTimeout;
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
	// Idee 1: direkt bei sendebeginn diesen Timer starten und bei sendeschluss stoppen.
	// Er überprüft in regelmäßigen Abständen, welche Devices sich in Sendereichweite befinden
	private class SendTimeout extends ServiceTimeout {
		private DeviceID deviceID;
		
		public SendTimeout() {
			super(0);
		}
			
		public void set(DeviceID deviceID) {
			this.deviceID = deviceID;
		}

		public void start() {
			delta = 200 / Mib.timeFactor;
			operatingSystem.setTimeout(this);
		}
		
		public void stop() {
			delta = 0;
			operatingSystem.removeTimeout(this);			
		}

		public void handle() {
    		// In regelmäßigem Intervall bis zum Ende des Sendens 
			// wird überprüft, ob sich durch eine Bewegung etwas am Zustand ändert.
		    start();
	    	checkMotion(deviceID);
 		}
	}

	public static class StartReceiptSignal implements Signal {
        private MacHeader macHeader;
        private Frame frame;
        
        public StartReceiptSignal() {
        	this(null,null);
        }

		public StartReceiptSignal(MacHeader macHeader, Frame frame) {
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
    
    private int deviceCount;
	public void updateCounter(int transmittedFragmentCount, int failedCount, int retryCount, int multipleRetryCount, int frameDuplicateCount, int rtsSuccessCount, int rtsFailureCount, int ackFailureCount, int receivedFragmentCount, int fcsErrorCount, int transmittedFrameCount) {
		if (deviceCount == 0) {
			this.transmittedFragmentCount = 0;
			this.failedCount = 0;
			this.retryCount = 0;
			this.multipleRetryCount = 0;
			this.frameDuplicateCount = 0;
			this.rtsSuccessCount = 0;
			this.rtsFailureCount = 0;
			this.ackFailureCount = 0;
			this.receivedFragmentCount = 0;
			this.fcsErrorCount = 0;
			this.transmittedFrameCount = 0;			
		}
		this.transmittedFragmentCount += transmittedFragmentCount;
		this.failedCount += failedCount;
		this.retryCount += retryCount;
		this.multipleRetryCount += multipleRetryCount;
		this.frameDuplicateCount += frameDuplicateCount;
		this.rtsSuccessCount += rtsSuccessCount;
		this.rtsFailureCount += rtsFailureCount;
		this.ackFailureCount += ackFailureCount;
		this.receivedFragmentCount += receivedFragmentCount;
		this.fcsErrorCount += fcsErrorCount;
		this.transmittedFrameCount += transmittedFrameCount;
		
		deviceCount++;
		if (deviceCount == globalKnowledge.getNodes().size()) {
			// Alle Devices haben ihre Counterstände mitgeteilt
			operatingSystem.write("Zeit " + operatingSystem.getSimulationTime());
			operatingSystem.write("--------------- Counter Ausgabe ---------------");
			operatingSystem.write("transmittedFragmentCount " + this.transmittedFragmentCount);
			operatingSystem.write("failedCount " + this.failedCount);
			operatingSystem.write("retryCount " + this.retryCount);
			operatingSystem.write("multipleRetryCount " + this.multipleRetryCount);
			operatingSystem.write("frameDuplicateCount " + this.frameDuplicateCount);
			operatingSystem.write("rtsSuccessCount " + this.rtsSuccessCount);
			operatingSystem.write("rtsFailureCount " + this.rtsFailureCount);
			operatingSystem.write("ackFailureCount " + this.ackFailureCount);
			operatingSystem.write("receivedFragmentCount " + this.receivedFragmentCount);
			operatingSystem.write("fcsErrorCount " + this.fcsErrorCount);
			operatingSystem.write("transmittedFrameCount " + this.transmittedFrameCount);
			operatingSystem.write("Count " + this.count);
			operatingSystem.write("--------------- Counter Ausgabe Ende ---------------");
			deviceCount = 0;
		}
	}
}