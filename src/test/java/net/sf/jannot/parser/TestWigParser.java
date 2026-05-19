package net.sf.jannot.parser;

import static org.junit.Assert.assertEquals;
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

import org.apache.commons.lang3.stream.Streams;
import org.junit.Test;

import net.sf.jannot.Data;
import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.JavaLogInterceptor;
import net.sf.jannot.StringKey;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import net.sf.jannot.wiggle.TroveArrayWiggle;
import net.sf.nameservice.NameService;
import support.DataManager;

public class TestWigParser {

	private final DistributingReporter log;
	private final Global global;

	public TestWigParser() throws ReadFailedException, IOException {
		log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log));
	}

	private void checkLogs() {
		verify(log, times(0)).log(eq(Level.WARNING), anyString(), any());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString(), any());
	}

	private void testFile(File file)
			throws URISyntaxException, IOException, ReadFailedException {
		DataSource ds = DataSourceFactory
				.create(new Locator(file, global.getLog()), global);
		EntrySet es = ds.read(new EntrySet(global));
		checkLogs();
		assertEquals("anthracis", es.firstEntry().getID());

		assertEquals(1, es.size());

		Entry entry = es.firstEntry();
		assertEquals(2, entry.keys().size());

		Data<?> fixeddata = entry.get(new StringKey("fixedStep"));
		TroveArrayWiggle array = (TroveArrayWiggle) fixeddata;
		assertEquals(10000 + 9 * 300 + 200, array.size());

		// check start=10000 step=300 span=200 value=1000
		for (int n = 0; n < 200; n++) {
			assertEquals("Wrong value at " + n, 1000f,
					fixeddata.get(10000 + n, 10001 + n).iterator().next());
		}
		for (int n = 200; n < 300; n++) {
			assertEquals("Wrong value at " + n, 0f,
					fixeddata.get(10000 + n, 10001 + n).iterator().next());
		}

		int offset9 = 9 * 300 + 10000;
		for (int n = 0; n < 200; n++) {
			assertEquals("Wrong value at " + n, 100f, fixeddata
					.get(offset9 + n, offset9 + 1 + n).iterator().next());
		}
		for (int n = 200; n < 300; n++) {
			assertEquals("Wrong value at " + n, 0f, fixeddata
					.get(offset9 + n, offset9 + 1 + n).iterator().next());
		}

		Data<?> vardata = entry.get(new StringKey("variableStep"));
		array = (TroveArrayWiggle) vardata;
		assertEquals(22350, Streams.of(vardata.get()).count());
		assertEquals(22350, array.size());
		// check span=150 value=10
		for (int n = 0; n < 150; n++) {
			assertEquals("Wrong value at " + n, 10f,
					vardata.get(20000 + n, 20001 + n).iterator().next());
		}
		// check un-set area between first and second area
		for (int n = 20150; n < 20300; n++) {
			assertEquals("Wrong value at " + n, 0f,
					vardata.get(n, n + 1).iterator().next());
		}
		for (int n = 0; n < 150; n++) {
			assertEquals("Wrong value at " + n, 12.5f,
					vardata.get(20300 + n, 20301 + n).iterator().next());
		}

	}

	@Test
	public void testReadWig() throws Exception {
		File f = DataManager.file("anthracis.wig");
		testFile(f);
	}
}
