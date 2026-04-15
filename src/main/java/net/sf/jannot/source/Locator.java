/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import be.abeel.net.URIFactory;
import htsjdk.samtools.seekablestream.SeekableStream;
import htsjdk.samtools.seekablestream.SeekableStreamFactory;
import net.sf.jannot.Data;
import net.sf.jannot.tabix.TabixWriter;
import net.sf.jannot.tabix.TabixWriter.Conf;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * {@link Data} pointer. Can either be local or remote on a server. Can be
 * indexed or plain. This locator can describe the type of data that is in the
 * file.
 * 
 * @author Thomas Abeel
 * 
 */
public class Locator {
//	private static Logger log = Logger
//			.getLogger(Locator.class.getCanonicalName());
	private String locator;
	private long length = -1;
	private boolean exists = false; // exists and can be read
	private boolean streamCompressed = false;
	private boolean blockCompressed = false;
	private String ext;
	private long lastModified = -1;
	private final Reporter log;

	public Locator(File f, Reporter log) {
		this(f.toString(), log);
	}

	/**
	 * 
	 * @param l   the link to the file or URL. Files may start with file://
	 *            http:// or https: If neither, it is assumed to be a file.
	 *            Leading and trailing whitespaces are removed, so it is not
	 *            possible to use filenames starting or ending with whitespace.
	 * @param log a Reporter to log problems with the URL. Locators never throw
	 *            but they report issues accessing the URL.
	 */
	public Locator(String l, Reporter log) {
		this.log = log;
		if (l.startsWith("file://")) {
			l = l.substring(7);
		}
		this.locator = l.trim();
		init();
	}

	@Override
	public String toString() {
		return locator;
	}

	/**
	 * Removes the index extension from the file name
	 */
	public void stripIndex() {
		if (locator.endsWith(".mfi") || locator.endsWith(".tbi")
				|| locator.endsWith(".fai")) {
			locator = locator.substring(0, locator.length() - 4);
			init();
		}
		if (locator.endsWith(".bai")) {
			locator = locator.substring(0, locator.length() - 4);
			if (!locator.endsWith(".bam"))
				locator += ".bam";
			init();
		}

	}

	/**
	 * 
	 */
	private void init() {
		String[] arr = locator.toString().toLowerCase().split("\\.");
		initExt(arr);

		if (isURL()) {
			initURL();
		} else
			initFile();

	}

	public boolean isStreamCompressed() {
		return streamCompressed;
	}

	public boolean isBlockCompressed() {
		return blockCompressed;
	}

	/**
	 * @param arr
	 */
	private void initExt(String[] arr) {
		streamCompressed = false;
		blockCompressed = false;
		ext = arr[arr.length - 1];
		if (arr[arr.length - 1].equals("bgz")) {
			ext = arr[arr.length - 2];
			blockCompressed = true;
		}
		if (arr[arr.length - 1].equals("gz")) {
			ext = arr[arr.length - 2];
			streamCompressed = true;
		}

	}

	/**
	 * 
	 */
	private void initURL() {
		try {
			// log.fine("Checking: " + locator);
			URLConnection conn = URIFactory.url(locator).openConnection();
			conn.setUseCaches(false);
			// log.info(conn.getHeaderFields().toString());

			// #3 URLConnection doesn't parse the response code
			// so we have to do it here...
			String header = conn.getHeaderField(null);
			// we expect "HTTP/1.1 432 stringmessage"
			if (!header.matches("HTTP/.*\\s\\d\\d\\d\\s.*")) {
				log.log(Level.WARNING, "Unexpected server response from "
						+ locator + ":" + header);
				return;
			}
			// received expected response
			Integer responseCode = Integer.valueOf(header.split(" ")[1]);
			if (responseCode >= 400) {
				log.log(Level.WARNING,
						"Server eror: " + header + ". Can't read " + locator);
				return;
			}

			int len = conn.getContentLength();

			if (len < 0) {
				// happens eg with https://tudelft.nl
				log.log(Level.WARNING,
						"Server eror: file has negative size: " + locator);
				return;
			}

			// len >= 0
			byte[] buffer = new byte[50];
			conn.getInputStream().read(buffer);
			if (new String(buffer).trim().startsWith("<!DOCTYPE")) {
				/*
				 * This is not supposed to happen, except with badly configured
				 * CMS that take over
				 */
				return;
			}

			exists = true;
			length = len;
			lastModified = conn.getLastModified();
		} catch (Exception ioe) {
			log.log(Level.WARNING, "Failed to open " + locator, ioe);
		}

	}

	private void initFile() {
		File tmp = new File(locator);
		exists = tmp.exists() && tmp.canRead();
		if (exists) {
			length = tmp.length();
			lastModified = tmp.lastModified();
		}

	}

	/**
	 * @return the file extension tbi,fasta,bai,mfi
	 * @throws IllegalStateException if we don't have proper postfix for this
	 */
	public String getPostfix() {

		if (isTabix())
			return "tbi";
		if (isFasta())
			return "fai";
		if (isBAM())
			return "bai";
		if (isMaf())
			return "mfi";
		throw new IllegalStateException("no known postfix for this");
	}

	public boolean isURL() {
		return locator.startsWith("http://") || locator.startsWith("https://");
	}

	/**
	 * 
	 * @return available number of chars in file, -1 if file can not be opened
	 */
	public long length() {
		return length;
	}

	/**
	 * @return true iff the file exists and we can read it. So files without
	 *         read permission, access denied errors, etc will return false.
	 */
	public boolean exists() {
		return exists;
	}

	/**
	 * @return
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public URL url() throws MalformedURLException, URISyntaxException {
		return URIFactory.url(locator);

	}

	public File file() {
		return new File(locator);
	}

	/**
	 * @return
	 */
	public boolean isWebservice() {

		return locator.indexOf('&') >= 0 || locator.indexOf('?') >= 0;

	}

	/**
	 * @return
	 */
	public boolean isTDF() {

		return ext.equals("tdf");
	}

	public boolean isWig() {
		return ext.equals("wig");
	}

	public boolean isBigWig() {
		return ext.equals("bw") || ext.equals("bigwig");
	}

	public boolean isTabix() {
		return ext.equals("vcf") || ext.equals("gff") || ext.equals("gff3")
				|| ext.equals("bed") || ext.equals("tsv")
				|| ext.equals("pileup") || ext.equals("swig")
				|| ext.equals("tab");

	}

	public boolean requiresIndex() {
		return (isMaf() && isBlockCompressed()) || isBAM() || ext.equals("tsv")
				|| ext.equals("pileup") || ext.equals("swig")
				|| ext.equals("tab");
	}

	public boolean recommendedIndex() {
		return requiresIndex() || isFasta() || isMaf();
	}

	public boolean supportsIndex() {
		return recommendedIndex() || requiresIndex() || isVCF()
				|| ext.equals("gff") || ext.equals("gff3") || ext.equals("bed");
	}

	public boolean isBAM() {
		return ext.equals("bam");

	}

	public boolean isFasta() {
		return ext.equals("fasta") || ext.equals("fa") || ext.equals("fas")
				|| ext.equals("con") || ext.equals("fna") || ext.equals("tfa");
	}

	public boolean isMaf() {
		return ext.equals("maf");

	}

	public boolean isVCF() {
		return ext.equals("vcf");

	}

	public boolean isPileup() {
		return ext.equals("pileup");
	}

	public long lastModified() {
		return lastModified;
	}

	/**
	 * @return
	 */
	public Conf getTabixConfiguration() {
		if (!isTabix())
			return null;
		if (ext.equals("gff") || ext.equals("gff3")) {
			return TabixWriter.GFF_CONF;
		}
		if (ext.equals("bed")) {
			return TabixWriter.BED_CONF;
		}

		if (ext.equals("vcf")) {
			return TabixWriter.VCF_CONF;
		}
		Conf out = new Conf(0, 0, 0, 0, '#', 0);

		if (ext.equals("pileup") || ext.equals("swig") || ext.equals("tab")
				|| ext.equals("tsv")) {
			out.chrColumn = 1;
			out.startColumn = 2;
			out.endColumn = 2;
		}

		return out;

	}

	/**
	 * @return {@link SeekableStream}
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public SeekableStream stream() throws IOException, URISyntaxException {
		if (!exists)
			throw new IOException("File can't be read: " + locator);
		if (!isURL())
			return SeekableStreamFactory.getInstance()
					.getStreamFor(this.file().toString());
		return SeekableStreamFactory.getInstance().getStreamFor(this.url());
	}

	public boolean isAnyCompressed() {
		return streamCompressed || blockCompressed;
	}

	public String getName() throws MalformedURLException, URISyntaxException {
		if (isURL()) {
			int slashIndex = url().getPath().lastIndexOf('/');
			return url().getPath().substring(slashIndex + 1);

		} else
			return file().getName();

	}

}
