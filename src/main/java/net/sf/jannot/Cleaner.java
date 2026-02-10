/**
 * %HEADER%
 */
package net.sf.jannot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.seekablestream.SeekableStream;

/**
 * 
 * Registry for random access files that need to be closed.
 * 
 * See http://bugs.sun.com/view_bug.do?bug_id=6357433
 * 
 * for explanation why we need to to it the hard way.
 * 
 * @author Thomas Abeel
 * 
 */
public class Cleaner {

	private static ArrayList<RandomAccessFile> rafs = new ArrayList<RandomAccessFile>();
	private static ArrayList<File> files = new ArrayList<File>();
	private static ArrayList<SeekableStream> streams = new ArrayList<SeekableStream>();
	private static ArrayList<SamReader> sfrs = new ArrayList<SamReader>();

	public static void register(SamReader sfr, SeekableStream content, File f) {
		sfrs.add(sfr);
		if(f!=null)
			files.add(f);
		streams.add(content);
	}

	public static void register(RandomAccessFile raf, File f) {
		rafs.add(raf);
		files.add(f);
	}

	private static Logger log = Logger.getLogger(Cleaner.class.getCanonicalName());

	public static void exit() {

		for (SeekableStream s : streams) {
			try {

				s.close();
			} catch (Exception e) {
				log.log(Level.WARNING, "Failed to close SeekableStream "+s.getSource(), e);
			}
		}

		for (SamReader sfr : sfrs) {
			try {
				sfr.close();
			} catch (Exception e) {
				log.log(Level.WARNING, "Failed to close SAMFileReader "+sfr, e);
			}
		}

		for (RandomAccessFile raf : rafs) {
			try {
				raf.close();
			} catch (IOException e) {
				log.log(Level.WARNING, "Failed to close RandomAccessFile "+raf, e);
			}

		}
		System.gc();
		System.gc();
		for (File file : files) {

			if (file.delete()) {
				log.info("Successfully deleted: " + file);
			} else {
				log.info("Failed to delete: " + file);
			}

		}

	}

}
