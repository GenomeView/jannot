/**
 * %HEADER%
 */
package net.sf.jannot.parser.software;

import java.io.InputStream;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.Global;
import net.sf.jannot.Location;
import net.sf.jannot.MemoryFeatureAnnotation;
import net.sf.jannot.Strand;
import net.sf.jannot.Type;
import net.sf.jannot.parser.Parser;

/**
 * @author Thomas Abeel
 * 
 */
public class FindPeaksParser extends Parser {

	/**
	 * @param dataKey
	 */
	public FindPeaksParser(Global global) {
		super(null, global);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		LineIterator it = new LineIterator(is);
		it.setSkipBlanks(true);
		it.setSkipComments(true);
		/* Skip first line */
		it.next();
		Type t = Type.get("peak");
		for (String line : it) {
			String[] arr = line.split("[ \t]+");
			if (arr.length < 5) {
				getLog().log(Level.SEVERE,
						"Expected at least 5 columns but found " + line);
				break;
			}
			Entry e = set.getOrCreateEntry(arr[1]);
			int start = Integer.parseInt(arr[2]);
			int end = Integer.parseInt(arr[3]);
			Feature f = new Feature(new Location(start, end));
			f.setType(t);
			f.addQualifier("max_coord", arr[4]);
			f.addQualifier("Name", "Peak " + arr[0]);
			// f.addQualifier("t-score", arr[5]);
			f.setScore(Double.parseDouble(arr[5]));
			f.setStrand(Strand.UNKNOWN);
			MemoryFeatureAnnotation fa = e.getMemoryAnnotation(t);
			fa.add(f);

		}
		return set;
	}

}
