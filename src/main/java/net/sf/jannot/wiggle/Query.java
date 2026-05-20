/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.io.IOException;

import net.sf.jannot.Data;

/**
 * This seems a data container interface similar to {@link Data}
 * 
 * @author Thomas Abeel
 *
 */
public interface Query {
	/**
	 * @param start the start point, inclusive. presumably 0-based index.
	 * @param end   the end, exclusive. Presumably 0-based index
	 * @return copy of data from start (inclusive) to end (exclusive)
	 */
	float[] getRawRange(int start, int end) throws IOException;

	/**
	 * @return the number of floats contained in the data. 1+the maximum
	 *         position. end-start (since end is exclusive)
	 */
	long size();
}
