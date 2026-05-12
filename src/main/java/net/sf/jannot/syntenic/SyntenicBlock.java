/**
 * %HEADER%
 */
package net.sf.jannot.syntenic;

import net.sf.jannot.Entry;
import net.sf.jannot.Location;
import net.sf.jannot.Strand;

/**
 * Synthenic block provides a syntenic mapping between two locations in 2
 * chromosomes ( {@link Entry}s). It maps one location in one entry to another
 * location in the other Entry.
 * 
 * @author Thomas Abeel
 * 
 */
public class SyntenicBlock {

	private Location refLoc;
	private Location targetLoc;
	/* ID of reference */
	private String refEntry;
	/* ID of target */
	private String targetEntry;
	private Strand refStrand;
	private Strand targetStrand;

	/**
	 * 
	 * @param refEntry     ID of reference
	 * @param targetEntry  ID of target
	 * @param refLoc
	 * @param targetLoc
	 * @param refStrand
	 * @param targetStrand
	 */
	public SyntenicBlock(String refEntry, String targetEntry, Location refLoc,
			Location targetLoc, Strand refStrand, Strand targetStrand) {
		super();
		this.refLoc = refLoc;
		this.targetLoc = targetLoc;
		this.refEntry = refEntry;
		this.targetEntry = targetEntry;
		this.refStrand = refStrand;
		this.targetStrand = targetStrand;

	}

	/**
	 * Flip reference and target
	 * 
	 * @return
	 */
	public SyntenicBlock flip() {
		return new SyntenicBlock(targetEntry, refEntry, targetLoc, refLoc,
				targetStrand, refStrand);
	}

	/**
	 * 
	 * @return ID of reference
	 */
	public String reference() {
		return refEntry;
	}

	/**
	 * 
	 * @return ID of target
	 */
	public String target() {
		return targetEntry;
	}

	public Location refLocation() {
		return refLoc;
	}

	public Location targetLocation() {
		return targetLoc;
	}

	public Strand getRefStrand() {
		return refStrand;
	}

	public Strand getTargetStrand() {
		return targetStrand;
	}

	/**
	 * @param ref    the ref ID
	 * @param target the target ID
	 * @return this , possibly {@link #flip()}ped, or null if this does not
	 *         match ref+target
	 */
	public SyntenicBlock match(String ref, String target) {
		if (refEntry.equals(ref) && targetEntry.equals(target)) {
			return this;
		}
		if (targetEntry.equals(ref) && refEntry.equals(target)) {
			return flip();
		}
		return null;
	}

	@Override
	public String toString() {
		return "Syntenic[" + refLoc + "," + refEntry + "," + refStrand + ","
				+ targetLoc + "," + targetEntry + "," + targetStrand + "]";

	}

}
