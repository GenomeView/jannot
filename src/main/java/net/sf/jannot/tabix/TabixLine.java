/**
 * %HEADER%
 */
package net.sf.jannot.tabix;

import java.util.ArrayList;

/**
 * Contains the elements of a single line, eg from a table or table-like data
 * file
 * 
 * @author Thomas Abeel
 * 
 */
public class TabixLine {
	private ArrayList<String> cols = new ArrayList<String>();
	private int tid;
	private int beg;
	private int end;
	private boolean meta = false;
	private final String line;
	private TabIndex idx;
	private char split;

	/**
	 * 
	 * @param line  a string line
	 * @param idx   the {@link TabIndex}
	 * @param split a split character, eg '\t'
	 */
	public TabixLine(String line, TabIndex idx, char split) {
		this.line = line;
		this.idx = idx;
		this.split = split;
		parse();
	}

	public int getTid() {
		return tid;
	}

	public int getBegin() {
		return beg;
	}

	public int getEnd() {
		return end;
	}

	/**
	 * 
	 * @return the original string line
	 */
	public String line() {
		return line;
	}

	public String get(int idx) {
		return cols.get(idx);
	}

	/**
	 * @return true iff line is comment lines or other useless lines, like empty
	 *         one
	 */
	public boolean isMeta() {
		return meta;
	}

	/**
	 * 
	 * @param idx the field index number
	 * @return parsed int
	 * @throws NumberFormatException if the value is not a proper int
	 */
	public int getInt(int idx) {
		return Integer.parseInt(get(idx));
	}

	/**
	 * This parses {@link #line} and fills the content of this TabixLine with
	 * the parse results
	 */
	private void parse() {
		if (line.length() == 0) {
			this.meta = true;
			return;
		}
		StringBuffer tmp = new StringBuffer();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == split) {
				cols.add(tmp.toString());
				tmp = new StringBuffer();
			} else {
				tmp.append(c);

			}

		}

		cols.add(tmp.toString());

		this.tid = idx.names.indexOf(cols.get((int) idx.sc - 1));
		this.beg = Integer.parseInt(cols.get((int) idx.bc - 1).toString());
		if (idx.ec > 0)
			this.end = Integer.parseInt(cols.get((int) idx.ec - 1).toString());
		else
			this.end = beg;
	}

	/**
	 * @return number of columns in the data
	 */
	public int length() {
		return cols.size();
	}

}