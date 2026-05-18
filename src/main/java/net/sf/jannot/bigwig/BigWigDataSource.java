/**
 * %HEADER%
 */
package net.sf.jannot.bigwig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.broad.igv.bbfile.BBFileReader;

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
 * Data source for BigWig files.
 * 
 * @author Thomas Abeel
 * 
 */
public class BigWigDataSource extends DataSource {

	private final SeekableStream s;
	private final BBFileReader tr;

	/**
	 * @param file
	 * @param log  the {@link Reporter} to log issues to
	 * @throws URISyntaxException
	 * @throws ReadFailedException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public BigWigDataSource(Locator l, Global global)
			throws ReadFailedException, URISyntaxException,
			MalformedURLException, IOException {
		super(l, global);
		if (!l.isURL()) {
			s = new SeekableFileStream(l.file());
		} else {
			s = new SeekableFileCachedHTTPStream(l.url());
		}
		tr = new BBFileReader(l, s);

	}

	@Override
	public EntrySet read(EntrySet set) {

		if (set == null) {
			set = new EntrySet(global);
		}

		Set<String> chrs = new HashSet<String>();

		for (String s : tr.getChromosomeNames()) {
			chrs.add(s);
			Entry e = set.getOrCreateEntry(s);
			e.add(new StringKey(tr.getLocator().toString()),
					new BigWigData(s, tr, getGlobal()));
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
