package net.sf.jannot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sf.jannot.event.ChangeEvent;

public class LocationTest {
	private static final double SMALL = 1e-8;
	private final static Location loc1 = new Location(0, 10);
	private final static Location loc2 = new Location(10, 20);

	@Test
	public void smoke() {
		new Location(0, 1, true, true);
	}

	@Test
	public void checkValues() {
		assertEquals(0, loc1.start());
		assertEquals(10, loc1.end());
	}

	@Test
	public void testUndoRedo() {
		Location loc = new Location(0, 10, true, true);
		ChangeEvent action = loc.setStart(3);
		assertEquals(3, loc.start());
		action.undoChange();
		assertEquals(0, loc.start());
		action.doChange();
		assertEquals(3, loc.start());
	}

	@Test
	public void testUndoRedoDoesNotChangeCopy() {
		Location loc = new Location(0, 10, true, true);
		ChangeEvent action = loc.setStart(3);
		assertEquals(3, loc.start());
		Location copy = loc.copy();
		assertEquals(3, copy.start());

		action.undoChange();
		assertEquals(3, copy.start());
	}

	@Test
	public void testExtend() {
		Location extended = loc1.extend(loc2);
		assertEquals(0, extended.start());
		assertEquals(20, extended.end());

		// check originals were not affected
		assertEquals(new Location(0, 10), loc1);
		assertEquals(new Location(10, 20), loc2);
	}

	@Test
	public void testSwappedEnds() {
		Location loc = new Location(10, 0);
		assertEquals(loc1, loc);
	}

	@Test
	public void testOverlaps() {
		assertFalse(loc2.overlaps(new Location(0, 5)));
		assertTrue(loc2.overlaps(new Location(9, 10)));
		assertTrue(loc2.overlaps(new Location(10, 11)));
		assertTrue(loc2.overlaps(new Location(12, 18)));
		assertTrue(loc2.overlaps(new Location(20, 21)));
		assertFalse(loc2.overlaps(new Location(21, 31)));
	}

	@Test
	public void testLength() {
		assertEquals(11, loc1.length());
		assertEquals(11, loc2.length());
	}

	@Test
	public void testFraction() {
		assertEquals(-1f, loc2.fraction(0), SMALL);
		assertEquals(0f, loc2.fraction(10), SMALL);
		assertEquals(1f, loc2.fraction(20), SMALL);
		assertEquals(2f, loc2.fraction(30), SMALL);
	}

	@Test
	public void toStringTest() {
		assertEquals("0..10", loc1.toString());
	}

	@Test
	public void compareTest() {
		assertEquals(1, loc2.compareTo(loc1));
		assertEquals(0, loc2.compareTo(loc2));
		assertEquals(-1, loc1.compareTo(loc2));
		assertEquals(1, loc1.compareTo(new Location(0, 5)));
	}
}
