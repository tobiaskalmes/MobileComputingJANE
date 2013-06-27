/*
 * Created on 28.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.link_layer.winfra;

import java.util.ArrayList;
import java.util.Random;

import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;

/**
 * @author christian.hiedels
 *
 * This Class returns random Positions where BaseStations should be placed.
 * The Positions are oriented on a grid. The number of possible Positions is determined through the Extent of the Plane and the range_modifier.
 * TODO: Set basestation with own created distribution... with setsimulationparameters.. (.... random long)
 */
public class BaseStationPositions {
	
	private static double range_modifier = 1;	// -> 1: 16 possible Positions / -> 0.5: 81 possible Positions 
//	private static int overlayDistance = (int)(WinfraBaseStation.SENDING_RANGE * range_modifier);
	private static int overlayDistance = (int)(100 * range_modifier);

//	private static int[] bsp =	// 10 BaseStationPositions
//		{1, 1,
//			3,1,
//			5,1,
//			2,2,
//			4,2,
//			1,3,
//			3,3,
//			5,3,
//			2,4,
//			4,4};

	private static int counter = 0;

	/**
	 * Get an array of Positions depending on the number of Devices. It selects the Positions from a pre calculated set of possible Positions
	 * which are determined based on a grid, so that each base station has the same distance to each other.
	 */
	public static Position[] getPositions( Extent extent, int numberOfDevices ) {
		int xCount = (int) (extent.getWidth() / overlayDistance);	// number of bs in the x direction
		int yCount = (int) (extent.getHeight() / overlayDistance);	// number of bs in the y direction

		int loopCount = xCount*yCount;
		int counter = 0;
		Position possiblePositions[] = new Position[(xCount-1)*(yCount-1)];
		if( numberOfDevices > (xCount-1)*(yCount-1) ) {
			throw new IllegalStateException("The number of BaseStations ("+numberOfDevices+") may not be higher than the number of possible Base Station Positions ("+possiblePositions.length+")!");
		}
		
		Position positions[] = new Position[numberOfDevices];
		for( int x = 1; x < xCount; x++ ) {
			for( int y = 1; y < yCount; y++ ) {
				possiblePositions[counter++] = new Position(x*overlayDistance, y*overlayDistance);
			}
		}
		// get a random selection out the calculated possible positions
		counter = 0;
		Random ranGen = new Random();
		ArrayList pPosArray = new ArrayList();
		for( int copy = 0; copy < possiblePositions.length; copy++ ) {
			pPosArray.add( possiblePositions[copy] );
		}

		for(int i = 0; i < numberOfDevices; i++ ) {
			int random = ranGen.nextInt(pPosArray.size());
			
			positions[counter++] = (Position) pPosArray.remove( random );
		}
		return positions;
	}
	
//	/**
//	 * Returns a Position or null, if no Position is left
//	 * @param extent The Area on which BaseStations should be placed
//	 * @return A Position or null
//	 */
//	public static Position getNextBSP( Extent extent ) {
//		if( counter > 20 ) return null;
//		Position pos = new Position(extent.getWidth()/6*bsp[counter], extent.getHeight()/4*bsp[counter+1]);
//		counter+=2;
//		
//		return pos; 
//	}
}
