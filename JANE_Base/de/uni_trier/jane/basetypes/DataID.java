package de.uni_trier.jane.basetypes;

import de.uni_trier.jane.basetypes.*;

import java.io.*;

/**
 * A data ID is used in order to uniquely identify the data object stored
 * in a data collection of a beaconing message. Each data ID implementation
 * has to override the equals and has code methods.
 */
public abstract class DataID extends ID implements Serializable {

}
