package net.sf.jannot;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class FeatureTest {
	private final static Location loc1 = new Location(10, 20);

	@Test
	public void smoke() {
		new Feature(loc1, null, Strand.UNKNOWN);
	}

	@Test
	public void testConstructorsEqual() {
		Feature f1 = new Feature(loc1, null, Strand.UNKNOWN);
		Feature f2 = new Feature(new HashSet<>(Arrays.asList(loc1)), null,
				Strand.UNKNOWN);
		assertEquals(f1, f2);
	}

}
