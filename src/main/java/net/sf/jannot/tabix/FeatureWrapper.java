/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import net.sf.jannot.Feature;
import net.sf.jannot.FeatureAnnotation;
import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * 
 */
public abstract class FeatureWrapper extends TabixWrapper<Feature>
		implements FeatureAnnotation {

	/**
	 * @param key
	 * @param data
	 * @param idx
	 */
	public FeatureWrapper(String key, IndexedFeatureFile data, TabIndex idx,
			Reporter log) {
		super(key, data, idx, log);
	}

}
