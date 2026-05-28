/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.parser.Parser;
import net.sf.jannot.parser.ParserFactory;

/**
 * Extends AbstractStreamDataSource. It prepares the data to be read.
 * 
 * @author Thomas Abeel
 *
 */
public class FileSource extends AbstractStreamDataSource {

	private final File file;
	private final Parser parser;

	public File getFile() {
		return file;
	}

	public FileSource(File file, Global global) throws IOException {
		super(new Locator(file.toString(), global.getLog()), global);
		// temp stream to determine the data type
		InputStream tempstr = new FileInputStream(file);
		parser = ParserFactory.create(tempstr, file, global);
		tempstr.close();
		this.file = file;
	}

	@Override
	public String toString() {
		if (file.getParentFile() != null) {
			return ".../" + file.getParentFile().getName() + "/"
					+ file.getName().toString();
		} else {
			return file.getName().toString();
		}
	}

	@Override
	public boolean isIndexed() {
		return false;
	}

	@Override
	public long size() {
		return file.length();
	}

	@Override
	public Parser getParser() {
		return parser;
	}

	@Override
	public EntrySet read(EntrySet set) {
		try {
			set = parser.parse(new FileInputStream(file), set);
		} catch (FileNotFoundException e) {
			getLog().log(Level.WARNING, "Failed to parse", e);
		}
		return set;
	}

}
