/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Type;
import tudelft.utilities.logging.Reporter;

/**
 * Base class for all genome data file parsers.
 * 
 */
public abstract class Parser {

	/*
	 * Data key for data types that require some external information to
	 * determine the name of the data
	 */
	protected final DataKey dataKey;
	private final Reporter log;

	/**
	 * 
	 * @param dataKey
	 * @param log     the {@link Reporter} to report parser issues to.
	 */
	public Parser(DataKey dataKey, Reporter log) {
		this.dataKey = dataKey;
		this.log = log;
	}

	public Reporter getLog() {
		return log;
	}

	@Override
	public String toString() {
		return this.getClass().getName().replaceAll("net.sf.jannot.parser.",
				"");
	}

	/**
	 * Read all data from an input stream. Set the data source for each item to
	 * the supplied source. If and {@link EntrySet} is supplied the data will be
	 * added to this set, otherwise a new {@link EntrySet} will be created.
	 * <p>
	 * The parser needs to figure out which {@link Entry} / {@link DataKey} in
	 * the {@link EntrySet} to use. This is usually done with data available in
	 * the parsed file itself, If multiple files are read, all referring to the
	 * same {@link Entry}, it is assumed that different parsers store their
	 * results under different keys in the {@link Entry}'s data.
	 * <p>
	 * Can not throw because parsers usually run in a separate thread. Just
	 * return an empty entryset in the worst case.
	 * 
	 * 
	 * @param is  inputStream
	 * @param set an EntrySet to add the parse results to. Must be not null.
	 * @return either the set to which the parsed data was added.
	 */
	public abstract EntrySet parse(InputStream is, EntrySet set);

	/**
	 * Output everything from the provided entry to the output stream.
	 * 
	 * 
	 * @param os     output stream to write data to
	 * @param e      the entry to save
	 * @param source the source to filter on, or null when no filtering is
	 *               required.
	 */
	public void write(OutputStream os, Entry entry) throws IOException {
		write(os, entry, Type.values());
	}

	public void write(OutputStream os, Entry entry, DataKey[] dk)
			throws IOException {
		// Do nothing by default, parser can choose to implement the write
		// method.

	}

}
