/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import net.sf.jannot.DataKey;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Location;
import net.sf.jannot.Strand;
import net.sf.jannot.StringKey;
import net.sf.jannot.SyntenicBlock;
import net.sf.jannot.SyntenicData;
import tudelft.utilities.logging.Reporter;

/**
 * Parses syntenic files.
 * 
 * THe file must have a first non-comment line with "gvheader:syntenic".
 * Thereafter, each line in input reads as a {@link SyntenicBlock}. Each line 8
 * columns , tab separated , with
 * <ol>
 * <li>name of reference</li>
 * <li>start of ref</li>
 * <li>end of ref</li>
 * <li>strand of ref. Only first char is relevant. See
 * {@link Strand#fromSymbol(char)}</li>
 * <li>name of informant</li>
 * <li>start of informant</li>
 * <li>end of informant</li>
 * <li>strand of informant. Only first char is relevant. See
 * {@link Strand#fromSymbol(char)}</li>
 * </ol>
 * 
 * blank lines are ignored.
 * 
 * Lines starting with 'gvheader' '#' and '//" are ignored.
 * 
 * @author Thomas Abeel
 * 
 */
public class SyntenicParser extends Parser {

	public static final StringKey SYNTENIC_KEY = new StringKey("syntenic");

	/**
	 * @param dataKey
	 * @param log     the {@link Reporter} to log issues to
	 */
	public SyntenicParser(DataKey dataKey, Reporter log) {
		super(dataKey, log);
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {

		LineIterator it = new LineIterator(is);
		it.setSkipBlanks(true);
		it.setSkipComments(true);
		it.addCommentIdentifier("gvheader");

		final List<SyntenicBlock> blocks = new ArrayList<>();
		for (final String line : it) {
			final String[] arr = line.split("\t");
			if (line.length() < 8) {
				getLog().log(Level.SEVERE,
						"Expected at least 8 values but got " + line);
				break;
			}
			final Location refLoc = new Location(Integer.parseInt(arr[1]),
					Integer.parseInt(arr[2]));
			final Strand refStrand = Strand.fromSymbol(arr[3].charAt(0));
			final Location informantLoc = new Location(Integer.parseInt(arr[5]),
					Integer.parseInt(arr[6]));
			final Strand informantStrand = Strand.fromSymbol(arr[7].charAt(0));

			blocks.add(new SyntenicBlock(arr[0], arr[4], refLoc, informantLoc,
					refStrand, informantStrand));
//			set.getEntry(line).set.syntenic.add(sb);
//			// FIXME set.getOrCreateEntry(arr[0], source);
//			SyntenicBlock sbf = sb.flip();
//			set.syntenic.add(sbf);
//			// FIXME set.getOrCreateEntry(arr[4], source);

		}
		final SyntenicData data = new SyntenicData(blocks, getLog());

		// add this data to ALL relevant Entry's
		for (String ref : data.getReferences()) {
			// FIXME loading multiple syntenics might overwrite existing
			set.getOrCreateEntry(ref).add(SYNTENIC_KEY, data.get(ref));
		}

		return set;
	}

}
