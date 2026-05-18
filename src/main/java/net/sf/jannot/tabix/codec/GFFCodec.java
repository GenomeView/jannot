/**
 * %HEADER%
 */
package net.sf.jannot.tabix.codec;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import net.sf.jannot.Feature;
import net.sf.jannot.Global;
import net.sf.jannot.Location;
import net.sf.jannot.Strand;
import net.sf.jannot.tabix.FeatureWrapper;
import net.sf.jannot.tabix.TabixLine;

/**
 * @author Thomas Abeel
 * 
 */
public class GFFCodec extends Codec<Feature> {

	private FeatureWrapper wrapper;

	/**
	 * @param wrapper
	 * @param in
	 */
	public GFFCodec(FeatureWrapper wrapper, Iterable<TabixLine> in,
			Global global) {
		super(in, 1024, global);
		this.wrapper = wrapper;
	}

	@Override
	public Feature parse(TabixLine line) {
		Feature f = lru.get(line);
		if (f != null) {
			return f;
		} else {

			try {
				Location l = new Location(line.getInt(3), line.getInt(4));
				SortedSet<Location> tmp = new TreeSet<Location>();
				tmp.add(l);

				Strand str = Strand.UNKNOWN;
				char strand = line.get(6).charAt(0);
				switch (strand) {
				case '-':
					str = (Strand.REVERSE);
					break;
				case '+':
					str = (Strand.FORWARD);
					break;
				// case '.' '?' and default:
				}

				f = new Feature(tmp, global.typeFactory().get(line.get(2)),
						str);
//				f.setLocation(tmp);
				f.addQualifier("source", line.get(1));
				String five = line.get(5);
				if (!(five.length() == 1 && five.charAt(0) == '.')
						&& five.length() != 0) {
					f.setScore(Double.parseDouble(five));
				}
				if (line.length() > 8) {
					String[] attributes = line.get(8).split(";");
					for (String s : attributes) {
						String[] pair = s.trim().split("=");
						if (pair.length == 2) {
							String[] values = pair[1].split(",");
							for (String v : values) {
								f.addQualifier(pair[0], v);
							}
						} else {
							f.addQualifier("note", pair[0]);
						}
					}
				}
				return f;

			} catch (Exception e) {
				getLog().log(Level.WARNING, "can't parse line" + line, e);
				return null;
			}
		}
	}

}
