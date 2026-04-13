/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import net.sf.jannot.bigwig.BigWigDataSource;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.cache.CachedURLSource;
import net.sf.jannot.tabix.IndexedFeatureFile;
import net.sf.jannot.tdf.TDFDataSource;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class DataSourceFactory {
	// private static Logger log =
	// Logger.getLogger(DataSourceFactory.class.getCanonicalName());

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

	public static boolean disableURLCaching = true;

	public static DataSource create(Locator locator, Reporter log)
			throws URISyntaxException, IOException, ReadFailedException {
		return create(locator, null, log);

	}

	/**
	 * 
	 * @param data
	 * @param index
	 * @param log   the logger to be used for the datasource; also used to
	 *              report issues creating the datasource
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ReadFailedException
	 */
	public static DataSource create(Locator data, Locator index, Reporter log)
			throws URISyntaxException, IOException, ReadFailedException {
		log.log(Level.INFO, "Data: " + data);
		log.log(Level.INFO, "Index: " + index);
		if (data.isURL()) {
			SSL.certify(data.url());
		}

		if (data.isTDF()) {
			return new TDFDataSource(data, log);
		}

		if (data.isBigWig())
			return new BigWigDataSource(data, log);

		if (index == null) {
			log.log(Level.INFO, "Could not find index");
			if (data.isURL()) {
				if (disableURLCaching) {
					log.log(Level.INFO, "Loading as regular URLSource");
					return new URLSource(data.url(), log);
				} else {
					log.log(Level.INFO, "Loading as CachedURLSource");
					return new CachedURLSource(data.url());
				}
			} else {
				log.log(Level.INFO, "Loading as FileSource");
				return new FileSource(data.file(), log);
			}
		} else {

			if (data.isBAM()) {
				return new SAMDataSource(data, index, log);
			}
			if (data.isFasta())
				return new IndexedFastaDataSource(data, index, log);

			if (data.isTabix())
				return new IndexedFeatureFile(data, index, log);

			if (data.isMaf())
				return new IndexedMAFDataSource(data, index, log);
		}
		log.log(Level.SEVERE, "Could not construct data source for \n\t" + data
				+ "\n\t" + index);
		return null;

	}

}
