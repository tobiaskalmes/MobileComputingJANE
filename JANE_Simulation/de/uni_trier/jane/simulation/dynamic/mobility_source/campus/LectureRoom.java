/*****************************************************************************
 * 
 * LectureRoom.java
 * 
 * $Id: LectureRoom.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.simulation.dynamic.mobility_source.campus; 

import java.util.*;
import java.util.Map.Entry;



import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.DistributionCreator;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.ArrivalInfo;
import de.uni_trier.jane.simulation.dynamic.position_generator.PositionGenerator;
import de.uni_trier.jane.simulation.dynamic.position_generator.RandomPositionGenerator;
import de.uni_trier.jane.simulation.kernel.Condition;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class LectureRoom implements DeviceLocation {

    private Rectangle rectangle;
    private ShapeCollection shape;
    private double seatsHight;
    private double seatsWidth;
    private Rectangle seats;
    private Rectangle frontPlace;
    private RandomPositionGenerator positionGenerator;
    private RandomPositionGenerator positionGeneratorSeats;
    private DeviceID dozent;
    //private HashMap otherDevices;
    private HashMap devicePositionMap;
    private DeviceID[][] deviceIDs;
	private RandomPositionGenerator seatPositiongenerator;
	private boolean seatDozent;
    
    /**
     * Constructor for class <code>LectureRoom</code>
     * @param seatDozent 
     */
    public LectureRoom(Rectangle room,Rectangle seats,int numberSeats, Position front, DistributionCreator distributionCreator, boolean seatDozent) {
        if (!room.contains(seats)) throw new IllegalArgumentException("The room must contain the seat area");
        if (!room.contains(front)) throw new IllegalArgumentException("The room must contain the lecturers place");
        positionGenerator=new RandomPositionGenerator(
                distributionCreator.getContinuousUniformDistribution(room.getBottomLeft().getX(),room.getTopRight().getX()),
                distributionCreator.getContinuousUniformDistribution(room.getBottomLeft().getY(),room.getTopRight().getY())
                );
        positionGeneratorSeats=new RandomPositionGenerator(
                distributionCreator.getContinuousUniformDistribution(seats.getBottomLeft().getX(),seats.getTopRight().getX()),
                distributionCreator.getContinuousUniformDistribution(seats.getBottomLeft().getY(),seats.getTopRight().getY())
                );
        this.rectangle=room;
        this.seats=seats;
        devicePositionMap=new HashMap();
        double s=Math.sqrt((seats.getHeight()*seats.getWidth())/numberSeats);
        int seatsAmountHight=(int) (seats.getHeight()/s);
        int seatsAmountWidth=(int) (seats.getWidth()/s);
        seatsHight=seats.getHeight()/seatsAmountHight;
        seatsWidth=seats.getWidth()/seatsAmountWidth;
        seatPositiongenerator=new RandomPositionGenerator(
        		distributionCreator.getContinuousUniformDistribution(-seatsWidth/4,seatsWidth/4),
        		distributionCreator.getContinuousUniformDistribution(-seatsHight/4,seatsHight/4));
        frontPlace=new Rectangle(front,new Extent(seatsWidth,seatsHight));
        if (seats.contains(frontPlace)) 
            throw new IllegalArgumentException("The seat area should not contain the lectures place");
 //       if (!room.contains(frontPlace))
 //               throw new IllegalArgumentException("The room must contain the lecturers place");
        deviceIDs=new DeviceID[seatsAmountHight][seatsAmountWidth];
        
        
        shape=new ShapeCollection();
        this.seatDozent=seatDozent;
        

//        for (int i=0;i<seatsAmountHight;i++){
//            for (int j=0;j<seatsAmountWidth;j++){
//                shape.addShape(new RectangleShape(
//                        getSeatRectangle(i,j)
//                        ,
//                        Color.GREY,false));
//            }
//        }
        for (int i=0;i<seatsAmountHight;i++){
        	Position offset=new Position(0,seatsHight*i);
        	shape.addShape(new LineShape(seats.getBottomLeft().add(offset),seats.getBottomRight().add(offset),Color.GREY));;
        }
        for (int i=0;i<seatsAmountWidth;i++){
        	Position offset=new Position(seatsWidth*i,0);
        	shape.addShape(new LineShape(seats.getBottomLeft().add(offset),seats.getTopLeft().add(offset),Color.GREY));;
        }

        shape.addShape(new RectangleShape(front,new Extent(seatsHight,seatsWidth),Color.LIGHTRED,false));
        shape.addShape(new RectangleShape(seats,Color.LIGHTBLUE,false));
        shape.addShape(new RectangularBoxShape(rectangle, 30, Color.BLACK,false));
    }
    
    
    
    
    
    /**
     * TODO Comment method
     * @param i
     * @param j
     * @return
     */
    private Rectangle getSeatRectangle(int i, int j) {
        double xOffset=seats.getBottomLeft().getX();
        double yOffset=seats.getBottomLeft().getY();
        return new Rectangle(xOffset+j*seatsWidth,yOffset+i*seatsHight,xOffset+(j+1)*seatsWidth,yOffset+(i+1)*seatsHight);
    }





    public Position[] getDevicePath(DeviceID device, Position newPosition, Position oldPosition) {
        if (!rectangle.contains(newPosition)) throw new IllegalStateException("Position is not in this mobility source");
        Position position=null;
        if (seats.contains(newPosition)){
            position= findFreeSeat(device,newPosition);
        }else
        if (frontPlace.contains(newPosition)){
            if (dozent!=null&&!dozent.equals(device)){
                position=findFreeSeat2(device,newPosition);
            }else{
                freeDevice(device);
                position=frontPlace.getCenter();
                dozent=device;
            }
        }else{
            position=findOtherPlace(device,newPosition);
        }
        devicePositionMap.put(device,position);
        
        return new Position[]{position};
    }
    
    public void removeDevice(DeviceID deviceID,Position oldPosition){
        freeDevice(deviceID);
    }
    
    public ArrivalInfo[] getInitialArrivalInfos(DeviceID[] deviceIDs,double[] enterTimes){
        ArrivalInfo[] infos=new ArrivalInfo[deviceIDs.length];
        if (infos.length==0)return infos;
        int i=0;
        if (seatDozent){
        	infos[0]=new ArrivalInfo(frontPlace.getCenter(),enterTimes[0]);
        	dozent=deviceIDs[0];
        	i++;
        	   
        }
     
        devicePositionMap.put(dozent,frontPlace.getCenter());
        for (;i<deviceIDs.length;i++){
            infos[i]=new ArrivalInfo(sitRandom(deviceIDs[i]),enterTimes[i]);
        }
        return infos;
    }

    /**
     * TODO Comment method
     * @param deviceID
     * @return
     */
    private Position sitRandom(DeviceID deviceID) {
        
        return getDevicePath(deviceID,positionGeneratorSeats.getNext(0),null)[0];
    }





    /**
     * TODO Comment method
     * @param newPosition
     * @return
     */
    private Position findOtherPlace(DeviceID deviceID,Position newPosition) {
        if (!seats.contains(newPosition)&&!frontPlace.contains(newPosition)){
            freeDevice(deviceID);
            
            return newPosition;
        }
        return findOtherPlace(deviceID,positionGenerator.getNext(0));
    }





    /**
     * TODO Comment method
     * @param newPosition
     * @return
     */
    private Position findFreeSeat(DeviceID deviceID,Position newPosition) {
        int[] seat=getSeat(newPosition);

        DeviceID sittingDevice=deviceIDs[seat[0]][seat[1]];
        if (sittingDevice==null||deviceID.equals(sittingDevice)){
            
            return seat(deviceID,seat[0],seat[1]); 
            
//            else for (int i=-1;i<2;i++){
//                for (int j=-1;j<2;j++){
//                    DeviceID sittingDevice=deviceIDs[row+i][collumn+j];
//                    if (sittingDevice==null||sittingDevice.equals(deviceID)){
//                        return seat(row+i,collumn+j);
//                    }
//                }
//            }
        }
        return findFreeSeat2(deviceID,newPosition);
    }





    /**
     * TODO Comment method
     * @param newPosition
     * @return
     */
    private int[] getSeat(Position newPosition) {
        int[] seat=new int[2];
        if (!seats.contains(newPosition)){
            throw new IllegalStateException("");
        }
        seat[0]=deviceIDs.length-1-(int)((seats.getHeight()-(newPosition.getY()-seats.getBottomLeft().getY()))/seatsHight);
        seat[1]=deviceIDs[0].length-1-(int)((seats.getWidth()-(newPosition.getX()-seats.getBottomLeft().getX()))/seatsWidth);
        if (seat[0]<0) seat[0]=0;
        if (seat[1]<0) seat[1]=0;
        return seat;
    }





    /**
     * TODO Comment method
     * @param deviceID
     * @param newPosition
     * @return
     */
    private Position findFreeSeat2(DeviceID deviceID, Position newPosition) {
        for (int i=0;i<deviceIDs.length;i++){
            for (int j=0;j<deviceIDs[i].length;j++){
                DeviceID sittingDevice=deviceIDs[i][j];
                if (sittingDevice==null||deviceID.equals(sittingDevice)){
                    
                    return seat(deviceID,i,j); 
                    
                }
            }
        }
        return findOtherPlace(deviceID,newPosition);
    }









    /**
     * TODO Comment method
     * @param deviceID
     * @param i
     * @param j
     */
    private Position seat(DeviceID deviceID, int i, int j) {
        
        freeDevice(deviceID);
        deviceIDs[i][j]=deviceID;
        //return getSeatRectangle(i,j).getCenter();
        return getSeatRectangle(i,j).getCenter().add(seatPositiongenerator.getNext(0));
    }





    /**
     * TODO Comment method
     * @param deviceID
     */
    private void freeDevice(DeviceID deviceID) {
        Position position=(Position) devicePositionMap.remove(deviceID);
        if (position!=null){
            if (seats.contains(position)){
                int[] seat=getSeat(position);
                deviceIDs[seat[0]][seat[1]]=null;
            }else if (frontPlace.contains(position)){
                dozent=null;
            }
        }
    }





    public DeviceID[] getAddress(Rectangle rectangle) {
        List list=new ArrayList();
        Iterator iterator=devicePositionMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry element = (Entry) iterator.next();
            if (rectangle.contains((Position)element.getValue())){
                list.add(element.getKey());
            }
            
        }
        return (DeviceID[]) list.toArray(new DeviceID[list.size()]);
    }



    public Rectangle getRectangle() {
        return rectangle;
    }

    public Shape getShape() {
        return shape;
    }





	public static LectureRoom createRoom(Rectangle room, int i,DistributionCreator distributionCreator,boolean left,boolean seatDozent) {
		int orient=1;
		if (left) orient=-1;
		Position pos = room.getCenter().add(new Position(room.getWidth()*0.1*orient,0));
		Position lecturer=room.getCenter().add(new Position(-room.getWidth()*0.4*orient,0));
		Rectangle area=new Rectangle(pos, new Extent(room.getWidth()*0.8,room.getHeight()*0.8));
		
		return new LectureRoom(room,area,i,lecturer,distributionCreator,seatDozent);
		
	}



}
