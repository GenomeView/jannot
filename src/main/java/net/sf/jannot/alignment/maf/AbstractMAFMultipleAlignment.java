/**
 * %HEADER%
 */
package net.sf.jannot.alignment.maf;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sf.jannot.Data;
import tudelft.utilities.logging.Reporter;

/**
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractMAFMultipleAlignment
		implements Data<AbstractAlignmentBlock> {

	private final Reporter log;

	public AbstractMAFMultipleAlignment(Reporter log) {
		this.log = log;
	}

	public Reporter getLog() {
		return log;
	}

	public String label() {
		return "Multiple alignment";
	}

	private Set<String> species = new HashSet<String>();

	/**
	 * @param string
	 */
	public void addSpecies(String string) {
		species.add(string);

	}

	public Collection<String> species() {
		return Collections.unmodifiableCollection(species);
	}

	@Override
	public abstract Iterable<AbstractAlignmentBlock> get(int start, int end);

	@Override
	public abstract Iterable<AbstractAlignmentBlock> get();

	@Override
	public boolean canSave() {
		return false;
	}

	/**
	 * 
	 * @return number of alidnment blocks in the iteratable {@link #get()}
	 */
	public abstract int noAlignmentBlocks();

}
