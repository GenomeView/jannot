package net.sf.jannot.syntenic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import net.sf.jannot.Data;
import net.sf.jannot.Global;
import net.sf.jannot.Location;

/**
 * Contains Syntenic Data which is just a set of {@link SyntenicBlock}s.
 */
public class SyntenicData implements Data<SyntenicBlock> {

	private final List<SyntenicBlock> data = new ArrayList<>();
	// the range (Location) of the IDs found in data. LinkedHashMap to fix order
	private final Map<String, Location> range = new LinkedHashMap<>();
	private final Global global;

	/**
	 * 
	 * @param d             the data. List to fix the order for visualization
	 * @param referencename the name of the reference. U
	 */
	public SyntenicData(List<SyntenicBlock> d, Global global) {
		this.global = Objects.requireNonNull(global);
		this.data.addAll(Objects.requireNonNull(d));
		for (SyntenicBlock b : data) {
			extendRange(b.reference(), b.refLocation());
			extendRange(b.target(), b.targetLocation());
		}
	}

	@Override
	public Global global() {
		return global;
	}

	/**
	 * extend {@link #range} of target to include targetLocation
	 * 
	 * @param target
	 * @param targetLocation
	 */
	private void extendRange(String target, Location targetLocation) {
		Location oldloc = range.get(target);
		if (oldloc == null) {
			range.put(target, targetLocation);
		} else {
			range.put(target, oldloc.extend(targetLocation));
		}
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
	public Collection<String> getReferences() {
		return Collections.unmodifiableCollection(range.keySet());
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

	/**
	 * 
	 * @param name the name
	 * @return The {@link SyntenicData} that have either start or end equal to
	 *         name. FIXME this duplicates the data, which might be avoidable eg
	 *         with ImmutableList
	 */
	public SyntenicData get(String name) {
		return new SyntenicData(data.stream().filter(
				d -> d.reference().equals(name) | d.target().equals(name))
				.collect(Collectors.toList()), global);
	}

	/**
	 * @param name the name of the syntenic target
	 * @return the range of the given entry name. All syntenic data for given
	 *         name is inside this
	 * 
	 */
	public Location getRange(String name) {
		return range.get(name);
	}

	@Override
	public String toString() {
		return "SyntenicData[" + data + "]";
	}

}