/**
 * %HEADER%
 */
package net.sf.jannot.tabix.codec;

import net.sf.jannot.Global;
import net.sf.jannot.pileup.DoublePile;
import net.sf.jannot.pileup.ReadDetailPile;
import net.sf.jannot.tabix.TabixLine;

/**
 * @author Thomas Abeel
 * 
 */
public class PileupCodec extends Codec<DoublePile> {

	/**
	 * @param in
	 */
	public PileupCodec(Iterable<TabixLine> in, Global global) {
		super(in, 15000, global);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jannot.tabix.codec.Codec#parse(java.lang.String)
	 */
	@Override
	public DoublePile parse(TabixLine line) {
		DoublePile f = lru.get(line);
		if (f != null) {
			return f;
		} else {
			int pos = line.getInt(1);
//			int coverage = line.getInt(3);
			byte[] reads = line.get(4).getBytes();
			ReadDetailPile p = ReadDetailPile.create(pos, reads);
			lru.put(line, f);
			return p;
		}
	}

}
