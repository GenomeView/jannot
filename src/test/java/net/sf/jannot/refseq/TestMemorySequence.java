/**
 * %HEADER%
 */
package net.sf.jannot.refseq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sf.jannot.Entry;
import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestMemorySequence {

	private Reporter log = new ReportToLogger(
			TestMemorySequence.class.getSimpleName());

	/**
	 * When adding a String to an empty sequence, the sequence should be
	 * extended with this String.
	 * 
	 */

	@Test
	public void addSequenceTest() {
		Entry entry = new Entry("test", log);
		MemorySequence seq = (MemorySequence) entry.sequence();
		String seqString = "actgactg";
		seq.addSequence(seqString);
		assertEquals(seqString.toUpperCase(), entry.sequence().toString());
	}
}
