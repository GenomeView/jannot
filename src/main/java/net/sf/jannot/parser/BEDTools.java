/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.tribble.bed.BEDCodec;
import net.sf.jannot.Feature;
import net.sf.jannot.Location;
import net.sf.jannot.Strand;
import net.sf.jannot.Type;

/**
 * @author Thomas Abeel
 * 
 */
public class BEDTools {

	/**
	 * 
	 * @param line        an input line with '\t' separated strings. See
	 *                    {@link BEDCodec}
	 * @param type        the data type. If null, defaultType is used
	 * @param defaultType the defaultType. If null, "CDS" is used if arr length
	 *                    ==12, else "BED"
	 * @return
	 */
	public static Feature parseLine(final String line, Type type,
			final String defaultType) {
		/* Any other lines */
		final String[] arr = line.split("\t");
		if (type == null) {
			if (defaultType != null) {
				type = Type.get(defaultType);
			} else {
				if (arr.length == 12) {
					type = Type.get("CDS");
				} else {
					type = Type.get("BED");
				}
			}
		}

		final Feature f;
		SortedSet<Location> tmp = getLocations(arr);
		if (tmp.size() > 0) {
			f = new Feature(tmp);
		} else {
			f = new Feature(getPseudoGeneLocations(arr));
			f.setType(Type.get("pseudoGene"));
		}

		f.setType(type);
		/* Chromosome */
		f.addQualifier("chrom", arr[0]);

		/* Optional name */
		if (arr.length > 3) {
			f.addQualifier("Name", arr[3]);
		}
		/*
		 * If user only specified 4 column, (s)he may have forgotten the name
		 * column and put a score instead at position 3
		 */
		try {
			if (arr.length == 4) {
				double score = Double.parseDouble(arr[3]);
				f.setScore(score);
			}
		} catch (NumberFormatException e) {
			// no worries, nothing wrong
		}

		/* Optional score */
		if (arr.length > 4) {
			f.setScore(Double.parseDouble(arr[4]));
		}

		/* Optional strand */
		if (arr.length > 5) {
			char strand = arr[5].charAt(0);
			switch (strand) {
			case '-':
				f.setStrand(Strand.REVERSE);
				break;
			case '+':
				f.setStrand(Strand.FORWARD);
				break;
			case '.':
			case '?':
				f.setStrand(Strand.UNKNOWN);
				break;
			}
		}
		/* Optional color */
		if (arr.length > 8) {
			f.addQualifier("color", arr[8]);
		}

		return f;
	}

	/**
	 * 
	 * @param arr
	 * @param f
	 * @return Set of Location. If empty, this is a Non-coding gene and you need
	 *         to call {@link #getPseudoGeneLocations(String[])}
	 * 
	 */
	private static SortedSet<Location> getLocations(final String[] arr) {
		Location chromLoc = new Location(Integer.parseInt(arr[1]) + 1,
				Integer.parseInt(arr[2]));

		/* TODO implement other optional fields */
		SortedSet<Location> tmp = new TreeSet<Location>();
		if (arr.length == 12) {
			Location codingLoc = new Location(Integer.parseInt(arr[6]) + 1,
					Integer.parseInt(arr[7]));

			int count = Integer.parseInt(arr[9]);
			String[] arrSize = arr[10].split(",");
			String[] arrStart = arr[11].split(",");

			for (int i = 0; i < count; i++) {
				int start = Integer.parseInt(arrStart[i].trim())
						+ chromLoc.start();
				int end = Integer.parseInt(arrSize[i].trim()) + start - 1;
				Location loc = new Location(start, end);
				if (loc.start() < codingLoc.start()
						&& loc.end() > codingLoc.start()) {
					loc.setStart(codingLoc.start());
				}
				if (loc.start() < codingLoc.end()
						&& loc.end() > codingLoc.end()) {
					loc.setEnd(codingLoc.end());
				}
				if (loc.start() >= codingLoc.start()
						&& loc.end() <= codingLoc.end()) {
					tmp.add(loc);
				}

			}
		} else {

			tmp.add(chromLoc);

		}
		return tmp;
	}

	private static SortedSet<Location> getPseudoGeneLocations(
			final String[] arr) {
		final Location chromLoc = new Location(Integer.parseInt(arr[1]) + 1,
				Integer.parseInt(arr[2]));
		final SortedSet<Location> tmp = new TreeSet<Location>();

		int count = Integer.parseInt(arr[9]);
		final String[] arrSize = arr[10].split(",");
		final String[] arrStart = arr[11].split(",");

		for (int i = 0; i < count; i++) {
			int start = Integer.parseInt(arrStart[i].trim()) + chromLoc.start();
			int end = Integer.parseInt(arrSize[i].trim()) + start - 1;
			tmp.add(new Location(start, end));
		}
		return tmp;

	}

	public static HashMap<String, String> parseTrack(String trackLine) {
		HashMap<String, String> out = new HashMap<String, String>();

		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern
				.compile("[\\S]+=[\\S\"']+|[\\S]+=\"[^\"]*\"|[\\S]+='[^']*'");
		Matcher regexMatcher = regex.matcher(trackLine);
		while (regexMatcher.find()) {
			matchList.add(regexMatcher.group());
		}

		for (String s : matchList) {
			// System.out.println("QQ=" + s);
			// String[] arr = trackLine.split(" ");
			// for (String s : arr) {
			String[] kv = s.split("=");
			if (kv.length > 1) {
				if (kv[1].charAt(0) == '\'' || kv[1].charAt(0) == '\"') {
					out.put(kv[0], kv[1].substring(1, kv[1].length() - 1));
				} else {
					out.put(kv[0], kv[1]);
				}
			} else {
				out.put(kv[0], null);
			}

		}
		// }
		return out;
	}
}
