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
import net.sf.jannot.Global;
import net.sf.jannot.Strand;
import net.sf.jannot.alignment.maf.AbstractAlignmentSequence;
import net.sf.jannot.alignment.maf.MAFMemoryMultipleAlignment;
import net.sf.jannot.alignment.maf.MemoryAlignmentBlock;
import net.sf.jannot.alignment.maf.MemoryAlignmentSequence;
import net.sf.jannot.refseq.MemorySequence;

/**
 * MAF files are Multiple Alignment Format and are specified on
 * https://genome.ucsc.edu/FAQ/FAQformat.html#format5
 * 
 * . They are plain text files and contain a comment line "## maf" near the top
 * of the file.
 * 
 * @author Thomas Abeel
 * 
 */
public class MAFParser extends Parser {

	/**
	 * @param dataKey the datakey for the data to parse
	 * @param global  the {@link Global} with globally used objects
	 */
	public MAFParser(DataKey dataKey, Global global) {
		super(dataKey, global);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		LineIterator it = new LineIterator(is);
		it.setCommentIdentifier("#");
		it.setSkipBlanks(true);
		MemoryAlignmentBlock a = null;
		Entry entry = null;
		MAFMemoryMultipleAlignment ma = null;
		boolean first = true;// true iff first line after 'a'
		int row = 1;
		for (final String line : it) {
			if (line.charAt(0) == 'a') {
				first = true;
			} else if (line.charAt(0) == 's') {

				String[] arr = line.split("[ \t]+");
				if (arr.length != 7) {
					getLog().log(Level.SEVERE, "line " + row
							+ ": expected 7 columns but found " + line);
					break;
				}

				if (first) {
					// first line is reference genome.
//					final String[] names = arr[1].split("\\.");
//					final String name = names[names.length - 1];
					entry = set.getOrCreateEntry(arr[1]);
					if (entry.get(dataKey) != null) {
						ma = (MAFMemoryMultipleAlignment) entry.get(dataKey);
					} else {
						ma = new MAFMemoryMultipleAlignment(getGlobal());
						entry.add(dataKey, ma);
					}
				}

				MemorySequence seq = new MemorySequence(arr[6], getGlobal());
				AbstractAlignmentSequence s = new MemoryAlignmentSequence(
						arr[1], Integer.parseInt(arr[2]),
						Integer.parseInt(arr[3]), Integer.parseInt(arr[5]),
						Strand.fromSymbol(arr[4].charAt(0)), seq);
				if (first) {
					first = false;
					a = new MemoryAlignmentBlock(s.start(), s.end());
					ma.add(a);
				}
				a.add(s);
				ma.addSpecies(arr[1]);

			}
			row++;
		}
		return set;
	}
}
