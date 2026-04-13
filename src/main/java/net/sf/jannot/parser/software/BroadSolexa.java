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
import net.sf.jannot.parser.ParserError;
import net.sf.jannot.shortread.MemoryReadSet;
import tudelft.utilities.logging.Reporter;

/**
 * A short read parser for Broad data
 * 
 * @author Thomas Abeel
 * 
 */
public class BroadSolexa extends Parser {

	/**
	 * @param dataKey
	 * @param log     the Reporter to log issues to
	 */
	public BroadSolexa(DataKey dataKey, Reporter log) {
		super(dataKey, log);

	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		if (set == null)
			set = new EntrySet();
		LineIterator it = new LineIterator(is);

//		long time = System.currentTimeMillis();

		/* This parser assumes that header and sequences lines alternate */
		int mapStart = -1;
//		boolean forward = false;
		Entry entry = null;
//		set.setMute(true);

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
//					forward = arr[2].equals("fw");

					entry = set.getOrCreateEntry(arr3[0]);
//					if(entry.shortReads.getReadGroup(source)==null){
//						entry.shortReads.add(source, new MemoryReadSet());
//					}
					if (entry == null) {
						throw new ParserError(
								"There is no reference sequence loaded for this short read: "
										+ arr[3]);
						// current.description.setPrimaryAccessionNumber(line.substring(1).split("
						// ")[0].split("\t")[0]);
					}
					if (!entry.contains(dataKey)) {
						entry.add(dataKey, new MemoryReadSet());
					}
				} catch (Exception e) {
					getLog().log(Level.SEVERE, "Failed to parse " + is
							+ ". Offending line: " + line, e);
					return set;
				}
			} else {
				MemoryReadSet mrs = (MemoryReadSet) entry.get(dataKey);
				// if(forward)
				// FIXME mrs.add(new BasicShortRead(line.toCharArray(),
				// mapStart, forward));
				// else
				// FIXME mrs.add(new
				// BasicShortRead(SequenceTools.reverseComplement(new
				// MemorySequence(new
				// StringBuffer(line))).getSequence().toCharArray(), mapStart,
				// forward));
			}
		}
//		set.setMute(false);

		return set;
	}

}
