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
		assertEquals(9 * 300 + 200, array.size());

		Data<?> vardata = entry.get(new StringKey("variableStep"));
		assertEquals(2200 + 150, Streams.of(vardata.get()).count());
	}

	@Test
	public void testReadWig() throws Exception {
		File f = DataManager.file("anthracis.wig");
		testFile(f);
	}
}
