package de.uni_trier.jane.service.positioning.gps.nmea_parser.enums;

public class TalkerID {

	public static final TalkerID GPS_DEVICE = new TalkerID("GPS_DEVICE");

	private String name;

	private TalkerID(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public static TalkerID parse(String token) {
		if(token.equals("GP")) {
			return GPS_DEVICE;
		}
		else {
			throw new IllegalArgumentException("The given token is no talker id.");
		}
	}


}
