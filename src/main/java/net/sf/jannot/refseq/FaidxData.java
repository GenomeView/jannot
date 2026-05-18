/**
 * %HEADER%
 */
package net.sf.jannot.refseq;

import htsjdk.samtools.seekablestream.SeekableStream;
import net.sf.jannot.Global;
import net.sf.jannot.refseq.FaidxIndex.IndexEntry;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class FaidxData extends Sequence {

	private SeekableStream data = null;
	private IndexEntry idx;

	/**
	 * @param index
	 * @param content
	 * @param name
	 * @param global     the {@link Global} to user for the data
	 */
	public FaidxData(FaidxIndex index, SeekableStream content, String name,
			Global global) {
		super(global);
		this.data = content;
		this.idx = index.get(name);

	}

	@Override
	public Iterable<Character> get(int start, int end) {
		return new FaixDataIterable(data, start, end - 1, idx.start, idx.len,
				idx.lineLen, idx.byteLen);
	}

	@Override
	public Iterable<Character> get() {
		return get(1, (int) (idx.len + 1));
		// return new FaixDataIterable(data, 1, (int) this.len, this.start,
		// this.len, this.lineLen, this.byteLen);
	}

	@Override
	public int size() {
		return (int) idx.len;
	}

}
