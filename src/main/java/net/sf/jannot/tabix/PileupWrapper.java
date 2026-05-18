/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.jannot.Global;
import net.sf.jannot.pileup.DoublePile;
import net.sf.jannot.pileup.PileNormalization;
import net.sf.jannot.tabix.codec.PileupCodec;

public class PileupWrapper extends TabixWrapper<DoublePile>
		implements PileNormalization {

	PileupWrapper(String key, IndexedFeatureFile data, TabIndex idx,
			Global global) {
		super(key, data, idx, global);
	}

	@Override
	public Iterable<DoublePile> get(int start, int end) throws IOException {
		try {
			return new PileupCodec(data.query(key, start, end), global());
		} catch (URISyntaxException e) {
			throw new IOException("can't get PileupCodec", e);
		}
	}

	@Override
	public boolean supportsNormalization() {
		return false;
	}

}