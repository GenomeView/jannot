package net.sf.jannot.source;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import htsjdk.samtools.seekablestream.SeekableStream;

public class TestLocator {

	@Test
	public void smoke() {
		Locator l = new Locator("src/test/resources/junit.txt");
	}

	@Test
	public void testPlainFile() throws IOException, URISyntaxException {
		Locator l = new Locator("src/test/resources/junit.txt");
		SeekableStream stream = l.stream();
		assertEquals(45, stream.available());
		assertEquals("This is a ",
				new String(stream.readNBytes(10), StandardCharsets.UTF_8));
		stream.close();
	}

	@Test(expected = IOError.class)
	public void testFileWithoutReadPermissions() throws IOException {
		File f = File.createTempFile("writeonly", ".txt");
		PrintWriter writer = new PrintWriter(f);
		writer.println("The first line");
		writer.println("The second line");
		writer.close();
		f.setReadable(false);

		Locator l = new Locator(f);
	}
}