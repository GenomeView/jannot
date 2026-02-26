/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.InputStream;

import be.abeel.io.LineIterator;
import net.sf.jannot.DataKey;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Location;
import net.sf.jannot.Strand;
import net.sf.jannot.SyntenicBlock;

/**
 * This parser. Each line in input reads as a {@link SyntenicBlock}. Each line 8
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

	/**
	 * @param dataKey
	 */
	public SyntenicParser(DataKey dataKey) {
		super(dataKey);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EntrySet parse(InputStream is, EntrySet set) {
		// List<Entry>list=new ArrayList<Entry>();
		if (set == null)
			set = new EntrySet();
		// Map<String,Entry>mapping=new HashMap<String, Entry>();

		LineIterator it = new LineIterator(is);
		it.setSkipBlanks(true);
		it.setSkipComments(true);
		it.addCommentIdentifier("gvheader");
		for (String line : it) {
			String[] arr = line.split("\t");
			Location refLoc = new Location(Integer.parseInt(arr[1]),
					Integer.parseInt(arr[2]));
			Strand refStrand = Strand.fromSymbol(arr[3].charAt(0));
			Location informantLoc = new Location(Integer.parseInt(arr[5]),
					Integer.parseInt(arr[6]));
			Strand informantStrand = Strand.fromSymbol(arr[7].charAt(0));

			SyntenicBlock sb = new SyntenicBlock(arr[0], arr[4], refLoc,
					informantLoc, refStrand, informantStrand);
			set.syntenic.add(sb);
			// FIXME set.getOrCreateEntry(arr[0], source);
			SyntenicBlock sbf = sb.flip();
			set.syntenic.add(sbf);
			// FIXME set.getOrCreateEntry(arr[4], source);

		}
		return set;
	}

}
