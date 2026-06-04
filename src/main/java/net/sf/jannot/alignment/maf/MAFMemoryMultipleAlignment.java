/**
 * %HEADER%
 */
package net.sf.jannot.alignment.maf;

import java.security.KeyStore.Entry;

import net.sf.jannot.Data;
import net.sf.jannot.Global;
import net.sf.jannot.MemoryListData;

/**
 * A list of {@link AbstractAlignmentBlock}s containing the locations of various
 * sequences with respect to a reference chromosome. This object is stored in an
 * {@link Entry} using the name of this chromosome
 * 
 * @author Thomas Abeel
 * 
 */
public class MAFMemoryMultipleAlignment extends AbstractMAFMultipleAlignment {

	private MemoryListData<AbstractAlignmentBlock> delegate;

	@SuppressWarnings("serial")
	public MAFMemoryMultipleAlignment(Global global) {
		super(global);
		final Data<AbstractAlignmentBlock> _self = this;
		delegate = new MemoryListData<AbstractAlignmentBlock>(global) {
			@Override
			public String label() {
				return _self.label();
			}

		};
	}

	@Override
	public Iterable<AbstractAlignmentBlock> get(int start, int end) {
		return delegate.get(start, end);
	}

	@Override
	public Iterable<AbstractAlignmentBlock> get() {
		return delegate.get();

	}

	public void add(MemoryAlignmentBlock a) {
		delegate.add(a);

	}

	@Override
	public int noAlignmentBlocks() {
		return delegate.size();
	}

}
