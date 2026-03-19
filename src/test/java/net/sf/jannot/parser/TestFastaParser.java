package net.sf.jannot.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.refseq.Sequence;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import support.DataManager;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestFastaParser {

	private void testFile(File file)
			throws URISyntaxException, IOException, ReadFailedException {

		DataSource ds = DataSourceFactory.create(new Locator(file));
		EntrySet es = ds.read();
		// System.out.println(es.firstEntry());
		Assert.assertEquals("TestFasta", es.firstEntry().getID());
		int count = 0;
		for (Entry e : es)
			count++;
		Assert.assertEquals(1, count);

		Sequence d = es.firstEntry().sequence();
		Assert.assertEquals(38, d.size());

		Assert.assertEquals("ACGTACGTAACCGGTTTTGGCCAATGCATGCAAGTTGA",
				d.stringRepresentation());
	}

	@Test
	public void testMiniFasta()
			throws URISyntaxException, IOException, ReadFailedException {
		File f = DataManager.file("mini.fasta");
		testFile(f);
	}

	@Test
	public void testWrite()
			throws URISyntaxException, IOException, ReadFailedException {
		File f = DataManager.file("mini.fasta");
		DataSource ds = DataSourceFactory.create(new Locator(f));
		EntrySet es = ds.read();

		File out = File.createTempFile("unittesting.", ".fasta");
		out.deleteOnExit();

		FileOutputStream fos = new FileOutputStream(out);
		for (Entry e : es)
			new FastaParser().write(fos, e);
		fos.close();

		testFile(out);

	}

}
