package net.sf.jannot;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import tudelft.utilities.junit.GeneralTests;

public class StringKeyTest extends GeneralTests<StringKey> {

	private StringKey KEY1 = new StringKey("a");
	private StringKey KEY1A = new StringKey("a");
	private StringKey KEY2 = new StringKey("b");

	@Override
	public List<List<StringKey>> getGeneralTestData() {

		return Arrays.asList(Arrays.asList(KEY1, KEY1A), Arrays.asList(KEY2));
	}

	@Override
	public List<String> getGeneralTestStrings() {
		return Arrays.asList("a", "b");
	}

	@Test
	public void compareTest() {
		Type a = new Type("a");
		Type b = new Type("b");
		assertEquals(1, b.compareTo(a));
		assertEquals(-1, a.compareTo(b));
		assertEquals(0, a.compareTo(a));
		assertEquals(0, b.compareTo(b));

	}

}