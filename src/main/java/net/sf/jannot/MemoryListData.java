/**
 * %HEADER%
 */
package net.sf.jannot;

import java.util.ArrayList;

/**
 * Data that is stored in memory in a list of a particular type
 */
@SuppressWarnings("serial")
public abstract class MemoryListData<T> extends ArrayList<T>
		implements Data<T> {

	private final Global global;

	public MemoryListData(Global global) {
		this.global = global;
	}

	@Override
	public Global global() {
		return global;
	}

	public void addAll(MemoryListData<T> t) {
		Iterable<T> list = t.get();
		addAll(list);
	}

	public void addAll(Iterable<T> list) {
		for (T t : list) {
			this.add(t);
		}
	}

	@Override
	public Iterable<T> get(int start, int end) {
		return new LocatedListIterable(this, new Location(start, end));

	}

	@Override
	public Iterable<T> get() {
		return new ListIterable<T>(this);
	}

	@Override
	public boolean canSave() {
		return false;
	}

}
