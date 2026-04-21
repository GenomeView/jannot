package net.sf.nameservice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.sf.jannot.exception.ReadFailedException;
import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestNameService {

	@Before
	public void before() throws ReadFailedException {
		Reporter log = new ReportToLogger((getClass().getSimpleName()));
		NameService.init(log);
	}

	@Test
	public void testDefaultSynonyms() {
		String primary = "Acaryochloris marina MBIC11017 chromosome, complete genome. (NC_009925)";
		String alt1 = "NC_009925";
		String alt2 = "NC_009925.1";

		Assert.assertEquals(primary,
				NameService.instance().getPrimaryName(alt1));
		Assert.assertEquals(primary,
				NameService.instance().getPrimaryName(alt2));
		Assert.assertEquals(primary, NameService.instance()
				.getPrimaryName(primary.replace(' ', '_')));
	}

	@Test
	public void testAddSynonym() {
		String primary = "Acaryochloris marina MBIC11017 chromosome, complete genome. (NC_009925)";

		NameService.instance().addSynonym(primary, "test alternative");

		Assert.assertEquals(primary,
				NameService.instance().getPrimaryName("test alternative"));
		Assert.assertEquals(primary,
				NameService.instance().getPrimaryName("test ALTERNAtive"));

	}
}
