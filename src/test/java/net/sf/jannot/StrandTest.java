package net.sf.jannot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StrandTest {

	@Test
	public void testFromSymbol() {
		assertEquals(Strand.FORWARD, Strand.fromSymbol('+'));
		assertEquals(Strand.REVERSE, Strand.fromSymbol('-'));
		assertEquals(Strand.UNKNOWN, Strand.fromSymbol('.'));
		// check some other random chars
		assertEquals(Strand.UNKNOWN, Strand.fromSymbol('*'));
		assertEquals(Strand.UNKNOWN, Strand.fromSymbol(' '));
	}

	@Test
	public void testEquals() {
		assertTrue(Strand.FORWARD.equals(Strand.FORWARD));
		assertTrue(Strand.REVERSE.equals(Strand.REVERSE));
		// check a few unequals
		assertFalse(Strand.FORWARD.equals(Strand.REVERSE));
		assertFalse(Strand.REVERSE.equals(Strand.FORWARD));
		assertFalse(Strand.UNKNOWN.equals(Strand.UNKNOWN));
		assertFalse(Strand.UNKNOWN.equals(Strand.FORWARD));
		assertFalse(Strand.REVERSE.equals(Strand.UNKNOWN));

	}

	@Test
	public void testChar() {
		assertEquals("+", Strand.FORWARD.symbol());
		assertEquals("-", Strand.REVERSE.symbol());
		assertEquals(".", Strand.UNKNOWN.symbol());
	}
}