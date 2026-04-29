package net.sf.jannot.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
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

	private final Global global;

	public TestFastaParser() throws IOException, ReadFailedException {
		global = new Global();
	}

	private void testFile(File file)
			throws URISyntaxException, IOException, ReadFailedException {
		DataSource ds = DataSourceFactory
				.create(new Locator(file, global.getLog()), global);
		EntrySet es = ds.read(new EntrySet(global));
		// System.out.println(es.firstEntry());
		Assert.assertEquals("TestFasta", es.firstEntry().getID());
		int count = 0;
		for (Entry e : es) {
			count++;
		}
		Assert.assertEquals(1, count);

		Sequence d = es.firstEntry().sequence();
		Assert.assertEquals(38, d.size());

		Assert.assertEquals("ACGTACGTAACCGGTTTTGGCCAATGCATGCAAGTTGA",
				d.stringRepresentation());
	}

	@Test
	public void testMiniFasta() throws Exception {
		File f = DataManager.file("mini.fasta");
		testFile(f);
	}

	@Test
	public void testWrite() throws Exception {
		File f = DataManager.file("mini.fasta");
		DataSource ds = DataSourceFactory
				.create(new Locator(f, global.getLog()), global);
		EntrySet es = ds.read(new EntrySet(global));

		File out = File.createTempFile("unittesting.", ".fasta");
		out.deleteOnExit();

		FileOutputStream fos = new FileOutputStream(out);
		for (Entry e : es) {
			new FastaParser(global).write(fos, e);
		}
		fos.close();

		testFile(out);

	}

	@Test
	public void testMFasta() throws Exception {

		DataSource ds = DataSourceFactory.create(new Locator(
				DataManager.file("10313-CDS.fasta"), global.getLog()), global);
		EntrySet es = ds.read(new EntrySet(global));

		assertEquals(11386 / 2, es.size());
		// EntrySet sorts the names alphabetically... Makes not much sense in
		// this case,
		// this gives some 'random' entry somewhere deep in the file.
		assertEquals("1||1000275||1001150||BACI_c10590||1||CDS||71503756||1028",
				es.firstEntry().getID());
	}

}
