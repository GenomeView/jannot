/**
 * %HEADER%
 */
package net.sf.jannot.alignment.maf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import net.sf.jannot.Strand;
import net.sf.jannot.picard.LineBlockCompressedInputStream;
import net.sf.jannot.refseq.MemorySequence;
import net.sf.jannot.utils.SequenceTools;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * @author thpar
 * 
 */
public class LazyAlignmentBlock extends AbstractAlignmentBlock {

	private long offsetStart;

	private LineBlockCompressedInputStream zr;

	private ArrayList<AbstractAlignmentSequence> list = new ArrayList<AbstractAlignmentSequence>();

	private final Reporter log;

	public LazyAlignmentBlock(long offsetStart,
			LineBlockCompressedInputStream zr, int start, int end,
			Reporter log) {
		super(start, end);
		this.offsetStart = offsetStart;
		this.zr = zr;
		this.log = log;
	}

	public Reporter getLog() {
		return log;
	}

	public void add(AbstractAlignmentSequence as) {
		list.add(as);
	}

	private boolean lazyLoading = false;

	/**
	 * Load the actual alignment block from the zipped maf file and fill in the
	 * gaps in the alignment sequences
	 */
	public synchronized void lazyLoad() {
		if (list.size() == 0 || lazyLoading)
			return;
		lazyLoading = true;

		// make a mapping id -> alignment sequence
		Map<String, AbstractAlignmentSequence> idMap = new HashMap<String, AbstractAlignmentSequence>();
		for (AbstractAlignmentSequence s : list) {
			idMap.put(s.id, s);
		}

		try {
			zr.seek(offsetStart);
			String line = zr.readLine();
			// System.err.println("Processing block");
			while (!line.startsWith("#") && !line.isEmpty()) {
				// System.err.println("\t" + line);
				String[] cols = line.split("\\s+");
				if (cols.length == 7) {
					String type = cols[0];
					if (type.equals("s")) {
						String id = cols[1];
						LazyAlignmentSequence alSeq = (LazyAlignmentSequence) idMap
								.get(id);

						if (alSeq != null) {
//							System.err.println("Loading sequence for: " + id);
							MemorySequence seq = new MemorySequence(cols[6],
									getLog());
							alSeq.noNucleotides = Integer.parseInt(cols[3]);
							int startNuc = Integer.parseInt(cols[2]);
							int totalLength = Integer.parseInt(cols[5]);
							if (alSeq.strand == Strand.FORWARD) {
								alSeq.start = startNuc;
								alSeq.setSeq(seq);// = seq;
							} else {
								alSeq.start = totalLength - startNuc
										- alSeq.noNucleotides;
								alSeq.setSeq(
										SequenceTools.reverseComplement(seq));//

								// ;
							}

//							list.add(alSeq);
						} else {
							log.log(Level.WARNING, "LAS is not in map: " + id);
						}
					}
				}
				line = zr.readLine();
			}

		} catch (NumberFormatException | IOException e) {
			log.log(Level.WARNING, "lazy load failed", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<AbstractAlignmentSequence> iterator() {
		return list.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.jannot.alignment.maf.AbstractAlignmentBlock#getAlignmentSequence
	 * (int)
	 */
	@Override
	public AbstractAlignmentSequence getAlignmentSequence(int i) {
		return list.get(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jannot.alignment.maf.AbstractAlignmentBlock#size()
	 */
	@Override
	public int size() {
		return list.size();
	}

}
