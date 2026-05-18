/**
 * %HEADER%
 */
package net.sf.jannot.parser.software;

import java.io.InputStream;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.parser.Parser;
import net.sf.jannot.parser.ParserError;
import net.sf.jannot.shortread.MemoryReadSet;

/**
 * A short read parser for Broad data
 * 
 * @author Thomas Abeel
 * 
 */
public class BroadSolexa extends Parser {

	/**
	 * @param dataKey
	 * @param global  the Reporter to log issues to
	 */
	public BroadSolexa(DataKey dataKey, Global global) {
		super(dataKey, global);

	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		LineIterator it = new LineIterator(is);

		/* This parser assumes that header and sequences lines alternate */
		int mapStart = -1;
		Entry entry = null;

		for (String line : it) {

			if (line.startsWith(">")) {
				try {
					String[] arr = line.split(" ");
					/*
					 * Mapping start in Broad short read format is zero based,
					 * correct for it
					 */
					String[] arr3 = arr[3].split("\\.");
					mapStart = Integer.parseInt(arr3[1]);

					entry = set.getOrCreateEntry(arr3[0]);
					if (entry == null) {
						throw new ParserError(
								"There is no reference sequence loaded for this short read: "
										+ arr[3]);
					}
					if (!entry.contains(dataKey)) {
						entry.add(dataKey, new MemoryReadSet(getGlobal()));
					}
				} catch (Exception e) {
					getLog().log(Level.SEVERE, "Failed to parse " + is
							+ ". Offending line: " + line, e);
					return set;
				}
			} else {
				MemoryReadSet mrs = (MemoryReadSet) entry.get(dataKey);
			}
		}
//		set.setMute(false);

		return set;
	}

}
