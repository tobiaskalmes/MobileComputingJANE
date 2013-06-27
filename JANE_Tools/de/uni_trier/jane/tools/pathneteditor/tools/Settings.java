package de.uni_trier.jane.tools.pathneteditor.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;



public class Settings implements PathNetConstants {
	
	/**
	 * zooms in/out to/from the center of the screen
	 */
	public static final int MOUSE_ZOOM_MIDDLE = 0;
	
	/**
	 * zooms in/out/ to/from the mouse position
	 */
	public static final int MOUSE_ZOOM_POSITION = 1;

	public static int mouse_zoom = MOUSE_ZOOM_POSITION;
	public static boolean use_antialiasing = true;
	
//	public static boolean draw_grid = true;
//	public static boolean draw_zero_lines = true;
//	public static boolean draw_coordinate_system = true;
//	public static boolean draw_meter_ruler = true;
//	public static boolean auto_zoom_on_load = true;
//	public static boolean draw_mouse_meter = true;
//	public static boolean draw_zero_source = true;
//	public static boolean draw_overview_map = true;
	
	
	public static final File CONFIG_FILE = new File(System.getProperty("user.home") + File.separator + ".PathNetEditor_Config");
	
	private static final Properties		SETTINGS = new Properties() {
		private static final long serialVersionUID = 1L;

		public synchronized Object put(Object key, Object value) {
			String newValue = value.toString();
			
			if (value instanceof Color) {
				Color c = (Color)value;
				newValue = c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "," + c.getAlpha();
			}
			
			else if (value instanceof Integer) {
				Integer i = (Integer)value;
				newValue = "" + i.intValue();
			}
			
			else if (value instanceof Double) {
				Double d = (Double)value;
				newValue = "" + d.doubleValue();
			}
			
			else if (value instanceof Font) {
				Font f = (Font)value;
				newValue = f.getFontName() + "," + f.getStyle() + "," + f.getSize();
			}
			
			else if (value instanceof String) {
				// do nothing
			}
			
			else if (value instanceof Point) {
				Point p = (Point)value;
				newValue = p.x + "," + p.y;
			}
			
			else if (value instanceof Boolean) {
				Boolean b = (Boolean)value;
				newValue = "" + (b.booleanValue() ? 1 : 0);
			}
			
			return super.put(key, newValue);
		}		
	};
	
	static {
		loadSettings();
	}
	
	public static void loadSettings() {
		setDefaultValues();
				
		try {
			SETTINGS.load(new FileInputStream(CONFIG_FILE));
			System.out.println("Found settings in file: " + CONFIG_FILE.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Unable to find config file in: " + CONFIG_FILE.getAbsolutePath());
			return;
		}
		
	}
	
	public static void saveSettings() {
		System.out.println("Found settings in file: " + CONFIG_FILE.getAbsolutePath() + ", loading...");
		
		try {
			SETTINGS.store(new FileOutputStream(CONFIG_FILE), "PATHNETEDITOR_CONFIG_"+ new Date());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		}
	}
	
	private static void createTemplateSettingsFile() {
		System.out.println("Settings not found, creating new template file");
		
		saveSettings();		
	}
	
	private static void setDefaultValues() {
		 SETTINGS.put(DEFAULT_ZOOM, new Integer(100));
		 SETTINGS.put(DEFAULT_MAX_ZOOM, new Integer(10000));
		 SETTINGS.put(DEFAULT_SCROLL_EXTEND, new Integer(10));
		 SETTINGS.put(DEFAULT_OFFSET_POINT, new Point(0, 0));
		 
		 SETTINGS.put(DEFAULT_SELECTION_OVERHEAD, new Integer(10));		// real coordinates
		 SETTINGS.put(SELECTION_TOLERANCE, new Integer(1000));				// real coordinates
		 SETTINGS.put(DEFAULT_TARGET_SIZE, new Integer(1000));				// real coordinates
		 SETTINGS.put(DEFAULT_WAYPOINT_SIZE, new Integer(1000));
		 SETTINGS.put(MAX_SYMBOL_SIZE, new Integer(100000));
		 SETTINGS.put(DEFAULT_VERTICES_SIZE, new Integer(5000));			// real coordinates
		
		 SETTINGS.put(DEFAULT_LINE_COLOR,  Color.BLACK);
		 SETTINGS.put(DEFAULT_FILL_COLOR,  Color.BLUE);
		 SETTINGS.put(DEFAULT_SELECTION_COLOR,  new Color(102, 0, 102, 255));
		 SETTINGS.put(DEFAULT_WAYPOINT_COLOR,  Color.BLUE);
		 SETTINGS.put(DEFAULT_TARGET_COLOR,  Color.BLACK);
		 SETTINGS.put(DEFAULT_EDGE_COLOR,  Color.RED);
		 SETTINGS.put(DEFAULT_INNER_POINT_COLOR,  new Color(100, 0, 0));
		 SETTINGS.put(DEFAULT_INNER_POINT_SIZE, new Double(1000.0));
		 SETTINGS.put(DEFAULT_AREA_COLOR,  new Color(0, 210, 0));
		 SETTINGS.put(DEFAULT_AREA_TARGET_COLOR,  getColor(DEFAULT_TARGET_COLOR).brighter());
		
		 SETTINGS.put(DEFAULT_DRAG_ALPHA, new Integer(50));	
		
		 SETTINGS.put(TABLE_HEADER_BACKGROUND,  Color.BLUE);
		 SETTINGS.put(TABLE_HEADER_FOREGROUND,  Color.WHITE);
		 SETTINGS.put(TABLE_SELECTED_FOREGROUND,  Color.WHITE);
		 SETTINGS.put(TABLE_SELECTED_BACKGROUND,  new Color(255, 200, 0, 160));
		 SETTINGS.put(TABLE_DEFAULT_BACKGROUND,  Color.WHITE);
		 SETTINGS.put(TABLE_DEFAULT_FOREGROUND,  Color.DARK_GRAY);
		 
		 SETTINGS.put(MESSAGES_DEFAULT_OUT_COLOR,  Color.GREEN.darker());
		 SETTINGS.put(MESSAGES_DEFAULT_ERR_COLOR,  Color.RED.brighter());
		 SETTINGS.put(DEFAULT_MAP_SHADOW_COLOR,  new Color(90,170,235,50));
		 SETTINGS.put(DEFAULT_MAP_COLOR,  new Color(110,190,255,50));
		
		 SETTINGS.put(COORIDINATE_SYSTEM_COLOR,  new Color(170, 170, 170));	
		 SETTINGS.put(GRID_COLOR,  new Color(240,240,240));
		 SETTINGS.put(RULER_COLOR,  new Color(100,100,100));
		 
		 SETTINGS.put(ZERO_LINES_COLOR,  Color.BLACK);		 
		 SETTINGS.put(ZERO_LINES_FONT,  new Font("Dialog", Font.PLAIN, 8));
		
		 SETTINGS.put(INNER_POINT_RELATION_TO_EDGE, new Double(1.1));					// 110 % of edge width for inner points
		 
		 SETTINGS.put(SHOW_EDGE_LABELS, new Boolean(false));
		 SETTINGS.put(SHOW_WAYPOINT_LABELS, new Boolean(false));
		 SETTINGS.put(SHOW_TARGET_LABELS, new Boolean(false));
		 SETTINGS.put(SHOW_AREA_LABELS, new Boolean(false));
		 SETTINGS.put(LABEL_COLOR, new Color(80,80,80,150));
		 SETTINGS.put(LABEL_FONT, new Font("Dialog", Font.PLAIN, 10));
		 SETTINGS.put(FONT, new Font("Dialog", Font.PLAIN, 10));
		 
		 SETTINGS.put(DTD_PATH, new File(System.getProperty("user.dir")+File.separator+"/dtds/").toURI().toString());
		 
		 SETTINGS.put(DRAW_COORDINATE_SYSTEM, new Boolean(true));
		 SETTINGS.put(DRAW_GRID, new Boolean(true));
		 SETTINGS.put(DRAW_METER_RULE, new Boolean(true));
		 SETTINGS.put(DRAW_MOUSEMETER, new Boolean(true));
		 SETTINGS.put(DRAW_OVERVIEW_MAP, new Boolean(true));
		 SETTINGS.put(DRAW_ZERO_LINES, new Boolean(true));
		 SETTINGS.put(DRAW_ZERO_SOURCE, new Boolean(true));
		 
		 SETTINGS.put(AUTO_ZOOM_ON_LOAD, new Boolean(true));
         SETTINGS.put(DEFAULT_PATH,System.getProperty("user.dir"));
	}
	
	public static Color getColor(String key) {
		String obj = SETTINGS.getProperty(key);
		
		if (obj==null)
			throw new RuntimeException("No settings value found for key: " + key);
		
		Color c = null;
		
		try {
			StringTokenizer st = new StringTokenizer(obj, ",");
			int r = Integer.parseInt(st.nextToken());
			int g = Integer.parseInt(st.nextToken());
			int b = Integer.parseInt(st.nextToken());
			int a = Integer.parseInt(st.nextToken());
			c = new Color(r, g, b, a);
		} catch (Exception e) {
			throw new RuntimeException("Settings value with key: " + key + " is NOT a color");
		}
		
		return c;
	}
	
	public static int getInt(String key) {
		String obj = SETTINGS.getProperty(key);
		
		if (obj==null)
			throw new RuntimeException("No settings value found for key: " + key);
		
		int i = 0;
		
		try {
			i = Integer.parseInt(obj);
		} catch(Exception e) {
			throw new RuntimeException("Settings value with key: " + key + " is NOT an integer");
		}
		
		return i;
	}
	
	public static double getDouble(String key) {
		String obj = SETTINGS.getProperty(key);
		
		if (obj==null)
			throw new RuntimeException("No settings value found for key: " + key);
		
		double d = 0.0;
		
		try {
			d = Double.parseDouble(obj);
		} catch(Exception e) {
			throw new RuntimeException("Settings value with key: " + key + " is NOT a double");
		}
		
		return d;
	}
	
	public static String getString(String key) {
		String obj = SETTINGS.getProperty(key);
		
		if (obj==null)
			throw new RuntimeException("No settings value found for key: " + key);
				
		return obj;
	}
	
	public static Font getFont(String key) {
		String obj = SETTINGS.getProperty(key);
		
		if (obj==null)
			throw new RuntimeException("No settings value found for key: " + key);
		
		Font f = null;
		
		try {
			StringTokenizer st = new StringTokenizer(obj, ",");
			String fName = st.nextToken();
			int fStyle = Integer.parseInt(st.nextToken());
			int fSize = Integer.parseInt(st.nextToken());
			f = new Font(fName, fStyle, fSize);
		} catch (Exception e) {
			throw new RuntimeException("Settings value with key: " + key + " is NOT a font");
		}
		
		return f;
	}
	
	public static Point getPoint(String key) {
		String obj = SETTINGS.getProperty(key);
		
		if (obj==null)
			throw new RuntimeException("No settings value found for key: " + key);
		
		Point p = null;
		
		try {
			StringTokenizer st = new StringTokenizer(obj, ",");			
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			p = new Point(x, y);
		} catch (Exception e) {
			throw new RuntimeException("Settings value with key: " + key + " is NOT a point");
		}
		
		return p;
	}
	
	public static boolean getBoolean(String key) {
		String obj = SETTINGS.getProperty(key);
		
		if (obj==null)
			throw new RuntimeException("No settings value found for key: " + key);
						
		int bool_val = -1;
		
		try {						
			bool_val = Integer.parseInt(obj);
			if (bool_val != 0 && bool_val != 1)
				throw new RuntimeException();
		} catch (Exception e) {
			throw new RuntimeException("Settings value with key: " + key + " is NOT a boolean");
		}
		
		return bool_val != 0;
	}
	
	public static void set(String key, Object obj) {
		if (!SETTINGS.containsKey(key))
			throw new RuntimeException("Invalid settings key: " + key);
		
		SETTINGS.put(key, obj);
	}
}
