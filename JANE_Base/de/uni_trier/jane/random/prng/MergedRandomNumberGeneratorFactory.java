package de.uni_trier.jane.random.prng;

import ch.fourmilab.hotbits.randomX.*;
import edu.cornell.lassp.houle.RngPack.*;

/**
 * This factory enables to instantiate the random number generators from Java,
 * edu.cornell.lassp.houle.RngPack, and ch.fourmilab.hotbits.randomX.
 * @author Hannes Frey
 */
public class MergedRandomNumberGeneratorFactory implements PseudoRandomNumberGeneratorFactory {

	// the standard Java random number generator
	public static final int JAVA = 0;

	// the random number generators from edu.cornell.lassp.houle.RngPack
	public static final int RANECU = 1;
	public static final int RANLUX = 2;
	public static final int RANMAR = 3;
	public static final int RANMT = 4;

	// the random number generators from ch.fourmilab.hotbits.randomX
	public static final int LCG = 5;
	public static final int LECUYER = 6;
	public static final int MCG = 7;
	public static final int HOTBITS = 8;

	private int generator1;
	private int generator2;
	private boolean combined;

	private MergedRandomNumberGeneratorFactory(int generator1, int generator2, boolean combined) {
		this.generator1 = generator1;
		this.generator2 = generator2;
		this.combined = combined;
	}

	/**
	 * Construct a generator which creates the given generator.
	 * @param generator the generator type
	 */
	public MergedRandomNumberGeneratorFactory(int generator) {
		this(generator, -1, false);
	}

	/**
	 * Construct a generator which creates a combination of both generators.
	 * @param generator1 the first generator which creates an array of values
	 * @param generator2 the second generator which selects a number form that array
	 */
	public MergedRandomNumberGeneratorFactory(int generator1, int generator2) {
		this(generator1, generator2, true);
	}

	public PseudoRandomNumberGenerator getPseudoRandomNumberGenerator(long seed) {
		if(!combined) {
			return createGenerator(generator1, seed);
		}
		else {
			RandomElement element1 = new PRNGToRandomElement(createGenerator(generator1, seed));
			RandomElement element2 = new PRNGToRandomElement(createGenerator(generator2, seed));
			RandomElement combination = new RandomShuffle(element1, element2, 32);
			return new RandomElementToPRNG(combination);
		}
	}

	private PseudoRandomNumberGenerator createGenerator(int generator, long seed) {
		RandomElement element;
		randomX randomx;
		switch(generator) {
		case JAVA :
			return new DefaultPseudoRandomNumberGenerator(seed);
		case RANECU :
			element = new Ranecu(seed);
			return new RandomElementToPRNG(element);
		case RANLUX :
			element = new Ranlux(seed);
			return new RandomElementToPRNG(element);
		case RANMAR :
			element = new Ranmar(seed);
			return new RandomElementToPRNG(element);
		case RANMT :
			element = new RanMT(seed);
			return new RandomElementToPRNG(element);
		case LCG :
			randomx = new randomLCG(seed);
			return new RandomXToPRNG(randomx);
		case LECUYER:
			randomx = new randomLEcuyer(seed);
			return new RandomXToPRNG(randomx);
		case MCG :
			randomx = new randomMCG(seed);
			return new RandomXToPRNG(randomx);
		case HOTBITS :
			randomx = new randomHotBits();
			return new RandomXToPRNG(randomx);
		}
		throw new IllegalArgumentException("the generator " + generator + " does not exist.");
	}
	
}
