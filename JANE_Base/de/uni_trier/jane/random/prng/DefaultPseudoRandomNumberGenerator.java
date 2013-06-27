package de.uni_trier.jane.random.prng;

import java.util.*;

/**
 * This default implementation of <code>PseudoRandomNumberGenerator</code>
 * utilizes the standard Java pseudo random number generator implemenation.
 * @author Hannes Frey
 */
public class DefaultPseudoRandomNumberGenerator implements
		PseudoRandomNumberGenerator {

	private Random random;

	/**
	 * Create a new random number generator
	 * @param seed the seed value
	 */
	public DefaultPseudoRandomNumberGenerator(long seed) {
		random = new Random(seed);
	}
	
	public double nextDouble() {
		return random.nextDouble();
	}

	public int nextInt(int max) {
		return random.nextInt(max);
	}

	public long nextLong() {
		return random.nextLong();
	}

	public double nextGaussian() {
		return random.nextGaussian();
	}

}
