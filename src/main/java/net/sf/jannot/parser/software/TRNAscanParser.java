/**
 * %HEADER%
 */
package net.sf.jannot.parser.software;

import java.io.InputStream;

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
import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * 
 */
public class TRNAscanParser extends Parser {

	/**
	 * @param dataKey
	 * @param global  the {@link Reporter} to log issues to
	 */
	public TRNAscanParser(Global global) {
		super(null, global);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jannot.parser.Parser#parse(java.io.InputStream,
	 * net.sf.jannot.source.DataSource, net.sf.jannot.EntrySet)
	 */
	@Override
	public EntrySet parse(InputStream is, EntrySet set) {

		LineIterator it = new LineIterator(is);
		it.setSkipBlanks(true);
		it.setSkipComments(true);
		/* Skip first three lines */
		it.next();
		it.next();
		it.next();
		Type t = getGlobal().typeFactory().get("tRNA");
		for (String line : it) {
			String[] arr = line.split("[ \t]+");
			Entry e = set.getOrCreateEntry(arr[0]);
			int start = Integer.parseInt(arr[2]);
			int end = Integer.parseInt(arr[3]);
			Strand str = start > end ? Strand.REVERSE : Strand.FORWARD;

			Feature f = new Feature(new Location(start, end), t, str);
			f.addQualifier("type", arr[4]);
			f.addQualifier("anti-codon", arr[5]);
			f.setScore(Double.parseDouble(arr[8]));
			MemoryFeatureAnnotation fa = e.getMemoryAnnotation(t);
			fa.add(f);

		}
		return set;
	}

}
