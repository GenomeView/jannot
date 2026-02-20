package net.sf.jannot.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import be.abeel.io.LineIterator;
import htsjdk.samtools.seekablestream.SeekableStream;

public class TestLocator {
	private static final String EMPTYURL = "https://raw.githubusercontent.com/GenomeView/jannot/refs/heads/main/src/test/resources/empty";
	private static final String NONEXISTINGURL = "http:nothing";
	private static final String HG19_ACCESS_DENIED = "http://bioinformatics.psb.ugent.be/downloads/genomeview/genomes/hg19/genome.fasta";
	private static final String TUDELFT = "https://www.tudelft.nl";
	private static final String TINYVCF = "https://raw.githubusercontent.com/GenomeView/jannot/refs/heads/main/src/test/resources/tiny.vcf";
	private static final String SRC_TEST_RESOURCES_JUNIT_TXT = "src/test/resources/junit.txt";
	private static final String TEST_RESOURCES_JUNIT_TXT = "/"
			+ SRC_TEST_RESOURCES_JUNIT_TXT;
	private static final String JUNIT_TXT = "file://"
			+ SRC_TEST_RESOURCES_JUNIT_TXT;

	@Test
	public void smoke() {
		Locator l = new Locator(SRC_TEST_RESOURCES_JUNIT_TXT);
	}

	@Test
	public void testPlainFile() throws IOException, URISyntaxException {
		Locator l = new Locator(SRC_TEST_RESOURCES_JUNIT_TXT);
		assertTrue(l.exists());
		SeekableStream stream = l.stream();
		assertEquals(45, stream.available());
		assertEquals("This is a ",
				new String(stream.readNBytes(10), StandardCharsets.UTF_8));
		for (String line : new LineIterator(l.stream())) {
			System.out.println(line);
		}

		stream.close();
	}

	@Test
	public void testURLLocalFile() throws URISyntaxException, IOException {

		Locator l = new Locator(JUNIT_TXT);
		assertEquals(45, l.length());
		for (String line : new LineIterator(l.stream())) {
			System.out.println(line);
		}

	}

	@Test
	public void testLocalAbsoluteFile() throws URISyntaxException, IOException {
		String path = new java.io.File(".").getCanonicalPath()
				+ TEST_RESOURCES_JUNIT_TXT;
		Locator l = new Locator(path);
		for (String line : new LineIterator(l.stream())) {
			System.out.println(line);
		}

	}

	@Test
	public void testURLLocalAbsoluteFile()
			throws URISyntaxException, IOException {
		String path = "file://" + new java.io.File(".").getCanonicalPath()
				+ TEST_RESOURCES_JUNIT_TXT;
		Locator l = new Locator(path);
		for (String line : new LineIterator(l.stream())) {
			System.out.println(line);
		}

	}

	@Test
	public void testURL() throws URISyntaxException, IOException {
		Locator l = new Locator(TINYVCF);
		SeekableStream s = l.stream();
		assertEquals(1770, s.available());
		for (String line : new LineIterator(s)) {
			System.out.println(line);
		}

	}

	@Test
	public void testFileWithoutReadPermissions() throws IOException {
		File f = File.createTempFile("writeonly", ".txt");
		PrintWriter writer = new PrintWriter(f);
		writer.println("The first line");
		writer.println("The second line");
		writer.close();
		f.setReadable(false);

		Locator l = new Locator(f);
		assertFalse(l.exists());
	}

	@Test(expected = IOException.class)
	public void testDoesntExist() throws IOException, URISyntaxException {
		Locator l = new Locator(NONEXISTINGURL);
		assertFalse(l.exists());
		assertEquals(-1, l.length());
		l.stream(); // should throw
	}

	@Test(expected = IOException.class)
	public void testAccessDenied() throws IOException, URISyntaxException {
		// access denined
		Locator l = new Locator(HG19_ACCESS_DENIED);
		l.stream(); // should throw
	}

	@Test
	public void testOpenURL() throws IOException, URISyntaxException {
		// This one's interesting, exists but reports length -1
		Locator l = new Locator(TUDELFT);
		assertFalse(l.exists());
	}

	@Test
	public void testOpenEmptyURL() throws IOException, URISyntaxException {
		// open URL that has empty content.
		Locator l = new Locator(EMPTYURL);
		assertTrue(l.exists());
		assertEquals(0, l.length());
	}

}