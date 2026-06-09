/**
 * %HEADER%
 */
package net.sf.jannot;

/**
 * 
 * 
 * Represents the Feature annotation of a single type
 * 
 * All data that is kept in memory, these get added manually
 */

@SuppressWarnings("serial")
public class MemoryFeatureAnnotation extends MemoryListData<Feature>
		implements FeatureAnnotation {
	private double minStart = Integer.MAX_VALUE;
	private double maxEnd = 0;
	private String label = null;

	public MemoryFeatureAnnotation(Global global) {
		super(global);
	}

	/**
	 * @param f
	 * @return
	 */
	@Override
	public synchronized boolean add(Feature f) {
		if (label == null) {
			label = f.type().toString();
		}
		super.add(f);
		if (f.start() < minStart) {
			minStart = f.start();
		}
		if (f.end() > maxEnd) {
			maxEnd = f.end();
		}
		return true;
	}

	/**
	 * @param row
	 * @return
	 */
	public Feature getCached(int row) {
		return super.get(row);
	}

	/**
	 * @param f the feature
	 * @return the index of f in the list
	 */
	public int getCachedIndexOf(Feature f) {
		return super.indexOf(f);
	}

	@Override
	public int getEstimateCount(Location l) {
		if (size() < 200) {
			return 0;
		}
		double d = size() / (maxEnd - minStart);

		int estMemory = (int) (l.length() * d);

		return estMemory;

	}

	@Override
	public int getMaximumCoordinate() {
		return (int) maxEnd;
	}

	@Override
	public String toString() {
		if (super.size() > 0) {
			return super.get(0).type().toString();
		} else {
			return null;
		}
	}

	@Override
	public boolean canSave() {
		return size() > 0;
	}

	@Override
	public String label() {
		if (label == null) {
			return "no data";
		} else {
			return label;
		}
	}

}
