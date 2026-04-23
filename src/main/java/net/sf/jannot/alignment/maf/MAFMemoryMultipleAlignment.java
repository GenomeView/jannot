/**
 * %HEADER%
 */
package net.sf.jannot.alignment.maf;

import net.sf.jannot.Data;
import net.sf.jannot.MemoryListData;
import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * 
 */
public class MAFMemoryMultipleAlignment extends AbstractMAFMultipleAlignment {

	private MemoryListData<AbstractAlignmentBlock> delegate;

	@SuppressWarnings("serial")
	public MAFMemoryMultipleAlignment(Reporter log) {
		super(log);
		final Data<AbstractAlignmentBlock> _self = this;
		delegate = new MemoryListData<AbstractAlignmentBlock>(log) {
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
