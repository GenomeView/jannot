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
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.junit.Test;

import net.sf.jannot.Data;
import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.JavaLogInterceptor;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import net.sf.jannot.wiggle.FloatArrayWiggle;
import net.sf.nameservice.NameService;
import support.DataManager;

public class BedGraphTest {

	private final DistributingReporter log;
	private final Global global;

	public BedGraphTest() throws ReadFailedException, IOException {
		log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log));
	}

	private void checkLogs() {
		// check log/2 and log/3 separately
		verify(log, times(0)).log(eq(Level.WARNING), anyString());
		verify(log, times(0)).log(eq(Level.WARNING), anyString(), any());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString(), any());
	}

	private void testFile(File file)
			throws URISyntaxException, IOException, ReadFailedException {
		DataSource ds = DataSourceFactory
				.create(new Locator(file, global.getLog()), global);
		EntrySet es = ds.read(new EntrySet(global));
		checkLogs();
		assertEquals("anthracis", es.firstEntry().getID());

		// the test file only contains anthracis chromosome data
		assertEquals(1, es.size());
		Entry entry = es.firstEntry();

		// the name of the entry is weird, the full filename
		// src/test/resources/anthracis.bedGraph. Just get first

		Data<?> data = entry.get(entry.iterator().next());
		assertTrue(data instanceof FloatArrayWiggle);
		FloatArrayWiggle array = (FloatArrayWiggle) data;
		// data goes to 4700 but FloatArrayWiggle sizes larger
		assertTrue(array.size() > 4700);

		// compare with actual values in / not in file
		// first 2000 are never set. Expect 0?
		for (int n = 0; n < 2000; n++) {
			assertEquals("Wrong value at " + n, 0f,
					data.get(n, n + 1).iterator().next());
		}
		for (int n = 2000; n < 2300; n++) {
			assertEquals("Wrong value at " + n, -1f,
					data.get(n, n + 1).iterator().next());
		}
		for (int n = 4400; n < 4700; n++) {
			assertEquals("Wrong value at " + n, 1f,
					data.get(n, n + 1).iterator().next());
		}
		// the array fills in also values at the end... probably 0?
		for (int n = 4700; n < 5000; n++) {
			assertEquals("Wrong value at " + n, 0f,
					data.get(n, n + 1).iterator().next());
		}

	}

	@Test
	public void testReadWig() throws Exception {
		File f = DataManager.file("anthracis.bedGraph");
		testFile(f);
	}
}
