package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.basetypes.SimulationDeviceID;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.wlan.events.EndReceiptEvent;
import de.uni_trier.jane.service.network.link_layer.wlan.events.StartReceiptEvent;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.shapes.Shape;

// Die Realisierung der Physical-Layer eines WLAN Device
public class PhysicalLayer implements MacToPhysicalLayerInterface, PhysicalLayerInterface, RuntimeService {

    private static final boolean SEND_EVENTS = true;
    private ServiceID mediumServiceID;
    private ServiceID macLayerServiceID;
    private MediumInterface medium;
    private PhysicalToMacLayerInterface macLayer;
    private Mib mib;
    private RuntimeOperatingSystem operatingSystem;
    private int physicalState;
    private int mediumState;

    /**
     * 
     * Constructor for class <code>PhysicalLayer</code>
     *
     * @param mediumServiceID
     * @param mib
     * @param macLayer
     */
    public PhysicalLayer(ServiceID mediumServiceID, Mib mib, ServiceID macLayer) {
        this.macLayerServiceID=macLayer;
		this.mediumServiceID = mediumServiceID;
		this.mib = mib;
    } 

//    public void setMacLayerServiceID(ServiceID macLayerServiceID) {        
//		this.macLayerServiceID = macLayerServiceID;
//		
//    } 

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {        
		this.operatingSystem = runtimeOperatingSystem;
        //register interface for exporting as stub
		operatingSystem.registerSignalListener(MacToPhysicalLayerInterface.class);
		operatingSystem.registerSignalListener(PhysicalLayerInterface.class);
		
		//register PhysicalService at Global Medium-Service
        operatingSystem.registerAtService(mediumServiceID, MediumInterface.class);
        medium = (MediumInterface) operatingSystem.getSignalListenerStub(mediumServiceID,MediumInterface.class);                
        medium.registerDevice(mib); 
        //waiting for the other services to get up... 
		operatingSystem.setTimeout(new ServiceTimeout(0) {
			public void handle() {
				macLayer = (PhysicalToMacLayerInterface) operatingSystem.getSignalListenerStub(macLayerServiceID, PhysicalToMacLayerInterface.class);
//				medium = (MediumInterface) operatingSystem.getSignalListenerStub(mediumServiceID,MediumInterface.class);		        
//				medium.registerDevice(mib);				
				setMediumState(MediumInterface.IDLE);
				setPhysicalState(PhysicalLayerInterface.CCA);
			}
			
		});
    } 

    public ServiceID getServiceID() {        
		// TODO Auto-generated method stub
		return null;
    } 

    public void finish() {        
		// TODO Auto-generated method stub
    } 

    public Shape getShape() {        
		// TODO Auto-generated method stub
		return null;
    } 

    public void getParameters(Parameters parameters) {        
		// TODO Auto-generated method stub
    } 

    private void setPhysicalState(int physicalState) {        
		if (this.physicalState == physicalState) {
			throw new IllegalStateException("Zeit " + operatingSystem.getTime() + ": Device " + operatingSystem.getDeviceID() + " Am physikalischen Zustand hat sich nichts geändert");
		}
		if ((this.physicalState == PhysicalLayerInterface.TRANSMIT) && (physicalState == PhysicalLayerInterface.RECEIVE))
			return ;
		// Physical State von CCA auf RECEIVE und umgekehrt
		// werden in der Physical Layer gehandhabt und dann an die Mac Layer weitergeleitet
		// Die restlichen Übergänge werden in der Mac Layer geregelt
		if (((physicalState == PhysicalLayerInterface.RECEIVE) || (this.physicalState == PhysicalLayerInterface.RECEIVE)) && 
                    (physicalState != PhysicalLayerInterface.TRANSMIT)){
            
     
			macLayer.setPhysicalState(physicalState);
        }
		this.physicalState = physicalState;
    } 

    // Setzt den Zustand des Mediums
    private void setMediumState(int mediumState) {        
		this.mediumState = mediumState;
    } 

    // Führt den Start einer Übertragung aus
    public void startTransmission(MacHeader macHeader, Frame frame, double signalFraction) {        
        if (SEND_EVENTS) operatingSystem.sendEvent(new StartReceiptEvent(true,operatingSystem.getDeviceID()));
		setPhysicalState(PhysicalLayerInterface.TRANSMIT);
		setMediumState(MediumInterface.BUSY);
		medium.newTransmission(mib.getSignalStrength()*signalFraction, macHeader, frame);				
    } 

    // Signalisiert das Ende einer Übertragung
    public void endTransmission() {        
        operatingSystem.sendEvent(new EndReceiptEvent(operatingSystem.getDeviceID()));
		setPhysicalState(PhysicalLayerInterface.CCA);
		setMediumState(MediumInterface.IDLE);
		medium.endOfTransmission(new EndTransmitCallback() {
			public void stillBusy() {
				setPhysicalState(PhysicalLayerInterface.RECEIVE);
				setMediumState(MediumInterface.BUSY);
			}
			
		});
    } 

    // Das Gerät beginnt den Empfang eines Frames
    public void startReceipt(MacHeader macHeader, Frame frame) {
        if (SEND_EVENTS){
            operatingSystem.sendEvent(new StartReceiptEvent((macHeader != null) && (frame != null),operatingSystem.getDeviceID()));
        }
		// Absicherung, damit nicht falsch die duration weitergegeben wird
		if (physicalState == PhysicalLayerInterface.CCA) {
			if ((macHeader != null) && (frame != null)) {
	    		macLayer.receiveFrame(macHeader, frame);
			}
			setMediumState(MediumInterface.BUSY);		
			setPhysicalState(PhysicalLayerInterface.RECEIVE);
		}
		else if (physicalState == PhysicalLayerInterface.RECEIVE) {
			if ((macHeader != null) && (frame != null)) {
	    		macLayer.receiveFrame(macHeader, frame);
			}
		}
		else{
		    System.err.println("Zeit " + operatingSystem.getTime() + ": Device " + operatingSystem.getDeviceID() + " Falscher Zustand.");
            //throw new IllegalStateException("Zeit " + operatingSystem.getTime() + ": Device " + operatingSystem.getDeviceID() + " Falscher Zustand.");
        }
            
		
    } 

    // Der Empfang eines Frames wurde erfolgreich abgeschlossen
    public void endReceipt() {
        operatingSystem.sendEvent(new EndReceiptEvent(operatingSystem.getDeviceID()));
		if (physicalState == PhysicalLayerInterface.RECEIVE) {
			setMediumState(MediumInterface.IDLE);
			setPhysicalState(PhysicalLayerInterface.CCA);
		}		
		else
			throw new IllegalStateException("Zeit " + operatingSystem.getTime() + ": Device " + operatingSystem.getDeviceID() + " Falscher Zustand.");
    } 

    // Diese Funktion verarbeitet eine auftretende Kollision/Fehler
    public void collision() {        
		if (physicalState != PhysicalLayerInterface.TRANSMIT)
			macLayer.collision();
		else{
            System.err.println("Zeit " + operatingSystem.getTime() + ": Device " + operatingSystem.getDeviceID() + " Falscher Zustand.");
			//throw new IllegalStateException("Zeit " + operatingSystem.getTime() + ": Device " + operatingSystem.getDeviceID() + " Falscher Zustand.");
        }
    }

    public static ServiceID createInstance(ServiceUnit serviceUnit,Mib mib, ServiceID macLayer) {
        ServiceID mediumID;
        if (serviceUnit.hasService(MediumInterface.class)){
            mediumID=serviceUnit.getService(MediumInterface.class);
        }else{
            throw new IllegalStateException("No WLAN Medium instantiated");
        }
        
        return serviceUnit.addService(new PhysicalLayer(mediumID,mib,macLayer));
    } 
 }
