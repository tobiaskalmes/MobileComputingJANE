package de.uni_trier.jane.tools.pathneteditor.constants;

/**
 * @author salewski
 *
 * Corresponding dtd-files: PathNet.dtd, RoomCollection.dtd
 */
public interface XMLConstants {
	/*
	 * The xml tags
	 */
	final public static String	XML_ROOT			=	"PATHNET";
	final public static String	XML_ROOT_V2			=	"PATHNET_V2";
	final public static String	XML_TARGET			=	"DEST";
	final public static String	XML_WAYPOINT		=	"CROSS";
	final public static String	XML_EDGE			=	"PATH";
	final public static String	XML_ROUTING			=	"ROUTING";
	final public static String	XML_INNER_POINT		=	"INNER";
	final public static String	XML_WAYPOINT_PROB	=	"BRANCH";
	
	final public static String	XML_AREA_ROOT		=	"ROOMCOLLECTION";
	final public static String	XML_AREA_ROOT_V2	=	"ROOMCOLLECTION_V2";
	final public static String	XML_AREA			= 	"ROOM";
	final public static String	XML_ENTRY			=	"DOOR";
	
	/*
	 * The xml attributes
	 */
	final public static String	XML_ATT_NAME		=	"NAME";
	final public static String	XML_ATT_POS_X		=	"X";
	final public static String	XML_ATT_POS_Y		=	"Y";
	final public static String	XML_ATT_WIDTH		=	"W";
	final public static String	XML_ATT_EDGE_SOURCE	=	"FIRST";
	final public static String	XML_ATT_EDGE_TARGET	=	"LAST";
	final public static String	XML_ATT_TARGET		=	XML_TARGET;
	final public static String	XML_ATT_PROB		=	"PROB";
	final public static String	XML_ATT_EDGE		=	XML_EDGE;
	
	final public static String	XML_ATT_AREA_WIDTH	=	"WIDTH";
	final public static String	XML_ATT_AREA_HEIGHT	=	"HEIGHT";
	final public static String	XML_ATT_AREA_DEPTH	=	"DEPTH";
	final public static String	XML_ATT_POS_Z		=	"Z";
	
	/*
	 * Not used, but usefull attributes
	 */
	final public static String	XML_ATT_DESCR		=	"DESCRIPTION";
}
