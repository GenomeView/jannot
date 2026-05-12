/**
 * %HEADER%
 */
package net.sf.jannot.syntenic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import net.sf.jannot.Location;
import net.sf.jannot.Strand;

public class SyntenicBlockTest {
	private final static String id1 = "id1";
	private final static String id2 = "id2";
	private final static Location loc1 = mock(Location.class);
	private final static Location loc2 = mock(Location.class);

	private final static SyntenicBlock sb = new SyntenicBlock(id1, id2, loc1,
			loc2, Strand.FORWARD, Strand.REVERSE);

	@Test
	public void smoke() {
		// FIXME? Is this really acceptable?
		SyntenicBlock b = new SyntenicBlock(null, null, null, null, null, null);
		b.toString();
	}

	@Test
	public void testFlip() {
		SyntenicBlock rb = sb.flip();

		assertEquals(id1, rb.target());
		assertEquals(id2, rb.reference());

		assertEquals(loc1, rb.targetLocation());
		assertEquals(loc2, rb.refLocation());

		assertEquals(Strand.REVERSE, rb.getRefStrand());
		assertEquals(Strand.FORWARD, rb.getTargetStrand());
	}

	@Test
	public void testMatch() {
		SyntenicBlock rb = sb.match(id1, id2);
		assertEquals(id1, rb.reference());
		assertEquals(id2, rb.target());
	}

	@Test
	public void testMatchFlip() {
		SyntenicBlock rb = sb.match(id2, id1);
		assertEquals(id1, rb.target());
		assertEquals(id2, rb.reference());
	}

	@Test
	public void testNoMatch() {
		SyntenicBlock rb = sb.match(id2, "unknownid");
		assertNull(rb);
	}

	@Test
	public void testNoMatch2() {
		SyntenicBlock rb = sb.match("unknownid", id2);
		assertNull(rb);
	}
}
