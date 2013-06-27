/*****************************************************************************
 * 
 * PlatformDeviceID.java
 * 
 * $Id: PlatformDeviceID.java,v 1.1 2007/06/25 07:23:00 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.platform.basetypes; 

import java.net.*;

import de.uni_trier.jane.basetypes.*;


/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PlatformDeviceID extends DeviceID {

	private InetAddress address;


	private static final int INADDRSZ = 4;
	
	/**
	 * Construct a new <code>Address</code> object.
	 * @param address the ip address of the device in <code>java.net,InetAddress</code> representation
	 */
	public PlatformDeviceID(InetAddress address) {
		this.address = address;
	}
	
	/**
	 * Construct a new <code>Address</code> object.
	 * @param address	th ip address of the device in <code>int</code>  representation
	 */
	public PlatformDeviceID(int address) {
		try {
			this.address=Inet4Address.getByAddress(intToAddress(address));
			// TODO: only for compatibiliy with ApplicationSimulation
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the int representation of this address
	 * @return	the <code>int</code> representation
	 */
	public int getIntRepresentation(){
		return addressToInt(address.getAddress());
	}
	
	private int addressToInt(byte[] addr){
		int retaddress=-1;
		if (addr != null) {
		    if (addr.length == INADDRSZ) {
			retaddress = addr[3] & 0xFF;
			retaddress |= ((addr[2] << 8) & 0xFF00);
			retaddress |= ((addr[1] << 16) & 0xFF0000);
			retaddress |= ((addr[0] << 24) & 0xFF000000);
		    } 
		}
		return retaddress;
	 }
	
	private byte[] intToAddress(int addr){
		byte[] retaddress = new byte[INADDRSZ];

		retaddress[0] = (byte) ((addr >>> 24) & 0xFF);
		retaddress[1] = (byte) ((addr >>> 16) & 0xFF);
		retaddress[2] = (byte) ((addr >>> 8) & 0xFF);
		retaddress[3] = (byte) (addr & 0xFF);
		return retaddress;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String str = address.toString();
		int slashPos = str.indexOf('/');
		if (slashPos != -1) {
			return str.substring(slashPos+1);
		} else {
			return str;
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return address.hashCode();
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other.getClass() != getClass()) {
			return false;
		}
		PlatformDeviceID oth = (PlatformDeviceID) other;
		return address.equals(oth.address);
	}
	
	/**
	 * Returns the <code>java.net.InetAddress</code> representation of the device address
	 * @return	InetAddress
	 */
	public InetAddress getInetAddress(){
		return address;
	}

   /** compares two Addresses
	* returns -1 if other is ower than this returns 1  if other is greater than
	* this returns 0  if other is equal
	* @param	  		o
	* @return int		-1, 1 or 0
	*/
	public int compareTo(Object o) {
	    PlatformDeviceID other=(PlatformDeviceID)o;
		if (equals(other)) return 0;
		for (int i=0;i<4;i++){	
			if (address.getAddress()[i]<other.address.getAddress()[i]){
				return -1;
			}else if(address.getAddress()[i]>other.address.getAddress()[i]){
				return 1;
			}
		}
		return 0;
	   
	}

/* (non-Javadoc)
 * @see de.uni_trier.ssds.service.DeviceID#getSize()
 */
	public int getCodingSize() {
	    
	    return 4*8;
	}
}
