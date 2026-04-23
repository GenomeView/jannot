/**
 * %HEADER%
 */
package net.sf.jannot.refseq;

import net.sf.jannot.Data;
import tudelft.utilities.logging.Reporter;

/**
 * a Data set with {@link Character}s
 * 
 * @author Thomas Abeel
 *
 */
public abstract class Sequence implements Data<Character> {

	private final Reporter log;

	public Sequence(Reporter log) {
		this.log = log;
	}

	@Override
	public Reporter getLog() {
		return log;
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
		return new SubSequence(this, start, end, getLog());
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
