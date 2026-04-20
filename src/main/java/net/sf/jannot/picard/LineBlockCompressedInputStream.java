/**
 * %HEADER%
 */
package net.sf.jannot.picard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import htsjdk.samtools.seekablestream.SeekableBufferedStream;
import htsjdk.samtools.seekablestream.SeekableStream;
import htsjdk.samtools.util.BlockCompressedInputStream;

/**
 * @author Thomas Abeel
 * 
 */
public class LineBlockCompressedInputStream extends BlockCompressedInputStream {

	private BufferedReader br = null;

	/**
	 * @param strm
	 */
	public LineBlockCompressedInputStream(SeekableStream strm) {
		super(new SeekableBufferedStream(strm));
		br = new BufferedReader(new InputStreamReader(this));
	}

	/**
	 * Reads a line from the inputstream until a \n or \n\r is encountered. The
	 * file pointer will be positioned at the beginning of the next line after
	 * this read.
	 * 
	 * @return a string with the read characters. Null when no more characters
	 *         are being read.
	 */
	public String readLine() throws IOException {
		return br.readLine();
	}

	@Override
	public void seek(long place) throws IOException {
		super.seek(place);
		br = new BufferedReader(new InputStreamReader(this));
	}

}
