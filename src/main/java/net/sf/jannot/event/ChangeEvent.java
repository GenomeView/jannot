/**
 * %HEADER%
 */
package net.sf.jannot.event;

/**
 * Stores info about an undo-able actino.
 */
public interface ChangeEvent {

	/**
	 * re-apply the change. Undefined behaviour if called repeatedly
	 */
	public void doChange();

	/**
	 * undo the change. Undefined behaviour if called repeatedly
	 */
	public void undoChange();

}
