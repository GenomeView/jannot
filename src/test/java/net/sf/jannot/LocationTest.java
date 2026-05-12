package net.sf.jannot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sf.jannot.event.ChangeEvent;

public class LocationTest {
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
}
