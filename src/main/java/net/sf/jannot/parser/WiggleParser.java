/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import net.sf.jannot.Data;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.StringKey;
import net.sf.jannot.wiggle.TroveArrayWiggle;

/**
 * 
 * @author Thomas
 *
 */
public class WiggleParser extends Parser {

	public WiggleParser(Global global) {
		super(null, global);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		try {
			LineIterator it = new LineIterator(is);
			it.setSkipComments(true);
			it.setCommentIdentifier("#");
			it.addCommentIdentifier("browser ");

			TroveArrayWiggle daw = null; // current data container
			boolean variable = false;
			int step = 0;
			int span = 1;
			int start = 0;
			int stepOffset = 1;
			String name = "" + System.currentTimeMillis();
			Entry e = null;
			for (String line : it) {
				if (line.startsWith("track")) {
					Map<String, String> lineMap = BEDTools.parseTrack(line);
					name = lineMap.get("name");
//					String chr = lineMap.get("chrom");
//					if (chr == null) {
//						throw new IOException(
//								"track line misses 'chrom' key/value. ignoring "
//										+ line);
//					}
//					e = set.getEntry(chr);

				} else if (line.startsWith("variableStep")) {
					final Map<String, String> lineMap = BEDTools
							.parseTrack(line);
					if (!lineMap.containsKey("chrom")) {
						throw new IOException(
								"variableStep lacks 'chrom' key: " + line);
					}
					e = set.getOrCreateEntry(lineMap.get("chrom").trim());
					daw = new TroveArrayWiggle(e.getMaximumLength(),
							getGlobal());

					add(e, name, daw);

//					String[] arr = line.split("[ \t]+");
					span = 1;
					variable = true;
					if (lineMap.containsKey("span")) {
						span = Integer.parseInt(lineMap.get("span"));
					}

//					for (String s : arr) {
//						String[] kv = s.split("=");
//						if (kv[0].equals("span")) {
//							span = Integer.parseInt(kv[1].trim());
//						}
//						if (kv[0].equals("chrom")) {
//							e = set.getOrCreateEntry(kv[1].trim());
//							daw = new TroveArrayWiggle(e.getMaximumLength(),
//									getGlobal());
//
//						}
//
//					}
				} else if (line.startsWith("fixedStep")) {
					final Map<String, String> lineMap = BEDTools
							.parseTrack(line);
					if (!lineMap.containsKey("chrom")) {
						throw new IOException(
								"variableStep lacks 'chrom' key: " + line);
					}
					e = set.getOrCreateEntry(lineMap.get("chrom").trim());

					variable = false;

					stepOffset = 1;

					if (lineMap.containsKey("span")) {
						span = Integer.parseInt(lineMap.get("span").trim());
					}
					if (lineMap.containsKey("step")) {
						step = Integer.parseInt(lineMap.get("step").trim());
					}
					if (lineMap.containsKey("start")) {
						start = Integer.parseInt(lineMap.get("start").trim());
					}
					daw = new TroveArrayWiggle(e.getMaximumLength(),
							getGlobal());
					add(e, name, daw);

//					if (e == null) {
//						e = set.iterator().next();
//					}
//					add(e, name, daw);

//					String[] arr = line.split("[ \t]+");

//					for (String s : arr) {
//						String[] kv = s.split("=");
//						if (kv[0].equals("span")) {
//							span = Integer.parseInt(kv[1]);
//						}
//						if (kv[0].equals("step")) {
//							step = Integer.parseInt(kv[1]);
//						}
//						if (kv[0].equals("start")) {
//							start = Integer.parseInt(kv[1]);
//						}
//						if (kv[0].equals("chrom")) {
//							e = set.getOrCreateEntry(kv[1].trim());
//							daw = new TroveArrayWiggle(e.getMaximumLength(),
//									getGlobal());
//						}
//					}
				} else if (variable) {
					// we are in variable-mode
					String[] arr = line.split("[ \t]+");
					int s = Integer.parseInt(arr[0]);
					double val = Double.parseDouble(arr[1]);

					for (int i = s; i < s + span; i++) {
						daw.set(i, (float) val);
					}
				} else {
					// we are in not-variable so in fixed mode
					double val = Double.parseDouble(line);

					for (int i = start + stepOffset; i < start + stepOffset
							+ span; i++) {
						daw.set(i, (float) val);
					}
					stepOffset += step;
				}
			}
		} catch (Exception ioex) {
			ioex.printStackTrace();
			getLog().log(Level.SEVERE, "Failed to read data", ioex);
		}

		return set;
	}

	private Map<String, String> parseLine(String line) {
		String[] arr = line.split("[ \t]+");

		Map<String, String> map = new HashMap<>();
		for (String s : arr) {
			String[] kv = s.split("=");
			map.put(kv[0], kv[1]);
		}
		return map;
	}

	/**
	 * @param e    the {@link Entry} to be extended
	 * @param name the key for the new data
	 * @param daw  the {@link Data} to get, in this case
	 */
	private void add(Entry e, String name, TroveArrayWiggle daw) {
		/* Add the previous one */
		if (daw != null) {
			daw.init();
			e.add(new StringKey(name), daw);

			daw = null;
		}

	}

}
