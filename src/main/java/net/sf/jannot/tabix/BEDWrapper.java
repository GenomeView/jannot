/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.jannot.Feature;
import net.sf.jannot.tabix.codec.BEDCodec;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class BEDWrapper extends FeatureWrapper {

	BEDWrapper(String key, IndexedFeatureFile data, TabIndex idx,
			Reporter log) {
		super(key, data, idx, log);
	}

	@Override
	public Iterable<Feature> get(int start, int end) throws IOException {
		try {
			return new BEDCodec(this, data.query(key, start, end),
					data.getLog());
		} catch (URISyntaxException e) {
			throw new IOException("Failed to get BEDCodec", e);
		}
	}

}