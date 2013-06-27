package de.uni_trier.jane.service.positioning.gps.nmea_parser.message;

import de.uni_trier.jane.service.positioning.gps.nmea_parser.enums.*;
import de.uni_trier.jane.service.positioning.gps.nmea_parser.types.*;

public class GGAMessage {

	// mandatory fields
	private TalkerID talkerID;
	private double time;
	private Latitude latitude;
	private Longitude longitude;
	private QualityIndicator qualityIndicator;
	private int sattelitesInView;
	private double horizontalDilutionOfPrecision;
	private Altitude altitude;
	private int differentialReferenceStationID;

	// optional fields
	private GeodialSeparation geodialSeparation;
	private double differencialCorrectionAge;

	public GGAMessage(
		TalkerID talkerID,
		double time,
		Latitude latitude,
		Longitude longitude,
		QualityIndicator qualityIndicator,
		int sattelitesInView,
		double horizontalDilutionOfPrecision,
		Altitude altitude,
		int differentialReferenceStationID,
		GeodialSeparation geodialSeparation,
		double differencialCorrectionAge) {
		this.talkerID = talkerID;
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
		this.qualityIndicator = qualityIndicator;
		this.sattelitesInView = sattelitesInView;
		this.horizontalDilutionOfPrecision = horizontalDilutionOfPrecision;
		this.altitude = altitude;
		this.differentialReferenceStationID = differentialReferenceStationID;
		this.geodialSeparation = geodialSeparation;
		this.differencialCorrectionAge = differencialCorrectionAge;
	}

	public TalkerID getTalkerID() {
		return talkerID;
	}

	public Altitude getAltitude() {
		return altitude;
	}

	public int getDifferentialReferenceStationID() {
		return differentialReferenceStationID;
	}

	public double getHorizontalDilutionOfPrecision() {
		return horizontalDilutionOfPrecision;
	}

	public Latitude getLatitude() {
		return latitude;
	}

	public Longitude getLongitude() {
		return longitude;
	}

	public QualityIndicator getQualityIndicator() {
		return qualityIndicator;
	}

	public int getSattelitesInView() {
		return sattelitesInView;
	}

	public double getTime() {
		return time;
	}

	public boolean hasDifferencialCorrectionAge() {
		return differencialCorrectionAge >= 0;
	}

	public double getDifferencialCorrectionAge() {
		if(!hasDifferencialCorrectionAge()) {
			throw new IllegalStateException("the differencial correction age is unknown.");
		}
		return differencialCorrectionAge;
	}

	public boolean hasGeodialSeparation() {
		return geodialSeparation != null;
	}

	public GeodialSeparation getGeodialSeparation() {
		if(!hasGeodialSeparation()) {
			throw new IllegalStateException("the geodial sparation is unknown.");
		}
		return geodialSeparation;
	}

}
