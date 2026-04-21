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
	float[] getRawRange(int start, int end) throws IOException;

	long size();
}
