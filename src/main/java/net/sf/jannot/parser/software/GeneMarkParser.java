/**
 * %HEADER%
 */
package net.sf.jannot.parser.software;

import java.io.InputStream;

import be.abeel.io.LineIterator;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.Global;
import net.sf.jannot.Location;
import net.sf.jannot.MemoryFeatureAnnotation;
import net.sf.jannot.Strand;
import net.sf.jannot.Type;
import net.sf.jannot.parser.Parser;

/**
 * Parser for the output of the GeneMark gene caller
 * 
 * @author Thomas Abeel
 * 
 */
public class GeneMarkParser extends Parser {

	/**
	 * @param dataKey
	 */
	public GeneMarkParser(Global global) {
		super(null, global);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		Type t = getGlobal().typeFactory().get("CDS_pred");
		MemoryFeatureAnnotation fa = set.iterator().next()
				.getMemoryAnnotation(t);
		for (String line : new LineIterator(is)) {
			if (!line.startsWith(" ") || line.contains("Gene")
					|| line.contains("Length")) {
				continue;
			}
			String[] arr = line.replace('<', ' ').trim().split("[ \t]+");
			final Location loc = new Location(Integer.parseInt(arr[2]),
					Integer.parseInt(arr[3]));
			Feature f = new Feature(loc, t,
					Strand.fromSymbol(arr[1].charAt(0)));
			f.addQualifier("source", "GeneMark");
			f.addQualifier("Gene", arr[0]);
			fa.add(f);
		}

		return set;
	}

}
