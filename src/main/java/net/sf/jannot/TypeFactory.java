package net.sf.jannot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An instance is available through {@link Global#typeFactory()}
 */
public class TypeFactory {

	private final ConcurrentHashMap<String, Type> map = new ConcurrentHashMap<String, Type>();

	// the index number of each type
	private final List<Type> order = new ArrayList<Type>();

	/**
	 * @return array of declared types, in the current order
	 */
	public List<Type> values() {
		return Collections.unmodifiableList(order);
//		return order.toArray(new Type[0]);

	}

	/**
	 * @param key the key under which the type is stored. If the key si not yet
	 *            stored, a new Type with the same string as the key is created.
	 * @return a {@link Type}
	 */
	public synchronized Type get(String key) {
		if (!map.containsKey(key)) {
			map.put(key, new Type(key));
			order.add(map.get(key));
		}
		return map.get(key);
	}

	/**
	 * moveUp swap 'type' index with its previous one. If 'type' is the first
	 * index, nothing happens.
	 * 
	 * @param type
	 */
	public synchronized void moveUp(Type type) {
		int index = order.indexOf(type);
		if (index > 0) {
			Collections.swap(order, index - 1, index);
		}

	}

	/**
	 * moveDown swap 'type' index with its next one. If 'type' is the last
	 * index, nothing happens.
	 * 
	 * @param type
	 */
	public synchronized void moveDown(Type type) {
		int index = order.indexOf(type);
		if (index < order.size() - 1) {
			Collections.swap(order, index, index + 1);
		}

	}
}
