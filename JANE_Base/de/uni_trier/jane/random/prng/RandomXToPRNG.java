package de.uni_trier.jane.random.prng;

import ch.fourmilab.hotbits.randomX.*;

/**
 * This class serves as a wrapper from <code>randomX</code> to
 * <code>PseudoRandomNumberGenerator</code>.
 * @author Hannes Frey
 */
public class RandomXToPRNG implements PseudoRandomNumberGenerator {

	private randomX random;

	/**
	 * Create a new Wrapper.
	 * @param random the wrapped object.
	 */
	public RandomXToPRNG(randomX random) {
		this.random = random;
	}
	
	public double nextDouble() {
		return random.nextDouble();
	}

	public int nextInt(int i) {
		return Math.abs(random.nextInt() % i);
	}

	public long nextLong() {
		return random.nextLong();
	}

	public double nextGaussian() {
		return random.nextGaussian();
	}

}
