/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.io.IOException;

import net.sf.jannot.Data;

/**
 * a data container interface. Similar to {@link Data}
 * 
 * @author Thomas Abeel
 *
 */
public interface Query {
	/**
	 * @param start the raw start point, inclusive. presumably 0-based index.
	 *              "raw start point" means the start point in the original
	 *              sequence.
	 * @param end   the raw end, exclusive. Presumably 0-based index
	 * @return copy of data from start (inclusive) to end (exclusive). Normally
	 *         the returned array contains (end-start) samples; however some
	 *         implementations return a down-sampled array, having eg
	 *         {@link FloatCache#getRawRange(int, int)} only (end-start)/32
	 *         samples.
	 */
	float[] getRawRange(int start, int end) throws IOException;

	/**
	 * @return the number of floats contained in the data. 1+the maximum
	 *         position. end-start (since end is exclusive)
	 */
	long size();
}
