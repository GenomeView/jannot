/**
 * %HEADER%
 */
package net.sf.jannot.refseq;

import cern.colt.list.ByteArrayList;
import net.sf.jannot.AminoAcidMapping;
import net.sf.jannot.Global;
import net.sf.jannot.utils.ArrayIterable;
import net.sf.jannot.utils.SequenceTools;

/**
 * A (mutable) sequence (list of chars) in memory
 */
public class MemorySequence extends Sequence {

	private DefaultByteArrayList sequence = new DefaultByteArrayList(
			(byte) 0xff);

	public MemorySequence(Global global) {
		super(global);
	}

	/**
	 * Copy constructor
	 * 
	 * @param sequence sequence to make a copy of
	 */
	public MemorySequence(MemorySequence sequence) {
		super(sequence.global());
		this.sequence = sequence.sequence.copy();
		this.size = sequence.size;
	}

	public MemorySequence(String string, Global global) {
		this(new StringBuffer(string), global);
	}

	public MemorySequence(StringBuffer string, Global global) {
		super(global);
		setSequence(string);
	}

	@Deprecated
	public char getNucleotide(int index) {
		if (index < 1 || index > size()) {
			return '_';
		}
		return get(index - 1);

	}

	@Deprecated
	public char getReverseNucleotide(int index) {
		return SequenceTools.complement(getNucleotide(index));
	}

	@Deprecated
	public char getAminoAcid(int pos, AminoAcidMapping mapping) {
		String codon = "" + getNucleotide(pos) + getNucleotide(pos + 1)
				+ getNucleotide(pos + 2);
		return mapping.get(codon);
	}

	@Deprecated
	public char getReverseAminoAcid(int pos, AminoAcidMapping mapping) {
		String codon = "" + getReverseNucleotide(pos + 2)
				+ getReverseNucleotide(pos + 1) + getReverseNucleotide(pos);
		return mapping.get(codon);
	}

	static class DefaultByteArrayList extends ByteArrayList {

		private byte def;

		private DefaultByteArrayList(byte[] ele, byte def) {
			super(ele);
			this.def = def;
		}

		public DefaultByteArrayList(byte def) {
			this.def = def;
		}

		public void setDef(byte def) {
			this.def = def;
		}

		@Override
		public byte get(int index) {
			if (index >= 0 && index < super.size) {
				return super.get(index);
			} else {
				return def;
			}
		}

		@Override
		public DefaultByteArrayList copy() {
			return new DefaultByteArrayList(super.elements, this.def);
		}

	}

	private char decode(int b) {
		switch (b) {
		case 0:
			return 'A';
		case 1:
			return 'C';
		case 2:
			return 'G';
		case 3:
			return 'T';
		case 4:
			return 'N';
		case 5:
			return '-';
		case 6:
		default:
			return '_';
		}
	}

	private int encode(char c) {
		switch (c) {
		case 'a':
		case 'A':
			return 0;
		case 'c':
		case 'C':
			return 1;
		case 'g':
		case 'G':
			return 2;
		case 't':
		case 'T':
			return 3;
		case 'n':
		case 'N':
			return 4;
		case '-':
			return 5;
		default:
			return 6;
		}

	}

	/**
	 * @param pos Zero based
	 * @param c   character to put at pos
	 */
	private void set(int pos, char c) {
		int coded = encode(c);
		int mask = 15;
		if (pos % 2 == 1) {
			coded <<= 4;

		} else {
			mask <<= 4;
		}

		if (sequence.size() <= pos / 2) {
			sequence.setSize(sequence.size() + 1);
		}

		int current = sequence.get(pos / 2);
		current &= mask;
		int newCurrent = current | coded;
		sequence.set(pos / 2, (byte) newCurrent);
	}

	/**
	 * 
	 * @param pos Zero based position
	 * @return sequence char at pos
	 */
	protected char get(int pos) {
		int current = sequence.get(pos / 2);
		int mask = 15;
		if (pos % 2 == 1) {
			current >>= 4;
		} else {
			current &= mask;
		}
		return decode(current);
	}

	@Override
	public String toString() {
		return stringRepresentation();
	}

	public void setSequence(String sequence) {
		setSequence(new StringBuffer(sequence));
	}

	@Override
	public int size() {
		return size;
	}

	public void addSequence(String seq) {
		int currentLength = size();
		for (int i = 0; i < seq.length(); i++) {
			set(i + currentLength, seq.charAt(i));
			size++;
		}

	}

	public void setSequence(StringBuffer seq) {
		this.sequence = new DefaultByteArrayList((byte) 0xff);
		for (int i = 0; i < seq.length(); i++) {
			set(i, seq.charAt(i));
		}

		this.size = seq.length();

	}

	private int size = 0;

	@Override
	public Iterable<Character> get(int start, int end) {
		char[] seq = new char[end - start];

		start--;// = startPos;
		end--;// = startPos;
		for (int i = start; i < end; i++) {
			seq[i - start] = get(i);
		}
		return new ArrayIterable<Character>(seq);

	}

	@Override
	public Iterable<Character> get() {
		return get(1, size() + 1);
	}

}
