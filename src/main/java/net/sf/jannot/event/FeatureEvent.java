/**
 * %HEADER%
 */
package net.sf.jannot.event;

import net.sf.jannot.Feature;

public abstract class FeatureEvent implements ChangeEvent {

	private final Feature feature;
	private final String msg;

	public FeatureEvent(Feature f, String msg) {
		this.feature = f;
		this.msg = msg;
	}

	public final Feature getFeature() {
		return feature;
	}

	@Override
	public String toString() {
		return new String("Edit feature " + feature + " (" + msg + ")");
	}
}
