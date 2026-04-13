package net.sf.jannot.parser;

/**
 * thrown if a file can not be parsed properly and can not be worked around.
 */
public class ParserError extends Exception {

	/**
	 * @param string the error message. Please include line,column if possible
	 *               and explain what was expected and what you found
	 */
	public ParserError(String msg) {
		super(msg);
	}
}
