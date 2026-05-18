package net.sf.jannot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TypeTest {
	private TypeFactory factory = new TypeFactory();

	@Test
	public void smoke() {
		assertEquals(0, factory.values().length);
	}

	@Test
	public void orderTest() {
		addSomeTypes();
		assertValues(1, 2, 3, 4);
	}

	@Test
	public void moveDownTest() {
		addSomeTypes();
		factory.moveDown(factory.get("2"));
		assertValues(1, 3, 2, 4);
	}

	private void assertValues(int... vals) {
		addSomeTypes();
		Type[] actualtypes = factory.values();
		for (int i = 0; i < vals.length; i++) {
			assertEquals("" + vals[i], actualtypes[i].toString());
		}
	}

	private void addSomeTypes() {
		factory.get("1");
		factory.get("2");
		factory.get("3");
		factory.get("4");
	}
}
