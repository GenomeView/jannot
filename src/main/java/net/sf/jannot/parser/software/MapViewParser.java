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
import net.sf.jannot.parser.Parser;
import net.sf.jannot.shortread.MemoryReadSet;
import tudelft.utilities.logging.Reporter;

public class MapViewParser extends Parser {

	/**
	 * @param dataKey
	 */
	public MapViewParser(DataKey dataKey, Reporter log) {
		super(dataKey, log);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		if (set == null) {
			set = new EntrySet(getLog());
		}
		LineIterator it = new LineIterator(is);

		// long time = System.currentTimeMillis();

//		set.setMute(true);
		int count = 0;
		for (String line : it) {
			String[] arr = line.split("\t");
			Entry entry = set.getOrCreateEntry(arr[1]);

//			if(e.shortReads.getReadGroup(source)==null){
//				e.shortReads.add(source, new MemoryReadSet());
//			}
			if (!entry.contains(dataKey)) {
				entry.add(dataKey, new MemoryReadSet(getLog()));
			}
			if (!arr[14].matches(".*[nN].*")) {
				MemoryReadSet mrs = (MemoryReadSet) entry.get(dataKey);
				// mrs.add(new BasicShortRead(arr[14].toCharArray(),
				// Integer.parseInt(arr[2]), arr[3].charAt(0) == '+'));
			} else {
				// System.out.println("discarding: "+arr[0]);
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
