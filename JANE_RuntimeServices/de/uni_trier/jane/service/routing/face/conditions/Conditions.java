/*****************************************************************************
* 
* $Id: Conditions.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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
/*
 * @author Stefan Peters
 * Created on 19.04.2005
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * This object is used to store all needed informations and conditions for facerouting.
 * It is more clearly laid out then assigning this informations and conditions direkt to facerouting
 * @author Stefan Peters
 */
public class Conditions {
//	private FinishCondition finishCondition;
	private StartCondition startCondition;
	private BreakCondition breakCondition;
	private CrossingCondition crossingCondition;
	private DominatingSetStartCondition dominatingSetStartCondition;
	private StepSelector weightCondition;
	private ResumeGreedyCondition resumeGreedyCondition;
	private TurnCondition turnCondition;
	
	/**
	 * Creates all needed Conditions for simple routing algorithms.
	 * @param destination
	 * @param clockwise
	 * @param crossing
	 */
	public Conditions(boolean clockwise, boolean crossing){
//		finishCondition=new FinishConditionImpl(destination);
		startCondition=new SimpleStartCondition(clockwise);
		breakCondition=new BreakConditionImpl();
		crossingCondition=new SimpleCrossingCondition(crossing);
		dominatingSetStartCondition=new DominatingSetStartConditionImpl();
		weightCondition=new HopCountWeightCondition();
		resumeGreedyCondition=new ResumeGreedyConditionImpl();//SimpleResumeGreedyCondition();
		turnCondition=new SimpleTurnCondition();
	}
	
	
	
	/**
	 * @return Returns the breakCondition.
	 */
	public BreakCondition getBreakCondition() {
		return breakCondition;
	}
	/**
	 * @param breakCondition The breakCondition to set.
	 */
	public void setBreakCondition(BreakCondition breakCondition) {
		this.breakCondition = breakCondition;
	}
	/**
	 * @return Returns the crossingCondition.
	 */
	public CrossingCondition getCrossingCondition() {
		return crossingCondition;
	}
	/**
	 * @param crossingCondition The crossingCondition to set.
	 */
	public void setCrossingCondition(CrossingCondition crossingCondition) {
		this.crossingCondition = crossingCondition;
	}
	/**
	 * @return Returns the dominatingSetStartCondition.
	 */
	public DominatingSetStartCondition getDominatingSetStartCondition() {
		return dominatingSetStartCondition;
	}
	/**
	 * @param dominatingSetStartCondition The dominatingSetStartCondition to set.
	 */
	public void setDominatingSetStartCondition(
			DominatingSetStartCondition dominatingSetStartCondition) {
		this.dominatingSetStartCondition = dominatingSetStartCondition;
	}
	/**
	 * @return Returns the finishCondition.
	 * @deprecated  better create a finish condition for each routing task
	 */
//	public FinishCondition getFinishCondition() {
//		return finishCondition;
//	}
	/**
	 * @param finishCondition The finishCondition to set.
	 * @deprecated better create a finish condition for each routing task
	 */
//	public void setFinishCondition(FinishCondition finishCondition) {
//		this.finishCondition = finishCondition;
//	}
	/**
	 * @return Returns the resumeGreedyCondition.
	 */
	public ResumeGreedyCondition getResumeGreedyCondition() {
		return resumeGreedyCondition;
	}
	/**
	 * @param resumeGreedyCondition The resumeGreedyCondition to set.
	 */
	public void setResumeGreedyCondition(
			ResumeGreedyCondition resumeGreedyCondition) {
		this.resumeGreedyCondition = resumeGreedyCondition;
	}
	/**
	 * @return Returns the startCondition.
	 */
	public StartCondition getStartCondition() {
		return startCondition;
	}
	/**
	 * @param startCondition The startCondition to set.
	 */
	public void setStartCondition(StartCondition startCondition) {
		this.startCondition = startCondition;
	}
	/**
	 * @return Returns the turnCondition.
	 */
	public TurnCondition getTurnCondition() {
		return turnCondition;
	}
	/**
	 * @param turnCondition The turnCondition to set.
	 */
	public void setTurnCondition(TurnCondition turnCondition) {
		this.turnCondition = turnCondition;
	}
	/**
	 * @return Returns the weightCondition.
	 */
	public StepSelector getWeightCondition() {
		return weightCondition;
	}
	/**
	 * @param weightCondition The weightCondition to set.
	 */
	public void setWeightCondition(StepSelector weightCondition) {
		this.weightCondition = weightCondition;
	}

}
