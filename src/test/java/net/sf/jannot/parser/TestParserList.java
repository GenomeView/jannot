package net.sf.jannot.parser;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.Global;
import net.sf.jannot.exception.ReadFailedException;

public class TestParserList {

	private final Global global;

	public TestParserList() throws IOException, ReadFailedException {
		global = new Global();
	}

	@Test
	public void testGFF() {
		Parser[] arr = ParserFactory.parsers("test", global);
		boolean contains = false;
		for (Parser p : arr) {
			if (p instanceof GFF3Parser) {
				contains = true;
			}
		}

		Assert.assertTrue(contains);

	}

	@Test
	public void testVCF() {
		Parser[] arr = ParserFactory.parsers("test", global);
		boolean contains = false;
		for (Parser p : arr) {
			if (p instanceof VCFParser) {
				contains = true;
			}
		}

		Assert.assertTrue(contains);

	}
}
