/*****************************************************************************
* 
* MacLayer.java
* 
* $Id: MacLayer80211.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
import de.uni_trier.jane.random.ContinuousDistribution;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerObserver.LinkLayerObserverStub;
import de.uni_trier.jane.service.network.link_layer.extended.*;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.global_knowledge.GlobalKnowledge;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

import java.text.DecimalFormat;
import java.util.*;

// Die Realisierung der MAC-Layer eines WLAN Device
public class MacLayer80211 implements SimulationService, MacLayerInterface, PhysicalToMacLayerInterface, LinkLayerExtended {

	/**
     * @author goergen
     *
     * TODO comment class
     */
    public static final class MacLinkLayerConfiguration extends
            LinkLayerConfiguration {

        private int shortRetryLimit;
        private double signalStrengthFraq;

        /**
         * Constructor for class <code>MyLinkLayerConfiguration</code>
         * @param maxReceiveLifetime
         * @param longRetryLimit
         * @param shortRetryLimit
         */
        public MacLinkLayerConfiguration(double maxReceiveLifetime, int longRetryLimit, int shortRetryLimit,double signalStrengthFraq) {
            super(maxReceiveLifetime,longRetryLimit);
            if (0>=signalStrengthFraq||1<signalStrengthFraq) throw new IllegalArgumentException("Signalstrength factor must be between 0 and 1");
            this.shortRetryLimit=shortRetryLimit;
            this.signalStrengthFraq=signalStrengthFraq;
        }
        
        
        
        /**
         * Constructor for class <code>MyLinkLayerConfiguration</code>
         * @param configuration
         * @param defaultCnfiguration 
         */
        private MacLinkLayerConfiguration(LinkLayerConfiguration configuration, MacLinkLayerConfiguration defaultConfiguration) {
            super(configuration.getTimeout()<=0?defaultConfiguration.getTimeout():configuration.getTimeout(),
                    configuration.getRetries()<=0?defaultConfiguration.retries:configuration.getRetries());
            
            if (configuration instanceof MacLinkLayerConfiguration) {
                MacLinkLayerConfiguration myLinkLayerConfiguration = (MacLinkLayerConfiguration)configuration;
                shortRetryLimit=myLinkLayerConfiguration.shortRetryLimit<0?
                        defaultConfiguration.shortRetryLimit:
                        myLinkLayerConfiguration.shortRetryLimit;
                signalStrengthFraq=myLinkLayerConfiguration.signalStrengthFraq<=0||myLinkLayerConfiguration.signalStrengthFraq>1?
                        defaultConfiguration.signalStrengthFraq:
                            myLinkLayerConfiguration.signalStrengthFraq;
            }else{
                shortRetryLimit=defaultConfiguration.shortRetryLimit;
                signalStrengthFraq=defaultConfiguration.signalStrengthFraq;
            }
            
        }



        public LinkLayerConfiguration setDefaults(LinkLayerConfiguration configurationToTest) {
            
            return new MacLinkLayerConfiguration(configurationToTest,this);
            
        }
        
        /**
         * @return Returns the signalStrengthFraq.
         */
        public double getSignalStrengthFraq() {
            return this.signalStrengthFraq;
        }
        /**
         * @return Returns the shortRetryLimit.
         */
        public int getShortRetryLimit() {
            return this.shortRetryLimit;
        }



        /**
         * TODO Comment method
         * @return
         */
        public int getLongRetryLimit() {
            return getRetries();
        }

    }

    public static final ServiceID SERVICE_ID = new EndpointClassID(MacLayer80211.class.toString());
    
	private boolean debug = false;
    //private boolean visualize;
    
    private ServiceID physicalLayerServiceID;
    private ServiceID upperLayerServiceID;
    private MacToPhysicalLayerInterface physicalLayer;
    private MacToUpperLayerInterface upperLayer;

    private Mib mib;
    private MacLinkLayerConfiguration defaultConfiguration;
    private SimulationOperatingSystem operatingSystem;
    private GlobalKnowledge globalKnowledge;
    private ContinuousDistribution random;
    
    private BeaconTimeout beaconTimeout;
    private BackoffTimeout beaconBackoffTimeout;
    private SendTimeout sendTimeout;
    private ReceiveTimeout receiveTimeout;
    private NAVTimeout navTimeout;
    private NAVResetTimeout navResetTimeout;
    private WaitTimeout waitTimeout;
    private SIFSTimeout sifsTimeout;
    private DIFSTimeout difsTimeout;
    private EIFSTimeout eifsTimeout;
    private BackoffTimeout backoffTimeout;
    
    private int physicalState;
    private ArrayList messageQueue;
    private MessageEntry currentEntry;
    private boolean fcsError;
    private ReceiveCacheMap receiveCacheMap;
    private ConnectedDeviceMap connectedDeviceMap;

    private int sequenceNumber;
    private int cw;
    private int shortRetryCount;
    private int longRetryCount;
    private Address macAddress;

    private  LinkLayerExtended_Plugin_onSingleAddressedBC addressedBroadcastPlugin;

    protected LinkLayerObserverStub linkLayerObserver;

    private ContinuousDistribution messageProcessingTime;



    /**
     * 
     * Constructor for class <code>MacLayer80211</code>
     * @param macAddress
     * @param physicalLayerServiceID
     * @param mib
     * @param messageProcessingTime
     * @param upperLayerServiceID
     */
    public MacLayer80211(Address macAddress, ServiceID physicalLayerServiceID, Mib mib, ContinuousDistribution messageProcessingTime,  ServiceID upperLayerServiceID) {
        
        this.macAddress=macAddress;
		this.physicalLayerServiceID = physicalLayerServiceID;
		this.mib = mib;
        this.messageProcessingTime=messageProcessingTime;
        defaultConfiguration=new MacLinkLayerConfiguration(mib.getMaxReceiveLifetime(),mib.getLongRetryLimit(),mib.getShortRetryLimit(),1);
        this.upperLayerServiceID = upperLayerServiceID;
		
		messageQueue = new ArrayList();
		currentEntry = null;
		receiveCacheMap = new ReceiveCacheMap();
		connectedDeviceMap = new ConnectedDeviceMap();
		
		beaconTimeout = new BeaconTimeout();
		beaconBackoffTimeout = new BackoffTimeout();
		sendTimeout = new SendTimeout();
		receiveTimeout = new ReceiveTimeout();
		navTimeout = new NAVTimeout();
		navResetTimeout = new NAVResetTimeout();
		waitTimeout = new WaitTimeout();
		sifsTimeout = new SIFSTimeout();
		difsTimeout = new DIFSTimeout();
		eifsTimeout = new EIFSTimeout();
		backoffTimeout = new BackoffTimeout();
		
		fcsError = false;
		sequenceNumber = 0;
		cw = mib.getCWMin();
		shortRetryCount = 0;
		longRetryCount = 0;

        
    } 

//    public void setUpperLayerServiceID(ServiceID upperLayerServiceID) {        
//        this.upperLayerServiceID = upperLayerServiceID;
//	
//    } 

    public void start(SimulationOperatingSystem simulationOperatingSystem) {     
//        messageProcessingTime =                                                                      
//            simulationOperatingSystem.getDistributionCreator().getContinuousUniformDistribution(0.1, 0.0001);
        //simulationOperatingSystem.getDistributionCreator().getContinuousUniformDistribution(0.01, 0.000001);
		this.operatingSystem = simulationOperatingSystem;
        linkLayerObserver=new LinkLayerObserverStub(simulationOperatingSystem);
		random = operatingSystem.getDistributionCreator().getContinuousUniformDistribution(0,1);
		//macAddress = new SimulationLinkLayerAddress(operatingSystem.getDeviceID());
		operatingSystem.registerAddress(macAddress);
		mib.setMacAddress(macAddress);
		beaconTimeout.set();
		beaconTimeout.start();
		beaconTimeout.startBeaconProcedure();
		
        //register interface for exporting as stub
		operatingSystem.registerSignalListener(LinkLayerExtended_async.class);
        operatingSystem.registerAccessListener(LinkLayer_sync.class);
		operatingSystem.registerSignalListener(PhysicalToMacLayerInterface.class);
		operatingSystem.registerSignalListener(MacLayerInterface.class);
		globalKnowledge = operatingSystem.getGlobalKnowledge();
		
        //waiting for the other services to get up... 
		operatingSystem.setTimeout(new ServiceTimeout(0) {
			public void handle() {				
				physicalLayer = (MacToPhysicalLayerInterface) operatingSystem.getSignalListenerStub(physicalLayerServiceID, MacToPhysicalLayerInterface.class);
				upperLayer = (MacToUpperLayerInterface) operatingSystem.getSignalListenerStub(upperLayerServiceID, MacToUpperLayerInterface.class);
				setPhysicalState(PhysicalLayerInterface.CCA); 
			}
			
		});		 
        
        addressedBroadcastPlugin=new LinkLayerExtended_Plugin_onSingleAddressedBC(this,operatingSystem,defaultConfiguration);
		
//		operatingSystem.setTimeout(new ServiceTimeout(10) {
//			public void handle() {
//				debug = true;
//				visualize = true;
//			}
//			
//		});
    } 

    public ServiceID getServiceID() {        
		return SERVICE_ID;
    } 

    public void finish() {        
		// TODO Auto-generated method stub
    } 

    public void getParameters(Parameters parameters) {        
		// TODO Auto-generated method stub
		
    } 

    public Shape getShape() {        
		
			DeviceID deviceID = operatingSystem.getDeviceID();
	        ShapeCollection shape = new ShapeCollection();
	        Color color = Color.BLACK;
	        if (physicalState == PhysicalLayerInterface.TRANSMIT) {
	        	color = Color.GREEN;
	        }
	        else if (fcsError) {
	        	color = Color.RED;
	        }
	        else if (navTimeout.isBusy()) {
	        	color = Color.ORANGE;      
	        }
	        else if (physicalState == PhysicalLayerInterface.RECEIVE) {
	        	if (receiveTimeout.isBusy()) {
	    			if (operatingSystem.getDeviceID().equals(globalKnowledge.getDeviceID(receiveTimeout.getReceiver())) || receiveTimeout.getReceiver().equals(Mib.BROADCAST)) {
		           		color = Color.BLUE;
	    			}
	    			else {
		        		color = Color.mixColor(Color.BLUE, Color.WHITE,  0.5);
	    				
	    			}
	        	}
	        	else
	        		throw new IllegalStateException("Device ist ungültiges am empfangen.");
	        }
	        else if (sifsTimeout.isBusy() || difsTimeout.isBusy() || eifsTimeout.isBusy()) {
	        	color = Color.YELLOW;
	        }
	        else if (backoffTimeout.isBusy()) {
	        	color = Color.BROWN;
	        }
	        else if (waitTimeout.isBusy()) {
	        	color = Color.PINK;
	        }
	        
			shape.addShape(new EllipseShape(deviceID, new Extent(10, 10), color, true));
	        //shape.addShape(new TextShape(deviceID.toString(), deviceID, Color.BLACK), new Position(7,7,7));
	        return shape;
        
		
		
    } 

    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message) {
        sendAddressedBroadcast(receiver, message, defaultConfiguration, null);
        
    }
    //public void sendAddressedBroadcast(Address receiver, double timeoutDelta, LinkLayerMessage message, UnicastCallbackHandler callbackHandler) {
    public void sendAddressedBroadcast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, UnicastCallbackHandler callbackHandler) {
     
        if(debug){
            operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + "scheduling single addressed broadcast - ignoring timout delta!");
        }
        sendUnicast(receiver,message,(MacLinkLayerConfiguration) getConfiguration(configuration),callbackHandler,true); 
        
    }
    public void sendAddressedBroadcast(Address[] receivers, LinkLayerMessage message, LinkLayerConfiguration configuration, AddressedBroadcastCallbackHandler callbackHandler) {
        if(debug){
            operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + "emulating addressed broadcast");
        }
        addressedBroadcastPlugin.sendAddressedBroadcast(receivers,message,getConfiguration(configuration),callbackHandler);
        
    }
    /**
     * TODO: comment method 
     * @param configuration
     * @return
     */
    private LinkLayerConfiguration getConfiguration(LinkLayerConfiguration configuration) {
        if(configuration==null) return defaultConfiguration;
        return defaultConfiguration.setDefaults(configuration);
    }

    public void sendAddressedBroadcast(Address[] receivers, LinkLayerMessage message) {
        if(debug){
            operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + "emulating addressed broadcast");
        }
        addressedBroadcastPlugin.sendAddressedBroadcast(receivers,message);
        
    }
    public void sendAddressedMulticast(Address[] receivers,  LinkLayerMessage message, LinkLayerConfiguration configuration, AddressedBroadcastCallbackHandler callbackHandler) {
        if(debug){
            operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + "emulating addressed multicast");
        }
        addressedBroadcastPlugin.sendAddressedMulticast(receivers,message,getConfiguration(configuration), callbackHandler);
        
    }

    public void sendUnicast(Address receiver, LinkLayerMessage message) {        
		sendUnicast(receiver, message, null);
    }
    
    public void sendUnicast(Address receiver, LinkLayerMessage message, UnicastCallbackHandler callbackHandler){
       sendUnicast(receiver,message,defaultConfiguration,callbackHandler,false);
    }
    
    public void sendUnicast(Address receiver, LinkLayerMessage message, LinkLayerConfiguration configuration, UnicastCallbackHandler callbackHandler) {
        sendUnicast(receiver,message,(MacLinkLayerConfiguration) getConfiguration(configuration),callbackHandler,false);
        
    }

    protected void sendUnicast(final Address receiver, final LinkLayerMessage message, final MacLinkLayerConfiguration configuration, final UnicastCallbackHandler callbackHandler, final boolean promisc) {        
//		if (messageQueue.isEmpty()) {
//			resetShortRetryCount();
//			resetLongRetryCount();
//		}
        ///to avoid synchronous sending!!!
        operatingSystem.setTimeout(new ServiceTimeout(messageProcessingTime.getNext()){
            public void handle() {
                if (debug)
                    operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat neue Nachricht der Größe " + message.getSize() + " in MessageQueue eingefügt.");
                messageQueue.add(new UnicastMessageEntry(receiver, message, callbackHandler,promisc,configuration));
                if (messageQueue.size() == 1) {
                    checkSend();        
                }            
                
            }
        });
	
		
    } 

    public void sendBroadcast(LinkLayerMessage message) {        
		sendBroadcast(message, null);
    } 

 
    public void sendBroadcast(LinkLayerMessage message, BroadcastCallbackHandler callbackHandler) {
        sendBroadcast(message,defaultConfiguration,callbackHandler);
    }
    public void sendBroadcast(final LinkLayerMessage message, final LinkLayerConfiguration configuration, final BroadcastCallbackHandler callbackHandler) {
             
//		if (messageQueue.isEmpty()) {
//			resetShortRetryCount();
//			resetLongRetryCount();
//		}
        operatingSystem.setTimeout(new ServiceTimeout(messageProcessingTime.getNext()){
            public void handle() {
                
        		if (debug)
        			operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat neue Broadcast Nachricht der Größe " + message.getSize() + " in MessageQueue eingefügt.");
        		Frame frame = new DataFrame(message);
        		double txTime = transmitTime(mib.getDataRate(), frame.getSize());
        		MacHeader macHeader = MacHeader.createBroadcastHeader(mib.getMacAddress(), Mib.BROADCAST, txTime, mib.getDataRate());
        		messageQueue.add(new BroadcastMessageEntry(frame, macHeader, callbackHandler,(MacLinkLayerConfiguration) defaultConfiguration.setDefaults(configuration)));
        		if (messageQueue.size() == 1) {
        			checkSend();		
        		}
            }
                 
        });
    } 

    // Rundet value auf sechs Stellen hinterm Komma als double
    public static double roundUsec(double value) {        
		return Double.parseDouble(getTimeString(value));		
    } 

    // Rundet time auf sechs Stellen hinterm Komma als String
    public static String getTimeString(double time) {        
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
		//int length = String.valueOf((int) Mib.timeFactor).length();
        int length=15;
		String pattern = "0.";
		for (int i = 1; i < length; i++)
			pattern += "0";
		df.applyPattern(pattern);
		String s = df.format( time );
		return s;
    } 

    // Rundet die aktuelle Zeit auf sechs Stellen hinterm Komma als String
    private String getTimeString() {        
        return getTimeString(operatingSystem.getTime());
    } 

    // Berechnet die Übertragungszeit
    private double transmitTime(double dataRate, int size) {        
		double txTime;
		// TransmitTime in microseconds
		txTime = (int) (size / dataRate);
		if (txTime * dataRate < size)
			txTime += 1;
		txTime += mib.getPreambleLength() + mib.getPLCPHeaderTime();
		// TransmitTime in seconds
		txTime = roundUsec(txTime / Mib.timeFactor);
		return txTime;
    } 

    // Setzt den Zustand eines Device
    public void setPhysicalState(int physicalState) {        
		// Physical State von CCA auf TRANSMIT und umgekehrt 
		// werden in der MacLayer gehandhabt und dann an die Physical Layer weitergeleitet
		// Die restlichen Übergänge werden in der Physical Layer geregelt

		if (this.physicalState == physicalState) {
			throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Am physikalischen Zustand hat sich nichts geändert");
		}
		if ((this.physicalState == PhysicalLayerInterface.TRANSMIT) && (physicalState == PhysicalLayerInterface.RECEIVE))
			return ;
		this.physicalState = physicalState;
		
		// warten, dass alle übrigen aktionen die zur gleichen Zeit eintreten durchgeführt werden 
		operatingSystem.setTimeout(new ServiceTimeout(0) {
			public void handle() {
				carrierSense();
			}			
		});
    } 

    // Überprüft, ob ein Frame zum Versenden bereitsteht
    private void checkSend() {        
		nextMessage();
		if (currentEntry != null) {
			if (currentEntry.getFrameType() == MacHeader.BEACON) {
				if (physicalState == PhysicalLayerInterface.CCA) {
					// Beacon Frames werden direkt nach ablaufen des Backoff-Timers gesendet
					currentEntry.sendFrame();			
				}
				else
					throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Beacon senden, aber Medium Busy.");
			}
			else if ((difsTimeout.isFinished() || eifsTimeout.isFinished()) && !backoffTimeout.isBusy()) {
				// Daten Frames werden erst nach Abschluss der Backoff Prozedur gesendet
				currentEntry.sendFrame();
			}
			else {
				if (!difsTimeout.isBusy() && !eifsTimeout.isBusy() && !backoffTimeout.isSet() && !messageQueue.isEmpty()) {
					// Da das Senden nicht möglich ist, wird der Backoff Timer gesetzt
					backoffTimeout.set(cw);
				}
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Senden nicht möglich.");
			}	
		}
    } 

    // Aktualisiert die messageQueue und currentEntry
    private void nextMessage() {        
		// bestimmen der nächsten zu übertragenden Nachricht
		if (currentEntry == null) {
			if (messageQueue.isEmpty()) {
				return ;
			}
			currentEntry = (MessageEntry) messageQueue.get(0);
		}
		while ((currentEntry != null) && currentEntry.isFinished()) {
			int index = messageQueue.indexOf(currentEntry);
			if (index != -1)
				messageQueue.remove(index);
			currentEntry = null;
			if (messageQueue.isEmpty()) {
				return ;
			}
			currentEntry = (MessageEntry) messageQueue.get(0);
		}
    } 

    // Überprüft, ob das Medium belegt oder frei ist und fährt dementsprechend
    // das MAC-Protokoll weiter aus
    private void carrierSense() {        
		nextMessage();
		if (physicalState == PhysicalLayerInterface.CCA) {
			// Medium ist IDLE
			if (!difsTimeout.isBusy() && !eifsTimeout.isBusy() && !(backoffTimeout.isBusy() && !backoffTimeout.isPaused())) {
				if (receiveTimeout.isBusy() && (receiveTimeout.getRemainingTime() != 0)) {
					// Das Device war am empfangen, aber die Übertragung bricht ab
					if ((currentEntry != null) && (currentEntry.isWaitingForACK() || currentEntry.isWaitingForCTS() || currentEntry.isWaitingForFragment())) {
						// Device erwartet Ack, CTS oder nächstes Fragment, aber die Übertragung bricht ab.
						// Deshalb ist die Übertragung des vorigen Daten-Frames fehlerhaft bzw. das warten auf weitere Fragmente wird eingestellt.
						if (debug)
							operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " ein Fehler in der Übertragung ist aufgetreten, obwohl Frame erwartet wurde.");
						currentEntry.sendFailed();
					}
					if (!fcsError) {
						fcsError = true;
						mib.incFCSErrorCount();
					}
					receiveTimeout.stop();
				}
				if ((currentEntry == null) || ((currentEntry != null) && !currentEntry.isBusy())) {
					if (fcsError) {
						fcsError = false;
						// virtual carrier sense wird hier ignoriert
						if (navTimeout.isBusy()) {
							navTimeout.stop();
							if (navResetTimeout.isBusy())  {
								navResetTimeout.stop();
							}
						}
						if (beaconTimeout.beaconing()) {
							// TBTT Zeit: Beacon-Prozedur wird ausgeführt, nachdem auf Fertigstellung 
							// der vorigen Übertragung gewartet wurde.
							beaconTimeout.startBeaconProcedure();
						}
						else {
							eifsTimeout.set();
							eifsTimeout.start();						
						}
					}			
					else {
						if (!navTimeout.isBusy()) {
							if (beaconTimeout.beaconing()) {
								// TBTT Zeit: Beacon-Prozedur ausführen, nachdem auf Fertigstellung 
								// der vorigen Übertragung gewartet wurde.
								beaconTimeout.startBeaconProcedure();
							}
							else {
								difsTimeout.set();
								difsTimeout.start();	
							}
						}											
					}				
				}
			}
			else
				throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " DIFS Timeout bzw. EIFS Timeout doppelt gestartet.");
		}
		else {
			// Medium ist BUSY
			if (difsTimeout.isFinished()) 
				difsTimeout.invalid();
			else if (difsTimeout.isBusy()) 
				difsTimeout.stop();
			
			if (eifsTimeout.isFinished())
				eifsTimeout.invalid();
			else if (eifsTimeout.isBusy()) 
				eifsTimeout.stop();	
			
			if ((physicalState == PhysicalLayerInterface.RECEIVE) && !sifsTimeout.isBusy()) {
				if(!receiveTimeout.isBusy()) {
					if (!fcsError) {
						fcsError = true;
						mib.incFCSErrorCount();
					}
					if ((currentEntry != null) && (currentEntry.isWaitingForACK() || currentEntry.isWaitingForCTS() || currentEntry.isWaitingForFragment())) {
						// Device erwartet Ack, CTS oder nächstes Fragment, aber die Übertragung startet fehlerhaft ab.
						// Deshalb ist die Übertragung des vorigen Daten-Frames fehlerhaft bzw. das warten auf weitere Fragmente wird eingestellt.
						if (debug)
							operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " ein Fehler in der Übertragung ist aufgetreten, obwohl Frame erwartet wurde.");
						currentEntry.sendFailed();
					}
				}
				
				// die Beacon Prozedur wird gestoppt, da ein Frame empfangen wird
				if (beaconBackoffTimeout.isBusy() && (beaconBackoffTimeout.getRemainingTime() != 0)) {
					beaconBackoffTimeout.stop();
					if ((currentEntry != null) && (currentEntry.getFrameType() == MacHeader.BEACON)) {
						currentEntry.setFinished();
						if (debug)
							operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " die Beacon Prozedur wird gestoppt, da ein Frame empfangen wird.");
					}			
				}
				
				if (waitTimeout.isBusy()) {
					waitTimeout.stop();
				}
				if (navResetTimeout.isBusy())  {
					navResetTimeout.stop();
				}
				if (backoffTimeout.isBusy()) {
					if (!backoffTimeout.isPaused())
						backoffTimeout.pause();
				}
				else {
					if (!messageQueue.isEmpty() && !backoffTimeout.isSet()) {
						if (!currentEntry.isWaitingForFragment())
							backoffTimeout.set(cw);
					}
				}
			}
		}		
    } 

    // Beginnt eine Übertragung
    public void startTransmission(MacHeader macHeader, Frame frame, double signalFraction) {        
		setPhysicalState(PhysicalLayerInterface.TRANSMIT);
		physicalLayer.startTransmission(macHeader, frame, signalFraction);
		sendTimeout.set(macHeader.getTxTime());
		sendTimeout.start();
    } 

    // Beendet eine Übertragung
    private void transmissionFinished() {        
		setPhysicalState(PhysicalLayerInterface.CCA);
		fcsError = false;
		physicalLayer.endTransmission();
		currentEntry.sendFinished();
    } 

    // Ein neues Frame wird empfangen
    public void receiveFrame(MacHeader macHeader, Frame frame) {        
		if (physicalState != PhysicalLayerInterface.TRANSMIT) {
			if (!(backoffTimeout.getRemainingTime() ==  0.0) 
					&& !(((difsTimeout.getRemainingTime() == 0) || (eifsTimeout.getRemainingTime() == 0)) && !backoffTimeout.isSet())					
					&& !(beaconBackoffTimeout.getRemainingTime() == 0)
					&& !(receiveTimeout.getRemainingTime() == 0)
					&& !sifsTimeout.isBusy()) {
				if (receiveTimeout.isBusy()) {
					receiveTimeout.stop();
					if (debug)
						operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Empfang der Nachricht von Device " + receiveTimeout.getSender() + " abgebrochen, da ein stärkeres Signal empfangen wird.");
				}
				double txTime = macHeader.getTxTime();
				receiveTimeout.set(txTime, frame, macHeader);
				receiveTimeout.start();
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Empfang von Device " + macHeader.getSender() + " begonnen.");
			}
			else {
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " kann Nachricht von Device " + macHeader.getSender() + " nicht empfangen, da Device momentan beschäftigt ist.");			
			}		
		}
		else {
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " kann Nachricht von Device " + macHeader.getSender() + " nicht empfangen, da gesendet wird.");			
		}
    } 

    private void receiveFinished(Frame frame, MacHeader macHeader) {        
		frame.handle(macHeader, this);
    }
    
    private void receivePromisc(Frame frame, MacHeader macHeader) {        
        frame.handlePromisc(macHeader, this);
    }

    // Eine Kollsision/Fehler wird gemäß dem Protokoll verarbeitet
    public void collision() {        
		if (!fcsError) {
			fcsError = true;
			mib.incFCSErrorCount();
		}
		if ((currentEntry != null) && (currentEntry.isWaitingForACK() || currentEntry.isWaitingForCTS() || currentEntry.isWaitingForFragment())) {
			// Device erwartet Ack, CTS oder nächstes Fragment, aber es tritt ein Kollision auf.
			// Deshalb ist die Übertragung des vorigen Daten-Frames fehlerhaft bzw. das warten auf weitere Fragmente wird eingestellt.
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " eine Kollision ist aufgetreten, obwohl Frame erwartet wurde.");
			currentEntry.sendFailed();
		}
		if (physicalState == PhysicalLayerInterface.RECEIVE) {
			if (receiveTimeout.isBusy()) {
				receiveTimeout.stop();
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " kann Nachricht nicht empfangen, da eine Kollision aufgetreten ist.");
			}
		}
		else {
			//throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Es kann keine Kollision auftreten, falls das Device im Zustand CCA ist.");
            System.err.println("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Es kann keine Kollision auftreten, falls das Device im Zustand CCA ist.");
		}			
    } 
    
// Jeder Nachricht wird eine sequence number zugewiesen,
// die bei jeder Nachricht durch einen Modulo 4096 Zähler hochgesetzt wird.
// -> 7.1.3.4.1
    private int getSequenceNumber() {        
		int seq = sequenceNumber;
		sequenceNumber = (sequenceNumber + 1) % 4096;
		return seq;
    } 

    // Erhöht das Contention Window
    private void increaseCW() {        
		if(cw < mib.getCWMax())
			cw = (cw * 2 ) + 1;
		else
			cw = mib.getCWMax();
    } 

    // Setzt das Contention Window zurück
    private void resetCW() {        
		cw = mib.getCWMin();
    } 

//    // Erhöht den Short Retry Counter
//    private void increaseShortRetryCount() {        
//		shortRetryCount++;
//		if (shortRetryCount == mib.getShortRetryLimit()) {
//			resetCW();
//			resetShortRetryCount();
//		}
//		else {
//			increaseCW();
//		}
//    } 
//
//   // Setzt den Short Retry Counter zurück
//   private void resetShortRetryCount() {        
//		shortRetryCount = 0;
//    } 
//
//   // Erhöht den Long Retry Counter
//    private void increaseLongRetryCount() {        
//		longRetryCount++;
//		if (longRetryCount == mib.getLongRetryLimit()) {
//			resetCW();
//			resetLongRetryCount();
//		}
//		else {
//			increaseCW();
//		}
//    } 
//
//    // Setzt den Long Retry Counter zurück
//    private void resetLongRetryCount() {        
//		longRetryCount = 0;
//    } 

    // führt die Backoff-Prozedur aus 
    private void backoffProcedure() {        
		if (!backoffTimeout.isBusy()) {
			if (backoffTimeout.isSet()) {
				// Backoff Timer wurde gesetzt, wegen Fehlversuch oder da Medium belegt war
				backoffTimeout.start();
			}
			else { 
				// Backoff Timer wird nicht gestartet, falls nach eintreffen der Nachricht 
				// von der höheren Schicht das Medium direkt DIFS-Zeit frei ist
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Backoff Timer muss nicht gestartet werden.");
				checkSend();
			}
		}
		else {
			if (backoffTimeout.isPaused())
				backoffTimeout.resume();
			else
				throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " DIFS Timeout bzw. EIFS Timeout, obwohl Backoff-Timer aktiv ist.");
		}
    } 
    
    // Oberklasse für alle Timeouts
    // Die Timeouts lassen sich alle über Funktionen setzen, starten, stoppen 
    // und es können Informationen über den momentanen Zustand des Timeouts abgefragt werden
	private abstract class MacTimeout extends ServiceTimeout {
	
	    protected String name;
	    protected boolean busy;
	    protected double startTime;
	    
	    public MacTimeout() {        
			super(0);
			busy = false;
	    } 
	
	    public void start() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat " + getName() + " gestartet für Dauer " + getTimeString(delta));
			busy = true;
			startTime = operatingSystem.getTime();
			operatingSystem.setTimeout(this);
	    } 
	
	    public void stop() {        
			delta = 0;
			startTime = 0;
			operatingSystem.removeTimeout(this);			
			busy = false;
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat " + getName() + " gestoppt.");
	    } 
	
	    public boolean isBusy() {        
			return busy;
	    } 
	
	    public double getRemainingTime() {        
			return delta - (operatingSystem.getTime() - startTime);
	    } 
	
	    public String getName() {        
			return name;
	    } 
	}

	// Beacon Intervall
	private class BeaconTimeout extends MacTimeout {

	    private boolean beaconing;
	
	    public BeaconTimeout() {        
    		super();
    		name = "Beacon Timeout";
    		beaconing = false;
	    } 

	    public void set() {        
			delta = mib.getBeaconIntervall();
	    } 

	    public void handle() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " es ist TBTT Zeit.");
			connectedDeviceMap.beaconMissed();
			beaconing = true;
			beaconTimeout.set();
			beaconTimeout.start();
			if ((physicalState == PhysicalLayerInterface.CCA) 
					&& ((currentEntry == null) || ((currentEntry != null) && !currentEntry.isBusy()))
					&& !navTimeout.isBusy()) {
				// Es wird gerade keine Übertragung durchgeführt. 
				// Deshalb kann die Beacon-Prozedur sofort gestartet werden.
				// Gegebenenfalls muss das laufende DIFS bzw. EIFS Timeout gestoppt werden
				// und der laufende Backoff Timer angehalten werden
				// -> 11.1.2
				if (difsTimeout.isFinished()) 
					difsTimeout.invalid();
				else if (difsTimeout.isBusy()) 
					difsTimeout.stop();
				if (eifsTimeout.isFinished())
					eifsTimeout.invalid();
				else if (eifsTimeout.isBusy()) 
					eifsTimeout.stop();
				if (backoffTimeout.isBusy() && !backoffTimeout.isPaused())
					backoffTimeout.pause();
				startBeaconProcedure();
			}
	    } 
	
	    public void startBeaconProcedure() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " die Beacon Prozedur wird gestartet.");
			Frame frame = new BeaconFrame();
			double txTime = transmitTime(mib.getDataRate(), frame.getSize());
			MacHeader macHeader = MacHeader.createBeaconHeader(mib.getMacAddress(), Mib.BROADCAST, txTime, mib.getDataRate());
			currentEntry = new BroadcastMessageEntry(frame, macHeader, null,defaultConfiguration);
			currentEntry.setBusy(true);
			messageQueue.add(0, currentEntry);
			beaconBackoffTimeout.set(2 * mib.getCWMin());
			beaconBackoffTimeout.start();
			beaconing = false;
	    } 
	
	    public boolean beaconing() {        
			return beaconing;
	    } 
	
	    public void stopBeaconing() {        
			beaconing = false;
	    } 
	}

	// Timeout, dass das Ende des Sendens angibt
	private class SendTimeout extends MacTimeout {
	
	    public  SendTimeout() {        
			super();
    		name = "Send Timeout";
	    } 
	
	    public void set(double txTime) {        
			delta = txTime;
	    } 
	
	    public void handle() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat fertig gesendet.");
			busy = false;
			transmissionFinished();
	    } 
	}

	// Timeout, dass das Ende des Empfangs angibt
	private class ReceiveTimeout extends MacTimeout {
	    private Frame frame;
	    private MacHeader macHeader;
	
	    public ReceiveTimeout() {        
			super();
    		name = "Recieve Timeout";
	    } 
	
	    public void set(double txTime, Frame frame, MacHeader macHeader) {        
			delta = txTime;
    		this.frame = frame;
    		this.macHeader = macHeader;
	    } 
	
	    public void handle() {        
    		busy = false;
			if ( macHeader.getReceiver().equals(Mib.BROADCAST)||operatingSystem.getDeviceID().equals(globalKnowledge.getDeviceID(macHeader.getReceiver())) ) {
				// Das Frame ist für dieses Device und wird jetzt weiter verarbeitet
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Frame von Device " + macHeader.getSender() + " empfangen.");
	    		receiveFinished(frame, macHeader);
			}
			else {
                if(mib.isPromiscous()||macHeader.isPromiscous()){
                    receivePromisc(frame,macHeader);
                }
                
				// Das Frame ist nicht für dieses Device,
				// aber die mitgelieferte Duration wird weiter verarbeitet 
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Duration erhalten von Device " + macHeader.getSender() + " für Dauer " + getTimeString(macHeader.getDuration()));
				handleDuration(macHeader);
			}
	    } 
	
	    public Address getSender() {        
			return macHeader.getSender();
	    } 
	
	    public Address getReceiver() {        
			return macHeader.getReceiver();
	    } 
	}

	// NAV-Timer
	private class NAVTimeout extends MacTimeout {
	
	    public  NAVTimeout() {        
			super();
    		name = "NAV Timeout";
	    } 
	
	    public void set(double delta) {        
			this.delta =delta;
	    } 
	
	    public void reset(double duration) {        
			if (isBusy()) {
				operatingSystem.removeTimeout(this);
				delta = duration;
				startTime = operatingSystem.getTime();
				operatingSystem.setTimeout(this);
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " NAV Timer neu gesetzt für Dauer " + getTimeString(delta));				
			}
			else
				throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " NAV Timer kann nicht neu gesetzt werden, da er nicht aktiv ist."); 		
	    } 
	
	    public void handle() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat NAV-Timeout beendet.");
			if (navResetTimeout.isBusy())  {
				navResetTimeout.stop();
			}
			busy = false;
			if (physicalState == PhysicalLayerInterface.CCA) 
				carrierSense();
	    } 
	}

	// Timeout zum warten auf das nächste Fragment/ACK/CTS
	private class WaitTimeout extends MacTimeout {
	
	    public  WaitTimeout() {        
			super();
    		name = "Wait Timeout";
	    } 
	
	    public void setAckTimeout() {        
			delta = mib.getSIFS() + transmitTime(mib.getDataRate(), ACKFrame.size) + mib.getSlotTime();
			name = "Ack Timeout";
	    } 
	
	    public void setCTSTimeout() {        
			delta = mib.getSIFS() + transmitTime(mib.getDataRate(), ACKFrame.size) + mib.getSlotTime();
			name = "CTS Timeout";
	    } 
	
	    public void setFragmentTimeout(double delta) {        
			this.delta = delta;
			name = "Fragment Timeout";
	    } 
	
	    public void handle() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat " + getName() + " beendet.");
			currentEntry.sendFailed();
			busy = false;
			// Die Übertragung ist fehlgeschlagen und die Backoff-Prozedur muss vor erneutem senden durchgeführt werden
			if (physicalState == PhysicalLayerInterface.CCA) 
				carrierSense();
			else
				throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " " + getName() + " obwohl Medium Busy.");
	    } 
	}

// Falls die duration Information zum setzen des NAV Timers von einem RTS Frame stammt,
// wird durch diesen Timer sichergestellt, dass das Device nicht unnötig lange wartet
// falls keine weitere Kommunikation stattfindet.
// Falls während der Dauer des Timers keine Nachricht empfangen wird, darf der NAV Timer gestoppt werden.
// -> 9.2.5.4
	private class NAVResetTimeout extends MacTimeout {
	
	    public NAVResetTimeout() {        
	    	super();
    		name = "NAV Reset Timeout";
	    } 

	    public void set() {        
			delta = 2 * mib.getSIFS() + transmitTime(mib.getDataRate(), ACKFrame.size) + 2 * mib.getSlotTime();
	    } 
	
	    public void handle() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat während NAVReset Timeout keine Nachricht empfangen und setzt NAV Timer zurück.");
			busy = false;
			navTimeout.stop();
			if (physicalState == PhysicalLayerInterface.CCA) 
				carrierSense();
			else
				throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " NAV Reset obwohl Medium Busy.");
	    } 
	}

	// SIFS-Intervall
	private class SIFSTimeout extends MacTimeout {

		public SIFSTimeout() {        
			super();
    		name = "SIFS Timeout";
		} 

		public void set() {        
			delta = mib.getSIFS();
		} 

		public void handle() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " SIFS Zeit erfolgreich abgeschlossen.");
			currentEntry.sendFrame();
			busy = false;
		} 
	}

	// DIFS-Intervall
	private class DIFSTimeout extends MacTimeout {

		private boolean finished;

		public DIFSTimeout() {        
			super();
    		name = "DIFS Timeout";
			finished = false;
		} 

		public void set() {        
			delta = mib.getDIFS();
			finished = false;
		} 

		public boolean isFinished() {        
			return finished;
		} 

		public void invalid() {        
			finished = false;
		} 

		public void handle() {        
			if (busy) {
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " DIFS Zeit erfolgreich abgeschlossen.");
				finished = true;
				busy = false;
				backoffProcedure();
			}
		} 
	}

	// EIFS-Intervall
	private class EIFSTimeout extends MacTimeout {

		private boolean finished;

		public EIFSTimeout() {        
			super();
    		name = "EIFS Timeout";
			finished = false;
	    } 

		public void set() {        
			delta = mib.getEIFS();
			finished = false;
		} 

		public boolean isFinished() {        
			return finished;
		} 

		public void invalid() {        
			finished = false;
		} 

		public void handle() {        
			if (busy) {
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " EIFS Zeit erfolgreich abgeschlossen.");
				finished = true;
				busy = false;
				backoffProcedure();
			}
	    } 
	}

	// Gibt das Ende der Backoff-Zeit an (und den momentanen Zustand)
	private class BackoffTimeout extends MacTimeout {

		private boolean paused;
		private boolean isSet;

		public BackoffTimeout() {        
			super();
    		name = "Backoff Timeout";
			paused = false;
			isSet = false;
		} 

		public void stop() {        
			paused = false;
			isSet = false;			
			super.stop();
		} 

		public void set(int cw) {        
//			delta = (int) (random.getNext() * (cw + 1)) * mib.getSlotTime();
			// Backoff Timer setzen auf Wert zwischen (0,cw]
			// nicht auf 0 setzen, wie laut Standard erlaubt (9.2.4)
			// da sonst Fehler auftreten beim Beaconing 
			// (senden und empfangen zur gleichen Zeit, da keine IFS Zeit gewartet wird) 
			delta = ((int) (random.getNext() * (cw)) + 1) * mib.getSlotTime();
			isSet = true;
			paused = false;
		} 

		public boolean isSet() {        
			return isSet;
		} 

		public void handle() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Backoff Timer abgelaufen.");
			isSet = false;
			paused = false;
			busy = false;
			checkSend();
		} 

		public void pause() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Backoff Timer angehalten.");
			paused = true;
			operatingSystem.removeTimeout(this);
			int slots = (int) ((operatingSystem.getTime() - startTime) / mib.getSlotTime());
			if (slots < 0)
				slots = 0;
			delta = delta - (slots * mib.getSlotTime());
			if (delta < 0)
				throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Backoff kann nicht angehalten werden, da es bereits abgelaufen ist"); 		
		} 

		public void resume() {        
			if (isPaused()) {
				startTime = operatingSystem.getTime();
				operatingSystem.setTimeout(this);
				paused = false;
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Backoff Timer fortgesetzt für Zeit " + getTimeString(delta));				
			}
			else
				throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Backoff kann nicht fortgesetzt werden, da es nicht angehalten wurde."); 		
		} 

		public boolean isPaused() {        
			return paused;
		} 

		public double getRemainingTime() {        
			if (isPaused() || !isBusy())
				return -1;
			return delta - (operatingSystem.getTime() - startTime);
		} 
	}

	// Die Maximale Lebensdauer einer zu übertragenden Nachricht
	private class MaxTransmitMSDULifetimeTimeout extends MacTimeout {
		
		private MessageEntry msdu;

		public MaxTransmitMSDULifetimeTimeout() {        
			super();
    		name = "MaxTransmitMSDULifetime Timeout";
		} 

		public void set(MessageEntry msdu, LinkLayerConfiguration configuration) {        
			delta = configuration.getTimeout();//mib.getMaxTransmitMSDULifetime();
			this.msdu = msdu;
		} 

		public void handle() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " MaxTransmitMSDULifetime Timer abgelaufen.");
			if (!msdu.isBusy())
				msdu.discard();
			busy = false;
		} 
	}

	// Die maximale Lebensdauer einer empfangenen Nachricht
	private class MaxReceiveLifetimeTimeout extends ServiceTimeout {

		private ReceiveCacheKey key;

    	public MaxReceiveLifetimeTimeout(ReceiveCacheKey key) {        
			super(mib.getMaxReceiveLifetime());
			this.key = key;
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat MaxReceiveLifetime Timeout gestartet für Dauer " + getTimeString(delta));
    	} 

    	public void handle() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " MaxReceiveLifetime Timer abgelaufen.");
			receiveCacheMap.remove(key);
    	} 
	}

	// Die Oberklasse für einen Eintrag der messageQueue
	// Enthält verschiedene Funktionen im Zusammenhang mit dem Versenden des enthaltenen Frames
	// und liefert Auskunft über den Zustand des Eintrags
	private abstract class MessageEntry {

		private boolean finished;
		private boolean busy;
		protected Address receiver;
		protected Frame frame;
		protected MacHeader macHeader;
		protected double txTime;

		public MessageEntry() {        
			receiver = null;
			frame = null;
			macHeader = null;
			finished = false;
			busy = false;
			txTime = 0;
		} 

		public abstract boolean isUnicast();

		public void sendFailed() {        
			throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Die Funktion darf hier nicht ausgeführt werden.");
		} 

		public void sendSuccessful() {        
			throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Die Funktion darf hier nicht ausgeführt werden.");
		} 

		public void discard() {        
			throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Die Funktion darf hier nicht ausgeführt werden.");			
		} 

		public Shape getMessageShape() {        
			if (frame == null)
				return null;
		    return frame.getShape();
		} 

		public abstract void sendFrame();

		public abstract void sendFinished();

		public boolean isBusy() {        
			return busy;
		} 

		public void setBusy(boolean busy) {        
			this.busy = busy;
		} 

		public boolean isFinished() {        
			return finished;
		} 

		public void setFinished() {        
			this.finished = true;
			setBusy(false);
			nextMessage();
		} 

		public Address getReceiver() {        
			return receiver;
		} 

		public boolean isWaitingForACK() {        
			return false;
		} 

		public boolean isWaitingForCTS() {        
			return false;
		} 

		public boolean isWaitingForFragment() {        
			return false;
		} 

		public int getFrameType() {        
			return macHeader.getFrameType();
		} 

		public LinkLayerMessage getMessage() {        
			return null;			
		} 
	}

	// messageQueue Eintrag für alle per Unicast verschickten Daten-Frames
	private class UnicastMessageEntry extends MessageEntry {

		private LinkLayerMessage message;
		private UnicastCallbackHandler callbackHandler;
		private int totalFragments;
		private int fragmentNumber;
		private int sequenceNumber;
		private boolean rtsCtsMechanism;
		private boolean retry;
//		private int shortRetryCount;
//		private int longRetryCount;
		private int numberRetries;
		private boolean waitingForCTS;
		private boolean waitingForACK;
		private boolean processed;		
		private MaxTransmitMSDULifetimeTimeout maxTransmitMSDULifetimeTimeout;
		private int messageSizeBytes;
        private boolean promisc;
        private MacLinkLayerConfiguration configuration;

		public UnicastMessageEntry(Address receiver, LinkLayerMessage message, UnicastCallbackHandler callbackHandler, boolean promisc, MacLinkLayerConfiguration configuration) {
            
        	super();
            this.configuration=configuration;
			this.receiver = receiver;
			this.message = message;
            this.promisc=promisc;
			this.callbackHandler = callbackHandler;
        	this.sequenceNumber = getSequenceNumber();
        	shortRetryCount = 0;
        	longRetryCount = 0;
        	numberRetries = 0;
        	retry = false;
        	waitingForCTS = false;
			waitingForACK = false;
			processed = false;
			maxTransmitMSDULifetimeTimeout = new MaxTransmitMSDULifetimeTimeout();
			messageSizeBytes = message.getSize() / 8;
			if ((messageSizeBytes * 8) < message.getSize())
				messageSizeBytes++;
			
			// es wird überprüft, ob die Nachricht Fragmentiert werden muss
			if (messageSizeBytes > mib.getFragmentationThreshold()) {
				// Die Nachricht muss so zerlegt werden, dass die resultierenden Fragmente (Frame Body + Header) 
				// nicht größer als FragmentationThreshold sind (in Bytes) -> 9.1.4
				totalFragments = messageSizeBytes / (mib.getFragmentationThreshold() - 28);  
	        	if (totalFragments * (mib.getFragmentationThreshold() - 28) < messageSizeBytes)
	        		totalFragments++;
			}
			else {
        		totalFragments = 1;
			}
			fragmentNumber = 0;
			if (totalFragments > 1)
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Nachricht in " + totalFragments + " Fragmente zerlegt.");			
			
        	// es wird überprüft, ob der RTS/CTS Mechanismus angewendet werden muss
        	rtsCtsMechanism = false;
        	Frame nextFragment = getFragment(fragmentNumber);
			int fragmentSizeBytes = nextFragment.getSize() / 8;
			if ((fragmentSizeBytes * 8) < nextFragment.getSize())
				fragmentSizeBytes++;
    		if (fragmentSizeBytes > mib.getRTSThreshold()) {
    			rtsCtsMechanism = true;
        	}
			setFrameToSend();
		} 

		private Frame getFragment(int number) {        
        	Frame fragment;
        	if ((number + 1) < totalFragments) {
        		// Fragment der Nachricht, aber nicht das letzte
        		int messageSize = mib.getFragmentationThreshold() - 28;
				fragment = new DataFrame(messageSize * 8, message.getShape());       		        		
        	}
        	else {
 				if (totalFragments == 1) {
 		       		// komplette Nachricht ohne Fragmentierung
 					fragment = new DataFrame(message);
 				}
				else {
					// letztes Fragment der Nachricht
					int messageSize = messageSizeBytes - ((totalFragments - 1) * (mib.getFragmentationThreshold() - 28));
 					fragment = new DataFrame(message, messageSize * 8);
				}
        	}
			return fragment;
		} 

		private void setFrameToSend() {        
        	if (rtsCtsMechanism) {
        		// RTS Frame wird als nächstes gesendet
				frame = new RTSFrame();
				txTime = transmitTime(mib.getDataRate(), frame.getSize());
	        	Frame nextFragment = getFragment(fragmentNumber);
	        	// Duration für RTS Frame berechnet wie in 7.2.1.1 beschrieben
				double duration = transmitTime(mib.getDataRate(), nextFragment.getSize()) + 2 * transmitTime(mib.getDataRate(), ACKFrame.size) + 3 * mib.getSIFS(); 
				macHeader = MacHeader.createRTSHeader(mib.getMacAddress(), receiver, txTime, mib.getDataRate(), duration);		        		
       		}
        	else {
        		// das nächste Fragment de Nachricht wird gesendet
    			frame = getFragment(fragmentNumber);
    			double duration;
    			boolean moreFragments = ((fragmentNumber + 1) < totalFragments);
	        	// Duration für Data Frame berechnet wie in 7.2.2 beschrieben
    			if (moreFragments) {
            		// Duration für ein Fragment der Nachricht, aber nicht das letzte 				
	    	        Frame nextFragment = getFragment(fragmentNumber + 1);
	   				duration = transmitTime(mib.getDataRate(), nextFragment.getSize()) + 2 * transmitTime(mib.getDataRate(), ACKFrame.size) + 3 * mib.getSIFS(); 
    			}
     			else {
     	   			// Duration für ganze Nachricht ohne Fragmentierung bzw. letztes Fragment
    				duration = transmitTime(mib.getDataRate(), ACKFrame.size) + mib.getSIFS(); 
    			}
				txTime = transmitTime(mib.getDataRate(), frame.getSize());
				macHeader = MacHeader.createDataHeader(mib.getMacAddress(), receiver, txTime, mib.getDataRate(), moreFragments, retry, duration, fragmentNumber, this.sequenceNumber,promisc);		
        	}    } 

		public void sendFrame() {        
			setBusy(true);
			if (!maxTransmitMSDULifetimeTimeout.isBusy()) {
				maxTransmitMSDULifetimeTimeout.set(this,configuration);
				maxTransmitMSDULifetimeTimeout.start();
			}
			startTransmission(macHeader, frame, configuration.getSignalStrengthFraq());
			// Ausgabe
        	if (rtsCtsMechanism) {
        		if (debug)
        			operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " sendet " + frame.getName() + " an Device " + macHeader.getReceiver() + " für Zeit " + getTimeString(txTime));
       		}
        	else {
				if (totalFragments > 1) {
					if (debug)
						operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " sendet " + frame.getName() + " mit Fragment " + fragmentNumber + " der Größe " + frame.getSize() + " an Device " + macHeader.getReceiver() + " für Zeit " + getTimeString(txTime));
				}
				else {
					if (debug)
						operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " sendet " + frame.getName() + " mit kompletter Nachricht an Device " + macHeader.getReceiver() + " für Zeit " + getTimeString(txTime));
				}
        	}
		} 

		public void sendFinished() {        
			if (((fragmentNumber + 1) == totalFragments) && !processed) {
                linkLayerObserver.notifyUnicastProcessed(receiver, message);
				if (callbackHandler != null) {
					callbackHandler.notifyUnicastProcessed(receiver, message);
				}				
                processed = true;
			}
			
			if (rtsCtsMechanism) {
				waitingForCTS = true;
				waitTimeout.setCTSTimeout();
			}
			else {
				waitingForACK = true;				
				waitTimeout.setAckTimeout();
			}
			waitTimeout.start();
		} 

		public void sendFailed() {        
			setBusy(false);
			if (rtsCtsMechanism) {
				// Ein RTS Frame wurde gesendet, aber kein CTS Frame als Antwort empfangen
				mib.incRTSFailureCount();
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Übertragung fehlgeschlagen, da kein CTS empfangen wurde.");				
				waitingForCTS = false;
				shortRetryCount ++;
				increaseShortRetryCount();
			}
			else {
				// Ein DATA Frame wurde gesendet, aber kein ACK Frame als Antwort empfangen
				mib.incACKFailureCount();
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Übertragung fehlgeschlagen, da kein Ack empfangen wurde.");				
				retry = true;
				waitingForACK = false;
				int frameSizeBytes = frame.getSize() / 8;
				if ((frameSizeBytes * 8) < frame.getSize())
					frameSizeBytes++;
				if (frameSizeBytes <= mib.getRTSThreshold()) {
					increaseShortRetryCount();
					shortRetryCount ++;				
				}
				else {
					increaseLongRetryCount();
					longRetryCount ++;								
	    			rtsCtsMechanism = true;
				}
			}
			numberRetries ++;
			backoffTimeout.set(cw);
			if ((shortRetryCount == configuration.getShortRetryLimit()) || (longRetryCount == configuration.getLongRetryLimit()) || !maxTransmitMSDULifetimeTimeout.isBusy()) {
				maxTransmitMSDULifetimeTimeout.stop();
				discard();
			}
			else {
				setFrameToSend();
			}
		} 
        // Erhöht den Short Retry Counter
        private void increaseShortRetryCount() {        
            shortRetryCount++;
            if (shortRetryCount == configuration.getShortRetryLimit()) {
                resetCW();
                resetShortRetryCount();
            }
            else {
                increaseCW();
            }
        } 

       // Setzt den Short Retry Counter zurück
       private void resetShortRetryCount() {        
            shortRetryCount = 0;
        } 

       // Erhöht den Long Retry Counter
        private void increaseLongRetryCount() {        
            longRetryCount++;
            if (longRetryCount == configuration.getLongRetryLimit()) {
                resetCW();
                resetLongRetryCount();
            }
            else {
                increaseCW();
            }
        } 

        // Setzt den Long Retry Counter zurück
        private void resetLongRetryCount() {        
            longRetryCount = 0;
        } 


		public void sendSuccessful() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Übertragung des " + frame.getName() + " erfolgreich.");			
			// Zurücksetzen der zuständigen retry counter
			if (rtsCtsMechanism) {
				// Ein RTS Frame wurde gesendet und mit einem CTS Frame beantwortet
				mib.incRTSSuccessCount();
				rtsCtsMechanism = false;
				waitingForCTS = false;
				shortRetryCount = 0;
				resetShortRetryCount();
			}
			else {
				// Ein Fragment wurde gesendet und mit einem ACK Frame beantwortet
				mib.incTransmittedFragmentCount();
				fragmentNumber++;
				retry = false;
				waitingForACK = false;
				int frameSizeBytes = frame.getSize() / 8;
				if ((frameSizeBytes * 8) < frame.getSize())
					frameSizeBytes++;
				if (frameSizeBytes <= mib.getRTSThreshold()) {
					shortRetryCount = 0;
					resetShortRetryCount();			
				}
				else {
					longRetryCount = 0;
					resetLongRetryCount();
				}
			}
						
			if (fragmentNumber < totalFragments) {
				if (!maxTransmitMSDULifetimeTimeout.isBusy()) {
					// die maximale Übertragungszeit für dieses Frame ist abgelaufen, 
					// also wird die Übertragung abgebrochen und die Nachricht verworfen
					setBusy(false);
					discard();
				}
				else {
					// senden des nächsten Fragments
					sifsTimeout.set();
					sifsTimeout.start();								
					setFrameToSend();
				}
			}
			else {
				// sonst Abschluss des Nachrichteneintrags 
				if (numberRetries > 0) {
					mib.incRetryCount();
					if (numberRetries > 1)
						mib.incMultipleRetryCount();
				}
				setBusy(false);
				maxTransmitMSDULifetimeTimeout.stop();
                linkLayerObserver.notifyUnicastReceived(receiver, message);
				if (callbackHandler != null) {
					callbackHandler.notifyUnicastReceived(receiver, message);
				}
				finishMessage(this);
			}
			
		} 
        
//        // Erhöht den Short Retry Counter
//        private void increaseShortRetryCount() {        
//            shortRetryCount++;
//            if (shortRetryCount == mib.getShortRetryLimit()) {
//                resetCW();
//                resetShortRetryCount();
//            }
//            else {
//                increaseCW();
//            }
//        } 
//
//
//       /**
//         * TODO Comment method
//         */
//        private void resetShortRetryCount() {
//            shortRetryCount=0;
//            resetCW();
//            
//        }
//
//    // Erhöht den Long Retry Counter
//      private void increaseLongRetryCount() {        
//          longRetryCount++;
//          if (longRetryCount == mib.getLongRetryLimit()) {
//              resetCW();
//              longRetryCount=0;
//          }
//          else {
//              increaseCW();
//          }
//      } 

		public void discard() {     
            if (!processed){
                linkLayerObserver.notifyUnicastProcessed(receiver, message);
                if (callbackHandler != null) 
                    callbackHandler.notifyUnicastProcessed(receiver, message);
                processed=true;
            }
				if ((fragmentNumber + 1) < totalFragments) {
                    linkLayerObserver.notifyUnicastLost(receiver, message);
                    if (callbackHandler != null) 
                        callbackHandler.notifyUnicastLost(receiver, message);										
				}
				else {
                    linkLayerObserver.notifyUnicastUndefined(receiver, message);
                    if (callbackHandler != null)
					callbackHandler.notifyUnicastUndefined(receiver, message);					
				}
			
			discardMessage(this);
		} 

		public boolean isWaitingForACK() {        
			return waitingForACK;
		} 

		public boolean isWaitingForCTS() {        
			return waitingForCTS;
		} 

		public LinkLayerMessage getMessage() {        
			return message;
		} 

		public boolean isUnicast() {        
			return true;
		} 
	}

	// messageQueue Eintrag für alle Kontroll-Frames
	private class ControlMessageEntry extends MessageEntry {

	    private boolean waitingForFragment;
	    
	    public ControlMessageEntry(Address receiver, Frame frame, double duration) {        
        	super();
			this.receiver = receiver;
			this.frame = frame;
			waitingForFragment = false;
			txTime = transmitTime(mib.getDataRate(), frame.getSize());
			macHeader = MacHeader.createACK_CTSHeader(mib.getMacAddress(), receiver, txTime, mib.getDataRate(), duration);		
	    } 

	    public void sendFrame() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " sendet " + frame.getName() + " an Device " + macHeader.getReceiver() + " für Zeit " + getTimeString(txTime));
			startTransmission(macHeader, frame, 1);
	    } 

	    public void sendFinished() {        
			if (macHeader.getDuration() > 0) {
				waitingForFragment = true;
				// Timer bei weiteren erwarteten Fragmenten der Übertragung zum Warten auf nächstes Fragment (siehe NS-2)
				// Wartezeit: geschätzte Übertragungszeit für nächstes Fragment + Slot-Time (analog zu Ack-CTS Timeout)
				double timeout = macHeader.getDuration() - transmitTime(mib.getDataRate(), ACKFrame.size) - mib.getSIFS();
				waitTimeout.setFragmentTimeout(timeout);
				waitTimeout.start();
			}
			else {
				setFinished();		
			}
	    } 

	    public void sendFailed() {        
			waitingForFragment = false;
			setFinished();		
	    } 

	    public void sendSuccessful() {        
			waitingForFragment = false;
			setFinished();			
	    } 

	    public boolean isWaitingForFragment() {        
			return waitingForFragment;
	    } 

	    public boolean isUnicast() {        
			return true;
	    } 
	}

	// messageQueue Eintrag für alle per Broadcast verschickten Frames
	private class BroadcastMessageEntry extends MessageEntry {

		private BroadcastCallbackHandler callbackHandler;
        private MacLinkLayerConfiguration configuration;

		public BroadcastMessageEntry(Frame frame, MacHeader macHeader, BroadcastCallbackHandler callbackHandler, MacLinkLayerConfiguration configuration) {        
			super();
			this.frame = frame;
			this.macHeader = macHeader;
			this.callbackHandler = callbackHandler;
            this.configuration=configuration;
			txTime = macHeader.getTxTime();
		} 

		public void sendFrame() {        
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " sendet Broadcast " + frame.getName() + " für Zeit " + getTimeString(txTime));
			startTransmission(macHeader, frame, configuration.getSignalStrengthFraq());
		} 

		public void sendFinished() {   
		    linkLayerObserver.notifyBroadcastProcessed(frame.getMessage());
			if (callbackHandler != null)
				callbackHandler.notifyBroadcastProcessed(frame.getMessage());
			setFinished();
		} 

		public boolean isUnicast() {        
			return false;
		} 
	}

	// Nachricht wird verworfen
	private void discardMessage(MessageEntry msdu) {        
		mib.incFailedCount();
		if (debug)
			operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " komplette Übertragung der Nachricht fehlgeschlagen und Nachricht verworfen.");		
		msdu.setFinished();
		upperLayer.sendFailed(msdu.getMessage());
    } 

	// Nachricht wird erfolgreich abgeschlossen
    private void finishMessage(MessageEntry msdu) {        
		mib.incTransmittedFrameCount();
		if (debug)
			operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " komplette Übertragung der Nachricht erfolgreich.");		
		resetCW();
		backoffTimeout.set(cw);
		msdu.setFinished();
		upperLayer.sendFinished(msdu.getMessage());		
    } 

    // Handhabt die im Frame mitgelieferte Dauer für den NAV
    private void handleDuration(MacHeader macHeader) {        
		if (!operatingSystem.getDeviceID().equals(globalKnowledge.getDeviceID(macHeader.getReceiver()))) {
			if ((currentEntry != null) && (currentEntry.isWaitingForACK() || currentEntry.isWaitingForCTS() || currentEntry.isWaitingForFragment())) {
				// Device erwartet Ack, CTS oder nächstes Fragment, aber es wird Duration empfangen.
				// Deshalb ist die Übertragung des vorigen Daten-Frames fehlerhaft bzw. das warten auf weitere Fragmente wird eingestellt.
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Duration empfangen, obwohl Frame erwartet wurde.");
				currentEntry.sendFailed();
			}
			
			double duration = macHeader.getDuration();
			if (duration > 0) {
				if (navTimeout.isBusy()) {
					if (duration > navTimeout.getRemainingTime()) {
						navTimeout.reset(duration);
					}
				}
				else {
					navTimeout.set(duration);
					navTimeout.start();
				}
				if (macHeader.getFrameType() == MacHeader.RTS) {
					// nachdem Duration von RTS Frame empfangen wurde, wird ein Timer gestartet, damit nicht unnötig gewartet wird,
					// falls keine weitere Kommunikation stattfindet. -> 9.2.5.4
					navResetTimeout.set();
					navResetTimeout.start();
				}
			}
			else {
				if (physicalState == PhysicalLayerInterface.CCA)
					throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " zu Früh auf CCA gewechselt."); 		
			}
		}
		else
			throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " der Receiver soll keine Duration erhalten."); 		
    } 

    public void handlePromiscDataFrame(MacHeader macHeader, LinkLayerMessage message){
        mib.incReceivedFragmentCount();
        if (debug) {
            if (!macHeader.getMoreFragments() && (macHeader.getFragmentNumber() == 0))
                    operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Promisc Daten Frame mit kompletter Nachricht von Device " + macHeader.getSender() + " empfangen");
            else
                    operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Promisc Daten Frame mit Fragment " + macHeader.getFragmentNumber() + " von Device " + macHeader.getSender() + " empfangen");
        }
         
        
//      es wird überprüft, wie mit der Nachricht weiter verfahren wird
        receiveCacheMap.check(macHeader, message);
        
    }
    
    
    // Handhabt ein empfangenes Daten-Frame
    public void handleDataFrame(MacHeader macHeader, LinkLayerMessage message) {        
		if (macHeader.isUnicastMessage()) {
			mib.incReceivedFragmentCount();
			if (debug) {
				if (!macHeader.getMoreFragments() && (macHeader.getFragmentNumber() == 0))
						operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Daten Frame mit kompletter Nachricht von Device " + macHeader.getSender() + " empfangen");
				else
						operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Daten Frame mit Fragment " + macHeader.getFragmentNumber() + " von Device " + macHeader.getSender() + " empfangen");
			}
				
			if ((currentEntry != null) && currentEntry.isWaitingForFragment() && (currentEntry.getReceiver().equals(macHeader.getSender()))) {
				// richtiges Data Frame empfangen, also wird vorige Übertragung weiter fortgesetzt
				currentEntry.sendSuccessful();
			}
			if ((currentEntry != null) && (currentEntry.isWaitingForACK() || currentEntry.isWaitingForCTS())) {
				// Device erwartet Ack bzw. CTS, aber es wird ein Daten Frame empfangen.
				// Deshalb ist die Übertragung des vorigen Daten Frames fehlerhaft.
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Daten Frame empfangen, obwohl ACK bzw. CTS erwartet wurde.");
				currentEntry.sendFailed();
			}
			
			// Ack Frame wird immer nach SIFS-Zeit gesendet, 
			// egal ob Medium Busy oder Idle -> 9.2.8		
			Address receiver = macHeader.getSender();
			// Duration für ACK Frame wird wie in 7.2.1.3 berechnet
			double duration;
			if (macHeader.getMoreFragments())
				duration = macHeader.getDuration() - transmitTime(mib.getDataRate(), ACKFrame.size) - mib.getSIFS();
			else
				duration = 0;
			currentEntry = new ControlMessageEntry(receiver, new ACKFrame(), duration);
			currentEntry.setBusy(true);
			messageQueue.add(0, currentEntry);
			sifsTimeout.set();
			sifsTimeout.start();
			
			// es wird überprüft, wie mit der Nachricht weiter verfahren wird
			receiveCacheMap.check(macHeader, message);
		}
		else {
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Broadcast Frame von Device " + macHeader.getSender() + " empfangen.");
			
			if ((currentEntry != null) && (currentEntry.isWaitingForACK() || currentEntry.isWaitingForCTS() || currentEntry.isWaitingForFragment())) {
				// Device erwartet Ack, CTS oder nächstes Fragment, aber es wird ein Broadcast Frame empfangen.
				// Deshalb ist die Übertragung des vorigen Daten-Frames fehlerhaft bzw. das warten auf weitere Fragmente wird eingestellt.
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Broadcast Frame empfangen, obwohl anderes Frame erwartet wurde.");
				currentEntry.sendFailed();
			}
			
			// Die Nachricht wird an die höhere Schicht weitergeleitet
			//LinkLayerInfo info = new LinkLayerInfoImplementation(macHeader.getSender(), macHeader.getReceiver(), false);
            macHeader=(MacHeader) macHeader.copy();
            macHeader.setReceiver(macAddress);
			upperLayer.receive(macHeader, message);	
            
	        operatingSystem.sendSignal(new MessageReceiveSignal(macHeader,message));
	        			
			if (physicalState == PhysicalLayerInterface.CCA)
				throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " zu Früh auf CCA gewechselt."); 				
		}
    } 

    // Handhabt ein empfangenes RTS-Frame
    public void handleRTSFrame(MacHeader macHeader) {        
		if (debug)
			operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat RTS Frame von Device " + macHeader.getSender() + " empfangen");
		if ((currentEntry != null) && (currentEntry.isWaitingForACK() || currentEntry.isWaitingForCTS() || currentEntry.isWaitingForFragment())) {
			// Device erwartet Ack, CTS oder nächstes Fragment, aber es wird ein RTS Frame empfangen.
			// Deshalb ist die Übertragung des vorigen Daten-Frames fehlerhaft bzw. das warten auf weitere Fragmente wird eingestellt.
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat RTS Frame empfangen, obwohl anderes Frame erwartet wurde.");
			currentEntry.sendFailed();
		}
		
		if (!navTimeout.isBusy()) {
			// CTS Frame wird nur gesendet, falls NAV Timer das Medium als Idle kennzeichnet -> 9.2.5.7
			Address receiver = macHeader.getSender();
			// Duration für CTS Frame wird wie in 7.2.1.2 berechnet
			double duration = macHeader.getDuration() - transmitTime(mib.getDataRate(), ACKFrame.size) - mib.getSIFS(); 
			currentEntry = new ControlMessageEntry(receiver, new CTSFrame(), duration);
			currentEntry.setBusy(true);
			messageQueue.add(0, currentEntry);
			sifsTimeout.set();
			sifsTimeout.start();			
		}
		else {
			// da der NAV Timer aktiv ist, wird das RTS Frame ignoriert
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " ignoriert das RTS-Frame empfangen von Device " + macHeader.getSender() + ", weil der NAV Timer aktiv ist.");			
		}
    } 

    // Handhabt ein empfangenes ACK-Frame
    public void handleACKFrame(MacHeader macHeader) {        
		if ((currentEntry != null) && currentEntry.isWaitingForACK() && (currentEntry.getReceiver().equals(macHeader.getSender()))) {
			// richtiges ACK empfangen, also Übertragung des vorigen Daten-Frames erfolgreich
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat ACK von Device " + macHeader.getSender() + " empfangen");
			currentEntry.sendSuccessful();
		}
    } 

    // Handhabt ein empfangenes CTS-Frame
    public void handleCTSFrame(MacHeader macHeader) {        
		
		if ((currentEntry != null) && currentEntry.isWaitingForCTS() && (currentEntry.getReceiver().equals(macHeader.getSender()))) {
			// richtiges CTS empfangen, also Übertragung des vorigen RTS-Frames erfolgreich
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat CTS von Device " + macHeader.getSender() + " empfangen");
			currentEntry.sendSuccessful();
		}
    } 

    // Handhabt ein empfangenes Beacon-Frame
    public void handleBeaconFrame(MacHeader macHeader) {        
		if (beaconTimeout.beaconing()) {
			beaconTimeout.stopBeaconing();
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " die Beacon Prozedur wird gestoppt, da ein Frame empfangen wird.");
		}
		if (debug)
			operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Beacon Frame von Device " + macHeader.getSender() + " empfangen");
		if ((currentEntry != null) && (currentEntry.isWaitingForACK() || currentEntry.isWaitingForCTS() || currentEntry.isWaitingForFragment())) {
			// Device erwartet Ack, CTS oder nächstes Fragment, aber es wird ein Beacon Frame empfangen.
			// Deshalb ist die Übertragung des vorigen Daten-Frames fehlerhaft bzw. das warten auf weitere Fragmente wird eingestellt.
			if (debug)
				operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " hat Beacon Frame empfangen, obwohl anderes Frame erwartet wurde.");
			currentEntry.sendFailed();
		}
		connectedDeviceMap.beaconReceived(globalKnowledge.getDeviceID(macHeader.getSender()));
		upperLayer.connectedDevices(connectedDeviceMap.getConnectedSet());
		if (physicalState == PhysicalLayerInterface.CCA)
			throw new IllegalStateException("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " zu Früh auf CCA gewechselt."); 		
    } 

    // Eine Liste der mit diesem Device verbundenen Devices
    private class ConnectedDeviceMap {
        // Wurde während den letzten 10 Beacon-Intervallen von Station S1 mindestens ein Beacon-Frame 
        // von einer Station S2 empfangen, dann ist S1 mit S2 verbunden.

    	private HashMap connectedDeviceMap;

    	public ConnectedDeviceMap() {        
			connectedDeviceMap = new HashMap();
    	} 

    	public void beaconReceived(DeviceID deviceID) {        
			connectedDeviceMap.put(deviceID, new Integer(0));
    	} 

    	public void beaconMissed() {        
			DeviceIDIterator iterator = globalKnowledge.getNodes().iterator();
			while (iterator.hasNext()) {
				DeviceID deviceID = iterator.next();
				if (connectedDeviceMap.containsKey(deviceID)) {
					int beaconMissedCount = ((Integer) (connectedDeviceMap.get(deviceID))).intValue();
					beaconMissedCount++;
					if (beaconMissedCount > 10) {
						connectedDeviceMap.remove(deviceID);
					}
					else {
						connectedDeviceMap.put(deviceID, new Integer(beaconMissedCount));						
					}
				}
			}
    	} 

    	public DeviceIDSet getConnectedSet() {        		
			return new DeviceIDSet(connectedDeviceMap.keySet());
    	} 
    }

    private static class ReceiveCacheKey {
    	
    	private Address sender;
    	private int sequenceNumber;

    	public ReceiveCacheKey(Address sender, int sequenceNumber) {        
			this.sender = sender;
			this.sequenceNumber = sequenceNumber;
    	} 

    	public Address getSender() {        
			return sender;
    	} 

    	public int getSequenceNumber() {        
			return sequenceNumber;
    	} 
    }

    // Cache der bereits empfangenen Frames
    private class ReceiveCacheMap {

    	private HashMap receiveCacheMap;

    	public ReceiveCacheMap() {        
			receiveCacheMap = new HashMap();
    	} 
    	
		// Hier wird überprüft, ob das Fragment neu ist, bereits empfangen wurde,
		// die Nachricht fertig übertragen ist und weitergeleitet werden kann,
		// oder der MaxReceiveLifetime Timer abgelaufen ist und das Fragment ignoriert wird
    	public void check(MacHeader macHeader, LinkLayerMessage message) {        
			ReceiveCacheKey key = getKey(macHeader.getSender(), macHeader.getSequenceNumber());
			if (macHeader.getRetry() && (key != null) && (getFragmentNumber(key) == macHeader.getFragmentNumber())) {
				// empfangenes Fragment wird verworfen, da es schon vorher korrekt empfangen wurde -> 9.2.9
				mib.incFrameDuplicateCount();
				if (debug)
					operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Fragment ignoriert, da es schon vorher korrekt empfangen wurde.");
			}
			else {
				// Fragment wird erstmalig empfangen
				if (key != null) {
					// für dieses (Address, Sequenznummer) Paar ist schon ein Eintrag im Cache vorhanden,
					// also wird das Fragment weiterverarbeitet
					setReceiveCache(key, macHeader.getFragmentNumber());
					if (!macHeader.getMoreFragments()) {
						// Die Nachricht wurde komplett empfangen und wird an höhere Schicht weitergeleitet
						//LinkLayerInfo info = new LinkLayerInfoImplementation(macHeader.getSender(), macHeader.getReceiver(), true);
						upperLayer.receive(macHeader, message);	
                        operatingSystem.sendSignal(new MessageReceiveSignal(macHeader,message));
					}				
				}
				else {
					if (macHeader.getFragmentNumber() == 0) {
						// Es können wenigstens 3 Nachrichten gleichzeitig empfangen werden (-> 9.5). Hier beliebig viele
						// Es wurde eine Nachricht empfangen bzw. der Empfang einer neuen Nachricht hat begonnen
						// und diese wird im Receive Cache gespeichert (zur Erkennung von evtl. Duplikaten)
						// -> MaxReceiveLifetimeTimeout wird gestartet. Am Ende dieses Timeouts wird die Nachricht aus dem Receive Cache geloescht.
						key = new ReceiveCacheKey(macHeader.getSender(), macHeader.getSequenceNumber());
						operatingSystem.setTimeout(new MaxReceiveLifetimeTimeout(key));
						setReceiveCache(key, macHeader.getFragmentNumber());
                        if (!macHeader.getMoreFragments()) {
                            // Die Nachricht wurde komplett empfangen und wird an höhere Schicht weitergeleitet
                            //LinkLayerInfo info = new LinkLayerInfoImplementation(macHeader.getSender(), macHeader.getReceiver(), true);
                            upperLayer.receive(macHeader, message);
                            operatingSystem.sendSignal(new MessageReceiveSignal(macHeader,message));
                        }   
					}
					else {
						// Falls der Cache voll ist oder ein mittleres Fragment empfangen wird, 
						// dessen Nachricht nicht im Cache gespeichert ist, dann wird das Fragment ignoriert
						if (debug)
							operatingSystem.write("Zeit " + getTimeString() + ": Device " + operatingSystem.getDeviceID() + " Fragment ignoriert, da MaxReceiveLifetime Timer für diese Nachricht abgelaufenist.");
					}
				}
			}			
    	} 
    	
	    // setzen des (Address, sequence-number, fragment-number) Tupels -> 9.2.9
	    // und starten der MaxReceiveLivetime -> 9.5
	    private void setReceiveCache(ReceiveCacheKey key, int fragmentNumber) {        
			// Pro (Address, sequence-number) Paar nur ein Eintrag
			Integer fragmentNumberEntry = new Integer(fragmentNumber);
			receiveCacheMap.put(key, fragmentNumberEntry);
	    } 

	    private ReceiveCacheKey getKey(Address sender, int sequenceNumber) {        
			Iterator iterator = receiveCacheMap.keySet().iterator();
			while (iterator.hasNext()) {
				ReceiveCacheKey key = (ReceiveCacheKey) iterator.next();
				if (key.getSender().equals(sender) && (key.getSequenceNumber() == sequenceNumber)) {
					return key;
				}
			}
			return null;
	    } 

	    private int getFragmentNumber(ReceiveCacheKey key) {        
			return ((Integer) receiveCacheMap.get(key)).intValue();		
	    } 

	    public void remove(ReceiveCacheKey key) {        
			receiveCacheMap.remove(key);
	    } 
    }

	public Address getNetworkAddress() {            
        return macAddress;
    } 

    public LinkLayerProperties getLinkLayerProperties() {        
        //TODO:
        return new MibLinkLayerProperties(mib, macAddress, -1,0);
    } 
    
    
    public void setLinkLayerProperties(LinkLayerProperties props){
        mib=((MibLinkLayerProperties)props).getMib();
    }

    public void setPromiscuous(boolean promiscuous) {        
        //TODO
        mib.setPromiscous(true);
        
    } 
    

    /**
     * 
     * TODO Comment method
     * @param serviceUnit
     * @param mib
     * @param messageProcessTime
     * @return
     */
    public  static ServiceID createInstance(ServiceUnit serviceUnit,Mib mib, ContinuousDistribution messageProcessTime) {
        
        ServiceID phyID;
        if (!serviceUnit.hasService(MacToPhysicalLayerInterface.class)){
             phyID=PhysicalLayer.createInstance(serviceUnit,mib, SERVICE_ID);
        }else{
            phyID=serviceUnit.getService(MacToPhysicalLayerInterface.class);
        }
        ServiceID upperLayer;
        if (serviceUnit.hasService(MacToUpperLayerInterface.class)){
            upperLayer=serviceUnit.getService(MacToUpperLayerInterface.class);
        }else{
            upperLayer=serviceUnit.addService(new EmptyUpperLayer());
        }
        
        return serviceUnit.addService(new MacLayer80211(
                serviceUnit.getDeviceID(),
                phyID,mib,messageProcessTime, upperLayer));
    }
    public  static ServiceID createInstance(ServiceUnit serviceUnit,Mib mib){
        ContinuousDistribution messageProcessingTime =                                                                      
            serviceUnit.getDistributionCreator().getContinuousUniformDistribution(0.01, 0.000001);
        return createInstance(serviceUnit,mib,messageProcessingTime);
    }
    
    /**
     * Global Factory for this network.
     * A global Medium and device lokal PHY and MAC layers are created using unique MIB configurations. 
     * @param serviceUnit   The unit to add the service
     * @param mib           The Management Information base to configure the PHY and the MAC.
     *  
     */
    public static void createInstanceGlobal(ServiceUnit serviceUnit, final Mib mib){
        createInstanceGlobal(serviceUnit,mib,false,false,false,false);
    }
   
    /**
     * Global Factory for this network.
     * A global Medium and device lokal PHY and MAC layers are created using unique MIB configurations. 
     * @param serviceUnit   The unit to add the service
     * @param mib           The Management Information base to configure the PHY and the MAC.
     * @param visualizeMessages
     * @param visualizeSendingRadius
     * @param visualizeCommunicationLinks
     * @param visualizeState
     */
    public static void createInstanceGlobal(ServiceUnit serviceUnit, final Mib mib,final boolean visualizeMessages, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks, final boolean visualizeState){
        ContinuousDistribution messageProcessingTime =                                                                      
            serviceUnit.getDistributionCreator().getContinuousUniformDistribution(0.01, 0.000001);
        createInstanceGlobal(serviceUnit,mib,visualizeMessages,visualizeSendingRadius,visualizeCommunicationLinks,visualizeState,null,messageProcessingTime);
    }
    
    /**
     * Global Factory for this network.
     * A global Medium and device lokal PHY and MAC layers are created using unique MIB configurations. 
     * @param serviceUnit   The unit to add the service
     * @param mib           The Management Information base to configure the PHY and the MAC.
     * @param visualizeMessages
     * @param visualizeSendingRadius
     * @param visualizeCommunicationLinks
     * @param visualizeState
     * @param randomNoise   The randomized background noise in mW. 0.1mW is normal noise. 
     */
    public static void createInstanceGlobal(ServiceUnit serviceUnit, final Mib mib,
            final boolean visualizeMessages, boolean visualizeSendingRadius, boolean visualizeCommunicationLinks, final boolean visualizeState, 
            ContinuousDistribution randomNoise, final ContinuousDistribution messageProcessTime){
        boolean visualizeOld=serviceUnit.getVisualizeAddedServices();
        serviceUnit.setVisualizeAddedServices(visualizeCommunicationLinks||visualizeMessages||visualizeSendingRadius);
        if (!serviceUnit.hasService(MediumInterface.class)){
            serviceUnit.addService(new MediumMobileDevicesSNR(visualizeMessages,visualizeSendingRadius,visualizeCommunicationLinks, randomNoise));
        }
       // final boolean visualize=serviceUnit.getVisualizeAddedServices(); 
        serviceUnit.addServiceFactory(new ServiceFactory() {
        
            public void initServices(ServiceUnit serviceUnit) {
                boolean visualizeOld=serviceUnit.getVisualizeAddedServices();
                serviceUnit.setVisualizeAddedServices(visualizeState);
                createInstance(serviceUnit,mib.copy(),messageProcessTime);
                serviceUnit.setVisualizeAddedServices(visualizeOld);
        
            }
        
        });
        serviceUnit.setVisualizeAddedServices(visualizeOld);
            
        
    }
 }
