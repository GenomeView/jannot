/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.io.IOException;

/**
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
	 * @return the range of positions covered by this.
	 */
	long size();
}
