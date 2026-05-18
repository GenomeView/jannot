/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.jannot.Feature;
import net.sf.jannot.Global;
import net.sf.jannot.tabix.codec.BEDCodec;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class BEDWrapper extends FeatureWrapper {

	BEDWrapper(String key, IndexedFeatureFile data, TabIndex idx,
			Global global) {
		super(key, data, idx, global);
	}

	@Override
	public Iterable<Feature> get(int start, int end) throws IOException {
		try {
			return new BEDCodec(this, data.query(key, start, end), global());
		} catch (URISyntaxException e) {
			throw new IOException("Failed to get BEDCodec", e);
		}
	}

}