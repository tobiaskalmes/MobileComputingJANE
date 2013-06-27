package de.uni_trier.jane.tools.seeds;

import de.uni_trier.jane.random.DistributionCreator;
import de.uni_trier.jane.random.prng.MergedRandomNumberGeneratorFactory;
import de.uni_trier.jane.random.prng.PseudoRandomNumberGenerator;
import de.uni_trier.jane.random.prng.PseudoRandomNumberGeneratorFactory;

public class SeedGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int maxseeds = 0;
		PseudoRandomNumberGenerator rand = null;
		try {
			MergedRandomNumberGeneratorFactory randomNumberGeneratorFactory = new MergedRandomNumberGeneratorFactory(
					Integer.parseInt(args[0]));
			rand = randomNumberGeneratorFactory.getPseudoRandomNumberGenerator(Integer
					.parseInt(args[1]));
			maxseeds = Integer.parseInt(args[2]);
		} catch (Exception e) {
			System.out.println(SeedGenerator.class.getName()+" <seedGenerator> <initseed> <numberseeds>");
			return;
		}		
		for (int i=0;i<maxseeds;i++){
			System.out.println(rand.nextInt(Integer.MAX_VALUE));
		}
	}

}
