package net.sf.jannot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contains Syntenic Data which is just a set of {@link SyntenicBlock}s. Work in
 * progress, not yet working, part of #34
 */
public class SyntenicData implements Data<SyntenicBlock> {

	private final List<SyntenicBlock> data = new ArrayList<>();

	// List to fix order.
	private final List<String> refs = new ArrayList<>();

	/**
	 * 
	 * @param d             the data. List to fix the order for visualization
	 * @param referencename the name of the reference. U
	 */
	public SyntenicData(List<SyntenicBlock> d) {
		this.data.addAll(d);
		Set<String> uniquerefs = new HashSet<>();
		for (SyntenicBlock b : data) {
			uniquerefs.add(b.reference());
			uniquerefs.add(b.target());
		}
		refs.addAll(uniquerefs);
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

	/**
	 * 
	 * @return all referenced names in the {@link #data}
	 */
	public List<String> getReferences() {
		return Collections.unmodifiableList(refs);
	}

	/**
	 * 
	 * @param ref    the reference
	 * @param target the target
	 * @return all {@link SyntenicBlock}s from ref to target
	 */
	public List<SyntenicBlock> get(String ref, String target) {
		return data.stream().map(d -> d.match(ref, target))
				.filter(d -> d != null).collect(Collectors.toList());
	}
}