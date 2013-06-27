package de.uni_trier.jane.random.prng;


/**
 * The <code>DistribitionCreator</code> needs this factory in order to
 * create the source of random.
 * @author Hannes Frey
 * @see de.uni_trier.jane.random.DiscreteDistribution
 */
public interface PseudoRandomNumberGeneratorFactory {

	/**
	 * Create a new pseudo random number generator.
	 * @param seed the initial seed value
	 * @return the generator
	 */
	public PseudoRandomNumberGenerator getPseudoRandomNumberGenerator(long seed);

}
