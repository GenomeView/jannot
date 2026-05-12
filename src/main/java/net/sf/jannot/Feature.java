/**
 * %HEADER%
 */
package net.sf.jannot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.jannot.event.ChangeEvent;
import net.sf.jannot.event.FeatureEvent;

/**
 * 
 * Features is a Map/dictionary attached to one or more locations in a sequence.
 * The map is stored in {@link #qualifiers}. An example entry is "Name":
 * "inosine-5'-monophosphate dehydrogenase (guaB)" Both key and value are stored
 * as String. Typically features are stored in a gff file, separate from the
 * sequence
 * 
 * @author Thomas Abeel
 */
public class Feature implements Comparable<Feature>, Located {

	private final List<Location> locs = new ArrayList<>();

	private final Map<String, String> qualifiers = new HashMap<String, String>();

	private byte[] phase = null;

	private Type type;

	private Strand strand = Strand.UNKNOWN;

	// cache
	private boolean scoreBuffer = false; // true if cached score is valid
	private double score = Double.NaN; // cached value

	private int fStart = -1;
	private int fEnd = -1;

	public Feature(Set<Location> location) {
		if (location == null || location.size() == 0) {
			throw new NullPointerException(
					"location must contain at least 1 element");
		}

		setLocation(location);
	}

	public Feature(Location location) {
		setLocation(Arrays.asList(location));
	}

	/**
	 * Add a new qualifier to this Feature. The key,value pair will override the
	 * existing one
	 * 
	 * Exception: If key ="score", the score key will be extended with ","+value
	 * and then also scoreBuffer is set false.
	 * 
	 * All line breaks in value will be removed before storage in
	 * {@link #qualifiers}.
	 * 
	 * @param key   of the qualifier
	 * @param value value
	 */
	public void addQualifier(String key, String value) {
		if (value != null) {
			assert key != null;
			key = key.intern();

		}
		/* Remove line breaks in value */
		if (value != null) {
			value = value.replaceAll("\n", "");
		}

		if (!key.equals("score") && qualifiers.containsKey(key)) {
			qualifiers.put(key, qualifiers.get(key) + "," + value);
		} else {
			qualifiers.put(key, value);
		}

		if (key.equals("score")) {
			scoreBuffer = false;
		}

	}

	/**
	 * same as {@link #addQualifier(String, String)}
	 * 
	 * @param key   of the qualifier
	 * @param value value
	 */
	public void setQualifier(String key, String value) {
		if (key != null) {
			qualifiers.remove(key);
		}
		addQualifier(key, value);

	}

	@Override
	public boolean equals(Object f) {
		return this == f;
	}

	public Type type() {
		return type;

	}

	/**
	 * @param locations the locations for this feature. Must contain at least 1
	 *                  location.
	 */
	public void setLocation(Collection<Location> locations) {
		if (locations == null || locations.size() == 0) {
			throw new IllegalArgumentException(
					"at least 1 location required for feature");
		}
		// FIXME we'd like to check but some implementations of Collection
		// don't support contains(null)
		// if (locations.contains(null))
		// throw new IllegalArgumentException("null location in locations");
		if (locations.size() == 1) {
			setLocation(locations.iterator().next());
		} else {

			SortedSet<Location> set = new TreeSet<Location>();
			for (Location l : locations) {
				set.add(l);
			}
			locs.clear();
			for (Location l : set) {
				locs.add(l);

			}
			for (Location x : locs) {
				x.setParent(this);
			}
			updatePhase();
		}
	}

	/**
	 * Sets a singlelocation.
	 * 
	 * @param l the single {@link Location} of this feature.
	 */
	public void setLocation(Location l) {
		locs.clear();
		locs.add(l);
		phase = null;
		fStart = l.start();
		fEnd = l.end();
		l.setParent(this);
	}

	public void setLocation(Location[] l) {
		if (l.length == 1) {
			setLocation(l[0]);
		} else {
			locs.clear();
			for (Location loc : l) {
				locs.add(loc);
			}
			for (Location tmp : locs) {
				tmp.setParent(this);
			}

			updatePhase();
		}

	}

	public boolean overlaps(Location otherLoc) {
		return otherLoc.overlaps(otherLoc);
	}

	public boolean overlaps(Feature otherFeat) {
		Location thisLoc = new Location(fStart, fEnd);
		return thisLoc.overlaps(new Location(otherFeat.fStart, otherFeat.fEnd));
	}

	/**
	 * Set the direction
	 * 
	 * @param s the Strand
	 * @return a {@link ChangeEvent}
	 */
	public ChangeEvent setStrand(Strand s) {
		ChangeEvent ce = new ChangeStrandEvent(this, this.strand, s);
		ce.doChange();
		if (!locs.isEmpty()) {
			updatePhase();
		}
		return ce;

	}

	class ChangeTypeEvent extends FeatureEvent {
		private Type prev, next;

		public final Type getPrev() {
			return prev;
		}

		public final Type getNext() {
			return next;
		}

		public ChangeTypeEvent(Feature f, Type prev, Type next) {
			super(f, "set type to " + next);
			this.next = next;
			this.prev = prev;
		}

		@Override
		public void doChange() {
			type = next;

		}

		@Override
		public void undoChange() {
			assert (type == next);
			type = prev;

		}

	}

	public ChangeEvent setType(Type type) {
		ChangeEvent ce = new ChangeTypeEvent(this, this.type, type);
		ce.doChange();
		return ce;

	}

	/**
	 * Called when there are possible multiple locations for this feature.
	 * 
	 * Phase is not the same thing as Frame. Phase is the number of bases to
	 * skip before reading in-frame, while frame is the actual frame identifier
	 * beginning at 1.
	 */
	void updatePhase() {
		if (locs.isEmpty()) {
			return;
		}

		phase = new byte[locs.size()];

		// update fStart, fEnd
		int fStart = Integer.MAX_VALUE;
		int fEnd = 0;

		for (Location l : locs) {
			if (l.start() < fStart) {
				fStart = l.start();
			}
			if (l.end() > fEnd) {
				fEnd = l.end();
			}
		}
		this.fStart = fStart;
		this.fEnd = fEnd;

		int currentPhase = 0;
		if (strand == Strand.FORWARD) {
			for (int i = 0; i < locs.size(); i++) {
				phase[i] = (byte) currentPhase;
				currentPhase = (locs.get(i).length() - currentPhase);
				currentPhase %= 3;
				currentPhase = 3 - currentPhase;
				currentPhase %= 3;
			}
		} else if (strand == Strand.REVERSE) {
			for (int i = locs.size() - 1; i >= 0; i--) {
				phase[i] = (byte) currentPhase;
				currentPhase = (locs.get(i).length() - currentPhase);
				currentPhase %= 3;
				currentPhase = 3 - currentPhase;
				currentPhase %= 3;
			}
		} else {
			for (int i = 0; i < locs.size(); i++) {
				phase[i] = 0;
			}
		}

	}

	/**
	 * 
	 * @return list,either {@link #location} list, or list containing
	 *         {@link #singleLocation}, or mew Location[0] if both are null
	 */
	public Location[] location() {
		return locs.toArray(new Location[0]);
	}

	public int length() {
		return fEnd - fStart + 1;
	}

	@Override
	public int compareTo(Feature o) {

		int comp = new Integer(fStart).compareTo(o.fStart);
		if (comp == 0) {
			comp = new Integer(fEnd).compareTo(o.fEnd);
		}

		if (comp == 0) {
			return new Integer(hashCode()).compareTo(o.hashCode());
		} else {
			return comp;
		}
	}

	public Strand strand() {
		return strand;
	}

	public void removeQualifier(String key) {
		qualifiers.remove(key);
	}

	/**
	 * @param key indexing the qualifiers
	 * @return qualifiers.get(key) or null if no such key
	 */
	public String qualifier(String key) {
		return qualifiers.get(key);

	}

	/**
	 * 
	 * @return set of all qualifier keys. WARNING modifications to this set are
	 *         reflected into the qualifiers map private to this class,
	 */
	public Set<String> getQualifiersKeys() {
		return qualifiers.keySet();
	}

	/**
	 * Creates a deep copy of this feature.
	 * 
	 * @return copy of this
	 */
	public Feature copy() {
		SortedSet<Location> loc = new TreeSet<Location>();
		for (Location l : this.location()) {
			loc.add(l.copy());
		}
		Feature f = new Feature(loc);
		f.setStrand(this.strand());

		for (String key : qualifiers.keySet()) {
			f.addQualifier(key, qualifiers.get(key));
		}
		f.type = this.type();
		return f;
	}

	// private double bufferedScore = Double.NaN;
	@Deprecated
	public void setScore(double score) {
		setQualifier("score", "" + score);
		scoreBuffer = false;

	}

	/**
	 * 
	 * @return the value in the "score" qualifier.
	 */
	public double getScore() {
		// FIXME what if qualifiers were modified?
		if (scoreBuffer) {
			return score;
		} else {
			String val = qualifier("score");
			if (val == null) {
				return 0;
			} else {
				scoreBuffer = true;
				double tmpScore = 0;
				try {
					// bug? score may be a list
					tmpScore = Double.parseDouble(val);
				} catch (Exception e) {
					// FIXME maybe log something? Maybe not?
					// why are we not parsing score head-on?
				}
				score = tmpScore;
				return score;
			}
		}
	}

	@Override
	public String toString() {
		if (type != null) {
			return type.toString() + " [" + new Location(fStart, fEnd) + "]";
		} else {
			return "[" + new Location(fStart, fEnd).toString() + "]";
		}
	}

	public int getFrame() {
		int frame;
		if (locs.isEmpty()) {
			if (strand == Strand.REVERSE) {
				frame = fEnd % 3;
			} else {
				frame = fStart % 3;
			}
		} else {
			if (strand == Strand.REVERSE) {
				frame = (locs.get(locs.size() - 1).end()) % 3;
			} else {
				frame = (locs.get(0).start()) % 3;
			}
		}
		return frame == 0 ? 3 : frame;

	}

	/**
	 * 
	 * @param idx the location index number
	 * @return the phase of locs[idx]. The phase seems the proteine index 0,1,2.
	 *         Returns 0 if locs is empty
	 */
	public int getPhase(int idx) {
		if (locs.isEmpty()) {
			return 0;
		}
		return phase[idx];
	}

	/**
	 * @return
	 */
	public String getColor() {
		String notes = this.qualifier("colour");
		if (notes == null) {
			notes = this.qualifier("color");
		}
		return notes;
	}

	/**
	 * 
	 */
	public void clearQualifiers() {
		qualifiers.clear();

	}

	@Override
	public int start() {
		return fStart;
	}

	@Override
	public int end() {
		return fEnd;
	}

	public void addLocations(Collection<Location> locs) {
		for (Location l : locs) {
			addLocation(l);
		}
	}

	/**
	 * @param l the location to add
	 */
	public void addLocation(Location l) {
		List<Location> arr = new ArrayList<Location>(locs);
		arr.add(l);
		setLocation(arr);
	}

	/**
	 * @param rf
	 */
	public void removeLocation(Location rf) {
		if (locs.size() <= 1) {
			throw new RuntimeException("Can not remove the last location!!!");
		}
		List<Location> arr = new ArrayList<Location>(locs);
		locs.remove(rf);
		setLocation(arr);

	}

	/**
	 * inner class to make a strand change undo-able.
	 */
	class ChangeStrandEvent extends FeatureEvent {
		private Strand from, to;

		public ChangeStrandEvent(Feature f, Strand from, Strand to) {
			super(f, "Change strand from " + from.symbol() + " to "
					+ to.symbol());
			this.from = from;
			this.to = to;
		}

		@Override
		public void doChange() {
			super.getFeature().strand = to;

		}

		@Override
		public void undoChange() {
			super.getFeature().strand = from;

		}

	}

}
