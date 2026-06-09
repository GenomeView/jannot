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
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.After;
import org.junit.Test;

import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.cache.SourceCache;
import net.sf.nameservice.NameService;

public class MemoryListDataTest {

	private final DistributingReporter log;
	private final Global global;
	private final MemoryListData<Located> mld;

	private final Loc L1 = new Loc(1), L2 = new Loc(2), L3 = new Loc(3),
			L5 = new Loc(5);

	@After
	public void after() {
		checkLogs();
	}

	public MemoryListDataTest() throws IOException {
		log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log),
				new DataSourceFactory(mock(SourceCache.class), true));

		mld = new MemoryListData<Located>(global) {
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
	public void testAddGet() {
		List<Loc> list = Arrays.asList(L1, L2, L3);
		mld.addAll((Iterable) list);

		List<Located> all = StreamSupport.stream(mld.get().spliterator(), false)
				.collect(Collectors.toList());
		assertEquals(list, all);

		assertEquals(list, mld);
		mld.add(L5);
		assertEquals(Arrays.asList(L1, L2, L3, L5), mld);

		List<Located> sublist = StreamSupport
				.stream(mld.get(2, 5).spliterator(), false)
				.collect(Collectors.toList());
		assertEquals(Arrays.asList(L2, L3, L5), sublist);

		MemoryListData<Located> m2 = new MemoryListData<Located>(global) {
			@Override
			public String label() {
				return "label2";
			}
		};
		m2.addAll(mld);
		assertEquals(mld, m2); // using ArrayList.equal

	}
}

/**
 * Stupid Located object for testing the iterators
 */
class Loc implements Located {
	private int n;

	public Loc(int n) {
		this.n = n;
	}

	@Override
	public int start() {
		return n;
	}

	@Override
	public int end() {
		return n;
	}

	@Override
	public String toString() {
		return "" + n;
	}

	@Override
	public int hashCode() {
		return Objects.hash(n);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Loc other = (Loc) obj;
		return n == other.n;
	}
}
