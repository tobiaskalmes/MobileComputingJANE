/*
 * JSPBMConfigRemoteClient.java
 *
 * Created on 2. November 2005, 22:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_trier.jane.jspbm;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.RemoteException;
import de.uni_trier.jane.jspbm.*;

/**
 *
 * @author Stefan Pohl
 */
public class JSPBMConfigRemoteClient {
    
    private JSPBMConfigRemote server = null;
    private static JSPBMConfigRemoteClient _exemplar = null;
    
    /** Creates a new instance of JSPBMConfigRemoteClient */
    private JSPBMConfigRemoteClient() {
        try{
            //Registry registry = LocateRegistry.getRegistry();
            
           
            server = (JSPBMConfigRemote) Naming.lookup("rmi://localhost/"+JSPBMConfigRemote.LOOKUPNAME);
        } catch(Exception e){
            System.err.println("-- Exception while instatiating JSPBMConfigRemoteClient");
            e.printStackTrace();
        }
    }
    
    /** returns the singleton instance of JSPBMConfigRemoteClient */
    public static JSPBMConfigRemoteClient getSingleton(){
        if(_exemplar == null){
            _exemplar = new JSPBMConfigRemoteClient();
        }
        
        return _exemplar;
    }
    
    /** */
    public void joinGroup(int groupNum) throws java.rmi.RemoteException{
        server.joinGroup(groupNum);
    }
    
    public void leaveGroup(int groupNum) throws java.rmi.RemoteException{
        server.leaveGroup(groupNum);
    }
    
    public int[] getJoinedGroups() throws java.rmi.RemoteException{
        return server.getJoinedGroups();
    }
    
    public void setHexStringPosition(String xPos, String yPos) throws java.rmi.RemoteException{
        server.setHexStringPosition(xPos, yPos);
    }
    
    public void setDecStringPosition(String xPos, String yPos) throws java.rmi.RemoteException{
        server.setHexStringPosition(xPos, yPos);
    }
    
    public void setPosition(long xPos, long yPos) throws java.rmi.RemoteException{
        if (server==null) return;
        server.setPosition(xPos, yPos);
    }
    
    public long[] getPosition() throws java.rmi.RemoteException {
        return server.getPosition();
    }
    
}
