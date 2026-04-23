/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.InputStream;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import cern.colt.list.FloatArrayList;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.wiggle.FloatArrayWiggle;
import tudelft.utilities.logging.Reporter;

public class BedGraphParser extends Parser {

	/**
	 * @param dataKey
	 * @param log     the {@link Reporter} to log issues to.
	 */
	public BedGraphParser(DataKey datakey, Reporter log) {
		super(datakey, log);

	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		LineIterator it = new LineIterator(is);
		it.setSkipComments(true);
		it.setCommentIdentifier("#");
		it.addCommentIdentifier("browser");
		it.addCommentIdentifier("track");

		FloatArrayList values = new FloatArrayList();
		String last = "";
		Entry e = null;
		int row = 1;
		for (String line : it) {
			String[] arr = line.split("\t");
			if (arr.length < 4) {
				getLog().log(Level.SEVERE, "Failed to parse row " + row
						+ ": need at least 4 tab-separated values but found "
						+ line);
				break;
			}
			int start = Integer.parseInt(arr[1]);
			int end = Integer.parseInt(arr[2]);
			if (!last.equals(arr[0])) {
				last = arr[0];

				if (e != null) {
					e.add(dataKey,
							new FloatArrayWiggle(values.elements(), getLog()));
					getLog().log(Level.INFO,
							"Adding: " + e + "\t" + values.size());
					values = new FloatArrayList();
				}
				e = set.getOrCreateEntry(arr[0]);
			}
			float val = Float.parseFloat(arr[3]);
			/* Make sure the array is big enough */
			if (end > values.size()) {
				values.setSize(end);
			}
			for (int i = start; i < end; i++) {
				values.set(i, val);
			}
			row++;

		}

		return set;
	}

}
