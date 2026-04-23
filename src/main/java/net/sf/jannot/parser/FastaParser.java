/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import be.abeel.io.LineIterator;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.alignment.ReferenceSequence;
import net.sf.jannot.alignment.mfa.Alignment;
import net.sf.jannot.alignment.mfa.AlignmentAnnotation;
import net.sf.jannot.refseq.MemorySequence;
import net.sf.jannot.refseq.Sequence;
import tudelft.utilities.logging.Reporter;

/**
 * In case of multiple alignments it is strongly advised to set the dataKey
 * 
 * @author Thomas Abeel
 * 
 */
public class FastaParser extends Parser {
	/**
	 * Flag to indicate whether the parser should always force sequences to go
	 * into separate entries, even if they look like a multiple alignment.
	 */
	public static boolean forceEntries = false;

	public FastaParser(Reporter log) {
		super(null, log);
	}

	/**
	 * @param dataKey
	 */
	public FastaParser(DataKey dataKey, Reporter log) {
		super(dataKey, log);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		final LineIterator it = new LineIterator(is);
		StringBuffer current = null; // accumulates all lines after last '>'
		final ArrayList<StringBuffer> seq = new ArrayList<StringBuffer>();
		final ArrayList<String> names = new ArrayList<String>();
		final ArrayList<String> description = new ArrayList<String>();

		// load names and current lists with matching name-content.
		for (String line : it) {

			if (line.startsWith(">")) {
				names.add(line.substring(1).trim().split("[ \t]+", 2)[0]);
				current = new StringBuffer(); // will be filled later in loop
				seq.add(current);
				description.add(line.substring(1).trim());

				// current.description.setPrimaryAccessionNumber(line.substring(1).split("
				// ")[0].split("\t")[0]);

			} else {
				current.append(line);
			}
		}
		if (!forceEntries && likelyMultipleAlign(seq)) {
			AlignmentAnnotation alignAnnot = new AlignmentAnnotation(getLog());
			Entry ref = set.getOrCreateEntry(names.get(0));

			if (ref != null) {
				List<Alignment> alist = new ArrayList<Alignment>();
				ReferenceSequence rs = new ReferenceSequence(seq.get(0),
						getLog());
				for (int i = 0; i < seq.size(); i++) {
					Alignment align = new Alignment(names.get(i),
							new MemorySequence(seq.get(i), getLog()), rs);
					alist.add(align);
				}
				alignAnnot.addAll((Iterable<Alignment>) alist);
				ref.add(dataKey, alignAnnot);
			}
		} else {
			for (int i = 0; i < seq.size(); i++) {
				Entry e = set.getOrCreateEntry(names.get(i));
				e.setSequence(new MemorySequence(seq.get(i), getLog()));
			}
		}

		return set;
	}

	/**
	 * Check if all sequences are the same size and if there is no other data
	 * besides the sequences.
	 */
	private boolean likelyMultipleAlign(ArrayList<StringBuffer> data) {
		HashSet<Integer> lengths = new HashSet<Integer>();
		lengths.add(0);
		for (StringBuffer e : data) {
			lengths.add(e.length());

		}
		return lengths.size() == 2 && data.size() > 1;

	}

	@Override
	public void write(OutputStream os, Entry entry) {
		PrintWriter out = new PrintWriter(new BufferedOutputStream(os));
		if (entry.description.get("header") != null) {
			out.println(">" + entry.description.get("header"));
		} else {
			out.println(">" + entry.getID());
		}
		Sequence seq = entry.sequence();
		int i = 1;
		for (; i < seq.size() - 80; i += 80) {
			out.println(ss(seq.get(i, i + 80), 80));
		}
		out.println(ss(seq.get(i, seq.size() + 1), seq.size() - i + 1));

		// }
		out.flush();

	}

	/**
	 * @param iterable
	 * @return
	 */
	private char[] ss(Iterable<Character> iterablem, int len) {
		char[] out = new char[len];
		int idx = 0;
		for (char c : iterablem) {
			out[idx++] = c;
		}
		return out;

	}

}
