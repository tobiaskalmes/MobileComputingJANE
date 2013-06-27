package de.uni_trier.jane.service.positioning.gps.nmea_parser;

import java.util.*;
import org.apache.regexp.*;

import de.uni_trier.jane.service.positioning.gps.nmea_parser.enums.*;
import de.uni_trier.jane.service.positioning.gps.nmea_parser.message.*;
import de.uni_trier.jane.service.positioning.gps.nmea_parser.types.*;

public class NMEAParser {

	private Set listenerSet;
		
	public NMEAParser() {
		listenerSet = new HashSet();
	}

	public void addListener(NMEAParserListener listener) {
		listenerSet.add(listener);
	}

	public void removeListener(NMEAParserListener listener) {
		listenerSet.remove(listener);
	}

	public void parse(String message) {
        if (message.equals("")) return;
		RE pattern;
		try {
			pattern = new RE("\\$((..)(.*))\\*(..)");
		} catch (RESyntaxException e) {
			throw new Error(e.getMessage());
			//return;
		}
		boolean matched = pattern.match(message);
		if(!matched) {
			throw new IllegalArgumentException("The given message does not match the NMEA format.");
		}
		if(pattern.getParenCount() != 5) {
			throw new IllegalArgumentException("The given message is malformed.");
		}
		String body = pattern.getParen(1);
		String device = pattern.getParen(2);
		String info = pattern.getParen(3);
		String checksum = pattern.getParen(4);
		int cs = Integer.parseInt(checksum, 16);
		int s = 0;
		for(int i=0; i<body.length(); i++) {
			s ^= body.charAt(i);
		}
		if(s != cs) {
			throw new IllegalArgumentException("The given checksum is not valid.");
		}
		TalkerID talkerID = TalkerID.parse(device);
		parseInfo(talkerID, info);
	}

	private void parseInfo(TalkerID talkerID, String info) {
		StringTokenizer tokenizer = new StringTokenizer(info, ",", true);
		String[] tokens = getTokens(tokenizer);
		Iterator it = listenerSet.iterator();
		if(tokens[0].equals("GGA")) {
			GGAMessage message = parseGGAMessage(talkerID, tokens);
			while (it.hasNext()) {
				NMEAParserListener listener = (NMEAParserListener)it.next();
				listener.parsedGGAMessage(message);
			}
		}
		else if(tokens[0].equals("GSA")) {
			GSAMessage message = parseGSAMessage(talkerID, tokens);
			while (it.hasNext()) {
				NMEAParserListener listener = (NMEAParserListener)it.next();
				listener.parsedGSAMessage(message);
			}
		}
		else if(tokens[0].equals("GSV")) {
			GSVMessage message = parseGSVMessage(talkerID, tokens);
			while (it.hasNext()) {
				NMEAParserListener listener = (NMEAParserListener)it.next();
				listener.parsedGSVMessage(message);
			}
		}
		else if(tokens[0].equals("RMC")) {
			RMCMessage message = parseRMCMessage(talkerID, tokens);
			while (it.hasNext()) {
				NMEAParserListener listener = (NMEAParserListener)it.next();
				listener.parsedRMCMessage(message);
			}
		}
		else if(tokens[0].equals("GLL")) {
			GLLMessage message = parseGLLMessage(talkerID, tokens);
			while (it.hasNext()) {
				NMEAParserListener listener = (NMEAParserListener)it.next();
				listener.parsedGSAMessage(message);
			}
		}
		else {
			throw new IllegalArgumentException("The given NMEA message is not recognized by the parser.");
		}
	}
	private GLLMessage parseGLLMessage(TalkerID talkerID, String[] tokens){
		if (tokens.length!=7){
			throw new IllegalArgumentException("The given GLL message is not valid.");
		}
		Latitude latitude = new Latitude(normalizeLatitudeLongitude(Double.parseDouble(tokens[1])), NorthSouthIndicator.parse(tokens[2]));
		Longitude longitude = new Longitude(normalizeLatitudeLongitude(Double.parseDouble(tokens[3])), EastWestIndicator.parse(tokens[4]));
		double time = Double.parseDouble(tokens[5]);
		QualityIndicator qualityIndicator = QualityIndicator.parse(tokens[6]);
		return new GLLMessage(talkerID,time,latitude,longitude,qualityIndicator);
		
		
	}

	private GGAMessage parseGGAMessage(TalkerID talkerID, String[] tokens) {
		if(tokens.length != 15) {
			throw new IllegalArgumentException("The given GGA message is not valid.");
		}
		double time = Double.parseDouble(tokens[1]);
		Latitude latitude = new Latitude(normalizeLatitudeLongitude(Double.parseDouble(tokens[2])), NorthSouthIndicator.parse(tokens[3]));
		Longitude longitude = new Longitude(normalizeLatitudeLongitude(Double.parseDouble(tokens[4])), EastWestIndicator.parse(tokens[5]));
		QualityIndicator qualityIndicator = QualityIndicator.parse(tokens[6]);
		int sattelitesInView = Integer.parseInt(tokens[7]);
		double horizontalDilutionOfPrecision = Double.parseDouble(tokens[8]);
		Altitude altitude = new Altitude(Double.parseDouble(tokens[9]), Units.parse(tokens[10]));
		GeodialSeparation geodialSeparation = null;
		if(tokens[11].length() != 0) {
			geodialSeparation = new GeodialSeparation(Double.parseDouble(tokens[11]), Units.parse(tokens[12]));
		}
		double differencialCorrectionAge = -1.0;
		if(tokens[13].length() != 0) {
			differencialCorrectionAge = Double.parseDouble(tokens[13]);
		}
		int differentialReferenceStationID = Integer.parseInt(tokens[14]);
		return new GGAMessage(
			talkerID,
			time,
			latitude,
			longitude,
			qualityIndicator,
			sattelitesInView,
			horizontalDilutionOfPrecision,
			altitude,
			differentialReferenceStationID,
			geodialSeparation,
			differencialCorrectionAge
		);
	}

	private GSAMessage parseGSAMessage(TalkerID talkerID, String[] tokens) {
		return null;
//		throw new IllegalArgumentException("parsing of GSA messages is not yet implemented.");
	}

	private GSVMessage parseGSVMessage(TalkerID talkerID, String[] tokens) {
		return null;
//		throw new IllegalArgumentException("parsing of GSV messages is not yet implemented.");
	}

	private RMCMessage parseRMCMessage(TalkerID talkerID, String[] tokens) {
		return null;
//		throw new IllegalArgumentException("parsing of RMC messages is not yet implemented.");
	}

	private String[] getTokens(StringTokenizer tokenizer) {
		List tokenList = new ArrayList();
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(token.equals(",")) {
				tokenList.add("");
			}
			else {
				tokenList.add(token);
				if(tokenizer.hasMoreTokens()) {
					tokenizer.nextToken();
				}
			}
		}
		return (String[])tokenList.toArray(new String[tokenList.size()]);
	}
	
	private double normalizeLatitudeLongitude(double c) {
		double deg = Math.floor( c * 1e-2 );
		double min = c - deg * 1e2;
		return deg + min / 60.0;
	}

}
