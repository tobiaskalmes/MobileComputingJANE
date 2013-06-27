/*****************************************************************************
 * 
 * DefaultHybridParameters.java}
 * 
 * $Id: DefaultHybridParameters.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.hybrid.remote;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.SimulationDeviceID;
import de.uni_trier.jane.console.*;
import de.uni_trier.jane.random.DistributionCreator;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class DefaultHybridParameters implements HybridParameters {
    
    private DistributionCreator distributionCreator;
    private InetAddress jANEHybridServerHost;
    private DeviceID deviceID;
    private Console defaultConsole;
    private boolean forceDeviceSelect;
    
 
    /**
     * Constructor for class DefaultHybridParameters 
     * @throws UnknownHostException 
     *
     * 
     */
    public DefaultHybridParameters() throws UnknownHostException {
        distributionCreator= new DistributionCreator(
				new long[] { 2992515376L, 2278929704L,
						2220834997L, 2580257810L, 4102705091L, 2323110552L,
						3932568423L, 2726271371L, 2915158958L, 3209013225L,
						2451125634L, 2790182054L, 3174286243L, 2946319777L,
						2658270748L, 3798086406L, 3721595981L, 4138230070L,
						2894504813L, 4198898146L, 4209437256L, 3353908892L,
						3777241970L, 4012596588L, 3700701375L, 3006727708L,
						3021678370L, 2695257626L, 3940206439L, 3390587984L,
						3689292477L, 2791185772L, 3119876180L, 3809634552L,
						2357978999L, 3815754978L, 2150105929L, 4222981334L,
						2743901562L, 2972333909L, 4266298526L, 3764380222L,
						4166426483L, 2191737416L, 3459612483L, 3589411244L,
						4235161741L, 2692700196L, 3049326775L, 2240639017L,
						2214260825L, 4100106086L, 3290868482L, 4153290466L,
						4043026814L, 3345470120L, 4257932121L, 2768328448L,
						3569880499L, 2567379269L, 4115025113L, 2425754172L,
						3955518023L, 2784295484L, 2652223495L, 3251235549L,
						4283747673L, 3969610748L, 2483344807L, 3831027129L,
						2482004287L, 3654719834L, 3069436291L, 2295069938L,
						3781709309L, 3453834308L, 4042917813L, 3528137750L,
						3999032117L, 2692473174L, 3763587115L, 3057809419L,
						2467004861L, 2647401658L, 2305545939L, 4095494918L,
						3173446098L, 3942105797L, 2871190919L, 2888677175L,
						2398563468L, 2168884391L, 2375568598L, 2813259816L,
						3505883198L, 3952497072L, 2766111656L, 4218410491L,
						3734243328L, 2292209605L });
        jANEHybridServerHost=InetAddress.getByName("localhost");
        defaultConsole=new SystemOutConsole();
        
    }
   /**
    * Returns the id of the simulated device to be connected to
    * @return the deviceID
    */
    public DeviceID getDeviceID() {
        return deviceID;
    }
    public void setDeviceID(DeviceID deviceID) {
        setDeviceID(deviceID,true);
    }
    
    public void setDeviceID(DeviceID deviceID, boolean force) {
        this.deviceID=deviceID;
        forceDeviceSelect=force;
    }
    
    /**
     * @return Returns the forceDeviceSelect.
     */
    public boolean isForceDeviceSelect() {
        return forceDeviceSelect;
    }

    public DistributionCreator getDistributionCreator() {
        return distributionCreator;
    }
    public void setDistributionCreator(DistributionCreator distributionCreator) {
        this.distributionCreator = distributionCreator;
    }
    public InetAddress getJANEHybridServerHost() {
        return jANEHybridServerHost;
    }
    public void setJANEHybridServerHost(String hybridServerHost) throws UnknownHostException {
        jANEHybridServerHost = InetAddress.getByName(hybridServerHost);
    }
    public void setJANEHybridServerHost(InetAddress serverAddress) {
    	jANEHybridServerHost=serverAddress;
    	
    }
   
    
    
    
    public Console getDefaultConsole() {
        
        return defaultConsole;
    }
    
  
    public void setDefaultConsole(Console defaultConsole) {
        this.defaultConsole = defaultConsole;
    }
}
