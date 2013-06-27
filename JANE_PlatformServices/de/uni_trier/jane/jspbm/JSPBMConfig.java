/*
 * JSPBMConfig.java
 *
 * Created on 30. Oktober 2005, 21:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


/**
 *
 * @author dodger
 */
package de.uni_trier.jane.jspbm;

import java.lang.*;
import java.io.*;
import java.util.*;

public class JSPBMConfig {
    
    private  String joinFilePath = "/proc/spbm/join";
    private String leaveFilePath = "/proc/spbm/leave";
    private String posFilePath = "/proc/spbm/pos";
    
    private File joinFile;
    private File leaveFile;
    private File posFile;
    
    private static JSPBMConfig _exemplar = null;
    
    
    /** Creates a new instance of JSPBMConfig */
    private JSPBMConfig() {
        
        /*
         * Initialisieren der Konfigurations Dateien des Kernel Moduls
         */
        try{
            joinFile = new File(joinFilePath);
            if (!( joinFile.exists() && joinFile.canWrite() && joinFile.canRead() ) ){
                System.err.println("-- File: \""+joinFilePath+"\" doesn't exist, or has wrong permissions\n");
                System.exit(-1);
            }
            
            leaveFile = new File(leaveFilePath);
            if (!( leaveFile.exists() && leaveFile.canWrite() && leaveFile.canRead() ) ){
                System.err.println("-- File: \""+leaveFilePath+"\" doesn't exist, or has wrong permissions\n");
                System.exit(-1);
            }
            
            posFile = new File(posFilePath);
            if (!( posFile.exists() && posFile.canRead() && posFile.canWrite() ) ){
                System.err.println("-- File: \""+posFilePath+"\" doesn't exist, or has wrong permissions\n");
                System.exit(-1);
            }
            
        } catch( SecurityException se){
            System.err.println("-- Initialization of spbm kernel module config files\n");
            se.printStackTrace();
        } catch( Exception e){
            e.printStackTrace();
        }
        
    }//public JSPBMConfig()
    
    /**  */
    public static JSPBMConfig getSingleton(){
        if (_exemplar == null){
            _exemplar = new JSPBMConfig();
        }
        return _exemplar;
    }
    
    
    /*
     * Klassenfunktionen zum Auslesen und schreiben der Konfigurationsdateien
     */
    
    /** writes the string-representation of the groupnumber into the file /proc/spbm/join. */
    public void joinGroup(int groupNum){
        try{
            FileWriter outWriter = new FileWriter(joinFile);
            outWriter.write( Integer.toString(groupNum) );
            outWriter.close();
        } catch(Exception e){
            System.err.println("-- Error writing /proc/spbm/join");
            e.printStackTrace();
            System.exit(-2);
        }
    } //public void joinGroup()
    
    /** @return an Array of ints or the null-pointer if no groups were joined   */
     public int[] getJoinedGroups(){
        int[] result = null;
        try{
            
            System.out.println("++ reading /proc/spbm/join");
            BufferedReader buffInReader = new BufferedReader(new InputStreamReader( new FileInputStream(joinFile) ));
            String[] line = (buffInReader.readLine()).split(" ");
            buffInReader.close();
            
            System.out.println();
            if(line.length > 2){
                result = new int[line.length - 2];
                
                for(int i=2; i< line.length; i++){
                    result[i-2] = Integer.valueOf(line[i]).intValue();
                }
            }
            
            
        } catch(Exception e){
            System.err.println("-- Error reading /proc/spbm/join");
            e.printStackTrace();
            System.exit(-2);
        }
        
        return result;
    }//public void getJoinedGroups()
     
  
    
    /** */
    public void leaveGroup(int groupNum){
        try{
            FileOutputStream outStream = new FileOutputStream(leaveFile);
            outStream.write( (Integer.toString(groupNum)).getBytes() );
            outStream.close();
        } catch(Exception e){
            System.err.println("-- Error writing /proc/spbm/leave");
            e.printStackTrace();
            System.exit(-2);
        }
    }//public void leaveGroup()
    
    
    public long[] getPos(){
        long[] result = { 0, 0};
        String tmp;
        String[] tmp2;
        
        try{
            BufferedReader buffInReader = new BufferedReader(new InputStreamReader( new FileInputStream(posFile) ));
            tmp = buffInReader.readLine();
            buffInReader.close();
            tmp2 = tmp.split(" ");
            
            result[0] = Long.parseLong(tmp2[1]);
            result[1] = Long.parseLong(tmp2[3]);
        } catch(Exception e){
            System.out.println("-- Error reading /proc/spbm/pos");
            e.printStackTrace();
        }
        
        return result;
    }
    
    public void setPos(String x, String y){
        String pos = x +" "+y;
        
        try{
            FileWriter outWriter = new FileWriter(posFile);
            outWriter.write( pos );
            outWriter.close();
        } catch(Exception e){
            System.out.println("-- Error Writing "+posFilePath);
        }
    }
    
  
    
}//public class JSPBMConfig