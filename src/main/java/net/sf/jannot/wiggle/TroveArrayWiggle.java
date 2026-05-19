/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.util.Iterator;

import gnu.trove.map.hash.TIntFloatHashMap;
import net.sf.jannot.Global;
import net.sf.jannot.Location;

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

	// keeps track of total range of positions. null= not set yet
	private Location range = null;

	/**
	 * 
	 * @param size   the final size of the data range. Maybe the size of the
	 *               sequence that this wiggle is referring to. WARNING this
	 *               value is final and not enforced. If the actual size is
	 *               different other methods in here will not work correctly. If
	 *               the sequence itself is not yet loaded or for some other
	 *               reason this is 0, odd things will happen. FIXME this seems
	 *               a bug
	 * @param global the {@link Global}
	 */
	public TroveArrayWiggle(int size, Global global) {
		super(global);
	}

	/**
	 * 
	 * @param position the position to change. Zero based
	 * @param value    the new value for position
	 */
	public void set(int position, float value) {

		if (value > max) {
			max = value;
		}
		if (value < min) {
			min = value;
		}
		blob.put(position, value);
		Location l = new Location(position, position);
		range = range == null ? l : range.extend(l);
	}

	public void init() {
		super.init(this);
	}

	@Override
	public float[] getRawRange(int start, int end) {
		int size = range.length();
		if (start >= size) {
			return new float[0];
		}
		float[] out = new float[end - start];
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
		return range == null ? 0 : range.length();
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
