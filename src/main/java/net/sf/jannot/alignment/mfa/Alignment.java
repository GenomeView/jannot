/**
 * %HEADER%
 */
package net.sf.jannot.alignment.mfa;

import java.util.BitSet;

import net.sf.jannot.Located;
import net.sf.jannot.alignment.ReferenceSequence;
import net.sf.jannot.refseq.MemorySequence;
import net.sf.jannot.refseq.Sequence;

/**
 * A sequence with a name that aligns at a known location with some
 * {@link ReferenceSequence}
 */
public class Alignment implements Located {

	private final String name;
	private final MemorySequence alignment;
	private final ReferenceSequence reference;
	// true where ref and target explicitly match (not '-')
	private final BitSet aligned = new BitSet();

	public Alignment(String name, MemorySequence sequence,
			ReferenceSequence reference) {
		this.name = name;
		this.alignment = sequence;
		this.reference = reference;

		/* Only positions in reference sequence are cached */
		for (int i = 1; i < this.refLength(); i++) {
			char inf = getNucleotide(i);
			char ref = getReferenceNucleotide(i);
			if (inf != '-' && ref != '-'
					&& Character.toLowerCase(inf) == Character.toLowerCase(ref))
				aligned.set(i);
		}

	}

	/**
	 * Returns the expanded query sequence including gaps
	 * 
	 * @return expanded query sequence
	 */
	public Sequence getExpandedQuerySequence() {
		return alignment;
	}

	/**
	 * Returns the expanded reference sequence including gaps.
	 * 
	 * @return expanded reference sequence
	 */
	public Sequence getExpandedReferenceSequence() {
		return reference;
	}

	/**
	 * Returns the name of this alignment
	 * 
	 * @return
	 */
	public String name() {
		return name;
	}

	/**
	 * Gives the nucleotide that appears at the given position in the alignment.
	 * The coordinates are in the expanded reference sequence.
	 * 
	 * @param pos position the get nucleotide
	 * @return nucleotide at provided position
	 */
	public char getNucleotide(int pos) {
		return alignment.getNucleotide(reference.ref2aln(pos));
	}

	/**
	 * Gives the nucleotide that appears at the given position in the reference
	 * sequence. The coordinates are in the expanded reference sequence.
	 * 
	 * @param pos position the get nucleotide
	 * @return nucleotide at provided position
	 */
	public char getReferenceNucleotide(int pos) {
		return reference.getNucleotide(reference.ref2aln(pos));
	}

	/**
	 * @param pos the exact position
	 * @return true iff target and ref nucleotice explicitly match (not '-') at
	 *         pos.
	 */
	public boolean isAligned(int pos) {
		return aligned.get(pos);
	}

	/**
	 * 
	 * @return the length of the reference sequence. This is the length of the
	 *         reference alignment minus all gaps.
	 * 
	 */
	public int refLength() {
		return reference.size() - reference.getRefGapCount();
	}

	/*
	 * @return true iff there is extra data between this position and the next
	 * one.
	 */
	public int sizeGapAfter(int i) {
		return reference.ref2aln(i + 1) - reference.ref2aln(i) - 1;

	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int start() {
		return 1;

	}

	@Override
	public int end() {
		return refLength();
	}

}
