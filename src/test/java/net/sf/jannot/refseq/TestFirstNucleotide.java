package net.sf.jannot.refseq;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.junit.Test;

import net.sf.jannot.Cleaner;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.IndexedFastaDataSource;
import net.sf.jannot.source.Locator;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestFirstNucleotide {
	private final Global global;

	public TestFirstNucleotide() throws ReadFailedException, IOException {
		global = new Global();
	}

	@Test
	public void testNucleotide() throws MalformedURLException, IOException,
			ReadFailedException, URISyntaxException {

		global.getLog().log(Level.INFO, "Loading source");
		Locator l = new Locator("http://genomeview.org/frigg/genome.fasta",
				global.getLog());
		Locator i = new Locator("http://genomeview.org/frigg/genome.fasta.fai",
				global.getLog());

		IndexedFastaDataSource ifd = new IndexedFastaDataSource(l, i, global);
		System.out.println("Reading entries");
		EntrySet es = ifd.read(new EntrySet(global));

		System.out.println("Query");
		for (Character c : es.getEntry("chr1").sequence().get(1, 1000)) {
			System.out.print(c);
		}

		System.out.println("Query");
		for (Character c : es.getEntry("chr1").sequence().get(-10, 1000)) {
			System.out.print(c);
		}

		Cleaner.exit(global.getLog());

	}
}
