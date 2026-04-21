/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.jannot.Feature;
import net.sf.jannot.tabix.codec.GFFCodec;

/**
 */
public class GFFWrapper extends FeatureWrapper {

	public GFFWrapper(String key, IndexedFeatureFile data, TabIndex idx) {
		super(key, data, idx);
	}

	@Override
	public Iterable<Feature> get(int start, int end) throws IOException {
		try {
			return new GFFCodec(this, data.query(key, start, end));

		} catch (URISyntaxException e) {
			throw new IOException("Failed to get GFFCodec", e);
		}
	}

}