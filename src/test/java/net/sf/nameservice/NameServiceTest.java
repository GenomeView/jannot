package net.sf.nameservice;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.Global;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class NameServiceTest {

	private final Reporter log;
	private NameService ns;

	public NameServiceTest() throws IOException {
		Global global = new Global();
		log = global.getLog();
		ns = global.getNameService();
	}

	@Test
	public void testDefaultSynonyms() {
		String primary = "Acaryochloris marina MBIC11017 chromosome, complete genome. (NC_009925)";
		String alt1 = "NC_009925";
		String alt2 = "NC_009925.1";

		Assert.assertEquals(primary, ns.getPrimaryName(alt1));
		Assert.assertEquals(primary, ns.getPrimaryName(alt2));
		Assert.assertEquals(primary,
				ns.getPrimaryName(primary.replace(' ', '_')));
	}

	@Test
	public void testAddSynonym() {
		String primary = "Acaryochloris marina MBIC11017 chromosome, complete genome. (NC_009925)";

		ns.addSynonym(primary, "test alternative");

		Assert.assertEquals(primary, ns.getPrimaryName("test alternative"));
		Assert.assertEquals(primary, ns.getPrimaryName("test ALTERNAtive"));

	}
}
