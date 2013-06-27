package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.model.*;
import de.uni_trier.jane.tools.pathneteditor.objects.*;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


/**
 * the panel to diplay the graph
 */
public class GraphicsPanel extends JPanel {

	static final long serialVersionUID = -5207704984452090404L;	
	
	private MousePositionDisplay mouse_position_display = new MousePositionDisplay();
	private BufferedImage buffer = null;
	private PathNetModel model = null;
	private Point offset = Settings.getPoint(PathNetConstants.DEFAULT_OFFSET_POINT);
	private int zoom = Settings.getInt(PathNetConstants.DEFAULT_ZOOM);
	private Map map = new Map();
	private boolean dragging = false;
	private int last_drag_x = -1;
	private int last_drag_y = -1;
	
	private double drag_x_sum = 0.0; // to memorize how much was dragged (int gets cut off)
	private double drag_y_sum = 0.0; // to memorize how much was dragged (int gets cut off)
	
	// this panel provides the border and other gui features for this panel
	private JPanel	main = new JPanel() {
		private static final long serialVersionUID = 1L;
		
		/**
		 * draws the buffer on screen graphics
		 * @param g
		 */
		public void paint(Graphics g) {
			super.paint(g);
			
			if (buffer!=null) {
				g.drawImage(buffer, 0, 0, null);
			}
			
			/*
			 * paint temporary objects
			 */
			if (!temporaryShapes.isEmpty()) {
				Graphics2D g2d = (Graphics2D)g;
			
				AffineTransform orig_trans = g2d.getTransform();		
				g2d.transform(PathNetTools.getAffineTransform(getZoom(), offset.x, offset.y));
				
				for (int i=0; i<temporaryShapes.size(); i++)
					((ColoredShape)(temporaryShapes.get(i))).paint(g2d);
				
				g2d.setTransform(orig_trans);
			}
			
			/*
			 * draw coordinates
			 */
			if (showMouseMeter)
				mouse_position_display.paint(g);
		}		
	};
	
	// gui options
	private boolean showGrid				= Settings.getBoolean(PathNetConstants.DRAW_GRID);
	private boolean showCoordinateSystem	= Settings.getBoolean(PathNetConstants.DRAW_COORDINATE_SYSTEM);
	private boolean showMeterRule			= Settings.getBoolean(PathNetConstants.DRAW_METER_RULE);
	private boolean showZeroLines			= Settings.getBoolean(PathNetConstants.DRAW_ZERO_LINES);
	private boolean autoZoom				= Settings.getBoolean(PathNetConstants.AUTO_ZOOM_ON_LOAD);
	private boolean showMouseMeter			= Settings.getBoolean(PathNetConstants.DRAW_MOUSEMETER);
	private boolean showZeroSource			= Settings.getBoolean(PathNetConstants.DRAW_ZERO_SOURCE);
	private boolean showOverviewMap			= Settings.getBoolean(PathNetConstants.DRAW_OVERVIEW_MAP);
	
	private boolean	showEdgeLabels			= Settings.getBoolean(PathNetConstants.SHOW_EDGE_LABELS);
	private boolean	showAreaLabels			= Settings.getBoolean(PathNetConstants.SHOW_TARGET_LABELS);
	private boolean	showTargetLabels		= Settings.getBoolean(PathNetConstants.SHOW_AREA_LABELS);
	private boolean	showWaypointLabels		= Settings.getBoolean(PathNetConstants.SHOW_WAYPOINT_LABELS);
	
	
	
	/*
	 * holds the temporary shapes
	 */
	private Vector temporaryShapes = new Vector();
	private class ColoredShape {
		private Shape s;
		private Color c;
		private boolean filled;
		public ColoredShape(Shape s, Color c, boolean filled) {
			if (s==null || c==null)
				throw new RuntimeException("NULL");
			this.s = s; this.c = c; this.filled = filled; }
		public void paint(Graphics2D g) {
			g.setColor(c);
			if (filled) {
				g.fill(s);
			} else {
				g.draw(s);
			}
		}
	}
	
	public GraphicsPanel(PathNetModel model) {
		if (model==null)
			throw new NullPointerException("ERROR: cannot create GraphicsPanel, model must be NOT NULL!");
		
		final PathNetModel myModel = model;
		this.addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent evt) {
				int amount = evt.getScrollAmount()/MouseWheelEvent.WHEEL_BLOCK_SCROLL;
				int new_zoom = zoom + (evt.getWheelRotation()>0?1:-1/*amount:-amount*/);
				if (new_zoom==0)
					new_zoom = evt.getWheelRotation()>0?2:-2;
				switch(Settings.mouse_zoom) {
					case Settings.MOUSE_ZOOM_POSITION:
						setZoom(new_zoom, evt.getX(), evt.getY());
						break;
					//case Settings.MOUSE_ZOOM_MIDDLE:
					default:
						setZoom(new_zoom>=1?new_zoom:1);
						break;
				}
			}});
		this.addMouseMotionListener(new MouseMotionListener() {
			PathNetObject old_object = null;
						
//			public void mouseDragged(MouseEvent e) {
//				if (dragging) {
//					if (last_drag_x==-1) {
//						last_drag_x = e.getX();
//						last_drag_y = e.getY();
//						return;	
//					}
//					int diff_x = PathNetTools.virtualToReal(e.getX()-last_drag_x, getZoom());
//					int diff_y = PathNetTools.virtualToReal(e.getY()-last_drag_y, getZoom());
//					last_drag_x = e.getX();
//					last_drag_y = e.getY();
//					setOffset(offset.x - diff_x, offset.y - diff_y);
//				}
//				
//			}
			public void mouseDragged(MouseEvent e) {
				if (dragging) {
					if (last_drag_x==-1) {
						last_drag_x = e.getX();
						last_drag_y = e.getY();
						return;	
					}
					// 'calculate' difference between current and last mouse position
					int diff_x = e.getX()-last_drag_x;
					int diff_y = e.getY()-last_drag_y;
					
					// transform difference in map coordinates (as 'exact' doubles)
					drag_x_sum += PathNetTools.exactVirtualToReal(diff_x, getZoom());
					drag_y_sum += PathNetTools.exactVirtualToReal(diff_y, getZoom());
					
					// remember position for next time...
					last_drag_x = e.getX();
					last_drag_y = e.getY();
					
					// calculate the amount the offset can be moved (whole numbers)
					int move_x = (int)drag_x_sum;
					int move_y = (int)drag_y_sum;
					drag_x_sum -= move_x;
					drag_y_sum -= move_y;
					
					setOffset(offset.x - move_x, offset.y - move_y);
				}
				
			}
			public void mouseMoved(MouseEvent e) {				
				Point vp = PathNetTools.virtualToReal(e.getPoint(), getZoom());
				Point p = new Point(vp.x+offset.x, vp.y+offset.y);
				
				// Test if we are at the same object
				if (old_object != null && old_object.containsPoint(p))
					return;
				
				// We have left the object: new tooltip text
				PathNetObject new_obj = PathNetTools.getObjectAtRealPoint(myModel, p);
				GraphicsPanel.this.setToolTipText((new_obj==null?null:new_obj.getTooltipText(myModel)));
				
				// remember old object
				old_object = new_obj;
			}
		});
						
		addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}

			public void mousePressed(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON2) { // middle mouse button -> move offset
					dragging = true;
				}
			}

			public void mouseReleased(MouseEvent e) {
				dragging = false;
				last_drag_x = -1;
				last_drag_y = -1;
				
			}});
		this.model = model;		
		this.model.addPathNetListener(new ModelListener());
		this.model.getSelectionModel().addSelectionListener(new SelectionListener() {
			public void selectionChanged(SelectionEvent e) {
				update();
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				resizeBuffer();
				update();
			}
		});
		
		// adding the main JPanel with the overwritten paint method
		this.setLayout(new BorderLayout());
		this.add(main, BorderLayout.CENTER);
		this.setBackground(Color.WHITE);
	}
		
	/**
	 * call this method to update the buffer
	 */
	public void update() {
//		System.out.println("panel: update " + new Date());
		render();
		repaint();
	}

	/**
	 * call this method to add a temporary object to draw to the panel
	 */
	public void addTemporaryShape(Shape s, Color c, boolean filled, boolean doRepaint) {
		if (s==null)
			return;
		temporaryShapes.add(new ColoredShape(s, c, filled));
		if (doRepaint) repaint();
	}
	
	/**
	 * This will clear the temporary objects
	 */
	public void clearTemporaryShapes(boolean doRepaint) {
		temporaryShapes.clear();
		if (doRepaint) repaint();
	}
	
	/**
	 * renders the content on a buffer
	 */
	private void render() {
		createBuffer();
		
		if (buffer == null)
			return;
		
		Graphics2D g = (Graphics2D) buffer.getGraphics();
		Waypoint[] nodes = model.getTargetsAndWaypoints();
		Edge[] edges = model.getEdges();
		Area[] areas = model.getAreas();
		
		g.setColor(Color.white);
		g.fillRect(0,0,getWidth(), getHeight());		
		
		if (showGrid) drawGrid(g);
		
		// set up the affine transformation and AA (if activated)
		AffineTransform orig_trans = g.getTransform();		
		g.transform(PathNetTools.getAffineTransform(getZoom(), offset.x, offset.y));
		PathNetTools.setAAActivated(g, Settings.use_antialiasing);
		
		if (showZeroLines) drawZeroLines(g, orig_trans);
		
		// draw objects
		for (int i=0; areas!=null&&i<areas.length; i++)
			areas[i].paint(
					g,
					model.getSelectionModel().isSelected(areas[i])
							? PathNetConstants.DRAW_STYLE_SELECTED
							: PathNetConstants.DRAW_STYLE_UNSELECTED
			);
		for (int i=0; edges!=null&&i<edges.length; i++)
			edges[i].paint(
					g,
					model.getSelectionModel().isSelected(edges[i])
							? PathNetConstants.DRAW_STYLE_SELECTED
							: PathNetConstants.DRAW_STYLE_UNSELECTED
			);
		for (int i=0; nodes!=null&&i<nodes.length; i++)
			nodes[i].paint(
					g,
					model.getSelectionModel().isSelected(nodes[i])
							? PathNetConstants.DRAW_STYLE_SELECTED
							: PathNetConstants.DRAW_STYLE_UNSELECTED
			);
		
		// restore affine transformation
		PathNetTools.setAAActivated(g, false);
		g.setTransform(orig_trans);
		
		// draw edge labels
		if (showEdgeLabels)
			drawEdgeLabels(g, edges);
		
		if (showTargetLabels)
			drawWaypointLabels(g, model.getTargets());
		
		if (showWaypointLabels)
			drawWaypointLabels(g, model.getWaypoints());
		
		if (showAreaLabels)
			drawAreaLabels(g, areas);
		
		if (showCoordinateSystem) drawCoordinateSystem(g);
		if (showMeterRule) drawMeterRuler(g);
		
		// draw map
		if (showOverviewMap && (nodes!=null && nodes.length>0 || areas!=null && areas.length>0))
			map.paint(g);
		
//		g.drawString(zoom+"", 20,20);
	}
	
	private int min(int a, int b) {
		return a<b?a:b;
	}
	private int max(int a, int b) {
		return a>b?a:b;
	}
	
	private int getRulerLength(Graphics2D g) {
		// set amount to length
		int length = PathNetTools.realToVirtual(amount, getZoom());
		FontMetrics fm = g.getFontMetrics();
		
		while (length/10>fm.stringWidth((amount/10)+"")+(2*RULER_TEXT_SPACE_LEFT) && amount>1 && (amount>PREFERRED_AMOUNT || length>getWidth()-MIN_SPACE)) {
			amount=max(amount/10, 1);
			length = PathNetTools.realToVirtual(amount, getZoom());
		}
		while (length<fm.stringWidth(amount+UNIT_TEXT)) {
			amount*=10;
			length = PathNetTools.realToVirtual(amount, getZoom());
		}
		return length;
	}

	// for the meter ruler and the grid (getRulerLength method)
// 	private final Font text_font = new Font("Dialog", Font.PLAIN, 8);
	private final int RULER_SPACE_TOP = 27;
	private final int RULER_SPACE_LEFT = 37;
	private final int RULER_TEXT_SPACE_LEFT = 20;
	private final int RULER_TEXT_SPACE_TOP = 2;
	private final int PREFERRED_AMOUNT = 10;
	private final int MIN_SPACE = 100; // length + min_space must be smaller than width
	private final String UNIT_TEXT = "m";
	private int amount = PREFERRED_AMOUNT;

	private void drawEdgeLabels(Graphics2D g, Edge[] edges) {
		g.setFont(Settings.getFont(PathNetConstants.LABEL_FONT));
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Settings.getColor(PathNetConstants.LABEL_COLOR));
		for (int i=0; edges!=null&&i<edges.length; i++) {
			Point p = PathNetTools.realToVirtual(edges[i].getMiddle(), getZoom(), offset.x, offset.y);
			String label = edges[i].getID() + "[" + edges[i].getDescription() + "]";
			int width = fm.stringWidth(label);
			g.drawString(label, p.x-width/2, p.y+(int)(fm.getHeight()*.5));
		}
	}
	
	private void drawWaypointLabels(Graphics2D g, Waypoint[] waypoints) {
		g.setFont(Settings.getFont(PathNetConstants.LABEL_FONT));
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Settings.getColor(PathNetConstants.LABEL_COLOR));
		for (int i=0; waypoints!=null&&i<waypoints.length; i++) {
			Point p = PathNetTools.realToVirtual(waypoints[i].getPosition(), getZoom(), offset.x, offset.y);
			String label = waypoints[i].getID() + "[" + waypoints[i].getDescription() + "]";
			int width = fm.stringWidth(label);
			g.drawString(label, p.x-width/2, p.y-(int)(fm.getHeight()*.5));
		}
	}
	
	private void drawAreaLabels(Graphics2D g, Area[] areas) {
		g.setFont(Settings.getFont(PathNetConstants.LABEL_FONT));
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Settings.getColor(PathNetConstants.LABEL_COLOR));
		for (int i=0; areas!=null&&i<areas.length; i++) {
			Rectangle bounds = areas[i].getBounds();
			Point p = PathNetTools.realToVirtual(bounds.x+bounds.width/2, bounds.y+bounds.height/2, getZoom(), offset.x, offset.y);
			String label = areas[i].getID() + "[" + areas[i].getDescription() + "]";
			int width = fm.stringWidth(label);
			g.drawString(label, p.x-width/2, p.y+(int)(fm.getHeight()*.5));
		}
	}
	
	
	/**
	 * draws a ruler for meters (if zoom big enough)
	 * @param g
	 */
	private void drawMeterRuler(Graphics2D g) {
		
		Font font = g.getFont();
		g.setColor(Settings.getColor(PathNetConstants.RULER_COLOR));
		g.setFont(Settings.getFont(PathNetConstants.FONT));
		FontMetrics fm = g.getFontMetrics();
		double newAmount = amount/1000;
		int text_width = fm.stringWidth(newAmount + UNIT_TEXT);
		
		int length = getRulerLength(g);
		g.setColor(Settings.getColor(PathNetConstants.RULER_COLOR));
		text_width = fm.stringWidth(newAmount + UNIT_TEXT);
		g.drawLine(RULER_SPACE_LEFT,RULER_SPACE_TOP, RULER_SPACE_LEFT+length, RULER_SPACE_TOP);
		g.drawLine(RULER_SPACE_LEFT,RULER_SPACE_TOP, RULER_SPACE_LEFT, RULER_SPACE_TOP + 5);
		g.drawLine(RULER_SPACE_LEFT+length,RULER_SPACE_TOP, RULER_SPACE_LEFT+length, RULER_SPACE_TOP + 5);
		g.drawString(newAmount+UNIT_TEXT, RULER_SPACE_LEFT+((length+(2*RULER_TEXT_SPACE_LEFT)-text_width)/2)-RULER_TEXT_SPACE_LEFT, RULER_SPACE_TOP + RULER_TEXT_SPACE_TOP + g.getFontMetrics().getHeight());
		
		g.setFont(font);
	}

	private void drawGrid(Graphics2D g) {
		int length = getRulerLength(g);
		g.setColor(Settings.getColor(PathNetConstants.GRID_COLOR));
		int i=0;
		// draw vertical lines
		int x_offset = PathNetTools.realToVirtual(-offset.x, getZoom())%length;
		while (i*length<getWidth()) {
			g.drawLine(x_offset+(i*length), 0, x_offset+(i*length), getHeight());
			i++;
		}
		// draw horizontal lines
		i=0;
		int y_offset = PathNetTools.realToVirtual(-offset.y, getZoom())%length;
		while (i*length<getHeight()) {
			g.drawLine(0, y_offset+(i*length), getWidth(), y_offset+(i*length));
			i++;
		}
	}
	
	private final int ORIGIN_SPACE_LEFT = 5;
	private final int ORIGIN_SPACE_TOP = -1;
	
	private void drawZeroLines(Graphics2D g, AffineTransform no_transform) {
		g.setColor(Settings.getColor(PathNetConstants.ZERO_LINES_COLOR));

		Point upper_left = offset;
		Point bottom_right = PathNetTools.virtualToReal(getWidth(), getHeight(), getZoom(), offset.x, offset.y); 
		
		g.drawLine(0,upper_left.y,0,bottom_right.y);
		g.drawLine(upper_left.x, 0, bottom_right.x, 0);
		
//		g.setFont(PathNetConstants.ZERO_LINES_FONT);
				
		if (showZeroSource) {
			g.setFont(Settings.getFont(PathNetConstants.FONT));
			FontMetrics fm = g.getFontMetrics();
			
			AffineTransform current_transform = g.getTransform();
			g.setTransform(no_transform);
						
			Point o = PathNetTools.realToVirtual(0,0,getZoom(), offset.x, offset.y);
			g.drawString("0,0", 
					o.x+ORIGIN_SPACE_LEFT, 
					o.y+fm.getHeight()+ORIGIN_SPACE_TOP);
			
			g.setTransform(current_transform);
		}		
	}	

	private void drawCoordinateSystem(Graphics g) {
		g.setFont(Settings.getFont(PathNetConstants.FONT));
		g.setColor(Settings.getColor(PathNetConstants.COORIDINATE_SYSTEM_COLOR));
		int width = getWidth();		// width of the panel
		int height = getHeight();	// height of the panel
		int lines = 11;	// number of lines
		int length = 7;	// length of the lines
		int space = 2;	// between line and string
		FontMetrics fm = g.getFontMetrics();
		int strheight = fm.getHeight();
		for (int i=0; i<lines-1; i++) {
			//                 <     screen pos     >
			int coordinate_x = (int)((double)((i+1)*(width /lines))  *getZoom()) + offset.x;
			int coordinate_y = (int)((double)((i+1)*(height/lines)) *getZoom()) + offset.y;

			int strwidth = fm.stringWidth(coordinate_x+"");
			g.drawLine(	(i+1)*(width/lines),
						0,
						(i+1)*(width/lines),
						length
					);
			g.drawString(coordinate_x+"", (i+1)*(width/lines)-(strwidth/2), length+space+strheight);
			g.drawLine(	0,
						(i+1)*(height/lines),
						length,
						(i+1)*(height/lines)
					);
			strwidth = fm.stringWidth(coordinate_y+"");
			g.drawString(coordinate_y+"", length+space, (i+1)*(height/lines)+(strheight/2));
		}
	}
	
	/**
	 * creates a buffer if it doesn't exist yet.
	 */
	private void createBuffer() {
		if (buffer!=null)
			return;
		if (getWidth()!=0 && getHeight()!=0)
			buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		else
			buffer = null;
	}
	
	/**
	 * if the panel is resized -> resize buffer as well.
	 * (the buffer is set to null and then recreated if the render method is called)
	 */
	private void resizeBuffer() {
		buffer = null;
	}
	
	private class ModelListener extends PathNetModelAdapter {

		public void objectAdded(PathNetModelEvent event) {
			update();
		}

		public void objectDeleted(PathNetModelEvent event) {			
			update();
		}

		public void modelDataCleared(PathNetModelEvent e) {			
			update();
		}
		
		public void modelDataLoaded(PathNetModelEvent e) {
			update();
		}
		
		public void modelEventsReenabled(PathNetModelEvent e) {
			update();
		}
	}
	
	public void setZoom(int value, int x, int y) {
		int middle_x = offset.x + PathNetTools.virtualToReal(x, getZoom());
		int middle_y = offset.y + PathNetTools.virtualToReal(y, getZoom());

		int new_middle_x = offset.x + PathNetTools.virtualToReal(x, PathNetTools.getZoom(value));
		int new_middle_y = offset.y + PathNetTools.virtualToReal(y, PathNetTools.getZoom(value));
		
		int diff_x = middle_x - new_middle_x;
		int diff_y = middle_y - new_middle_y;
		
		int new_x = offset.x+diff_x;
		int new_y = offset.y+diff_y;
		offset.setLocation(new_x, new_y);

		this.zoom = value;
		update();
	}
	
	// FIXME: Why do we need the model as an argument here? This is missleading because its a class member at all
	public void zoomToPage(PathNetModel model) {
		PathNetObject[] objects = model.getAllObjects();
		if (objects==null || objects.length<2)
			return;
		Point space = PathNetTools.virtualToReal(new Point(5,5),getZoom());
		int max_x = PathNetTools.getMaxX(objects)+space.x;
		int max_y = PathNetTools.getMaxY(objects)+space.y;
		int min_x = PathNetTools.getMinX(objects)-space.x;
		int min_y = PathNetTools.getMinY(objects)-space.y;
		int diff_x = max_x - min_x;
		int diff_y = max_y - min_y;
		
		double factor_x = (double)diff_x / (double)getWidth();
		double factor_y = (double)diff_y / (double)getHeight();
		
		int new_zoom = (int)(Math.ceil(factor_x<factor_y?factor_y:factor_x));
				
		setZoom(new_zoom);
		setOffset(min_x, min_y);
	}
	
	public void setZoom(int value) {
		int middle_x = offset.x + PathNetTools.virtualToReal(getWidth()/2, getZoom());
		int middle_y = offset.y + PathNetTools.virtualToReal(getHeight()/2, getZoom());

		int new_middle_x = offset.x + PathNetTools.virtualToReal(getWidth()/2, value);
		int new_middle_y = offset.y + PathNetTools.virtualToReal(getHeight()/2, value);
		
		int diff_x = middle_x - new_middle_x;
		int diff_y = middle_y - new_middle_y;
		
		int new_x = offset.x+diff_x;
		int new_y = offset.y+diff_y;
		offset.setLocation(new_x, new_y);

		this.zoom = value;
		update();
	}
	
	/*
	 * Getter methods	 
	 */		
	public double getZoom() { return PathNetTools.getZoom(zoom); }
	public Point getOffset() { return offset; }
	
	public PathNetModel getPathnetModel() { return model; }
	
	/**
	 * use this method to set the offset in the sliders listener
	 */
	public void setOffset(int x, int y) {
		this.offset.setLocation(x, y);
		update();
	}
	
	public Rectangle getVisibleBounds() {
		Point upperleft = PathNetTools.getRealPoint(this, new Point(0,0));
		Point bottomright = PathNetTools.getRealPoint(this, new Point(getWidth(), getHeight()));
		Dimension d = new Dimension(bottomright.x, bottomright.y);
		return new Rectangle(upperleft, d);
	}

	public void showGrid(boolean showGrid) {
		if (this.showGrid == showGrid)
			return;
		
		this.showGrid = showGrid;
		update();
	}
	
	public void showCoordinateSystem(boolean showCoordinateSystem) {
		if (this.showCoordinateSystem == showCoordinateSystem)
			return;
		
		this.showCoordinateSystem = showCoordinateSystem;
		update();
	}
	
	public void showZeroLines(boolean showZeroLines) {
		if (this.showZeroLines == showZeroLines)
			return;
		
		this.showZeroLines = showZeroLines;
		update();
	}
	
	public void showMeterRule(boolean showMeterRule) {
		if (this.showMeterRule = showMeterRule)
			return;
		
		this.showMeterRule = showMeterRule;
		update();
	}
	
	public void showMouseMeter(boolean showMouseMeter) {
		if (this.showMouseMeter = showMouseMeter)
			return;
		
		this.showMouseMeter = showMouseMeter;
		update();
	}
	
	public void showZeroSource(boolean showZeroSource) {
		if (this.showZeroSource == showZeroSource)
			return;
		
		this.showZeroSource = showZeroSource;
		update();
	}
	
	public void showOverviewMap(boolean showOverviewMap) {
		if (this.showOverviewMap == showOverviewMap)
			return;
		
		this.showOverviewMap = showOverviewMap;
		update();
	}
	
	public void showEdgeLabels(boolean showLabels) {
		if (this.showEdgeLabels == showLabels)
			return;
		this.showEdgeLabels = showLabels;
		update();
	}
	
	public void showTargetLabels(boolean showLabels) {
		if (this.showTargetLabels == showLabels)
			return;
		this.showTargetLabels = showLabels;
		update();
	}
	
	public void showWaypointLabels(boolean showLabels) {
		if (this.showWaypointLabels == showLabels)
			return;
		this.showWaypointLabels = showLabels;
		update();
	}
	
	public void showAreaLabels(boolean showLabels) {
		if (this.showAreaLabels == showLabels)
			return;
		this.showAreaLabels = showLabels;
		update();
	}
		
	public void setAutoZoomOnLoad(boolean autoZoom) {		
		this.autoZoom = autoZoom;
	}
	
	private class Map {
		private Waypoint[] waypoints = null;
		private Target[] targets = null;
		
		public void paint(Graphics2D g) {
			waypoints = model.getWaypoints();
			targets = model.getTargets();
			Edge[] edges = model.getEdges();
			Waypoint[] nodes =  model.getTargetsAndWaypoints();
			Area[] areas = model.getAreas();
			PathNetObject[] objects = model.getAllObjects();

			// map rectangle
			int outer_space_right = 10;	// space between map border and panel border
			int outer_space_top = 10;
			int inner_space = 5;	// space between map border and map_graph
			int width = 100;
			int height = 100;
			int map_x = getWidth()-width-outer_space_right;
			int map_y = outer_space_top + mouse_position_display.getHeight();
			int shadow_x = 5;
			int shadow_y = 5;
			
			// draw map background
			g.setColor(Color.black);
			g.drawRect(map_x, map_y, width, height);
			g.setColor(Settings.getColor(PathNetConstants.DEFAULT_MAP_SHADOW_COLOR));
			g.fillRect(map_x, map_y, width, height);
			g.setColor(Settings.getColor(PathNetConstants.DEFAULT_MAP_COLOR));
			g.fillRect(map_x+shadow_x, map_y+shadow_y, width, height);
			
			// compute zoom and offset to draw graph on map
			int min_x = PathNetTools.getMinX(objects);
			int min_y = PathNetTools.getMinY(objects);
			int max_x = PathNetTools.getMaxX(objects);
			int max_y = PathNetTools.getMaxY(objects);
			
			int diff_x = max_x - min_x;
			int diff_y = max_y - min_y;
			
			double factor_x = ((double)diff_x) / (((double)width)-(2.0*((double)inner_space)));
			double factor_y = ((double)diff_y) / (((double)height)-(2.0*((double)inner_space)));
			
			double map_zoom = factor_x<factor_y?factor_y:factor_x;

			if (map_zoom==0) {
				map_zoom = (int)(getZoom()*(double)getWidth()/(double)width);
				System.out.println("MAPZOOM 0!");
			}
//			if (map_zoom<Settings.getInt(PathNetConstants.DEFAULT_ZOOM))
//				map_zoom = Settings.getInt(PathNetConstants.DEFAULT_ZOOM);
			
			int map_offset_x = (int)(-((double)map_x+(double)inner_space)*map_zoom+(double)min_x);
			int map_offset_y = (int)(-((double)map_y+(double)inner_space)*map_zoom+(double)min_y);
			
			// set up the affine transformation and AA (if activated)
			AffineTransform orig_trans = g.getTransform();		
			g.transform(PathNetTools.getAffineTransform(map_zoom, map_offset_x, map_offset_y));
			PathNetTools.setAAActivated(g, Settings.use_antialiasing);			

			// draw graph
			for (int i=0; areas!=null&&i<areas.length; i++)
				areas[i].paint(g, PathNetConstants.DRAW_STYLE_DEFAULT);
			for (int i=0; edges!=null&&i<edges.length; i++)
				edges[i].paint(g, PathNetConstants.DRAW_STYLE_DEFAULT);
			for (int i=0; nodes!=null&&i<nodes.length; i++)
				nodes[i].paint(g, PathNetConstants.DRAW_STYLE_DEFAULT);			
			
//			Rectangle r = getVisibleBounds();
//			g.setColor(Color.black);
//			g.drawRect(r.x, r.y, r.width, r.height);
			
			// restore affine transformation
			PathNetTools.setAAActivated(g, false);
			g.setTransform(orig_trans);

		}
		
		private int getMaxX() {
			int max = -1;
			for (int i=0; waypoints!=null&&i<waypoints.length; i++)
				if (waypoints[i].getPosition().x>max)
					max=waypoints[i].getPosition().x;
			for (int i=0; targets!=null && i<targets.length; i++)
				if (targets[i].getMaxX()>max)
					max=targets[i].getMaxX();
			return max;
		}
		private int getMaxY() {
			int max = -1;
			for (int i=0; waypoints!=null&&i<waypoints.length; i++)
				if (waypoints[i].getPosition().y>max)
					max=waypoints[i].getPosition().y;
			for (int i=0; targets!=null && i<targets.length; i++)
				if (targets[i].getMaxY()>max)
					max=targets[i].getMaxY();
			return max;
		}
		private int getMinX() {
			if ((waypoints==null||waypoints.length<1) && (targets==null||targets.length<1))
				return -1;
			int min = waypoints.length>0?waypoints[0].getPosition().x:targets[0].getMinX();
			for (int i=0; i<waypoints.length; i++)
				if (waypoints[i].getPosition().x<min)
					min = waypoints[i].getPosition().x;
			for (int i=0; targets!=null && i<targets.length; i++)
				if (targets[i].getMinX()<min)
					min = targets[i].getMinX();
			return min;
		}
		private int getMinY() {
			if ((waypoints==null||waypoints.length<1) && (targets==null||targets.length<1))
				return -1;
			int min = waypoints.length>0?waypoints[0].getPosition().y:targets[0].getMinY();
			for (int i=0; i<waypoints.length; i++)
				if (waypoints[i].getPosition().y<min)
					min = waypoints[i].getPosition().y;
			for (int i=0; targets!=null && i<targets.length; i++)
				if (targets[i].getMinY()<min)
					min = targets[i].getMinY();
			return min;
		}
	}
	
	private class MousePositionDisplay implements MouseMotionListener, MouseListener {

		private Point point = null;
		
		public MousePositionDisplay() {
			addMouseMotionListener(this);
			addMouseListener(this);
		}
		
		private void updateCoords(Point point) {
			if (point!=null)
				this.point = PathNetTools.getRealPoint(GraphicsPanel.this, point);
			else
				this.point = null;
			repaint();
		}
		
		public int getHeight() {
			return 50;
		}
		
		public void paint(Graphics g) {
			int x = getWidth()-10-100;
			int y = 30;
			int height = 20;
			int width = 100;
			int shadow_x = 5;
			int shadow_y = 5;
			
			// draw background
			g.setColor(Color.black);
			g.drawRect(x, y, width, height);
			g.setColor(Settings.getColor(PathNetConstants.DEFAULT_MAP_SHADOW_COLOR));
			g.fillRect(x, y, width, height);
			g.setColor(Settings.getColor(PathNetConstants.DEFAULT_MAP_COLOR));
			g.fillRect(x+shadow_x, y+shadow_y, width, height);

			
			if (point==null)
				return;
			
			String coords = point.x + "," + point.y;
			FontMetrics fm = g.getFontMetrics();
			
			g.setColor(Color.black);
			g.drawString(coords, getWidth()-13-fm.stringWidth(coords), y+fm.getHeight());
		}

		public void mouseDragged	(MouseEvent evt)		{ updateCoords(evt.getPoint()); }
		public void mouseMoved		(MouseEvent evt)		{ updateCoords(evt.getPoint()); }
		public void mouseExited		(MouseEvent evt)		{ updateCoords(null); }

		public void mouseClicked	(MouseEvent evt) 		{ }
		public void mousePressed	(MouseEvent evt)		{ }
		public void mouseReleased	(MouseEvent evt) 		{ }
		public void mouseEntered	(MouseEvent evt) 		{ }
	}
	
}
