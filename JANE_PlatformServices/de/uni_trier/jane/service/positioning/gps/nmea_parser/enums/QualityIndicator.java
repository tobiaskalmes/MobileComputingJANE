package de.uni_trier.jane.service.positioning.gps.nmea_parser.enums;

public class QualityIndicator {

	public static final QualityIndicator INVALID = new QualityIndicator("INVALID");
	public static final QualityIndicator GPS_SPS = new QualityIndicator("GPS_SPS");
	public static final QualityIndicator DGPS_SPS = new QualityIndicator("DGPS_SPS");
	public static final QualityIndicator GPS_PPS = new QualityIndicator("GPS_PPS");

	private String name;

	private QualityIndicator(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public static QualityIndicator parse(String token) {
		if(token.equals("0")||token.equalsIgnoreCase("v")) {
			return INVALID;
		}
		else if(token.equals("1")||token.equalsIgnoreCase("a")) {
			return GPS_SPS;
		}
		else if(token.equals("2")) {
			return DGPS_SPS;
		}
		else if(token.equals("3")) {
			return GPS_PPS;
		}
		else {
			throw new IllegalArgumentException("The given token is no QualityIndicator.");
		}
	}

}
