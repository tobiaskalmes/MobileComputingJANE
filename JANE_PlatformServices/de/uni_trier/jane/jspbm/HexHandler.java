/*
 * HexHandler.java
 *
 * Created on 6. Oktober 2005, 14:21
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package de.uni_trier.jane.jspbm;
import java.util.regex.*;

/**
 *
 * @author Stefan Pohl
 */
public class HexHandler {
    private static  Pattern hexString;
    private static  Pattern decString;
    private static  Pattern hexAlpha;
    private static  Pattern hexNum;
    
    private static final long a = 10L;
    private static final long b = 11L;
    private static final long c = 12L;
    private static final long d = 13L;
    private static final long e = 14L;
    private static final long f = 15L;
    
    /**
     * Creates a new instance of HexHandler
     */
    public HexHandler() {
        try{
            hexString = Pattern.compile("[-?a-fA-F0-9]*");
            decString = Pattern.compile("[-?0-9]");
            hexAlpha = Pattern.compile("[-?+a-fA-F]*");
            hexNum = Pattern.compile("[-?0-9]*");
        } catch ( Exception e){
            System.err.println("++ Initialization of Class HexHandler");
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public static boolean isHexString(String input){
        Matcher match = hexString.matcher(input);
        return match.matches();
    }
    
    public static boolean isDecString(String input){
        Matcher match = decString.matcher(input);
        return match.matches();
    }
    
    public static boolean isHexAlpha(String input){
        Matcher match = hexAlpha.matcher(input);
        return match.matches();
    }
    
    public static boolean isHexNum(String input){
        Matcher match = hexNum.matcher(input);
        return match.matches();
    }
    
    /** Eingabe der Stringrepraesentation einer Hexadezimalzahl ohne fuerhendes "0x"!
     * Rueckgabe: Wert der Long */
    public static long longVal(String in){
        String input = (in.trim()).toLowerCase();
        String current = "";
        long result = 0L;
        boolean negative = false;
        
        //funktioniert
        if (! isHexString(input)){
            System.err.println("-- "+input+"is not a hexString!");
            System.exit(1);
        }
        
        if( (input.substring(0, 1)).equals("-") ){
            //           System.out.println("-- is negative!");
            negative = true;
            input = input.substring(1);
            
        }
        
        
        
        try{
            int j = 0;
            for(int i = input.length() - 1; i >= 0; i--){
                current = input.substring(i, i+1);
                
                if(isHexNum(current)){
                    result += (Integer.parseInt(current)) * Math.pow(16, j);
                } else{
                    if (current.equals("a")) result += a * Math.pow(16, j);
                    else if (current.equals("b")) result += b * Math.pow(16, j);
                    else if (current.equals("c")) result += c * Math.pow(16, j);
                    else if (current.equals("d")) result += d * Math.pow(16, j);
                    else if (current.equals("e")) result += e * Math.pow(16, j);
                    else if (current.equals("f")) result += f * Math.pow(16, j);
                }
                
                j++;
            }
        } catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        
        if (negative){
            result *= -1;
        }
        
        return result;
    }
    
    /** Eingabe der Stringrepraesentation einer Hexadezimalzahl ohne fuerhendes "0x"!
     * Rueckgabe: Wert der Integer */
    public static int intVal(String in){
        String input = (in.trim()).toLowerCase();
        String current = "";
        int result = 0;
        boolean negative = false;
        
        //funktioniert
        if (! isHexString(input)){
            System.err.println("-- "+input+"is not a hexString!");
            System.exit(1);
        }
        
        if( (input.substring(0, 1)).equals("-") ){
            //           System.out.println("-- is negative!");
            negative = true;
            input = input.substring(1);
            
        }
        
        
        
        try{
            int j = 0;
            for(int i = input.length() - 1; i >= 0; i--){
                current = input.substring(i, i+1);
                
                if(isHexNum(current)){
                    result += (Integer.parseInt(current)) * Math.pow(16, j);
                } else{
                    if (current.equals("a")) result += (int) a * Math.pow(16, j);
                    else if (current.equals("b")) result += (int) b * Math.pow(16, j);
                    else if (current.equals("c")) result += (int) c * Math.pow(16, j);
                    else if (current.equals("d")) result += (int) d * Math.pow(16, j);
                    else if (current.equals("e")) result += (int) e * Math.pow(16, j);
                    else if (current.equals("f")) result += (int) f * Math.pow(16, j);
                }
                
                j++;
            }
        } catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        
        if (negative){
            result *= -1;
        }
        
        return result;
    }
    
    /** Input: String  representation of a decimal integer*/
    public static String getHexString(String in){
        boolean isNegative = false;
        if (!isDecString(in)){
            System.err.println("-- "+in+" is not a decString");
            System.exit(1);
        }
        
        if (in.startsWith("-")){
            isNegative = true;
            in = in.substring(1);
        }
        
        int tmp = Integer.valueOf(in).intValue();
         if (!isNegative){
            return Integer.toHexString(tmp);
        }
        else{
            return "-"+Integer.toHexString(tmp);
        }
    }
    
     /** Input: String  representation of a decimal integer*/
    public static String getHexString(int in){
        boolean isNegative = false;
        
        if (in < 0){
            isNegative = true;
            in = Math.abs(in);
        }
        
        if (!isNegative){
            return Integer.toHexString(in);
        }
        else{
            return "-"+Integer.toHexString(in);
        }
        
    }
   
      /** Input: String  representation of a decimal long integer*/
    public static String getHexString(long in){
        boolean isNegative = false;
        
        if (in < 0){
            isNegative = true;
            in = Math.abs(in);
        }
        
        if (!isNegative){
            return Long.toHexString(in);
        }
        else{
            return "-"+Long.toHexString(in);
        }
        
    }
   
    
}// public class HexHandler()


