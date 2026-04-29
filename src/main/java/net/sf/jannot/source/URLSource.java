/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.jannot.Global;
import net.sf.jannot.parser.ParserFactory;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class URLSource extends AbstractStreamDataSource {

	protected URL url;

	/*
	 * Only for internal use by subclasses. The extra object is only to
	 * distinguish constructors and is ignored
	 */
	protected URLSource(URL url, Object x, Global global) throws IOException {
		super(new Locator(url.toString(), global.getLog()), global);
		this.url = url;
	}

	private void init(Global global) throws MalformedURLException, IOException {
		PushbackInputStream pis = new PushbackInputStream(url.openStream(),
				16 * 1024);
		byte[] buffer = new byte[16 * 1024];
		int i = pis.read(buffer);
		super.setParser(ParserFactory
				.create(new ByteArrayInputStream(buffer, 0, i), url, global));
		pis.unread(buffer, 0, i);
		super.setIos(pis);

	}

	/**
	 * @param url
	 * @param global the {@link Reporter} to log to
	 * @throws IOException
	 */
	public URLSource(URL url, Global global) throws IOException {
		this(url, null, global);
		new SSL(global.getLog()).certify(url);
		init(global);

	}

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

	private long cachedSize = -2;

	@Override
	public long size() throws IOException {
		if (cachedSize == -2) {
			cachedSize = url.openConnection().getContentLength();
		}
		return cachedSize;
	}

}
