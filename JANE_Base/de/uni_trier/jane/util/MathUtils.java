/*
 * Created on 12.05.2005
 */
package de.uni_trier.jane.util;

/**
 * @author Christoph Lange
 *
 * This class provides several handy functions not available in <code>java.lang.Math</code>
 * 
 * @see java.lang.Math
 */
public final class MathUtils {
    /**
     * Calculates the exponential mean of the previous exponential mean
     * and a new value
     * @param alpha the weight factor
     * @param newValue the new value
     * @param prevMean the previously computed exponential mean
     * @return the new exponential mean
     */
    public static double exponentialMean(double alpha, double newValue, double prevMean) {
        return alpha * newValue + (1 - alpha) * prevMean;
    }

    /**
	 * Returns the value of the first argument raised to the power of the second argument.
	 * This optimized implementation tries to square the base as long as possible.
	 * The tail-recursion is hard-coded.  
	 * 
	 * @param b	the base
	 * @param x	the exponent, must not be negative
	 * @return the value b<sup>x</sup>
	 */
	public static int pow(int b, int x) {
		if (b == 2) return 1 << x;
		int result = 1;
		while (x != 0) {
			int i = 1;
			int pres = b;
			while (i <= x / 2) {
				pres *= pres;
				i *= 2;
			}
			result *= pres;
			x -= i;
		}
		return result;
	}
	
	/**
	 * Returns the value of the first argument raised to the power of the second argument.
	 * This optimized implementation tries to square the base as long as possible.
	 * The tail-recursion is hard-coded.  
	 * 
	 * @param b	the base
	 * @param x	the exponent, must not be negative
	 * @return the value b<sup>x</sup>
	 */
	public static double pow(double b, int x) {
		int result = 1;
		while (x != 0) {
			int i = 1;
			double pres = b;
			while (i <= x / 2) {
				pres *= pres;
				i *= 2;
			}
			result *= pres;
			x -= i;
		}
		return result;
	}

	/**
	 * Returns the logarithm of <code>n</code> to the base <code>b</code>, rounded down 
	 * 
	 * @param b the base, must be greater than 1
	 * @param n 
	 * @return the value <i>log<sub>b</sub>n</i>
	 */	
	public static int logb(int b, int n) {
		int result = 0;
		while (n >= b) {
			n /= b;
			result++;
		}
		return result;
		
	}
	
	/**
	 * Returns the logarithm of <code>n</code> to the base 2, rounded down 
	 * 
	 * @param n 
	 * @return the value <i>log<sub>2</sub>n</i>
	 */	
	public static int log2(int n) {
		return logb(2, n);
	}

	/**
     * returns whether a &le; b &le; c (&lt; a), considering a circular space
     * 
	 * @param <T> a comparable type
	 * @param a the first value
	 * @param b the second value
	 * @param c the third value
	 * @return
     * @see Comparable
	 */
	/*public static <T extends Comparable<? super T>> boolean between(T a, T b, T c) {
		return a.compareTo(b) <= 0 ?
			b.compareTo(c) <= 0 || c.compareTo(a) < 0 :
		    b.compareTo(c) <= 0 && c.compareTo(a) < 0;
	}*/
}
