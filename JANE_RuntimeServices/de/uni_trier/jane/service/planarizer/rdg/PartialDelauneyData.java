package de.uni_trier.jane.service.planarizer.rdg;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;

public class PartialDelauneyData implements Data {

	private static final long serialVersionUID = 5970664315603299112L;
	public static final DataID DATA_ID = new ClassDataID(PartialDelauneyData.class);
	private TransmitData graph;
	
	public PartialDelauneyData(TransmitData graph){
		this.graph=graph;
	}
	
	public DataID getDataID() {
		return DATA_ID;
	}

	public Data copy() {
		return this;
	}

	public int getSize() {
		return 0;
	}

	public TransmitData getTransmitData() {
		return graph;
	}
	

}
