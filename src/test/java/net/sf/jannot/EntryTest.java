package net.sf.jannot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;

import org.junit.Test;
import org.mockito.exceptions.verification.NeverWantedButInvoked;

import net.sf.jannot.refseq.Sequence;
import net.sf.jannot.shortread.ReadGroup;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.cache.SourceCache;
import net.sf.nameservice.NameService;

public class EntryTest {

	private static final DataKey key1 = new StringKey("key1");
	private static final DataKey key2 = new StringKey("key2");
	private static final DataKey key3 = new StringKey("key3");
	private static final DataKey key4 = new StringKey("key4");
	private static final Sequence data1 = mock(Sequence.class);
	private static final Sequence seqdata = mock(Sequence.class);
	private static final Data<?> data3 = mock(Data.class);
	private static final FeatureAnnotation data4 = mock(
			FeatureAnnotation.class);

	private final DistributingReporter log;
	private final Global global;
	private final Entry entry1, entry1a, entry2;

	public EntryTest() throws IOException {
		log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log),
				new DataSourceFactory(mock(SourceCache.class), true));
		entry1 = new Entry("test", global);
		entry1a = new Entry("test", global);
		entry2 = new Entry("other", global);

	}

	/**
	 * call this after every test that is supposed to give no warning. We can't
	 * use it in @after because some tests are testing if a warning is raised
	 * and then the @after would fail
	 */
	private void checkLogs() {
		// check log/2 and log/3 separately
		verify(log, times(0)).log(eq(Level.WARNING), anyString());
		verify(log, times(0)).log(eq(Level.WARNING), anyString(), any());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString(), any());
	}

	@Test
	public void smoke() {
		checkLogs();
	}

	@Test
	public void idTest() {
		assertEquals("test", entry1.getID());
		checkLogs();
	}

	@Test
	public void testMaxSize() {
		when(data1.size()).thenReturn(10);
		when(seqdata.size()).thenReturn(20);
		when(data4.getMaximumCoordinate()).thenReturn(30);

		entry1.add(key1, data1);
		entry1.add(key2, seqdata);
		assertEquals(20, entry1.getMaximumLength());

		// data3 is unknown type, it will be ignored
		entry1.add(key3, data3);
		assertEquals(20, entry1.getMaximumLength());

		// data4 is FeatureAnnotation
		entry1.add(key4, data4);
		assertEquals(30, entry1.getMaximumLength());
		checkLogs();

	}

	@Test(expected = NeverWantedButInvoked.class)
	public void testKeyReuse() {
		// key reuse is not allowed, data should be ignored
		entry1.add(key1, data1);
		entry1.add(key1, seqdata);

		assertEquals(data1, entry1.get(key1));
		checkLogs();
	}

	@Test
	public void testKeys() {
		assertFalse(entry1.contains(key1));
		assertFalse(entry1.contains(key2));
		assertFalse(entry1.contains(key3));

		entry1.add(key1, data1);
		entry1.add(key2, seqdata);
		assertEquals(new HashSet(Arrays.asList(key1, key2)), entry1.keys());
		assertTrue(entry1.contains(key1));
		assertTrue(entry1.contains(key2));
		assertFalse(entry1.contains(key3));
		entry1.add(key3, seqdata);
		assertEquals(new HashSet(Arrays.asList(key1, key2, key3)),
				entry1.keys());

		entry1.remove(key1);
		assertFalse(entry1.contains(key1));

	}

	@Test
	public void testShortReads() {
		Iterator<ReadGroup> it = entry1.shortReads().iterator();
		assertFalse(it.hasNext());
		entry1.add(key1, data1);
		assertFalse(it.hasNext());
		ReadGroup group = mock(ReadGroup.class);
		entry1.add(key2, group);
		entry1.add(key3, data3);
		it = entry1.shortReads().iterator();
		assertEquals(group, it.next());
		assertFalse(it.hasNext());
	}

	@Test
	public void testSequence() {
		assertEquals(0, entry1.sequence().size());

		entry1.add(key1, data1);
		entry1.add(key2, seqdata);
		entry1.add(key3, data3);
		entry1.setSequence(seqdata);
		assertEquals(seqdata, entry1.sequence());
	}

	@Test
	public void testCompare() {
		assertTrue(entry1.compareTo(entry1a) == 0);
		assertTrue(entry1.compareTo(entry2) > 0);
		assertTrue(entry2.compareTo(entry1) < 0);
	}

}
