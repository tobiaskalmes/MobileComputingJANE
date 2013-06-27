package de.uni_trier.jane.service.positioning.gps.nmea_parser.enums;

public class Units {

	public static final Units METERS = new Units("METERS");

	private String name;
	
	private Units(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public static Units parse(String token) {
		if(token.equals("M")) {
			return METERS;
		}
		else {
			throw new IllegalArgumentException("The given token is no unit.");
		}
	}
	
}
