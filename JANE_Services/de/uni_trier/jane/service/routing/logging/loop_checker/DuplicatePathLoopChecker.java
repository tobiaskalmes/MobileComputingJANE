package de.uni_trier.jane.service.routing.logging.loop_checker;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;

public class DuplicatePathLoopChecker implements LoopChecker {

	public static LoopCheckerFactory DUPLICATE_NODE_SEQUENCE_FACTORY = new LoopCheckerFactory() {
		public LoopChecker getLoopChecker() {
			return new DuplicatePathLoopChecker(false);
		}
		public String toString() {
			return "DuplicateNodeSequenceChecker";
		}
	};

	public static LoopCheckerFactory DUPLICATE_EDGE_SEQUENCE_FACTORY = new LoopCheckerFactory() {
		public LoopChecker getLoopChecker() {
			return new DuplicatePathLoopChecker(true);
		}
		public String toString() {
			return "DuplicateEdgeSequenceChecker";
		}
	};

	public static final ServiceObjectElement DUPLICATE_EDGE_SEQUENCE_LOOP_CHECKER_FACTORY = new ServiceObjectElement("duplicateEdgeSequence") {
		public Object getValue(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			return DUPLICATE_EDGE_SEQUENCE_FACTORY;
		}
	};

	public static final ServiceObjectElement DUPLICATE_NODE_SEQUENCE_LOOP_CHECKER_FACTORY = new ServiceObjectElement("duplicateNodeSequence") {
		public Object getValue(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			return DUPLICATE_NODE_SEQUENCE_FACTORY;
		}
	};

	private boolean checkEdges;
	private LinkedList nodeList;
	private Map indexMap;
	private int loopLength;

	public DuplicatePathLoopChecker(boolean checkEdges) {
		this.checkEdges = checkEdges;
		nodeList = new LinkedList();
		indexMap = new HashMap();
		loopLength = -1;
	}

	public void reset(Address node) {
		nodeList.clear();
		indexMap.clear();
		loopLength = -1;
		addNode(node);
	}

	public void addNode(Address node) {
		
		Integer index = (Integer)indexMap.get(node);
		nodeList.add(node);
		int lastIndex = nodeList.size() - 1;
		indexMap.put(node, new Integer(lastIndex));
		
		if(index != null) {

			ListIterator lastIterator = nodeList.listIterator(lastIndex);
			ListIterator previousIterator = nodeList.listIterator(index.intValue());

			int len = 0;
			
			while(lastIterator.hasPrevious() && previousIterator.hasPrevious()) {
				
				len++;
				
				Address lastPredecessor = (Address)lastIterator.previous();
				Address previousPredecessor = (Address)previousIterator.previous();
				
				if(!lastPredecessor.equals(previousPredecessor)) {
					return;
				}

				if(previousPredecessor.equals(node)) {
					
					if(checkEdges) {
						if(lastIterator.hasPrevious() && previousIterator.hasPrevious()) {
							lastPredecessor = (Address)lastIterator.previous();
							previousPredecessor = (Address)previousIterator.previous();
							if(!lastPredecessor.equals(previousPredecessor)) {
								return;
							}
						}
					}
					loopLength = len;
					return;
					
				}
				
			}

		}


	}

	public boolean checkForLoop() {
		return loopLength >= 0;
	}

	public int getLoopLength() {
		return loopLength;
	}

	public static void main(String[] args) {
		DuplicatePathLoopChecker loopChecker = new DuplicatePathLoopChecker(true);
		
		SimulationDeviceID node1 = new SimulationDeviceID(1);
		SimulationDeviceID node2 = new SimulationDeviceID(2);
		SimulationDeviceID node3 = new SimulationDeviceID(3);
		SimulationDeviceID node4 = new SimulationDeviceID(4);

		

		loopChecker.addNode(node1);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node2);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node3);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node4);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node3);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node4);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node3);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node4);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node3);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node4);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());

		
		
		loopChecker.addNode(node1);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node2);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node3);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
//		loopChecker.addNode(node4);
//		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node2);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node3);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
//		loopChecker.addNode(node4);
//		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node2);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node3);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		loopChecker.addNode(node4);
		System.out.println(loopChecker.checkForLoop() + " " + loopChecker.getLoopLength());
		
	}


}
