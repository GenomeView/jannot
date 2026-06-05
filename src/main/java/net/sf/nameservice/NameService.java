package net.sf.nameservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import tudelft.utilities.logging.Reporter;

/**
 * Stores key-value pairs.
 */
public class NameService {
	// all keys are stored in UPPER CASE.
	private final Map<String, String> map = new HashMap<String, String>();
	private final Reporter log;

	public NameService(Reporter reporter) {
		this.log = reporter;
		resetDefault();
	}

	/**
	 * 
	 * @param key a non-null un-cleaned key
	 * @return a cleaned key, with all leading and trailing spaces removed. But
	 *         if the cleaned key in all-upper-case is a known key, the value
	 *         stored for that key is returned.
	 */
	public String getPrimaryName(String key) {
		key = Objects.requireNonNull(key).trim();
		if (map.containsKey(key.toUpperCase())) {
			return map.get(key.toUpperCase());
		} else {
			return key;
		}
	}

	public void resetDefault() {
		map.clear();
		addSynonyms(NameService.class.getResourceAsStream("synonyms.txt"));
	}

	/**
	 * 
	 * @param primary the key. The key is cleaned: trimmed and whitespaces are
	 *                replaced with '_'.
	 * @param alt     a comma-separated list of [value]s for key. Each of these
	 *                values is put as [cleaned-primary]:[value] in the map.
	 */
	public void addSynonym(String primary, String alt) {
		map.put(primary.trim().replace(' ', '_').toUpperCase(), primary.trim());
		String[] arr = alt.split(",");
		for (String s : arr) {
			map.put(s.trim().toUpperCase(), primary.trim());
			map.put(s.trim().replace(' ', '_').toUpperCase(), primary.trim());
		}

	}

	/**
	 * Imports maps from an input stream. The stream must contain lines of the
	 * form XXX=AA,BB,CC...,FF. AA..FF is a comma-separated list of values. Each
	 * is added with {@link #addSynonym(XX, AA..FF)}
	 * 
	 * @param is the {@link InputStream}
	 */
	public void addSynonyms(InputStream is) {
		for (String line : new LineIterator(is, true, true)) {
			String[] prim = line.split("=");
			addSynonym(prim[0], prim[1]);

		}
		try {
			is.close();
		} catch (IOException ioe) {
			log.log(Level.WARNING, "Failed to close inputstream " + is);
		}
	}

}
