/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import net.sf.jannot.Global;
import net.sf.jannot.bigwig.BigWigDataSource;
import net.sf.jannot.source.cache.CachedURLSource;
import net.sf.jannot.source.cache.SourceCache;
import net.sf.jannot.tabix.IndexedFeatureFile;
import net.sf.jannot.tdf.TDFDataSource;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class DataSourceFactory {
	public boolean disableURLCaching = true;
	private final SourceCache cache;

	/**
	 * 
	 * @param cache          the {@link SourceCache}
	 * @param disableCaching false iff the SourceCache should be used
	 */
	public DataSourceFactory(SourceCache cache, boolean disableCaching) {
		this.cache = cache;
		this.disableURLCaching = disableCaching;
	}

	public enum Sources {
		LOCALFILE, URL;// , DAS;

		@Override
		public String toString() {
			switch (this) {
			case URL:
				return "URL";
			case LOCALFILE:
				return "Local file";
			// case DIRECTORY:
			// return "Directory";
			// case DAS:
			// return "DAS server";
			}
			return null;
		}
	}

	public DataSource create(Locator locator, Global log)
			throws URISyntaxException, IOException {
		return create(locator, null, log);

	}

	/**
	 * 
	 * @param data
	 * @param index
	 * @param global the {@link Global} data
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ReadFailedException
	 */
	public DataSource create(Locator data, Locator index, Global global)
			throws URISyntaxException, IOException {
		global.getLog().log(Level.INFO, "Data: " + data);
		global.getLog().log(Level.INFO, "Index: " + index);
		if (data.isURL()) {
			new SSL(global.getLog()).certify(data.url());
		}

		if (data.isTDF()) {
			return new TDFDataSource(data, global);
		}

		if (data.isBigWig()) {
			return new BigWigDataSource(data, global);
		}

		if (index == null) {
			global.getLog().log(Level.INFO, "Could not find index");
			if (data.isURL()) {
				if (disableURLCaching) {
					global.getLog().log(Level.INFO,
							"Loading as regular URLSource");
					return new URLSource(data.url(), global);
				} else {
					global.getLog().log(Level.INFO,
							"Loading as CachedURLSource");
					return new CachedURLSource(data.url(), global, cache);
				}
			} else {
				global.getLog().log(Level.INFO, "Loading as FileSource");
				return new FileSource(data.file(), global);
			}
		} else {

			if (data.isBAM()) {
				return new SAMDataSource(data, index, global);
			}
			if (data.isFasta()) {
				return new IndexedFastaDataSource(data, index, global);
			}

			if (data.isTabix()) {
				return new IndexedFeatureFile(data, index, global);
			}

			if (data.isMaf()) {
				return new IndexedMAFDataSource(data, index, global);
			}
		}
		global.getLog().log(Level.SEVERE,
				"Could not construct data source for \n\t" + data + "\n\t"
						+ index);
		return null;

	}

	/**
	 * 
	 * @return the {@link SourceCache}. Needed to change cache directory if user
	 *         wants to change it.
	 */
	public SourceCache getCache() {
		return cache;
	}

	/**
	 * 
	 * @param disable true iff caching disabled. Needed to allow user to change
	 *                caching on/off
	 * 
	 */
	public void setDisableCaching(boolean disable) {
		this.disableURLCaching = disable;
	}

}
