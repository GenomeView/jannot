/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import net.sf.jannot.utils.ArrayIterable;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class FloatArrayWiggle extends AbstractWiggle {

	private float[] buffer;
	private float min = Float.POSITIVE_INFINITY;
	private float max = Float.NEGATIVE_INFINITY;

	public FloatArrayWiggle(float[] arr, Reporter log) {
		super(log);
		this.buffer = arr;
		for (float f : arr) {
			if (f > max)
				max = f;
			if (f < min)
				min = f;
		}
		super.init(this);

	}

	@Override
	public float[] getRawRange(int start, int end) {
		if (start >= buffer.length)
			return new float[0];
		float[] out = new float[end - start];
		int len = out.length;
		if (start + len > buffer.length)
			len = buffer.length - start;
		if (start < 0)
			start = 0;
		System.arraycopy(buffer, start, out, 0, len);
		return out;
	}

	@Override
	public float max() {
		return max;
	}

	@Override
	public float min() {
		return min;
	}

	@Override
	public long size() {
		return buffer.length;
	}

	/**
	 * Get a single value, one based coordinate
	 * 
	 * @see net.sf.jannot.wiggle.Graph#value(int)
	 */
	@Override
	public float value(int pos) {
		return buffer[pos - 1];
	}

	@Override
	public Iterable<Float> get() {

		return new ArrayIterable<Float>(buffer);

	}

	@Override
	public boolean canSave() {
		return false;
	}

}
