/*****************************************************************************
 * 
 * DeviceManager.java
 * 
 * $Id: DeviceManager.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.device;

import java.rmi.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.hybrid.basetypes.*;
import de.uni_trier.jane.hybrid.local.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.DeviceServiceFactory;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.basetypes.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.kernel.eventset.*;
import de.uni_trier.jane.simulation.operating_system.*;
import de.uni_trier.jane.simulation.operating_system.manager.*;
import de.uni_trier.jane.simulationl.visualization.console.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * The device manager controls all mobile devices in the simulation. It dispatches 
 * arriving messages, handles events and calls the corresponding methods. 
 */
public class DeviceManager implements 	DynamicEventListener,Visualizable,ShutdownListener{

	/**
     * @author goergen
     *
     * TODO comment class
     */
    private static final class DeviceMangerShutdownListener implements
            DeviceShutdownListener {

        private int devices;
        private TerminalCondition terminalCondition;

        /**
         * Constructor for class <code>DeviceMangerShutdownListener</code>
         * @param length
         */
        public DeviceMangerShutdownListener(int devices, TerminalCondition terminalCondition) {
            this.devices=devices;
            this.terminalCondition=terminalCondition;
        }



        public void notifyBeginShutdown() {
            // TODO Auto-generated method stub
            
        }

        public void notifyEndShutdown() {
            devices--;
            if (devices==0){
                terminalCondition.setTrue();
            }
            
        }

        public void notifyStartBoot() {
            // TODO Auto-generated method stub
            
        }

    }


    private final static String VERSION = "$Id: DeviceManager.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";


	
	private AddressShapeMap addressShapeMap;
	
	private AddressDeviceMapping addressDeviceMapping;
	

	private ConsoleTextBuffer consoleTextBuffer;
	private AddressMobileDeviceMap addressMobileDeviceMap;

	private GlobalKnowledgeImplementation globalKnowledge;
	private DeviceKnowledge deviceKnowledge;

	private double minimumTransmissionRadius;
	private double maximumTransmissionRadius;
	private TerminalCondition terminalCondition;

    private SimulationServiceFactory serviceFactory;
    
    //TODO da fliegt SimulationDeviceID weg !!!!!!!
    // Address must be equal or greater then zero
    private static final DeviceID globalDeviceID=new SimulationDeviceID(-1);

    private MobileDevice globalMobilDevice;
	
    private DeviceServiceFactory deviceServiceFactory;

	

	protected DefaultServiceUnit globalServiceUnit;
	
	

    private ShutdownAnnouncer shutdownAnnouncer;

    private DefaultSimulationParameters initializer;
    
	
	/**
	 * Constructor for the class <code>DeviceManager</code>.
	 * @param eventSet
	 * @param simulation
	 * @param distributionCreator
	 * @param serviceFactory
	 * @param consoleTextBuffer
	 * @param addressShapeMap
	 * @param terminalCondition
	 * @param minimumTransmissionRadius
	 * @param maximumTransmissionRadius
	 * @param shutdownAnnouncer
	 * @param isHybrid
	 */
//	public DeviceManager(
//			EventSet eventSet,
//            ApplicationSimulation simulation,
//            DistributionCreator distributionCreator,
//            final SimulationServiceFactory serviceFactory, 
//            ConsoleTextBuffer consoleTextBuffer,
//            AddressShapeMap addressShapeMap,
//            TerminalCondition terminalCondition,
//            double minimumTransmissionRadius, 
//            double maximumTransmissionRadius, 
//            ShutdownAnnouncer shutdownAnnouncer, 
//            boolean isHybrid
//            ) {
//		this(
//				eventSet, 
//				simulation, 
//				distributionCreator, 
//				serviceFactory, 
//				consoleTextBuffer, 
//				addressShapeMap, 
//				terminalCondition, 
//				minimumTransmissionRadius,
//				maximumTransmissionRadius,
//				shutdownAnnouncer,
//				isHybrid,
//				false
//				);
//	}
	
	/**
	 * Constructor for class <code>DeviceManager</code>
	 * @param initializer
	 * @param simulation
	 * @param distributionCreator
	 * @param serviceFactory
	 * @param consoleTextBuffer
	 * @param addressShapeMap
	 * @param terminalCondition
	 * @param minimumTransmissionRadius
	 * @param maximumTransmissionRadius
	 * @param shutdownAnnouncer
	 * @param isHybrid
	 */
    public DeviceManager(final DefaultSimulationParameters initializer,
            ApplicationSimulation simulation,

            final SimulationServiceFactory serviceFactory, 
            ConsoleTextBuffer consoleTextBuffer,
            AddressShapeMap addressShapeMap,
            TerminalCondition terminalCondition,
            double minimumTransmissionRadius, 
            double maximumTransmissionRadius,
            ShutdownAnnouncer shutdownAnnouncer) {
        this.initializer=initializer;
        this.shutdownAnnouncer=shutdownAnnouncer;
    	shutdownAnnouncer.addShutdownListener(this);
    	addressDeviceMapping = new AddressDeviceMapping();

		

		this.serviceFactory=serviceFactory;
		this.consoleTextBuffer = consoleTextBuffer;
		
		this.addressShapeMap = addressShapeMap;
		this.terminalCondition=terminalCondition;
		addressMobileDeviceMap = new AddressMobileDeviceMap();
		

	
		
		globalKnowledge = new GlobalKnowledgeImplementation(this,maximumTransmissionRadius,minimumTransmissionRadius, addressDeviceMapping);
		deviceKnowledge=new DeviceKnowledge(this);
		this.minimumTransmissionRadius = minimumTransmissionRadius;
		this.maximumTransmissionRadius = maximumTransmissionRadius;
	//	networkGraph=new NetworkGraphImplementation();
		if (initializer.isHybrid()){
    		SynchronizedEventSet syncEventSet=(SynchronizedEventSet)initializer.getEventSet();
    		SyncObject object=syncEventSet.getSynchronizeObject();
    		try {
				RemoteJANEServerImplementation server=new RemoteJANEServerImplementation(this,object,shutdownAnnouncer);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
		}
		ShutdownManager shutdownManager=new ShutdownManager(globalDeviceID);
		
		SimulationGlobalDeviceServiceManager globalOperatingSystem = 
			new SimulationGlobalDeviceServiceManager(
					globalDeviceID,
					globalKnowledge,
					initializer,
					new ExactClock(new SimulationClock(initializer.getEventSet())),
					new ActionScheduler(),
					shutdownAnnouncer,
					consoleTextBuffer,
					deviceKnowledge, 
					addressDeviceMapping,
					shutdownManager);
		//deviceServiceFactory = new DeviceServiceFactory(serviceFactory);
		globalOperatingSystem.setServiceFactory(new DeviceServiceFactory() {

			public ServiceCollection getServiceCollection() {
				ServiceCollection collection = new ServiceCollection();
				globalServiceUnit = new DefaultServiceUnit(globalDeviceID, collection, initializer.getDistributionCreator());
	    	    serviceFactory.initGlobalServices(globalServiceUnit);
				return collection;
			}

            public ServiceID checkServiceID(Service service) {
                return globalServiceUnit.checkServiceID(service);
            }
		});
		
		//addressMobileDeviceMap.add(new MobileDevice(null,null,null,globalOperatingSystem,eventSet));
		globalMobilDevice=new MobileDevice(globalOperatingSystem,shutdownManager);
		//TODO: use a start method
		globalOperatingSystem.notifyStartBoot();
		
		
	}

	
    /**
	 * Get the mobile device corresponding to the given address.
	 * @param deviceID the device address
	 * @return the mobile device
	 */
	public MobileDevice getMobileDevice(DeviceID deviceID) {
	    if (deviceID.equals(globalDeviceID)){
	        return globalMobilDevice;
	    }
		return addressMobileDeviceMap.get(deviceID);
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.DynamicEventListener#handleEnter(Address, TrajectoryMapping, boolean, DoubleMapping)
	 */
	public void handleEnter(DeviceID deviceID, TrajectoryMapping trajectoryMapping, boolean suspended, DoubleMapping sendingRadius) {
	
		
		//DeviceID deviceID=(new SimulationDeviceID(address));

		addressDeviceMapping.enterDevice(deviceID);
		
		MobileDevice mobileDevice = new MobileDevice(deviceID, globalDeviceID,globalKnowledge,
				deviceKnowledge, initializer,consoleTextBuffer, 
				shutdownAnnouncer,serviceFactory, globalServiceUnit, addressDeviceMapping); 
		        
		
		addressMobileDeviceMap.add(mobileDevice);
			
		
		mobileDevice.notifyEnter(trajectoryMapping,suspended, sendingRadius);
		globalKnowledge.notifyEnter(deviceID);
		
		// used to check consistency with minimum and maximum transmission radius
		if(sendingRadius.getInfimum() < minimumTransmissionRadius) {
			throw new DeviceManagerException("Device sending radius < minimum transmission radius is not allowed.");
		}
		if(sendingRadius.getSupremum() > maximumTransmissionRadius) {
			throw new DeviceManagerException("Device sending radius > maximum transmission radius is not allowed.");
		}
        addressMobileDeviceMap.get(deviceID).notifyChangeTrack(trajectoryMapping, suspended);
        globalKnowledge.changeTrack(deviceID, trajectoryMapping,suspended);
    
		
	}

	/**
	 * @param listener 
	 * @see de.uni_trier.ubi.appsim.kernel.DynamicEventListener#handleExit(Address)
	 */
	private void handleExit(DeviceID deviceID, DeviceShutdownListener listener) {

		
		MobileDevice mobileDevice = addressMobileDeviceMap.get(deviceID);
        mobileDevice.getShutdownManager().addShutdownListener(listener);
		mobileDevice.notifyExit();
		

		
		//network.notifyExit(address);
		

	}
    
    public void handleExit(final DeviceID address) {
        handleExit(address, new DeviceShutdownListener() {
        
            public void notifyStartBoot() {
                // TODO Auto-generated method stub
        
            }
        
            public void notifyEndShutdown() {
                try{
                    
                    addressDeviceMapping.exitDevice(address);
                }catch(Exception e){
                    e.printStackTrace();
                }
                //TODO: shutdown if no devices are left?
                addressMobileDeviceMap.remove(address);
                addressShapeMap.remove(address);
                globalKnowledge.notifyExit(address);
        
            }
        
            public void notifyBeginShutdown() {
                // TODO Auto-generated method stub
        
            }
        
        });
        
    }
	
	

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.DynamicEventListener#handleAttach(Address, Address, DoubleMappingInterval)
	 */
	public void handleAttach(DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability) {
	    //SimulationDeviceID sender=new SimulationDeviceID(senderAddress);
	    //SimulationDeviceID receiver=new SimulationDeviceID(receiverAddress);
		//if (network.vetoableNotifyAttach(sender, receiver, linkReliability)) {
	  //  networkGraph.addEdge(sender,receiver);
		addressMobileDeviceMap.get(receiver).notifyAttachUnidirectional(sender);
			
	//	if(network.isConnected(receiver, sender)) {
//			addressMobileDeviceMap.get(receiver).notifyAttachBidirectional(sender);
//			addressMobileDeviceMap.get(sender).notifyAttachBidirectional(receiver);
		//}
			globalKnowledge.handleAttach(sender,receiver);
		
		
		// used to check consistency with minimum and maximum transmission radius
		//TODO: transmission range not in DeviceManager?
		//else {
		//	Position pos1 = globalKnowledge.getTrajectory(sender).getPosition();
		//	Position pos2 = globalKnowledge.getTrajectory(receiver).getPosition();
		//	if(pos1.distance(pos2) <= minimumTransmissionRadius) {
		//		throw new DeviceManagerException("The devices have to be attached due to the minimum transmission radius.");
		//	}
		//}
		
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.DynamicEventListener#handleDetach(Address, Address)
	 */
	public void handleDetach(DeviceID sender, DeviceID receiver) {
	    //SimulationDeviceID sender=new SimulationDeviceID(senderAddress);
	    //SimulationDeviceID receiver=new SimulationDeviceID(receiverAddress);
	    
		MobileDevice device=addressMobileDeviceMap.get(receiver);
        if (device!=null){
            device.notifyDetachUnidirectional(sender);
        }
		globalKnowledge.handleDetach(sender,receiver);
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.DynamicEventListener#handleChangeTrack(Address, TrajectoryMapping, boolean)
	 */
	public void handleChangeTrack(DeviceID deviceID, TrajectoryMapping trajectoryMapping, boolean suspended) {
		//SimulationDeviceID deviceID=new SimulationDeviceID(address);
		if (addressMobileDeviceMap.hasDevice(deviceID)){
			addressMobileDeviceMap.get(deviceID).notifyChangeTrack(trajectoryMapping, suspended);
			globalKnowledge.changeTrack(deviceID, trajectoryMapping,suspended);
		}
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.DynamicEventListener#handleChangeLinkReliability(Address, Address, DoubleMappingInterval)
	 */
	public void handleChangeLinkReliability(DeviceID sender, DeviceID receiver, DoubleMappingInterval linkReliability) {
		//network.notifyChangeLinkReliability(sender, receiver, linkReliability);
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.DynamicEventListener#handleChangeSendingRadius(Address, DoubleMapping)
	 */
	public void handleChangeSendingRadius(DeviceID deviceID, DoubleMapping sendingRadius) {
	    //SimulationDeviceID deviceID=new SimulationDeviceID(address);
		addressMobileDeviceMap.get(deviceID).notifyChangeSendingRadius(sendingRadius);
	}

	

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.device.NetworkVisualizationInfoAnnouncer#getNetworkVizualisationInfo(Address)
	 */


	/**
	 * @see de.uni_trier.jane.visualization.Visualizable#getShape()
	 */
	public Shape getShape() {
		ShapeCollection refactor = new ShapeCollection();
		MobileDeviceIterator it = addressMobileDeviceMap.getMobileDeviceIterator();
		//refactor.addShape(globalKnowledge.getShape(),Position.NULL_POSITION);
		refactor.addShape(globalMobilDevice.getShape(),Position.NULL_POSITION);
		while(it.hasNext()) {
			MobileDevice device = it.next();
			//Position position = device.getTrajectory().getPosition();
			//TODO: make shapes relative! <- fixed. see CollisionFreeNetwork.getShape
			refactor.addShape(device.getShape(), Position.NULL_POSITION);
			
		}
		return refactor;
	}

	private static class AddressMobileDeviceMap {
		protected Map addressDeviceMap;
		/**
		 * 
		 *
		 */
		public AddressMobileDeviceMap() {
			addressDeviceMap = new HashMap();
		}
		
		/**
         * TODO Comment method
         * @return
         */
        public boolean isEmpty() {
            return addressDeviceMap.isEmpty();
        }

        /**
		 * @param deviceID
		 * @return
		 */
		public boolean hasDevice(DeviceID deviceID) {

			return addressDeviceMap.containsKey(deviceID);
		}

		/**
		 * adds a mobile device
		 * @param mobileDevice
		 */
		public void add(MobileDevice mobileDevice) {
			addressDeviceMap.put(mobileDevice.getDeviceID(),mobileDevice);
		}
		/**
		 * Gets the Mobile device for the given address
		 * @param address
		 * @return the mobile device
		 */
		public MobileDevice get(DeviceID address) {
			return (MobileDevice)addressDeviceMap.get(address);
		}
		/**
		 * removes a mobile device for the given address 
		 * @param address
		 */
		public void remove(DeviceID address) {
			addressDeviceMap.remove(address);
		}
		
		/**
		 * returns a MobileDeviceIterator containing all mobile devices
		 * @return the mobile device iterator
		 */
		public MobileDeviceIterator getMobileDeviceIterator() {
			return new MobileDeviceIterator() {
				private Iterator it = addressDeviceMap.values().iterator();
				public boolean hasNext() {
					return it.hasNext();
				}
				public MobileDevice next() {
					return (MobileDevice)it.next();
				}
			};
		}

        /**
         * @return
         */
        public DeviceIDSet getAllDeviceIDs() {

            return new MutableDeviceIDSet(addressDeviceMap.keySet());
        }
	}


	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.ShutdownListener#shutdown()
	 */
	public void shutdown() {
		DeviceID[] addresses=(DeviceID[])addressMobileDeviceMap.addressDeviceMap.keySet().toArray(new DeviceID[addressMobileDeviceMap.addressDeviceMap.size()]);
        DeviceShutdownListener listener=new DeviceMangerShutdownListener(addresses.length+1,terminalCondition);
		for (int i=0;i<addresses.length;i++){
			handleExit(addresses[i], listener);

		}
        globalMobilDevice.notifyExit();
        globalMobilDevice.getShutdownManager().addShutdownListener(listener);
        
        
		
		
	}

    /**
     * @return
     */
    public DeviceIDPositionMap getAddressPositionMap() {
        DeviceIDPositionMapImpl addressPositionMap = new DeviceIDPositionMapImpl();
		MobileDeviceIterator it = addressMobileDeviceMap.getMobileDeviceIterator();
		while(it.hasNext()) {
			MobileDevice device = it.next();
			Position position = device.getTrajectory().getPosition();
			
			DeviceID deviceID = device.getDeviceID();
			addressPositionMap.addAddressPositionPair(deviceID, position);
			Address[] addresses = (Address[])addressDeviceMapping.getAddresses(deviceID);
			for(int i=0; i<addresses.length; i++) {
				addressPositionMap.addAddressPositionPair(addresses[i], position);
			}
			
		}
		return addressPositionMap;
    }

	/**
	 * @return
	 */
	public GlobalKnowledge getGlobalKnowledge() {
		return globalKnowledge;
	}

    /**
     * @return
     */
    public DeviceIDSet getNodes() {
        
        return addressMobileDeviceMap.getAllDeviceIDs();
    }


    /**
     * TODO: comment method 
     * @return
     * 
     */
    public MobileDevice getGlobalDevice() {
        return globalMobilDevice;
        
    }


    /**
     * TODO: comment method 
     * @return
     */
    public DefaultServiceUnit getGlobalServiceUnit() {

       return globalServiceUnit;
    }
}

