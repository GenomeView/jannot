/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.jannot.variation.VCFCodec;
import net.sf.jannot.variation.Variation;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class VCFWrapper extends TabixWrapper<Variation> {

	VCFWrapper(String key, IndexedFeatureFile data, TabIndex idx,
			Reporter log) {
		super(key, data, idx, log);
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