/**
 * %HEADER%
 */
package net.sf.jannot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Stores all available {@link Entry}s. Each {@link Entry} is a named chromosome
 * and can be selected with the combobox in top of the viewer.
 * 
 * Call {@link #getOrCreateEntry(String)} to get a (new or existing) entry to
 * add a new Entry.
 * 
 * Top level class for JAnnot, this class represents a set of {@link Entry}.
 * 
 * 
 * @author Thomas Abeel
 *
 */
public class EntrySet implements Iterable<Entry> {

	final public Description description = new Description();

	// sorts entries to 'natural' (alphabetical) order
	private final ConcurrentSkipListSet<Entry> entries = new ConcurrentSkipListSet<Entry>();
	private final HashMap<String, Entry> map = new HashMap<String, Entry>();

	private final Global global;

	/**
	 * 
	 * @param log the Reporter, used to create dummy entries if needed
	 */
	public EntrySet(Global global) {
		this.global = global;
	}

	/*
	 * @return map[key], or if that is null map[key.lowercase] , or if that is
	 * also null map["chr"+key], or if that is null and key starts with "chr"
	 * map[key without "chr"], or else null
	 */
	private Entry mapGet(String key) {
		Entry out = map.get(key);
		if (out == null) {
			out = map.get(key.toLowerCase());
		}
		if (out == null) {
			out = map.get("chr" + key);
		}
		if (out == null && key.toLowerCase().startsWith("chr")) {
			out = map.get(key.substring(3));
		}

		return out;
	}

	@Override
	public Iterator<Entry> iterator() {
		return entries.iterator();
	}

	/**
	 * 
	 * @param key a key. If the key is a registered synonym, the 'real' value is
	 *            used
	 * @return an Entry that has the key as id.
	 */
	public synchronized Entry getOrCreateEntry(String key) {
		key = global.getNameService().getPrimaryName(key);
		if (mapGet(key) == null) {
			Entry e = new Entry(key, global);
			map.put(key, e);
			entries.add(e);
		}
		return mapGet(key);
	}

	// the first ID (IDs are sorted alphabetically)
	public synchronized Entry firstEntry() {
		return entries.first();
	}

	public synchronized Entry getEntry(String string) {
		return mapGet(global.getNameService().getPrimaryName(string));

	}

	public int size() {
		return map.size();
	}

	public void clear() {
		entries.clear();
		map.clear();
	}

	@Override
	public String toString() {
		return "EntrySet[" + map + "]";
	}
}
