package de.uni_trier.jane.random.seed;

import java.io.*;
import java.util.*;

/**
 * This seed generator is based on an array of long values. The array may either
 * be passed directly or be read fomr a file or stream.
 * @author Hannes Frey
 *
 */
public class ArraySeedGenerator implements SeedGenerator {

	private long[] seedArray;
	private int current;

	/**
	 * Construct a new <code>ArraySeedGenerator</code> object.
	 * @param seedArray the array of seed values (used cyclic)
	 */
	public ArraySeedGenerator(long[] seedArray) {
		this.seedArray = seedArray;
		current = 0;
	}
	
	/**
	 * Construct a new <code>ArraySeedGenerator</code> object from a given
	 * seed file.
	 * @param seedFile name of the file
	 */
	public ArraySeedGenerator(String seedFile) {
		this(createSeedArray(seedFile));
	}
	
	/**
	 * Construct a new <code>ArraySeedGenerator</code> object from a given
	 * seed input stream.
	 * @param seedStream the input stream
	 */
	public ArraySeedGenerator(InputStream seedStream) {
		this(creatSeedArray(seedStream));
	}

	
	public long getNext() {
		current = current % seedArray.length;
		return seedArray[current++];
	}

	private static long[] createSeedArray(String seedFile) {
		try {
			FileReader fr = new FileReader(seedFile);
			BufferedReader br = new BufferedReader(fr);
			long[] result = readSeedFromReader(br);
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
		catch (FileNotFoundException e) {
			throw new IllegalArgumentException("The seedfile does not exist.");
		}
	}

	private static long[] creatSeedArray(InputStream seedStream) {
		BufferedReader br = new BufferedReader(new InputStreamReader(seedStream));
		long[] result = readSeedFromReader(br);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return result;
	}

	private static long[] readSeedFromReader(BufferedReader br) {
		try {
			String line;
			List seedList = new ArrayList();
			while((line = br.readLine()) != null) {
				seedList.add(new Long(line));
			}
			if(seedList.size() == 0) {
				throw new IllegalArgumentException("The seedfile is empy.");
			}
			long[] result = new long[seedList.size()];
			for(int i=0; i<seedList.size(); i++) {
				result[i] = ((Long)seedList.get(i)).longValue();
			}
			return result;
		} catch (IOException e) {
			throw new IllegalArgumentException("The seedfile could not be read.");
		}		
	}

}
