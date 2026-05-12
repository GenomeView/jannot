package net.sf.jannot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LocationTest {
	private final static Location loc1 = new Location(0, 10, true, true);
	private final static Location loc2 = new Location(10, 20, true, true);

	@Test
	public void smoke() {
		new Location(0, 1, true, true);
	}

	@Test
	public void checkValues() {
		assertEquals(0, loc1.start());
		assertEquals(10, loc1.end());
	}

}
