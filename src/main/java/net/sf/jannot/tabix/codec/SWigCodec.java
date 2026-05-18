/**
 * %HEADER%
 */
package net.sf.jannot.tabix.codec;

import net.sf.jannot.Global;
import net.sf.jannot.pileup.Pile;
import net.sf.jannot.pileup.PileTools;
import net.sf.jannot.tabix.TabixLine;

/**
 * @author Thomas Abeel
 * 
 */
public class SWigCodec extends Codec<Pile> {

	/**
	 * @param in
	 */
	public SWigCodec(Iterable<TabixLine> in, Global global) {
		super(in, 15000, global);
	}

	@Override
	public Pile parse(TabixLine line) {
		Pile f = lru.get(line);
		if (f != null) {
			return f;
		} else {
			int pos = line.getInt(1);
			int fcoverage = line.getInt(2);
			int rcoverage = line.getInt(3);
			Pile p = PileTools.create(pos, fcoverage, rcoverage);
			lru.put(line, f);
			return p;
		}
	}

}
