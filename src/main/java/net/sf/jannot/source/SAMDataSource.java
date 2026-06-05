/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamInputResource;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.SamReaderFactory.Option;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.seekablestream.SeekableFileStream;
import htsjdk.samtools.seekablestream.SeekableStream;
import net.sf.jannot.Cleaner;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.picard.SeekableFileCachedHTTPStream;
import net.sf.jannot.shortread.BAMreads;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class SAMDataSource extends DataSource {

	private SeekableStream content;

	/* File containing the BAM index */
	private File index;
	/* Display name for this data */
	private DataKey sourceKey;

	private SamReader sfr = null;
	private long size;
	private boolean deleteIndex = false;

	/**
	 * @param data
	 * @param index2
	 * @param global the {@link Reporter} to log issues to
	 */
	public SAMDataSource(Locator data, Locator index, Global global) {
		super(data, global);
		if (data == null || index == null) {
			throw new NullPointerException(
					"Neither data nor index provided: " + data + "; " + index);
		}
		try {
			if (data.isURL()) {
				if (index.isURL()) {
					init(data.url(), index.url());
				} else {
					init(data.url(), index.file());
				}
			} else {
				init(data.file(), index.file());
			}
		} catch (IOException | URISyntaxException e) {
			global.getLog().log(Level.WARNING, "failed to init SAMDataSource",
					e);
		}
	}

	/**
	 * @return a {@link SamReader} of the data source, set at index and with
	 *         {@link ValidationStringency#SILENT} and
	 *         {@link Option#EAGERLY_DECODE}
	 */
	public SamReader getReader() {
		// System.out.println(content);
		// System.out.println(content.getSource());
		if (sfr == null) {
			// FIXME we should not change the default strategy.
			SamReaderFactory.setDefaultValidationStringency(
					ValidationStringency.SILENT);
			getLog().log(Level.FINE, "SDS: " + content + "\t" + index);
			sfr = SamReaderFactory.makeDefault().enable(Option.EAGERLY_DECODE)
					.validationStringency(ValidationStringency.SILENT)
					.open(SamInputResource.of(content).index(index));
			Cleaner.register(sfr, content, deleteIndex ? index : null);

		}
		return sfr;
		// return new SAMFileReader(content, index, false);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SAMDataSource other = (SAMDataSource) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		if (index == null) {
			if (other.index != null) {
				return false;
			}
		} else if (!index.equals(other.index)) {
			return false;
		}
		return true;
	}

	@Override
	public EntrySet read(EntrySet set) {
		if (set == null) {
			set = new EntrySet(getGlobal());
		}
		SamReader inputSam = getReader();

		SAMSequenceDictionary tmpDic = inputSam.getFileHeader()
				.getSequenceDictionary();
		for (int i = 0; i < tmpDic.size(); i++) {
			SAMSequenceRecord org = inputSam.getFileHeader().getSequence(i);
			Entry e = set.getOrCreateEntry(org.getSequenceName());
			e.add(getSourceKey(),
					new BAMreads(this, org.getSequenceName(), getGlobal()));

		}
		return set;
	}

	@Override
	public String toString() {
		return content.toString();
	}

	@Override
	public void finalize() {
		try {
			sfr.close();
		} catch (IOException e) {
			getLog().log(Level.WARNING, "failed to close", e);
		}
		if (content instanceof SeekableFileCachedHTTPStream) {
			((SeekableFileCachedHTTPStream) content).closeAll();
		}

	}

	private void setSourceKey(DataKey sourceKey) {
		this.sourceKey = sourceKey;
	}

	public DataKey getSourceKey() {
		return sourceKey;
	}

	@Override
	public boolean isIndexed() {
		return true;
	}

	@Override
	public long size() {
		return size;
	}

	/**
	 * @param url BAM file URL
	 * @throws IOException
	 * @throws ReadFailedException
	 * @throws URISyntaxException
	 */
	private void init(URL url, URL idx) throws IOException, URISyntaxException {
		setSourceKey(new SAMKey(url.toString()));
		/* BAM file */
		// content =new SeekableHTTPStream(url);
		content = new SeekableFileCachedHTTPStream(url);
		size = url.openConnection().getContentLength();

		/* Index file */
		File tmpBAI = File.createTempFile("urlbam", ".bai");
		tmpBAI.deleteOnExit();
//		url = URIFactory.url(idx);
		copy(idx.openStream(), tmpBAI);
		index = tmpBAI;
		deleteIndex = true;

	}

	/**
	 * 
	 * @param url BAM file
	 * @param idx index is local
	 * 
	 * @throws IOException
	 * @throws ReadFailedException
	 * @throws URISyntaxException
	 */
	private void init(URL url, File idx)
			throws IOException, URISyntaxException {
		setSourceKey(new SAMKey(url.toString()));
		/* BAM file */
		// content =new SeekableHTTPStream(url);
		content = new SeekableFileCachedHTTPStream(url);
		size = url.openConnection().getContentLength();

		index = idx;

	}

	private void copy(InputStream in, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);

		byte[] buffer = new byte[100000];
		while (true) {
			int amountRead = in.read(buffer);
			if (amountRead == -1) {
				break;
			}
			out.write(buffer, 0, amountRead);

		}
		out.close();

	}

	/**
	 * BAM file
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void init(File file, File index) throws IOException {
		setSourceKey(new SAMKey(file.toString()));
		size = file.length();
		content = new SeekableFileStream(file);
		this.index = index;
	}

}

class SAMKey implements DataKey {

	private String string;

	SAMKey(String file) {
		this.string = file;
	}

	@Override
	public String toString() {
		return this.string;
	}

	@Override
	public int compareTo(DataKey o) {
		return toString().compareTo(toString());
	}

	@Override
	public boolean equals(Object o) {
		return toString().equals(o.toString());
	}

}
