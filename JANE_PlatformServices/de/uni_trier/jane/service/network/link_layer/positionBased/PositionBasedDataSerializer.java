/*****************************************************************************
 * 
 * 
 * PositionBasedDataSerializer.java
 * $Id: PositionBasedDataSerializer.java,v 1.1 2007/06/25 07:23:46 srothkugel Exp $
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
package de.uni_trier.jane.service.network.link_layer.positionBased; 


import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.service.network.link_layer.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class PositionBasedDataSerializer implements DataSerializer {
    
    private DataSerializer dataSerializer;
    private Position position;
    
    
    
    /**
     * Constructor for class <code>PositionBasedDataSerializer</code>
     * @param serializer
     */
    public PositionBasedDataSerializer(DataSerializer serializer) {
        // TODO Auto-generated constructor stub
        dataSerializer = serializer;
        
    }

    public byte[] getData(LinkLayerMessage message)
            throws DataSerializerException {

        byte[] msg=dataSerializer.getData(message);
        byte[] posmsg=new byte[msg.length+8];
        toByte(posmsg,0,(int)position.getX());
        toByte(posmsg,4,(int)position.getY());
        for (int i=0;i<msg.length;i++){
            posmsg[i+8]=msg[i];
        }
        
        //return new String(buf, charPos, (64 - charPos));
        
        return posmsg;
    }

    public LinkLayerMessage getMessage(byte[] data)
            throws DataSerializerException {
        byte[] msg=new byte[data.length-8];
        for (int i=8;i<data.length;i++){
            msg[i-8]=data[i];
        }
        return dataSerializer.getMessage(msg);
    }
    
    private static void toByte(byte[] b, int offset, int val){
        //byte[] b=new byte[4];
        b[3+offset] = (byte) (val >>> 0);
        b[2+offset] = (byte) (val >>> 8);
        b[1+offset] = (byte) (val >>> 16);
        b[0+offset] = (byte) (val >>> 24);
        //return b;
    }
    
    private static int getInt(byte[] b, int offset){
        return ((b[ 3+offset] & 0xFF) << 0) +
           ((b[ 2+offset] & 0xFF) << 8) +
           ((b[ 1+offset] & 0xFF) << 16) +
           ((b[ 0+offset] & 0xFF) << 24);
//        int tempInt=0;
//        for (int i = 0; i < 4; i++) {
//   
//            tempInt =tempInt <<8;
//            tempInt |= buffer[i];
//            //tempInt &= 0xff;
//        }
//        return tempInt;
    }

    /**
     * TODO Comment method
     * @param data
     * @return
     */
    public Position getPosition(byte[] data) {

        return new Position(getInt(data,0),getInt(data,4));
    }

    /**
     * TODO Comment method
     * @param myPosition
     */
    public void setPosition(Position position) {
        this.position=position;
        
    }

    /**
     * TODO Comment method
     * @param args
     */
//    public static void main(String[] args) {
//        //for (int i=-1;i<=1;i++){
//        for (int i=Integer.MIN_VALUE;i<Integer.MAX_VALUE;i++){
//            byte[] bytes=toByte(i);
//            if (i==0) System.out.println("yipp");
//            int j=getInt(bytes);
//            if (i!=j){
//                System.out.print(i+":"+j+":");//+bytes[0]+"."+bytes[1]+"."+bytes[2]+"."+bytes[3]);
//                printBits(bytes[0]);
//                System.out.print(".");
//                printBits(bytes[1]);
//                System.out.print(".");
//                printBits(bytes[2]);
//                System.out.print(".");
//                printBits(bytes[3]);
//                System.out.println();
//            }
//            
//        }
//
//    }
//    
//    public static void printBits(byte b){
//        for (int i=0;i<8;i++){
//            if ((b&0x80)==0x80){
//                System.out.print(1);    
//            }else{
//                System.out.print(0);
//            }
//            b<<=1;
//            
//        }
//        
//    }

}
