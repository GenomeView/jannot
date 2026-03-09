/**
 * %HEADER%
 */
package net.sf.jannot;

/**
 * Data of type K, for an {@link Entry}. Data has a start and end point, a
 * label, and can be iterated over
 * 
 * @author Thomas Abeel
 */
public interface Data<K> {

	/**
	 * Gets data. The selected data should cover [start,end[. The coordinates
	 * are one based.
	 * 
	 * @param start the start coordinate, this one will be included. This is a
	 *              one-based coordinate.
	 * @param end   the end coordinate, this one will not be included. This is a
	 *              one-based coordinate.
	 * @return the selected data.
	 */
	public Iterable<K> get(int start, int end);

	/**
	 * 
	 * @return {@link Iterable} over all data
	 */
	public Iterable<K> get();

	/**
	 * @return true iff this data can be saved?
	 */
	public boolean canSave();

	/**
	 * 
	 * @return a label for this data
	 */
	public String label();

}