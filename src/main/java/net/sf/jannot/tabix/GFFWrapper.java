/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.jannot.Feature;
import net.sf.jannot.tabix.codec.GFFCodec;
import tudelft.utilities.logging.Reporter;

/**
 */
public class GFFWrapper extends FeatureWrapper {

	public GFFWrapper(String key, IndexedFeatureFile data, TabIndex idx,
			Reporter log) {
		super(key, data, idx, log);
	}

	@Override
	public Iterable<Feature> get(int start, int end) throws IOException {
		try {
			return new GFFCodec(this, data.query(key, start, end),
					data.getLog());

		} catch (URISyntaxException e) {
			throw new IOException("Failed to get GFFCodec", e);
		}
	}

}