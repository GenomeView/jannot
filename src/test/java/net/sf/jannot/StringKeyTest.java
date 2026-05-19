package net.sf.jannot;

import java.util.Arrays;
import java.util.List;

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

}