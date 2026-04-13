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
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.picard.SeekableFileCachedHTTPStream;
import net.sf.jannot.refseq.FaidxData;
import net.sf.jannot.refseq.FaidxIndex;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class IndexedFastaDataSource extends DataSource {

	private final SeekableStream content;
	private final Locator index;
	private final Locator data;

	/**
	 * @param data  the data file
	 * @param index the index file that indexes the data
	 * @param log   the {@link Reporter} to log issues to
	 */
	public IndexedFastaDataSource(Locator data, Locator index, Reporter log)
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
				getLog().log(Level.WARNING, "failed to open stream to " + index,
						e1);
				// just proceed as original code did. Maybe we should return?
			}
		else
			try {
				iis = new FileInputStream(index.file());
			} catch (FileNotFoundException e1) {
				getLog().log(Level.WARNING, "file not found " + index, e1);
				// just proceed as original code did. Maybe we should return?
			}

		FaidxIndex index = new FaidxIndex(iis);
		// SAMSequenceDictionary tmpDic =
		// inputSam.getFileHeader().getSequenceDictionary();
		// for (int i = 0; i < tmpDic.size(); i++) {
		for (String name : index.names()) {
			Entry e = set.getOrCreateEntry(name);
			// try {
			try {
				e.setSequence(new FaidxData(index, content, name));
			} catch (Exception ex) {
				getLog().log(Level.SEVERE,
						"Faidx error, locator=" + data + " index=" + index, ex);
				return set; // abort immediately as original code
			}
		}
		return set;
	}

	@Override
	public String toString() {
		return content.toString();
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
