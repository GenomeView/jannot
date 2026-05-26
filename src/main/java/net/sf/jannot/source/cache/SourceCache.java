/**
 * %HEADER%
 */
package net.sf.jannot.source.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import atk.io.ExtensionFileFilter;
import atk.util.MD5Tools;
import net.sf.jannot.Global;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.FileSource;

/**
 * Utility class. Methods to store streams to be cached in user home directory ,
 * After calling {@link #startCaching(URL)} a file N.tmp is created where N is
 * the MD5 hash code of the URL. Then after calling {@link #finish(URL)} the
 * file is closed and renamed to N.url. To get a cached version of the URL, use
 * {@link #get(URL, Global)}
 */
public class SourceCache {
	private File cacheDir;

	/**
	 * 
	 * @param cacheDir a directory where downloaded URLs are cached. not null
	 */
	public SourceCache(File cacheDir) {
		this.cacheDir = Objects.requireNonNull(cacheDir);
	}

	/**
	 * bit nasty method to change the cache dir.
	 * 
	 * @param cacheDir a directory where downloaded URLs are cached. not null
	 */
	public void changeCacheDir(File cacheDir) {
		this.cacheDir = Objects.requireNonNull(cacheDir);
	}

	/**
	 * 
	 * @param url
	 * @return true iff the URL has been cached.
	 */
	public boolean contains(URL url) {

		// System.out.println("URL cache: " + cacheDir);
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		File[] files = cacheDir.listFiles(new ExtensionFileFilter("url"));
		Set<String> names = new HashSet<String>();
		String md5 = MD5Tools.md5(url.toString());
		for (File file : files) {
			names.add(file.getName());

		}
		return names.contains(md5 + ".url");

	}

	/**
	 * 
	 * @param url    the data needed
	 * @param global the {@link Global} vars
	 * @return {@link DataSource} constructed from the cached version of the URL
	 * @throws IOException
	 */
	public DataSource get(URL url, Global global) throws IOException {
		// System.out.println("Retrieving from cache: " + url);
		return new FileSource(
				new File(cacheDir, MD5Tools.md5(url.toString()) + ".url"),
				global);
	}

	/**
	 * 
	 * @param url an {@link URL} that is intended for caching
	 * @return an OutputStream to the cache file for the URL. The caller becomes
	 *         the owner of the stream and should flush/close it and then call
	 *         {@link #finish(URL)} when done.
	 * @throws FileNotFoundException
	 */
	public OutputStream startCaching(URL url) throws FileNotFoundException {
		return new FileOutputStream(
				new File(cacheDir, MD5Tools.md5(url.toString()) + ".tmp"));
	}

	/**
	 * Rename the cached file to .url. Assumes the data is completely written.
	 * 
	 * @param url the {@link URL} for which the cache file is complete
	 */
	public void finish(URL url) {
		File f = new File(cacheDir, MD5Tools.md5(url.toString()) + ".tmp");
		f.renameTo(new File(cacheDir, MD5Tools.md5(url.toString()) + ".url"));

	}

	/**
	 * 
	 * @param dir the new cache dir
	 */
	public void setDirectory(File dir) {
		this.cacheDir = dir;
	}

}
