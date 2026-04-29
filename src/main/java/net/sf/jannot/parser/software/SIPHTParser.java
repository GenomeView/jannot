/**
 * %HEADER%
 */
package net.sf.jannot.parser.software;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import cern.colt.Arrays;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.Global;
import net.sf.jannot.Location;
import net.sf.jannot.Strand;
import net.sf.jannot.StringKey;
import net.sf.jannot.Type;
import net.sf.jannot.parser.Parser;
import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * 
 */
public class SIPHTParser extends Parser {
	/**
	 * @param stringKey
	 * @param global    the {@link Reporter} to log issues to
	 */
	public SIPHTParser(StringKey stringKey, Global global) {
		super(stringKey, global);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		LineIterator it = new LineIterator(is);
		it.setSkipBlanks(true);
		String id = null;
		Type t = Type.get("SIPHT");
		int count = 0;
		while (it.hasNext() && count < 1) {
			if (it.next().startsWith("~")) {
				count++;
			}
		}
		ArrayList<String> header = new ArrayList<String>();
		for (String line : it) {
			if (line.startsWith("~") || line.startsWith("**")) {
				count++;
				continue;
			}
			String[] arr = line.split("\t+",
					header.size() > 0 ? header.size() : 0);
			if (count == 1) {
				// System.out.println("header: " + Arrays.toString(arr));
				for (String s : arr) {
					header.add(s.trim());
				}
			}

			if (count == 2 || count == 3) {
				getLog().log(Level.INFO, "putative: " + Arrays.toString(arr));
				int start = Integer.parseInt(arr[8]);
				int end = Integer.parseInt(arr[9]);
				Feature f = new Feature(new Location(start, end));
				f.setType(t);
				if (arr[10].equals("<<<")) {
					f.setStrand(Strand.REVERSE);
				} else {
					f.setStrand(Strand.FORWARD);
				}
				for (int i = 0; i < arr.length; i++) {
					if (!header.get(i).equals("|")) {
						f.addQualifier(header.get(i), arr[i]);
					}
				}

				Entry e = set.getOrCreateEntry(arr[2].split("_")[0]);
				e.getMemoryAnnotation(t).add(f);

			}

		}

		return set;
	}
}
