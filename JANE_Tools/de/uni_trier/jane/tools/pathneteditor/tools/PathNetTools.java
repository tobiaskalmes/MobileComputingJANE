package de.uni_trier.jane.tools.pathneteditor.tools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.BitSet;
import java.util.Vector;

import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.*;
import de.uni_trier.jane.tools.pathneteditor.tools.GeometricTools.ParameterLine;


public class PathNetTools {
	
	private static final int DEFAULT_ZOOM = 20;
	
	public static Point realToVirtual(Point pos, double grid_size, int x_offset, int y_offset) {
		return new Point((int)((double)(pos.x-x_offset)/grid_size), (int)((double)(pos.y-y_offset)/grid_size));
	}

	public static Point virtualToReal(int x, int y, double grid_size, int x_offset, int y_offset) {
		return new Point((int)((double)x*grid_size)+x_offset, (int)((double)y*grid_size)+y_offset);
	}
	
	public static Point realToVirtual(int x, int y, double grid_size, int x_offset, int y_offset) {
		return new Point((int)((double)(x-x_offset)/grid_size), (int)((double)(y-y_offset)/grid_size));
	}
	
	public static int realToVirtual(int p, double grid_size) {
		return (int)((double)p/grid_size);
	}
	
	public static Point realToVirtual(Point p, double grid_size) {
		return new Point((int)((double)p.x/grid_size), (int)((double)p.y/grid_size));
	}
	
	public static int virtualToReal(int p, double grid_size) {
		return (int)((double)p*grid_size);
	}
	
	public static Point virtualToReal(Point p, double grid_size) {
		return new Point((int)((double)p.x*grid_size), (int)((double)p.y*grid_size));
	}

	/**
	 * returns an "exactly"  transformed value (as double, not int...).
	 * @param diff_x
	 * @param zoom
	 * @return
	 */
	public static double exactVirtualToReal(int diff_x, double zoom) {
		return ((double)diff_x)*zoom;
	}
	
	/**
	 * translates a mouse position to a 'real' coordinate
	 * @param panel				the graphics panel
	 * @param mouse_position	the position of the mouse
	 * @return	the point of the map corresponding to the mouse position
	 */
	public static Point getRealPoint(GraphicsPanel panel, Point mouse_position) {
		Point p = PathNetTools.virtualToReal(mouse_position, panel.getZoom());
		p.translate(panel.getOffset().x, panel.getOffset().y);
		return p;
	}
	

	
	/**
	 * creates a planar graph by adding edges to a model
	 * @param model  the model to add the edges to (should have no edges)
	 */
	public static void addPlanarEdges(PathNetModel model) {
		Waypoint[] waypoints = model.getTargetsAndWaypoints();
		for (int i=0; waypoints!=null && i<waypoints.length; i++) {
			int found_nodes = 0;
			for (int j=i+1; j<waypoints.length; j++) {
				if (i==j)
					continue; //obsolete
				Point middle = getMiddle(waypoints[i], waypoints[j]);
				int radius = getDistance(waypoints[i], waypoints[j])/2;
				boolean found = true;
				for (int k=0; k<waypoints.length; k++)
					if (k!=i && k!=j && getDistance(waypoints[k], middle)<=radius) {
						found = false;
						break;
					}
				if (found)
					model.addEdge(new Edge(waypoints[i], waypoints[j]));
			}
		}
	}
	
	private static Point getMiddle(Waypoint p1, Waypoint p2) {
		return new Point(
				(p1.getPosition().x+p2.getPosition().x) / 2,
				(p1.getPosition().y+p2.getPosition().y) / 2
		);
	}
	
	private static int getDistance(Waypoint p1, Waypoint p2) {
		return (int)(p1.getPosition().distance(p2.getPosition()));
	}
	
	private static int getDistance(int x1, int y1, int x2, int y2) {
		return (int)Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
	
	private static int getDistance(Waypoint wp, Point p) {
		return (int)(p.distance(wp.getPosition()));
	}
	
	public static void setAAActivated(Graphics2D g, boolean activated) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, activated?RenderingHints.VALUE_ANTIALIAS_ON:RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	public static int getMaxX(PathNetObject[] objects) {
		int maxX = Integer.MIN_VALUE;
		for (int i=0; objects!=null && i<objects.length; i++) {
			Rectangle r = objects[i].getBounds(); 
			if (r.x + r.width > maxX)
				maxX = r.x + r.width;
		}
		return objects==null ? -1 : maxX;
	}
	public static int getMaxY(PathNetObject[] objects) {
		int maxY = Integer.MIN_VALUE;
		for (int i=0; objects!=null && i<objects.length; i++) {
			Rectangle r = objects[i].getBounds();
			if (r.y + r.height > maxY)
				maxY = r.y + r.height;
		}
		return objects==null ? -1 : maxY;
	}
	public static int getMinX(PathNetObject[] objects) {
		int minX = Integer.MAX_VALUE;
		for (int i=0; objects!=null && i<objects.length; i++)
			if (objects[i].getBounds().x < minX)
				minX = objects[i].getBounds().x;
		return objects==null ? -1 : minX;
	}
	public static int getMinY(PathNetObject[] objects) {
		int minY = Integer.MAX_VALUE;
		for (int i=0; objects!=null && i<objects.length; i++)
			if (objects[i].getBounds().y < minY)
				minY = objects[i].getBounds().y;
		return objects==null ? -1 : minY;
	}

	public static AffineTransform getAffineTransform(double grid_size, int x_offset, int y_offset) {
		AffineTransform at = AffineTransform.getTranslateInstance(
				(double)(-x_offset)/grid_size, (double)(-y_offset)/grid_size);
		at.scale(1d/grid_size, 1d/grid_size);
		return at;
	}
	
	public static PathNetObject getObjectAtRealPoint(PathNetModel model, Point realPoint) {	

		// Test all objects
		PathNetObject[] objects = model.getAllObjects();
		for (int i=0; i<objects.length; i++)
			if (objects[i].containsPoint(realPoint))
				return objects[i];
		
		// nothing found here, return null
		return null;			
	}
	
	public static double getZoom(int zoom) {
		return zoom < 0
			? 1.0/(double)(-zoom)
			: zoom; //zoom>=1?zoom:1;
	}
	
	
	public static void applyFactorToModelData(PathNetModel model, double factor) {
		PathNetObject[] objects = model.getAllObjects();
		for (int i=0; objects!=null && i<objects.length; i++) {
			objects[i].applyFactor(factor);
		}
	}
	
	public static int random(int min, int max) {
//		System.out.println("random(" + min + "," + max + ")");
		return (int)((Math.random()*(max+1-min))+min);
	}

	public static void createGraph(
									PathNetModel model, 
									Rectangle size, 
									Pair no_areas, 
									boolean areasNonintersected,
                                    Pair no_targets, 
									Pair no_waypoints, 
									boolean wayponitsNoninterscted, Pair no_edges, 
									Pair no_inner_points, 
									Pair area_width, 
									Pair area_height, 
									Pair target_size, 
									Pair waypoint_size, 
									Pair inner_point_size) {
		addAreas(model, size, no_areas, area_width, area_height,areasNonintersected);
		addTargets(model, no_targets, target_size);
		addWaypoints(model, size, no_waypoints, waypoint_size,wayponitsNoninterscted);
		addEdges(model, no_edges, no_inner_points, inner_point_size);
		
		// debug output
		System.out.println("After creating random PathNetObjects the model has");
		System.out.println("\tAreas:\t\t"+model.getAreas().length);
		System.out.println("\tTargets:\t"+model.getTargets().length);
		System.out.println("\tWaypoints:\t"+model.getWaypoints().length);
		System.out.println("\tEdges:\t"+model.getEdges().length);
		System.out.println("\tTotal:\t\t"+model.getAllObjects().length + " objects");
	}
	
	private static void addInnerPoints(Edge e, int ips, Pair ips_size) {
		// get ParameterLine for edge e
		ParameterLine pl = new ParameterLine(
				new Line2D.Float( e.getSource().getPosition(), e.getTarget().getPosition() )
		);
		
		// get length of direction vector
		double v_length = pl.d_vec_length();
		
		// normalize pl
		pl.normalize();
		
		for (int i=0; i<ips; i++) {
			// get random distance for ip from source in direction to target
			double p_length = Math.random() * v_length;
		
			// calculate new point p
			int p_x = (int)(pl.s_vec.getX() + pl.d_vec.getX() * p_length);
			int p_y = (int)(pl.s_vec.getY() + pl.d_vec.getY() * p_length);
			
			// add new inner point to edge
			e.addInnerPoint(new Point(p_x, p_y), random(ips_size.min, ips_size.max));
		}				
		
	}
	
	public static void addEdges(PathNetModel model, Pair no_edges, Pair no_inner_points, Pair inner_point_size) {
		int random_no = random(no_edges.min, no_edges.max);
		if (random_no==0)
			return;
		
		Waypoint[] waypoints = model.getTargetsAndWaypoints();
		int waypoint_count = waypoints.length;
		random_no = Math.min(random_no, ((waypoint_count-1)*waypoint_count)/2); // n*(n-1) / 2 max possible edges
		AdjacentMatrix am = new AdjacentMatrix(waypoints.length);
		for (int i=0 ;i<random_no; i++) {
			int r = random(0,am.getFree()-1);
			Pair points = am.set(r);
			Edge e = new Edge(waypoints[points.min], waypoints[points.max]);
			addInnerPoints(e, random(no_inner_points.min, no_inner_points.max), inner_point_size);
			model.addEdge(e);
		}
		
	}
	
	private static Vector getWaypointAndTargets(PathNetModel model) {
		Waypoint[] wps = model.getTargetsAndWaypoints();
		Vector result = new Vector(wps.length);
		for (int i=0; i<wps.length; i++)
			result.add(wps[i]);
		return result;
	}
	
	public static void addWaypoints(PathNetModel model, Rectangle size, Pair no_waypoints, Pair waypoint_size, boolean wayponitsNoninterscted) {
        
		int random_no = random(no_waypoints.min, no_waypoints.max);
		for (int i=0; i<random_no; i++) {
            Waypoint w = new Waypoint();
            boolean intersectionFound=true;
            int count=0;
            do{
                w.setPosition(getRandomPoint(size));
                w.setSymbolSize(random(waypoint_size.min, waypoint_size.max));
                if (!wayponitsNoninterscted){
                    count++;
                    Area[] areas=model.getAreas();
                    intersectionFound=false;
                    for (int j=0;j<areas.length;j++){
                        if(areas[j].getBounds().intersects(w.getBounds())){
                            intersectionFound=true;
                            break;
                        }
                    }
                }

                if (wayponitsNoninterscted||!intersectionFound){
                    model.addWaypoint(w);
                }
            } while(!wayponitsNoninterscted&&intersectionFound);
		}
	}
	
	private static void addTargets(PathNetModel model, Pair no_targets, Pair target_size) {
		Area[] areas = model.getAreas();
		for (int i=0; areas!=null && i<areas.length; i++) {
			int random_no = random(no_targets.min, no_targets.max);
			for (int j=0; j<random_no; j++) {
				// IMPORTANT: The name of the target must be set explicitly to [areaID]:[NoOfTarget], which is done here
				Target t = new Target("" + areas[i].getID() + ":" + (j+1));
				//Point p = getRandomPoint(areas[i].getBounds());
				//t.setPosition(areas[i].getNearestOutlinePoint(p));
                t.setPosition(getRandomPointOutline(areas[i].getBounds()));
				t.setSymbolSize(random(target_size.min, target_size.max));
				model.addTarget(areas[i], t);
			}
		}
	}
	
	private static void addAreas(PathNetModel model, Rectangle size, Pair no_areas, Pair area_width, Pair area_height, boolean areasNonintersected) {
		int random_no = random(no_areas.min, no_areas.max);
		for (int i=0; i<random_no; i++) {
            boolean intersectionFound=true;
            int count=0;
            do{
//          int x1 = size.x+random(0,size.width);
//          int y1 = size.y+random(0, size.height);
//          int x2 = size.x+random(0,size.width);
//          int y2 = size.y+random(0, size.height);
                int x1 = size.x+random(0,size.width);
                int y1 = size.y+random(0, size.height);
              int x2 = x1+random(area_width.min,area_width.max);
              int y2 = y1+random(area_height.min, area_height.max);
    
    			Area area = new Area();
    			area.addVertex(new Point(x1,y1));
    			area.addVertex(new Point(x2,y1));
    			area.addVertex(new Point(x2,y2));
    			area.addVertex(new Point(x1,y2));
              if (!areasNonintersected){
                  count++;
                  Area[] areas=model.getAreas();
                  intersectionFound=false;
                  for (int j=0;j<areas.length;j++){
                      if(areas[j].getBounds().intersects(area.getBounds())){
                          intersectionFound=true;
                          break;
                      }
                  }
              }
              if (areasNonintersected||!intersectionFound){
    			model.addArea(area);
              }
            }while (!areasNonintersected&&intersectionFound&&count<100000);
            
		}
	}
    private static Point getRandomPointOutline(Rectangle bounds) {
        int r=random(0,bounds.width+bounds.height);
        int lr_td=random(0,1);
        int x=bounds.x,y=y=bounds.y;
        
        if(r>bounds.height){
            if (lr_td==0){
                y+=bounds.height;
            }
               
            x+=r-bounds.height;
        }else{
            if (lr_td==0){
                x+=bounds.width;
            }
            y+=r;
        }
        return new Point(x,y);
    }
	private static Point getRandomPoint(Rectangle bounds) {
		return new Point(random(bounds.x, bounds.x+bounds.width), random(bounds.y, bounds.y+bounds.height));
	}
		
	/**
	 * a hopefully efficient way to store the edges already created
	 * all just to avoid a possibly infinite loop when randomly creating edges...
	 */
	public static class AdjacentMatrix {
		private int size = 0;
		private int[] count = null;
		private int total = 0;
		private BitSet set = null;
		private Pair pair = new Pair();
		
		public AdjacentMatrix(int size) {
			if (size<2)
				throw new RuntimeException("You don't need this for less two waypoints!");
			this.size = size;
			total = size * (size-1) / 2;
			set = new BitSet(total);
			count=new int[size-1];
			initCount();
		}
		
		private void initCount() {
			int sum=1;
			for (int i=1; i<size; i++) {
				count[i-1] = sum;
				sum += i+1;
			}
		}
		public int[] getCount() {
			return count;
		}
		private int getPos(int index) {
			int pos = -1;
			for (int i=0;;i++) {
				if (!set.get(i))
					pos++;
				if (pos == index)
					return i;
			}
		}
		public Pair set(int index) {
			int pos = getPos(index);
			total--;
			if (set.get(pos))
				throw new RuntimeException("WAS SET BEFORE!!!");
			set.set(pos);
			
			// calculate row/column
			int row = binsearch(count, pos);
			pair.min = row<0
						? -row
						: row+2;
			pair.max = pos-(pair.min-1>0?count[pair.min-2]:0);
			/**
			 * OTHER WAY TO DETERMINE THE ROW AND COLUMN:
			 * 
			 * pair.min = (int)(0.5 + Math.sqrt(0.25 + 2 * pos));
			 * pair.max = (int)(pos - ( pair.min * (pair.min - 1)) / 2.0);
			 * 
			 * PROBLEM: the Math.sqrt() function is slower then the binsearch
			 * 			implemented below.
			 */
			return pair;
		}
		public int getFree() {
			return total;
		}
	}

	public static int binsearch(int[] array, int key) {
		int l = 0;
		int r = array.length-1;
		int m = r/2;
		while (l<r) {
			if (key<array[m])
				r=m-1;
			else if (key>array[m])
				l=m+1;
			else
				return m;
			m=(r+l)/2;
		}
		if (key==array[l])
			return l;
		else if (key>array[l])
			return -l-2;
		else
			return -l-1;
	}
	

}
