/*
 * Created on 31.03.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.service.positioning.gps.nmea_parser.message;

import de.uni_trier.jane.service.positioning.gps.nmea_parser.enums.QualityIndicator;
import de.uni_trier.jane.service.positioning.gps.nmea_parser.enums.TalkerID;
import de.uni_trier.jane.service.positioning.gps.nmea_parser.types.Latitude;
import de.uni_trier.jane.service.positioning.gps.nmea_parser.types.Longitude;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GLLMessage {
	
	private TalkerID talkerID;
	private double time;
	private Latitude latitude;
	private Longitude longitude;
	private QualityIndicator qualityIndicator;

	public GLLMessage(
		TalkerID talkerID,
		double time,
		Latitude latitude,
		Longitude longitude,
		QualityIndicator qualityIndicator) {
		this.talkerID = talkerID;
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
		this.qualityIndicator = qualityIndicator;
	}

	/**
	 * @return Latitude
	 */
	public Latitude getLatitude() {
		return latitude;
	}

	/**
	 * @return Longitude
	 */
	public Longitude getLongitude() {
		return longitude;
	}

	/**
	 * @return QualityIndicator
	 */
	public QualityIndicator getQualityIndicator() {
		return qualityIndicator;
	}

	/**
	 * @return TalkerID
	 */
	public TalkerID getTalkerID() {
		return talkerID;
	}

	/**
	 * @return double
	 */
	public double getTime() {
		return time;
	}

}
