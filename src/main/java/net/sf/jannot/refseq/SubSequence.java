/**
 * %HEADER%
 */
package net.sf.jannot.refseq;

import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * 
 */
public class SubSequence extends Sequence {

	private final int end;
	private final int start;
	private final Sequence seq;

	/**
	 * @param start
	 * @param end
	 */
	public SubSequence(Sequence s, int start, int end, Reporter log) {
		super(log);
		this.seq = s;
		this.start = start;
		this.end = end;
	}

	@Override
	public Iterable<Character> get(int start, int end) {
		return seq.get(start + this.start, end);
	}

	@Override
	public Iterable<Character> get() {
		return seq.get(this.start, this.end);
	}

	@Override
	public int size() {
		return end - start;
	}

}
