/**
 * %HEADER%
 */
package net.sf.jannot;

/**
 * The original direction of the sequence. {@link #FORWARD} is the 5′-to-3′
 * direction
 */
public enum Strand {

	FORWARD('+'), // 5′-to-3′ direction
	REVERSE('-'), // 3′-to-5′ direction
	UNKNOWN('.');

	private final String symbol;

	private Strand(char c) {
		this.symbol = "" + c;
	}

	/**
	 * 
	 * @param c a character
	 * @return strand matching c: +=FORWARD -=REVERSE, all else gives UNKNOWN.
	 */
	public static Strand fromSymbol(char c) {
		switch (c) {
		case '+':
			return FORWARD;
		case '-':
			return REVERSE;
		default:
			return UNKNOWN;
		}
	}

	/**
	 * Symbol representing the Strand.FORWARD=+ REVERSE=- UNKNOWN=.
	 * 
	 * @return symbol representation of the strand
	 */
	public String symbol() {
		return symbol;
	}

	/**
	 * @param other another {@link Strand}
	 * @return true if the strands are equal. Returns false if this or other is
	 *         UNKNOWN
	 */
	public boolean equals(Strand other) {
		if (this == UNKNOWN || other == UNKNOWN) {
			return false;
		}
		return this == other;
	}
}
