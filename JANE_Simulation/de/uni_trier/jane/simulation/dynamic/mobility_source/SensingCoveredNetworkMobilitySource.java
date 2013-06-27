/*
 * Created on 23.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.simulation.dynamic.mobility_source;


import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.dynamic.position_generator.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.visualization.*;
import de.uni_trier.jane.util.dijkstra.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * This class is used to simulate a sensing covered network.
 */
public class SensingCoveredNetworkMobilitySource implements MobilitySource {

    private final static double LAST_COMMAND_TIME = 600;//Double.POSITIVE_INFINITY;
    private FixedNodesData fixedNodesData;
    private Set arrivedSet;

    private SensingCoveredNetworkMobilitySource(FixedNodesData fixedNodesData) {
        this.fixedNodesData = fixedNodesData;
        arrivedSet = new HashSet();
    }

    /**
     * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextEnterInfo()
     */
    public boolean hasNextEnterInfo() {
        return fixedNodesData.hasNext();
    }

    /**
     * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextEnterInfo()
     */
    public EnterInfo getNextEnterInfo() {
        NodeData data = fixedNodesData.next();
        return new EnterInfo(data.getAddress(), data.getCommunicationRange(),
                new ArrivalInfo(data.getPosition(), 0.0));
    }

    /**
     * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
     */
    public boolean hasNextArrivalInfo(DeviceID address) {
        return !arrivedSet.contains(address);
    }

    /**
     * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
     */
    public ArrivalInfo getNextArrivalInfo(DeviceID address) {
        NodeData data = fixedNodesData.getNodeData(address);
        arrivedSet.add(address);
        return new ArrivalInfo(data.getPosition(), LAST_COMMAND_TIME);
    }

    /**
     * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getRectangle()
     */
    public Rectangle getRectangle() {
        return fixedNodesData.getRectangle();
    }

    /**
     * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getTotalDeviceCount()
     */
    public int getTotalDeviceCount() {
        return fixedNodesData.getNodeCount();
    }

    /**
     * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getShape()
     */
    public Shape getShape() {
        return fixedNodesData.getShape();
    }

    public Position getPosition(DeviceID address) {
        return fixedNodesData.getNodeData(address).getPosition();
    }

    /**
     * @param number
     * @param distributionCreator
     * @param areaWidth
     * @param areaHeight
     * @param concaveWidth
     * @param concaveHeight
     * @param rangeRatio
     * @return
     */
    public static SensingCoveredResult createRandom(int number,
            DistributionCreator distributionCreator, double areaWidth,
            double areaHeight, double concaveWidth, double concaveHeight,
            double rangeRatio) {
        List list = new ArrayList();
        double sensingRange = 10.0;
        double communicationRange;
        Rectangle rectangle = null;

        Rectangle concave = new Rectangle(new Position(0,
                (areaHeight - concaveHeight) / 2), new Position(concaveWidth,
                areaHeight - ((areaHeight - concaveHeight) / 2)));
        ContinuousDistribution xDistribution = distributionCreator
                .getContinuousUniformDistribution(0, areaWidth);
        ContinuousDistribution yDistribution = distributionCreator
                .getContinuousUniformDistribution(0, areaHeight);
        PositionGenerator positionGenerator = new RandomPositionGenerator(
                xDistribution, yDistribution);
        Rectangle r1 = new Rectangle(new Position(0, 0), new Position(
                areaWidth, areaHeight));
        Shape shape = new RectangleShape(r1, Color.BLACK, false);
        for(;;){
            List tmp = new ArrayList();
            for (;;) {
                int i = 0;
                while (i < number) {
                    Position p = positionGenerator.getNext(0);
                    if (!concave.contains(p)) {
                        NodeData data = new NodeData(new SimulationDeviceID(i+1), p);
                        tmp.add(data);
                        if (rectangle == null) {
                            rectangle = new Rectangle(data.getPosition(),
                                    Extent.NULL_EXTENT);
                        } else {
                            rectangle = rectangle.union(new Rectangle(data
                                    .getPosition(), Extent.NULL_EXTENT));
                        }
                        i++;
                    }
                }
                if (i == number)
                    break;
            }
            Area area = createArea(areaWidth, areaHeight, concaveWidth,
                    concaveHeight);
            
            if (!isCovered(area, (ArrayList) tmp, sensingRange)) {
                sensingRange = sensingRange+10;
            }
            else{
                list = tmp;
                System.out.println("SensingRange is: "+sensingRange);
                break;
            }
        }
       communicationRange = sensingRange*rangeRatio;
//       System.out.println("CommunicationRange is: "+communicationRange);
       for(Iterator i = list.iterator();i.hasNext();){
           NodeData d = (NodeData) i.next();
           d.setCommunicationRange(communicationRange);
           d.setSensingRange(sensingRange);
       }

//       MobilitySource mobilitySource = new SensingCoveredNetworkMobilitySource(

       FixedNodesData data = new FixedNodesData(list, rectangle, shape, communicationRange, communicationRange);
       
       return new SensingCoveredResult(sensingRange, communicationRange, data);

    }

    public static SensingCoveredResult createRandom2(int number,
            DistributionCreator distributionCreator, Rectangle area, Rectangle obstacle,
            double border, double startSensingRange, double startIncrement, double stopIncrement,
            double rangeRatio) {
        
        double sensingRange = startSensingRange;
        double communicationRange;
        Rectangle rectangle = null;

        
        
        
        
        ContinuousDistribution xDistribution = distributionCreator
                .getContinuousUniformDistribution(0, area.getWidth());
        ContinuousDistribution yDistribution = distributionCreator
                .getContinuousUniformDistribution(0, area.getHeight());
        PositionGenerator positionGenerator = new RandomPositionGenerator(
                xDistribution, yDistribution);
        //Rectangle r1 = new Rectangle(new Position(0, 0), new Position(
          //      areaWidth, areaHeight));
       Shape shape = new RectangleShape(obstacle, Color.BLACK, false);
        List list = new ArrayList();
        for (;;) {
            int i = 0;
            while (i < number) {
                Position p = positionGenerator.getNext(0);
                if (!obstacle.contains(p)) {
                    NodeData data = new NodeData(new SimulationDeviceID(i+1), p);
                    list.add(data);
                    if (rectangle == null) {
                        rectangle = new Rectangle(data.getPosition(),
                                Extent.NULL_EXTENT);
                    } else {
                        rectangle = rectangle.union(new Rectangle(data
                                .getPosition(), Extent.NULL_EXTENT));
                    }
                    i++;
                }
            }
            if (i == number)
                break;
        }
        
        Area coveredArea = createCoveredArea(area,obstacle,border);
        double increment=startIncrement;
        boolean forward=true;
        boolean changed=false;
        while (!changed){
            if (!isCovered(coveredArea, (ArrayList) list, sensingRange)) {
                if (sensingRange==startSensingRange){
                    forward=true;    
                }
                if (!forward){
                    changed=true;
                }else{
                    sensingRange+= increment;
                }
                
            }else{
                if (sensingRange==startSensingRange){
                    forward=false;    
                }
                if (forward){
                    changed=true;
                }else{
                    sensingRange-= increment;
                }
            }
        }
        forward=!forward;
        
        while (increment/2>stopIncrement){
            increment/=2;
            if (forward){
                sensingRange+=increment;
            }else{
                sensingRange-=increment;
            }
            
            
            
            if (!isCovered(coveredArea, (ArrayList) list, sensingRange)) {
                forward=true;
            }else{
                forward=false;
            }
        }
        if(forward){
            //last run was to far...
            sensingRange+=increment;
        }
        
        System.out.println("SensingRange is: "+sensingRange);
       communicationRange = sensingRange*rangeRatio;
//       System.out.println("CommunicationRange is: "+communicationRange);
       for(Iterator i = list.iterator();i.hasNext();){
           NodeData d = (NodeData) i.next();
           d.setCommunicationRange(communicationRange);
           d.setSensingRange(sensingRange);
       }



       FixedNodesData data = new FixedNodesData(list, rectangle, shape, communicationRange, communicationRange);
       
       return new SensingCoveredResult(sensingRange, communicationRange, data);

    }
  
    public static SensingCoveredResult createRandom2(int number,
            DistributionCreator distributionCreator, Rectangle area, Rectangle obstacle,
            double sensingRange,
            double rangeRatio) {
        
        
        double communicationRange;
        Rectangle rectangle = null;

        
        
        
        
        ContinuousDistribution xDistribution = distributionCreator
                .getContinuousUniformDistribution(0, area.getWidth());
        ContinuousDistribution yDistribution = distributionCreator
                .getContinuousUniformDistribution(0, area.getHeight());
        PositionGenerator positionGenerator = new RandomPositionGenerator(
                xDistribution, yDistribution);
        //Rectangle r1 = new Rectangle(new Position(0, 0), new Position(
          //      areaWidth, areaHeight));
       Shape shape = new RectangleShape(obstacle, Color.BLACK, false);
        List list = new ArrayList();
        for (;;) {
            int i = 0;
            while (i < number) {
                Position p = positionGenerator.getNext(0);
                if (!obstacle.contains(p)) {
                    NodeData data = new NodeData(new SimulationDeviceID(i+1), p);
                    list.add(data);
                    if (rectangle == null) {
                        rectangle = new Rectangle(data.getPosition(),
                                Extent.NULL_EXTENT);
                    } else {
                        rectangle = rectangle.union(new Rectangle(data
                                .getPosition(), Extent.NULL_EXTENT));
                    }
                    i++;
                }
            }
            if (i == number)
                break;
        }
        
     
        System.out.println("SensingRange is: "+sensingRange);
       communicationRange = sensingRange*rangeRatio;
//       System.out.println("CommunicationRange is: "+communicationRange);
       for(Iterator i = list.iterator();i.hasNext();){
           NodeData d = (NodeData) i.next();
           d.setCommunicationRange(communicationRange);
           d.setSensingRange(sensingRange);
       }



       FixedNodesData data = new FixedNodesData(list, rectangle, shape, communicationRange, communicationRange);
       
       return new SensingCoveredResult(sensingRange, communicationRange, data);

    }
  
    private static boolean isCovered(Area ConcaveRegion,
            ArrayList deployedNodes, double sensingRange) {

        int elements = deployedNodes.size();
        Area area = new Area();

        for (int i = 0; i < elements; i++) {
            Position nodeCenter = ((NodeData) deployedNodes.get(i))
                    .getPosition();
            EllipsePathIterator ellipsePathIterator = new EllipsePathIterator(
                    nodeCenter, new Extent(2*sensingRange,
                            2*sensingRange), 12);
            int XPoints[] = new int[12];
            int YPoints[] = new int[12];
            int k = 0;
            while(!ellipsePathIterator.isDone() && k<12){
                double m[]={0.0,0.0};
                if(ellipsePathIterator.currentSegment(m)!=EllipsePathIterator.SEG_CLOSE){
                    XPoints[k] = (int)m[0];
                    YPoints[k] = (int)m[1];
                    k++;
                    
                }
                ellipsePathIterator.next();
            }
            Polygon polygon = new Polygon(XPoints, YPoints, 12);
            area.add(new Area(polygon));

        }
        area.intersect(ConcaveRegion);
        return area.equals(ConcaveRegion);
    }


    private static Area createArea(double areaWidth, double areaHeight,
            double concaveWidth, double concaveHeight) {
        int t1 = (int) (areaHeight - ((areaHeight - concaveHeight) / 2));
        int t2 = (int) (t1 - concaveHeight);
        int[] xPoints = { 0, (int) areaWidth, (int) areaWidth, 0, 0,
                (int) concaveWidth, (int) concaveWidth, 0 };
        int[] yPoints = { 0, 0, (int) areaHeight, (int) areaHeight, t1, t1, t2,
                t2 };

        Polygon polygon = new Polygon(xPoints, yPoints, 8);

        return new Area(polygon);
    }
    private static Area createCoveredArea(Rectangle area, Rectangle obstacle, double border) {
        
        java.awt.Rectangle areaRect=new java.awt.Rectangle((int)border,(int)border,(int)(area.getWidth()-2*border),(int)(area.getHeight()-2*border));
        java.awt.Rectangle obstRect=new java.awt.Rectangle((int)(obstacle.getBottomLeft().getX()+border),(int)(obstacle.getBottomLeft().getY()-border),(int)obstacle.getWidth(),(int)(obstacle.getHeight()+2*border));
        Area obstArea=new Area(obstRect);
        Area areaArea=new Area(areaRect);
        areaArea.subtract(obstArea);
        
        
//        int t1 = (int) (border+areaHeight - ((areaHeight - concaveHeight) / 2));
//        int t2 = (int) (t1 - concaveHeight-2*border);
//        int[] xPoints = { (int)border, (int) (areaWidth-border), (int) (areaWidth-border), (int)border, (int)border,
//                (int) (concaveWidth+border), (int) (concaveWidth+border), (int)border };
//        int[] yPoints = { (int)border, (int)border, (int) (areaHeight-border), (int) (areaHeight-border), t1, t1, t2,
//                t2 };
//
//        Polygon polygon = new Polygon(xPoints, yPoints, 8);

        return areaArea;
    }

    /**
     * this class describes all devices in the network
     */
    private static class FixedNodesData {
        private List nodeDataList;
        private Rectangle rectangle;
        private Shape shape;
        private Map addressNodeDataMap;
        private Iterator valueIterator;
        private double minimumSendingRadius;
        private double maximumSendingRadius;
        public FixedNodesData(List nodeDataList, Rectangle rectangle,
                Shape shape, double minimumSendingRadius, double maximumSendingRadius) {
            this.nodeDataList = nodeDataList;
            this.rectangle = rectangle;
            this.shape = shape;
            this.minimumSendingRadius = minimumSendingRadius;
            this.maximumSendingRadius = maximumSendingRadius;
            addressNodeDataMap = new HashMap();
            Iterator iterator = nodeDataList.iterator();
            while (iterator.hasNext()) {
                NodeData data = (NodeData) iterator.next();
                addressNodeDataMap.put(data.getAddress(), data);
            }
            valueIterator = nodeDataList.iterator();
        }

        public boolean hasNext() {
            return valueIterator.hasNext();
        }

        public NodeData next() {
            return (NodeData) valueIterator.next();
        }

        public NodeData getNodeData(DeviceID address) {
            return (NodeData) addressNodeDataMap.get(address);
        }

        public Shape getShape() {
            return shape;
        }

        public Rectangle getRectangle() {
            return rectangle;
        }

        public int getNodeCount() {
            return nodeDataList.size();
        }

        public double getMaximumSendingRadius() {
			return maximumSendingRadius;
		}

		public double getMinimumSendingRadius() {
			return minimumSendingRadius;
		}
        
    }

    /**
     * this class describes a node (i.g, a device in the network)
     */
    private static class NodeData {
        private DeviceID address;
        private Position position;
        private double communicationRange;
        private double sensingRange;

        public NodeData(DeviceID address, Position position) {
            this.address = address;
            this.position = position;

        }

        public NodeData(DeviceID address, Position position,
                double communicationRange, double sensingRange) {
            this.address = address;
            this.position = position;
            this.communicationRange = communicationRange;
            this.sensingRange = sensingRange;
        }

        public DeviceID getAddress() {
            return address;
        }

        public Position getPosition() {
            return position;
        }

        public double getCommunicationRange() {
            return communicationRange;
        }

        public double getSensingRange() {
            return sensingRange;
        }

        /**
         * @param communicationRange
         *            The communicationRange to set.
         */
        public void setCommunicationRange(double communicationRange) {
            this.communicationRange = communicationRange;
        }

        /**
         * @param sensingRange
         *            The sensingRange to set.
         */
        public void setSensingRange(double sensingRange) {
            this.sensingRange = sensingRange;
        }
    }

    /**
     * this class gives information about each node and his neighbors.
     */
    private static class NodeDataGraph implements Graph {
        private List neighborLists;
        private Map addressIndexMap;

        public NodeDataGraph(List nodeData) {
            addressIndexMap = new HashMap();
            neighborLists = new ArrayList();
            int elementCount = nodeData.size();
            for (int i = 0; i < elementCount; i++) {
                List neighborList = new ArrayList();
                NodeData node1 = (SensingCoveredNetworkMobilitySource.NodeData) nodeData
                        .get(i);
                for (int j = 0; j < elementCount; j++) {
                    NodeData node2 = (SensingCoveredNetworkMobilitySource.NodeData) nodeData
                            .get(j);
                    if (!node1.getAddress().equals(node2.getAddress())) {
                        double communicationRange = Math.min(node1
                                .getCommunicationRange(), node2
                                .getCommunicationRange());
                        if(node1.getPosition().distance(node2.getPosition())<communicationRange) {
							neighborList.add(new Integer(j));
						}
                    }
                }
                neighborLists.add(i, neighborList);
                addressIndexMap.put(node1.getAddress(), new Integer(i));
            }
        }

        public int getNodeCount() {
            return neighborLists.size();
        }

        public int getNode(int number) {
            return number;
        }

        public int getNeighborCount(int node) {
            return ((List) (neighborLists.get(node))).size();
        }

        public int getNeighbor(int node, int number) {
            List neighborList = (List) (neighborLists.get(node));
            return ((Integer) neighborList.get(number)).intValue();
        }

        public double getWeight(int source, int destination) {
            return 1;
        }

        public int getIndex(DeviceID address) {
            return ((Integer) addressIndexMap.get(address)).intValue();
        }
    }

    public static class SensingCoveredResult {
        private double sensingRange;
        private double communicationRange;
        private FixedNodesData data;
        private MobilitySource mobilitySource;
        
        /**
         * @param sensingRange
         * @param communicationRange
         * @param mobilitySource
         */
        public SensingCoveredResult(double sensingRange,
                double communicationRange, FixedNodesData data) {
            this.sensingRange = sensingRange;
            this.communicationRange = communicationRange;
            this.data = data;
            mobilitySource = new SensingCoveredNetworkMobilitySource(data);
        }
        
        /**
         * @return Returns the communicationRange.
         */
        public double getCommunicationRange() {
            return communicationRange;
        }
        /**
         * @return Returns the mobilitySource.
         */
        public MobilitySource getMobilitySource() {
            return mobilitySource;
        }
        /**
         * @return Returns the sensingRange.
         */
        public double getSensingRange() {
            return sensingRange;
        }
        
        public Position getPosition(DeviceID address) {
            return data.getNodeData(address).getPosition();
        }

    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getTerminalCondition(de.uni_trier.jane.basetypes.Clock)
     */
    public Condition getTerminalCondition(Clock clock) {
        // TODO Auto-generated method stub
        return null;
    }

	public double getMinimumTransmissionRange() {
		return fixedNodesData.getMinimumSendingRadius();
	}

	public double getMaximumTransmissionRange() {
		return fixedNodesData.getMaximumSendingRadius();
	}
    
    
}