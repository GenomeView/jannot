package net.sf.jannot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class FeatureTest {

	private final static Location loc0 = new Location(0, 18);
	private final static Location loc1 = new Location(10, 20);
	private final static Location loc2 = new Location(10, 15);

	final Feature f0 = new Feature(loc0, null, Strand.UNKNOWN);
	final Feature f1 = new Feature(loc1, null, Strand.UNKNOWN);
	final Feature f2 = new Feature(loc2, null, Strand.UNKNOWN);

	@Test
	public void smoke() {
		new Feature(loc1, null, Strand.UNKNOWN);
	}

	@Test
	public void testConstructorsEqual() {
		Feature f2 = new Feature(new HashSet<>(Arrays.asList(loc1)), null,
				Strand.UNKNOWN);
		assertEquals(f1, f2);
	}

	@Test
	public void testCompare() {
		assertEquals(1, f1.compareTo(f0));
		assertEquals(-1, f0.compareTo(f1));
		assertEquals(0, f0.compareTo(f0));

		Feature merged = new Feature(loc0.extend(loc1), null, Strand.UNKNOWN);
		assertEquals(1, merged.compareTo(f0));
	}

	@Test
	public void testEmptyCopy() {
		Feature f = new Feature(loc1, null, Strand.UNKNOWN);
		Feature g = f.copy();
	}

	@Test
	public void testFeatures() {

		Feature f = new Feature(new Location(1, 100), null, Strand.FORWARD);
		f.copy();

	}

	@Test
	public void testFeatureSetLocation() {
		Feature f = new Feature(new Location(1, 10), null, Strand.FORWARD);
		Assert.assertEquals(1, f.start());
		Assert.assertEquals(10, f.end());

		f.setLocation(Arrays.asList(new Location(5, 15)));
		Assert.assertEquals(5, f.start());
		Assert.assertEquals(15, f.end());

		f.setLocation(Arrays.asList(new Location(3, 13)));
		Assert.assertEquals(3, f.start());
		Assert.assertEquals(13, f.end());

		f.setLocation(Arrays.asList(new Location(4, 8), new Location(12, 16)));
		Assert.assertEquals(4, f.start());
		Assert.assertEquals(16, f.end());

		f.addLocation(new Location(17, 22));
		Assert.assertEquals(4, f.start());
		Assert.assertEquals(22, f.end());

		List<Location> list = new ArrayList<Location>();
		list.add(new Location(5, 7));
		list.add(new Location(8, 11));
		f.setLocation(list);

		Assert.assertEquals(5, f.start());
		Assert.assertEquals(11, f.end());

		list = new ArrayList<Location>();
		list.add(new Location(17, 31));
		f.setLocation(list);

		Assert.assertEquals(17, f.start());
		Assert.assertEquals(31, f.end());

	}

	@Test
	public void testQualifier() {
		Feature f = new Feature(new Location(1, 100), null, Strand.FORWARD);

		assertTrue(f.getQualifiersKeys().size() == 0);

		f.addQualifier("protein", "test");
		assertTrue(f.getQualifiersKeys().size() == 1);
		assertEquals("test", f.qualifier("protein"));

		f.addQualifier("protein", "more");
		assertTrue(f.getQualifiersKeys().size() == 1);
		assertEquals("test,more", f.qualifier("protein"));

	}

}
