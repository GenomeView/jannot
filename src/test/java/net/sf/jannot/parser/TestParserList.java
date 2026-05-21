package net.sf.jannot.parser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Global;
import net.sf.jannot.JavaLogInterceptor;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.nameservice.NameService;

public class TestParserList {

	private final Global global;
	private final DistributingReporter log;

	public TestParserList() throws ReadFailedException, IOException {
		log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log));
	}

	@After
	public void after() {
		// check log/2 and log/3 separately
		verify(log, times(0)).log(eq(Level.WARNING), anyString());
		verify(log, times(0)).log(eq(Level.WARNING), anyString(), any());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString(), any());
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
