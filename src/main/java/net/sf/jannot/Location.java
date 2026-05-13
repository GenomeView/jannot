/**
 * %HEADER%
 */
package net.sf.jannot;

import net.sf.jannot.event.ChangeEvent;

/**
 * A Location is a range with start, end position. It probably means "locations
 * of a label/tag on a genome".
 */
public class Location implements Comparable<Location> {

	protected int start, end; // FIXME mutable
	private final boolean fuzzyStart;
	private final boolean fuzzyEnd;

	/*
	 * A location can belong to a feature, but the feature is responsible for
	 * notifying the location that it belongs to that feature
	 * 
	 * This change is not recorded using a ChangeEvent.
	 */
	protected Feature parent = null;

	/**
	 * The main constructor
	 * 
	 * @param start      One endpoint of the interval. Can be negative. If
	 *                   smaller than end, start is used as end
	 * @param end        the other endpoint of the interval. Can be negative. If
	 *                   smaller than start, end is used as start
	 * @param fuzzyStart true iff start is fuzzy. NOTE this has no meaning
	 *                   anywhere excpet for {@link #toString()}
	 * @param fuzzyEnd   true iff end is fuzzy. NOTE this has no meaning
	 *                   anywhere except for {@link #toString()}
	 */
	public Location(int start, int end, boolean fuzzyStart, boolean fuzzyEnd) {
		if (end > start) {
			this.start = start;
			this.end = end;
		} else {
			this.start = end;
			this.end = start;
		}
		this.fuzzyStart = fuzzyStart;
		this.fuzzyEnd = fuzzyEnd;
	}

	/**
	 * Shortcut to create non-fuzzy interval.
	 * 
	 * @param x one endpoint of the interval
	 * @param y the other endpoint of the interval.
	 */
	public Location(int x, int y) {
		this(x, y, false, false);
	}

	@Override
	public String toString() {
		return (fuzzyStart ? "<" : "") + start + ".." + (fuzzyEnd ? ">" : "")
				+ end;
	}

	/**
	 * 
	 * @return the start, which is the low side of the range
	 */
	public final int start() {
		return start;
	}

	public final ChangeEvent setStart(int start) {
		ChangeEvent e = new SetStartEvent(this, this.start, start);
		e.doChange();
		return e;
	}

	/**
	 * 
	 * @return the end, which is the high side of the range
	 */

	public final int end() {
		return end;
	}

	public final ChangeEvent setEnd(int end) {
		ChangeEvent e = new SetEndEvent(this, this.end, end);
		e.doChange();
		return e;
	}

	/**
	 * {@inheritDoc}. the start position is compared first. If equal, the
	 * ordering is determined by the end position. {@link #fuzzyEnd} and
	 * {@link #fuzzyStart} have no effect here.
	 */
	@Override
	public int compareTo(Location arg0) {
		int comp = new Integer(start).compareTo(arg0.start());
		if (comp == 0) {
			comp = new Integer(this.end).compareTo(arg0.end());
		}

		return comp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + (fuzzyEnd ? 1231 : 1237);
		result = prime * result + (fuzzyStart ? 1231 : 1237);
		result = prime * result + start;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Location other = (Location) obj;
		if (end != other.end) {
			return false;
		}
		if (fuzzyEnd != other.fuzzyEnd) {
			return false;
		}
		if (fuzzyStart != other.fuzzyStart) {
			return false;
		}
		if (start != other.start) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return the extent of this location, so end-start+1
	 */
	public int length() {
		return end - start + 1;
	}

	/**
	 * 
	 * @param pos a position
	 * @return true iff pos is inside this
	 */
	public boolean contains(int pos) {
		return pos >= start && pos <= end;
	}

	/**
	 * 
	 * @param lStart start of other range
	 * @param lEnd   end of other range
	 * @return true if this and other overlap - some locations are in both
	 *         ranges.
	 */
	public boolean overlaps(Location other) {
		// our start or end is inside the other
		if (other.contains(start) || other.contains(end)) {
			return true;
		}
		// we completely cover the other
		if (other.start >= start && other.end <= end) {
			return true;
		}
		return false;
	}

	/**
	 * @return copy of current. Undo actions on the original will not affect the
	 *         copy
	 */
	public Location copy() {
		return new Location(start, end, fuzzyStart, fuzzyEnd);
	}

	/**
	 * Set/override the parent feature
	 * 
	 * @param f the new parent feature.
	 */
	void setParent(Feature f) {
		this.parent = f;
	}

	/**
	 * @return the {@link Feature} that is the unique parent of this location,
	 *         or null.
	 */
	public Feature getParent() {
		return parent;
	}

	/**
	 * 
	 * @param range an additional range to include
	 * @return copy of this but extended such that targetLocation is included
	 */
	public Location extend(Location range) {
		return new Location(Math.min(start, range.start),
				Math.max(end, range.end), fuzzyStart, fuzzyEnd);
	}

	/**
	 * @param x a number that is to be scaled relative to this
	 * @return the position as number relative to this. x=start returns 0, x=end
	 *         returns 1, and extrapolates linearly.
	 * 
	 */
	public double fraction(int x) {
		return (double) (x - start) / (end - start);
	}

}

class SetEndEvent implements ChangeEvent {
	/* New position */
	private int to;

	/* Original position */
	private int from;

	private Location l;

	public SetEndEvent(Location l, int orig, int newpos) {
		this.l = l;
		this.from = orig;
		this.to = newpos;
	}

	@Override
	public void doChange() {
		l.end = to;
		if (l.parent != null) {
			l.parent.updatePhase();
		}

	}

	@Override
	public void undoChange() {
		assert (l.end == to);
		l.end = from;
		if (l.parent != null) {
			l.parent.updatePhase();
		}

	}

	@Override
	public String toString() {
		return new String("Set end from " + from + " to " + to);
	}
}

class SetStartEvent implements ChangeEvent {

	private int to;

	private int from;

	private Location l;

	public SetStartEvent(Location l, int originalPosition, int newPosition) {
		this.l = l;
		this.from = originalPosition;
		this.to = newPosition;
	}

	@Override
	public void doChange() {
		l.start = to;
		if (l.parent != null) {
			l.parent.updatePhase();
		}

	}

	@Override
	public void undoChange() {
		assert (l.start == to);
		l.start = from;
		if (l.parent != null) {
			l.parent.updatePhase();
		}
	}

	@Override
	public String toString() {
		return new String("Set start from " + from + " to " + to);
	}
}
