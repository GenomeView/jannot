/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import net.sf.jannot.Global;
import net.sf.jannot.utils.ArrayIterable;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class FloatArrayWiggle extends AbstractWiggle {

	private final float[] buffer;
	private final float min, max;

	/**
	 * 
	 * @param arr    the data. WARNING FIXME we take ownership of this array. Do
	 *               not alter the array after calling this. WARNING for sparse
	 *               arrays this very inefficient.
	 * @param global the {@link Global}
	 */
	public FloatArrayWiggle(float[] arr, Global global) {
		super(global);

		this.buffer = arr;

		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		for (float f : arr) {
			if (f > max) {
				max = f;
			}
			if (f < min) {
				min = f;
			}
		}
		this.min = min;
		this.max = max;

	}

	@Override
	public float[] getRawRange(int start, int end) {
		if (start >= buffer.length) {
			return new float[0];
		}
		float[] out = new float[end - start];
		int len = out.length;
		if (start + len > buffer.length) {
			len = buffer.length - start;
		}
		if (start < 0) {
			start = 0;
		}
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
