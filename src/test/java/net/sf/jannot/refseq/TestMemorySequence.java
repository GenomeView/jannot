/**
 * %HEADER%
 */
package net.sf.jannot.refseq;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import net.sf.jannot.Entry;
import net.sf.jannot.Global;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestMemorySequence {

	private final Global global;

	public TestMemorySequence() throws IOException {
		global = new Global();

	}

	/**
	 * When adding a String to an empty sequence, the sequence should be
	 * extended with this String.
	 * 
	 */

	@Test
	public void addSequenceTest() {
		Entry entry = new Entry("test", global);
		MemorySequence seq = (MemorySequence) entry.sequence();
		String seqString = "actgactg";
		seq.addSequence(seqString);
		assertEquals(seqString.toUpperCase(), entry.sequence().toString());
	}
}
