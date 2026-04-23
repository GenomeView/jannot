/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.io.IOException;
import java.util.Iterator;

import gnu.trove.map.hash.TIntFloatHashMap;
import net.sf.jannot.Data;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * Make wiggle {@link Data}set
 * 
 * Fill it with setMethod
 * 
 * Initialize with init();
 * 
 * @author Thomas Abeel
 * 
 */
public class TroveArrayWiggle extends AbstractWiggle
		implements Iterable<Float> {

	private float min = Float.POSITIVE_INFINITY;
	private float max = Float.NEGATIVE_INFINITY;

	// the data in blob is mutable
	private final TIntFloatHashMap blob = new TIntFloatHashMap();
	private final int size;

	public TroveArrayWiggle(int size, Reporter log) throws IOException {
		super(log);
		this.size = size;
		System.out.println("Mapping: " + size * 4);
		System.out.println("Mapping successfull!");

	}

	/**
	 * Zero based coordinate
	 * 
	 * @param position the position to change
	 * @param value    the new value for position
	 */
	public void set(int position, float value) {

		if (value > max)
			max = value;
		if (value < min)
			min = value;
		// try {
		blob.put(position, value);
	}

	public void init() {
		super.init(this);
	}

	/**
	 * @return copy of data from start (inclusive) to end (exclusive)
	 */
	@Override
	public float[] getRawRange(int start, int end) {
		if (start >= size)
			return new float[0];
		float[] out = new float[end - start];
		int len = out.length;
		if (start + len > size)
			len = size - start;
		if (start < 0)
			start = 0;
		for (int i = start; i < end; i++)
			out[i - start] = blob.get(i);
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
		return size;// / 4;
	}

	/**
	 * Get a single value, one based coordinate
	 * 
	 * @see net.sf.jannot.wiggle.Graph#value(int)
	 */
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
