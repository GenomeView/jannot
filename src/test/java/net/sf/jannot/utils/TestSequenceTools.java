/**
 * %HEADER%
 */
package net.sf.jannot.utils;

import org.junit.Test;

import junit.framework.Assert;
import net.sf.jannot.refseq.MemorySequence;
import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestSequenceTools {

	private Reporter log = new ReportToLogger(
			TestSequenceTools.class.getSimpleName());

	@Test
	public void testReverseComplementSmall() {
		MemorySequence a = new MemorySequence(log);
		a.setSequence("AACCGGTTACTGACTG");
		MemorySequence b = (MemorySequence) SequenceTools.reverseComplement(a);
		for (int i = 1; i <= 16; i++) {
			Assert.assertEquals(a.getNucleotide(i),
					b.getReverseNucleotide(17 - i));
		}
		System.out.println(a);
		System.out.println(b);
	}

	@Test
	public void testReverseComplementOdd() {
		MemorySequence a = new MemorySequence(log);
		a.setSequence("AACCGGTTACTGACT");
		MemorySequence b = (MemorySequence) SequenceTools.reverseComplement(a);
		for (int i = 1; i <= 15; i++) {
			Assert.assertEquals(a.getNucleotide(i),
					b.getReverseNucleotide(16 - i));
		}
		System.out.println(a);
		System.out.println(b);
	}
}
