/**
 * %HEADER%
 */
package net.sf.jannot.source.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.logging.Level;

import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.parser.Parser;
import net.sf.jannot.parser.ParserFactory;
import net.sf.jannot.source.AbstractStreamDataSource;
import net.sf.jannot.source.Locator;
import net.sf.jannot.source.SSL;
import net.sf.jannot.source.URLSource;

/**
 * previously extended {@link URLSource} but too problematic #100
 */
public class CachedURLSource extends AbstractStreamDataSource {

	private final URL url;
	private final SourceCache cache;

	// cached values.
	private final Parser parser;
	private final int cachedSize;

	/**
	 * 
	 * @param url    the URL that we are pointing at and possibly caching. The
	 *               URL is sertified before use.
	 * @param global the {@link Global} vars
	 * @param cache  the {@link SourceCache}. if null, users home dir is used
	 * @throws IOException
	 */
	public CachedURLSource(URL url, Global global, SourceCache cache)
			throws IOException {
		super(new Locator(url.toString(), global.getLog()), global);
		this.url = url;
		new SSL(getLog()).certify(url);
		if (cache == null) {
			cache = new SourceCache(new File(System.getProperty("user.home")));
		}
		this.cache = cache;
		parser = ParserFactory.create(url.openStream(), url, getGlobal());
		cachedSize = url.openConnection().getContentLength();

	}

	@Override
	public EntrySet read(EntrySet set) {
		if (!cache.contains(url)) {
			try {
				final PipedInputStream in = new PipedInputStream();
				final PipedOutputStream forParser = new PipedOutputStream(in);

				// weird construct to force the stream to go through cache...
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							OutputStream out = cache.startCaching(url);
							InputStream is = url.openStream();
							byte[] buffer = new byte[100000];
							while (true) {

								int amountRead = is.read(buffer);
								if (amountRead == -1) {
									break;
								}
								forParser.write(buffer, 0, amountRead);
								out.write(buffer, 0, amountRead);

							}
							forParser.close();
							out.close();
							cache.finish(url);
						} catch (IOException e) {
							getLog().log(Level.WARNING,
									"failed caching url " + url, e);
						}

					}
				}).start();

				set = parser.parse(in, set);

			} catch (IOException e) {
				getLog().log(Level.SEVERE, "Failed to read data from " + url,
						e);
			}
		} else {
			try {
				return cache.get(url, getGlobal()).read(set);
			} catch (IOException e) {
				// FIXME this shouldn't happen
				getLog().log(Level.SEVERE, "failed to read " + url, e);
			}
		}
		return set;
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

}
