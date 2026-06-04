/**
 * %HEADER%
 */
package net.sf.jannot.source;

import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.parser.Parser;

/**
 * Extends DataSource. These Data Sources use a Parser to convert the data
 * stream to an {@link EntrySet}
 * 
 * Contains methods to read
 * 
 * @author Thomas Abeel
 *
 */
public abstract class ParserDataSource extends DataSource {

	protected ParserDataSource(Locator l, Global global) {
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
