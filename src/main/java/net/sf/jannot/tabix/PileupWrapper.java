/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.jannot.pileup.DoublePile;
import net.sf.jannot.pileup.PileNormalization;
import net.sf.jannot.tabix.codec.PileupCodec;

public class PileupWrapper extends TabixWrapper<DoublePile>
		implements PileNormalization {

	PileupWrapper(String key, IndexedFeatureFile data, TabIndex idx) {
		super(key, data, idx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jannot.Data#get(int, int)
	 */
	@Override
	public Iterable<DoublePile> get(int start, int end) throws IOException {
		try {
			return new PileupCodec(data.query(key, start, end));
		} catch (URISyntaxException e) {
			throw new IOException("can't get PileupCodec", e);
		}
	}

	@Override
	public boolean supportsNormalization() {
		return false;
	}

}