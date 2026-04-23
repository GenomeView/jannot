/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.Location;
import net.sf.jannot.MemoryFeatureAnnotation;
import net.sf.jannot.Type;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class VCFParser extends Parser {

	enum Variation {
		Match, SingleSubstitution, LongSubstitution, SingleDeletion,
		LongDeletion, SingleInsertion, LongInsertion;

	}

	/**
	 * @param dataKey
	 * @param log     the {@link Reporter} to log issues to
	 */
	VCFParser(String fileName, Reporter log) {
		super(getType(fileName), log);
	}

	private static Type getType(String fileName) {
		String[] arr = fileName.replace('\\', '/').split("/");
		return Type.get(arr[arr.length - 1]);

	}

	/**
	 * Will return an entry for each unique seq_id
	 */
	@Override
	public EntrySet parse(InputStream is, EntrySet set) {

		LineIterator it = new LineIterator(is, true, true);

		for (String line : it) {
			final String[] arr = line.trim().split("\\s+");
			Entry e = set.getOrCreateEntry(arr[0]);
			MemoryFeatureAnnotation annot = e.getMemoryAnnotation(dataKey);
			if (arr.length < 7) {
				getLog().log(Level.SEVERE,
						"Not sufficient columns " + Arrays.toString(arr));
				break;
			}
			/*
			 * Only parse lines that pass the filters
			 */
			String filter = arr[6];
			if (!(filter.equalsIgnoreCase("PASS") || filter.equals("."))) {
				continue;
			}

			int pos = Integer.parseInt(arr[1]);

			final String id = arr[2];

			final String ref = arr[3];

			final String multiAlt = arr[4];

			for (final String alt : multiAlt.split(",")) {

				double score = 0;
				if (arr[5].charAt(0) != '.') {
					score = Double.parseDouble(arr[5]);
				}

				int refLength = ref.length();

				int altLength = alt.length();
				Variation variation;
				try {
					variation = getVariation(ref, alt);
				} catch (IllegalArgumentException err) {
					getLog().log(Level.SEVERE, "Failed to get variation", err);
					break;
				}
				/*
				 * Only include differences, don't load matches
				 */
				if (variation == Variation.Match) {
					continue;
				}
				int end = pos + refLength - 1;
				int start = pos;
				// if(variation==Variation.SingleSubstitution||variation==Variation.SingleInsertion||variation==Variation.LongInsertion)
				// end=pos;
				if (variation == Variation.SingleDeletion
						|| variation == Variation.LongDeletion) {
					start = pos + 1;
					// end=pos+refLength;
				}

				final Feature f = new Feature(new Location(start, end));
				f.setQualifier("id", id);
				f.setQualifier("ref", ref);
				f.setQualifier("alt", alt);
				f.setQualifier("score", "" + score);

				/*
				 * Add winglets for large events
				 */
				if (variation == Variation.LongInsertion) {
					f.addLocation(new Location(pos - altLength / 2,
							pos - altLength / 2));
					f.addLocation(new Location(pos + altLength / 2,
							pos + altLength / 2));
				}
				/*
				 * Add additional winglets for unclear calls
				 */
				if (variation == Variation.LongInsertion && alt.contains("N")) {
					f.addLocation(
							new Location(pos - (int) ((altLength / 2) * 1.1),
									pos - (int) ((altLength / 2) * 1.1)));
					f.addLocation(
							new Location(pos + (int) ((altLength / 2) * 1.1),
									pos + (int) ((altLength / 2) * 1.1)));
				}

				int delta = altLength - refLength;
				if (variation == Variation.LongSubstitution
						&& refLength < altLength) {
					f.addLocation(
							new Location(start - delta / 2, start - delta / 2));
					f.addLocation(
							new Location(end + delta / 2, end + delta / 2));
					if (alt.contains("N")) {
						f.addLocation(
								new Location(start - (int) ((delta / 2) * 1.1),
										start - (int) ((delta / 2) * 1.1)));
						f.addLocation(
								new Location(end + (int) ((delta / 2) * 1.1),
										end + (int) ((delta / 2) * 1.1)));
					}
				}

				f.addQualifier("delta", "" + delta);
				f.setType(Type.get(variation.toString()));
				annot.add(f);

			}
			// lazy val blankFilter=filter.equals(".")

		}
		return set;
	}

	/**
	 * 
	 * @param ref
	 * @param alt
	 * @return the {@link Variation}
	 * @throws IllegalArgumentException if the values are inconsistent
	 */
	private Variation getVariation(final String ref, String alt)
			throws IllegalArgumentException {
		if (ref.length() == alt.length()) {
			if (alt.equals(".") || ref.equals(alt)) {
				return Variation.Match;
			} else {
				if (ref.length() == 1) {
					return Variation.SingleSubstitution;
				} else {
					return Variation.LongSubstitution;
				}
			}
		} else {
			// assume(ref.length() > 0 && alt.length() > 0)
			if (ref.length() == 1 || alt.length() == 1) {
				int diff = ref.length() - alt.length();
				// if (ref.length() > alt.length())
				if (diff > 1) {
					return Variation.LongDeletion;
				} else if (diff > 0) {
					return Variation.SingleDeletion;
				} else if (diff < -1) {
					return Variation.LongInsertion;
				} else if (diff < 0) {
					return Variation.SingleInsertion;
				} else {
					throw new IllegalArgumentException(
							"illegal value for diff");
				}
			} else {
				return Variation.LongSubstitution;
			}

		}
	}
}
