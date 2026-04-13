package net.sf.jannot.refseq;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.junit.Test;

import net.sf.jannot.Cleaner;
import net.sf.jannot.EntrySet;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.IndexedFastaDataSource;
import net.sf.jannot.source.Locator;
import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestFirstNucleotide {
	private static final Reporter log = new ReportToLogger(
			TestFirstNucleotide.class.getCanonicalName());

	@Test
	public void testNucleotide() throws MalformedURLException, IOException,
			ReadFailedException, URISyntaxException {

		log.log(Level.INFO, "Loading source");
		Locator l = new Locator("http://genomeview.org/frigg/genome.fasta");
		Locator i = new Locator("http://genomeview.org/frigg/genome.fasta.fai");

		IndexedFastaDataSource ifd = new IndexedFastaDataSource(l, i, log);
		System.out.println("Reading entries");
		EntrySet es = ifd.read();

		System.out.println("Query");
		for (Character c : es.getEntry("chr1").sequence().get(1, 1000)) {
			System.out.print(c);
		}
		System.out.println();
		System.out.println();

		System.out.println("Query");
		for (Character c : es.getEntry("chr1").sequence().get(-10, 1000)) {
			System.out.print(c);
		}

		Cleaner.exit();

	}
}
