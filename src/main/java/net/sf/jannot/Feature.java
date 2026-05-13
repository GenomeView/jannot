/**
 * %HEADER%
 */
package net.sf.jannot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

	/** the {@link Location}s that this feature associates with. never empty */
	private final List<Location> locs = new ArrayList<>();

	/*** The qualifiers for this feature */
	private final Map<String, String> qualifiers = new HashMap<String, String>();

	/** computed phase of each {@link #locs} */
	private byte[] phase = null;

	Type type;

	// set initially, so that ChangeEvent can 'undo' any change
	Strand strand = Strand.UNKNOWN;

	// cached values
	private boolean scoreBuffer = false; // true if cached score is valid
	private double score = Double.NaN; // cached value
	/** computed contour of {@link #locs} */
	private Location location;

	/**
	 * 
	 * @param locations a non-empty set of {@link Location}s.
	 * @param type      the {@link Type} of the feature
	 * @param strand    the non-null {@link Strand} of the feature.
	 */
	public Feature(Set<Location> locations, Type type, Strand strand) {
		setLocation(locations);
		setType(type);
		setStrand(strand);
	}

	/**
	 * Convenience constructor, see {@link #Feature(Set, Type, Strand)}
	 */
	public Feature(Location location, Type type, Strand strand) {
		this(new HashSet<>(Arrays.asList(location)), type, strand);
	}

	/**
	 * Add a new qualifier to this Feature. If a value already exists for key,
	 * the value will be extended with "," and the given value.
	 * 
	 * Exception: If key ="score", the score value will replace the old value
	 * 
	 * All line breaks in value will be removed before storage in
	 * {@link #qualifiers}.
	 * 
	 * @param non-null key of the qualifier key
	 * @param value    some string, or null
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(phase);
		result = prime * result + Objects.hash(locs, qualifiers, strand, type);
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
		Feature other = (Feature) obj;
		return Objects.equals(locs, other.locs)
				&& Arrays.equals(phase, other.phase)
				&& Objects.equals(qualifiers, other.qualifiers)
				&& strand == other.strand && Objects.equals(type, other.type);
	}

	public Type type() {
		return type;

	}

	/**
	 * @param locations the locations for this feature. Must contain at least 1
	 *                  location.
	 */
	public void setLocation(Collection<Location> locations) {
		if (Objects.requireNonNull(locations).isEmpty()) {
			throw new IllegalArgumentException(
					"at least 1 location required for feature");
		}
		// sort the locations
		final SortedSet<Location> set = new TreeSet<Location>();
		for (Location l : locations) {
			set.add(Objects.requireNonNull(l));
		}
		locs.clear();
		for (Location l : set) {
			locs.add(l);
			l.setParent(this);
		}
		updatePhase();
	}

	public boolean overlaps(Feature otherFeat) {
		return location.overlaps(otherFeat.location);
	}

	/**
	 * Set the direction
	 * 
	 * @param s the Strand
	 * @return a {@link ChangeEvent}
	 */
	public ChangeEvent setStrand(Strand s) {
		Objects.requireNonNull(s);
		ChangeEvent ce = new ChangeStrandEvent(this, this.strand, s);
		ce.doChange();
		updatePhase();
		return ce;

	}

	public ChangeEvent setType(Type type) {
		ChangeEvent ce = new ChangeTypeEvent(this, this.type, type);
		ce.doChange();
		return ce;

	}

	/**
	 * 
	 * @return copy of the {@link #locs} that this feature is associated with
	 */
	public Location[] location() {
		return locs.toArray(new Location[0]);
	}

	/**
	 * 
	 * @return the total extent of this feature, covering all {@link #locs}
	 */
	public int length() {
		return location.length();
	}

	@Override
	public int compareTo(Feature o) {
		int comp = location.compareTo(o.location);
		if (comp != 0) {
			return comp;
		}
		// fallback, a bit arbitrary based on the memory location...
		return new Integer(hashCode()).compareTo(o.hashCode());
	}

	public Strand strand() {
		return strand;
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
		Feature f = new Feature(loc, type(), strand());

		for (String key : qualifiers.keySet()) {
			f.addQualifier(key, qualifiers.get(key));
		}
		return f;
	}

	@Deprecated
	public void setScore(double score) {
		addQualifier("score", "" + score);
		scoreBuffer = false;

	}

	/**
	 * 
	 * @return the value in the "score" qualifier. Score is kept in qualifiers
	 *         but cached
	 */
	public double getScore() {
		if (scoreBuffer) {
			return score;
		}
		String val = qualifier("score");
		if (val == null) {
			return 0;
		}
		scoreBuffer = true;
		double tmpScore = 0;
		try {
			tmpScore = Double.parseDouble(val);
		} catch (Exception e) {
			// FIXME maybe log something? Maybe not?
			// why are we not parsing score head-on?
		}
		score = tmpScore;
		return score;

	}

	@Override
	public String toString() {
		if (type != null) {
			return type.toString() + " [" + location + "]";
		} else {
			return "[" + location.toString() + "]";
		}
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
	 * @return the colour or color qualifier or null if no such qualifier
	 */
	public String getColor() {
		String notes = this.qualifier("colour");
		if (notes == null) {
			notes = this.qualifier("color");
		}
		return notes;
	}

	/**
	 * remove all qualifiers
	 */
	public void clearQualifiers() {
		qualifiers.clear();

	}

	@Override
	public int start() {
		return location.start();
	}

	@Override
	public int end() {
		return location.end();
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
	 * computes {@link #phase} and {@link #location}. Apparently this is also
	 * called externally
	 * <p>
	 * Phase is not the same thing as Frame. Phase is the number of bases to
	 * skip before reading in-frame, while frame is the actual frame identifier
	 * beginning at 1.
	 * <p>
	 * FIXME what is "in-frame"? What is frame? What is 'actual frame
	 * identifier'?
	 */
	protected void updatePhase() {
		phase = new byte[locs.size()];

		/** update {@link #location} */
		location = locs.get(0);
		for (Location l : locs) {
			location = location.extend(l);
		}
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
}

/**
 * {@link ChangeEvent} storing a {@link Strand} change undo-able.
 */
class ChangeStrandEvent extends FeatureEvent {
	private Strand from, to;

	public ChangeStrandEvent(Feature f, Strand from, Strand to) {
		super(f, "Change strand from " + from.symbol() + " to " + to.symbol());
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

/**
 * {@link ChangeEvent} storing a {@link Type} change
 */
class ChangeTypeEvent extends FeatureEvent {
	private Type prev, next;
	private Feature feature;

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
		getFeature().type = next;

	}

	@Override
	public void undoChange() {
		assert (getFeature().type == next);
		getFeature().type = prev;

	}

}