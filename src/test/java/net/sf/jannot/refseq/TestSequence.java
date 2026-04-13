/**
 * %HEADER%
 */
package net.sf.jannot.refseq;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jannot.EntrySet;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.IndexedFastaDataSource;
import net.sf.jannot.source.Locator;
import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

public class TestSequence {

	private static final Reporter log = new ReportToLogger(
			TestSequence.class.toString());

	@Ignore // urls give access denied
	@Test
	public void testFaidx() throws URISyntaxException, MalformedURLException,
			ReadFailedException, IOException {
		Locator l = new Locator(
				"http://bioinformatics.psb.ugent.be/downloads/genomeview/genomes/hg19/genome.fasta");
		Locator i = new Locator(
				"http://bioinformatics.psb.ugent.be/downloads/genomeview/genomes/hg19/genome.fasta.fai");

		EntrySet es = new IndexedFastaDataSource(l, i, log).read();
		Sequence seq = es.firstEntry().sequence();
		String tmp = "";
		for (Character c : seq.get(1, 4)) {
			tmp += c;
		}
		assertEquals(3, tmp.length());

	}

	@Test
	public void testSubSequence() {
		MemorySequence a = new MemorySequence("AGTCG");

		assertEquals("GT", a.subsequence(2, 4).stringRepresentation());
		assertEquals("AGTCG", a.subsequence(1, 6).stringRepresentation());
		String tmp = "";
		for (Character c : a.get(2, 4)) {
			tmp += c;
		}
		assertEquals("GT", tmp);
		tmp = "";
		for (Character c : a.get(1, 6)) {
			tmp += c;
		}
		assertEquals("AGTCG", tmp);

	}

}
