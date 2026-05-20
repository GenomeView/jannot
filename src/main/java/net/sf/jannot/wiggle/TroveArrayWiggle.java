/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.util.Iterator;
import java.util.Map;

import gnu.trove.map.hash.TIntFloatHashMap;
import net.sf.jannot.Global;

/**
 * Container for a bunch of wiggle data, either of type variable or of type
 * fixed.
 * 
 * @author Thomas Abeel
 * 
 */
public class TroveArrayWiggle extends AbstractWiggle
		implements Iterable<Float> {

	// extreme values encountered
	private float min = Float.POSITIVE_INFINITY;
	private float max = Float.NEGATIVE_INFINITY;

	// the data in blob. mutable
	private final TIntFloatHashMap blob = new TIntFloatHashMap();

	// keeps track of highest encountered position
	private int maxposition = 0;

	/**
	 * 
	 * @param global the {@link Global}
	 * @param values a Map<Integer,Float> with the values. All values will be
	 *               copied into our local copy.
	 */
	public TroveArrayWiggle(Global global, Map<Integer, Float> values) {
		super(global);
		for (Integer position : values.keySet()) {
			Float value = values.get(position);
			if (value > max) {
				max = value;
			}
			if (value < min) {
				min = value;
			}
			blob.put(position, value);
			maxposition = Math.max(maxposition, position);
		}
		init();
	}

	@Override
	public float[] getRawRange(int start, int end) {
		int size = (int) size();
		float[] out = new float[end - start];

		if (start >= size) {
			return out;
		}

		int len = out.length;
		if (start + len > size) {
			len = size - start;
		}
		if (start < 0) {
			start = 0;
		}
		for (int i = start; i < end; i++) {
			out[i - start] = blob.get(i);
		}
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
		return maxposition + 1;
	}

	@Override
	public float value(int pos) {
		return blob.get(pos - 1);
	}

	@Override
	public Iterable<Float> get() {
		return this;
	}

	@Override
	public boolean canSave() {
		return false;
	}

	class DiskIterator implements Iterator<Float> {

		private int currentIdx = 1;
		private TroveArrayWiggle daw;

		/**
		 * @param blob
		 */
		public DiskIterator(TroveArrayWiggle blob) {
			this.daw = blob;
		}

		@Override
		public boolean hasNext() {
			return currentIdx <= daw.size();
		}

		@Override
		public Float next() {
			return daw.value(currentIdx++);
		}

		@Override
		public void remove() {
			throw new RuntimeException("Does not work");

		}

	}

	@Override
	public Iterator<Float> iterator() {
		return new DiskIterator(this);
	}

}
