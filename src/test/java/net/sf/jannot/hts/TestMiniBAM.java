package net.sf.jannot.hts;

import org.junit.Test;

import junit.framework.Assert;
import net.sf.jannot.Data;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import support.DataManager;
import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestMiniBAM {
	private static Reporter log = new ReportToLogger(
			TestMiniBAM.class.toString());

	@Test
	public void testShortRead() throws Exception {

		Locator fData = new Locator(DataManager.file("tworead.bam"), log);
		Locator fIndex = new Locator(DataManager.file("tworead.bam.bai"), log);

		DataSource ds = DataSourceFactory.create(fData, fIndex, log);
		Assert.assertNotNull(ds);
		EntrySet entries = ds.read();
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
