/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.IOException;

import net.sf.jannot.EntrySet;
import tudelft.utilities.logging.Reporter;

/**
 * This is an object from which an {@link EntrySet} can be read.
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class DataSource implements Comparable<DataSource> {

	protected final Locator locator;
	private final Reporter log;

	/**
	 * 
	 * @param l   the Locator of this, used for loading it
	 * @param log the logger to use. Must not be null
	 */
	protected DataSource(Locator l, Reporter log) {
		if (log == null) {
			throw new NullPointerException("log must be not null");
		}
		this.locator = l;
		this.log = log;
	}

	@Override
	public int compareTo(DataSource o) {
		return this.toString().compareTo(o.toString());
	}

	/**
	 * 
	 * @param add the {@link EntrySet} to add data to that was read. If null,
	 *            use a new empty EntrySet.
	 * @return the (non-null) entryset to add the data to. This can not throw
	 *         because it's generally used inside separate threads. If there is
	 *         a problem, log it and return whatever entryset is now available.
	 * 
	 */
	public abstract EntrySet read(EntrySet add);

	/**
	 * 
	 * @return true iff this is indexed.
	 */
	public abstract boolean isIndexed();

	/**
	 * 
	 * @return size of the object.
	 */
	public abstract long size() throws IOException;

	public Locator getLocator() {
		return locator;
	}

	/**
	 * 
	 * @return the {@link Reporter} used for logging issues
	 */
	public Reporter getLog() {
		return log;
	}
}
