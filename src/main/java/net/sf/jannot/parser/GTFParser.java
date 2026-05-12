/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.collections.map.Flat3Map;

import be.abeel.io.LineIterator;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.FeatureAnnotation;
import net.sf.jannot.Global;
import net.sf.jannot.Location;
import net.sf.jannot.MemoryFeatureAnnotation;
import net.sf.jannot.Strand;
import net.sf.jannot.Type;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class GTFParser extends Parser {

	/**
	 * @param dataKey
	 */
	public GTFParser(Global global) {
		super(null, global);
	}

	/**
	 * Will return an entry for each unique seq_id
	 */
	@Override
	public EntrySet parse(InputStream is, EntrySet set) {

		/* Keeps track of which features have the same ID */
		Map<String, Feature> parentMap = new HashMap<String, Feature>();
		LineIterator it = new LineIterator(is, true, true);

		Map<String, String> quals = new Flat3Map();
		for (String line : it) {
			String[] arr = line.trim().split("\t");

			try {
				if (arr.length < 9) {
					arr = padGff(arr);
				}

				quals.clear();
				parseQualifiers(arr[8], quals);

				Location l = new Location(Integer.parseInt(arr[3]),
						Integer.parseInt(arr[4]));
				String parent = extractParent(quals, arr[2], arr[0]);

				/* Add to existing feature */
				if (parent != null && parentMap.containsKey(parent)) {

					parentMap.get(parent).addLocation(l);

				} else {/* Add as a new feature */
					Strand str = Strand.UNKNOWN;
					char strand = arr[6].charAt(0);
					switch (strand) {
					case '-':
						str = (Strand.REVERSE);
						break;
					case '+':
						str = (Strand.FORWARD);
						break;
					// case '.', '?': UNKNOWN
					}

					Feature f = new Feature(l, Type.get(arr[2]), str);

					f.addQualifier("source", arr[1]);
					if (!(arr[5].length() == 1 && arr[5].charAt(0) == '.')
							&& arr[5].length() != 0) {
						f.setScore(Double.parseDouble(arr[5]));
					}
					for (java.util.Map.Entry<String, String> me : quals
							.entrySet()) {
						f.addQualifier(me.getKey(), me.getValue());
					}
					if (parent != null) {
						parentMap.put(parent, f);
					}
					MemoryFeatureAnnotation fa = set.getOrCreateEntry(arr[0])
							.getMemoryAnnotation(f.type());
					fa.add(f);
				}

			} catch (Exception e) {
				getLog().log(Level.WARNING,
						"Could not parse line: " + Arrays.toString(arr), e);
			}

		}
		parentMap = null;
		return set;
	}

	/**
	 * @param string
	 * @param quals
	 */
	private void parseQualifiers(String qq, Map<String, String> quals) {
		String[] arr = qq.split(";");
		for (String s : arr) {
			s = s.trim();
			if (s.length() > 0) {
				int i = s.indexOf(' ');
				String key = "note";
				if (i >= 0) {
					key = s.substring(0, i);
				}
				key = key.trim();

				String value = s.substring(i + 1, s.length());
				value = value.trim().replaceAll("\"", "");

				if (quals.containsKey(key)) {
					quals.put(key, quals.get(key) + "," + value);
				} else {
					quals.put(key, value);
				}
			}
		}

	}

	public static String[] padGff(String[] arr) {
		String[] newArray = new String[9];
		int fullToken = 0;
		for (String token : arr) {
			newArray[fullToken++] = token;
		}
		for (int emptyToken = fullToken; emptyToken < newArray.length; emptyToken++) {
			newArray[emptyToken] = ".";
		}
		return newArray;
	}

	public static String extractParent(Map<String, String> quals, String type,
			String chromosome) {
		String out = quals.get("transcript_id");
//		if (out == null)
//			out = quals.get("gene_id");
		if (out != null) {
			out = chromosome + "$$" + type + "$$" + out;
		}
		return out;

	}

	// private static String tryParent(String line, String key) {
	// if (!line.contains(key))
	// return null;
	// String[] arr = line.split(";");
	// for (String s : arr) {
	// if (s.trim().startsWith(key))
	// return s.trim().split("=")[1].trim();
	// }
	// return null;
	// }

	@Override
	public void write(OutputStream os, Entry entry, DataKey[] dks)
			throws IOException {

		PrintWriter out = new PrintWriter(os);
		for (DataKey dk : dks) {
			if (entry.get(dk) instanceof FeatureAnnotation) {
				FeatureAnnotation fa = (FeatureAnnotation) entry.get(dk);
				for (Feature f : fa.get()) {
					for (int i = 0; i < f.location().length; i++) {
						out.println(line(entry, f, entry.getID(), i));
					}
				}
			}
		}
		out.flush();
	}

	private String line(Entry e, Feature f, String acc, int idx) {
		StringBuffer out = new StringBuffer();
		out.append(e.getID() + "\t");
		out.append(f.qualifier("source") + "\t");
		out.append(f.type() + "\t");
		out.append(f.location()[idx].start() + "\t");
		out.append(f.location()[idx].end() + "\t");
		out.append(f.getScore() + "\t");
		out.append(f.strand().symbol() + "\t.\t");
		StringBuffer qualifiers = new StringBuffer();
		for (String s : f.getQualifiersKeys()) {
			if (!s.equals("source") && !s.equals("seqid")) {
				qualifiers.append(";" + s + " ");
				qualifiers.append("\"" + f.qualifier(s) + "\"");

			}

		}
		if (qualifiers.length() > 0) {
			out.append(qualifiers.substring(1));
		} else {
			out.append("no qualifiers");
		}
		return out.toString();
	}

}
