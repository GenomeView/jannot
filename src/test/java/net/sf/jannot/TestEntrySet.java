package net.sf.jannot;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.nameservice.NameService;
import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestEntrySet {
	Reporter log = new ReportToLogger(TestEntrySet.class.getSimpleName());

	@Before
	public void before() throws ReadFailedException {
		NameService.init(log);
	}

	@Test
	public void testGet() {
		EntrySet es = new EntrySet(log);
		Entry x = es.getOrCreateEntry("chr1");
		Entry y = es.getOrCreateEntry("2");
		Assert.assertNotNull(x);
		Assert.assertNotNull(y);
		System.out.println(es.getEntry("1"));
		Assert.assertNotNull(es.getEntry("1"));
		Assert.assertNotNull(es.getEntry("CHR1"));
		Assert.assertNotNull(es.getEntry("chr1"));

		System.out.println(es.getEntry("chr2"));
		Assert.assertNotNull(es.getEntry("chr2"));
		Assert.assertNotNull(es.getEntry("Chr2"));
		Assert.assertNotNull(es.getEntry("chR2"));

	}
}