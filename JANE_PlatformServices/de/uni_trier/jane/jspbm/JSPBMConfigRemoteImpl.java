/*
 * JSPBMConfigRemoteImpl.java
 *
 * Created on 30. Oktober 2005, 23:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_trier.jane.jspbm;
import java.rmi.*;
/**
 *
 * @author dodger
 */
public class JSPBMConfigRemoteImpl  implements JSPBMConfigRemote{
    private JSPBMConfig configObj;
    
    /** Creates a new instance of JSPBMConfigRemoteImpl */
    public JSPBMConfigRemoteImpl() throws RemoteException{
        super();
        this.configObj = JSPBMConfig.getSingleton();
    }
    
    public void joinGroup(int groupNum) throws java.rmi.RemoteException{
        this.configObj.joinGroup(groupNum);
    }
    
    public void leaveGroup(int groupNum) throws java.rmi.RemoteException {
        this.configObj.leaveGroup(groupNum);
    }
    
    public int[] getJoinedGroups() throws java.rmi.RemoteException {
        return this.configObj.getJoinedGroups();
    }
    
    
    public void setHexStringPosition(String xPos, String yPos) throws java.rmi.RemoteException{
        this.configObj.setPos(xPos, yPos);
    }
    
    public void setDecStringPosition(String xPos, String yPos) throws java.rmi.RemoteException{
        this.configObj.setPos(HexHandler.getHexString(xPos), HexHandler.getHexString(yPos));
    }
    
    public void setPosition(long xPos, long yPos) throws java.rmi.RemoteException{
        this.configObj.setPos(HexHandler.getHexString(xPos), HexHandler.getHexString(yPos));
    }
    
    public long[] getPosition() throws java.rmi.RemoteException {
        return this.configObj.getPos();
    }
    
}//class JSPBMConfigRemoteImpl
