/**
 * %HEADER%
 */
package net.sf.jannot.picard;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.jannot.Cleaner;

/**
 * @author Thomas Abeel
 * 
 */
public class BinaryBlob {
	/* Files are each 32 Mb */
	private static final int BLOCKSIZE = 32 * 1024 * 1024;

	private File[] bufferFiles;
	private RandomAccessFile[] rafs;

	public BinaryBlob(long size) throws IOException {
		bufferFiles = new File[1 + (int) (size / (BLOCKSIZE))];
		rafs = new RandomAccessFile[bufferFiles.length];

		for (int i = 0; i < bufferFiles.length; i++) {
			bufferFiles[i] = File.createTempFile("GenomeView.binaryblob",
					".tmp." + i);
			bufferFiles[i].deleteOnExit();
			rafs[i] = new RandomAccessFile(bufferFiles[i], "rwd");
			Cleaner.register(rafs[i], bufferFiles[i]);
		}

	}

	/**
	 * @param pos the position to read
	 * @return float at given pos
	 * @throws IOException if seek or read fails
	 */
	public synchronized float getFloat(int pos) throws IOException {
		int fIndex = (int) (pos / BLOCKSIZE);
		long fPos = pos % BLOCKSIZE;
		if (fPos >= rafs[fIndex].length())
			return 0;

		rafs[fIndex].seek(fPos);
		return rafs[fIndex].readFloat();
	}

}
