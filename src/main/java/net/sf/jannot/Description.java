/**
 * %HEADER%
 */
package net.sf.jannot;

import java.util.HashMap;
import java.util.Set;

import net.sf.jannot.parser.EMBLParser;
import net.sf.jannot.parser.FastaParser;
import net.sf.jannot.parser.GenbankParser;

/**
 * A map to contain additional entry information like header, seqversion,
 * moleculeType, dataClass, taxDivision, first date. This is used only by a few
 * parsers - {@link EMBLParser}, {@link GenbankParser} and {@link FastaParser}.
 * Other parsers probably put the information in the {@link Data}. CHECK
 * 
 * 
 * @author Thomas Abeel
 *
 */
public class Description {

	private final HashMap<String, String> keyValues = new HashMap<String, String>();

	public Set<String> keys() {
		return keyValues.keySet();
	}

	public void put(String key, String value) {
		keyValues.put(key, value);
	}

	public String get(String key) {
		return keyValues.get(key);
	}

	/**
	 * Add a value to a key. If the keys doesn't exist yet, it is added.
	 * Different values for a key are separated with a line break.
	 * 
	 * @param key   key to use
	 * @param value value to add to the key
	 */
	public void add(String key, String value) {
		if (!keyValues.containsKey(key)) {
			put(key, value);
		} else {
			put(key, get(key) + "\n" + value);
		}

	}

	@Override
	public String toString() {
		return keyValues.toString();
	}
}
