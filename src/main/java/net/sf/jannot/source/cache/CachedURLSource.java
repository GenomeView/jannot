/**
 * %HEADER%
 */
package net.sf.jannot.source.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.logging.Level;

import net.sf.jannot.EntrySet;
import net.sf.jannot.parser.ParserFactory;
import net.sf.jannot.source.SSL;
import net.sf.jannot.source.URLSource;
import tudelft.utilities.logging.Reporter;

public class CachedURLSource extends URLSource {

	public CachedURLSource(URL url, Reporter log) throws IOException {
		super(url, log);

	}

	@Override
	public EntrySet read(EntrySet set) {
		if (!SourceCache.contains(url)) {
			new SSL(getLog()).certify(url);
			try {
				super.setParser(
						ParserFactory.create(url.openStream(), url, getLog()));
				final PipedInputStream in = new PipedInputStream();
				final PipedOutputStream forParser = new PipedOutputStream(in);

				new Thread(new Runnable() {
					public void run() {
						try {
							OutputStream out = SourceCache.startCaching(url);
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
							SourceCache.finish(url);
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
				return SourceCache.get(url, getLog()).read(set);
			} catch (IOException e) {
				// FIXME this shouldn't happen
				getLog().log(Level.SEVERE, "failed to read " + url, e);
				return set;
			}
		}
	}

}
