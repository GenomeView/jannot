/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.io.IOException;

import net.sf.jannot.Data;

/**
 * Extended version of {@link Data}. Extends {@link Data#get(int, int)} with a
 * lower-resolution option {@link #get(int, int, int)}
 * 
 * @author Thomas Abeel
 * 
 */
public interface Graph extends Data<Float> {

	/**
	 * Returns float array with float[i] containing a value
	 * 
	 * Resolutions should be powers of two: 1,2,4,8,16,32,64,128,256,...
	 * 
	 * Their corresponding indices are: 0,1,2,3,4,5,6,7,8,...
	 * 
	 * @param start           zero based coordinate of the start
	 * @param end             zero based coordinate of the end, non-inclusive
	 * @param resolutionIndex index of the desired resolution. 0 or positive
	 *                        integer. resolutionIndex n implies a stepsize 2^n
	 *                        through the data.
	 * @return an array of (end-start)/2^resolutionIndex floats.
	 * 
	 * @throws IOException if the value at given pos can't be read
	 */
	public float[] get(int start, int end, int resolutionIndex)
			throws IOException;

	/**
	 * 
	 * @return the minimum value found in the data array
	 */
	public float min();

	/**
	 * 
	 * @return the max value found in the data array
	 */
	public float max();

	/**
	 * @param pos one based coordinate
	 * @return value at pos
	 * @throws IOException if the value at given pos can't be read
	 */
	public float value(int pos) throws IOException;

}
