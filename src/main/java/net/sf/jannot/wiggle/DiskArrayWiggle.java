/**
 * %HEADER%
 */
package net.sf.jannot.wiggle;

import java.io.IOException;
import java.util.Iterator;

import net.sf.jannot.picard.BinaryBlob;

/**
 * 
 * Make wiggle
 * 
 * Fill it with setMethod
 * 
 * Initialize with init();
 * 
 * @author Thomas Abeel
 * 
 */
public class DiskArrayWiggle extends AbstractWiggle implements Iterable<Float> {

	// private float[] buffer;
	// private String name;
	private float min = Float.POSITIVE_INFINITY;
	private float max = Float.NEGATIVE_INFINITY;
	// private FloatBuffer fb = null;
	// private int size;

	private BinaryBlob blob = null;
	private int size;

	public DiskArrayWiggle(int size) throws IOException {
		this.size = size;
		// System.out.println("Mapping: " + size * 4);
		blob = new BinaryBlob(size * 4);
		// System.out.println("Mapping successfull!");

	}

	public void init() {
		super.init(this);
	}

	@Override
	public float[] getRawRange(int start, int end) throws IOException {
		if (start >= size)
			return new float[0];
		float[] out = new float[end - start];
		int len = out.length;
		if (start + len > size)
			len = size - start;
		if (start < 0)
			start = 0;
		for (int i = start; i < end; i++)
			out[i - start] = blob.getFloat(i * 4);
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

	@Override
	public float value(int pos) throws IOException {
		return blob.getFloat(4 * (pos - 1));
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
		private DiskArrayWiggle daw;

		/**
		 * @param blob
		 */
		public DiskIterator(DiskArrayWiggle blob) {
			this.daw = blob;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return currentIdx <= daw.size();
		}

		@Override
		public Float next() {
			try {
				return daw.value(currentIdx++);
			} catch (IOException e) {
				return 0f; // shouldn't happen
			}
		}

		@Override
		public void remove() {
			throw new RuntimeException("Not implemented");

		}

	}

	@Override
	public Iterator<Float> iterator() {
		return new DiskIterator(this);
	}

}
