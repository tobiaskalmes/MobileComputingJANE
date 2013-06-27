/*****************************************************************************
 * 
 * Medium.java
 * 
 * Id
 *  
 * Copyright (C) 2003 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer.global_shared_network;


import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.simulation.service.*;
//import de.uni_trier.ubi.appsim.kernel.*;
//import de.uni_trier.ubi.appsim.kernel.basetype.*;



/**
 * @author daniel
 *
 */
public class Medium {



  /**
   * Queue for storing medium reserve announces.
   * The device at the first queue position is allowed to send or currently sending.
   * Excact simulation time is needed for total ordering of reserve anouncements. 
   * @author goergen
   * 
   */
	
	private static final  class ReserverQueue{
		private TreeMap prioReserveList;
		private HashMap reservesMap;
		private NetworkStatistic networkStatistic;
		private  DeviceID owner;
		private GlobalTimestampCreator timestamp;
		
        //private GlobalOperatingSystem operatingSystem;
		

		/**
		 * Entry of a reserve queue.
		 * Containg device address of a reserver and the reserve time.
		 * @author daniel
		 *
		 */
		private static class ReserveQueueEntry implements Comparable{
			private long time;
			private DeviceID reserver;

			/**
			 * Constructor of a <code>ReserveQueueEntry</code> 
			 * @param time		creation time
			 * @param reserver2	reserver address
			 */
			public ReserveQueueEntry(long time, DeviceID reserver) {
				this.time = time;
				this.reserver = reserver;
			}

			/**
			 * Returns the reserver address.
			 * @return Returns the reserver.
			 */
			public DeviceID getReserver() {
				return reserver;
			}
				 
			/**
			 * @see java.lang.Object#equals(java.lang.Object)
			 */
			public boolean equals(Object object){
				if (object==this) return true;
				if (object==null) return false;
				if (object.getClass()!=this.getClass())return false;
				ReserveQueueEntry entry=(ReserveQueueEntry)object;
				return (entry.time==time&&entry.reserver.equals(reserver));
			}
			
			/**
			 * @see java.lang.Object#hashCode()
			 */
			public int hashCode() {
				int result = 0;
				int PRIME = 1001;
				
				result = PRIME*result + (int)(reserver.hashCode() >>> 32);
				result = PRIME*result + (int)(reserver.hashCode() & 0xFFFFFFFF);
				result = PRIME*result + (int)(time >>> 32);
				result = PRIME*result + (int)(time & 0xFFFFFFFF);		
				
				return result;
			}
	
			/**
			 * @see java.lang.Comparable#compareTo(java.lang.Object)
			 */
			public int compareTo(Object obj) {
				ReserveQueueEntry entry=(ReserveQueueEntry)obj;
				if (time<entry.time) return -1;
				if (time>entry.time) return 1;
				return reserver.compareTo(entry.reserver);
				
			}
			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			public String toString() {
			
				return reserver.toString();
			}
			
		}

				
		/**
		 * Constructer of class <code>ReserveQueue</code>. </b>
		 * 
		 * @param simulationClock	The simulation clock containing the simulation time.
		 * @param networkStatistic	Network statistics handler class.
		 * @param owner2				The Address of the owners device.
		 */
		public ReserverQueue(GlobalTimestampCreator timestamp, NetworkStatistic networkStatistic, DeviceID owner){
			this.networkStatistic=networkStatistic;
			this.owner=owner;
			this.timestamp=timestamp;
			prioReserveList=new TreeMap();
			reservesMap=new HashMap();
		}
		
		
		/**
		 * Adds a device wanting to use the Medium to the lokal reserve queue.
		 * @param reserver	Address of device annoncing a reserve
		 */
		public void add(DeviceID reserver, Medium medium){
			if (reservesMap.isEmpty()){
				networkStatistic.networkInUse(owner);
			}
			ReserveQueueEntry entry=new ReserveQueueEntry(timestamp.next(),reserver);
			prioReserveList.put(entry,medium);
			reservesMap.put(reserver,entry);
		}
		
		/**
		 * Removes a device from the lokal reserve queue
		 * @param reserver Address of device withdrawing a reserve
		 */
		public void remove(DeviceID reserver){
			if (reservesMap.containsKey(reserver)){
				ReserveQueueEntry entry=(ReserveQueueEntry)reservesMap.remove(reserver);
				prioReserveList.remove(entry);
			}
			if (reservesMap.isEmpty()){
				networkStatistic.networkFree(owner);
			}
		}
		
		/**
		 * Checks if reserver address is first in Queue
		 * @param reserver	Address to check
		 * @return true if address is first in queue
		 */
		public boolean isFirst(DeviceID reserver){
			if (prioReserveList.isEmpty()) return false;
			ReserveQueueEntry entry=(ReserveQueueEntry)prioReserveList.firstKey();
			if (entry!=null){
				return entry.getReserver().equals(reserver);
			}
			return false;
		}
		
		/**
		 * Checks if reserver is in queue
		 * @param reserver
		 * @return true if Address is in queue
		 */
		public boolean contains(DeviceID reserver){
			return reservesMap.containsKey(reserver);
		}
		
		/**
		 * Returns the <code>ReserveQueueEntry</code> containing reserve information for a given device address. 
		 * @param address	the device address for the ReserveQueueEntry
		 * @return the ReserveQueueEntry for the given device address
		 * @throws	<code>IllegalArgumentException</code> if the ReserveQueue does not contain the entry for given address 
		 */
		public ReserveQueueEntry getEntry(DeviceID address) {
			if (!reservesMap.containsKey(address)) throw new IllegalArgumentException("ReserverQueue does not contain this entry");
			return (ReserveQueueEntry)reservesMap.get(address);
		}

		/**
		 * Adds an existing ReserveQueueEntry to the ReserveQueue.
		 * This is normally used when two (sending) devices comes into direct communication range 
		 * @param entry
		 */
		public void add(ReserveQueueEntry entry,Medium medium) {
			reservesMap.put(entry.getReserver(),entry);
			prioReserveList.put(entry,medium);
			
		}


		/**
		 * @return
		 */
		public Medium getFirst() {
			
			return (Medium)(prioReserveList.get(prioReserveList.firstKey()));
		}


		/**
		 * @param address
		 * @return
		 */
		public Medium getEntrysMedium(DeviceID address) {
			
			return (Medium)prioReserveList.get(getEntry(address));
		}


		/**
		 * @return
		 */
		public boolean isEmpty() {
			
			return prioReserveList.isEmpty();
		}
	}
	
	
	/**
	 * @author daniel
	 *
	 * To change the template for this generated type comment go to
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */
//	private class UnicastReserveMap {
//		private HashMap receiverSendersMap;
//		
//		public UnicastReserveMap(){
//			receiverSendersMap=new HashMap();
//		}
//		
//		
//		public void put(DeviceID sender, DeviceID receiver){
//			HashSet senderSet;
//			if (receiverSendersMap.containsKey(receiver)){
//				senderSet=(HashSet)receiverSendersMap.get(receiver);
//			}else {
//				senderSet=new HashSet();
//			}
//			senderSet.add(sender);
//			receiverSendersMap.put(receiver,senderSet);
//		}
//		
//		public void remove(Address sender, Address receiver){
//			if (receiverSendersMap.containsKey(receiver)){
//				HashSet senderSet=(HashSet)receiverSendersMap.get(receiver);
//				senderSet.remove(sender);
//				if (senderSet.isEmpty()){
//					receiverSendersMap.remove(receiver);
//				}
//			}
//		}
//		
//		public boolean containsReceiver(Address receiver){
//			return receiverSendersMap.containsKey(receiver);
//			
//		}
//		public Set removeReceiver(Address receiver){
//			HashSet senderSet;
//			if (receiverSendersMap.containsKey(receiver)){
//				senderSet=(HashSet)receiverSendersMap.remove(receiver);
//			}else {
//				senderSet=new HashSet();
//			}
//			return senderSet;
//			
//		}
//
//
//		/**
//		 * @param object
//		 * @param owner
//		 * @return
//		 */
//		public boolean contains(Address sender, Address receiver) {
//			if (receiverSendersMap.containsKey(receiver)){
//				return ((HashSet)receiverSendersMap.get(receiver)).contains(sender);
//			}
//			return false;
//		}
//
//	}
	/**
	 * @author daniel
	 *
	 * To change the template for this generated type comment go to
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */
	private class ReserveInfo {
		private DeviceID receiver;
		private SendHandler sendHandler;
		

		/**
		 * @param sendHandler
		 */
		public ReserveInfo(SendHandler sendHandler) {
			this.sendHandler = sendHandler;
		}
		/**
		 * @param receiver
		 * @param sendHandler
		 */
		public ReserveInfo(DeviceID receiver, SendHandler sendHandler) {
			this.receiver = receiver;
			this.sendHandler = sendHandler;
		}
		/**
		 * @return Returns the receiver.
		 */
		public DeviceID getReceiver() {
			return receiver;
		}

		/**
		 * @return Returns the sendHandler.
		 */
		public SendHandler getSendHandler() {
			return sendHandler;
		}
		/**
		 * @return
		 */
		public boolean isUnicastInfo() {
			return receiver!=null;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
		
			return receiver.toString();
		}
		
		

	}
	private DeviceID owner;
	

	
	private Map neighbors;
	
	private Vector unicastSenderList;
	//private ReserveInfo reserveInfo;
	//private SendHandler sendHandler;
	private ReserveInfo reserveInfo;

	



	private ReserverQueue reserverQueue;



    private GlobalOperatingSystem operatingSystem;



	



	

	public Medium(DeviceID owner, GlobalOperatingSystem operatingSystem, GlobalTimestampCreator timestamp, NetworkStatistic networkStatistic){
		this.owner=owner;
		
		this.operatingSystem=operatingSystem;
		unicastSenderList=new Vector();
		reserverQueue=new ReserverQueue(timestamp,networkStatistic,owner);
		neighbors=new HashMap();
	}
	



	public void  reserveUnicast(DeviceID receiver, SendHandler sendHandler){
		if (reserveInfo!=null) throw new IllegalStateException("only one reserve at a time");
		reserveInfo=new ReserveInfo(receiver,sendHandler);
		reserverQueue.add(owner,this);

		
		Map unicastReceiverMap=new HashMap(neighbors);
		if (neighbors.containsKey(receiver)){
			Medium medium=((Medium)neighbors.get(receiver));
			unicastReceiverMap.putAll(medium.neighbors);
			unicastReceiverMap.remove(owner);
			medium.unicastSenderList.add(owner);
		}
		Iterator iterator=unicastReceiverMap.values().iterator();
		while (iterator.hasNext()){
			Medium medium=(Medium)iterator.next();
			medium.reserverQueue.add(owner,this);
		}
		operatingSystem.setTimeout(new ServiceTimeout(0) {
			public void handle() {
				checkSendOK();

			}
		});
			
		
	}
	

	
	



	public void reserveBroadcast(SendHandler sendHandler){
		if (reserveInfo!=null) throw new IllegalStateException("only one reserve at a time");
		reserveInfo=new ReserveInfo(sendHandler);
		reserverQueue.add(owner,this);
		Iterator iterator=neighbors.values().iterator();
		while(iterator.hasNext()){
			
			((Medium)iterator.next()).reserverQueue.add(owner,this);
		}
		operatingSystem.setTimeout(new ServiceTimeout(0) {
			public void handle() {
				checkSendOK();

			}
		});
		
	}
	
	
	/**
	 * 
	 */
	private void checkSendOK() {
		if (reserverQueue.isFirst(owner)){
			if (reserveInfo.isUnicastInfo()&&neighbors.containsKey(reserveInfo.getReceiver())){
				
				Medium medium=(Medium)neighbors.get(reserveInfo.getReceiver());
				if (medium.reserverQueue.isFirst(owner)||!medium.neighbors.containsKey(owner)){
					reserveInfo.getSendHandler().handle();
				}
			}else {
				reserveInfo.getSendHandler().handle();
			}
		}
	}




	public void unicastFinished(){
		if (!reserverQueue.contains(owner)||reserveInfo==null) throw new IllegalStateException("Medium was not reserved");
		if (!reserveInfo.isUnicastInfo())throw new IllegalStateException("Medium was not reserved for unicast");
		
		Map unicastReceivingNeighbors=new HashMap(neighbors);
		if (neighbors.containsKey(reserveInfo.getReceiver())){
			Medium medium=(Medium)neighbors.get(reserveInfo.getReceiver());
			unicastReceivingNeighbors.putAll(medium.neighbors);
			unicastReceivingNeighbors.remove(owner);
			medium.unicastSenderList.remove(owner);
		}
		Iterator iterator=unicastReceivingNeighbors.values().iterator();
	
		while(iterator.hasNext()){
			Medium medium=(Medium)iterator.next();
			medium.freeMedium(owner);
		}
		freeMedium(owner);
		
		
		reserveInfo=null;
	}



	/**
	 * 
	 */
	public void broadcastFinished() {
		if (!reserverQueue.contains(owner)||reserveInfo==null) throw new IllegalStateException("Medium was not reserved");
		if (reserveInfo.isUnicastInfo())throw new IllegalStateException("Medium was not reserved for broadcast");
		
		Iterator iterator=neighbors.values().iterator();
		while(iterator.hasNext()){
			Medium medium=(Medium)iterator.next();
			medium.freeMedium(owner);
		}
		
		freeMedium(owner);
		reserveInfo=null;
		
	}
	




	/**
	 * @param owner2
	 */
	private void freeMedium(DeviceID sender) {
		reserverQueue.remove(sender);
		
		if (!unicastSenderList.isEmpty()){
			
			final Medium medium=((Medium)neighbors.get(unicastSenderList.get(0)));
			if (medium!=null){
				operatingSystem.setTimeout(new ServiceTimeout(0) {
					public void handle() {
						medium.checkSendOK();
					}
				});
				
			}

		}
		operatingSystem.setTimeout(new ServiceTimeout(0) {
			public void handle() {
				if (!reserverQueue.isEmpty()){
					Medium medium=reserverQueue.getFirst();
					medium.checkSendOK();
				}

			}
		});
		
		
	}




	/**
	 * 
	 */
	public void notifyExit() {
		if (reserveInfo!=null){
			if (reserveInfo.isUnicastInfo()){
				
				unicastFinished();
			}else {
				broadcastFinished();
			}
		}
		
	}



	/**
	 * @param receiver
	 * @param medium
	 */
	public void addNeighbor(DeviceID receiver, Medium medium) {
		
		if (reserveInfo!=null){
			//if (!reserverQueue.isFirst(owner)){
				medium.reserverQueue.add(reserverQueue.getEntry(owner),this);
			//}
			if (reserveInfo.isUnicastInfo()&&receiver.equals(reserveInfo.getReceiver())){
				medium.unicastSenderList.add(owner);
				Map newUnicastReceivers=new HashMap(medium.neighbors);
				newUnicastReceivers.keySet().removeAll(neighbors.keySet());
				newUnicastReceivers.remove(owner);
				Iterator iterator= newUnicastReceivers.values().iterator();
				while (iterator.hasNext()){
					Medium currentMedium=(Medium)iterator.next();
					currentMedium.reserverQueue.add(reserverQueue.getEntry(owner),this);
				}
			}
		}
		Iterator iterator=unicastSenderList.iterator();
		while(iterator.hasNext()){
		    DeviceID address=(DeviceID)iterator.next();
			medium.reserverQueue.add(reserverQueue.getEntry(address),reserverQueue.getEntrysMedium(address));
		}
		neighbors.put(receiver,medium);	
			
		
	}



	/**
	 * @param receiver
	 */
	public void removeNeighbor(DeviceID receiver) {
		Medium medium=(Medium)neighbors.remove(receiver);
		if (reserveInfo!=null){
			
			if (reserveInfo.isUnicastInfo()){
				if (receiver.equals(reserveInfo.getReceiver())){
					medium.freeMedium(owner);
					medium.unicastSenderList.remove(owner);
					Map unicastReceivingNeighbors=new HashMap(medium.neighbors);
					unicastReceivingNeighbors.keySet().removeAll(neighbors.keySet());
					unicastReceivingNeighbors.remove(owner);
					Iterator iterator=unicastReceivingNeighbors.values().iterator();
					while(iterator.hasNext()){
						Medium currentMedium=(Medium)iterator.next();
						currentMedium.freeMedium(owner);
					}
				}else if (!medium.neighbors.containsKey(reserveInfo.getReceiver())){
							
					medium.freeMedium(owner);
				}else if (!neighbors.containsKey(reserveInfo.getReceiver())){
					medium.freeMedium(owner);
				}
				
			}else {
				medium.freeMedium(owner);	
			}
			
		}
		if (unicastSenderList.contains(receiver)){
			unicastSenderList.remove(receiver);
		}
		Iterator iterator=((List)unicastSenderList.clone()).iterator();
		while (iterator.hasNext()){
		    DeviceID address=(DeviceID)iterator.next();
			if (!medium.neighbors.containsKey(address)&&!address.equals(receiver)){
				medium.freeMedium(address);
			}
		}
		//(Medium)neighbors.remove(receiver);
	}		
	

}
