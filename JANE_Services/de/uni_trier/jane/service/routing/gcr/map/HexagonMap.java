/*
 * Created on 03.09.2004
 */
package de.uni_trier.jane.service.routing.gcr.map;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerAddress;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * this class implements a hexagon map
 * a hexagon map is so to speak an area divided in a finite set of hexagons
 * 
 * @author Hamid
 */
public class HexagonMap implements ClusterMap {
	
	private static final double cte =Math.sqrt(3.0)/2.0; 
	
	/**
	 * the display screen
	 */
	private Rectangle plane;     
	
	/**
	 * the cluster diameter of each one in the map
	 */
	private double diameter; 
	
	/**
	 * the Cluster height
	 */
	private double height ;  
	
	/**
	 * the area whre the clusters will reside as map
	 */
	private Map map;			
	
	/**
	 *  true if hexagon addresses want to be has shown
	 */
	private boolean visualizeAddress;

    
    /**
     * Visualizatio
     */
	private Shape shape;
    private int clusters;
	
	/**
	 * Constructs a hexagonmap with adiameter as diameter for each hexagon
	 * 
	 * @param aDiameter the cluster diameter 
	 * @param visualizeAddress true if hexagon addresses want to be has shown 
	 */
	public HexagonMap(double aDiameter, boolean visualizeAddress){
		this.diameter = aDiameter;
		this.height = diameter*cte;
		this.map = new HashMap();
		this.visualizeAddress = visualizeAddress;
	}
	
	/**
	 * Constructs a hexagonmap with adiameter as diameter for each hexagon
	 * 
	 * @param plane the area where the clusters are residing
	 * @param aDiameter the cluster diameter 
	 * @param visualizeAddress true if hexagon addresses want to be has shown 
	 */
	public HexagonMap(Rectangle plane,double aDiameter, boolean visualizeAddress){
		if(!plane.getBottomLeft().equals(Position.NULL_POSITION))
			throw new IllegalArgumentException("\nThe buttomLeft of the Plane must be Position.NULL_POSITION:\n Please try it again with arguments " +
			"like:\n\t Rectangle r = new Rectangle(Position.NULL_POSITION,new Position(100.0,100.0));");
		this.plane = plane;
		this.diameter = aDiameter;
		this.height = diameter*cte;
		this.map = new HashMap();
		this.visualizeAddress = visualizeAddress;
		buildMap(this.plane);   
	}
	
	/**
	 * @return Returns the diameter.
	 */
	public double getDiameter() {
		return diameter;
	}
	/**
	 * @return Returns the height.
	 */
	public double getHeight() {
		return height;
	}
	/**
	 * Get the cluster where a network node with the given position is located in.
	 * 
	 * @param position the position of the network node
	 * @return the cluster
	 */
	public Cluster getCluster(Position position) {
		double xtmp = assignXCoordinate(position.getX());
		double ytmp = assignYCoordinate(position.getX(),position.getY());
		
		double x1 = Math.ceil(xtmp);
		double x2 = Math.floor(xtmp);
		double y1 = Math.ceil(ytmp);
		double y2 = Math.floor(ytmp);
		
		ClusterAddress s1 = new ClusterAddress((int)x1,(int)y1,0);
		ClusterAddress s2 = new ClusterAddress((int)x1,(int)y2,0);
		ClusterAddress s3 = new ClusterAddress((int)x2,(int)y1,0);
		ClusterAddress s4 = new ClusterAddress((int)x2,(int)y2,0);
		
		Cluster cltmp1 = new Hexagon(new Position(reAssignOldXCoordinate(s1.getXc()),
				reAssignOldYCoordinate(s1.getXc(), s1.getYc())),diameter,s1);
		map.put(s1, cltmp1);
		Cluster cltmp2 = new Hexagon(new Position(reAssignOldXCoordinate(s2.getXc()),
				reAssignOldYCoordinate(s2.getXc(), s2.getYc())),diameter,s2);
		map.put(s2, cltmp2);
		Cluster cltmp3 = new Hexagon(new Position(reAssignOldXCoordinate(s3.getXc()),
				reAssignOldYCoordinate(s3.getXc(), s3.getYc())),diameter,s3);
		map.put(s3, cltmp3);
		Cluster cltmp4 = new Hexagon(new Position(reAssignOldXCoordinate(s4.getXc()),
				reAssignOldYCoordinate(s4.getXc(), s4.getYc())),diameter,s4);
		map.put(s4, cltmp4);
		
		
		if((cltmp1 != null)&& cltmp1.isInside(position)) return cltmp1;
		if((cltmp2 != null)&& cltmp2.isInside(position)) return cltmp2;
		if((cltmp3 != null)&& cltmp3.isInside(position)) return cltmp3;
		if((cltmp4 != null)&& cltmp4.isInside(position)) return cltmp4;
		return new Hexagon(new Position(0.0,0.0),diameter,new ClusterAddress(0,0,0));
		
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.ssds.geographic_routing.cfrplus.ClusterMap#getShape()
	 */
	public Shape getShape() {
		if (shape==null||clusters<map.size()){ 
			shape=buildShape();
            clusters=map.size();
		}
		return shape;
	}

	private Shape buildShape() {
		ShapeCollection shapeCollection = new ShapeCollection();
		Rectangle rectangle = new Rectangle(new Position(10.0,10.0), new Position(10.0,10.0));
		List l = new ArrayList(map.values());
		
		Iterator it = l.iterator();
		while (it.hasNext())
		{
			Hexagon tmp = (Hexagon) it.next();
			List positionList = new ArrayList();
			positionList.add(tmp.getCenter().add(new Position(-diameter/4, -height/2)));
			positionList.add(tmp.getCenter().add(new Position(-diameter/2, 0)));
			positionList.add(tmp.getCenter().add(new Position(-diameter/4, height/2)));
			positionList.add(tmp.getCenter().add(new Position(diameter/4, height/2)));
			positionList.add(tmp.getCenter().add(new Position(diameter/2, 0)));
			positionList.add(tmp.getCenter().add(new Position(diameter/4, -height/2)));
			Rectangle rectangle1 = new Rectangle(tmp.getCenter().add(new Position(-diameter/2, -height/2)), tmp.getCenter().add(new Position(diameter/2, height/2)));
			
			
			shapeCollection.addShape(new PolygonShape(new PositionListImpl(positionList, rectangle), Color.LIGHTBLUE, false), Position.NULL_POSITION);
			if(visualizeAddress){
				shapeCollection.addShape(new TextShape(tmp.getAddress().toString(),rectangle,Color.RED),tmp.getCenter());
			}
		}
		return shapeCollection;
	}
	/**
	 * build the cluster map and store clusters in a Hashmap.
	 * each cluster has unique address and Position 
	 * 
	 * @param the area where clusters are residing 
	 */
	private void buildMap(Rectangle aPlane)
	{
		Position offset0 = new Position(diameter*(3.0/2.0),0.0);
		Position p = aPlane.getBottomLeft();
		//int m = (int)(aPlane.getWidth()/(diameter*(3.0/2.0))) + 5;
		//int n = (int)Math.ceil(aPlane.getHeight()/((3.0/2.0)*height)) + 5;
		int m = (int)Math.ceil(aPlane.getWidth()/(diameter*(3.0/2.0)));
		int n = (int)Math.ceil(aPlane.getHeight()/(height));
		for(int j=0; j<n; j++){
			for(int i=0; i<m;i++)
			{
				LinkLayerAddress add = new ClusterAddress((int)Math.round(assignXCoordinate(p.getX())),(int)Math.round(assignYCoordinate(p.getX(),p.getY())),0);
				map.put(add,new Hexagon(p,diameter,add));
				p = p.add(offset0);		
			}
			p = Position.NULL_POSITION.add(new Position((3.0/4.0)*diameter,(j*height)+(height/2)));
			for(int i=0; i<m;i++)
			{
				LinkLayerAddress add = new ClusterAddress((int)Math.round(assignXCoordinate(p.getX())),(int)Math.round(assignYCoordinate(p.getX(),p.getY())),0);
				map.put(add,new Hexagon(p,diameter,add));
				p = p.add(offset0);		
			}
			p = Position.NULL_POSITION.add(new Position(0.0,(j+1)*height));
		}
		
		
	}
	private  double assignXCoordinate(double xOld){
		return xOld/((Math.cos(Math.PI/6)*height));
	}
	
	private  double assignYCoordinate(double xOld,double yOld){
		return (yOld/height) + ((xOld/height)*(Math.cos(Math.PI/3)/Math.cos(Math.PI/6)));
	}
	
	private double reAssignOldXCoordinate(int x){
		return x * Math.cos(Math.PI/6)* height;
	}
	
	private double reAssignOldYCoordinate(int x, int y){
		return (y*height) -  (x * Math.cos(Math.PI/3)* height);
	}
	
	public Map getMap(){
		return map;
	}
	
	public String toString() {
		return "Hexagon(" + diameter + ")";
	}

	public boolean isInReach(Address source, Address destination) {
		return getCluster(source).getDistance(getCluster(destination)) <= 2;
	}

	public int getRequiredBits() {
		return 18;
	}

	public Cluster getCluster(Address address) {
		ClusterAddress hexagonAddress = (ClusterAddress)address;
		Position pos_xc = new Position((3.0 * diameter)/4.0, (-height)/2.0);
		Position pos_yc = new Position(0.0, height);
		Position pos_zc = new Position((-3.0 * diameter)/4.0, (-height)/2.0);
		pos_xc = pos_xc.scale(hexagonAddress.getXc());
		pos_yc = pos_yc.scale(hexagonAddress.getYc());
		pos_zc = pos_zc.scale(hexagonAddress.getZc());
		Position pos = pos_xc.add(pos_yc).add(pos_zc);
		return getCluster(pos);
	}

	public double getClusterDiameter() {
		return diameter;
	}
	
}















