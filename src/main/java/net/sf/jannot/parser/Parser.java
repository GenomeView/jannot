/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.StringKey;
import net.sf.jannot.Type;

/**
 * Base class for all genome data file parsers. The main method is
 * {@link #findParser(InputStream, Object)}.
 * 
 */
public abstract class Parser {

	// public static final Parser GFF3 = new GFF3Parser();

	// public static final Parser EMBL = new EMBLParser();

	// private static Logger log = Logger.getLogger(Parser.class.toString());

	/*
	 * Data key for data types that require some external information to
	 * determine the name of the data
	 */
	protected DataKey dataKey = null;

	public Parser(DataKey dataKey) {
		this.dataKey = dataKey;
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
	 * 
	 * @param is  inputStream
	 * @param set an EntrySet to add the parse results to. If null, a new
	 *            {@link EntrySet} is created
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
	public void write(OutputStream os, Entry entry) {
		write(os, entry, Type.values());
	}

	public void write(OutputStream os, Entry entry, DataKey[] dk) {
		// Do nothing by default, parser can choose to implement the write
		// method.

	}

	/**
	 * 
	 * @param c the strand character
	 * @return true iff c is '+' '-' or '.' which are the strand direction
	 *         chars.
	 */
	private static boolean isStrand(char c) {
		return (c == '+' || c == '-' || c == '.');
	}

	public void setDataKey(DataKey dk) {
		this.dataKey = dk;
	}

	@Deprecated
	public void setDataKey(String s) {
		setDataKey(new StringKey(s));
	}

}
