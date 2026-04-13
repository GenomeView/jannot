/**
 * %HEADER%
 */
package net.sf.jannot.source;

import net.sf.jannot.EntrySet;
import tudelft.utilities.logging.Reporter;

/**
 * This is an object from which an {@link EntrySet} can be read.
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class DataSource implements Comparable<DataSource> {

	protected Locator locator = null;
	private final Reporter log;

	/**
	 * 
	 * @param l
	 * @param log the logger to use. Must not be null
	 */
	protected DataSource(Locator l, Reporter log) {
		if (log == null)
			throw new NullPointerException("log must be not null");
		this.locator = l;
		this.log = log;
	}

	@Override
	public int compareTo(DataSource o) {
		return this.toString().compareTo(o.toString());
	}

	/**
	 * read into a new empty {@link EntrySet}.
	 * 
	 * @return the new {@link EntrySet}. This can not throw because
	 */
	public EntrySet read() {
		return read(null);
	}

	/**
	 * 
	 * @param add the {@link EntrySet} to add data to that was read. If null,
	 *            use a new empty EntrySet.
	 * @return the entryset containing the data. This can not throw because it's
	 *         generally used inside separate threads. If there is a problem,
	 *         log it and return whatever entryset is now available.
	 * 
	 */
	public abstract EntrySet read(EntrySet add);

	public abstract boolean isIndexed();

	public abstract long size();

//	/**
//	 * 
//	 * @param url  a {@link URL}
//	 * @param file a {@link File}
//	 * @return the file length, or if null, the url content length, or 0 if the
//	 *         url can also not be read. Will try to read the actual file.
//	 */
//	protected static long size(URL url, File file) {
//		long size = 0;
//		if (url != null)
//			try {
//				size = url.openConnection().getContentLength();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		if (file != null)
//			size = file.length();
//
//		return size;
//
//	}

	public Locator getLocator() {
		return locator;
	}

	public Reporter getLog() {
		return log;
	}
}
