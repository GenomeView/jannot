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
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;

import org.junit.Test;

import net.sf.jannot.Data;
import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.JavaLogInterceptor;
import net.sf.jannot.alignment.maf.AbstractAlignmentBlock;
import net.sf.jannot.alignment.maf.AbstractAlignmentSequence;
import net.sf.jannot.alignment.maf.MAFMemoryMultipleAlignment;
import net.sf.jannot.alignment.maf.MemoryAlignmentSequence;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.FileSource;
import net.sf.jannot.source.Locator;
import net.sf.jannot.source.cache.SourceCache;
import net.sf.nameservice.NameService;
import support.DataManager;

public class MAFParserTest {

	private final DistributingReporter log;
	private final Global global;

	public MAFParserTest() throws IOException {
		log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log),
				new DataSourceFactory(mock(SourceCache.class), true));
	}

	private void checkLogs() {
		// check log/2 and log/3 separately
		verify(log, times(0)).log(eq(Level.WARNING), anyString());
		verify(log, times(0)).log(eq(Level.WARNING), anyString(), any());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString(), any());
	}

	@Test
	public void testOfficialFile() throws Exception {

		File f = DataManager.file("test.maf");
		DataSource ds = global.getSourceFactory()
				.create(new Locator(f, global.getLog()), global);
		assertTrue(ds instanceof FileSource);
		assertTrue(((FileSource) ds).getParser() instanceof MAFParser);

		EntrySet es = ds.read(new EntrySet(global));
		checkLogs();
		assertEquals(1, es.size());
		Entry entry = es.firstEntry();
		assertEquals("hg18.chr7", entry.getID());

		// the name of the entry is weird, the full filename
		// src/test/resources/anthracis.bedGraph. Just get first

		Data<?> data = entry.get(entry.keys().iterator().next());
		assertTrue(data instanceof MAFMemoryMultipleAlignment);

		MAFMemoryMultipleAlignment maf = (MAFMemoryMultipleAlignment) data;
		assertEquals(new HashSet<>(Arrays.asList("hg18.chr7", "panTro1.chr6",
				"baboon", "mm4.chr6", "rn3.chr4")), maf.species());

		// there are 3 alignment blocks in the file
		assertEquals(3, maf.noAlignmentBlocks());

		Iterator<AbstractAlignmentBlock> blockiter = maf.get().iterator();
		AbstractAlignmentBlock block1 = blockiter.next();
		AbstractAlignmentBlock block2 = blockiter.next();
		AbstractAlignmentBlock block3 = blockiter.next();

		// check the first block details
		assertEquals(5, block1.size());
		Iterator<AbstractAlignmentSequence> seqiter = block1.iterator();
		assertEquals("hg18.chr7 27578829 27578867", seqiter.next().toString());
		assertEquals("panTro1.chr6 28741141 28741179",
				seqiter.next().toString());
		assertEquals("baboon 116835 116873", seqiter.next().toString());
		assertEquals("mm4.chr6 53215345 53215383", seqiter.next().toString());
		assertEquals("rn3.chr4 81344244 81344284", seqiter.next().toString());

		// check the rest superficially
		assertEquals(5, block2.size());
		assertEquals(4, block3.size());

	}

	@Test
	public void testAnthracisMaf() throws Exception {
		// actual files look different. Checking 1 to be sure it all works
		File f = DataManager.file("anthracis_short.maf");
		DataSource ds = global.getSourceFactory()
				.create(new Locator(f, global.getLog()), global);
		assertTrue(ds instanceof FileSource);
		assertTrue(((FileSource) ds).getParser() instanceof MAFParser);

		EntrySet es = ds.read(new EntrySet(global));
		checkLogs();
		assertEquals(1, es.size());
		Entry entry = es.firstEntry();
		assertEquals("anthracis", entry.getID());

		// the name of the entry is weird, the full filename
		// src/test/resources/anthracis.bedGraph. Just get first

		Data<?> data = entry.get(entry.keys().iterator().next());
		assertTrue(data instanceof MAFMemoryMultipleAlignment);

		MAFMemoryMultipleAlignment maf = (MAFMemoryMultipleAlignment) data;
		assertEquals(8, maf.species().size());

		// there are 3 alignment blocks in the file
		assertEquals(6, maf.noAlignmentBlocks());

		// get the first one
		Iterator<AbstractAlignmentBlock> blockiter = maf.get().iterator();
		AbstractAlignmentBlock block1 = blockiter.next();

		// it has 7 items: anthracis, licheniformis,..., amylolique
		assertEquals(7, block1.size());
		AbstractAlignmentSequence data1 = block1.iterator().next();
		assertTrue(data1 instanceof MemoryAlignmentSequence);
		MemoryAlignmentSequence mas = (MemoryAlignmentSequence) data1;
		assertEquals(19, mas.seq().size());

	}

}
