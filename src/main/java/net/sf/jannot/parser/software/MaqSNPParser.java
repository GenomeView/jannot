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
import net.sf.jannot.Location;
import net.sf.jannot.MemoryFeatureAnnotation;
import net.sf.jannot.Type;
import net.sf.jannot.parser.Parser;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * Parser for the Maq SNP format.
 * 
 * Maq: Mapping and Assembly with Qualities
 * http://maq.sourceforge.net/maq-man.shtml
 * 
 * This parser handles the output of the cns2snp program of MAQ.
 * 
 * @author Thomas Abeel
 * 
 */
public class MaqSNPParser extends Parser {

	/**
	 * @param dataKey
	 */
	public MaqSNPParser(Reporter log) {
		super(null, log);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		LineIterator it = new LineIterator(is);
		it.setSkipBlanks(true);
		it.setSkipComments(true);
		Type t = Type.get("SNP");

		for (String line : it) {
			String[] arr = line.split("\t");
			if (arr.length < 12) {
				getLog().log(Level.SEVERE,
						"Expected at least 12 values but got " + line);
				break;
			}
			Entry e = set.getOrCreateEntry(arr[0]);
			MemoryFeatureAnnotation fa = e.getMemoryAnnotation(t);
			int pos = Integer.parseInt(arr[1]);
			Feature f = new Feature(new Location(pos, pos));
			f.addQualifier("reference", arr[2]);
			f.addQualifier("consensus", arr[3]);
			f.addQualifier("phred-like consensus quality", arr[4]);
			f.addQualifier("read depth", arr[5]);
			f.addQualifier("average coverage", arr[6]);
			f.addQualifier("highests mapping quality", arr[7]);
			f.addQualifier("minimum consensus quality", arr[8]);
			f.addQualifier("second best call", arr[9]);
			f.addQualifier("log likelihood ratio second and third best call",
					arr[10]);
			f.addQualifier("third best call", arr[11]);
			f.setType(t);
			fa.add(f);

		}
		return set;

	}

}
