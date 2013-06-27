/*
 * JSPBMConfigRemote.java
 *
 * Created on 30. Oktober 2005, 21:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_trier.jane.jspbm;

/**
 *
 * @author dodger
 */
import java.rmi.*;
import java.util.*;
public interface JSPBMConfigRemote extends java.rmi.Remote{

	public void joinGroup(int groupNum) throws java.rmi.RemoteException;
		
	public void leaveGroup(int groupNum) throws java.rmi.RemoteException;
	
	public int[] getJoinedGroups() throws java.rmi.RemoteException;
	
	public void setHexStringPosition(String xPos, String yPos) throws java.rmi.RemoteException;
        public void setDecStringPosition(String xPos, String yPos) throws java.rmi.RemoteException;
	public void setPosition(long xPos, long yPos) throws java.rmi.RemoteException;
        
        public long[] getPosition() throws java.rmi.RemoteException;
        
	public final static String LOOKUPNAME = "JSPBMConfigRemote";
}


