/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import htsjdk.samtools.seekablestream.SeekableFileStream;
import htsjdk.samtools.seekablestream.SeekableStream;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Type;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.mafix.IndexedMAF;
import net.sf.jannot.picard.SeekableFileCachedHTTPStream;
import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * @author Thomas Van Parys
 * 
 */
public class IndexedMAFDataSource extends DataSource {

	private SeekableStream content;
	private Locator index;
	private Locator data;

	/**
	 * @param data
	 * @param index
	 * @throws URISyntaxException
	 * @throws ReadFailedException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public IndexedMAFDataSource(Locator data, Locator index, Reporter log)
			throws MalformedURLException, IOException, ReadFailedException,
			URISyntaxException {
		super(data, log);
		if (data.isURL())
			content = new SeekableFileCachedHTTPStream(data.url());
		else
			content = new SeekableFileStream(data.file());
		this.index = index;
		this.data = data;
	}

	@Override
	public EntrySet read(EntrySet set) {
		if (content == null)
			throw new RuntimeException("Boenk!");
		if (set == null)
			set = new EntrySet();
		// SAMFileReader inputSam = getReader();

		InputStream iis = null;
		if (index.isURL())
			try {
				iis = index.url().openStream();
			} catch (IOException | URISyntaxException e1) {
				getLog().log(Level.WARNING, "failed to open " + index, e1);
				// and just continue as in original code (why?)
			}
		else
			try {
				iis = new FileInputStream(index.file());
			} catch (FileNotFoundException e1) {
				getLog().log(Level.WARNING, "file not found " + index, e1);
			}

		try {
			IndexedMAF maf = new IndexedMAF(content, iis, getLog());
			// System.out.println("Reading MAF: ");

			for (String name : maf.getNames()) {
				// System.out.println("Adding individual chroms: "+name);
				String[] nameParts = name.split("\\.");

				// the chromosome name is probably the last part
				// after the dot
				String chrom = nameParts[nameParts.length - 1];

				// if the chrom entry already exists, use that one
				// otherwise, use the full name we found in the MAF
				Entry e = null;
				if (set.getEntry(chrom) != null) {
					e = set.getOrCreateEntry(chrom);
				} else {
					e = set.getOrCreateEntry(name);
				}
				IndexedMAF idxMaf = new IndexedMAF(name, maf, getLog());
				// System.out.println("Adding MAF:
				// "+content+"\t"+idxMaf+"\t"+idxMaf.getClass());
				e.add(Type.get(data.toString()), idxMaf);
			}

		} catch (Exception ex) {
			getLog().log(Level.SEVERE, "Mafix error data: " + data + "\n\n"
					+ "Mafix error index: " + index, ex);
			return set;
		}
		return set;
	}

	@Override
	public void finalize() {
		// content.closeAll();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jannot.source.DataSource#isIndexed()
	 */
	@Override
	public boolean isIndexed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jannot.source.DataSource#size()
	 */
	@Override
	public long size() {
		return data.length();
	}

}
