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
import net.sf.jannot.shortread.MemoryReadSet;

public class MapViewParser extends Parser {

	/**
	 * @param dataKey
	 */
	public MapViewParser(DataKey dataKey, Global global) {
		super(dataKey, global);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		LineIterator it = new LineIterator(is);

		int count = 0;
		for (String line : it) {
			String[] arr = line.split("\t");
			Entry entry = set.getOrCreateEntry(arr[1]);

			if (!entry.contains(dataKey)) {
				entry.add(dataKey, new MemoryReadSet(getLog()));
			}
			if (!arr[14].matches(".*[nN].*")) {
				MemoryReadSet mrs = (MemoryReadSet) entry.get(dataKey);
			} else {
				count++;
			}

		}
		if (count > 0) {
			getLog().log(Level.WARNING, "Discarded: " + count
					+ " short reads because of ambiguity");
//		set.setMute(false);
		}

		return set;
	}

}
