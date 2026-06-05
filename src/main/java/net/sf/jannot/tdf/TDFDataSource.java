/**
 * %HEADER%
 */
package net.sf.jannot.tdf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.broad.igv.tdf.TDFReader;

import htsjdk.samtools.seekablestream.SeekableFileStream;
import htsjdk.samtools.seekablestream.SeekableStream;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.StringKey;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.picard.SeekableFileCachedHTTPStream;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.Locator;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * Data source for TDF files.
 * 
 * @author Thomas Abeel
 * 
 */
public class TDFDataSource extends DataSource {

	private SeekableStream s = null;

	/**
	 * @param file
	 * @param global the {@link Reporter} to log issues to.
	 * @throws URISyntaxException
	 * @throws ReadFailedException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public TDFDataSource(Locator l, Global global)
			throws URISyntaxException, MalformedURLException, IOException {
		super(l, global);
		if (!l.isURL()) {
			s = new SeekableFileStream(l.file());
		} else {
			s = new SeekableFileCachedHTTPStream(l.url());
		}
		tr = TDFReader.getReader(s);

	}

	private TDFReader tr = null;

	@Override
	public EntrySet read(EntrySet set) {

		if (set == null) {
			set = new EntrySet(getGlobal());
		}

		Set<String> chrs = new HashSet<String>();

		for (String s : tr.getDatasetNames()) {
			String[] arr = s.split("/");
			chrs.add(arr[1]);
		}
		chrs.remove("All");
		for (String chr : chrs) {
			Entry e = set.getOrCreateEntry(chr);
			e.add(new StringKey(tr.getLocator()),
					new TDFData(chr, tr, getGlobal()));

		}

		return set;
	}

	@Override
	public boolean isIndexed() {
		return true;
	}

	@Override
	public long size() {
		return s.length();
	}
}
