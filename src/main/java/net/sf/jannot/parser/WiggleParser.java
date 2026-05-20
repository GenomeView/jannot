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
		final Map<Integer, Float> values = new HashMap<>();
		final LineIterator it = new LineIterator(is);
		it.setSkipComments(true);
		it.setCommentIdentifier("#");
		it.addCommentIdentifier("browser ");

		try {

			// TroveArrayWiggle daw = null; // current data container
			boolean variable = false;
			int step = 0;
			int span = 1;
			int start = 0;
			int stepOffset = 1;
			int namenr = 1;
			String name = "unnamed";
			Entry e = null;
			for (String line : it) {
				if (line.startsWith("track")) {
					completeTrack(e, name, values);
					Map<String, String> lineMap = BEDTools.parseTrack(line);
					name = lineMap.get("name");
					if (name == null) {
						name = "unnamed-" + (namenr++);
					}

				} else if (line.startsWith("variableStep")) {
					final Map<String, String> lineMap = BEDTools
							.parseTrack(line);
					if (!lineMap.containsKey("chrom")) {
						throw new IOException(
								"variableStep lacks 'chrom' key: " + line);
					}
					e = set.getOrCreateEntry(lineMap.get("chrom").trim());
					span = 1;
					variable = true;
					if (lineMap.containsKey("span")) {
						span = Integer.parseInt(lineMap.get("span"));
					}

				} else if (line.startsWith("fixedStep")) {
					final Map<String, String> lineMap = BEDTools
							.parseTrack(line);
					if (!lineMap.containsKey("chrom")) {
						throw new IOException(
								"variableStep lacks 'chrom' key: " + line);
					}
					if (!lineMap.containsKey("step")) {
						throw new IOException(
								"fixedStep lacks 'step' key: " + line);
					}
					if (!lineMap.containsKey("start")) {
						throw new IOException(
								"fixedStep lacks 'start' key: " + line);
					}

					e = set.getOrCreateEntry(lineMap.get("chrom").trim());

					variable = false;
					stepOffset = 0;
					span = 1;
					if (lineMap.containsKey("span")) {
						span = Integer.parseInt(lineMap.get("span").trim());
					}
					step = Integer.parseInt(lineMap.get("step").trim());
					start = Integer.parseInt(lineMap.get("start").trim());

				} else if (variable) {
					// we are in variable-mode
					String[] arr = line.split("[ \t]+");
					int s = Integer.parseInt(arr[0]);
					double val = Double.parseDouble(arr[1]);

					for (int i = s; i < s + span; i++) {
						values.put(i, (float) val);
					}
				} else {
					// we are in not-variable so in fixed mode
					double val = Double.parseDouble(line);

					for (int i = start + stepOffset; i < start + stepOffset
							+ span; i++) {
						values.put(i, (float) val);
					}
					stepOffset += step;
				}
			}
			// at end we just fall out of the loop. Finish the last track.
			completeTrack(e, name, values);
		} catch (Exception ioex) {
			ioex.printStackTrace();
			getLog().log(Level.SEVERE, "Failed to read data", ioex);
		}

		return set;
	}

	/**
	 * Add values to entry e under the name. Then cleans out the map
	 * 
	 * @param e      the {@link Entry}. If null/not set, nothing happens.
	 * @param name   the name to use in the entry
	 * @param values the Map<Integer,Float> to add
	 */
	private void completeTrack(Entry e, String name,
			Map<Integer, Float> values) {
		if (e != null) {
			TroveArrayWiggle daw = new TroveArrayWiggle(getGlobal(), values);
			e.add(new StringKey(name), daw);
			values.clear();
		}
	}

}
