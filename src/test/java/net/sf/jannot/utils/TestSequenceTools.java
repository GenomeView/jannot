/**
 * %HEADER%
 */
package net.sf.jannot.utils;

import java.io.IOException;

import org.junit.Test;

import junit.framework.Assert;
import net.sf.jannot.Global;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.refseq.MemorySequence;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestSequenceTools {

	private final Global global;

	public TestSequenceTools() throws IOException, ReadFailedException {
		global = new Global();
	}

	@Test
	public void testReverseComplementSmall() {
		MemorySequence a = new MemorySequence(global);
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
		MemorySequence a = new MemorySequence(global);
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
