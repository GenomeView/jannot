package net.sf.jannot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Contains Syntenic Data which is just a set of {@link SyntenicBlock}s. Work in
 * progress, not yet working, part of #34
 */
public class SyntenicData implements Data<SyntenicBlock> {

	private final Collection<SyntenicBlock> data = new HashSet<>();

	/**
	 * 
	 * @param d             the data
	 * @param referencename the name of the reference. U
	 */
	public SyntenicData(Collection<SyntenicBlock> d) {
		this.data.addAll(d);
	}

	@Override
	public Iterable<SyntenicBlock> get(int start, int end) {
		// FIXME return only data in range
		return Collections.unmodifiableCollection(data);
	}

	@Override
	public Iterable<SyntenicBlock> get() {
		return Collections.unmodifiableCollection(data);
	}

	@Override
	public boolean canSave() {
		return false;
	}

	@Override
	public String label() {
		return "syntenic";
	}
}