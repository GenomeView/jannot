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
import net.sf.jannot.parser.ParserFactory;
import net.sf.jannot.source.SSL;
import net.sf.jannot.source.URLSource;

public class CachedURLSource extends URLSource {

	private final SourceCache cache;

	/**
	 * 
	 * @param url    the URL that we are pointing at and possibly caching
	 * @param global the {@link Global} vars
	 * @param cache  the {@link SourceCache}
	 * @throws IOException
	 */
	public CachedURLSource(URL url, Global global, SourceCache cache)
			throws IOException {
		super(url, global);
		if (cache == null) {
			cache = new SourceCache(new File(System.getProperty("user.home")));
		}
		this.cache = cache;
	}

	@Override
	public EntrySet read(EntrySet set) {
		if (!cache.contains(url)) {
			new SSL(getLog()).certify(url);
			try {
				super.setParser(ParserFactory.create(url.openStream(), url,
						getGlobal()));
				final PipedInputStream in = new PipedInputStream();
				final PipedOutputStream forParser = new PipedOutputStream(in);

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

				super.setIos(in);
			} catch (IOException e) {
				getLog().log(Level.SEVERE, "Failed to read data from " + url,
						e);
				return set;

			}
			return super.read(set);
		} else {
			try {
				return cache.get(url, getGlobal()).read(set);
			} catch (IOException e) {
				// FIXME this shouldn't happen
				getLog().log(Level.SEVERE, "failed to read " + url, e);
				return set;
			}
		}
	}

}
