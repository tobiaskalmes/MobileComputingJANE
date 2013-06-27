/*
 * Created on 23.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Data;
import de.uni_trier.jane.basetypes.DataID;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.ListenerAccess;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.signaling.SignalListener;

	
public final class NeighborDiscoveryServiceStub implements NeighborDiscoveryService_sync {


    private RuntimeOperatingSystem operatingSystem;
	private ServiceID NeighborDiscoveryServiceServiceID;
	
	public NeighborDiscoveryServiceStub(
		RuntimeOperatingSystem operatingSystem,
		ServiceID NeighborDiscoveryServiceServiceID) {
		this.operatingSystem = operatingSystem;
		this.NeighborDiscoveryServiceServiceID = NeighborDiscoveryServiceServiceID;
	}
	
	public void registerAtService(){
	    operatingSystem.registerAtService(NeighborDiscoveryServiceServiceID,NeighborDiscoveryService_sync.class);
	}
	
	public void unregisterAtService(){
	    operatingSystem.unregisterAtService(NeighborDiscoveryServiceServiceID,NeighborDiscoveryService_sync.class);
	}

	
	private static final class GetNeighborDiscoveryPropertiesSyncAccess
		implements
			ListenerAccess {
		public GetNeighborDiscoveryPropertiesSyncAccess() {
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service)
				.getNeighborDiscoveryProperties();
		}

	}
	public NeighborDiscoveryProperties getNeighborDiscoveryProperties() {
		return (NeighborDiscoveryProperties) operatingSystem
			.accessSynchronous(
				NeighborDiscoveryServiceServiceID,
				new GetNeighborDiscoveryPropertiesSyncAccess());
	}

	private static final class SetOwnDataSignal implements Signal {
		private Data data;
		public SetOwnDataSignal(Data data) {
			this.data = data;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public void handle(SignalListener service) {
			((NeighborDiscoveryService_sync) service).setOwnData(data);
		}

	}
	public void setOwnData(Data data) {
		operatingSystem.sendSignal(
			NeighborDiscoveryServiceServiceID,
			new SetOwnDataSignal(data));
	}

	private static final class RemoveOwnDataSignal implements Signal {
		private DataID id;
		public RemoveOwnDataSignal(DataID id) {
			this.id = id;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public void handle(SignalListener service) {
			((NeighborDiscoveryService_sync) service).removeOwnData(id);
		}

	}
	public void removeOwnData(DataID id) {
		operatingSystem.sendSignal(
			NeighborDiscoveryServiceServiceID,
			new RemoveOwnDataSignal(id));
	}

	private static final class GetNeighborsSyncAccess
		implements
			ListenerAccess {
		public GetNeighborsSyncAccess() {
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service).getNeighbors();
		}

	}
	public Address[] getNeighbors() {
		return (Address[]) operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new GetNeighborsSyncAccess());
	}

	private static final class GetFilteredNeighborsSyncAccess
		implements
			ListenerAccess {
		private NeighborDiscoveryFilter filter;
		public GetFilteredNeighborsSyncAccess(NeighborDiscoveryFilter filter) {
			this.filter = filter;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service)
				.getNeighbors(filter);
		}

	}
	public Address[] getNeighbors(
		NeighborDiscoveryFilter filter) {
		return (Address[]) operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new GetFilteredNeighborsSyncAccess(filter));
	}

	private static final class CountNeighborsSyncAccess
		implements
			ListenerAccess {
		public CountNeighborsSyncAccess() {
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return new Integer(((NeighborDiscoveryService_sync) service).countNeighbors());
		}

	}
	public int countNeighbors() {
		return ((Integer) operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new CountNeighborsSyncAccess())).intValue();
	}

	private static final class CountFilteredNeighborsSyncAccess
		implements
			ListenerAccess {
		private NeighborDiscoveryFilter filter;
		public CountFilteredNeighborsSyncAccess(
			NeighborDiscoveryFilter filter) {
			this.filter = filter;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return new Integer(((NeighborDiscoveryService_sync) service)
				.countNeighbors(filter));
		}

	}
	public int countNeighbors(NeighborDiscoveryFilter filter) {
		return ((Integer)operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new CountFilteredNeighborsSyncAccess(filter))).intValue();
	}

	private static final class GetNeighborDataSyncAccess
		implements
			ListenerAccess {
		public GetNeighborDataSyncAccess() {
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service).getNeighborDiscoveryData();
		}

	}
	public NeighborDiscoveryData[] getNeighborDiscoveryData() {
		return (NeighborDiscoveryData[]) operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new GetNeighborDataSyncAccess());
	}

	private static final class GetFilteredNeighborDataSyncAccess
		implements
			ListenerAccess {
		private NeighborDiscoveryFilter filter;
		public GetFilteredNeighborDataSyncAccess(
			NeighborDiscoveryFilter filter) {
			this.filter = filter;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service)
				.getNeighborDiscoveryData(filter);
		}

	}
	public NeighborDiscoveryData[] getNeighborDiscoveryData(
		NeighborDiscoveryFilter filter) {
		return (NeighborDiscoveryData[]) operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new GetFilteredNeighborDataSyncAccess(filter));
	}

	private static final class HasNeighborDataSyncAccess
		implements
			ListenerAccess {
		private Address address;
		public HasNeighborDataSyncAccess(Address address) {
			this.address = address;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return new Boolean(((NeighborDiscoveryService_sync) service)
				.hasNeighborDiscoveryData(address));
		}

	}
	public boolean hasNeighborDiscoveryData(Address address) {
		return ((Boolean)operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new HasNeighborDataSyncAccess(address))).booleanValue();
	}

	private static final class HasFilteredNeighborDataSyncAccess
		implements
			ListenerAccess {
		private Address address;
		private NeighborDiscoveryFilter filter;
		public HasFilteredNeighborDataSyncAccess(
			Address address,
			NeighborDiscoveryFilter filter) {
			this.address = address;
			this.filter = filter;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return new Boolean(((NeighborDiscoveryService_sync) service)
				.hasNeighborDiscoveryData(address, filter));
		}

	}
	public boolean hasNeighborDiscoveryData(
		Address address,
		NeighborDiscoveryFilter filter) {
		return ((Boolean) operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new HasFilteredNeighborDataSyncAccess(address, filter))).booleanValue();
	}

	private static final class GetNeighborDataSyncAccess1
		implements
			ListenerAccess {
		private Address address;
		public GetNeighborDataSyncAccess1(Address address) {
			this.address = address;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service)
				.getNeighborDiscoveryData(address);
		}

	}
	public NeighborDiscoveryData getNeighborDiscoveryData(Address address) {
		return (NeighborDiscoveryData) operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new GetNeighborDataSyncAccess1(address));
	}

	private static final class GetDataSyncAccess implements ListenerAccess {
		private Address address;
		private DataID dataID;
		public GetDataSyncAccess(Address address, DataID dataID) {
			this.address = address;
			this.dataID = dataID;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service).getData(
				address,
				dataID);
		}

	}
	public Data getData(Address address, DataID dataID) {
		return (Data) operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new GetDataSyncAccess(address, dataID));
	}

	private static final class HasDataSyncAccess implements ListenerAccess {
		private Address address;
		private DataID dataID;
		public HasDataSyncAccess(Address address, DataID dataID) {
			this.address = address;
			this.dataID = dataID;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}

		public Object handle(SignalListener service) {
			return new Boolean(((NeighborDiscoveryService_sync) service).hasData(address, dataID));
		}

	}
	public boolean hasData(Address address, DataID dataID) {
		return ((Boolean) operatingSystem.accessSynchronous(
			NeighborDiscoveryServiceServiceID,
			new HasDataSyncAccess(address, dataID))).booleanValue();
	}

	private static final class GetOwnAddressAccess implements ListenerAccess {
		public Dispatchable copy() {
			return this;
		}
		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}
		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service).getOwnAddress();
		}
	}
	public Address getOwnAddress() {
		return (Address)operatingSystem.accessSynchronous(
				NeighborDiscoveryServiceServiceID,
				new GetOwnAddressAccess());
	}

	private static final class GetGatewayNodesAccess implements ListenerAccess {
		private Address destination;
		public GetGatewayNodesAccess(Address destination) {
			this.destination = destination;
		}
		public Dispatchable copy() {
			return this;
		}
		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}
		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service).getGatewayNodes(destination);
		}
	}

	public Address[] getGatewayNodes(Address destination) {
		return (Address[])operatingSystem.accessSynchronous(
				NeighborDiscoveryServiceServiceID,
				new GetGatewayNodesAccess(destination));
	}

	private static final class GetNeighborNodesAccess implements ListenerAccess {
		private static final long serialVersionUID = -5605621844603464268L;
		private Address gateway;
		public GetNeighborNodesAccess(Address gateway) {
			this.gateway = gateway;
		}
		public Dispatchable copy() {
			return this;
		}
		public Class getReceiverServiceClass() {
			return NeighborDiscoveryService_sync.class;
		}
		public Object handle(SignalListener service) {
			return ((NeighborDiscoveryService_sync) service).getNeighborNodes(gateway);
		}
	}
	public Address[] getNeighborNodes(Address gateway) {
		return (Address[])operatingSystem.accessSynchronous(
				NeighborDiscoveryServiceServiceID,
				new GetNeighborNodesAccess(gateway));
	}
    
    /**
     * TODO: comment class  
     * @author daniel
     **/

    private static final class AddUnicastErrorProviderSignal implements Signal {

        private ServiceID serviceID;

        public AddUnicastErrorProviderSignal(ServiceID serviceID) {
            this.serviceID=serviceID;
        }

        //
        public void handle(SignalListener listener) {
            ((NeighborDiscoveryService)listener).addUnicastErrorProvider(serviceID);

        }

        //
        public Dispatchable copy() {
            return this;
        }

        //
        public Class getReceiverServiceClass() {

            return NeighborDiscoveryService.class;
        }

    }
    public void addUnicastErrorProvider(ServiceID serviceID) {
        operatingSystem.sendSignal(NeighborDiscoveryServiceServiceID,new AddUnicastErrorProviderSignal(serviceID));
        
    }

}