/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.jannot.variation.VCFCodec;
import net.sf.jannot.variation.Variation;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class VCFWrapper extends TabixWrapper<Variation> {

	VCFWrapper(String key, IndexedFeatureFile data, TabIndex idx) {
		super(key, data, idx);
	}

	@Override
	public Iterable<Variation> get(int start, int end) throws IOException {
		try {
			return new VCFCodec(this, data.query(key, start, end),
					data.getLog());
		} catch (URISyntaxException e) {
			throw new IOException("Can't create VCFCodec", e);
		}
	}

}