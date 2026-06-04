package net.sf.jannot;

import java.util.Iterator;
import java.util.List;

/**
 * This Iterator is used to iterate over Lists without fail-fast behavior.
 */
public class LocatedListIterable<T extends Located> implements Iterable<T> {

	private final Location location;
	private final List<T> array;

	public LocatedListIterable(List<T> array, Location l) {
		this.location = l;
		this.array = array;
	}

	/**
	 * create an Iterator for the Array array.
	 * 
	 * @param array java.lang.Object
	 * 
	 * @throws UnsupportedOperationException if array is not an Array
	 */
	public LocatedListIterable(List<T> array) {
		this(array, null);

	}

	@Override
	public Iterator<T> iterator() {
		return new LocatedListIterator<T>(array, location);
	}
}
