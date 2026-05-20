/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.io.IOException;

import net.sf.jannot.Global;
import net.sf.jannot.utils.ArrayIterable;

/**
 * abstract class that buffers both original and 32x downsampled data
 * 
 * @author Thomas Abeel
 *
 */
public abstract class AbstractWiggle implements Graph, Query {

	private final Global global;

	// buffer with 2^5 downsampled data.
	private FloatCache buffer5 = null;

	/**
	 * cache for last call to {@link #get(int, int, int)}
	 */
	private int lastStart = -1, lastEnd = -1, lastRes = -1;
	private float[] last = null;

	public AbstractWiggle(Global global) {
		this.global = global;
	}

	/**
	 * This must be called after all data was loaded and before a get() is done.
	 * This implies this object has at least 3 states : empty, loaded, ready.
	 */
	public void init() {
		buffer5 = new FloatCache(this);

	}

	@Override
	public String label() {
		return "wiggle";
	}

	@Override
	public float[] get(int start, int end, int resolutionIndex)
			throws IOException {
		if (buffer5 == null) {
			throw new IOException("Wiggle needs to be initialized");
		}
		if (lastStart == start && lastEnd == end
				&& lastRes == resolutionIndex) {
			return last;
		}
		if (resolutionIndex < 5) {
			last = getRawRange(start, end);

		} else {
			last = buffer5.getRawRange(start, end);
			resolutionIndex -= 5;

		}
		while (resolutionIndex > 0) {
			last = merge(last);
			resolutionIndex--;
		}
		return last;

	}

	@Override
	public Global global() {
		return global;
	}

	/**
	 * @param ds a float array
	 * @return array of half the length of ds (rounded up), with every 2 values
	 *         from ds averaged.
	 */
	private float[] merge(float[] ds) {
		float[] out = new float[(ds.length + 1) / 2];
//		double max = 0;
		for (int i = 0; i < ds.length - 1; i += 2) {
			out[i / 2] = (ds[i] + ds[i + 1]) / 2;
//			if (out[i / 2] > max) {
//				max = out[i / 2];
//			}
		}
		if (ds.length % 2 == 1) {
			out[out.length - 1] = ds[ds.length - 1];
		}

		return out;
	}

	@Override
	public abstract float[] getRawRange(int start, int end) throws IOException;

	@Override
	public Iterable<Float> get(int start, int end) throws IOException {
		float[] out = getRawRange(start, end);
		return new ArrayIterable<Float>(out);
	}

}
