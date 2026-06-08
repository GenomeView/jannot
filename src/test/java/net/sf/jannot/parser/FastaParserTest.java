package net.sf.jannot.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.Data;
import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.JavaLogInterceptor;
import net.sf.jannot.StringKey;
import net.sf.jannot.alignment.mfa.Alignment;
import net.sf.jannot.alignment.mfa.AlignmentAnnotation;
import net.sf.jannot.refseq.Sequence;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import net.sf.jannot.source.cache.SourceCache;
import net.sf.nameservice.NameService;
import support.DataManager;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class FastaParserTest {

	private final DistributingReporter log;
	private final Global global;

	public FastaParserTest() throws IOException {
		log = mock(DistributingReporter.class);
		DataSourceFactory factory = new DataSourceFactory(
				mock(SourceCache.class), true);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log), factory);
	}

	@After
	public void after() {
		// check log/2 and log/3 separately
		verify(log, times(0)).log(eq(Level.WARNING), anyString());
		verify(log, times(0)).log(eq(Level.WARNING), anyString(), any());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString(), any());
	}

	private void testFile(File file) throws URISyntaxException, IOException {
		DataSource ds = global.getSourceFactory()
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
		DataSource ds = global.getSourceFactory()
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
		// CHECK this test does NOT trigger the m-fasta part of the FastaParser
		// (line 72ev). Is this bug in parser ?
		DataSource ds = global.getSourceFactory().create(new Locator(
				DataManager.file("10313-CDS.fasta"), global.getLog()), global);
		EntrySet es = ds.read(new EntrySet(global));

		assertEquals(11386 / 2, es.size());
		// EntrySet sorts the names alphabetically... Makes not much sense in
		// this case,
		// this gives some 'random' entry somewhere deep in the file.
		assertEquals("1||1000275||1001150||BACI_c10590||1||CDS||71503756||1028",
				es.firstEntry().getID());
	}

	@Test
	public void testMultialignFasta() throws Exception {
		// this test triggers the m-fasta part in FastaParser.
		DataSource ds = global.getSourceFactory().create(
				new Locator(DataManager.file("apoex.fa"), global.getLog()),
				global);
		EntrySet es = ds.read(new EntrySet(global));

		assertEquals(1, es.size());
		// first entry contains all data.
		Entry entry = es.firstEntry();
		Data<?> data = entry.get(new StringKey("src/test/resources/apoex.fa"));
		assertTrue(data instanceof AlignmentAnnotation);
		AlignmentAnnotation aligns = (AlignmentAnnotation) data;
		assertEquals(11, aligns.size());
		Alignment align = aligns.get(0);
		assertEquals("APE_BOVIN", align.name());
	}
}
