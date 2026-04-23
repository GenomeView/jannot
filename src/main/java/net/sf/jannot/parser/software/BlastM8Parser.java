/**
 * %HEADER%
 */
package net.sf.jannot.parser.software;

import java.io.InputStream;

import be.abeel.io.LineIterator;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.Location;
import net.sf.jannot.MemoryFeatureAnnotation;
import net.sf.jannot.Strand;
import net.sf.jannot.Type;
import net.sf.jannot.parser.Parser;
import tudelft.utilities.logging.Reporter;

public class BlastM8Parser extends Parser {

	/**
	 * @param dataKey
	 * @param log     the {@link Reporter} to log issues to
	 */
	public BlastM8Parser(Reporter log) {
		super(null, log);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		LineIterator it = new LineIterator(is);
		it.setSkipComments(true);
		Type t = Type.get("NCBI Blast hit");
		for (String line : it) {
			String[] arr = line.split("\t");
			int start = Integer.parseInt(arr[6]);
			int end = Integer.parseInt(arr[7]);
			Strand s = Strand.FORWARD;
			if (start > end) {
				int tmp = start;
				start = end;
				end = tmp;
				s = Strand.REVERSE;
			}
			Feature f = new Feature(new Location(start, end));
			f.setType(t);
			f.setScore(Double.parseDouble(arr[10]));
			f.setStrand(s);
			f.addQualifier("subject id", arr[1]);
			f.addQualifier("% identity", arr[2]);
			// f.addQualifier(new Qualifier("% positives", arr[3]));
			f.addQualifier("alignment length", arr[3]);
			f.addQualifier("bit score", arr[11]);
			MemoryFeatureAnnotation fa = set.iterator().next()
					.getMemoryAnnotation(t);
			fa.add(f);

		}

		return set;
	}

}
