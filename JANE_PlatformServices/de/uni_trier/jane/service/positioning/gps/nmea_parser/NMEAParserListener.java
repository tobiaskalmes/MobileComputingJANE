package de.uni_trier.jane.service.positioning.gps.nmea_parser;

import de.uni_trier.jane.service.positioning.gps.nmea_parser.message.*;

public interface NMEAParserListener {
	
	public void parsedGGAMessage(GGAMessage message);
	public void parsedGSAMessage(GSAMessage message);
	public void parsedGSVMessage(GSVMessage message);
	public void parsedRMCMessage(RMCMessage message);
	/**
	 * @param message
	 */
	public void parsedGSAMessage(GLLMessage message);

}
