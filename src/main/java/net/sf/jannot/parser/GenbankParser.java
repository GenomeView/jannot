/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.InputStream;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.MemoryFeatureAnnotation;
import net.sf.jannot.Type;
import net.sf.jannot.refseq.MemorySequence;
import tudelft.utilities.logging.Reporter;

/*
 * http://www.ncbi.nlm.nih.gov/Sitemap/samplerecord.html
 * 
 * http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&rettype=gb&id=NM_023037
 */
public class GenbankParser extends Parser {

	/**
	 * @param dataKey
	 * @param log     the {@link Reporter} to log issues to
	 */
	public GenbankParser(DataKey key, Reporter log) {
		super(key, log);

	}

	private Feature lastFeature = null;

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		Entry e = null;
		LineIterator it = new LineIterator(is);
		String locus = null;
		String definition = null;
		String version = null;
		boolean featureMode = false;
		boolean seqMode = false;
		for (String line : it) {
			if (line.startsWith("//")) {
				seqMode = false;
			}
			if (seqMode) {
				processSequenceLine(line, e);
			}
			if (line.startsWith("ORIGIN")) {
				addQualifiers(e);
				seqMode = true;
				featureMode = false;
			}

			if (featureMode) {
				if (line.startsWith("BASE COUNT")) {
					// Ignore line
				} else {
					try {
						processFeatureLine(line, e, it);
					} catch (ArrayIndexOutOfBoundsException
							| NumberFormatException err) {
						getLog().log(Level.SEVERE,
								"Offending line: " + line + " for entry " + e,
								err);
						// original parser continued anyway, so do we
					}
				}

			}
			if (line.startsWith("FEATURES")) {
				featureMode = true;
				seqMode = false;
			}

			if (line.startsWith("LOCUS")) {
				String[] arr = line.trim().split("[ ]+");
//				e = set.getOrCreateEntry(arr[1]);
				locus = arr[1];

			}
			if (line.startsWith("DEFINITION")) {
				definition = line.substring(10).trim();

			}
			if (line.startsWith("VERSION")) {
				String[] arr = line.trim().split("\\s+", 2);
//				e = set.getOrCreateEntry(arr[1]);
				version = arr[1];
//				if(version!=null)
				e.description.put("VERSION", version);

			}
			if (line.startsWith("ACCESSION")) {

				String[] arr = line.trim().split("[ ]+");
				// e.description.setID(arr[1]);
				if (arr.length == 1) {
					arr = new String[2];
					arr[1] = locus;
				}
				e = set.getOrCreateEntry(arr[1]);
//				
				if (locus != null) {
					e.description.put("LOCUS", locus);
				}
				if (definition != null) {
					e.description.put("DEFINITION", definition);
				}

			}

		}
		return set;
	}

	private StringBuffer qualifierBuffer = new StringBuffer();

	private void processFeatureLine(String line, Entry e, LineIterator it)
			throws ArrayIndexOutOfBoundsException, NumberFormatException {

		if (line.startsWith("                     ")) {
			if (line.trim().startsWith("/")) {
				qualifierBuffer.append("\n");
			}
			qualifierBuffer.append(line.trim());
		} else {
			if (lastFeature != null) {
				addQualifiers(e);
			}
			String nl = it.peek();
			while (nl.startsWith("                     ")
					&& !nl.trim().startsWith("/")) {
				line += it.next().trim();
				nl = it.peek();
			}

			String[] arr = line.trim().split(" [ ]+");
			lastFeature = new Feature(ParserTools.parseLocation(arr[1]));
			lastFeature.setType(Type.get(arr[0]));
			lastFeature.setStrand(ParserTools.getStrand(arr[1]));
			// System.out.println(arr[1]+"\t"+e.annotation.noFeatures());

		}

	}

	private void addQualifiers(Entry e) {
		String[] arr = qualifierBuffer.toString().split("\n");
		for (int i = 1; i < arr.length; i++) {
			if (arr[i].contains("=")) {

				String[] qarr = arr[i].split("=");
				lastFeature.addQualifier(qarr[0].substring(1), qarr[1]);
			} else {
				lastFeature.addQualifier(arr[i].substring(1), null);
			}

		}
		qualifierBuffer = new StringBuffer();
		MemoryFeatureAnnotation fa = e.getMemoryAnnotation(lastFeature.type());
		fa.add(lastFeature);
		// e.annotation.add(lastFeature);

	}

	private void processSequenceLine(String line, Entry e) {
		String seq = cleanSeq(line);
		((MemorySequence) e.sequence()).addSequence(seq);

	}

	private String cleanSeq(String line) {
		String out = line.replaceAll("[0-9 ]", "");
		return out;
	}

}
