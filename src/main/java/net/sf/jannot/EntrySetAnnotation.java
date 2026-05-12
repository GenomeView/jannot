/**
 * %HEADER%
 */
package net.sf.jannot;

import java.util.Iterator;

import net.sf.jannot.alignment.mfa.AlignmentAnnotation;
import net.sf.jannot.syntenic.SyntenicAnnotation;

/**
 * Interface for annotation associated with an {@link EntrySet} of type T.
 * 
 * @author Thomas Abeel
 * 
 * @param <T> type of the annotation
 * @see FeatureAnnotation
 * @see AlignmentAnnotation
 * @see SyntenicAnnotation
 * @see GraphAnnotation
 */
public abstract class EntrySetAnnotation<T> implements Iterable<T> {

	/**
	 * Returns all object of type T that overlap with the provided location.
	 * 
	 * @param l
	 * @return
	 */
	public Iterable<T> get(Entry e, Location l) {
		return getAll(e);
	}

	/**
	 * 
	 * @param t an element in the set
	 * @return true iff this set contains t
	 */
	public abstract boolean contains(T t);

	/**
	 * Returns all object of type T that overlap with the provided location.
	 * 
	 * @param e     an entry. Unclear what this is used for
	 * @param l     the {@link Location} in which T are requested
	 * @param limit is ignored. Maybe originally intended to limit the number of
	 *              results?
	 * @return iter for all entries
	 */
	public Iterable<T> get(Entry e, Location l, int limit) {
		return get(e, l);
	}

	public abstract Iterable<T> getAll(Entry e);

	public abstract void add(T g);

	@Override
	public abstract Iterator<T> iterator();

}
