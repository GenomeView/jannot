/**
 * %HEADER%
 */
package net.sf.jannot.source;

import java.io.InputStream;

import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.parser.Parser;

/**
 * Extends DataSource.
 * 
 * Contains methods to read
 * 
 * @author Thomas Abeel
 *
 */
public abstract class AbstractStreamDataSource extends DataSource {
	private Parser parser;

	private InputStream ios;

	protected AbstractStreamDataSource(Locator l, Global global) {
		super(l, global);
	}

	public final void setParser(Parser parser) {
		this.parser = parser;
	}

	public final void setIos(InputStream ios) {
		this.ios = ios;
	}

	@Override
	public EntrySet read(EntrySet set) {
		if (set == null) {
			set = new EntrySet(global);
		}
		return parser.parse(ios, set);
	}

	public InputStream getIos() {
		return ios;
	}

	public Parser getParser() {
		return parser;
	}
}
