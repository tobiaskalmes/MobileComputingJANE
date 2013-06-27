package de.uni_trier.jane.random.prng;

import edu.cornell.lassp.houle.RngPack.*;

/**
 * This class serves as a wrapper from <code>PseudoRandomNumberGenerator</code> to
 * <code>RandomElement</code>. Is is needed in order to use
 * <code>PseudoRandomNumberGenerator</code> in combination with the
 * <code>RandomShuffle</code> class.
 * @author Hannes Frey
 * @see edu.cornell.lassp.houle.RngPack.RandomElement
 * @see edu.cornell.lassp.houle.RngPack.RandomShuffle
 * @see de.uni_trier.jane.random.prng.PseudoRandomNumberGenerator
 */
public class PRNGToRandomElement extends RandomElement {

	private PseudoRandomNumberGenerator pseudoRandomNumberGenerator;
	
	public PRNGToRandomElement(PseudoRandomNumberGenerator generator) {
		pseudoRandomNumberGenerator = generator;
	}

	public double raw() {
		return pseudoRandomNumberGenerator.nextDouble();
	}

}
