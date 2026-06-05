package net.sf.jannot.hts;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;

import junit.framework.Assert;
import net.sf.jannot.Data;
import net.sf.jannot.DataKey;
import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.JavaLogInterceptor;
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
public class TestMiniBAM {

	private final Global global;
	private final DistributingReporter log = mock(DistributingReporter.class);

	public TestMiniBAM() throws IOException {
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log),
				new DataSourceFactory(mock(SourceCache.class), true));
	}

	@Test
	public void testShortRead() throws Exception {

		Locator fData = new Locator(DataManager.file("tworead.bam"), log);
		Locator fIndex = new Locator(DataManager.file("tworead.bam.bai"), log);

		DataSource ds = global.getSourceFactory().create(fData, fIndex, global);
		Assert.assertNotNull(ds);
		EntrySet entries = ds.read(new EntrySet(global));
		Entry e = entries.getEntry("chr4");
		int dkCount = 0;
		int readCount = 0;
		for (DataKey dk : e) {
			dkCount++;
			Data d = e.get(dk);
			for (Object o : d.get(73151000, 73152000)) {
				readCount++;
				System.out.println(o);
			}
		}
		Assert.assertEquals(1, dkCount);
		Assert.assertEquals(2, readCount);
	}
}
