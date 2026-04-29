/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.FeatureAnnotation;
import net.sf.jannot.Global;
import net.sf.jannot.MemoryFeatureAnnotation;
import net.sf.jannot.Type;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class BEDParser extends Parser {

	private String defaultType;

	/**
	 * @param dataKey
	 * @param global  the {@link Reporter} to log issues to
	 */
	public BEDParser(String fileName, Global global) {
		super(null, global);
		String[] arr = fileName.replace('\\', '/').split("/");
		defaultType = arr[arr.length - 1];

	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		Type type = null;
		LineIterator it = new LineIterator(is);
		it.setSkipBlanks(true);
		it.setSkipComments(true);
		for (String line : it) {
			/* Handle header lines */
			if (line.startsWith("track")) {
				String name = BEDTools.parseTrack(line).get("name");
				getLog().log(Level.INFO, BEDTools.parseTrack(line).toString());
				getLog().log(Level.INFO, "NAME: " + name);
				if (name != null) {
					type = Type.get(name);
				}
				continue;
			} else if (line.startsWith("browser")) {
				continue;
			}
			Feature f = BEDTools.parseLine(line, type, defaultType);
			MemoryFeatureAnnotation fa = set
					.getOrCreateEntry(f.qualifier("chrom"))
					.getMemoryAnnotation(f.type());// (FeatureAnnotation)
			fa.add(f);

		}
		return set;
	}

	@Override
	public void write(OutputStream os, Entry entry) throws IOException {

		PrintWriter out = new PrintWriter(os);

		for (DataKey data : entry) {

			if (entry.get(data) instanceof FeatureAnnotation) {
				String headerLine = "track name=\"" + entry.get(data).label()
						+ "\"";
				if (entry.description.keys().size() > 0) {
					headerLine += " description=\""
							+ entry.description.toString() + "\"";
				}
				out.println(headerLine);
				FeatureAnnotation fa = (FeatureAnnotation) entry.get(data);
				for (Feature f : fa.get()) {
					out.println(line(f, entry.getID(), entry));
				}
			}

		}

		out.flush();

	}

	private String line(Feature f, String acc, Entry e) {

		StringBuffer out = new StringBuffer();

		out.append(e.getID() + "\t");
		out.append((f.start() - 1) + "\t");
		out.append(f.end() + "\t");
		out.append(f.qualifier("Name") + "\t");
		out.append(f.qualifier("score") + "\t");
		out.append(f.strand().symbol() + "\t");
		out.append((f.start() - 1) + "\t");
		out.append(f.end() + "\t");
		if (f.getColor() != null) {
			out.append(f.getColor().toString());
		} else {
			out.append("");
		}
		return out.toString();
	}

}
