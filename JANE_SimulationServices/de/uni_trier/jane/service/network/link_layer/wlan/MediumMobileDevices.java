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

public class MediumMobileDevices implements GlobalService, MediumInterface {

	private GlobalOperatingSystem operatingSystem;
	private GlobalKnowledge globalKnowledge;
    private AddressDeviceInfoMap addressDeviceInfoMap;
    private boolean visualize;
    private int count;
    
    
    public MediumMobileDevices() {
    	this(true);
    }

    public MediumMobileDevices(boolean visualize) {
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
            
            public void changeTrack(DeviceID deviceID, TrajectoryMapping trajectoryMapping, boolean suspended) {
        		// zur realisierung der Mobiltät 
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
		        	for (int i = 1; i <= 10; i++) {
		        		double sig = sendingRadius * Math.pow(i/ 10.0, 2);
		            	Extent extend = new Extent(sig * 2, sig * 2);
		            	shape.addShape(new EllipseShape(deviceID, extend, Color.GREEN, false));        		
		        	}	     
				
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
    
	// geschätzter Senderadius eines Device aufgrund des Two-Ray Ground Reflection Model
	// Nur zur Visualisierung benötigt
    private double calculateSendingRadius(DeviceID deviceID) {        
		DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
		Position devicePosition = globalKnowledge.getTrajectory(deviceID).getPosition();
		double signalStrength = deviceInfo.getSignalStrength();
		double receiveThreshold = deviceInfo.getReceiveThreshold(deviceInfo.getDataRate());
		double deviceHeight = devicePosition.getZ();
		if (deviceHeight <= 0)
			deviceHeight = 1;
		double antennaGain = deviceInfo.getAntennaGain();
		// Maximaler Senderadius für 
		// receiveThreshold = signalStrength - pathLoss
		double pathLoss = signalStrength - receiveThreshold;
		double sendingRadius = Math.pow(10, ((pathLoss + 20*log10(antennaGain) + 40*log10(deviceHeight)) /  40.0)); 
		return sendingRadius;
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
				DeviceIDIterator iterator = globalKnowledge.getNodes().iterator();
				while (iterator.hasNext()) {
					DeviceID deviceID = iterator.next();
					DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
					DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);
					double receivedPowerLevel = signalStrength - calculatePathLoss(sender, deviceID);
					double receiveThreshold = deviceInfo.getReceiveThreshold(senderInfo.getDataRate());
					if (receivedPowerLevel >= receiveThreshold) {
						// nur Devices in Reichweite des Senders sind von der Übertragung betroffen
						Set devicesInRangeSet = senderInfo.getDevicesInRangeSet();
						devicesInRangeSet.add(deviceID);
						if (!deviceInfo.isSending()) {
							// Wenn mehrere Devices gleichzeitig senden, darf diesen Stationen nichts weiter von den anderen gemeldet werden
							if (deviceInfo.getMediumState() == MediumInterface.IDLE) {
								// meldet allen untätigen Devices, dass der Empfang einer
								// neuen Nachricht beginnt und gibt das Frame an alle Devices weiter, 
								// die es empfangen können, so dass diese es weiter verarbeiten können
                                MacHeader macHeaderCopy=(MacHeader) macHeader.copy();
                                macHeaderCopy.setSignalStrength(receivedPowerLevel);
								operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new StartReceiptSignal(macHeaderCopy, frame));	
							}
							else if (deviceInfo.getMediumState() == MediumInterface.BUSY) {
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

    private void checkMotion(DeviceID sender, DeviceID deviceID) {        
		DeviceInfo senderInfo = addressDeviceInfoMap.getDeviceInfo(sender);		
		double signalStrength = senderInfo.getSignalStrength();
		Set devicesInRangeSet = senderInfo.getDevicesInRangeSet();
		DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(deviceID);
		double receivedPowerLevel = signalStrength - calculatePathLoss(sender, deviceID);
		double receiveThreshold = deviceInfo.getReceiveThreshold(senderInfo.getDataRate());
		if (receivedPowerLevel >= receiveThreshold) {
			// Das Device liegt in Reichweite des Senders und ist von der Übertragung betroffen
			if (!devicesInRangeSet.contains(deviceID)) {
				count++;
				// Das Device ist kürzlich in die Reichweite des sendenden Device geraten und muss ggf. auf Busy gesetzt werden
				devicesInRangeSet.add(deviceID);
				// Sendenden Devices wird nichts weiter mitgeteilt, da diese das Medium im Moment nicht abhören
				if (!deviceInfo.isSending()) {
					if (deviceInfo.getMediumState() == MediumInterface.IDLE) {
						// meldet allen untätigen Devices, dass das Medium BUSY ist
						operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new StartReceiptSignal());	
					}
					else if (deviceInfo.getMediumState() == MediumInterface.BUSY) {
						// In diesem Fall liegt eine Kollision vor, die dem Device ggf. gemeldet wird						
						if (!deviceInfo.hadCollision()) {
							deviceInfo.setCollision(true);
							operatingSystem.sendSignal(deviceID, deviceInfo.getServiceID(), new CollisionSignal());
						}
					}
				}
				deviceInfo.setMediumState(MediumInterface.BUSY);
			}
			else {
				// Das Device war vorher in Sendereichweite und jetzt auch, also ändert sich nichts
			}
		}
		else {
			// Das Device liegt nicht in Reichweite des Senderadius
			if (devicesInRangeSet.contains(deviceID)) {
				count++;
				// Das Device ist kürzlich aus der Reichweite des Senderasius geraten und muss ggf. auf IDLE gesetzt werden
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
				// Das Device war vorher nicht in Sendereichweite und jetzt auch nicht, also ändert sich nichts
			}				
		}		
    } 

	private class DeviceInfo {
        private DeviceID deviceID;
        private ServiceID serviceID;
        private int mediumState;
        private int busyCount;
		private boolean sending;
		private boolean collision;
		private Mib mib;
		private Set devicesInRangeSet; 
		private MotionTimeout motionTimeout;
		private MacHeader macHeader;
		private Frame frame;
		private double currentStartTime;

		public DeviceInfo(DeviceID deviceID, ServiceID serviceID, Mib mib) {
            this.deviceID = deviceID;
            this.serviceID = serviceID;
            this.mib = mib;
            mediumState = MediumInterface.IDLE;
            busyCount = 0;
            devicesInRangeSet = new HashSet();
            motionTimeout = new MotionTimeout();
            sending = false;
            collision = false;
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

        public boolean hadCollision() {
			return collision;
		}

		public void setCollision(boolean collision) {
			this.collision = collision;
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

		public void setSending(boolean sending, MacHeader macHeader, Frame frame) {
			this.sending = sending;
			this.macHeader = macHeader;
			this.frame = frame;
			if (sending)
				currentStartTime = operatingSystem.getSimulationTime();
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
	// Idee 2: nur bei beginn einer Bewegung Timer starten und nach Ende der Bewegung stoppen
	// Er überwacht, ob ein sendendes Device das mobile Device beeinflusst 
	private class MotionTimeout extends ServiceTimeout {
		private DeviceID mobileDevice;
		private boolean busy;
		private boolean motionFinished;
		
		public MotionTimeout() {
			super(0);
			busy = false;
			motionFinished = true;
		}
			
		public void set(DeviceID mobileDevice) {
			this.mobileDevice = mobileDevice;
		}

		public void start() {
			busy = true;
			motionFinished = false;
			delta = 300 / Mib.timeFactor;
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

	public static class StartReceiptSignal implements Signal {
        private MacHeader macHeader;
        private Frame frame;
        
        public StartReceiptSignal() {
        	this(null, null);
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
