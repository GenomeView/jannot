/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import atk.util.MD5Tools;
import htsjdk.samtools.BAMIndexer;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamInputResource;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import net.sf.jannot.indexing.Faidx;
import net.sf.jannot.mafix.MafixFactory;
import net.sf.jannot.tabix.TabixWriter;
import net.sf.jannot.tabix.TabixWriter.Conf;
import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * 
 */
public class IndexManager {
	// FIXME another singleton class with only static members

//	private static Logger log = Logger
//			.getLogger(IndexManager.class.getCanonicalName());
	public static File cacheDir = new File(
			System.getProperty("user.home") + "/.genomeview/index");

	public static Locator getIndex(Locator locator, Reporter log) {
		String postfix = locator.getPostfix();

		Locator out = findLocalIndex(locator, log);
		if (out == null) {
			out = findRemoteIndex(locator, log);
		}

		return out;

	}

	/**
	 * @param locator
	 * @param postfix
	 * @return
	 */
	private static Locator findRemoteIndex(Locator locator, Reporter log) {
		log.log(Level.INFO, "Trying to find remote index");
		Locator index = new Locator(locator + "." + locator.getPostfix(), log);

		if (!index.exists() || index.length() == 0)
			index = null;

		/* Special case handling of bam files */
		if (index == null) {
			index = new Locator(locator.toString().substring(0,
					locator.toString().length() - 4) + "."
					+ locator.getPostfix(), log);
			if (!index.exists() || index.length() == 0)
				index = null;
		}
		return index;
	}

	/**
	 * @param locator
	 * @param postfix
	 * @return
	 */
	private static Locator findLocalIndex(Locator locator, Reporter log) {
		if (!cacheDir.exists())
			cacheDir.mkdir();
		Locator idx = cacheIndex(locator, log);
		if (!idx.exists() || idx.length() == 0)
			idx = null;
		return idx;
	}

	private static Locator cacheIndex(Locator locator, Reporter log) {
		return new Locator(cacheDir + "/" + MD5Tools.md5(locator.toString())
				+ "." + locator.getPostfix(), log);
	}

	/**
	 * A potentially long-running method that will create an index for the
	 * provided locator
	 * 
	 * @param locator
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static boolean createIndex(Locator locator, Reporter log)
			throws MalformedURLException, IOException, URISyntaxException {
		log.log(Level.INFO, "Creating index for " + locator);
		if (!cacheDir.exists())
			cacheDir.mkdir();

		Locator idx = cacheIndex(locator, log);

		if (!idx.isURL()) {
			Locator tmp = new Locator(
					locator.toString() + "." + locator.getPostfix(), log);
			File f = tmp.file();
			try {
				f.createNewFile();
			} catch (IOException e) {
				log.log(Level.WARNING, "failed to make index file" + e);
			}
			if (f.exists() && f.canWrite())
				idx = tmp;

		}

		if (idx.exists())
			log.log(Level.INFO, "Index already exists and will be overwritten");

		if (locator.isMaf()) {
			MafixFactory.generateIndex(locator.stream(), idx.file());

		}

		if (locator.isBAM()) {

			InputStream ios = locator.stream();
			SamReader sfr = SamReaderFactory.makeDefault()
					.open(SamInputResource.of(ios));
			// sfr.enableFileSource(true); // CHECK do we still need this?

			SAMFileHeader head = sfr.getFileHeader();

			File tmpOutput = new File(idx + ".tmp");
			BAMIndexer bix = new BAMIndexer(tmpOutput, head);

			SAMRecordIterator sir = sfr.iterator();
			while (sir.hasNext()) {
				SAMRecord n = sir.next();
				bix.processAlignment(n);

			}
			bix.finish();
			idx.file().delete();
			boolean rename = tmpOutput.renameTo(idx.file());
			log.log(Level.INFO, "Rename action ok: " + rename);
			return rename;
//			return true;
			// public BAMIndexer(final File output, SAMFileHeader fileHeader) {
			//
			// numReferences = fileHeader.getSequenceDictionary().size();
			// indexBuilder = new BAMIndexBuilder(fileHeader);
			// outputWriter = new BinaryBAMIndexWriter(numReferences, output);
			// }

		}

		if (locator.isTabix()) {
			Conf c = locator.getTabixConfiguration();
			try {
				TabixWriter tw = new TabixWriter(locator, c);

				tw.createIndex(idx);
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (locator.isFasta()) {

			Faidx.index(locator, idx);
			return true;
		}

		return false;

	}

	/**
	 * @param data
	 * @return
	 */
	public static boolean canBuildIndex(Locator data) {
		boolean tbx = data.isTabix() && data.isBlockCompressed();
		return data.isBAM() || tbx
				|| (data.isFasta() && !data.isAnyCompressed())
				|| (data.isMaf() && data.isBlockCompressed());
	}

}
