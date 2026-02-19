/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.InputStream;

import be.abeel.io.LineIterator;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.Location;
import net.sf.jannot.MemoryFeatureAnnotation;
import net.sf.jannot.Strand;
import net.sf.jannot.Type;

/**
 * 
 * Sequin file format parser.
 * 
 * http/www.ncbi.nlm.nih.gov/Sequin/table.html
 * 
 * New specs seem on
 * http://wiki.christophchamp.com/index.php?title=TAB_file_format
 * 
 * @author Thomas Abeel
 * 
 */
public class TBLParser extends Parser {

	/**
	 * @param dataKey
	 */
	public TBLParser() {
		super(null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see net.sf.jannot.parser.Parser#parse(java.io.InputStream,
	 *      net.sf.jannot.source.DataSource, net.sf.jannot.EntrySet)
	 **/
	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		if (set == null)
			set = new EntrySet();
		LineIterator it = new LineIterator(is);
		Entry current = null;
		Feature currentF = null;
		for (String line : it) {
			if (line.strip().isEmpty())
				continue;// skip empty lines
			if (line.startsWith(">")) {
				current = set.getOrCreateEntry(line.split(">Feature ")[1]);
			} else {
				if (current == null) {
					current = set.getOrCreateEntry("default");
				}

				String[] arr = line.split("[ \t]+", 3);
				if (arr.length == 3) {
					if (arr[0].equals("")) {
						// \t <name> <value>
						currentF.addQualifier(arr[1], arr[2]);
					} else {
						// <start> <end> <gene>

						int s = Integer.parseInt(arr[0]);
						int t = Integer.parseInt(arr[1]);
						currentF = new Feature(new Location(s, t));
						if (s > t)
							currentF.setStrand(Strand.REVERSE);
						else
							currentF.setStrand(Strand.FORWARD);
						currentF.setType(Type.get(arr[2]));
						// current.annotation.add(currentF);
						MemoryFeatureAnnotation fa = current
								.getMemoryAnnotation(currentF.type());
						fa.add(currentF);
					}

				} else {
					// <start> <end> : another feature
					int s = Integer.parseInt(arr[0]);
					int t = Integer.parseInt(arr[1]);
					currentF.addLocation(new Location(s, t));
				}
			}
		}
		return set;
	}

}
