package de.uni_trier.jane.service.routing.multicast.spbm;

import java.util.BitSet;

import de.uni_trier.jane.basetypes.Position;

public class MulticastNeighborEntry {
	private Position position;

	private BitSet groups;

	public MulticastNeighborEntry(Position position, BitSet groups) {
		super();
		this.position = position;
		this.groups = groups;
	}

	public BitSet getGroups() {
		return groups;
	}

	public void setGroups(BitSet groups) {
		this.groups = groups;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
}
