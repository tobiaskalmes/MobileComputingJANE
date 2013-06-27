package de.uni_trier.jane.random.seed;

/**
 * This interface ist used by the <code>DistributionCreator</code> in order to create its
 * random number generators.
 * @author Hannes Frey
 * @see de.uni_trier.jane.random.DistributionCreator
 */
public interface SeedGenerator {

	/**
	 * Get the next seed value.
	 * @return the seed value
	 */
	public long getNext();
	
}
