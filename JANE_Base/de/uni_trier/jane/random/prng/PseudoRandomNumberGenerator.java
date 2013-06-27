package de.uni_trier.jane.random.prng;

/**
 * This interface describes the source of random used by the simulation envorinment.
 * @author Hannes Frey
 */
public interface PseudoRandomNumberGenerator {

	/**
	 * Get the next uniformly distributed double value.
	 * @return a value in [0,1)
	 */
	public double nextDouble();

	/**
	 * Get the next uniformly distributed int value.
	 * @param max the maximum integer value (excluding)
	 * @return a value in {0,1,...,max-1}
	 */
	public int nextInt(int max);

	/**
	 * Get the next uniformly distributed long value.
	 * @return a value between Long.MIN_VALUE and Long.MAX_VALUE
	 */
	public long nextLong();

	/**
	 * Get the next normal distributed double value.
	 * @return a normal distributed value
	 */
	public double nextGaussian();

}
