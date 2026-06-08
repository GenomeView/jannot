package net.sf.jannot;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Test;

import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.cache.SourceCache;
import net.sf.nameservice.NameService;

public class MemoryListDataTest {

	private final DistributingReporter log;
	private final Global global;
	private final MemoryListData<Integer> mld;

	public MemoryListDataTest() throws IOException {
		log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log),
				new DataSourceFactory(mock(SourceCache.class), true));

		mld = new MemoryListData<Integer>(global) {
			@Override
			public String label() {
				return "label";
			}

		};

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
	public void testAdd() {
		List<Integer> list = Arrays.asList(1, 2, 3);
		mld.addAll((Iterable) list);
		assertEquals(list, mld);
		mld.add(5);
		assertEquals(Arrays.asList(1, 2, 3, 5), mld);
		List<Integer> sublist = StreamSupport
				.stream(mld.get(1, 4).spliterator(), false)
				.collect(Collectors.toList());
		;
		assertEquals(Arrays.asList(2, 3, 5), sublist);

	}
}
