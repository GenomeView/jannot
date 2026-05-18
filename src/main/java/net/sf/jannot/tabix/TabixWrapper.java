/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.io.IOException;

import net.sf.jannot.Data;
import net.sf.jannot.DensityEstimate;
import net.sf.jannot.Global;
import net.sf.jannot.Location;

/**
 * abstract wrapper for density-like data like {@link FeatureWrapper},
 * {@link PileupWrapper}
 * 
 * @param <T> the type of the contained data.
 */
public abstract class TabixWrapper<T> implements Data<T>, DensityEstimate {

	private static final int RECORDSIZE = 12;
	protected final String key;
	protected final IndexedFeatureFile data;
	protected final TabIndex idx;
	private final Global global;

	TabixWrapper(String key, IndexedFeatureFile data, TabIndex idx,
			Global global) {
		this.global = global;
		this.data = data;
		this.key = key;
		this.idx = idx;
	}

	@Override
	public Global global() {
		return global;
	}

	@Override
	public String label() {
		String s = data.source();
		int bIndx = s.lastIndexOf('\\');
		int fIdx = s.lastIndexOf('/');
		int idx = Math.max(bIndx, fIdx);
		return s.substring(idx + 1);

	}

	@Override
	public boolean canSave() {
		return false;
	}

	@Override
	public Iterable<T> get() throws IOException {
		return get(1, getMaximumCoordinate());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.jannot.DensityEstimate#getEstimateCount(net.sf.jannot.Location)
	 */
	@Override
	public int getEstimateCount(Location l) {
		int tid = idx.names.indexOf(key);
		long max = 0;
		long min = Long.MAX_VALUE;
		for (int i = 0; i < idx.linIndex[tid].size(); i++) {
			long val = idx.linIndex[tid].get(i) >> 16;
			if (val > 0 && val < min) {
				min = val;
			}
			if (val > max) {
				max = val;
			}
		}

		double fraction = l.length() / (double) getMaximumCoordinate();
		int estimate = (int) (fraction * (max - min) / RECORDSIZE);
		return estimate;

	}

	@Override
	public int getMaximumCoordinate() {
		int tid = idx.names.indexOf(key);
		return idx.linIndex[tid].size() * 16 * 1024;
	}

}