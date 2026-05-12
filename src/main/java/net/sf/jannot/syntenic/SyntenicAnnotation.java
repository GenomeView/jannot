/**
 * %HEADER%
 */
package net.sf.jannot.syntenic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.jannot.Entry;
import net.sf.jannot.EntrySetAnnotation;
import net.sf.jannot.Location;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class SyntenicAnnotation extends EntrySetAnnotation<SyntenicBlock> {

	private final List<SyntenicBlock> syntenicBlocks = new ArrayList<SyntenicBlock>();
	private final HashSet<String> targets = new HashSet<String>();

	@Override
	public List<SyntenicBlock> get(Entry e, Location l) {
		return syntenicBlocks;
	}

	@Override
	public void add(SyntenicBlock t) {
		syntenicBlocks.add(t);
		targets.add(t.target());
	}

	@Override
	public List<SyntenicBlock> getAll(Entry e) {
		return syntenicBlocks;
	}

	public Set<String> getTargets() {
		return targets;
	}

	@Override
	public Iterator<SyntenicBlock> iterator() {
		return syntenicBlocks.iterator();
	}

	@Override
	public boolean contains(SyntenicBlock t) {
		return syntenicBlocks.contains(t);
	}

	/**
	 * 
	 */
	public void clear() {
		syntenicBlocks.clear();

	}
}
