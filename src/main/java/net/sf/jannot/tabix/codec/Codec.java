/**
 * %HEADER%
 */
package net.sf.jannot.tabix.codec;

import java.util.Iterator;

import be.abeel.util.LRUCache;
import net.sf.jannot.tabix.TabixLine;
import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * 
 */
public abstract class Codec<T> implements Iterable<T> {

	private final Iterable<TabixLine> in;
	protected final LRUCache<TabixLine, T> lru;
	private final Reporter log;

	public Codec(Iterable<TabixLine> in, int lruSize, Reporter log) {
		this.in = in;
		this.lru = new LRUCache<TabixLine, T>(lruSize);
		this.log = log;
	}

	public Reporter getLog() {
		return log;
	}

	/**
	 * @return an iterator over WHAT?
	 */
	@Override
	public Iterator<T> iterator() {
		return new CodecIterator(in);
	}

	/**
	 * @param next
	 * @return the next <T> parsed from a {@link TabixLine}
	 */
	public abstract T parse(TabixLine next);

	class CodecIterator implements Iterator<T> {

		private Iterator<TabixLine> it;

		/**
		 * @param in
		 */
		public CodecIterator(Iterable<TabixLine> in) {
			this.it = in.iterator();
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public T next() {
			return parse(it.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}
}
