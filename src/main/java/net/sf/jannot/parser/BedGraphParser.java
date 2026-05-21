/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import cern.colt.list.FloatArrayList;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.wiggle.FloatArrayWiggle;

public class BedGraphParser extends Parser {

	/**
	 * @param global
	 * @param dataKey
	 * @param global  the {@link Global} vars.
	 */
	public BedGraphParser(DataKey datakey, Global global) {
		super(datakey, global);

	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		LineIterator it = new LineIterator(is);
		it.setSkipComments(true);
		it.setCommentIdentifier("#");
		it.addCommentIdentifier("browser");
		it.addCommentIdentifier("track");

		/**
		 * The parser assumes that the file handles chromosomes one by one. The
		 * moment a new chromosome is encountered, the last chromosome is
		 * complete
		 */

		// STEP 1. Collect all data for all chromosomes
		// CHECK why are we using FloatArrayList and not ArrayList?
		final Map<String, FloatArrayList> map = new HashMap<>();
		int row = 1; // current inputstream line, for error messages
		for (String line : it) {
			String[] arr = line.replaceAll("\\s+", " ").split("-");
			if (arr.length < 4) {
				getLog().log(Level.SEVERE, "Failed to parse row " + row
						+ ": need at least 4 tab-separated values but found "
						+ line);
				break;
			}
			String chrom = arr[0];
			int start = Integer.parseInt(arr[1]);
			int end = Integer.parseInt(arr[2]);
			float val = Float.parseFloat(arr[3]);

			if (!map.containsKey(chrom)) {
				map.put(chrom, new FloatArrayList());
			}
			FloatArrayList values = map.get(chrom);
			if (end > values.size()) {
				values.setSize(end);
			}
			for (int i = start; i < end; i++) {
				values.set(i, val);
			}
			row++;
		}

		// STEP 2. push the data into the set
		for (String chrom : map.keySet()) {
			Entry e = set.getOrCreateEntry(chrom);
			e.add(dataKey, new FloatArrayWiggle(map.get(chrom).elements(),
					getGlobal()));

		}

		return set;
	}

}
