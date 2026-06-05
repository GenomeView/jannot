package net.sf.jannot;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Test;

import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.cache.SourceCache;
import net.sf.nameservice.NameService;

public class EntryTest {

	private final DistributingReporter log;
	private final Global global;
	private final Entry entry1, entry1a, entry2;

	public EntryTest() throws ReadFailedException, IOException {
		log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log),
				new DataSourceFactory(mock(SourceCache.class), true));
		entry1 = new Entry("test", global);
		entry1a = new Entry("test", global);
		entry2 = new Entry("other", global);

	}

	@After
	public void after() {
		checkLogs();
	}

	private void checkLogs() {
		// check log/2 and log/3 separately
		verify(log, times(0)).log(eq(Level.WARNING), anyString());
		verify(log, times(0)).log(eq(Level.WARNING), anyString(), any());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString(), any());
	}

	@Test
	public void smoke() {
	}

	@Test
	public void idTest() {
		assertEquals("test", entry1.getID());
	}

}
