/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.io.IOException;
import java.util.BitSet;

/**
 * This contains a 32-fold downsampling of a {@link Query} with values
 * containing the average of the original values. Samples 0..31 in the original
 * data are downsampled to sample 0, sample 32..63 downsampled to sample 2, etc.
 * 
 * @author Thomas Abeel
 *
 */
class FloatCache implements Query {

	private static final int reductionfactor = 32;
	// cache of already computed values
	private float[] buffer;
	// remmeber which buffer values have been computed
	private BitSet valid = new BitSet();
	private Query source;

	/**
	 * 
	 * @param source the original data to be downsampled
	 */
	public FloatCache(Query source) {
		buffer = new float[1 + (int) (source.size() / reductionfactor)];
		this.source = source;
	}

	/**
	 * <b>NOTE</b> this lazily computes and caches computed averages in a buffer
	 * array.
	 */
	@Override
	public float[] getRawRange(int start, int end) throws IOException {
		float[] out = new float[(end - start) / reductionfactor];
		if (start / reductionfactor >= buffer.length) {
			return out;
		}
		for (int i = start / reductionfactor; i < end / reductionfactor; i++) {
			if (i >= 0 && i < buffer.length && !valid.get(i)) {
				float[] tmp = source.getRawRange(i * reductionfactor,
						i * reductionfactor + reductionfactor);
				double sum = 0;

				for (float f : tmp) {
					sum += f;
				}

				buffer[i] = (float) (sum / reductionfactor);
				valid.set(i);
			}
		}

		int len = out.length;
		if (start / reductionfactor + len > buffer.length) {
			len = buffer.length - start / reductionfactor;
		}
		if (start < 0) {
			start = 0;
		}
		System.arraycopy(buffer, start / reductionfactor, out, 0, len);
		return out;
	}

	@Override
	public long size() {
		return buffer.length;
	}

}