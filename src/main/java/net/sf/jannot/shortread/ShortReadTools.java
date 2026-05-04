/**
 * %HEADER%
 */
package net.sf.jannot.shortread;

import java.util.logging.Level;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import net.sf.jannot.Strand;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class ShortReadTools {

	public static int length(SAMRecord sr) {
		return sr.getAlignmentEnd() - start(sr) + 1;
	}

	public static boolean isSecondInPair(SAMRecord sr) {
		return !isFirstInPair(sr);
	}

//	/**
//	 * Returns nucleotide in the aligned read;
//	 */
//	@Deprecated
//	public static char getNucleotide(SAMRecord sr, int pos) {
//		byte[] construct = null;
//		construct = construct(sr);
//		return (char) construct[pos - 1];
//
//	}

	public static byte[] construct(SAMRecord sr, Reporter log) {
		int pos = 0;
		int superPos = 0;
		byte[] out = new byte[length(sr)];
		byte[] readBases = sr.getReadBases();
		try {
			for (CigarElement ce : sr.getCigar().getCigarElements()) {
				CigarOperator co = ce.getOperator();
				for (int i = 0; i < ce.getLength(); i++) {
					switch (co) {
					case I:
						superPos++;
						// System.out.println("I: "+pos);
						break;
					case N:
						out[pos++] = '_';
						break;
					case D:
						// System.out.println("D: "+pos);
						out[pos++] = '-';
						break;
					case M:

						/* Bases are present */
						if (readBases.length > 0) {
							assert pos < out.length;
							assert superPos < readBases.length;
							out[pos] = readBases[superPos];
						} else {
							out[pos] = 'M';
						}
						pos++;
						superPos++;
						break;
					case S:
						// out[pos] = readBases[superPos];
						// pos++;
						superPos++;
						break;
					case H:
						i++;
						break;
					default:
						System.err.println("def: " + co + "\t" + pos + "\t"
								+ superPos + "\t" + out[pos]);
						break;

					}
				}
			}
		} catch (IndexOutOfBoundsException ex) {
			log.log(Level.SEVERE,
					"Could not get sequence for read. Read bases: "
							+ new String(readBases) + ". len: "
							+ readBases.length + ". Cigar string: "
							+ sr.getCigarString(),
					ex);
		}

		return out;
	}

	public static int end(SAMRecord sr) {
		return sr.getAlignmentEnd();
	}

	public static boolean isFirstInPair(SAMRecord sr) {
		return isPaired(sr) ? sr.getFirstOfPairFlag() : true;
	}

	public static boolean isPaired(SAMRecord sr) {
		return sr.getReadPairedFlag();
	}

	public static int start(SAMRecord sr) {
		return sr.getAlignmentStart();
	}

	public static Strand strand(SAMRecord sr) {
		return sr.getReadNegativeStrandFlag() ? Strand.REVERSE : Strand.FORWARD;
	}

}
