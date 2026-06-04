/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.parser.Parser;
import net.sf.jannot.parser.ParserFactory;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class URLSource extends ParserDataSource {

	protected final URL url;
	private final long cachedSize;
	private final Parser parser;

	/**
	 * @param url
	 * @param global the {@link Reporter} to log to
	 * @param stream an inputstream to the url. Normally url.openStream() or new
	 *               PushbackInputStream(url.openStream(), 16 * 1024)
	 * @throws IOException
	 */
	public URLSource(URL url, Global global) throws IOException {
		super(new Locator(url.toString(), global.getLog()), global);
		this.url = Objects.requireNonNull(url);
		new SSL(global.getLog()).certify(url);
		cachedSize = url.openConnection().getContentLengthLong();
		parser = ParserFactory.create(url.openStream(), url, getGlobal());

		// use temp stream for determining the parser type,
		// not touching the original stream because we can't reset it.
// read first few kb using the stream. to determine parser type
//		byte[] buffer = new byte[16 * 1024];
//		int i = stream.read(buffer);
//		super.setParser(ParserFactory
//				.create(new ByteArrayInputStream(buffer, 0, i), url, global));
// reset stream
//		((PushbackInputStream) getIos()).unread(buffer, 0, i);
	}

//	private InputStream getStream(URL url2) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public URL getURL() {
		return url;
	}

	@Override
	public String toString() {
		return url.toString();
	}

	@Override
	public boolean isIndexed() {
		return false;
	}

	@Override
	public long size() throws IOException {
		return cachedSize;
	}

	@Override
	public Parser getParser() {
		return parser;
	}

	@Override
	public EntrySet read(EntrySet set) {
		try {
			set = parser.parse(url.openStream(), set);
		} catch (IOException e) {
			getLog().log(Level.WARNING, "Failed to parse", e);
		}
		return set;
	}

}
