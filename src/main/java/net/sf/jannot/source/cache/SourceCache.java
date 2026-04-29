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
import java.util.Set;

import atk.io.ExtensionFileFilter;
import atk.util.MD5Tools;
import net.sf.jannot.Global;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.FileSource;

public class SourceCache {
	// singleton class with only static members.

	public static File cacheDir = new File(System.getProperty("user.home"));;

	public static boolean contains(URL url) {

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

	public static DataSource get(URL url, Global global) throws IOException {
		// System.out.println("Retrieving from cache: " + url);
		return new FileSource(
				new File(cacheDir, MD5Tools.md5(url.toString()) + ".url"),
				global);
	}

	public static OutputStream startCaching(URL url)
			throws FileNotFoundException {
		return new FileOutputStream(
				new File(cacheDir, MD5Tools.md5(url.toString()) + ".tmp"));
	}

	/**
	 * This method should only be called when the data is completely written.
	 * 
	 * @param url
	 */
	public static void finish(URL url) {
		File f = new File(cacheDir, MD5Tools.md5(url.toString()) + ".tmp");
		f.renameTo(new File(cacheDir, MD5Tools.md5(url.toString()) + ".url"));

	}

}
