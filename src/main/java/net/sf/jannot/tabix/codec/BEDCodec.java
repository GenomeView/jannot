/**
 * %HEADER%
 */
package net.sf.jannot.tabix.codec;

import net.sf.jannot.Feature;
import net.sf.jannot.parser.BEDTools;
import net.sf.jannot.tabix.FeatureWrapper;
import net.sf.jannot.tabix.TabixLine;
import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * 
 */
public class BEDCodec extends Codec<Feature> {

	private FeatureWrapper wrapper;

	/**
	 * @param in
	 */
	public BEDCodec(FeatureWrapper wrapper, Iterable<TabixLine> in,
			Reporter log) {
		super(in, 1024, log);
		this.wrapper = wrapper;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jannot.tabix.codec.Codec#parse(java.lang.String)
	 */
	@Override
	public Feature parse(TabixLine line) {
		Feature f = BEDTools.parseLine(line.line(), null, null);
//		wrapper.update(f);
		return f;
	}

}
