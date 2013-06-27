package de.uni_trier.jane.random.prng;


/**
 * This is the default implementation of the <code>PseudoRandomNumberGeneratorFactory</code>.
 * It retrns the standard Java implementation of a random number generator.
 * @author Hannes Frey
 */
public class DefaultPseudoRandomNumberGeneratorFactory implements
		PseudoRandomNumberGeneratorFactory {

	public PseudoRandomNumberGenerator getPseudoRandomNumberGenerator(long seed) {
		return new DefaultPseudoRandomNumberGenerator(seed);
	}

}
