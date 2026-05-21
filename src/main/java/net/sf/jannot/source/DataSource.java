/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.IOException;
import java.util.Objects;

import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import tudelft.utilities.logging.Reporter;

/**
 * This is an object from which an {@link EntrySet} can be read.
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class DataSource implements Comparable<DataSource> {

	protected final Locator locator;
	protected final Global global;

	/**
	 * 
	 * @param l      the Locator of this, used for loading it
	 * @param global the {@link Global}. Must not be null
	 */
	protected DataSource(Locator l, Global global) {
		this.global = Objects.requireNonNull(global);
		this.locator = l;
	}

	@Override
	public int compareTo(DataSource o) {
		// FIXME toString is not even implemented!!
		// this is broken way of comparing memory locations only
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
	 * Convenience method.
	 * 
	 * @return the {@link Reporter} used for logging issues
	 */
	public Reporter getLog() {
		return global.getLog();
	}

	public Global getGlobal() {
		return global;
	}
}
