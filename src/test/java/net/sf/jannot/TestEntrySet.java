package net.sf.jannot;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import net.sf.jannot.exception.ReadFailedException;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestEntrySet {

	private final Global global;

	public TestEntrySet() throws IOException, ReadFailedException {

		global = new Global();
	}

	@Test
	public void testGet() {
		EntrySet es = new EntrySet(global);
		Entry x = es.getOrCreateEntry("chr1");
		Entry y = es.getOrCreateEntry("2");
		assertNotNull(x);
		assertNotNull(y);
		System.out.println(es.getEntry("1"));
		assertNotNull(es.getEntry("1"));
		assertNotNull(es.getEntry("CHR1"));
		assertNotNull(es.getEntry("chr1"));

		System.out.println(es.getEntry("chr2"));
		assertNotNull(es.getEntry("chr2"));
		assertNotNull(es.getEntry("Chr2"));
		assertNotNull(es.getEntry("chR2"));

	}
}