/**
 * %HEADER%
 */
package net.sf.jannot;

import java.io.IOException;

import tudelft.utilities.logging.Reporter;

/**
 * Data related to genome. Thought of as a 'list with elements of type K.
 * Typically part of an {@link Entry}. Data has a start and end point, a label,
 * and can be iterated over
 * 
 * @author Thomas Abeel
 */
public interface Data<K> {

	/**
	 * Gets data. The selected data should cover [start,end[. The coordinates
	 * are one based.
	 * 
	 * 
	 * @param start the start coordinate, this one will be included. This is a
	 *              one-based coordinate.
	 * @param end   the end coordinate, this one will not be included. This is a
	 *              one-based coordinate.
	 * @return the selected data. <b>WARNING</b> returned iterable must he
	 *         thread safe, particularly sobust while the underlying data is
	 *         changing.
	 * 
	 * @throws IOException if there is a serious problem
	 */
	public Iterable<K> get(int start, int end) throws IOException;

	/**
	 * @return {@link Iterable} over all data <b>WARNING</b> returned iterable
	 *         must he thread safe, particularly sobust while the underlying
	 *         data is changing.
	 * 
	 * @throws IOException if there is a serious problem
	 */
	public Iterable<K> get() throws IOException;

	/**
	 * @return true iff this data can be saved?
	 */
	public boolean canSave();

	/**
	 * 
	 * @return a label for this data
	 */
	public String label();

	public Reporter getLog();

}