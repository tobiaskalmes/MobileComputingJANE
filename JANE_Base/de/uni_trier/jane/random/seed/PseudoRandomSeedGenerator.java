package de.uni_trier.jane.random.seed;

import de.uni_trier.jane.random.prng.*;

/**
 * This seed generator generates its seed values from a given pseudo random number
 * generator. This can be used in order to feed a different pseudo random number
 * generator implementation with appropriate seed values.
 * @author Hannes Frey
 */
public class PseudoRandomSeedGenerator implements SeedGenerator {

	private PseudoRandomNumberGenerator pseudoRandomNumberGenerator;

	/**
	 * Create the seed generator.
	 * @param generator the pseudo random number generator used to create the seed values.
	 */
	public PseudoRandomSeedGenerator(PseudoRandomNumberGenerator generator) {
		pseudoRandomNumberGenerator = generator;
	}

	public long getNext() {
		return pseudoRandomNumberGenerator.nextLong();
	}

}
