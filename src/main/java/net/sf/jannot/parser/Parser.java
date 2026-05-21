/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import tudelft.utilities.logging.Reporter;

/**
 * Base class for all genome data file parsers. Parser is in fact data binding
 * library between an {@link Entry} and a {@link Stream}, so it both parses and
 * dumps {@link Entry}s.
 * <p>
 * It's not clear why Parser is here - it's not an object anyone 'needs', it
 * seems it could have been part of {@link Entry} or {@link EntrySet}.
 */
public abstract class Parser {

	/*
	 * Data key for data types that require some external information to
	 * determine the name of the data
	 */
	protected final DataKey dataKey;
	private final Global global;

	/**
	 * 
	 * @param dataKey the datakey to use to store the data. This datakey is
	 *                usually linked to the type of data being stored and fixed
	 *                for each parser. If the data refers to multiple
	 *                chromosomes, the {@link Entry}s corresponding to each
	 *                chromosome will be extended with data using this dataKey
	 * @param global  the {@link Global}.
	 */
	public Parser(DataKey dataKey, Global global) {
		this.dataKey = dataKey;
		this.global = global;
	}

	/**
	 * 
	 * @return convenience method for getGlobal().getLog()
	 */
	public Reporter getLog() {
		return global.getLog();
	}

	/**
	 * 
	 * @return the shared instance of {@link Global}
	 */
	public Global getGlobal() {
		return global;
	}

	@Override
	public String toString() {
		return this.getClass().getName().replaceAll("net.sf.jannot.parser.",
				"");
	}

	/**
	 * Read all data from an input stream. The data is pushed to various entries
	 * in the set, usually based on the "chromosome name".
	 * <p>
	 * If an {@link EntrySet} is supplied the data will be added to this set,
	 * otherwise a new {@link EntrySet} will be created.
	 * <p>
	 * The parser needs to figure out which {@link Entry} / {@link DataKey} in
	 * the {@link EntrySet} to use. This is usually done with data available in
	 * the parsed file itself, using references to "chromosome names", If
	 * multiple files are read, all referring to the same {@link Entry}, it is
	 * assumed that different parsers store their results under different keys
	 * in the {@link Entry}'s data.
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
		write(os, entry, global.typeFactory().values());
	}

	public void write(OutputStream os, Entry entry, List<? extends DataKey> dk)
			throws IOException {
		// Do nothing by default, parser can choose to implement the write
		// method.

	}

}
