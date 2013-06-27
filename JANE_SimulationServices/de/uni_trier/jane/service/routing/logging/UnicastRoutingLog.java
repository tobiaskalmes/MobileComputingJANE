package de.uni_trier.jane.service.routing.logging;

import java.io.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.logging.loop_checker.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.global_knowledge.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

/*
 * Die Ausgabe des Endresultats eines Routingtasks ist eine Zeile mit folgendem Inhalt:
 * 
 * line prefix
 * message ID
 * action
 * loop length
 * start node
 * end node
 * start time
 * end time
 * time delta
 * start position
 * end position
 * position delta
 * hop count
 * distance count
 * energy count 1
 *  ...
 * energy count n
 * 
 */

// TODO Loop-Checking wird jetzt von dem RoutingService übernommen!
public class UnicastRoutingLog implements GlobalService, GlobalRoutingLogService {

    private static final ServiceID SERVICE_ID = new EndpointClassID(UnicastRoutingLog.class.getName());

    private boolean stopOnEmpty;
    private boolean stopOnLoop;
    
    private int numberOfRoutingTasks;
    
    private LoopCheckerFactory loopCheckerFactory;
    private boolean logDetails;
    private PrintStream printStream;
	private String prefixString;
	private EnergyModel[] energyModels;
    private Map messageMap;

    private GlobalOperatingSystem globalOperatingSystem;

    public static void createInstance(ServiceUnit serviceUnit) {
    	createInstance(serviceUnit, 1, true, true, DuplicateNodeLoopChecker.FACTORY, true, null, "", new EnergyModel[0]);
    }

    public static void createInstance(ServiceUnit serviceUnit, int numberOfRoutingTasks, boolean stopOnEmpty, boolean stopOnLoop, LoopCheckerFactory loopCheckerFactory, boolean logDetails, String logFileName, String prefixString, EnergyModel[] energyModels) {
    	PrintStream printStream = null;
    	if(logFileName != null) {
			try {
				FileOutputStream fos = new FileOutputStream(logFileName, true);
	        	printStream = new PrintStream(fos, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
        UnicastRoutingLog routingLog = new UnicastRoutingLog(numberOfRoutingTasks, stopOnEmpty, stopOnLoop, loopCheckerFactory, logDetails, printStream, prefixString, energyModels);
        serviceUnit.addService(routingLog);
        LocalRoutingLogProxyFactory proxyFactory = new LocalRoutingLogProxyFactory(routingLog.getServiceID());
        serviceUnit.addServiceFactory(proxyFactory);
    }

    public UnicastRoutingLog(int numberOfRoutingTasks, boolean stopOnEmpty, boolean stopOnLoop, LoopCheckerFactory loopCheckerFactory, boolean logDetails, PrintStream printStream, String prefixString, EnergyModel[] energyModels) {
    	this.numberOfRoutingTasks = numberOfRoutingTasks;
        this.stopOnEmpty = stopOnEmpty;
        this.stopOnLoop = stopOnLoop;
        this.loopCheckerFactory = loopCheckerFactory;
		this.printStream = printStream;
		this.logDetails = logDetails;
		this.prefixString = "";
		if(prefixString.length() > 0) {
			this.prefixString = prefixString + " ";
		}
		this.energyModels = energyModels;
		messageMap = new HashMap();
	}

	public void start(GlobalOperatingSystem globalOperatingSystem) {
        this.globalOperatingSystem = globalOperatingSystem;
    }

    public ServiceID getServiceID() {
        return SERVICE_ID;
    }

    public void finish() {
    	if(printStream != null) {
    		printStream.close();
    	}
    }

    public Shape getShape() {
        return null;
    }

    public void getParameters(Parameters parameters) {
    	parameters.addParameter("numberOfRoutingTasks", numberOfRoutingTasks);
    	parameters.addParameter("stopOnEmpty", stopOnEmpty);
    	parameters.addParameter("stopOnLoop", stopOnLoop);
    	parameters.addParameter("loopCheckerFactory", loopCheckerFactory.toString());
    	parameters.addParameter("logDetails", logDetails);
    	parameters.addParameter("logToFile", printStream != null);
    	parameters.addParameter("prefixString", prefixString);
    	parameters.addParameter("energyModels", energyModels);
    }

    public void logDropMessage(Address address, MessageID messageID) {
    	MessageInfo messageInfo = (MessageInfo)messageMap.get(messageID);
    	messageInfo.drop(address);
    	writeString(messageInfo.getFinalString("DROP", address, -1));
    	messageMap.remove(messageID);
    	checkFinishCondition();
    }

	public void logLoopMessage(Address address, MessageID messageID, int loopLength) {
    	MessageInfo messageInfo = (MessageInfo)messageMap.get(messageID);
//    	messageInfo.drop(address);
    	writeString(messageInfo.getFinalString("LOOP", address, loopLength));
    	if(stopOnLoop) {
       		globalOperatingSystem.finishSimulation();
    	}
    	messageMap.remove(messageID);
    	checkFinishCondition();
	}


	public void logIgnoreMessage(Address address, MessageID messageID) {
    	writeStep(createPrefix("IGNORE", address, messageID));
    	MessageInfo messageInfo = (MessageInfo)messageMap.get(messageID);
    	messageInfo.ignore(address);
    }

    public void logDeliverMessage(Address address, MessageID messageID) {
    	MessageInfo messageInfo = (MessageInfo)messageMap.get(messageID);
    	messageInfo.deliver(address);
    	writeString(messageInfo.getFinalString("DELIVER", address, -1));
    	messageMap.remove(messageID);
    	checkFinishCondition();
    }

    public void logForwardUnicast(Address address, MessageID messageID, RoutingHeader header, Address receiver) {
    	writeStep(createPrefix("UNICAST", address, messageID) + " -> " + receiver);
    	MessageInfo messageInfo = (MessageInfo)messageMap.get(messageID);
    	messageInfo.unicast(address, header, receiver);
    }

    public void logForwardBroadcast(Address address, MessageID messageID, RoutingHeader header) {
    	writeStep(createPrefix("BROADCAST", address, messageID));
    	MessageInfo messageInfo = (MessageInfo)messageMap.get(messageID);
    	messageInfo.broadcast(address, header);
    }

    public void logForwardError(Address address, MessageID messageID, RoutingHeader header, Address receiver) {
    	writeStep(createPrefix("ERROR", address, messageID) + " -> " + receiver);
    	MessageInfo messageInfo = (MessageInfo)messageMap.get(messageID);
    	messageInfo.error(address, header, receiver);
    }

    public void logMessageReceived(Address address, MessageID messageID, RoutingHeader header, Address sender) {
    	MessageInfo messageInfo = (MessageInfo)messageMap.get(messageID);
    	messageInfo.received(address, header, sender);
   		if(stopOnLoop && messageInfo.loopOccured()) { // TODO Loop-Checking wird jetzt von Routing-Service übernommen!!!
        	writeString(messageInfo.getFinalString("LOOP", address, Integer.MAX_VALUE));
       		globalOperatingSystem.finishSimulation();
       	}
   		else {
           	writeStep(createPrefix("RECEIVE", address, messageID) + " <- " + sender);
   		}
    }
    
	public void logDelegateMessage(Address address, ServiceID routingAlgorithmID, MessageID messageID, RoutingHeader routingHeader) {
    	writeStep(createPrefix("DELEGATE", address, messageID));
    	MessageInfo messageInfo = (MessageInfo)messageMap.get(messageID);
    	messageInfo.delegate(address, routingAlgorithmID, routingHeader);
	}

    public void logStart(Address address, MessageID messageID) {
    	LoopChecker loopChecker = loopCheckerFactory.getLoopChecker();
    	messageMap.put(messageID, new MessageInfo(messageID, address, loopChecker));
    	writeStep(createPrefix("START", address, messageID));
    }

    private void checkFinishCondition() {
    	numberOfRoutingTasks--;
    	if(numberOfRoutingTasks == 0 && stopOnEmpty) {
    		globalOperatingSystem.finishSimulation();
    	}
//    	if(messageMap.isEmpty() && stopOnEmpty) {
//    		globalOperatingSystem.finishSimulation();
//    	}
	}

	private String createPrefix(String action, Address address, MessageID messageID) {
		return prefixString + messageID + " " + action + " " + address + " " + globalOperatingSystem.getSimulationTime();
	}

    private void writeStep(String step) {
    	if(logDetails) {
    		writeString(step);
    	}
    }

	private void writeString(String string) {
		if(printStream != null) {
			printStream.println(string);
		}
		else {
			globalOperatingSystem.write(string);
		}
	}
	
	private class MessageInfo {

		private MessageID messageID;
		private Address startNode;
		private LoopChecker loopChecker;
		private Address currentAddress;
		private double startTime;
		private double receiveTime;
		private Position startPosition;
		private Position lastPosition;
		private Position currentPosition;
		private int hopCount;
		private double euclideanCount;
		private double[] energyCounts;

		public MessageInfo(MessageID messageID, Address startNode, LoopChecker loopChecker) {
			this.messageID = messageID;
			this.startNode = startNode;
			this.loopChecker = loopChecker;
			loopChecker.reset(startNode);
			currentAddress = startNode;
			startTime = globalOperatingSystem.getSimulationTime();
			receiveTime = startTime;
			startPosition = getPosition(startNode);
			lastPosition = startPosition;
			currentPosition = startPosition;
			hopCount = 0;
			euclideanCount = 0.0;
			int len = energyModels.length;
			energyCounts = new double[len];
			for(int i=0; i<len; i++) {
				energyCounts[i] = 0.0;
			}
		}

		public boolean loopOccured() {
			return loopChecker.checkForLoop();
		}

		public void deliver(Address address) {
		}

		public void ignore(Address address) {
		}

		public void drop(Address address) {
		}

		public void received(Address address, RoutingHeader header, Address sender) {
			currentAddress = address;
			receiveTime = globalOperatingSystem.getSimulationTime();
			lastPosition = getPosition(sender);
			currentPosition = getPosition(address);
			hopCount++;
			euclideanCount += currentPosition.distance(lastPosition);
			int len = energyModels.length;
			for(int i=0; i<len; i++) {
				energyCounts[i] += energyModels[i].calculate(lastPosition, currentPosition);
			}
			loopChecker.addNode(address);
		}

		public void error(Address address, RoutingHeader header, Address receiver) {
		}

		public void broadcast(Address address, RoutingHeader header) {
		}

		public void unicast(Address address, RoutingHeader header, Address receiver) {
		}

		public void delegate(Address address, ServiceID routingAlgorithmID, RoutingHeader routingHeader) {
			loopChecker.reset(address);
		}

		public String getFinalString(String action, Address address, int loopLength) {
			StringBuffer buffer = new StringBuffer();
			int len = energyModels.length;
			for(int i=0; i<len; i++) {
				buffer.append(energyCounts[i]);
				if(i < len - 1) {
					buffer.append(" ");
				}
			}
			return prefixString + messageID + " " + action + " " + loopLength + " " + startNode + " " + currentAddress + " " +
				startTime + " " + receiveTime + " " + (receiveTime - startTime) + " " +
				startPosition + " " + currentPosition + " " + currentPosition.distance(startPosition) + " " +
				hopCount + " " + euclideanCount + " " + buffer.toString();
		}

		private Position getPosition(Address address) {
			GlobalKnowledge globalKnowledge = globalOperatingSystem.getGlobalKnowledge();
			DeviceID deviceID = globalKnowledge.getDeviceID(address);
			return globalOperatingSystem.getGlobalKnowledge().getTrajectory(deviceID).getPosition();
		}

	}

}
