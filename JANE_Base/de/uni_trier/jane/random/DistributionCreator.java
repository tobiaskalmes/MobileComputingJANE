/*****************************************************************************
* 
* $Id: DistributionCreator.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
* 
* This program is free software; you can redistribute it and/or 
* modify it under the terms of the GNU General Public License 
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
* General Public License for more details.
* 
* You should have received a copy of the GNU General Public License 
* along with this program; if not, write to the Free Software 
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
* 
*****************************************************************************/
package de.uni_trier.jane.random;

import java.io.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.prng.*;
import de.uni_trier.jane.random.seed.*;

/**
 * This is the main class used to create new instances of distributions. Its inner
 * classes are implementing well known mathematical distributions. There exists a
 * method for each distribution to invoke an instance of it.
 */
public class DistributionCreator {

	private final static String VERSION = "$Id: DistributionCreator.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	private PseudoRandomNumberGeneratorFactory randomNumberGeneratorFactory;
	private SeedGenerator seedGenerator;

	/**
	 * Construct a new <code>DistributionCreator</code> object.
	 * @param randomNumberGeneratorFactory the factory used to create pseudo random number generators
	 * @param seedGenerator the source of seeds used to create the pseudo random number generators
	 */
	public DistributionCreator(PseudoRandomNumberGeneratorFactory randomNumberGeneratorFactory, SeedGenerator seedGenerator) {
		this.randomNumberGeneratorFactory = randomNumberGeneratorFactory;
		this.seedGenerator = seedGenerator;
	}

	/**
	 * Construct a new <code>DistributionCreator</code> object.
	 * @param seedGenerator the source of seeds used to create the pseudo random number generators
	 */
	public DistributionCreator(SeedGenerator seedGenerator) {
		this(new DefaultPseudoRandomNumberGeneratorFactory(), seedGenerator);
	}

	/**
	 * Construct a new <code>DistributionCreator</code> object.
	 * @param seedArray the array of seed values (used cyclic)
	 */
	public DistributionCreator(long[] seedArray) {
		this(new ArraySeedGenerator(seedArray));
	}
	
	/**
	 * Construct a new <code>DistributionCreator</code> object from a given
	 * seed file.
	 * @param seedFile name of the file
	 */
	public DistributionCreator(String seedFile) {
		this(new ArraySeedGenerator(seedFile));
	}
	
    /**
     * 
     * Constructor for class <code>DistributionCreator</code>
     * @param seedStream
     */
	public DistributionCreator(InputStream seedStream) {
		this(new ArraySeedGenerator(seedStream));
	}

	/**
     * 
     * Constructor for class <code>DistributionCreator</code>
     *
     * @param seedGenerator
	 */
    public DistributionCreator(PseudoRandomNumberGenerator seedGenerator) {
        this(new PseudoRandomSeedGenerator(seedGenerator));
    }

    /**
	 * Get an instance of <code>ContinuousUniformDistribution</code>.
	 * @param a the min value
	 * @param b the max value
	 * @return the distribution object
	 */
	public ContinuousDistribution getContinuousUniformDistribution(DoubleMapping a, DoubleMapping b) {
		return new ContinuousUniformDistribution(getRandom(), a, b);
	}

	/**
	 * Get an instance of <code>ContinuousUniformDistribution</code> by using the given fixed parameters.
	 * @param a the fixed min value
	 * @param b the fixed max value
	 * @return the distribution object
	 */
	public ContinuousDistribution getContinuousUniformDistribution(double a, double b) {
		DoubleMapping aMapping = new ConstantDoubleMapping(a);
		DoubleMapping bMapping = new ConstantDoubleMapping(b);
		return new ContinuousUniformDistribution(getRandom(), aMapping, bMapping);
	}

	/**
	 * Get an instance of <code>ContinuousDeterministicDistribution</code>.
	 * @param c the constant value
	 * @return the distribution object
	 */
	public ContinuousDistribution getContinuousDeterministicDistribution(DoubleMapping c) {
		return new ContinuousDeterministicDistribution(c);
	}
	
	/**
	 * Get an instance of <code>ContinuousDeterministicDistribution</code>.
	 * @param c the constant value
	 * @return the distribution object
	 */
	public ContinuousDistribution getContinuousDeterministicDistribution(double c) {
		return new ContinuousDeterministicDistribution(new ConstantDoubleMapping(c));
	}
	

	/**
	 * Get an instance of <code>ExponentialDistribution</code>.
	 * @param lambda the rate of the distribution (1/lambda is the mean value)
	 * @return the distribution object
	 */
	public ContinuousDistribution getExponentialDistribution(DoubleMapping lambda) {
		return new ExponentialDistribution(getRandom(), lambda);
	}
	/**
	 * Get an instance of <code>ExponentialDistribution</code>.
	 * @param lambda the rate of the distribution (1/lambda is the mean value)
	 * @return the distribution object
	 */
	public ContinuousDistribution getExponentialDistribution(double lambda) {
		return new ExponentialDistribution(getRandom(), new ConstantDoubleMapping(lambda));
	}
	
	/**
	 * Get an instance of <code>NormalDistribution</code>.
	 * @param mean the mean of the distribution
	 * @param deviation the deviation of the distribution
	 * @return the distribution object
	 */
	public ContinuousDistribution getNormalDistribution(DoubleMapping mean, DoubleMapping deviation) {
		return new NormalDistribution(getRandom(), mean, deviation);
	}
	/**
	 * Get an instance of <code>NormalDistribution</code>.
	 * @param mean the mean of the distribution
	 * @param deviation the deviation of the distribution
	 * @return the distribution object
	 */
	public ContinuousDistribution getNormalDistribution(double mean, double deviation) {
		return new NormalDistribution(getRandom(), new ConstantDoubleMapping(mean), new ConstantDoubleMapping(deviation));
	}
	
	/**
	 * Get an instance of <code>DiscreteUniformDistribution</code>.
	 * @param i the min value
	 * @param j the max value
	 * @return the distribution object
	 */
	public DiscreteDistribution getDiscreteUniformDistribution(IntegerMapping i, IntegerMapping j) {
		return new DiscreteUniformDistribution(getRandom(), i, j);
	}

	/**
	 * Get an instance of <code>DiscreteUniformDistribution</code> by using the given fixed parameters.
	 * @param i the fixed min value
	 * @param j the fixed max value
	 * @return the distribution object
	 */
	public DiscreteDistribution getDiscreteUniformDistribution(int i, int j) {
		IntegerMapping iMapping = new ConstantIntegerMapping(i);
		IntegerMapping jMapping = new ConstantIntegerMapping(j);
		return new DiscreteUniformDistribution(getRandom(), iMapping, jMapping);
	}
    
    /**
     * Get an instance of <code>DiscreteDeterministicDistribution</code>.
     * @param i the value
     * @return the distribution object
     */
    public DiscreteDistribution getDiscreteDeterministicDistribution(IntegerMapping i){
        return new DiscreteDeterministicDistribution(i);
    }
    
    /**
     * Get an instance of <code>DiscreteDeterministicDistribution</code>.
     * @param i the value
     * @return the distribution object
     */
    public DiscreteDistribution getDiscreteDeterministicDistribution(int i){
        return new DiscreteDeterministicDistribution(i);
    }

	/**
	 * Get an instance of <code>DiscreteWeightedDistribution</code>.
	 * @param v the domain of this distribution
	 * @param w the probability for each value in v
	 * @return the distribution object
	 */
	public DiscreteDistribution getDiscreteWeightedDistribution(IntegerSetParameter v, ProbabilityVectorParameter w) {
		return new DiscreteWeightedDistribution(getRandom(), v, w);
	}

	private PseudoRandomNumberGenerator getRandom() {
		long seed = seedGenerator.getNext();
		return randomNumberGeneratorFactory.getPseudoRandomNumberGenerator(seed);
	}

	private static class ContinuousUniformDistribution implements ContinuousDistribution {
		private PseudoRandomNumberGenerator random;
		private DoubleMapping a;
		private DoubleMapping b;
		public ContinuousUniformDistribution(PseudoRandomNumberGenerator random, DoubleMapping a, DoubleMapping b) {
			this.random = random;
			this.a = a;
			this.b = b;
		}
		public double getInfimum() {
			return Math.min(a.getInfimum(), b.getInfimum());
		}
		public double getSupremum() {
			return Math.max(a.getSupremum(), b.getSupremum());
		}
		public double getInfimum(double t) {
			return Math.min(a.getValue(t), b.getValue(t));
		}
		public double getSupremum(double t) {
			return Math.max(a.getValue(t), b.getValue(t));
		}
		public double getNext(double t) {
			double av = Math.min(a.getValue(t), b.getValue(t));
			double bv = Math.max(a.getValue(t), b.getValue(t));
			return av + ( random.nextDouble() * (bv - av) );
		}
		public double getNext() {
			return getNext(0.0);
		}
	}

	public static class ContinuousDeterministicDistribution implements ContinuousDistribution {
		private DoubleMapping c;
        
        /**
         * 
         * Constructor for class <code>ContinuousDeterministicDistribution</code>
         * @param c
         */
		public ContinuousDeterministicDistribution(DoubleMapping c) {
			this.c = c;
		}
        
        /**
         * 
         * Constructor for class <code>ContinuousDeterministicDistribution</code>
         * @param d
         */
        public ContinuousDeterministicDistribution(double d) {
            c=new ConstantDoubleMapping(d);
         
        }
		public double getInfimum() {
			return c.getInfimum();
		}
		public double getSupremum() {
			return c.getSupremum();
		}
		public double getInfimum(double t) {
			return c.getValue(t);
		}
		public double getSupremum(double t) {
			return c.getValue(t);
		}
		public double getNext(double t) {
			return c.getValue(t);
		}
		public double getNext() {
			return getNext(0.0);
		}
	}

	private static class ExponentialDistribution implements ContinuousDistribution {
		private PseudoRandomNumberGenerator random;
		private DoubleMapping lambda;
		public ExponentialDistribution(PseudoRandomNumberGenerator random, DoubleMapping lambda) {
			if(lambda.getInfimum() < 0) {
				throw new IllegalArgumentException("lambda is not allowed to be negtive.");
			}
			this.random = random;
			this.lambda = lambda;
		}
		public double getInfimum() {
			if(lambda.getInfimum() == 0) {
				return Double.POSITIVE_INFINITY;
			}
			else {
				return 0;
			}
		}
		public double getSupremum() {
			if(lambda.getSupremum() == Double.POSITIVE_INFINITY) {
				return 0;
			}
			else {
				return Double.POSITIVE_INFINITY;
			}
		}

		public double getInfimum(double t) {
			if(lambda.getValue(t) == 0) {
				return Double.POSITIVE_INFINITY;
			}
			else {
				return 0;
			}
		}

		public double getSupremum(double t) {
			if(lambda.getValue(t) == Double.POSITIVE_INFINITY) {
				return 0;
			}
			else {
				return Double.POSITIVE_INFINITY;
			}
		}

		public double getNext(double t) {
			double lv = lambda.getValue(t);
			if(lv == 0) {
				return Double.POSITIVE_INFINITY;
			}
			else if(lv == Double.POSITIVE_INFINITY) {
				return 0;
			}
			else {
				return - Math.log(1 - random.nextDouble()) / lv;
			}
		}
		public double getNext() {
			return getNext(0.0);
		}
	}

	private static class NormalDistribution implements ContinuousDistribution {
		private PseudoRandomNumberGenerator random;
		private DoubleMapping mean;
		private DoubleMapping deviation;
		public NormalDistribution(PseudoRandomNumberGenerator random, DoubleMapping mean, DoubleMapping deviation) {
			if(deviation.getInfimum()<0) {
				throw new IllegalArgumentException("deviation is not allowed to be negative.");
			}
			this.random = random;
			this.mean = mean;
			this.deviation = deviation;
		}
		public double getInfimum() {
			if(deviation.getInfimum() == 0) {
				return mean.getInfimum();
			}
			else {
				return Double.NEGATIVE_INFINITY;
			}
		}
		public double getInfimum(double t) {
			if(deviation.getValue(t) == 0) {
				return mean.getValue(t);
			}
			else {
				return Double.NEGATIVE_INFINITY;
			}
		}
		public double getNext(double t) {
			return mean.getValue(t)+deviation.getValue(t)*random.nextGaussian();
		}
		public double getSupremum() {
			if(deviation.getSupremum() == 0) {
				return mean.getSupremum();
			}
			else {
				return Double.POSITIVE_INFINITY;
			}
		}
		public double getSupremum(double t) {
			if(deviation.getValue(t) == 0) {
				return mean.getValue(t);
			}
			else {
				return Double.POSITIVE_INFINITY;
			}
		}
		public double getNext() {
			return getNext(0.0);
		}
	}

	private static class DiscreteUniformDistribution implements DiscreteDistribution {
		private PseudoRandomNumberGenerator random;
		private IntegerMapping i;
		private IntegerMapping j;
		public DiscreteUniformDistribution(PseudoRandomNumberGenerator random, IntegerMapping i, IntegerMapping j) {
			this.random = random;
			this.i = i;
			this.j = j;
		}
		public IntegerVector getDomain() {
			return new IntegerInterval(Math.min(i.getMinimum(), j.getMinimum()), Math.max(i.getMaximum(), j.getMaximum()));
		}
		public IntegerVector getDomain(double t) {
			return new IntegerInterval(Math.min(i.getValue(t), j.getValue(t)), Math.max(i.getValue(t), j.getValue(t)));
		}
		public int getNext(double t) {
			int iv = Math.min(i.getValue(t), j.getValue(t));
			int jv = Math.max(i.getValue(t), j.getValue(t));
			return iv + ( random.nextInt(jv - iv + 1) );
		}
	}
    
    
    public static class DiscreteDeterministicDistribution implements DiscreteDistribution {
        private IntegerMapping i;
        /**
         * 
         * Constructor for class <code>DiscreteDeterministicDistribution</code>
         * @param i
         */
        public DiscreteDeterministicDistribution(IntegerMapping i) {
            this.i = i;
        }
        
        /**
         * Constructor for class <code>DistributionCreator.DiscreteDeterministicDistribution</code>
         */
        public DiscreteDeterministicDistribution(int i) {
            this.i=new ConstantIntegerMapping(i);
        }
        public IntegerVector getDomain() {
            return new IntegerInterval(i.getMinimum(),i.getMaximum());
        }
        public IntegerVector getDomain(double t) {
            return new IntegerInterval(i.getValue(t), i.getValue(t));
        }
        public int getNext(double t) {
            return i.getValue(t);
        }
    }
    

	private static class DiscreteWeightedDistribution implements DiscreteDistribution {
		private PseudoRandomNumberGenerator random;
		private ProbabilityVectorParameter w;
		private IntegerSetParameter v;
		public DiscreteWeightedDistribution(PseudoRandomNumberGenerator random, IntegerSetParameter v, ProbabilityVectorParameter w) {
			this.random = random;
			this.w = w;
			this.v = v;
		}
		public IntegerVector getDomain() {
			return v.getDomain();
		}
		public IntegerVector getDomain(double t) {
			return v.getValue(t);
		}
		public int getNext(double t) {
			double d = random.nextDouble();
			double sum = 0;
			int i = -1;
			do {
				i++;
				sum += w.value(t).getValue(i);
			}
			while (sum < d);
			return v.getValue(t).get(i);
		}
	}

	
}
