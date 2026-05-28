/**
 * %HEADER%
 */
package net.sf.jannot.source;

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

	protected AbstractStreamDataSource(Locator l, Global global) {
		super(l, global);
	}

//	@Override
//	public EntrySet read(EntrySet set) {
//		if (set == null) {
//			set = new EntrySet(global);
//		}
//		return getParser().parse(ios, set);
//	}
//
	/**
	 * 
	 * @return {@link Parser} that can parse the input from locator.
	 */
	abstract public Parser getParser();
}
