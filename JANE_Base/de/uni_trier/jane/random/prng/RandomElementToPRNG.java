package de.uni_trier.jane.random.prng;

import edu.cornell.lassp.houle.RngPack.*;

/**
 * This class serves as a wrapper from <code>RandomElement</code> to
 * <code>PseudoRandomNumberGenerator</code>.
 * @author Hannes Frey
 */
public class RandomElementToPRNG implements PseudoRandomNumberGenerator {

	private RandomElement randomElement;

	/**
	 * Construct a new wrapper.
	 * @param element the wrapped object.
	 */
	public RandomElementToPRNG(RandomElement element) {
		randomElement = element;
	}

	public double nextDouble() {
		double result = 1.0;
		while(result >= 1.0) {
			result = randomElement.raw();
		}
		return result;
	}

	public int nextInt(int i) {
		return randomElement.choose(i+1)-1;
	}

	public long nextLong() {
		double raw = randomElement.raw();
		boolean positive = randomElement.coin();
		long result = (long)(Long.MAX_VALUE * raw);
		if(positive) {
			return result;
		}
		else {
			return - result;
		}
	}

	public double nextGaussian() {
		return randomElement.gaussian();
	}

}
