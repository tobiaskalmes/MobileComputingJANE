/*
 * JSPBM_Daemon.java
 *
 * Created on November 1, 2005, 4:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.uni_trier.jane.jspbm.daemon;

import de.uni_trier.jane.jspbm.JSPBMConfigRemote;
import de.uni_trier.jane.jspbm.JSPBMConfigRemoteImpl;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.rmi.registry.*;
/**
 *
 * @author dodger
 */
public class JSPBM_Daemon {
    
    /** Creates a new instance of JSPBMDaemon */
    public JSPBM_Daemon() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            // Create and install a security manager
            //if (System.getSecurityManager() == null) {
            //    System.setSecurityManager(new RMISecurityManager());
            //}
            
            
            System.out.println("++ instantiating server object");
            JSPBMConfigRemoteImpl server = new JSPBMConfigRemoteImpl();
            
            System.out.println("++ exporting server object");
            JSPBMConfigRemote stub = (JSPBMConfigRemote) UnicastRemoteObject.exportObject(server);
            
            //Registry registry = LocateRegistry.getRegistry();
            System.out.println("++ binding to "+JSPBMConfigRemote.LOOKUPNAME);
            Naming.rebind("//localhost/"+JSPBMConfigRemote.LOOKUPNAME, stub);
            
            System.out.println("++ JSPBM_Daemon done :-)");
            
        } catch(Exception e){
            System.err.println("-- JSPBM exception " + e.toString());
            e.printStackTrace();
        }
        
    }
    
}

