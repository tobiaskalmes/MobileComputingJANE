package de.uni_trier.jane.service.energy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.link_layer.global.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * 
 * @author Hannes Frey
 */
public class DefaultEnergyService implements EnergyConsumptionListenerService, EnergyStatusProviderService, SimulationService {

	private static final ServiceID serviceID = new EndpointClassID(DefaultEnergyService.class);
	
	private SimulationOperatingSystem simulationOperatingSystem;
	private double startJoule;
	private double joule;
	private Map map;

	public static void createFactory(ServiceUnit serviceUnit) {
       	serviceUnit.addServiceFactory(new ServiceFactory() {
			public void initServices(ServiceUnit serviceUnit) {
				DefaultEnergyService.createInstance(serviceUnit);
			}
       	});
	}

	public static void createFactory(ServiceUnit serviceUnit, final double joule) {
       	serviceUnit.addServiceFactory(new ServiceFactory() {
			public void initServices(ServiceUnit serviceUnit) {
				DefaultEnergyService.createInstance(serviceUnit, joule);
			}
       	});
	}

	public static void createInstance(ServiceUnit serviceUnit) {
		createInstance(serviceUnit, 100000.0);
	}

	public static void createInstance(ServiceUnit serviceUnit, double joule) {
		DefaultEnergyService energyService = new DefaultEnergyService(joule);
		serviceUnit.addService(energyService);
	}

	/**
	 * Creates a new Service, 
	 * @param joule The starting energy
	 */
	public DefaultEnergyService(double joule){
		this.startJoule = joule;
		this.joule=joule;
		map=new HashMap();
	}
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.energy.EnergyConsumptionListenerService#setCurrentEnergyConsumption(double)
	 */
	public void setCurrentEnergyConsumption(double watt) {
		ServiceID serviceID = simulationOperatingSystem.getCallingServiceID();
		double time=simulationOperatingSystem.getSimulationTime();
		if(map.containsKey(serviceID)){
			HashData hashData=(HashData) map.get(serviceID);
			double diffTime=time-hashData.getTime();
			reduceEnergy(watt*diffTime);
		}
		map.put(serviceID,new HashData(time,watt));
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.energy.EnergyConsumptionListenerService#reduceEnergy(double)
	 */
	public void reduceEnergy(double joule) {
		this.joule-=joule;
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.energy.EnergyStatusProviderService#getEnergyStatus()
	 */
	public EnergyStatus getEnergyStatus() {
		double joule=this.joule;
		Iterator iterator=map.keySet().iterator();
		double time=simulationOperatingSystem.getSimulationTime();
		while(iterator.hasNext()){
			ServiceID serviceID=(ServiceID) iterator.next();
			HashData hashData=(HashData) map.get(serviceID);
			joule-=hashData.getWatt()*(time-hashData.getTime());
		}
		return new EnergyStatus(joule);
	}

	public void start(SimulationOperatingSystem simulationOperatingSystem) {
		this.simulationOperatingSystem = simulationOperatingSystem;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getServiceID()
	 */
	public ServiceID getServiceID() {
		return serviceID;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#finish()
	 */
	public void finish() {
		// ignore

	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getShape()
	 */
	public Shape getShape() {
		double j = getEnergyStatus().getRemainingJoule();
		ShapeCollection shapeCollection = new ShapeCollection();
		if (j <= 0) {
			shapeCollection.addShape(new EllipseShape(simulationOperatingSystem.getDeviceID(), new Extent(10, 10), Color.BLACK, true));
		}else {
			shapeCollection.addShape(new ProgressShape(simulationOperatingSystem.getDeviceID(), Color.BLACK, Color.RED, 20.0, 4.0, j / startJoule), new Position(0,5));
		}
//		shapeCollection.addShape(new TextShape(simulationOperatingSystem.getDeviceID().toString(),simulationOperatingSystem.getDeviceID(),Color.RED),new Position(3.,-3.,-5.));
		return shapeCollection;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
	 */
	public void getParameters(Parameters parameters) {
		// ignore

	}
	
	private final class HashData{
		private double time;
		private double watt;
		
		
		/**
		 * @param time
		 * @param watt
		 */
		public HashData(double time, double watt) {
			this.time = time;
			this.watt = watt;
		}
		/**
		 * @return Returns the time.
		 */
		public double getTime() {
			return time;
		}
		/**
		 * @return Returns the watt.
		 */
		public double getWatt() {
			return watt;
		}
	}

}
