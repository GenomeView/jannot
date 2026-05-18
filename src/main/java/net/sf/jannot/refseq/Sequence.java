/**
 * %HEADER%
 */
package net.sf.jannot.refseq;

import net.sf.jannot.Data;
import net.sf.jannot.Global;

/**
 * a Data set with {@link Character}s
 * 
 * @author Thomas Abeel
 *
 */
public abstract class Sequence implements Data<Character> {

	private final Global global;

	public Sequence(Global global) {
		this.global = global;
	}

	@Override
	public Global global() {
		return global;
	}

	@Override
	public String label() {
		return "Sequence";
	}

	@Override
	public boolean canSave() {
		return false;
	}

	public abstract int size();

	/**
	 * Gets a subsequence from this sequence. The selected sequence is
	 * [start,end[. The coordinates are one based.
	 * 
	 * @param start the start coordinate, this one will be included in the
	 *              sequence. This is a one-based coordinate.
	 * @param end   the end coordinate, this one will not be included in the
	 *              sequence. This is a one-based coordinate.
	 * @return the selected subsequence.
	 */
	@Override
	public abstract Iterable<Character> get(int start, int end);

	@Override
	public abstract Iterable<Character> get();

	/**
	 * Coordinates are 1-based, cover [start,end[
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public Sequence subsequence(int start, int end) {
		return new SubSequence(this, start, end, global());
	}

	/**
	 * Use this method sparingly as it can be a fairly expensive operation
	 * 
	 * @return
	 */
	public String stringRepresentation() {
		StringBuffer out = new StringBuffer(size());
		for (Character c : get()) {
			out.append(c);
		}
		return out.toString();
	}

}
