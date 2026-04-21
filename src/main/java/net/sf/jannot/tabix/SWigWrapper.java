/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.jannot.pileup.Pile;
import net.sf.jannot.pileup.PileNormalization;
import net.sf.jannot.tabix.codec.SWigCodec;

public class SWigWrapper extends TabixWrapper<Pile>
		implements PileNormalization {

	SWigWrapper(String key, IndexedFeatureFile data, TabIndex idx) {
		super(key, data, idx);
	}

	@Override
	public Iterable<Pile> get(int start, int end) throws IOException {
		try {
			return new SWigCodec(data.query(key, start, end), data.getLog());
		} catch (URISyntaxException e) {
			throw new IOException("Can't get SWigCodec", e);
		}
	}

	@Override
	public boolean supportsNormalization() {
		return false;
	}

}