/**
 * %HEADER%
 */
package net.sf.jannot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.sf.jannot.refseq.MemorySequence;
import net.sf.jannot.refseq.Sequence;
import net.sf.jannot.shortread.ReadGroup;

/**
 * "chromosome" container for a (often short) named nucleotide sequence. If
 * multiple named sequences are loaded (eg through a m-fasta file), each named
 * sequence is kept as separate 'chromosome' and can be selected with the
 * combobox in the top of the viewer. Other files may contain references to
 * multiple chromosomes and their data is usually split over the corresponding
 * {@link Entry}s
 * 
 * each chromosome has an {@link #id} and {@link #description}
 * 
 * It contains a map of key-{@link Data} pairs. The {@link #seqKey} contains the
 * nucleotide sequence of the chromosome. Other keys contain other data such as
 * annotations, related {@link Sequence}s, {@link ReadGroup}s with shortreads
 * for this chromosome, etc.
 * 
 * In order to load data to an Entry set, you should use the class Filesource.
 * 
 * e.g.: EntrySet set = new FileSource(new File("sequence.fasta")).read();
 * 
 * will read a sequence from a fasta to Entryset set.
 * 
 * new FileSource(new File("sequence.gff")).read(set);
 * 
 * will read notations from the .gff file to Entryset set.
 * 
 * An entry is mutable
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class Entry implements Comparable<Entry>, Iterable<DataKey> {

	/**
	 * A key that is used to store the actual nucleotide sequence in this entry.
	 */
	private static final StringKey seqKey = new StringKey(
			"SEQ*(^#%(@#%)@#^@#^))^)@#)^(@#%^*()SEQ");

	private final Description description = new Description();

	// map itself is mutable
	private final Map<DataKey, Data<?>> data = new HashMap<DataKey, Data<?>>();

	private final String id;

	private final Global global;

	/**
	 * 
	 * @param id     the dirty id, may be an alias previously registered to the
	 *               NameService. Must not be null
	 * @param global the {@link Global} structures
	 */
	public Entry(String id, Global global) {
		this.id = global.getNameService().getPrimaryName(id);
		this.global = global;

	}

	/**
	 * @return the highest position for which there is data
	 */
	public int getMaximumLength() {
		// FIXME think of a more efficient way
		long maxSize = 0;
		for (DataKey dk : this) {
			Data<?> newData = data.get(dk);
			/* Update maximum size if applicable */
			long s = 0;
			if (newData instanceof Sequence) {
				s = ((Sequence) newData).size();
			}

			if (newData instanceof FeatureAnnotation) {
				s = ((FeatureAnnotation) newData).getMaximumCoordinate();
			}

			// System.out.println("s update: " + s);
			if (s > maxSize) {
				maxSize = s;
			}
		}
		return (int) maxSize;
	}

	/**
	 * Stores given data under the key. If the key already contains data, this
	 * does nothing and your data is NOT stored.
	 * 
	 * @param key     the key ID
	 * @param newData the data to add to this entry. Nothing happens if the key
	 *                is already used
	 */
	public void add(DataKey key, Data<?> newData) {

		if (data.containsKey(key)) {
			global.getLog().log(Level.WARNING,
					"ignoring data: key already used " + key);
			return;
		}
		data.put(key, newData);

	}

	/**
	 * @param dataKey
	 * @return the data stored under the datakey.
	 */
	public Data<?> get(DataKey dataKey) {
		return data.get(dataKey);
	}

	/**
	 * return iterator over the data keys
	 */
	@Override
	public Iterator<DataKey> iterator() {
		return data.keySet().iterator();
	}

	/**
	 * 
	 * @return the keys in the data
	 */
	public Set<DataKey> keys() {
		return data.keySet();
	}

	/**
	 * @return iteratable over all {@link ReadGroup}s contained in the data.
	 *         Note that these are in arbitrary order
	 */
	public Iterable<ReadGroup> shortReads() {
		ArrayList<ReadGroup> out = new ArrayList<ReadGroup>();
		for (DataKey key : data.keySet()) {
			Data<?> x = data.get(key);
			if (x instanceof ReadGroup) {
				out.add((ReadGroup) x);
			}
		}
		return out;
	}

	/**
	 * @param type the data key for which a {@link MemoryFeatureAnnotation} is
	 *             expected in the data
	 * @return the {@link MemoryFeatureAnnotation} registered for 'type', or
	 *         null if such feature is not there and cannot be created either.
	 */
	public MemoryFeatureAnnotation getMemoryAnnotation(DataKey type) {
		if (!data.containsKey(type)) {
			this.add(type, new MemoryFeatureAnnotation(global));
		}
		Data<?> tmp = this.get(type);
		if (tmp instanceof MemoryFeatureAnnotation) {
			return (MemoryFeatureAnnotation) tmp;
		} else {
			return null;
		}

	}

	/**
	 * 
	 * @return the sequence stored for this entry, or an empty sequence
	 */
	public Sequence sequence() {
		if (!data.containsKey(seqKey)) {
			data.put(seqKey, new MemorySequence(global));
		}
		return (Sequence) data.get(seqKey);
	}

	@Override
	public String toString() {
		return "" + data;
	}

	/**
	 * 
	 * @return the id of this entry
	 */
	public String getID() {
		return id;
	}

	/**
	 * @return {@link String#compareTo(String)} between our ID and o.getID()
	 */
	@Override
	public int compareTo(Entry o) {
		return this.getID().compareTo(o.getID());
	}

	/**
	 * @param dataKey
	 * @return true iff data contains a value for this dataKey
	 */
	public boolean contains(DataKey dataKey) {
		return data.containsKey(dataKey);
	}

	/**
	 * 
	 * @param seq the {@link Sequence}
	 */
	public void setSequence(Sequence seq) {
		data.put(seqKey, seq);
	}

	/**
	 * @param dataKey
	 */
	public void remove(DataKey dataKey) {
		data.remove(dataKey);
	}

	/**
	 * @return the description map. The original map is returned and any
	 *         modifications in it will change this entry
	 */
	public Description getDescription() {
		return description;
	}

}
