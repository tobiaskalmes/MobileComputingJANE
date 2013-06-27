package de.uni_trier.jane.tools.pathneteditor.constants;


public interface PathNetConstants {	
	// the object types
	final public static int		NONE		= 0;
	final public static int		TARGET		= 1;
	final public static int		WAYPOINT	= 2;
	final public static int		EDGE		= 3;
	final public static int		AREA		= 4;
	
	// the action types	
	final public static int		ACTION_SOURCE_CHANGED			=	 16;
	final public static int		ACTION_TARGET_CHANGED			=	 32;
	final public static int		ACTION_COLOR_CHANGED			=	 64;		
	public static final int 	ACTION_PROB_CHANGED 			= 	256;
	public static final int 	ACTION_POLYGON_CHANGED 			= 	512;
	final public static int		ACTION_DESCRIPTION_CHANGED		=  1024;
	final public static int		ACTION_POSITION_CHANGED			=  2048;
	final public static int		ACTION_ID_CHANGED				=  4096;
	final public static int		ACTION_TARGETS_CHANGED			=  8192;
	final public static int		ACTION_VERTICES_CHANGED			= 16384;
	final public static int		ACTION_SYMBOL_SIZE_CHANGED		= 32768;
	final public static int		ACTION_WIDTH_CHANGED			= 65536;
			
	// draw styles
	final public static int		DRAW_STYLE_DEFAULT			= -1;
	final public static int		DRAW_STYLE_SELECTED			= -2;
	final public static int		DRAW_STYLE_UNSELECTED		= DRAW_STYLE_DEFAULT;
	
	// SETTINGS constants --> change default settings values in class Settings
	public static final String 	DEFAULT_ZOOM				=	"DEFAULT_ZOOM";
	public static final String 	DEFAULT_MAX_ZOOM 			= 	"DEFAULT_MAX_ZOOM";
	public static final String 	DEFAULT_SCROLL_EXTEND 		= 	"DEFAULT_SCROLL_EXTEND";
	public static final String 	DEFAULT_OFFSET_POINT 		= 	"DEFAULT_OFFSET_POINT";
	
	public static final String	DEFAULT_SELECTION_OVERHEAD	= 	"DEFAULT_SELECTION_OVERHEAD";
	public static final String	SELECTION_TOLERANCE			= 	"SELECTION_TOLERANCE";
	public static final String 	DEFAULT_TARGET_SIZE			=	"DEFAULT_TARGET_SIZE";
	public static final String	DEFAULT_WAYPOINT_SIZE		=	"DEFAULT_WAYPOINT_SIZE";
	public static final String	DEFAULT_INNER_POINT_SIZE	=	"DEFAULT_INNER_POINT_SIZE";
	public static final String	MAX_SYMBOL_SIZE				=	"MAX_SYMBOL_SIZE";	
	public static final String	DEFAULT_VERTICES_SIZE		=	"DEFAULT_VERTICES_SIZE";
	
	public static final String DEFAULT_LINE_COLOR			=	"DEFAULT_LINE_COLOR";
	public static final String DEFAULT_FILL_COLOR			=	"DEFAULT_FILL_COLOR";
	public static final String DEFAULT_SELECTION_COLOR		=	"DEFAULT_SELECTION_COLOR";
	public static final String DEFAULT_WAYPOINT_COLOR		=	"DEFAULT_WAYPOINT_COLOR";
	public static final String DEFAULT_TARGET_COLOR			=	"DEFAULT_TARGET_COLOR";
	public static final String DEFAULT_EDGE_COLOR			=	"DEFAULT_EDGE_COLOR";
	public static final String DEFAULT_INNER_POINT_COLOR	=	"DEFAULT_INNER_POINT_COLOR";
	public static final String DEFAULT_AREA_COLOR			=	"DEFAULT_AREA_COLOR";
	public static final String DEFAULT_AREA_TARGET_COLOR	=	"DEFAULT_AREA_TARGET_COLOR";
	
	public static final String DEFAULT_DRAG_ALPHA			=	"DEFAULT_DRAG_ALPHA";	
	
	public static final String TABLE_HEADER_BACKGROUND		=	"TABLE_HEADER_BACKGROUND";
	public static final String TABLE_HEADER_FOREGROUND		= 	"TABLE_HEADER_FOREGROUND";
	public static final String TABLE_SELECTED_FOREGROUND	= 	"TABLE_SELECTED_FOREGROUND";
	public static final String TABLE_SELECTED_BACKGROUND	= 	"TABLE_SELECTED_BACKGROUND";
	public static final String TABLE_DEFAULT_BACKGROUND		= 	"TABLE_DEFAULT_BACKGROUND";
	public static final String TABLE_DEFAULT_FOREGROUND		= 	"TABLE_DEFAULT_FOREGROUND";
	
	public static final String MESSAGES_DEFAULT_OUT_COLOR	= 	"MESSAGES_DEFAULT_OUT_COLOR";
	public static final String MESSAGES_DEFAULT_ERR_COLOR	= 	"MESSAGES_DEFAULT_ERR_COLOR";
	public static final String DEFAULT_MAP_SHADOW_COLOR		= 	"DEFAULT_MAP_SHADOW_COLOR";
	public static final String DEFAULT_MAP_COLOR			= 	"DEFAULT_MAP_COLOR";
	
	public static final String COORIDINATE_SYSTEM_COLOR		=	"COORDINATE_SYSTEM_COLOR"; 
	public static final String GRID_COLOR					= 	"GRID_COLOR";
	public static final String RULER_COLOR					= 	"RULER_COLOR";
	public static final String ZERO_LINES_COLOR				=	"ZERO_LINES_COLOR";
		
	public static final String ZERO_LINES_FONT				=	"ZERO_LINES_FONT";
	
	public static final String INNER_POINT_RELATION_TO_EDGE =	"INNER_POINT_RELATION_TO_EDGE";
	
	final public static String DTD_PATH						=	"DTD_PATH";	
	
	final public static String SHOW_EDGE_LABELS				=	"SHOW_EDGE_LABELS";
	final public static String SHOW_TARGET_LABELS			=	"SHOW_TARGET_LABELS";
	final public static String SHOW_WAYPOINT_LABELS			=	"SHOW_WAYPOINT_LABELS";
	final public static String SHOW_AREA_LABELS				=	"SHOW_AREA_LABELS";
	
	final public static String LABEL_COLOR					= 	"LABEL_COLOR";
	final public static String LABEL_FONT					=	"LABEL_FONT";
	public static final String FONT 						= 	"FONT"; // font for ruler/coordinate system...
	
	final public static String DRAW_GRID					= 	"DRAW_GRID";
	final public static String DRAW_METER_RULE				= 	"DRAW_METER_RULE";
	final public static String DRAW_COORDINATE_SYSTEM		= 	"DRAW_COORDINATE_SYSTEM";
	final public static String DRAW_ZERO_LINES				= 	"DRAW_ZERO_LINES";
	final public static String DRAW_MOUSEMETER				= 	"DRAW_MOUSEMETER";
	final public static String DRAW_ZERO_SOURCE				= 	"DRAW_ZERO_SOURCE";
	final public static String DRAW_OVERVIEW_MAP			= 	"DRAW_OVERVIEW_MAP";
	
	final public static String AUTO_ZOOM_ON_LOAD			= 	"AUTO_ZOOM_ON_LOAD";
    final public static String DEFAULT_PATH            =   "DEFAULT_PATH";
}
