import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.gui.ExtendedClickAndPlaySimulationGUI;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService;
import de.uni_trier.jane.service.neighbor_discovery.OneHopNeighborDiscoveryService;
import de.uni_trier.jane.service.network.link_layer.LinkLayer;
import de.uni_trier.jane.service.network.link_layer.collision_free.CollisionFreeNetwork;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.Simulation;
import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.campus.ClickAndPlayMobilitySourceLocation;
import de.uni_trier.jane.simulation.dynamic.mobility_source.campus.FixedPositionLocation;
import de.uni_trier.jane.simulation.kernel.TimeExceeded;


public class TestSimulation extends Simulation {

    public static void main(String[] args) {
        Simulation simulation = new TestSimulation();
        simulation.run();
    }

    /* Aufbau der Simulationsumgebung
     */
    @Override
    public void initSimulation(SimulationParameters parameters) {
        MobilitySource mobilitySource = null;


// ------------------------------------------------------------------------
        //Feste Positionierung �ber Koordinaten
        //Bewegung der Knoten per Klick: Knoten anklicken, dann auf Zielpunkt klicken

        //Definition von drei Knoten und deren X/Y-Koordinaten
        //Definition der Simulationsfl�che und deren Gr��e
        FixedPositionLocation fixed = new FixedPositionLocation
                (new Position[]
                         {
                                 new Position(10, 410),
                                 new Position(110, 260),
                                 new Position(260, 160),
                                 new Position(410, 60),
                                 new Position(410, 260),
                                 new Position(110, 560)
                         },
                 new Rectangle(new Extent(600, 600))
                );


        //Definition des Mobility Modells: Click and Play
        mobilitySource = new ClickAndPlayMobilitySourceLocation(
                fixed,        //Positionen
                50.0,        //Bewegungsgeschwindigkeit
                181.0,         //Senderadius
                6,             //Anzahl der Ger�te
                1.0);


        ExtendedClickAndPlaySimulationGUI gui = new ExtendedClickAndPlaySimulationGUI(
                (ClickAndPlayMobilitySource) mobilitySource);
// ------------------------------------------------------------------------


/*
// ------------------------------------------------------------------------
		// Zufaellige Bewegung der mobilen Geraete

		mobilitySource = RandomMobilitySource.createRandomWaypointMobilitySource( parameters.getDistributionCreator(),
				10, // Number of devices
				200, // lifetime of devices
				500, // width 500,
				500, // height
				0.0, // pause
				100, // min sending radius
				100, // max sending radius
				2.0, // min speed
				10.0 // max speed
				);

		SimulationGUI gui = new SimulationGUI(new Extent(500, 500));
// ------------------------------------------------------------------------
*/


        //Init der GUI, gilt f�r beide Bewegungsmodelle
        //Beenden der GUI �ber roten Button rechts neben der JAVA Console, nicht �ber GUI!
        parameters.useVisualisation(gui);
        parameters.setMobilitySource(mobilitySource);

        //Dauer der Simulation in s
        parameters.setTerminalCondition(
                new TimeExceeded(parameters.getEventSet(), 1000));


    }

    //Aufbau des Netzwerkes, entf�llt bei realen Ger�ten
    //Zweiter Parameter: Senderate, wichitg! Wenn zu kleine verschwinden Nachrichten!
    @Override
    public void initGlobalServices(ServiceUnit serviceUnit) {
        CollisionFreeNetwork.createInstance(serviceUnit,
                                            8 * 1024, true, false, true);
    }

    //Lokale Services starten... Laufen auf jedem mobilen Endger�t
    @Override
    public void initServices(ServiceUnit serviceUnit) {

        //Ref zum LinkLayer
        ServiceID linkLayerID = serviceUnit.getService(LinkLayer.class);

        //Nachbarschaftsservice anlegen
        OneHopNeighborDiscoveryService.createInstance(serviceUnit, false);
        //Ref zum Nachbarschaftsservice
        ServiceID neighborID = serviceUnit.getService(NeighborDiscoveryService.class);

        //Testservice anlegen
        TestService testService = new TestService(linkLayerID, neighborID);
        serviceUnit.addService(testService);

    }

}
