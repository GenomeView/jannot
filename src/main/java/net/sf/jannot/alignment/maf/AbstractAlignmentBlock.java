/**
 * %HEADER%
 */
package net.sf.jannot.alignment.maf;

import net.sf.jannot.Located;
import net.sf.jannot.Location;

/**
 * contains alignment data {@link Location}s- for some chromosome related to the
 * reference chromosome See {@link MAFMemoryMultipleAlignment}
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractAlignmentBlock
		implements Comparable<AbstractAlignmentBlock>, Located,
		Iterable<AbstractAlignmentSequence> {
	/* Buffers for finding nucleotides */
	private final Location loc;

	public AbstractAlignmentBlock(int start, int end) {
		this.loc = new Location(start, end);
	}

	@Override
	public int compareTo(AbstractAlignmentBlock o) {
		int thisVal = this.hashCode();
		int anotherVal = o.hashCode();
		return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
	}

	@Override
	public int start() {
		return loc.start();
	}

	@Override
	public int end() {
		return loc.end();
	}

	public int length() {
		return loc.length();
	}

	/**
	 * @param as
	 * @return
	 */
	public abstract void add(AbstractAlignmentSequence as);

	/**
	 * @param i
	 * @return
	 */
	public abstract AbstractAlignmentSequence getAlignmentSequence(int i);

	/**
	 * @return
	 */
	public abstract int size();

}
