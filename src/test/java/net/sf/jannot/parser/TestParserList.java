package net.sf.jannot.parser;

import org.junit.Assert;
import org.junit.Test;

import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

public class TestParserList {

	private Reporter log = new ReportToLogger("TestParserList");

	@Test
	public void testGFF() {
		Parser[] arr = ParserFactory.parsers("test", log);
		boolean contains = false;
		for (Parser p : arr)
			if (p instanceof GFF3Parser)
				contains = true;

		Assert.assertTrue(contains);

	}

	@Test
	public void testVCF() {
		Parser[] arr = ParserFactory.parsers("test", log);
		boolean contains = false;
		for (Parser p : arr)
			if (p instanceof VCFParser)
				contains = true;

		Assert.assertTrue(contains);

	}
}
